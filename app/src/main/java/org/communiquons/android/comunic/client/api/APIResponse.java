package org.communiquons.android.comunic.client.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Store and serve the response of an API request
 *
 * @author Pierre HUBERT
 * Created by pierre on 10/31/17.
 */

public class APIResponse {

    /**
     * Reponse code
     */
    private int response_code;

    /**
     * Response string
     */
    private String response = null;

    /**
     * Constructor of the API response
     */
    APIResponse(){}

    /**
     * Set response string
     *
     * @param response The request response
     */
    void setResponse(String response) {
        this.response = response;
    }

    /**
     * Set the response code
     *
     * @param response_code The response code
     */
    void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    /**
     * Get the response code
     *
     * @return The response code of the request
     */
    public int getResponse_code() {
        return response_code;
    }

    /**
     * Get the response as a string
     * @return The response
     */
    public String getResponseString() {
        return response;
    }

    /**
     * Get the response as a JSON object
     *
     * @return The response as JSON object. False in case of failure
     */
    public JSONObject getJSONObject(){

        JSONObject response = null;

        //Try to decode JSON object
        try {
            response = new JSONObject(this.response);
        } catch (JSONException e) {
            //In case of failure
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Get the response as a JSON array
     *
     * Might be required in some case
     *
     * Warning ! Take care : use getJSONArray or getJSONObject in an adapted way to the response
     *
     * @return The response as JSON object. False in case of failure
     */
    public JSONArray getJSONArray(){

        JSONArray response = null;

        //Try to decode JSON object
        try {
            response = new JSONArray(this.response);
        } catch (JSONException e) {
            //In case of failure
            e.printStackTrace();
        }

        return response;
    }
}
