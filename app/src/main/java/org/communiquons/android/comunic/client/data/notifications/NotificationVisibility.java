package org.communiquons.android.comunic.client.data.notifications;

/**
 * Notifications visibility enum
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/9/18.
 */

public enum NotificationVisibility {

    /**
     * When the notification is targeting only one single user
     */
    EVENT_PRIVATE,

    /**
     * When a notification is targeting several users
     */
    EVENT_PUBLIC,

    /**
     * Unknown visibility
     */
    UNKNOWN

}
