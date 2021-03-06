package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Image loading utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/18/17.
 */

public class ImageLoadUtils {

    /**
     * Debug tag
     */
    private static final String TAG = "ImageLoadUtils";

    /**
     * The main folder in the cache directory that stores the file
     */
    private static final String IMAGE_CACHE_DIRECTORY = "img_cache/";

    /**
     * Get the file name, based on the URL name
     *
     * @param url The URL of the file
     * @return The name of the file, composed of characters that can be used in filename
     */
    private static String get_file_name(String url){
        return StringsUtils.sha1(url);
    }

    /**
     * Get the File object corresponding to an image URL
     *
     * Warning ! In some cases, the file might not exists
     *
     * @param context Application context
     * @param url URL of the target image
     * @return File object pointing on the image
     */
    public static File getFileForImage(Context context, String url){

        //Determine the filename for the requested URL
        String filename = IMAGE_CACHE_DIRECTORY + ImageLoadUtils.get_file_name(url);

        //Create file object
        return new File(context.getCacheDir(), filename);
    }

    /**
     * Create cache images files parent directory if it does not exist
     *
     * @param context Context of execution
     * @return True in case of success
     */
    public static boolean create_parent_directory(Context context){
        File parent = new File(context.getCacheDir(), IMAGE_CACHE_DIRECTORY);

        //Check if parent directory already exists
        if(parent.exists())
            return true;


        //Try to create directories
        boolean success = parent.mkdirs();

        //Return error if required
        if(!success)
            Log.e(TAG, "Couldn't create cache parent directory !");

        return success;
    }

}
