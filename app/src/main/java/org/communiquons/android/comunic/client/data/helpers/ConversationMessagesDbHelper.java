package org.communiquons.android.comunic.client.data.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.DatabaseContract.ConversationsMessagesSchema;
import org.communiquons.android.comunic.client.data.models.ConversationMessage;

import java.util.ArrayList;

/**
 * Conversation messages database helper
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/16/17.
 */

class ConversationMessagesDbHelper {

    /**
     * Debug tag
     */
    private static final String TAG = ConversationMessagesDbHelper.class.getSimpleName();

    /**
     * Database helper object
     */
    private DatabaseHelper dbHelper;

    /**
     * Conversations messages table name
     */
    private static final String TABLE_NAME = ConversationsMessagesSchema.TABLE_NAME;

    /**
     * Conversation messages table column
     */
    private final String[] columns = {
            ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID,
            ConversationsMessagesSchema.COLUMN_NAME_CONVERSATION_ID,
            ConversationsMessagesSchema.COLUMN_NAME_USER_ID,
            ConversationsMessagesSchema.COLUMN_NAME_IMAGE_PATH,
            ConversationsMessagesSchema.COLUMN_NAME_MESSAGE,
            ConversationsMessagesSchema.COLUMN_NAME_TIME_INSERT
    };

    /**
     * Class constructor
     *
     * @param context Application context
     */
    ConversationMessagesDbHelper(@NonNull Context context){
        this.dbHelper = DatabaseHelper.getInstance(context);
    }


    /**
     * Get the last message stored of a conversation
     *
     * @param conversationID The ID of the target conversation
     * @return The last message of the conversation or null if no message were found
     */
    @Nullable
    ConversationMessage getLast(int conversationID){

        ConversationMessage message = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Prepare the query on the database
        String selection = ConversationsMessagesSchema.COLUMN_NAME_CONVERSATION_ID + " =  ?";
        String[] selectionArgs = {""+conversationID};
        String orderBy = ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID + " DESC";
        String limit = "1";

        //Perform the request
        Cursor response = db.query(TABLE_NAME, columns, selection, selectionArgs, null,
                null, orderBy, limit);

        //Check for response
        if(response.getCount() != 0){
            response.moveToFirst();
            message = getMessageFromCursorPos(response);
        }

        response.close();
        //db.close();

        return message;
    }

    /**
     * Get the oldest message known of a conversation
     *
     * @param convID Target conversation ID
     * @return The ID of the latest known message / -1 in case of failure
     */
    int getOldestMessageID(int convID) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Prepare the query over the database
        String[] columns = {ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID};
        String selection = ConversationsMessagesSchema.COLUMN_NAME_CONVERSATION_ID + " = ?";
        String[] selectionArgs = {""+convID};
        String orderBy = ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID;

        //Perform the request
        Cursor response = db.query(TABLE_NAME, columns, selection, selectionArgs, null,
                null, orderBy, "1");

        //Process response
        int messageID = 0;
        if(response.getCount() != 0){
            response.moveToFirst();
            messageID = response.getInt(response.getColumnIndexOrThrow(
                    ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID));
        }
        response.close();

        return  messageID;
    }

    /**
     * Insert a list of messages into the database
     *
     * @param list The list of messages to insert
     * @return The result of the operation
     */
    boolean insertMultiple(ArrayList<ConversationMessage> list){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean success = true;

        //Process the list of messages
        for(ConversationMessage message : list){

            //Try to insert the message
            if(!insertOne(db, message))
                success = false;

        }

        //db.close();

        return success;
    }

    /**
     * Get an interval of messages from the database
     *
     * @param conv The conversation ID
     * @param start The ID of the oldest message to fetch
     * @param end The ID of the newest message to fetch
     * @return The list of messages | null in case of failure
     */
    ArrayList<ConversationMessage> getInterval(int conv, int start, int end){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Perform a request on the database
        String selection = ConversationsMessagesSchema.COLUMN_NAME_CONVERSATION_ID + " = ? AND " +
                " " + ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID + " >= ? AND " +
                ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID + " <= ?";
        String[] selectionArgs = {
                ""+conv,
                ""+start,
                ""+end
        };
        String order = ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID;
        Cursor cur = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, order);

        //Process each response
        ArrayList<ConversationMessage> list = new ArrayList<>();
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            list.add(getMessageFromCursorPos(cur));
            cur.moveToNext();
        }

        //Close objects
        cur.close();
        //db.close();

        return list;
    }

    /**
     * Update a conversation message in the database
     *
     * @param message Information about the message to update
     * @return TRUE in case of success / FALSE else
     */
    boolean updateMessage(ConversationMessage message){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String conditions = ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID + " = ?";
        String[] values = new String[]{message.getId()+""};

        return db.update(TABLE_NAME, getContentValues(message), conditions, values) > 0;
    }

    /**
     * Delete a message from the local database
     *
     * @param messageID The ID of the message to delete
     * @return The result of the operation
     */
    boolean deleteMessage(int messageID){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String conditions = ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID + " = ?";
        String[] args = {messageID+""};
        return db.delete(TABLE_NAME, conditions, args) > 0;
    }

    /**
     * Insert a single message into the database
     *
     * @param db Database object (with writeable access)
     * @param message The message to insert into the database
     * @return TRUE in case of success / FALSE else
     */
    private boolean insertOne(SQLiteDatabase db, ConversationMessage message){

        //Perform the query
        return db.insert(TABLE_NAME, null, getContentValues(message)) != -1;

    }

    /**
     * Fill a message object based on a current cursor positin
     *
     * @param cursor The response cursor
     * @return ConversationMessage object
     */
    private ConversationMessage getMessageFromCursorPos(Cursor cursor){

        ConversationMessage message = new ConversationMessage();

        //Query database result
        message.setId(cursor.getInt(cursor.getColumnIndexOrThrow(
                ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID)));

        message.setConversation_id(cursor.getInt(cursor.getColumnIndexOrThrow(
                ConversationsMessagesSchema.COLUMN_NAME_CONVERSATION_ID)));

        message.setUser_id(cursor.getInt(cursor.getColumnIndexOrThrow(
                ConversationsMessagesSchema.COLUMN_NAME_USER_ID)));

        message.setImage_path(cursor.getString(cursor.getColumnIndexOrThrow(
                ConversationsMessagesSchema.COLUMN_NAME_IMAGE_PATH)));

        message.setContent(cursor.getString(cursor.getColumnIndexOrThrow(
                ConversationsMessagesSchema.COLUMN_NAME_MESSAGE)));

        message.setTime_insert(cursor.getInt(cursor.getColumnIndexOrThrow(
                ConversationsMessagesSchema.COLUMN_NAME_TIME_INSERT)));

        return message;
    }

    /**
     * Generate a ContentValue from a message object
     *
     * @param message The message to convert
     * @return The generated ContentValues
     */
    private ContentValues getContentValues(ConversationMessage message){

        //Generate the ContentValues and return it
        ContentValues cv = new ContentValues();
        cv.put(ConversationsMessagesSchema.COLUMN_NAME_MESSAGE_ID, message.getId());
        cv.put(ConversationsMessagesSchema.COLUMN_NAME_CONVERSATION_ID,
                message.getConversation_id());
        cv.put(ConversationsMessagesSchema.COLUMN_NAME_USER_ID, message.getUser_id());
        cv.put(ConversationsMessagesSchema.COLUMN_NAME_IMAGE_PATH, message.getImagePathNotNull());
        cv.put(ConversationsMessagesSchema.COLUMN_NAME_MESSAGE, message.getContent());
        cv.put(ConversationsMessagesSchema.COLUMN_NAME_TIME_INSERT, message.getTime_insert());
        return cv;

    }
}
