package org.communiquons.android.comunic.client;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Comunic account class
 *
 * Created by pierre on 10/29/17.
 */

class Account {

    /**
     * Utilities object
     */
    private Utilities utils;

    /**
     * Tokens list
     */
    private ArrayList<String> tokens;

    /**
     * Tokens file
     */
    private String tokFilename = "login_tokens.json";

    /**
     * Application context
     */
    private Context mContext;

    /**
     * Account class constructor
     *
     * @param context Context of the application
     */
    Account(Context context){
        mContext = context;
        utils = new Utilities(context);

        //Initialize tokens array
        tokens = new ArrayList<>();
    }

    /**
     * Determine whether user is signed in or not
     *
     * @return True if signed in
     */
    boolean signed_in(){

        //Check if tokens are already loaded
        if(tokens.size() < 1){
            if(!load_tokens())
                return false;
        }

        return true;
    }

    /**
     * Try to load tokens in tokens array
     *
     * @return False in case of failure
     */
    private boolean load_tokens(){

        String tokens_file;

        //Get the tokens file content
        tokens_file = utils.file_get_content(tokFilename);

        //Check if there was an error
        if(Objects.equals(tokens_file, ""))
            return false;

        //Try to parse tokens file
        try {
            JSONArray tokens_array = new JSONArray(tokens_file);

            //Process login tokens
            if(tokens_array.length() == 0)
                return false; //No tokens to process

            for (int i = 0; i < tokens_array.length(); i++) {
                tokens.add(tokens_array.getString(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        //Success
        return true;
    }

    /**
     * Save new tokens in tokens file
     *
     * @param toks The array containing the tokens
     * @return False in case of failure
     */
    boolean save_new_tokens(ArrayList<String> toks){

        //Create tokens array
        JSONArray tokens = new JSONArray();

        //Populate tokens array with new tokens
        for(int i = 0; i < toks.size(); i++){
            tokens.put(toks.get(i));
        }

        //Convert JSON array into a string
        String tokens_string = tokens.toString();

        //Try to write result to file
        if(!utils.file_put_contents(tokFilename, tokens_string)){
            Log.e("Account", "Couldn't save new tokens !");
            return false;
        }

        return true;

    }

    /**
     * Remove login tokens from device
     * @return False in case of failure
     */
    private boolean remove_login_tokens(){
        if(!utils.file_put_contents(tokFilename, "[]"))
            return false;

        //Create new array
        tokens = new ArrayList<>();

        //Success
        return true;
    }
}
