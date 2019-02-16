package org.communiquons.android.comunic.client.ui.listeners;

/**
 * Open call listener
 *
 * @author Pierre HUBERT
 */
public interface OnOpenCallListener {

    /**
     * Create and open a call for a conversation
     *
     * @param convID The ID of the target conversation
     */
    void createCallForConversation(int convID);

    /**
     * Open a specifed call
     *
     * @param callID The ID of the target call
     */
    void openCall(int callID);
}
