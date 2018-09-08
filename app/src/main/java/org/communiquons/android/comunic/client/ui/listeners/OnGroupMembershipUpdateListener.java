package org.communiquons.android.comunic.client.ui.listeners;

import org.communiquons.android.comunic.client.data.models.GroupInfo;

public interface OnGroupMembershipUpdateListener {

    /**
     * Respond to a group membership invitation
     *
     * @param group Information about the target group
     * @param accept TRUE to accept / FALSE else
     */
    void onRespondInvitation(GroupInfo group, boolean accept);

    /**
     * Cancel a group membership request
     *
     * @param group The target group
     */
    void onCancelRequest(GroupInfo group);

    /**
     * Send a request to join a group
     *
     * @param group The target group
     */
    void onSendRequest(GroupInfo group);

    /**
     * Send a request to leave a group
     *
     * @param group The target group
     */
    void onLeaveGroup(GroupInfo group);
}
