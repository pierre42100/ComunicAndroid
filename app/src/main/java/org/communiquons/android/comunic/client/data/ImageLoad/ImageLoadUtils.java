package org.communiquons.android.comunic.client.data.ImageLoad;

import org.communiquons.android.comunic.client.data.Utilities;

/**
 * Image loading utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/18/17.
 */

class ImageLoadUtils {

    /**
     * The main folder in the cache directory that stores the file
     */
    static final String IMAGE_CACHE_DIRECTORY = "img_cache/";

    /**
     * Get the file name, based on the URL name
     *
     * @param url The URL of the file
     * @return The name of the file, composed of characters that can be used in filename
     */
    static String get_file_name(String url){
        return Utilities.sha1(url);
    }

}
