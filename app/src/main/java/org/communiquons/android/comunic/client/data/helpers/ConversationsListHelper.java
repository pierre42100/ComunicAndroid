package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;

import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.ConversationsInfo;
import org.communiquons.android.comunic.client.data.utils.ArrayUtils;
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
     * Database helper
     */
    private DatabaseHelper dbHelper;

    /**
     * The constructor of the class
     *
     * @param context The context of execution of the application
     * @param dbHelper Database helper
     */
    public ConversationsListHelper(Context context, DatabaseHelper dbHelper){
        mContext = context;
        convDBHelper = new ConversationsListDbHelper(dbHelper);
        this.dbHelper = dbHelper;
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
     * Get information about a conversation
     *
     * @param convID The conversation ID
     * @param allowDownload In case the conversation was not found locally, allow informations about
     *                      the conversation to be fetched online
     * @return Information about the conversation, or false in case of failure
     */
    @Nullable
    public ConversationsInfo getInfosSingle(int convID, boolean allowDownload){

        ConversationsInfo infos;

        //Try to fetch information from the local database
        if((infos = convDBHelper.getInfos(convID)) != null)
            return infos;

        //Check if we are not allowed to fetch informations online
        if(!allowDownload)
            return null;

        //Get informations about the conversation online
        return downloadSingle(convID);
    }

    /**
     * Search and return the ID of a private conversation with a specified user
     *
     * @param userID The ID of the target user
     * @param allowCreate If set to true, then if the server does not find any matching conversation,
     *                    it will automatically create a new one
     * @return The ID of the conversation or null in case of failure
     */
    @Nullable
    public Integer getPrivate(int userID, boolean allowCreate){

        //Prepare an API request
        APIRequest params = new APIRequest(mContext,
                "conversations/getPrivate");
        params.addInt("otherUser", userID);
        params.addBoolean("allowCreate", allowCreate);

        //Try to perform request
        try {
            APIResponse response = new APIRequestHelper().exec(params);
            JSONObject object = response.getJSONObject();

            //Check for conversations ID
            if(!object.has("conversationsID"))
                return null;

            //Get conversations ID
            JSONArray conversations = object.getJSONArray("conversationsID");

            //Check if the array is empty
            if(conversations.length() == 0)
                return null;
            else
                return conversations.getInt(0);
        }

        //Catch errors
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the display name of a conversation
     *
     * @param infos Informations about a conversation
     * @return The name of the conversation
     */
    public String getDisplayName(ConversationsInfo infos){

        //Check if a specific name has been specified
        if(infos.hasName())
            return infos.getName();

        //Get the list of members of the conversation
        ArrayList<Integer> members = infos.getMembers();

        //Get the ID of the three first members
        ArrayList<Integer> membersToGet = new ArrayList<>();
        int num = 0;
        for(int ID : members){
            membersToGet.add(ID);

            num++;

            if(num > 3)
                break;
        }

        //Get information about the users
        ArrayMap<Integer, UserInfo> users =
                new GetUsersHelper(mContext, dbHelper).getMultiple(membersToGet);

        if(users == null)
            return ""; //No name by default

        String name = "";

        int count = 0;
        for(Integer id : users.keySet()){

            //Do not display current user name
            if(id == new AccountUtils(mContext).get_current_user_id())
                continue;

            if(users.get(id) != null){

                if(count > 0)
                    name += ", ";

                name += users.get(id).getFullName();
                count++;

                if(count > 3)
                    break;
            }
        }

        return name;
    }

    /**
     * Delete a conversation specified by its ID
     *
     * @param convID The ID of the conversation to delete
     * @return True in case of success / false else
     */
    public boolean delete(int convID){

        //Delete the conversation on the API
        APIRequest params = new APIRequest(mContext, "conversations/delete");
        params.addString("conversationID", ""+convID);

        try {
            new APIRequestHelper().exec(params);
        } catch (Exception e){
            return false;
        }

        //Success
        return true;
    }

    /**
     * Create a new conversation
     *
     * @param name The name of the conversation
     * @param follow True to make the user follow the conversation
     * @param members The members of the conversation
     * @return The ID of the created conversation / null in case of failure
     */
    @Nullable
    public Integer create(String name, boolean follow, ArrayList<Integer> members){

        //Turn the list of members into a string
        String members_str = "";
        for(int id : members){
            members_str += id + ",";
        }

        //Make an API request
        APIRequest params = new APIRequest(mContext, "conversations/create");
        params.addString("name", name.equals("") ? "false" : name);
        params.addString("follow", follow ? "true" : "false");
        params.addString("users", members_str);

        //Perform the request
        try {
            APIResponse response = new APIRequestHelper().exec(params);

            //Get conversation ID
            JSONObject obj = response.getJSONObject();
            return obj.getInt("conversationID");

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update a conversation
     *
     * @param convID The ID of the conversation to update
     * @param following Specify wether the user would like to follow or not the conversation
     * @return True for a success / False else
     */
    public boolean update(int convID, boolean following){
        return update(convID, null, null, following);
    }

    /**
     * Update a conversation
     *
     * @param convID The ID of the conversation
     * @param name The name of the conversation
     * @param members The list of members of the conversation
     * @param following True to follow the conversation / false else
     * @return True for a success / false for a failure
     */
    public boolean update(int convID, @Nullable String name,
                          @Nullable ArrayList<Integer> members, boolean following){

        //Prepare a request on the database
        APIRequest params = new APIRequest(mContext,
                "conversations/updateSettings");
        params.addString("conversationID", ""+convID);

        //Add the name (if any)
        if(name != null)
            params.addString("name", name.equals("") ? "false" : name);

        //Add the members (if any)
        if(members != null)
            params.addString("members", ArrayUtils.int_array_to_string(members, ","));

        //Add following state
        params.addString("following", following ? "true" : "false");

        //Perform the request
        try {

            //Try to perform the request
            new APIRequestHelper().exec(params);

            //Delete the conversation from the local database to force it to be refreshed
            //on next load
            convDBHelper.delete(convID);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

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
            APIRequest params = new APIRequest(mContext, "conversations/getList");
            APIResponse response = new APIRequestHelper().exec(params);

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
     * Download informations about a single conversation online
     *
     * @param convID The ID of the conversation to fetch
     * @return Informations about the conversation in case of success / null else
     */
    @Nullable
    private ConversationsInfo downloadSingle(int convID){

        //Perform an API request
        APIRequest params = new APIRequest(mContext,
                "conversations/getInfosOne");
        params.addString("conversationID", ""+convID);

        try {

            APIResponse response = new APIRequestHelper().exec(params);

            JSONObject object = response.getJSONObject();

            return parseConversationJSON(object);

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

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
