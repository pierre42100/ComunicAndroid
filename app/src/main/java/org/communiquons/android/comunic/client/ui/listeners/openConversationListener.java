package org.communiquons.android.comunic.client.ui.listeners;

/**
 * Handles the opening of a conversation
 *
 * This interface should be implemented in all the activity that should handle such action
 *
 * @author Pierre HUBERT
 */
public interface openConversationListener {

    /**
     * Open the conversation specified by its ID
     *
     * @param id The ID of the conversation to open
     */
    void openConversation(int id);

    /**
     * Open a private conversation with the specified user ID
     *
     * @param userID The ID with who to start a private conversation
     */
    void openPrivateConversation(int userID);

}
