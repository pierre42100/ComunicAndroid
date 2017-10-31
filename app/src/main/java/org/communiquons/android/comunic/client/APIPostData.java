package org.communiquons.android.comunic.client;

import java.net.URLEncoder;

/**
 * This class stores on parameter for a POST request
 *
 * Created by pierre on 10/31/17.
 */

class APIPostData {

    /**
     * The name of the key
     */
    private String key_name;

    /**
     * The value of the key
     */
    private String key_value;

    /**
     * Public constructor of the class
     *
     * @param name The name of the parameter
     * @param value The value of the parameter
     */
    APIPostData(String name, String value){
        key_name = name;
        key_value = value;
    }

    /**
     * Get the the name and the value of the key in an encoded form
     *
     * @return The result
     */
    String get_encoded(){

        String encoded_key;
        String encoded_value;

        try {
            encoded_key = URLEncoder.encode(key_name, "UTF-8");
            encoded_value = URLEncoder.encode(key_value, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e){
            e.printStackTrace();

            return "";
        }

        //Return result
        return encoded_key + "&" + encoded_value;
    }

}
