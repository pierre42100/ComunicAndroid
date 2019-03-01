package org.communiquons.android.comunic.client.ui;

/**
 * UI constants
 *
 * @author Pierre HUBERT
 */
public final class Constants {

    /**
     * Intents request codes
     */
    public final class IntentRequestCode {

        /**
         * Find user ID intent for conversation update
         */
        public static final int CONVERSATION_UPDATE_FIND_USER_ID_INTENT = 0;

        /**
         * Pick image for conversation message
         */
        public static final int CONVERSATION_MESSAGE_PICK_PHOTO = 1;

        /**
         * Pick image for post creation form
         */
        public static final int POST_CREATE_FORM_PICK_PHOTO = 2;

        /**
         * Main Activity : search a user
         */
        public static final int MAIN_ACTIVITY_SEARCH_USER_INTENT = 3;

        /**
         * Main Activity : make a global search
         */
        public static final int MAIN_ACTIVITY_GLOBAL_SEARCH_INTENT = 4;

        /**
         * Pick image to update account image
         */
        public static final int ACCOUNT_IMAGE_SETTINGS_PICK_NEW_INTENT = 5;
    }


    /**
     * Intents results
     */
    public final class IntentResults {

        /**
         * Search user result
         */
        public static final String SEARCH_USER_RESULT = "org.communiquons.android.searchUser.RESULT";

        /**
         * Global search result
         */
        public static final String SEARCH_GLOBAL_RESULT = "org.communiquons.android.globalSearch.RESULT";

    }

    /**
     * Intents actions
     */
    public final class IntentActions {

        /**
         * Intent used to notify of new available calls
         */
        public static final String ACTION_NOTIFY_NEW_CALLS_AVAILABLE =
                "org.communiquons.android.comunic.client.NEW_CALLS_AVAILABLE";


        /**
         * Intent used to reject incoming call
         */
        public static final String ACTION_REJECT_INCOMING_CALL =
                "org.communiquons.android.comunic.client.REJECT_INCOMING_CALL";

    }

    /**
     * Notifications channels
     */
    public final class NotificationsChannels {

        /**
         * Global channel information
         */
        public static final String GLOBAL_CHANNEL_ID = "MainNotifChannel";
        public static final String GLOBAL_CHANNEL_NAME = "MainNotificationChannel";
        public static final String GLOBAL_CHANNEL_DESCRIPTION = "Global Comunic notifications";


        /**
         * Call channel information
         */
        public static final String CALL_CHANNEL_ID = "CallChannel";
        public static final String CALL_CHANNEL_NAME = "Call Notification Channel";
        public static final String CALL_CHANNEL_DESCRIPTION = "Channel used to notify incoming calls";
    }

    /**
     * Notifications IDs
     */
    public final class Notifications {

        /**
         * Main notification ID
         */
        public static final int MAIN_NOTIFICATION_ID = 0;


        /**
         * Call notification ID
         */
        public static final int CALL_NOTIFICATION_ID = 1;

    }


    /**
     * Preferences keys
     */
    public final class PreferencesKeys {

        /**
         * Enable debug mode
         */
        public static final String PREFERENCE_ENABLE_DEBUG_MODE = "enable_debug_mode";

        /**
         * Accelerate notifications refresh
         */
        public static final String PREFERENCE_ACCELERATE_NOTIFICATIONS_REFRESH
                = "accelerate_notifications_refresh";
    }


    /**
     * External storage directory
     */
    public final class EXTERNAL_STORAGE {

        /**
         * Main storage directory
         */
        public static final String MAIN_DIRECTORY_NAME = "Comunic";

        /**
         * Video calls directory
         */
        public static final String VIDEO_CALLS_STORAGE_DIRECTORY = "VideoCalls";
    }
}
