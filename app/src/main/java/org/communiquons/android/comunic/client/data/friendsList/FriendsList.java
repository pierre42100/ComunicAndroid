package org.communiquons.android.comunic.client.data.friendsList;

import android.content.Context;
import android.util.Log;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
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

    //Debug tag
    private static final String TAG = "FriendsList";

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
        try {
            //Remove the friend online
            APIRequestParameters delparams = new APIRequestParameters(mContext, "friends/remove");
            delparams.addParameter("friendID", ""+friend.getId());
            new APIRequest().exec(delparams);

            //Remove the friend from the local database
            fdbHelper.delete_friend(friend);

        } catch (Exception e){
            Log.e(TAG, "Couldn't delete friend !");
            e.printStackTrace();
        }
    }

    /**
     * Respond to a friendship request
     *
     * @param friend The friend to update
     * @param accept The new status for the request
     */
    public void respondRequest(Friend friend, boolean accept){
        try {

            //Perform a request to update the satus online
            APIRequestParameters reqParams = new APIRequestParameters(mContext,
                    "friends/respondRequest");
            reqParams.addParameter("friendID", ""+friend.getId());
            reqParams.addParameter("accept", accept ? "true" : "false");
            new APIRequest().exec(reqParams);

            //Update the friend in the local database
            if(accept) {
                friend.setAccepted(accept);
                fdbHelper.update_friend(friend);
            }
            else {
                fdbHelper.delete_friend(friend);
            }

        } catch(Exception e){
            Log.e(TAG, "Couldn't respond to friendship request !");
            e.printStackTrace();
        }
    }
}
