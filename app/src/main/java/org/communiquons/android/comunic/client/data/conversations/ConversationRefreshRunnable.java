package org.communiquons.android.comunic.client.data.conversations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Runnable that refresh in the background the list of messages
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/16/17.
 */

public class ConversationRefreshRunnable implements Runnable {

    /**
     * Debug tag
     */
    private String TAG = "ConversationRefreshRunn";

    /**
     * The ID of the conversation
     */
    private int conversation_id;

    /**
     * The ID of the last message available
     */
    private int last_message_id;

    /**
     * Conversation message helper
     */
    private ConversationMessagesHelper convMessHelper;

    /**
     * Set to true to make the thread exit
     */
    private boolean quit = false;

    /**
     * Object that helps to make breaks between refreshes
     */
    private final Object object = new Object();

    /**
     * Create a new conversation refresh runnable
     *
     * @param conversation_id The ID of the conversation
     * @param last_message_id The ID of the last message already present in the list (set to 0
     *                        for no message)
     * @param conversationMessagesHelper Conversation message helper to get access to the database
     *                                   and to be able to query the API through helper
     */
    public ConversationRefreshRunnable(int conversation_id, int last_message_id,
                                       @NonNull ConversationMessagesHelper conversationMessagesHelper){
        this.conversation_id = conversation_id;
        this.last_message_id = last_message_id;
        this.convMessHelper = conversationMessagesHelper;
    }

    /**
     * onMessagesChangeListener
     *
     * This interface is used to perform callback actions on the UI Thread to add messages
     * to a list for example
     *
     * This method also changes in the conversations
     */
    public interface onMessagesChangeListener {

        /**
         * Add new messages to a previous list of messages
         *
         * @param messages The new messagess
         */
        void onAddMessage(@NonNull ArrayList<ConversationMessage> messages);

        /**
         * This method is called when there is not any message in the conversation
         *
         * Warning ! This method may be called several time
         */
        void onNoMessage();

        /**
         * This method is called when an error occur on a request on the database and / or on the
         * remote server
         */
        void onError();

    }

    @Override
    public void run() {
        //Log action
        Log.v(TAG, "Started conversation refresh runnable.");

        synchronized (object) {
            //Loop that execute indefinitely until the fragment is stopped
            while (!quit) {

                //Refresh the list of message from the server - the function return the ID of the last
                // message available
                int lastMessageInDb = convMessHelper.refresh_conversation(conversation_id);

                //If the last message in the database is newer than the last message of the caller
                if (lastMessageInDb > last_message_id) {

                    //Fetch all the messages available in the database since the last request
                    ArrayList<ConversationMessage> newMessages = convMessHelper.getInDb(
                            conversation_id,
                            last_message_id + 1,
                            lastMessageInDb
                    );

                    //Check for errors
                    if(newMessages == null){

                        //Callback : an error occurred.
                        Log.e(TAG, "Couldn't get the list of new messages from local database !");

                    }
                    else {
                        //Use the callback to send the messages to the UI thread
                        for(ConversationMessage message: newMessages)
                            Log.v(TAG, "Message: " + message.getContent());

                        //Update the ID of the last message fetched
                        last_message_id = lastMessageInDb;
                    }

                }

                if(lastMessageInDb == -1){

                    //Callback : an error occurred
                    Log.e(TAG, "Couldn't get the list of new messages !");

                }

                //Make a small break
                try {
                    object.wait(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        Log.v(TAG, "Stopped conversation refresh runnable.");
    }

    /**
     * Make the thread quit safely (does not interrupt currently running operation)
     */
    public void quitSafely(){
        quit = true;
    }
}
