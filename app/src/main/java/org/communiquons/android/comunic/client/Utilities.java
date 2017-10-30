package org.communiquons.android.comunic.client;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Application utilities
 *
 * Created by pierre on 10/29/17.
 */

class Utilities {

    /**
     * Application context
     */
    private Context mContext;

    /**
     * Account class constructor
     *
     * @param context Context of the application
     */
    Utilities(Context context){
        mContext = context;
    }

    /**
     * Get the content of a file
     *
     * @param filename the name of the file to get
     * @return The content of the file
     */
    String file_get_content(String filename){

        FileInputStream fileInputStream;

        try {
            fileInputStream = mContext.openFileInput(filename);

            String result = "";
            int currByte = 0;
            while(currByte != -1){

                currByte = fileInputStream.read();

                if(currByte != -1)
                    result += (char) currByte;
            }

            fileInputStream.close();

            return result;
        }
        catch (Exception e){

            //Print error stack
            Log.e("Utilities", "Couldn't get file content !");
            e.printStackTrace();

            return ""; //This is a failure
        }


    }

    /**
     * Write something to a file
     *
     * @param filename The name of the file to write
     * @param content The new content of the file
     * @return FALSE in case of failure
     */
    boolean file_put_contents(String filename, String content){

        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (Exception e){
            e.printStackTrace();

            return false;
        }

        //Success
        return true;
    }
}
