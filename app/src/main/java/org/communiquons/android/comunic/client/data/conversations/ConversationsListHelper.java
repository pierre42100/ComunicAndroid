package org.communiquons.android.comunic.client.data.conversations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Operations on the conversation list (helper)
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/9/17.
 */

public class ConversationsListHelper {

    private String TAG = "ConversationsList";

    /**
     * The context of the application
     */
    private Context mContext;

    /**
     * Conversations list database helper
     */
    private ConversationsListDbHelper convDBHelper;

    /**
     * The constructor of the class
     *
     * @param context The context of execution of the application
     * @param dbHelper Database helper
     */
    public ConversationsListHelper(Context context, DatabaseHelper dbHelper){
        mContext = context;
        convDBHelper = new ConversationsListDbHelper(dbHelper);
    }

    /**
     * Get the list of conversation or null in case of failure
     *
     * @return The list of conversations
     */
    @Nullable
    public ArrayList<ConversationsInfo> get(){

        //Download a new list of conversations
        ArrayList<ConversationsInfo> list = download();

        if(list != null){
            //Save the list
            convDBHelper.update_list(list);
        }

        //Return the list
        return list;
    }

    /**
     * Get online (download) the list of all the conversations
     *
     * @return The list of conversations
     */
    @Nullable
    private ArrayList<ConversationsInfo> download(){

        ArrayList<ConversationsInfo> list = new ArrayList<>();

        try {

            //Prepare the request on the server
            APIRequestParameters params = new APIRequestParameters(mContext, "conversations/getList");
            APIResponse response = new APIRequest().exec(params);

            //Check if an error occurred
            JSONArray friends = response.getJSONArray();

            if(friends == null){
                Log.e(TAG, "Couldn't retrieve friends list !");
                return null;
            }

            for(int i = 0; i < friends.length(); i++){
                //Add the conversation to the list
                list.add(parseConversationJSON(friends.getJSONObject(i)));
            }

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return list;
    }

    /**
     * Parse a JSONObject into a conversation info element
     *
     * @param obj Information about the conversation
     * @return Conversation object or null in case of failure
     */
    @Nullable
    private ConversationsInfo parseConversationJSON(@NonNull JSONObject obj){

        ConversationsInfo info = new ConversationsInfo();

        try {
            //Get information about the conversation
            info.setID(obj.getInt("ID"));
            info.setID_owner(obj.getInt("ID_owner"));
            info.setLast_active(obj.getInt("last_active"));
            info.setName(obj.getString("name"));
            info.setFollowing(obj.getInt("following") == 1);
            info.setSaw_last_message(obj.getInt("saw_last_message") == 1);

            //Get the list of the members
            JSONArray members = obj.getJSONArray("members");
            for(int i = 0; i < members.length(); i++){
                info.addMember(members.getInt(i));
            }

        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        return info;
    }

}
