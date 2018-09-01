package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Files utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/14/18.
 */

public class FilesUtils {

    /**
     * Debug tag
     */
    private static final String TAG = FilesUtils.class.getCanonicalName();

    /**
     * Application context
     */
    private Context mContext;

    /**
     * Account class constructor
     *
     * @param context Context of the application
     */
    public FilesUtils(Context context){
        mContext = context.getApplicationContext();
    }

    /**
     * Temporary directory
     */
    private final static String TEMP_DIRECTORY = "temp";

    /**
     * Create and return a temporary file
     *
     * @param context The context of the application
     * @return Generated file / FALSE in case failure
     */
    @Nullable
    public static File getTempFile(Context context){

        //Get cache directory
        File cacheDir = context.getCacheDir();

        File target;
        do {

            //Generate target file
            target = new File(cacheDir,
                    TEMP_DIRECTORY + File.pathSeparator + StringsUtils.random(10));

        } while (target.exists());

        //Try to create the file
        try {
            return target.createNewFile() ? target : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Read the content of an input stream into a file
     *
     * @param is Source input stream
     * @param file Target file
     * @return TRUE in case of sucess / FALSE else
     */
    public static boolean InputStreamToFile(InputStream is, File file){

        try {
            FileOutputStream os = new FileOutputStream(file, false);
            StreamsUtils.InputToOutputStream(is, os);
            os.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Get the content of a file
     *
     * @param filename the name of the file to get
     * @return The content of the file
     */
    public String file_get_content(String filename){

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
            Log.e(TAG, "Couldn't get file content !");
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
    public boolean file_put_contents(String filename, String content){

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
