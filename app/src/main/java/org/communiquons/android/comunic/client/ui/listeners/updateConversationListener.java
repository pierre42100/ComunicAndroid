package org.communiquons.android.comunic.client.ui.listeners;

/**
 * Handles the creation and / or the update of a conversation
 *
 * @author Pierre HUBERT
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
