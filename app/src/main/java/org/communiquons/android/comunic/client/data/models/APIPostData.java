package org.communiquons.android.comunic.client.data.models;

import java.net.URLEncoder;

/**
 * This class stores on parameter for a POST request
 *
 * @author Pierre HUBERT
 * Created by pierre on 10/31/17.
 */

public class APIPostData {

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
     * Get the name of the key
     *
     * @return The name of key
     */
    public String getKey_name() {
        return key_name;
    }

    /**
     * Get the value associated with the ky
     *
     * @return The value of the key
     */
    public String getKey_value() {
        return key_value;
    }

    /**
     * Get the key name, as an encoded string
     *
     * @return The encoded key name
     */
    public String getEncodedKeyName(){
        try {
            return URLEncoder.encode(getKey_name(), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e){
            e.printStackTrace();
            throw new RuntimeException("Unsupported encoding : UTF-8 !", e);
        }
    }

    /**
     * Get the key value, as an encoded string
     *
     * @return The encoded key value
     */
    public String getEncodedKeyValue(){
        try {
            return URLEncoder.encode(getKey_value(), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e){
            e.printStackTrace();
            throw new RuntimeException("Unsupported encoding : UTF-8 !", e);
        }
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
        return encoded_key + "=" + encoded_value;
    }

}
