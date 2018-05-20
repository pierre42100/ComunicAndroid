package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.Friend;
import org.communiquons.android.comunic.client.data.models.FriendshipStatus;
import org.communiquons.android.comunic.client.ui.activities.LoginActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

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

    //Friends list access lock
    public static ReentrantLock ListAccessLock = new ReentrantLock();

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
        this.mContext = context.getApplicationContext();
    }

    /**
     * Get and return the friends list
     *
     * @return The list of friends
     */
    public ArrayList<Friend> get() {

        //Acquire friends list lock
        FriendsListHelper.ListAccessLock.lock();

        //Fetch the list
        ArrayList<Friend> list;
        try {
            list = fdbHelper.get_list();
        } finally {
            FriendsListHelper.ListAccessLock.unlock();
        }

        return list;
    }

    /**
     * Download a new version of the friends list
     *
     * @return The list of friend of the user
     */
    @Nullable
    public ArrayList<Friend> download(){

        //Prepare the API request
        APIRequest params = new APIRequest(mContext, "friends/getList");
        params.addBoolean("complete", true);

        //Make this request continue in case of errors
        params.setTryContinueOnError(true);

        //Prepare the result
        ArrayList<Friend> friends = new ArrayList<>();

        try {

            //Perform the request and retrieve the response
            APIResponse response = new APIRequestHelper().exec(params);

            //Check if the credentials have been rejected by the server (error 412 = invalid tokens)
            if(response.getResponse_code() == 412){

                //Sign out user
                new AccountHelper(mContext).sign_out();

                //Redirect user to login activity
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                return null;
            }

            //Check if an error occurred
            if(response.getResponse_code() != 200)
                return null;

            //Turn friends list into JSONArray
            JSONArray friendsList = response.getJSONArray();

            if(friendsList == null)
                return null;

            //Process JSON array
            for(int i = 0; i < friendsList.length(); i++){

                //Try to extract JSON object containing information
                JSONObject friendship_infos = friendsList.getJSONObject(i);

                //Save information about the friend in the friend object
                Friend friend = new Friend();

                //Set friend information
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
            APIRequest delparams = new APIRequest(mContext, "friends/remove");
            delparams.addString("friendID", ""+friend.getId());
            new APIRequestHelper().exec(delparams);

            //Remove the friend from the local database
            fdbHelper.delete_friend(friend);

        } catch (Exception e){
            Log.e(TAG, "Couldn't delete friend !");
            e.printStackTrace();
        }
    }

    /**
     * Send a friendship request to a user
     *
     * @param friendID The ID of the target friend
     * @return TRUE in case of success / FALSE else
     */
    public boolean sendRequest(int friendID){

        //Prepare the request
        APIRequest params = new APIRequest(mContext, "friends/sendRequest");
        params.addInt("friendID", friendID);

        //Try to perform the request
        try {
            APIResponse response = new APIRequestHelper().exec(params);
            return response.getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cancel a friendship request
     *
     * @param friendID The ID of the target friend
     * @return TRUE in case of success / FALSE else
     */
    public boolean cancelRequest(int friendID){

        //Prepare the request
        APIRequest params = new APIRequest(mContext, "friends/removeRequest");
        params.addInt("friendID", friendID);

        //Try to perform the request
        try {
            APIResponse response = new APIRequestHelper().exec(params);
            return response.getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

            //Respond to the request online
            respondRequest(friend.getId(), accept);

            //Update the friend in the local database
            if(accept) {
                friend.setAccepted(true);
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

    /**
     * Respond to a friendship request
     *
     * @param friendID The ID of the target friend
     * @param accept TRUE to accept request / FALSE else
     * @return TRUE in case of success / FALSE else
     */
    public boolean respondRequest(int friendID, boolean accept){

        //Perform a request to update the status online
        APIRequest reqParams = new APIRequest(mContext,
                "friends/respondRequest");
        reqParams.addInt("friendID", friendID);
        reqParams.addString("accept", accept ? "true" : "false");

        //Execute the request
        try {
            //Perform the request
            APIResponse response = new APIRequestHelper().exec(reqParams);
            return response.getResponse_code() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Get a friendship status
     *
     * @param friendID The ID of the target friend
     * @return Information about the friendship / null in case of failure
     */
    @Nullable
    public FriendshipStatus getFrienshipStatus(int friendID) {

        //Perform a request on the API
        APIRequest params = new APIRequest(mContext, "friends/getStatus");
        params.addInt("friendID", friendID);

        try {

            //Get the response
            APIResponse response = new APIRequestHelper().exec(params);

            //Check for errors
            if(response.getResponse_code() != 200)
                return null;

            //Parse the response
            JSONObject object = response.getJSONObject();
            FriendshipStatus status = new FriendshipStatus();
            status.setAreFriend(object.getBoolean("are_friend"));
            status.setSentRequest(object.getBoolean("sent_request"));
            status.setReceivedRequest(object.getBoolean("received_request"));
            status.setFollowing(object.getBoolean("following"));

            return status;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
