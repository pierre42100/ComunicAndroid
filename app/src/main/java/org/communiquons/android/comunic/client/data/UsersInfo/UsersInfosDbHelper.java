package org.communiquons.android.comunic.client.data.UsersInfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.communiquons.android.comunic.client.data.DatabaseContract.UsersInfoSchema;
import org.communiquons.android.comunic.client.data.DatabaseHelper;

/**
 * Users information helpers
 *
 * Makes the interface between the UsersInfo object and the SQLite object
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/2/17.
 */

class UsersInfosDbHelper {

    /**
     * Database helper
     */
    private DatabaseHelper dbHelper;

    /**
     * Class constructor
     *
     * @param dbHelper Database helper object
     */
    UsersInfosDbHelper(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    /**
     * Insert or update a user entry
     * * Insertion if the user isn't already present in the database
     * * Update if the user is already present in the database
     *
     * @param userInfo The user insert or update
     * @return True for a success / false else
     */
    boolean insertOrUpdate(UserInfo userInfo){

        //Check if the user is already present in the database or not
        if(!exists(userInfo.getId()))
            return insert(userInfo) > 0;
        else
            return update(userInfo);

    }

    /**
     * Check wether a user is present in the database or not
     *
     * @param userID The user to research on the database
     * @return boolean True if the user exists / false else
     */
    boolean exists(int userID){

        //Get the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Define the condition of the research
        String projection[] = {
                UsersInfoSchema._ID
        };

        String selection = UsersInfoSchema.COLUMN_NAME_USER_ID + " = ?";
        String[] selectionArgs = {""+userID};

        String sortOrder = UsersInfoSchema._ID;

        //Perform the request on the database
        Cursor c = db.query(
                UsersInfoSchema.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        int number_entries = c.getCount();

        //Close cursor
        c.close();

        //Close the database
        db.close();

        return number_entries > 0;
    }

    /**
     * Insert a user in the database
     *
     * @param user The user to insert in the database
     * @return -1 in case of failure / the id of the new entry else
     */
    private int insert(UserInfo user){

        //Get a database with write access
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Prepare the insertion
        ContentValues newValues = new ContentValues();
        newValues.put(UsersInfoSchema.COLUMN_NAME_USER_ID, user.getId());
        newValues.put(UsersInfoSchema.COLUMN_NAME_USER_FIRSTNAME, user.getFirstName());
        newValues.put(UsersInfoSchema.COLUMN_NAME_USER_LASTNAME, user.getLastName());
        newValues.put(UsersInfoSchema.COLUMN_NAME_USER_ACCOUNT_IMAGE, user.getAcountImageURL());

        //Insert it
        long newRowId = db.insert(UsersInfoSchema.TABLE_NAME, null, newValues);

        //Close the database
        db.close();

        return (int) newRowId;
    }

    /**
     * Get a user from the database
     *
     * @param userID The ID of the user to retrieve
     * @return UserInfo about requested user / null in case of filaure
     */
    public UserInfo get(int userID){

        UserInfo result;

        //Open database for access
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Prepare the request on the database
        String[] requestedFields = {
                UsersInfoSchema.COLUMN_NAME_USER_ID,
                UsersInfoSchema.COLUMN_NAME_USER_FIRSTNAME,
                UsersInfoSchema.COLUMN_NAME_USER_LASTNAME,
                UsersInfoSchema.COLUMN_NAME_USER_ACCOUNT_IMAGE
        };

        //Set the conditions of the request
        String selection = UsersInfoSchema.COLUMN_NAME_USER_ID + " = ?";
        String[] selectionArgs = { ""+userID };

        //Sort order
        String sortOrder = UsersInfoSchema._ID;

        //Perform the request
        Cursor c = db.query(
                UsersInfoSchema.TABLE_NAME,
                requestedFields,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        //Check for errors
        if(c.getCount() < 1)
            result = null;
        else {

            //Initialize User object
            result = new UserInfo();

            c.moveToFirst();

            //Extract the information and record them
            result.setId(c.getInt(c.getColumnIndexOrThrow(
                                UsersInfoSchema.COLUMN_NAME_USER_ID)));

            result.setFirstName(c.getString(c.getColumnIndexOrThrow(
                    UsersInfoSchema.COLUMN_NAME_USER_FIRSTNAME)));

            result.setLastName(c.getString(c.getColumnIndexOrThrow(
                    UsersInfoSchema.COLUMN_NAME_USER_LASTNAME)));

            result.setAccountImageURL(c.getString(c.getColumnIndexOrThrow(
                    UsersInfoSchema.COLUMN_NAME_USER_ACCOUNT_IMAGE)));
        }

        //Close the cursor
        c.close();

        //Close the database
        db.close();

        return result;
    }

    /**
     * Remove a user from the database (if he is present)
     *
     * @param userID The ID of the user to delete
     * @return False if nothing was deleted
     */
    boolean delete(int userID){

        //Get write access to the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Prepare the request
        String condition = UsersInfoSchema.COLUMN_NAME_USER_ID + " = ?";
        String[] conditionArgs = {""+userID};

        //Perform the request
        int result = db.delete(UsersInfoSchema.TABLE_NAME, condition, conditionArgs);

        //Close database
        db.close();

        return result > 0;
    }

    /**
     * Update a user entry
     *
     * @param userInfo New informations about the user
     * @return True if the operation seems to be a success / false else
     */
    private boolean update(UserInfo userInfo){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Prepare the request
        //Set the new values
        ContentValues newValues = new ContentValues();
        newValues.put(UsersInfoSchema.COLUMN_NAME_USER_FIRSTNAME, userInfo.getFirstName());
        newValues.put(UsersInfoSchema.COLUMN_NAME_USER_LASTNAME, userInfo.getLastName());
        newValues.put(UsersInfoSchema.COLUMN_NAME_USER_ACCOUNT_IMAGE, userInfo.getAcountImageURL());

        //Set the condition
        String conditions = UsersInfoSchema.COLUMN_NAME_USER_ID + " = ?";
        String[] conditionArgs = {
                ""+userInfo.getId()
        };


        //Perform the request
        int result = db.update(UsersInfoSchema.TABLE_NAME, newValues, conditions, conditionArgs);

        db.close();

        return result > 0;
    }
}
