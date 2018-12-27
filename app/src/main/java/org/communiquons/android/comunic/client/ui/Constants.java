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
}
