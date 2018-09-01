package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.AccountSettingsHelper;
import org.communiquons.android.comunic.client.data.models.AccountImageSettings;

/**
 * Get account image settings task
 *
 * @author Pierre HUBERT
 */
public class GetAccountImageSettingsTask extends SafeAsyncTask<Void, Void, AccountImageSettings> {

    public GetAccountImageSettingsTask(Context context) {
        super(context);
    }

    @Override
    protected AccountImageSettings doInBackground(Void... voids) {
        return new AccountSettingsHelper(getContext()).getAccountImageSettings();
    }
}
