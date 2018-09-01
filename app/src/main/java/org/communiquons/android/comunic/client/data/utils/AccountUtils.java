package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.AccountHelper;

/**
 * Account utilities functions
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/5/17.
 */

public class AccountUtils {

    /**
     * Get the current user ID quickly
     *
     * @param context The context of execution of the application
     * @return The ID of the current user or -1 in case of failure
     */
    public static int getID(Context context){
        return new AccountHelper(context).get_current_user_id();
    }

}
