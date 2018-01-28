package org.communiquons.android.comunic.client.data.conversations;

import android.app.Activity;
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
     * The activity executing the application
     */
    private Activity mActivity;

    /**
     * Messages change listener
     */
    private onMessagesChangeListener listener;

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
     * @param activity The activity of the application
     * @param listener The listener to get the new messages
     */
    public ConversationRefreshRunnable(int conversation_id,
                                       int last_message_id,
                                       @NonNull ConversationMessagesHelper conversationMessagesHelper,
                                       Activity activity,
                                       onMessagesChangeListener listener){
        this.conversation_id = conversation_id;
        this.last_message_id = last_message_id;
        this.convMessHelper = conversationMessagesHelper;
        this.mActivity = activity;
        this.listener = listener;
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
         * @param lastID The ID of the latest message downloaded from server
         * @param messages The new messagess
         */
        void onAddMessage(int lastID, @NonNull ArrayList<ConversationMessage> messages);

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
        void onLoadError();

    }

    @Override
    public void run() {
        //Log action
        Log.v(TAG, "Started conversation refresh runnable.");

        synchronized (object) {

            //Loop that execute indefinitely until the fragment is stopped
            while (!quit) {

                //Refresh the list of messages from the server
                if(!convMessHelper.refresh_conversation(conversation_id)){
                    //Callback : an error occurred
                    Log.e(TAG, "Couldn't get the list of new messages !");

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onLoadError();
                        }
                    });

                    continue;
                }

                //Get the ID of the last message available in the local database
                final int lastMessageInDb = convMessHelper.getLastIDFromDb(conversation_id);

                //If the last message in the database is newer than the last message already read
                if (lastMessageInDb > last_message_id) {

                    //Fetch all the messages available in the database since the last request
                    final ArrayList<ConversationMessage> newMessages = convMessHelper.getInDb(
                            conversation_id,
                            last_message_id + 1,
                            lastMessageInDb
                    );

                    //Check for errors
                    if(newMessages == null){

                        //Callback : an error occurred.
                        Log.e(TAG, "Couldn't get the list of new messages from local database !");

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onLoadError();
                            }
                        });
                    }
                    else {

                        //Use the callback to send the messages to the UI thread
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onAddMessage(lastMessageInDb, newMessages);
                            }


                        });

                        //Update the ID of the last message fetched
                        last_message_id = lastMessageInDb;
                    }

                }

                //Check if there isn't any message
                if(last_message_id == 0){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onNoMessage();
                        }
                    });
                }

                //Make a small break (1 sec 200)
                try {
                    object.wait(1200);
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
