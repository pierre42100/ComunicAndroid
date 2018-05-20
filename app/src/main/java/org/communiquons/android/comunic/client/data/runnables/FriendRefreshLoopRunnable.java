package org.communiquons.android.comunic.client.data.runnables;


import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.FriendsListDbHelper;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.models.Friend;

import java.util.ArrayList;

/**
 * Automatically refresh friends list thread
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/19/17.
 */

public class FriendRefreshLoopRunnable implements Runnable {

    private final Object object = new Object();

    private Context mContext;
    private DatabaseHelper dbHelper;

    private boolean mStop = false;

    public FriendRefreshLoopRunnable(Context context, DatabaseHelper dbHelper){
        this.mContext = context.getApplicationContext();
        this.dbHelper = dbHelper;
    }

    /**
     * Perpetual loop
     */
    @Override
    public void run() {

        synchronized (object) {

            while (true) {

                FriendsListHelper friendsListHelper = new FriendsListHelper(mContext);
                FriendsListDbHelper friendsDBHelper = new FriendsListDbHelper(dbHelper);

                //Get the latest version of the list
                ArrayList<Friend> friendsList = friendsListHelper.download();

                //Acquire a lock over the friend list
                FriendsListHelper.ListAccessLock.lock();

                try {
                    //Save it (only in case of success)
                    if (friendsList != null)
                        friendsDBHelper.update_list(friendsList);

                } finally {
                    //Release the lock
                    FriendsListHelper.ListAccessLock.unlock();
                }

                try {
                    object.wait(15000);
                } catch (Exception e) {
                    e.printStackTrace();
                    return; //Stop the refresh loop
                }

                //Check if this thread has to be interrupted
                if(mStop)
                    return;
            }
        }

    }

    /**
     * Query this thread to stop
     */
    public void interrupt(){
        mStop = true;
    }

}
