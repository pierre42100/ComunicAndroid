package org.communiquons.android.comunic.client.data.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.DatabaseContract.ConversationsListSchema;
import org.communiquons.android.comunic.client.data.models.ConversationInfo;

import java.util.ArrayList;

/**
 * Conversations DB Helper
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/13/17.
 */

public class ConversationsListDbHelper {

    /**
     * Pointer on the database
     */
    private DatabaseHelper databaseHelper;

    /**
     * Conversations list table name
     */
    private static final String TABLE_NAME = ConversationsListSchema.TABLE_NAME;

    /**
     * Create the conversation database helper
     *
     * @param databaseHelper Object pointing on database helper
     */
    public ConversationsListDbHelper(@NonNull DatabaseHelper databaseHelper){
        this.databaseHelper = databaseHelper;
    }


    /**
     * Update the list of conversations currently installed in the system with a new list
     *
     * @param list The new list of conversation
     * @return TRUE for a success / FALSE else
     */
    boolean update_list(ArrayList<ConversationInfo> list){

        //Remove any old list of conversations
        delete_all();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //Process the list of conversation
        boolean success = true;
        for(ConversationInfo info : list){
            if(!insert(db, info))
                success = false;
        }

        //db.close();

        return success;
    }

    /**
     * Get information about a single conversation
     *
     * @param convID The conversation ID
     * @return Information about the conversation (if available locally) or null in case of failure
     */
    @Nullable
    ConversationInfo getInfo(int convID){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        //Prepare database request
        String table = ConversationsListSchema.TABLE_NAME;
        String[] columns = {
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID,
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID_OWNER,
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_LAST_ACTIVE,
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_NAME,
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_FOLLOWING,
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_SAW_LAST_MESSAGES,
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_MEMBERS,
        };
        String selection = ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID + " = ?";
        String[] selectionArgs = {""+convID};

        //Perform database request
        Cursor c = db.query(table, columns, selection, selectionArgs, null, null, null);

        ConversationInfo infos = null;

        //Check for result
        if(c.getCount() != 0){
            c.moveToFirst();

            //Parse result
            infos = getConvObj(c);
        }

        c.close();
        //db.close();

        return infos;

    }

    /**
     * Delete a single conversation from the database
     *
     * @param convID The ID of the conversation to delete
     */
    void delete(int convID){

        //Get writeable access to the database
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //Perform a request on the database
        String condition = ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID + " = ?";
        String[] values = {""+convID};
        db.delete(ConversationsListSchema.TABLE_NAME, condition, values);

    }

    /**
     * Delete all the list of conversations
     */
    private void delete_all(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //Prepare the request
        db.delete(TABLE_NAME, null, null);

        //db.close();
    }

    /**
     * Insert a new conversation in the database
     * @param db Pointer on active database
     * @param info Informations about the conversation to insert
     * @return TRUE for a success / False else
     */
    private boolean insert(@NonNull SQLiteDatabase db, @NonNull ConversationInfo info){

        ContentValues values = getContentValues(info);

        return -1 == db.insert(TABLE_NAME, null, values);

    }

    /**
     * Get the contentvalues corresponding to a conversation
     *
     * @param info Information about a conversation
     * @return The values of the conservation
     */
    private ContentValues getContentValues(ConversationInfo info){
        ContentValues values = new ContentValues();

        values.put(ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID, info.getID());
        values.put(ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID_OWNER, info.getID_owner());
        values.put(ConversationsListSchema.COLUMN_NAME_CONVERSATION_LAST_ACTIVE,
                info.getLast_active());

        String name = info.hasName() ? info.getName() : "null";
        values.put(ConversationsListSchema.COLUMN_NAME_CONVERSATION_NAME, name);

        values.put(ConversationsListSchema.COLUMN_NAME_CONVERSATION_FOLLOWING,
                info.isFollowing() ? 1 : 0);

        values.put(ConversationsListSchema.COLUMN_NAME_CONVERSATION_SAW_LAST_MESSAGES,
                info.hasSaw_last_message() ? 1 : 0);

        values.put(ConversationsListSchema.COLUMN_NAME_CONVERSATION_MEMBERS,
                info.getMembersString());

        return values;
    }

    /**
     * Get the conversation object related to a current cursor position
     *
     * @param c The cursor
     * @return The Generated conversation information
     */
    private ConversationInfo getConvObj(Cursor c){

        ConversationInfo infos = new ConversationInfo();

        //Get the values
        infos.setID(c.getInt(c.getColumnIndexOrThrow(
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID)));
        infos.setID_owner(c.getInt(c.getColumnIndexOrThrow(
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_ID_OWNER)));
        infos.setLast_active(c.getInt(c.getColumnIndexOrThrow(
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_LAST_ACTIVE)));
        infos.setName(c.getString(c.getColumnIndexOrThrow(
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_NAME)));
        infos.setFollowing(c.getInt(c.getColumnIndexOrThrow(
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_FOLLOWING)) == 1);
        infos.setSaw_last_message(c.getInt(c.getColumnIndexOrThrow(
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_SAW_LAST_MESSAGES)) == 1);
        infos.parseMembersString(c.getString(c.getColumnIndexOrThrow(
                ConversationsListSchema.COLUMN_NAME_CONVERSATION_MEMBERS)));

        return infos;

    }
}
