package org.communiquons.android.comunic.client.api;

import org.communiquons.android.comunic.client.api.APIPostData;

import java.util.ArrayList;

/**
 * This class handles the setup of the parametres required to setup an API request
 *
 * Created by pierre on 10/31/17.
 */

public class APIRequestParameters {

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
    public APIRequestParameters(String uri){
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
    public void addParameter(String name, String value){
        parameters.add(new APIPostData(name, value));
    }

    /**
     * Retrieve request URI
     *
     * @return The request URI
     */
    String getRequest_uri() {
        return request_uri;
    }

    /**
     * Return all the request parameters as a string ready to be passed to the request output
     * stream.
     *
     * @return A string
     */
    String get_parameters_encoded(){

        //Return string
        String result = "";

        //Process loop
        for(int i = 0; i < parameters.size(); i++){

            //Make sure to separate parameters
            result += (i > 0 ? "&" : "");
            result += parameters.get(i).get_encoded();
        }

        return result;
    }
}
