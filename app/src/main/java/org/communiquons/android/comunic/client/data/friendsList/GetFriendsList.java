package org.communiquons.android.comunic.client.data.friendsList;

import android.content.Context;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Fetch and return friends list
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/12/17.
 */

class GetFriendsList {

    /**
     * The context of execution of the application
     */
    private Context mContext;

    /**
     * Public constructor of the class
     *
     * @param context the context of execution of the application
     */
    GetFriendsList(Context context){
        mContext = context;
    }

    /**
     * Get and return the friends list
     *
     * @return The list of friend of the user
     */
    ArrayList<Friend> get(){

        //Prepare the API request
        APIRequestParameters params = new APIRequestParameters(mContext, "friends/getList");

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

}
