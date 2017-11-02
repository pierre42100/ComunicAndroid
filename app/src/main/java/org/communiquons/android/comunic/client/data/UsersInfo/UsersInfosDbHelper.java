package org.communiquons.android.comunic.client.data.UsersInfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.communiquons.android.comunic.client.data.DatabaseContract.UsersInfoSchema;
import org.communiquons.android.comunic.client.data.DatabaseHelper;

/**
 * Users informations helpers
 *
 * Makes the interface between the UsersInfo object and the SQLite object
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/2/17.
 */

public class UsersInfosDbHelper {

    /**
     * Database helper
     */
    private DatabaseHelper dbHelper;

    /**
     * Class constructor
     *
     * @param dbHelper Database helper object
     */
    public UsersInfosDbHelper(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    /**
     * Check wether a user is present in the database or not
     *
     * @param userID The user to research on the database
     * @return boolean True if the user exists / false else
     */
    public boolean exists(int userID){

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

        return number_entries > 0;
    }
}
