package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.util.Log;

import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.NewAccount;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Comunic account class
 *
 * This class stores the account tokens
 *
 * @author Pierre HUBERT
 * Created by pierre on 10/29/17.
 */

public class AccountHelper {

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
    public AccountHelper(Context context){
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
    public boolean signed_in(){

        //Check if tokens are already loaded
        if(tokens.size() < 1){
            if(!load_tokens())
                return false;
        }

        return true;
    }

    /**
     * Sign out user
     * @return True in case of success / false else
     */
    public boolean sign_out(){
        return remove_login_tokens();
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
     * Get and return login tokens
     *
     * @return The list of tokens / null if user isn't signed in
     */
    public ArrayList<String> getLoginTokens(){

        //Check if tokens are already loaded or not
        if(tokens.size()< 1)
            load_tokens();

        //Check if there is still no tokens
        if(tokens.size() < 1)
            return null; //Nothing to be done

        //Return tokens list
        return tokens;
    }

    /**
     * Save new tokens in tokens file
     *
     * @param toks The array containing the tokens
     * @return False in case of failure
     */
    public boolean save_new_tokens(ArrayList<String> toks){

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

    /**
     * Create a new account
     *
     * @param newAccount Information about the new account to create
     * @return TRUE for a success / FALSE else
     */
    public boolean createAccount(NewAccount newAccount) {

        APIRequest request = new APIRequest(mContext, "account/create");
        request.addString("firstName", newAccount.getFirstName());
        request.addString("lastName", newAccount.getLastName());
        request.addString("emailAddress", newAccount.getEmail());
        request.addString("password", newAccount.getPassword());

        //Perform the request
        try {
            APIResponse response = new APIRequestHelper().exec(request);
            return response.getResponse_code() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
