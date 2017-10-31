package org.communiquons.android.comunic.client;

import java.util.ArrayList;

/**
 * This class handles the setup of the parametres required to setup an API request
 *
 * Created by pierre on 10/31/17.
 */

class APIParameters {

    /**
     * Parameters of the request
     */
    private ArrayList<APIPostData> parameters;

    /**
     * The URI on the server
     */
    private String request_uri;

    /**
     * The class constructor
     *
     * @param uri The request URI on the server
     */
    APIParameters(String uri){
        //Save request URI
        request_uri = uri;

        //Intializate parametres array
        parameters = new ArrayList<>();
    }

    /**
     * Add a new parameter to the request
     *
     * @param name The name of the new key
     * @param value The value of the new key
     */
    void addParameter(String name, String value){
        parameters.add(new APIPostData(name, value));
    }

}
