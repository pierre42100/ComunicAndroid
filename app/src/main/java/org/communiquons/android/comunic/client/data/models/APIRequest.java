package org.communiquons.android.comunic.client.data.models;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.APIRequestHelper;

import java.util.ArrayList;

/**
 * This class handles the setup of the parameters required to setup an API request
 *
 * @author Pierre HUBERT
 * Created by pierre on 10/31/17.
 */

public class APIRequest {

    /**
     * The context of the request
     */
    private Context context;

    /**
     * Parameters of the request
     */
    private ArrayList<APIPostData> parameters;

    /**
     * The URI on the server
     */
    private String request_uri;

    /**
     * Set if the connection should be parsed even in case of error
     */
    private boolean tryContinueOnError = false;

    /**
     * The class constructor
     *
     * @param context The context of the request
     * @param uri The request URI on the server
     */
    public APIRequest(Context context, String uri){

        //Save the context
        this.context = context;

        //Save request URI
        request_uri = uri;

        //Initialize parameters array
        parameters = new ArrayList<>();
    }

    /**
     * Add a new parameter to the request
     *
     * @param name The name of the new key
     * @param value The value of the new key
     */
    public void addString(String name, String value){
        parameters.add(new APIPostData(name, value));
    }

    /**
     * Add a new parameter to the request
     *
     * @param name The name of the new key
     * @param value The value of the new key (int)
     */
    public void addInt(String name, int value){
        this.addString(name, ""+value);
    }

    /**
     * Add a new parameter to the request
     *
     * @param name The name of the new key
     * @param value The value of the new key (boolean)
     */
    public void addBoolean(String name, boolean value){
        this.addString(name, value ? "true" : "false");
    }

    /**
     * Retrieve request URI
     *
     * @return The request URI
     */
    public String getRequest_uri() {
        return request_uri;
    }

    /**
     * Return all the request parameters as a string ready to be passed to the request output
     * stream.
     *
     * @return A string
     */
    public String get_parameters_encoded(){

        //Return string
        StringBuilder result = new StringBuilder();

        //Process loop
        for(int i = 0; i < parameters.size(); i++){

            //Make sure to separate parameters
            result.append(i > 0 ? "&" : "");
            result.append(parameters.get(i).get_encoded());
        }

        return result.toString();
    }

    /**
     * Get the list of parameters
     *
     * @return The list of parameters
     */
    public ArrayList<APIPostData> getParameters() {
        return parameters;
    }

    /**
     * Get the context of the request
     *
     * @return The context of the request
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set whether the response should be parsed even in case of error
     *
     * @param tryContinueOnError TRUE to continue / FALSE else
     */
    public void setTryContinueOnError(boolean tryContinueOnError) {
        this.tryContinueOnError = tryContinueOnError;
    }

    /**
     * Check whether the connection should be maintained even in case of error
     *
     * @return TRUE if the request should be maintained / FALSE else
     */
    public boolean isTryContinueOnError() {
        return tryContinueOnError;
    }

    /**
     * Execute the request
     *
     * This is a convenience method
     *
     * @return The result of the operation
     * @throws Exception In case of failure
     */
    public APIResponse exec() throws Exception {
        return new APIRequestHelper().exec(this);
    }
}
