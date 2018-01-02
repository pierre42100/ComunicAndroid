package org.communiquons.android.comunic.client.data.conversations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
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
        APIRequestParameters params = new APIRequestParameters(mContext, "conversations/delete");
        params.addParameter("conversationID", ""+convID);

        try {
            new APIRequest().exec(params);
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
        APIRequestParameters params = new APIRequestParameters(mContext, "conversations/create");
        params.addParameter("name", name.equals("") ? "false" : name);
        params.addParameter("follow", follow ? "true" : "false");
        params.addParameter("users", members_str);

        //Perform the request
        try {
            APIResponse response = new APIRequest().exec(params);

            //Get conversation ID
            JSONObject obj = response.getJSONObject();
            return obj.getInt("conversationID");

        } catch (Exception e){
            e.printStackTrace();
            return null;
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
     * Download informations about a single conversation online
     *
     * @param convID The ID of the conversation to fetch
     * @return Informations about the conversation in case of success / null else
     */
    @Nullable
    private ConversationsInfo downloadSingle(int convID){

        //Perform an API request
        APIRequestParameters params = new APIRequestParameters(mContext,
                "conversations/getInfosOne");
        params.addParameter("conversationID", ""+convID);

        try {

            APIResponse response = new APIRequest().exec(params);

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

    /**
     * Handles the opening of a conversation
     *
     * This interface should be implemented in all the activity that should handle such action
     */
    public interface openConversationListener {

        /**
         * Open the conversation specified by its ID
         *
         * @param id The ID of the conversation to open
         */
        void openConversation(int id);

    }

    /**
     * Handles the creation and / or the update of a conversation
     */
    public interface updateConversationListener {

        /**
         * This method is called when a user wants to create a new conversation
         */
        void createConversation();

        /**
         * This method is called when the user want to open a conversation
         *
         * @param convID The ID of the conversation to open
         */
        void updateConversation(int convID);
    }

}
