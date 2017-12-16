package org.communiquons.android.comunic.client.data;

import android.provider.BaseColumns;

/**
 * Database contract schema. Contains the structure of the application database
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/2/17.
 */

public final class DatabaseContract {

    //Empty constructor : avoid accidentally instantiations of the class
    public DatabaseContract(){}

    /* Database basic information */
    static final int DATABASE_VERSION = 4;
    static final String DATABASE_NAME = "database.db";

    /* Users info table */
    public static abstract class UsersInfoSchema implements BaseColumns {
        public static final String TABLE_NAME = "users_info";

        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_USER_FIRSTNAME = "first_name";
        public static final String COLUMN_NAME_USER_LASTNAME = "last_name";
        public static final String COLUMN_NAME_USER_ACCOUNT_IMAGE = "account_image";

    }

    /* Friends list table */
    public static abstract class FriendsListSchema implements BaseColumns {
        public static final String TABLE_NAME = "friends_list";

        public static final String COLUMN_NAME_FRIEND_ID = "friend_id";
        public static final String COLUMN_NAME_FRIEND_ACCEPTED = "accepted";
        public static final String COLUMN_NAME_FRIEND_FOLLOWING = "following";
        public static final String COLUMN_NAME_FRIEND_LAST_ACTIVITY = "last_activity";

    }

    /* Conversations list table */
    public static abstract class ConversationsListSchema implements BaseColumns {
        public static final String TABLE_NAME = "conversations_list";

        public static final String COLUMN_NAME_CONVERSATION_ID = "conversation_id";
        public static final String COLUMN_NAME_CONVERSATION_ID_OWNER = "id_owner";
        public static final String COLUMN_NAME_CONVERSATION_LAST_ACTIVE = "last_active";
        public static final String COLUMN_NAME_CONVERSATION_NAME = "name";
        public static final String COLUMN_NAME_CONVERSATION_FOLLOWING = "following";
        public static final String COLUMN_NAME_CONVERSATION_SAW_LAST_MESSAGES = "saw_last_message";
        public static final String COLUMN_NAME_CONVERSATION_MEMBERS = "members";
    }

    /* Conversations messages table */
    public static abstract class ConversationsMessagesSchema implements BaseColumns {
        public static final String TABLE_NAME = "conversation_messages";

        public static final String COLUMN_NAME_MESSAGE_ID = "message_id";
        public static final String COLUMN_NAME_CONVERSATION_ID = "conversation_id";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_IMAGE_PATH = "image_path";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_TIME_INSERT = "time_insert";
    }

}
