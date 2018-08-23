package org.communiquons.android.comunic.client.ui.listeners;

import android.view.View;

/**
 * Friends action listener
 *
 * @author Pierre HUBERT
 */
public interface OnFriendListActionListener {

    /**
     * Open a user page
     *
     * @param userID The ID of the user page to open
     */
    void onOpenUserPage(int userID);

    /**
     * Respond to a friendship request
     *
     * @param pos Position of the friend on the list
     * @param response TRUE to accept / FALSE else
     */
    void onRespondFrienshipRequest(int pos, boolean response);

    /**
     * Open the context menu for a friend
     *
     * @param pos The position of the friend in the list
     */
    void onOpenContextMenuForFriend(View view, int pos);
}
