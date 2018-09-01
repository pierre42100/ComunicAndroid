package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.enums.LoginResult;
import org.communiquons.android.comunic.client.data.helpers.AccountHelper;

/**
 * User login task
 *
 * @author Pierre HUBERT
 */
public class LoginTask extends SafeAsyncTask<String, Void, LoginResult> {

    public LoginTask(Context context) {
        super(context);
    }

    @Override
    protected LoginResult doInBackground(String... strings) {
        return new AccountHelper(getContext()).sign_in(strings[0], strings[1]);
    }

}
