package org.communiquons.android.comunic.client.data.enums;

/**
 * Enum of the notifications types
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/9/18.
 */

public enum NotificationTypes {

    /**
     * When a comment was created
     */
    COMMENT_CREATED,

    /**
     * When a friendship request was sent
     */
    SENT_FRIEND_REQUEST,

    /**
     * When a friendship request was accepted
     */
    ACCEPTED_FRIEND_REQUEST,

    /**
     * When a friend request was rejected
     */
    REJECTED_FRIEND_REQUEST,

    /**
     * When an elem has been created
     */
    ELEM_CREATED,

    /**
     * When an element is updated
     */
    ELEM_UPDATED,

    /**
     * Group membership invitations
     */
    SENT_GROUP_MEMBERSHIP_INVITATION,
    ACCEPTED_GROUP_MEMBERSHIP_INVITATION,
    REJECTED_GROUP_MEMBERSHIP_INVITATION,

    /**
     * Group membership requests
     */
    SENT_GROUP_MEMBERSHIP_REQUEST,
    ACCEPTED_GROUP_MEMBERSHIP_REQUEST,
    REJECTED_GROUP_MEMBERSHIP_REQUEST,

    /**
     * Unknown notification type
     */
    UNKNOWN

}
