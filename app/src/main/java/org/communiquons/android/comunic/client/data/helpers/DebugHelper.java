package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Debug helper
 *
 * This helpers handles all the debug operations made by the user other the application
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/28/18.
 */

public class DebugHelper extends BaseHelper {

    /**
     * Public constructor
     *
     * @param context The context of the application
     */
    public DebugHelper(Context context) {
        super(context);
    }

    /**
     * Clear the database of the application, setting it default values
     *
     * @return TRUE for a success / FALSE else
     */
    public boolean clearLocalDatabase(){

        try {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.clear_db(db);
            dbHelper.init_db(db);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
