package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.enums.AccountImageVisibility;
import org.communiquons.android.comunic.client.data.models.APIFileRequest;
import org.communiquons.android.comunic.client.data.models.APIPostFile;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.AccountImageSettings;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Account settings helper
 *
 * @author Pierre HUBERT
 */
public class AccountSettingsHelper extends BaseHelper {

    public AccountSettingsHelper(Context context) {
        super(context);
    }

    /**
     * Get account image settings
     *
     * @return The account image settings / null in case of failure
     */
    @Nullable
    public AccountImageSettings getAccountImageSettings(){

        APIRequest request = new APIRequest(getContext(), "settings/get_account_image");
        try {
            APIResponse response = new APIRequestHelper().exec(request);

            if(response.getResponse_code() != 200)
                return null;

            return APIToAccountImageSettings(response.getJSONObject());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Upload a new account image
     *
     * @param bitmap The image bitmap to upload
     * @return The result of the operation
     */
    public boolean uploadAccountImage(Bitmap bitmap){
        APIFileRequest request = new APIFileRequest(getContext(),
                "settings/upload_account_image");

        APIPostFile file = new APIPostFile();
        file.setFileName("image.png");
        file.setBitmap(bitmap);
        file.setFieldName("picture");
        request.addFile(file);

        try {
            return new APIRequestHelper().execPostFile(request).getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete user account image
     *
     * @return TRUE for a success / FALSE else
     */
    public boolean deleteAccountImage(){
        APIRequest request = new APIRequest(getContext(), "settings/delete_account_image");
        request.setTryContinueOnError(true);

        try {
            APIResponse response = new APIRequestHelper().exec(request);
            return response.getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update account image visibility
     *
     * @param visibility New account image visibility
     * @return The result of the operation
     */
    public boolean setAccountImageVisibility(AccountImageVisibility visibility){
        APIRequest request = new APIRequest(getContext(), "settings/set_account_image_visibility");
        request.addString("visibility", AccountImageVisibilityToString(visibility));

        try {
            return new APIRequestHelper().exec(request).getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse an API entry into an AccountImageSettings entry
     *
     * @param object JSON object to parse
     * @return Generated AccountImageSettings object
     * @throws JSONException In case of failure
     */
    private static AccountImageSettings APIToAccountImageSettings(JSONObject object)
            throws JSONException {
        AccountImageSettings accountImageSettings = new AccountImageSettings();
        accountImageSettings.setHas_image(object.getBoolean("has_image"));
        accountImageSettings.setImageURL(object.getString("image_url"));
        accountImageSettings.setVisibility(StringToAccountImageVisibility(
                object.getString("visibility")));
        return accountImageSettings;
    }

    /**
     * Turn a string into an AccountImageVisibility level
     *
     * @param string The string to parse
     * @return Matching enum entry
     */
    private static AccountImageVisibility StringToAccountImageVisibility(String string){
        switch (string){
            case "open":
                return AccountImageVisibility.OPEN;

            case "public":
                return AccountImageVisibility.PUBLIC;

            case "friends":
                return AccountImageVisibility.FRIENDS;

                default:
                    throw new AssertionError();
        }
    }

    /**
     * Turn an account image visibility level into a string ready for the API
     *
     * @param visibility Visibility level to convert
     * @return Matching string
     */
    private static String AccountImageVisibilityToString(AccountImageVisibility visibility){
        switch (visibility){
            case OPEN:
                return "open";

            case PUBLIC:
                return "public";

            case FRIENDS:
                return "friends";

            default:
                throw new AssertionError();
        }
    }
}
