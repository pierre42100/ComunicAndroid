package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.AccountSettingsHelper;

/**
 * Delete user account image AsyncTask
 *
 * @author Pierre HUBERT
 */
public class DeleteUserAccountImageTask extends SafeAsyncTask<Void, Void, Boolean> {

    public DeleteUserAccountImageTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return new AccountSettingsHelper(getContext()).deleteAccountImage();
    }
}
