package org.communiquons.signalexchangerclient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Signal exchanger client request
 *
 * @author Pierre HUBERT
 */
class ClientRequest {

    /**
     * Contains request information
     */
    private JSONObject mList;

    /**
     * Initialize object
     */
    ClientRequest(){
        this.mList = new JSONObject();
    }

    /**
     * Add a string to the request
     *
     * @param name The name of the string to add
     * @param value The value of the string to add
     * @return This object to help to concatenate requests
     */
    ClientRequest addString(String name, String value){
        try {
            mList.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not add a string to a JSON object!");
        }

        return this;
    }

    /**
     * Add a boolean to the request
     *
     * @param name The name of the string to add
     * @param value Boolean value
     * @return This object
     */
    ClientRequest addBoolean(String name, boolean value){
        try {
            mList.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Add a JSON object to the request
     *
     * @param name The name of the field to add
     * @param value The object
     * @return This object
     */
    ClientRequest addJSONObject(String name, JSONObject value){
        try {
            mList.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Get resulting JSON object
     *
     * @return Get the resulting JSON object
     */
    JSONObject get(){
        return mList;
    }
}
