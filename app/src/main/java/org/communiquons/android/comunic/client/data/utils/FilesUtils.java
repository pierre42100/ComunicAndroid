package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Files utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/14/18.
 */

public class FilesUtils {

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
            Utilities.InputToOutputStream(is, os);
            os.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
