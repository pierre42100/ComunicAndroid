package org.communiquons.android.comunic.client.data.runnables;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.FriendsListDbHelper;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.models.Friend;

import java.util.ArrayList;

/**
 * Auto friends list refresh runnable
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/19/17.
 */

class FriendsListRefreshRunnable implements Runnable {

    /**
     * This runnable requires a context object in order to connect to the API
     *
     * The context of the application (or of the activity)
     */
    private Context mContext;

    /**
     * Database object
     */
    private DatabaseHelper dbHelper;

    /**
     * Runnable constructor
     *
     * @param context The context of the application
     * @param dbHelper Database helper
     */
    FriendsListRefreshRunnable(Context context, DatabaseHelper dbHelper){
        this.mContext = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public void run() {

        FriendsListHelper friendsListHelper = new FriendsListHelper(mContext);
        FriendsListDbHelper friendsDBHelper = new FriendsListDbHelper(dbHelper);

        //Get the latest version of the list
        ArrayList<Friend> friendsList = friendsListHelper.download();

        //Save it (only in case of success)
        if(friendsList != null)
            friendsDBHelper.update_list(friendsList);
    }
}
