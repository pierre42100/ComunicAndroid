package org.communiquons.android.comunic.client.ui.listeners;

/**
 * This interface is implemented on activities that can open users page
 *
 * @author Pierre HUBERT
 */
public interface onOpenUsersPageListener {

    /**
     * On a user page specified by its id
     *
     * @param userID The ID of the user to create page
     */
    void openUserPage(int userID);

    /**
     * Open the page of a user for which the access has been denied
     *
     * @param userID The ID of the target user
     */
    void openUserAccessDeniedPage(int userID);
}
