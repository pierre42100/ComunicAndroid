package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.enums.VirtualDirectoryType;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.VirtualDirectory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Virtual directory helper
 *
 * @author Pierre HUBERT
 */
public class VirtualDirectoryHelper extends BaseHelper {

    public VirtualDirectoryHelper(Context context) {
        super(context);
    }

    /**
     * Find the information associated with a directory
     *
     * @param directory The directory to find
     * @return Information about the directory / null in case of failure
     */
    @Nullable
    public VirtualDirectory find(String directory){

        APIRequest request = new APIRequest(getContext(), "virtualDirectory/find");
        request.addString("directory", directory);

        try {
            APIResponse response = new APIRequestHelper().exec(request);
            if(response.getResponse_code() != 200) return null;
            return APIToVirtualDirectory(response.getJSONObject());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Turn an API entry into a VirtualDirectory object
     *
     * @param obj The object to parse
     * @return Generated VirtualDirectory object
     * @throws JSONException in case of failure
     */
    private static VirtualDirectory APIToVirtualDirectory(JSONObject obj) throws JSONException {
        VirtualDirectory virtualDirectory = new VirtualDirectory();
        virtualDirectory.setKind(StringToVirtualDirectoryType(obj.getString("kind")));
        virtualDirectory.setId(obj.getInt("id"));
        return virtualDirectory;
    }

    /**
     * Turn a string into a virtual directory type
     *
     * @param string The string to convert
     * @return Matching value in enum
     */
    private static VirtualDirectoryType StringToVirtualDirectoryType(String string) {
        switch (string){
            case "user":
                return VirtualDirectoryType.USER;

            case "group":
                return VirtualDirectoryType.GROUP;
        }

        throw new AssertionError();
    }
}
