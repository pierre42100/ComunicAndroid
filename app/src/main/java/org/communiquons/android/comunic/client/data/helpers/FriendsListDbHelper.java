package org.communiquons.android.comunic.client.data.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.DatabaseContract.FriendsListSchema;
import org.communiquons.android.comunic.client.data.models.Friend;

import java.util.ArrayList;

/**
 * Friends list helper
 *
 * This file makes the interface between the application and the database.
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/18/17.
 */

public class FriendsListDbHelper {

    /**
     * Database helper
     */
    private DatabaseHelper dbHelper;

    /**
     * Public constructor
     *
     * @param dbHelper Database helper, required to etablish the connexion with the database
     */
    public FriendsListDbHelper(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    /**
     * Retrieve the list of friends
     *
     * @return The list of friends of the user or null in case of failure
     */
    @Nullable
    ArrayList<Friend> get_list(){

        //Get read access to the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Prepare the request on the database
        String table_name = FriendsListSchema.TABLE_NAME;
        String[] columns = {
                FriendsListSchema.COLUMN_NAME_FRIEND_ID,
                FriendsListSchema.COLUMN_NAME_FRIEND_ACCEPTED,
                FriendsListSchema.COLUMN_NAME_FRIEND_FOLLOWING,
                FriendsListSchema.COLUMN_NAME_FRIEND_LAST_ACTIVITY
        };
        String order = FriendsListSchema._ID;

        //Perform the request
        Cursor c = db.query(table_name, columns, null, null, null, null, order);

        //Check if the request echoed
        if(c == null)
            return null; //An error occurred

        ArrayList<Friend> friendsList = new ArrayList<>();

        //Process the list of responses
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++){

            //Get information about the friend
            Friend friend = new Friend();

            friend.setId(c.getInt(c.getColumnIndexOrThrow(
                    FriendsListSchema.COLUMN_NAME_FRIEND_ID)));

            friend.setAccepted(c.getInt(c.getColumnIndexOrThrow(
                    FriendsListSchema.COLUMN_NAME_FRIEND_ACCEPTED)) == 1);

            friend.setFollowing(c.getInt(c.getColumnIndexOrThrow(
                    FriendsListSchema.COLUMN_NAME_FRIEND_FOLLOWING)) == 1);

            friend.setLast_activity(c.getInt(c.getColumnIndexOrThrow(
                    FriendsListSchema.COLUMN_NAME_FRIEND_LAST_ACTIVITY)));

            //Add the friend to the list
            friendsList.add(friend);

            //Move to the next friend
            if(!c.moveToNext())
                break;
        }

        //Close cursor
        c.close();

        return friendsList;
    }

    /**
     * Update the entire list of friends
     *
     * @param friendsList The new list of friend to insert in the database
     * @return The result of the operation
     */
    public boolean update_list(@NonNull ArrayList<Friend> friendsList){

        //First, remove all entries from the database
        remove_all();

        //Get writable access to the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Process the list of user
        boolean success = true;
        for(Friend friend : friendsList){
            success = insert_user(db, friend) && success;
        }

        //Close the database
        //db.close();

        return success;
    }


    /**
     * Insert a single user in the database
     *
     * @param db Access to the database
     * @param friend The friend to insert
     * @return True in case of success / false else
     */
    private boolean insert_user(SQLiteDatabase db, Friend friend){

        //Prepare the query
        String table_name = FriendsListSchema.TABLE_NAME;
        String nullColumnHack = null;

        //Set the values
        ContentValues values = createContentValue(friend);

        //Perform the query
        return db.insert(table_name, nullColumnHack, values) > -1;
    }

    /**
     * Remove all the friend entries from the database
     *
     * @return The number of affected rows
     */
    public int remove_all(){

        //Get writable database access
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Prepare the request
        String table_name = FriendsListSchema.TABLE_NAME;
        String whereClause = FriendsListSchema._ID + " > 0";
        String[] whereArgs = new String[0];

        int result = db.delete(table_name, whereClause, whereArgs);

        //Close the database
        //db.close();

        return result;
    }

    /**
     * Remove a friend from the list
     *
     * @param friend The friend to delete
     */
    boolean delete_friend(Friend friend){

        //Get access to the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Prepare the request
        String table_name = FriendsListSchema.TABLE_NAME;
        String whereClause = FriendsListSchema.COLUMN_NAME_FRIEND_ID + " = ?";
        String[] whereValues = {""+friend.getId()};

        int result = db.delete(table_name, whereClause, whereValues);

        //Close access to the database
        //db.close();

        return result > 0;
    }

    /**
     * Update a specified user in the database with specified information
     *
     * @param friend The friend to update on the databse
     * @return The result of the operation
     */
    boolean update_friend(Friend friend){

        //Get access to the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Prepare the request
        String table = FriendsListSchema.TABLE_NAME;
        ContentValues values = createContentValue(friend);
        String whereClause = FriendsListSchema.COLUMN_NAME_FRIEND_ID + " = ?";
        String[] whereArgs = {""+friend.getId()};

        //Perform it
        int result = db.update(table, values, whereClause, whereArgs);

        //Close access to the database
        //db.close();

        return result > 0;
    }

    /**
     * Create a full content value based on user informations in order to make operation on
     * the database easier
     *
     * @param friend The friend which will be turned into a contentvalue
     * @return The generated content value
     */
    private ContentValues createContentValue(Friend friend){
        ContentValues values = new ContentValues();
        values.put(FriendsListSchema.COLUMN_NAME_FRIEND_ID, friend.getId());
        values.put(FriendsListSchema.COLUMN_NAME_FRIEND_ACCEPTED, friend.isAccepted() ? 1 : 0);
        values.put(FriendsListSchema.COLUMN_NAME_FRIEND_FOLLOWING, friend.isFollowing() ? 1 : 0);
        values.put(FriendsListSchema.COLUMN_NAME_FRIEND_LAST_ACTIVITY, friend.getLast_activity());

        return values;
    }
}
