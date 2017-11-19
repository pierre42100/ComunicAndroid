package org.communiquons.android.comunic.client.data.friendsList;


import android.content.Context;

import org.communiquons.android.comunic.client.data.DatabaseHelper;

/**
 * Automatically refresh friends list thread
 *
 * Goal this thread : this thread is killed when it is not required anymore to refresh the friends
 * list.
 *
 * However, it launches periodically another thread, FriendsListRefreshRunnable, which is never
 * killed, in order not to get errors.
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/19/17.
 */

public class FriendRefreshLoopRunnable implements Runnable {

    private final Object object = new Object();

    private Context context;
    private DatabaseHelper dbHelper;

    public FriendRefreshLoopRunnable(Context context, DatabaseHelper dbHelper){
        this.context = context;
        this.dbHelper = dbHelper;
    }

    /**
     * Perpetual loop that only stops when it get killed
     */
    @Override
    public void run() {

        synchronized (object) {

            Thread thread;

            while (true) {

                //Create a refresh thread
                thread = new Thread(new FriendsListRefreshRunnable(context, dbHelper));
                thread.start();

                try {
                    thread.join();
                    object.wait(15000);
                } catch (Exception e) {
                    e.printStackTrace();
                    return; //Stop the refresh loop
                }
            }
        }

    }

}
