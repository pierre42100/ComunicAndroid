package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.communiquons.android.comunic.client.data.DatabaseContract;
import org.communiquons.android.comunic.client.data.DatabaseContract.FriendsListSchema;
import org.communiquons.android.comunic.client.data.DatabaseContract.UsersInfoSchema;
import org.communiquons.android.comunic.client.data.DatabaseContract.ConversationsListSchema;
import org.communiquons.android.comunic.client.data.DatabaseContract.ConversationsMessagesSchema;

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
     * Creation and deletion of the conversations list table
     */
    private static final String SQL_CREATE_CONVERSATIONS_LIST_TABLE =
            "CREATE TABLE " + ConversationsListSchema.TABLE_NAME + " (" +
                    ConversationsListSchema._ID + " INTEGER PRIMARY KEY," +
                    ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID + INTEGER_TYPE + COMMA_SEP +
                    ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID_OWNER + INTEGER_TYPE + COMMA_SEP +
                    ConversationsListSchema.COLUMN_NAME_CONVERSATION_LAST_ACTIVE + INTEGER_TYPE + COMMA_SEP +
                    ConversationsListSchema.COLUMN_NAME_CONVERSATION_NAME + TEXT_TYPE + COMMA_SEP +
                    ConversationsListSchema.COLUMN_NAME_CONVERSATION_FOLLOWING + INTEGER_TYPE + COMMA_SEP +
                    ConversationsListSchema.COLUMN_NAME_CONVERSATION_SAW_LAST_MESSAGES + INTEGER_TYPE + COMMA_SEP +
                    ConversationsListSchema.COLUMN_NAME_CONVERSATION_MEMBERS + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_CONVERSATIONS_LIST_TABLE = "DROP TABLE IF EXISTS " +
            ConversationsListSchema.TABLE_NAME;


    /**
     * Creation and deletion of the conversation messages table
     */
    private static final String SQL_CREATE_CONVERSATION_MESSAGES_TABLE =
            "CREATE TABLE " + ConversationsMessagesSchema.TABLE_NAME + " (" +
                    ConversationsMessagesSchema._ID + " INTEGER PRIMARY KEY," +
                    ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID + INTEGER_TYPE + COMMA_SEP +
                    ConversationsMessagesSchema.COLUMN_NAME_CONVERSATION_ID + INTEGER_TYPE + COMMA_SEP +
                    ConversationsMessagesSchema.COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    ConversationsMessagesSchema.COLUMN_NAME_IMAGE_PATH + TEXT_TYPE + COMMA_SEP +
                    ConversationsMessagesSchema.COLUMN_NAME_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    ConversationsMessagesSchema.COLUMN_NAME_TIME_INSERT + INTEGER_TYPE +
            " )";

    private static final String SQL_DELETE_CONVERSATION_MESSAGES_TABLE = "DROP TABLE IF EXISTS " +
            ConversationsMessagesSchema.TABLE_NAME;


    /**
     * This object cached instance
     */
    private static DatabaseHelper instance;

    /**
     * Get the current database helper instance
     *
     * @param context The context
     * @return DatabaseHelper object
     */
    public static synchronized DatabaseHelper getInstance(Context context){
        if(instance == null)
            instance = new DatabaseHelper(context.getApplicationContext());

        return instance;
    }

    /**
     * Private constructor
     *
     * @param context The context where the database is used
     */
    private DatabaseHelper(Context context){
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    /**
     * Initialize the database
     *
     * @param db Database object
     */
    public void init_db(SQLiteDatabase db){

        //Create user informations table
        db.execSQL(SQL_CREATE_USERS_INFOS_TABLE);

        //Create friends list table
        db.execSQL(SQL_CREATE_FRIENDS_LIST_TABLE);

        //Create conversations list table
        db.execSQL(SQL_CREATE_CONVERSATIONS_LIST_TABLE);

        //Create messages list table
        db.execSQL(SQL_CREATE_CONVERSATION_MESSAGES_TABLE);
    }

    /**
     * Clear the whole content of the database
     *
     * @param db The database
     */
    public void clear_db(SQLiteDatabase db){
        //Delete users informations table
        db.execSQL(SQL_DELETE_USERS_INFOS_TABLE);

        //Delete friends list table
        db.execSQL(SQL_DELETE_FRIENDS_LIST_TABLE);

        //Delete conversations list table
        db.execSQL(SQL_DELETE_CONVERSATIONS_LIST_TABLE);

        //Delete conversations messages table
        db.execSQL(SQL_DELETE_CONVERSATION_MESSAGES_TABLE);
    }

    /**
     * Handles database creation
     * @param db The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        init_db(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Clear the database
        clear_db(db);

        //Perform tables creation
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
