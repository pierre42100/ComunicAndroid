package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import org.communiquons.android.comunic.client.data.models.APIFileRequest;
import org.communiquons.android.comunic.client.data.models.APIPostFile;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.ConversationMessage;
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
     * Constructor of the helper
     *
     * @param context The context of the application
     */
    public ConversationMessagesHelper(Context context){
        this(context.getApplicationContext(), DatabaseHelper.getInstance(context));
    }

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
     * @return TRUE in case of success / FALSE else
     */
    public boolean refresh_conversation(int conversation_id){

        //Get the ID of the last message available in the database
        int last_message_id = getLastIDFromDb(conversation_id);

        //Perform a request on the database
        ArrayList<ConversationMessage> new_messages = downloadNew(conversation_id, last_message_id);

        //Check for errors
        if(new_messages == null){
            //An error occurred
            return false;
        }

        //Add the new messages to the database (if any)
        if(new_messages.size() > 0) {
            mDbHelper.insertMultiple(new_messages);
        }

        //Get the last message ID from database again
        return true;
    }

    /**
     * Send a new message to the conversation
     *
     * @param convID Target conversation ID
     * @param message The message to send
     * @param image Image to include with the request, as bitmap (can be null)
     * @return true in case of success / false else
     */
    public boolean sendMessage(int convID, String message, @Nullable Bitmap image){

        //Make an API request
        APIFileRequest params = new APIFileRequest(mContext,
                "conversations/sendMessage");
        params.addString("conversationID", ""+convID);
        params.addString("message", message);

        //Include image (if any)
        if(image != null) {
            APIPostFile file = new APIPostFile();
            file.setFieldName("image");
            file.setFileName("conversationImage.png");
            file.setBitmap(image);
            params.addFile(file);
        }

        try {

            if(image != null){
                //Perform a POST request
                new APIRequestHelper().execPostFile(params);
            }
            else
                //Perform normal request
                new APIRequestHelper().exec(params);

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
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
    public ArrayList<ConversationMessage> getInDb(int conv, int start, int end){

        return mDbHelper.getInterval(conv, start, end);

    }

    /**
     * Request the request of older messages from the database / API
     *
     * @param convID Target conversationID
     * @param oldestMessageID The ID of the oldest known message
     * @return The list of messages / null in case of failure
     */
    @Nullable
    public ArrayList<ConversationMessage> getOlderMessages(int convID, int oldestMessageID){

        //Check what is the oldest message stored in the database
        int oldestMessageDownloaded = mDbHelper.getOldestMessageID(convID);

        //Check if no message has been already download
        if(oldestMessageDownloaded == 0)
            return null;

        //Download older messages from the server
        ArrayList<ConversationMessage> messages =
                downloadOlder(convID, oldestMessageDownloaded, 10);

        //Check for errors
        if(messages == null)
            return null;

        //Save the new messages
        if(!mDbHelper.insertMultiple(messages))
            return null;

        //Get the messages
        return mDbHelper.getInterval(convID, 0, oldestMessageID - 1);
    }

    /**
     * Get the ID of the last message of the conversation from the database
     *
     * @param conversation_id Target conversation
     * @return The ID of the last message available in the database or 0 in case of failure
     */
    public int getLastIDFromDb(int conversation_id){

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
        APIRequest params = new APIRequest(mContext,
                "conversations/refresh_single");
        params.addString("conversationID", ""+conversationID);
        params.addString("last_message_id", ""+last_message_id);

        ArrayList<ConversationMessage> list = new ArrayList<>();

        try {
            //Perform the request
            APIResponse response = new APIRequestHelper().exec(params);

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
     * Get older messages for a conversation
     *
     * @param conversationID The ID of the target conversation
     * @param oldestMessageID The ID of the oldest known message
     * @param limit The limit for the download
     * @return The list of message / null in case of failure
     */
    @Nullable
    private ArrayList<ConversationMessage> downloadOlder(int conversationID, int oldestMessageID,
                                                         int limit){

        ArrayList<ConversationMessage> list = new ArrayList<>();

        //Prepare a request over the server
        APIRequest req = new APIRequest(mContext, "conversations/get_older_messages");
        req.addInt("conversationID", conversationID);
        req.addInt("oldest_message_id", oldestMessageID);
        req.addInt("limit", limit);

        //Perform the request
        try {

            APIResponse response = new APIRequestHelper().exec(req);

            //Check for errors
            if(response.getResponse_code() != 200)
                return null;

            //Process the list of messages
            JSONArray messages = response.getJSONArray();

            for(int i = 0; i < messages.length(); i++)
                list.add(getMessageObject(conversationID, messages.getJSONObject(i)));

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return list;
    }

    /**
     * Update a conversation message
     *
     * @param message Information about the message to update
     * @return TRUE for a success / FALSE else
     */
    public boolean updateMessage(ConversationMessage message){

        //Perform the request over the API
        APIRequest request = new APIRequest(mContext, "conversations/updateMessage");
        request.addInt("messageID", message.getId());

        if(message.hasContent())
            request.addString("content", message.getContent());

        try {
            APIResponse response = new APIRequestHelper().exec(request);

            return response.getResponse_code() == 200 && mDbHelper.updateMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a conversation message
     *
     * @param messageID The ID of the message to delete
     * @return TRUE for a success / FALSE else
     */
    public boolean deleteMessage(int messageID){

        //Make a request on the server
        APIRequest request = new APIRequest(mContext, "conversations/deleteMessage");
        request.addInt("messageID", messageID);

        try {
            APIResponse response = new APIRequestHelper().exec(request);

            if(response.getResponse_code() != 200) return false;

            //Delete the message in the local database
            return mDbHelper.deleteMessage(messageID);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
