package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.enums.AccountImageVisibility;
import org.communiquons.android.comunic.client.data.helpers.AccountSettingsHelper;

/**
 * Update account image visibility task
 *
 * @author Pierre HUBERT
 */
public class UpdateAccountImageVisibilityTask extends SafeAsyncTask<AccountImageVisibility, Void, Boolean> {
    public UpdateAccountImageVisibilityTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(AccountImageVisibility... accountImageVisibilities) {
        return new AccountSettingsHelper(getContext()).setAccountImageVisibility(accountImageVisibilities[0]);
    }
}
