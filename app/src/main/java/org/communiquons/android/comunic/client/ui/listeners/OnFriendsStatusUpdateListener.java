package org.communiquons.android.comunic.client.ui.listeners;

/**
 * On friend status update listener
 *
 * @author Pierre HUBERT
 */
public interface OnFriendsStatusUpdateListener {

    /**
     * This method is called when the two people are friends.
     *
     * Warning ! This may be called several times...
     */
    void onAreFriend();

}
