package org.communiquons.android.comunic.client.data.conversations;

import android.content.Context;
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
 * Conversation messages helper
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/16/17.
 */

public class ConversationMessagesHelper {

    /**
     * Debug tag
     */
    private static final String TAG = "ConversationMessagesHel";

    /**
     * Conversations messages database helper
     */
    private ConversationMessagesDbHelper mDbHelper;

    /**
     * Context of execution of the application
     */
    private Context mContext;

    /**
     * Public constructor of the helper
     *
     * @param context The context of execution of the application
     * @param dbHelper Database helper associated with the context
     */
    public ConversationMessagesHelper(Context context, DatabaseHelper dbHelper){
        mContext = context;
        mDbHelper = new ConversationMessagesDbHelper(dbHelper);
    }

    /**
     * Get the latest messages of a conversation
     *
     * @param conversation_id The ID of the conversation to refresh
     * @return The ID of the last message available in the database
     */
    int refresh_conversation(int conversation_id){

        //Get the ID of the last message available in the database
        int last_message_id = getLastIDFromDb(conversation_id);

        //Perform a request on the database
        ArrayList<ConversationMessage> new_messages = downloadNew(conversation_id, last_message_id);

        //Check for errors
        if(new_messages == null){
            //An error occurred
            return -1;
        }

        //Add the new messages to the database (if any)
        if(new_messages.size() > 0) {
            mDbHelper.insertMultiple(new_messages);
        }

        //Get the last message ID from database again
        return getLastIDFromDb(conversation_id);
    }

    /**
     * Fetch messages from the database
     *
     * @param conv Conversation ID
     * @param start The ID of the oldest message to fetch
     * @param end The ID of the last message to fetch
     * @return The message of the interval, or null in case of failure
     */
    @Nullable
    ArrayList<ConversationMessage> getInDb(int conv, int start, int end){

        return mDbHelper.getInterval(conv, start, end);

    }

    /**
     * Get the ID of the last message of the conversation from the database
     *
     * @param conversation_id Target conversation
     * @return The ID of the last message available in the database or 0 in case of failure
     */
    private int getLastIDFromDb(int conversation_id){

        //Get the id of the last message available in the database
        ConversationMessage last_message = mDbHelper.getLast(conversation_id);

        //Check if there isn't any message
        if(last_message == null)
            return 0; //There is no message in the database yet

        //Return the ID of the last message available
        else
            return last_message.getId();
    }

    /**
     * Download the latest messages available in the API
     *
     * @param conversationID The ID of the target conversation
     * @param last_message_id The ID of the last known message (0 for none)
     * @return null in case of failure, an empty array if there is no new messages available of the
     * list of new messages for the specified conversation
     */
    @Nullable
    private ArrayList<ConversationMessage> downloadNew(int conversationID, int last_message_id){

        //Prepare a request on the API
        APIRequestParameters params = new APIRequestParameters(mContext,
                "conversations/refresh_single");
        params.addParameter("conversationID", ""+conversationID);
        params.addParameter("last_message_id", ""+last_message_id);

        ArrayList<ConversationMessage> list = new ArrayList<>();

        try {
            //Perform the request
            APIResponse response = new APIRequest().exec(params);

            //Get the list of new messages
            JSONArray messages = response.getJSONArray();

            for(int i = 0; i < messages.length(); i++){

                //Convert the message into a message object
                list.add(getMessageObject(conversationID, messages.getJSONObject(i)));

            }

        } catch (Exception e){
            Log.e(TAG, "Couldn't refresh the list of messages!");
            e.printStackTrace();
            return null;
        }

        return list;
    }

    /**
     * Convert a JSON object into a conversation message element
     *
     * @param convID The ID of the current conversation
     * @param obj The target object
     * @return Generation Conversation Message object
     * @throws JSONException If the JSON objected couldn't be decoded
     */
    private ConversationMessage getMessageObject(int convID, JSONObject obj) throws JSONException{

        ConversationMessage message = new ConversationMessage();

        //Get the message values
        message.setId(obj.getInt("ID"));
        message.setConversation_id(convID);
        message.setUser_id(obj.getInt("ID_user"));
        message.setImage_path(obj.getString("image_path"));
        message.setContent(obj.getString("message"));
        message.setTime_insert(obj.getInt("time_insert"));

        return message;

    }
}
