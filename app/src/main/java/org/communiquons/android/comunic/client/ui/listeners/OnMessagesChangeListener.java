package org.communiquons.android.comunic.client.ui.listeners;

import android.support.annotation.NonNull;

import org.communiquons.android.comunic.client.data.models.ConversationMessage;

import java.util.ArrayList;

/**
 * OnMessagesChangeListener
 *
 * This interface is used to perform callback actions on the UI Thread to add messages
 * to a list for example
 *
 * This method also changes in the conversations
 *
 * @author Pierre HUBERT
 */
public interface OnMessagesChangeListener {

    /**
     * Add new messages to a previous list of messages
     *
     * @param lastID The ID of the latest message downloaded from server
     * @param messages The new messages
     */
    void onAddMessages(int lastID, @NonNull ArrayList<ConversationMessage> messages);

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

    /**
     * This method get called once we successfully retrieved new messages from server
     */
    void onGotMessageFromServer();

}
