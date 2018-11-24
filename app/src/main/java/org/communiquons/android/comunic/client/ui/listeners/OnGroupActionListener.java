package org.communiquons.android.comunic.client.ui.listeners;

/**
 * Action on group listener
 *
 * @author Pierre HUBERT
 */
public interface OnGroupActionListener extends OnGroupMembershipUpdateListener {

    /**
     * Open a group page
     *
     * @param groupID The ID of the group to open
     */
    void onOpenGroup(int groupID);

}
