package org.communiquons.android.comunic.client.data.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.communiquons.android.comunic.client.data.DatabaseContract.UsersInfoSchema;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.utils.TimeUtils;

import static org.communiquons.android.comunic.client.data.DatabaseContract.UsersInfoSchema.COLUMN_NAME_TIME_INSERT;
import static org.communiquons.android.comunic.client.data.DatabaseContract.UsersInfoSchema.TABLE_NAME;

/**
 * Users information helpers
 *
 * Makes the interface between the UsersInfo object and the SQLite object
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/2/17.
 */

class UsersInfoDbHelper {

    /**
     * Max age of user information in cache
     */
    private static final int USER_INFO_MAX_AGE = 36000; //10 hours

    /**
     * Database helper
     */
    private DatabaseHelper dbHelper;

    /**
     * Class constructor
     *
     * @param dbHelper Database helper object
     */
    UsersInfoDbHelper(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
        clean();
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
     * Check whether a user is present in the database or not
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
                TABLE_NAME,
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
        ContentValues newValues = UserInfoToDb(user);

        //Insert it
        long newRowId = db.insert(TABLE_NAME, null, newValues);

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
                TABLE_NAME,
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

            //Extract the information and record them
            c.moveToFirst();
            result = DbToUserInfo(c);
        }

        //Close the cursor
        c.close();

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
        int result = db.delete(TABLE_NAME, condition, conditionArgs);

        return result > 0;
    }

    /**
     * Update a user entry
     *
     * @param userInfo New information about the user
     * @return True if the operation seems to be a success / false else
     */
    private boolean update(UserInfo userInfo){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Prepare the request
        //Set the new values
        ContentValues newValues = UserInfoToDb(userInfo);

        //Set the condition
        String conditions = UsersInfoSchema.COLUMN_NAME_USER_ID + " = ?";
        String[] conditionArgs = {
                ""+userInfo.getId()
        };


        //Perform the request
        return db.update(TABLE_NAME, newValues, conditions, conditionArgs) > 0;

    }

    /**
     * Remove old user data
     */
    private void clean(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClauses = COLUMN_NAME_TIME_INSERT + " < ?";
        String[] whereArgs = {(TimeUtils.time() - USER_INFO_MAX_AGE)+""};
        db.delete(TABLE_NAME, whereClauses, whereArgs);
    }

    /**
     * Turn user information object into database entry
     *
     * @param user Information about the user
     * @return Generated database entry
     */
    private ContentValues UserInfoToDb(UserInfo user){
        ContentValues values = new ContentValues();
        values.put(UsersInfoSchema.COLUMN_NAME_USER_ID, user.getId());
        values.put(UsersInfoSchema.COLUMN_NAME_TIME_INSERT, TimeUtils.time());
        values.put(UsersInfoSchema.COLUMN_NAME_USER_FIRSTNAME, user.getFirstName());
        values.put(UsersInfoSchema.COLUMN_NAME_USER_LASTNAME, user.getLastName());
        values.put(UsersInfoSchema.COLUMN_NAME_USER_ACCOUNT_IMAGE, user.getAcountImageURL());
        return values;
    }

    /**
     * Turn a database entry into a UserInfo object
     *
     * @param c The cursor to parse
     * @return Generated user information
     */
    private UserInfo DbToUserInfo(Cursor c){
        UserInfo user = new UserInfo();
        user.setId(c.getInt(c.getColumnIndexOrThrow(UsersInfoSchema.COLUMN_NAME_USER_ID)));
        user.setFirstName(c.getString(c.getColumnIndexOrThrow(
                UsersInfoSchema.COLUMN_NAME_USER_FIRSTNAME)));
        user.setLastName(c.getString(c.getColumnIndexOrThrow(
                UsersInfoSchema.COLUMN_NAME_USER_LASTNAME)));
        user.setAccountImageURL(c.getString(c.getColumnIndexOrThrow(
                UsersInfoSchema.COLUMN_NAME_USER_ACCOUNT_IMAGE)));
        return user;
    }
}
