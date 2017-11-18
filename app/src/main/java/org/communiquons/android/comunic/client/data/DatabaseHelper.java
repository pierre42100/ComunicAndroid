package org.communiquons.android.comunic.client.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.communiquons.android.comunic.client.data.DatabaseContract.FriendsListSchema;
import org.communiquons.android.comunic.client.data.DatabaseContract.UsersInfoSchema;

/**
 * Database helper. This file handles the creation / upgrade of the local database
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/2/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Generic definitions
     */
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    /**
     * Creation and deletion of the users info table
     */
    private static final String SQL_CREATE_USERS_INFOS_TABLE =
            "CREATE TABLE " + UsersInfoSchema.TABLE_NAME + " (" +
                    UsersInfoSchema._ID + " INTEGER PRIMARY KEY," +
                    UsersInfoSchema.COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    UsersInfoSchema.COLUMN_NAME_USER_FIRSTNAME + TEXT_TYPE + COMMA_SEP +
                    UsersInfoSchema.COLUMN_NAME_USER_LASTNAME + TEXT_TYPE + COMMA_SEP +
                    UsersInfoSchema.COLUMN_NAME_USER_ACCOUNT_IMAGE + TEXT_TYPE
            + " )";

    private static final String SQL_DELETE_USERS_INFOS_TABLE = "DROP TABLE IF EXISTS " +
            UsersInfoSchema.TABLE_NAME;


    /**
     * Creation and deletion of the friends table
     */
    private static final String SQL_CREATE_FRIENDS_LIST_TABLE =
            "CREATE TABLE " + FriendsListSchema.TABLE_NAME + " (" +
                    FriendsListSchema._ID + " INTEGER PRIMARY KEY," +
                    FriendsListSchema.COLUMN_NAME_FRIEND_ID + INTEGER_TYPE + COMMA_SEP +
                    FriendsListSchema.COLUMN_NAME_FRIEND_ACCEPTED + INTEGER_TYPE + COMMA_SEP +
                    FriendsListSchema.COLUMN_NAME_FRIEND_FOLLOWING + INTEGER_TYPE + COMMA_SEP +
                    FriendsListSchema.COLUMN_NAME_FRIEND_LAST_ACTIVITY + INTEGER_TYPE +
            " )";

    private static final String SQL_DELETE_FRIENDS_LIST_TABLE = "DROP TABLE IF EXISTS " +
            FriendsListSchema.TABLE_NAME;

    /**
     * Public constructor
     * @param context The context where the database is used
     */
    public DatabaseHelper(Context context){
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    /**
     * Handles database creation
     * @param db The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create user informations table
        db.execSQL(SQL_CREATE_USERS_INFOS_TABLE);

        //Create friends list table
        db.execSQL(SQL_CREATE_FRIENDS_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete users informations table
        db.execSQL(SQL_DELETE_USERS_INFOS_TABLE);

        //Delete friends list table
        db.execSQL(SQL_DELETE_FRIENDS_LIST_TABLE);

        //Perform creation table
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
