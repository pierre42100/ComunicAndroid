package org.communiquons.android.comunic.client.ui.listeners;

import android.view.View;

import org.communiquons.android.comunic.client.data.models.ConversationMessage;

/**
 * Listener for actions on conversation messages
 *
 * @author Pierre HUBERT
 */
public interface OnConversationMessageActionsListener {

    /**
     * Open messages context menu for message at a specified position
     *
     * @param pos The position of the context menu
     * @param v The view to display the context menu
     */
    void onOpenContextMenu(int pos, View v);

    /**
     * Ask the user to confirm the deletion of a conversation message
     *
     * @param pos The position of the message to delete
     */
    void onConfirmDeleteConversationMessage(int pos);

}
