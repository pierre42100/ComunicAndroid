package org.communiquons.android.comunic.client.ui.fragments.accountsettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.AccountImageSettings;
import org.communiquons.android.comunic.client.ui.asynctasks.DeleteUserAccountImageTask;
import org.communiquons.android.comunic.client.ui.asynctasks.GetAccountImageSettingsTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.asynctasks.UploadNewAccountImageTask;
import org.communiquons.android.comunic.client.ui.preference.AccountImagePreference;
import org.communiquons.android.comunic.client.ui.utils.BitmapUtils;

import java.io.FileNotFoundException;

import static org.communiquons.android.comunic.client.ui.Constants.IntentRequestCode.ACCOUNT_IMAGE_SETTINGS_PICK_NEW_INTENT;

/**
 * Account image settings fragment
 */
public class AccountImageSettingsFragment extends BaseAccountSettingsFragment implements Preference.OnPreferenceClickListener {

    private static final String TAG = AccountImageSettingsFragment.class.getSimpleName();

    //Preferences
    private static final String PREFERENCE_UPDATE_ACCOUNT_IMAGE = "update_account_image";
    private static final String PREFERENCE_DELETE_ACCOUNT_IMAGE = "delete_account_image";

    private AccountImageSettings mAccountImageSettings;

    private GetAccountImageSettingsTask mGetAccountImageSettingsTask;
    private UploadNewAccountImageTask mUploadNewAccountImageTask;
    private DeleteUserAccountImageTask mDeleteUserAccountImageTask;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.account_preference_image);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findPreference(PREFERENCE_UPDATE_ACCOUNT_IMAGE).setOnPreferenceClickListener(this);
        findPreference(PREFERENCE_DELETE_ACCOUNT_IMAGE).setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.preferences_account_image_title);

        if(mAccountImageSettings == null)
            load_settings();
        else
            onGotAccountImageSettings(mAccountImageSettings);
    }

    @Override
    public void onPause() {
        super.onPause();
        unset_loading_tasks();
        removeLoadingDialog();
    }

    private void unset_loading_tasks(){
        if(mGetAccountImageSettingsTask != null)
            mGetAccountImageSettingsTask.setOnPostExecuteListener(null);

        if(mUploadNewAccountImageTask != null)
            mUploadNewAccountImageTask.setOnPostExecuteListener(null);

        if(mDeleteUserAccountImageTask != null)
            mDeleteUserAccountImageTask.setOnPostExecuteListener(null);
    }



    /**
     * Load account image settings. As soon as this method is called, cached information about
     * account image are cleared, so on pause and resume of the fragment it can be loaded again
     */
    private void load_settings(){
        unset_loading_tasks();

        showLoadingDialog();

        mAccountImageSettings = null;
        mGetAccountImageSettingsTask = new GetAccountImageSettingsTask(getActivity());
        mGetAccountImageSettingsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<AccountImageSettings>() {
            @Override
            public void OnPostExecute(AccountImageSettings accountImageSettings) {
                onGotAccountImageSettings(accountImageSettings);
            }
        });
        mGetAccountImageSettingsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void onGotAccountImageSettings(@Nullable AccountImageSettings accountImageSettings){

        removeLoadingDialog();

        //Check for errors
        if(accountImageSettings == null){
            Toast.makeText(getActivity(), R.string.err_get_account_image_settings,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAccountImageSettings = accountImageSettings;

        //Apply settings
        ((AccountImagePreference)findPreference(PREFERENCE_UPDATE_ACCOUNT_IMAGE))
                .setImage_url(mAccountImageSettings.getImageURL());
        findPreference(PREFERENCE_DELETE_ACCOUNT_IMAGE).setEnabled(
                accountImageSettings.isHas_image());

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        switch (preference.getKey()){

            //Upload new account image
            case PREFERENCE_UPDATE_ACCOUNT_IMAGE:
                pickNewAccountImage();
                break;

            //Delete account image
            case PREFERENCE_DELETE_ACCOUNT_IMAGE:
              confirmDeleteAccountImage();
              break;

        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check if the request was to choose a new account image
        if(requestCode == ACCOUNT_IMAGE_SETTINGS_PICK_NEW_INTENT)
            pickNewAccountImageCallback(resultCode, data);
    }

    /**
     * Prompt the user to choose a new account image
     */
    private void pickNewAccountImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ACCOUNT_IMAGE_SETTINGS_PICK_NEW_INTENT);
    }

    /**
     * Pick new account image callback
     *
     * @param resultCode Result code of the operation
     * @param data Associated data
     */
    private void pickNewAccountImageCallback(int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK)
            return;

        try {
            uploadNewAccountImage(BitmapUtils.IntentResultToBitmap(getActivity(), data));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform the upload of new account image
     *
     * @param bitmap The new account image, as bitmap
     */
    private void uploadNewAccountImage(Bitmap bitmap){
        showLoadingDialog();
        unset_loading_tasks();

        mUploadNewAccountImageTask = new UploadNewAccountImageTask(getContext());
        mUploadNewAccountImageTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<Boolean>() {
            @Override
            public void OnPostExecute(Boolean result) {
                if(!result)
                    Toast.makeText(getActivity(), R.string.err_upload_account_image,
                            Toast.LENGTH_SHORT).show();

                load_settings();
            }
        });
        mUploadNewAccountImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bitmap);
    }

    /**
     * Prompt user confirmation to delete account image
     */
    private void confirmDeleteAccountImage(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_delete_accountimage_title)
                .setMessage(R.string.dialog_delete_accountimage_message)
                .setNegativeButton(R.string.dialog_delete_accountimage_cancel, null)

                .setPositiveButton(R.string.dialog_delete_accountimage_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccountImage();
                    }
                })

                .show();
    }

    /**
     * Perform account image deletion
     */
    private void deleteAccountImage(){
        showLoadingDialog();
        unset_loading_tasks();

        mDeleteUserAccountImageTask = new DeleteUserAccountImageTask(getActivity());
        mDeleteUserAccountImageTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<Boolean>() {
            @Override
            public void OnPostExecute(Boolean result) {

                if(!result)
                    Toast.makeText(getActivity(), R.string.err_delete_account_image,
                            Toast.LENGTH_SHORT).show();

                load_settings();
            }
        });
        mDeleteUserAccountImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
