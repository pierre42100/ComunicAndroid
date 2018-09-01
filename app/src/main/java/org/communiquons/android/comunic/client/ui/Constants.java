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
         * Intent code : search a user
         */
        public static final int MAIN_ACTIVITY_SEARCH_USER_INTENT = 3;

        /**
         * Pick image to update account image
         */
        public static final int ACCOUNT_IMAGE_SETTINGS_PICK_NEW_INTENT = 4;
    }

}
