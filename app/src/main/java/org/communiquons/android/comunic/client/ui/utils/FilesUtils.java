package org.communiquons.android.comunic.client.ui.utils;

import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;

import static org.communiquons.android.comunic.client.ui.Constants.EXTERNAL_STORAGE.MAIN_DIRECTORY_NAME;

/**
 * Files utilities
 *
 * @author Pierre HUBERT
 */
public class FilesUtils {

    /**
     * Get a {@link File} object for a file present outside of the application directories
     *
     * @param subdirectory Comunic storage subdirectory
     * @param filename The name of the target file
     * @return File pointer / null in case of failure
     */
    @Nullable
    public static File GetExternalStorageFile(String subdirectory, String filename){

        try {
            File container = new File(
                    Environment.getExternalStorageDirectory() + "/" + MAIN_DIRECTORY_NAME,
                    subdirectory);

            if (!container.exists())
                if (!container.mkdirs())
                    return null;

            return new File(container, filename);

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
