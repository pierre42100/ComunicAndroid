package org.communiquons.android.comunic.client.data.friendsList;

import android.content.Context;

import org.communiquons.android.comunic.client.data.DatabaseHelper;

/**
 * Friends list functions
 *
 * Some of the functions specified here are utilities (such as delete a friend)
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/19/17.
 */

public class FriendsList {

    private FriendsListDbHelper fdbHelper;
    private Context mContext;

    /**
     * Public application constructor
     *
     * @param dbHelper Database helper
     * @param mContext the context of the application
     */
    public FriendsList(DatabaseHelper dbHelper, Context mContext){
        this.fdbHelper = new FriendsListDbHelper(dbHelper);
        this.mContext = mContext;
    }

    /**
     * Remove a friend from the list
     *
     * @param friend The friend to delete
     */
    public void remove(Friend friend){

        //Remove the friend online
        //TODO : Remove the friend form online

        //Remove the friend from the local database
        //TODO : Remove the friend from the local database
    }
}
