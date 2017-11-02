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
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database.db";

    /* Users info table */
    public static abstract class UsersInfo implements BaseColumns {
        public static final String TABLE_NAME = "users_info";

        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_USER_FIRSTNAME = "first_name";
        public static final String COLUMN_NAME_USER_LASTNAME = "last_name";
        public static final String COLUMN_NAME_USER_ACCOUNT_IMAGE = "account_image";

    }

}
