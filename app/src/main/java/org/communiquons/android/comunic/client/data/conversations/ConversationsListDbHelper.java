package org.communiquons.android.comunic.client.data.conversations;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.communiquons.android.comunic.client.data.DatabaseContract.ConversationsListSchema;
import org.communiquons.android.comunic.client.data.DatabaseHelper;

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
    boolean update_list(ArrayList<ConversationsInfo> list){

        //Remove any old list of conversations
        delete_all();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //Process the list of conversation
        boolean success = true;
        for(ConversationsInfo info : list){
            if(!insert(db, info))
                success = false;
        }

        db.close();

        return success;
    }

    /**
     * Delete all the list of conversations
     */
    private void delete_all(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //Prepare the request
        db.delete(TABLE_NAME, null, null);

        db.close();
    }

    /**
     * Insert a new conversation in the database
     * @param db Pointer on active database
     * @param info Informations about the conversation to insert
     * @return TRUE for a success / False else
     */
    private boolean insert(@NonNull SQLiteDatabase db, @NonNull ConversationsInfo info){

        ContentValues values = getContentValues(info);

        return -1 == db.insert(TABLE_NAME, null, values);

    }

    /**
     * Get the contentvalues corresponding to a conversation
     *
     * @param info Information about a conversation
     * @return The values of the conservation
     */
    private ContentValues getContentValues(ConversationsInfo info){
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
}
