package org.communiquons.android.comunic.client.data.models;

import android.content.Context;

import java.util.ArrayList;

/**
 * This class handles information about the request that includes files to the server
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/21/18.
 */

public class APIFileRequest extends APIRequest {

    /**
     * The list of post files
     */
    private ArrayList<APIPostFile> files;

    /**
     * The class constructor
     *
     * @param context The context of the request
     * @param uri     The request URI on the server
     */
    public APIFileRequest(Context context, String uri) {
        super(context, uri);

        //Create files list
        files = new ArrayList<>();
    }

    /**
     * Add a file to the request
     *
     * @param file The file to add
     */
    public void addFile(APIPostFile file){
        files.add(file);
    }

    /**
     * Get the list of files
     *
     * @return The list of the files to include into the request
     */
    public ArrayList<APIPostFile> getFiles() {
        return files;
    }
}
