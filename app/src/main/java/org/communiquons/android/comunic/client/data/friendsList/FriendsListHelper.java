package org.communiquons.android.comunic.client.data.friendsList;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Friends list functions
 *
 * Some of the functions specified here are utilities (such as delete a friend)
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/19/17.
 */

public class FriendsListHelper {

    //Debug tag
    private static final String TAG = "FriendsList";

    private FriendsListDbHelper fdbHelper;
    private Context mContext;

    /**
     * Public constructor
     *
     * @param context The context of the application
     */
    public FriendsListHelper(Context context){
        this.fdbHelper = new FriendsListDbHelper(DatabaseHelper.getInstance(context));
        this.mContext = context;
    }

    /**
     * Public application constructor
     *
     * @param dbHelper Database helper
     * @param context the context of the application
     */
    public FriendsListHelper(DatabaseHelper dbHelper, Context context){
        this.fdbHelper = new FriendsListDbHelper(dbHelper);
        this.mContext = context;
    }

    /**
     * Get and return the friends list
     *
     * @return The list of firned
     */
    public ArrayList<Friend> get(){
        return fdbHelper.get_list();
    }

    /**
     * Download a new version of the friends list
     *
     * @return The list of friend of the user
     */
    @Nullable
    ArrayList<Friend> download(){

        //Prepare the API request
        APIRequestParameters params = new APIRequestParameters(mContext, "friends/getList");
        params.addParameter("complete", true);

        //Prepare the result
        ArrayList<Friend> friends = new ArrayList<>();

        try {

            //Perform the request and retrieve the response
            APIResponse response = new APIRequest().exec(params);
            JSONArray friendsList = response.getJSONArray();

            if(friendsList == null)
                return null;

            //Process JSON array
            for(int i = 0; i < friendsList.length(); i++){

                //Try to extract JSON object containing informations
                JSONObject friendship_infos = friendsList.getJSONObject(i);

                //Save informations about the friend in the friend object
                Friend friend = new Friend();

                //Set friend informations
                friend.setId(friendship_infos.getInt("ID_friend"));
                friend.setAccepted(friendship_infos.getInt("accepted") == 1);
                friend.setFollowing(friendship_infos.getInt("ID_friend") == 1);
                friend.setLast_activity(friendship_infos.getInt("time_last_activity"));

                //Add the friend to the list
                friends.add(friend);

            }


        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return friends;
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
