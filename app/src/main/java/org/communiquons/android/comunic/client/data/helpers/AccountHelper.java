package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.util.Log;

import org.communiquons.android.comunic.client.data.enums.CreateAccountResult;
import org.communiquons.android.comunic.client.data.enums.LoginResult;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.NewAccount;
import org.communiquons.android.comunic.client.data.utils.FilesUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Comunic account helper class
 *
 * This class stores the account tokens
 *
 * @author Pierre HUBERT
 * Created by pierre on 10/29/17.
 */

public class AccountHelper extends BaseHelper {

    /**
     * Files utilities object
     */
    private FilesUtils mFilesUtils;

    /**
     * Tokens list
     */
    private ArrayList<String> tokens;

    /**
     * Tokens file
     */
    private static final String tokFilename = "login_tokens.json";

    /**
     * The name of the userID file
     */
    private static final String USER_ID_FILENAME = "user_id.txt";

    /**
     * Account class constructor
     *
     * @param context Context of the application
     */
    public AccountHelper(Context context){
        super(context);

        mFilesUtils = new FilesUtils(context);

        //Initialize tokens array
        tokens = new ArrayList<>();
    }

    /**
     * Intend to sign in user
     *
     * @param email Email address of the user
     * @param password The password of the user
     * @return The result of the operation
     */
    public LoginResult sign_in(String email, String password){

        APIRequest request = new APIRequest(getContext(), "account/login");
        request.setTryContinueOnError(true);
        request.addString("userMail", email);
        request.addString("userPassword", password);

        try {

            APIResponse response = new APIRequestHelper().exec(request);

            //Check for login errors
            if(response.getResponse_code() != 200){

                return response.getResponse_code() == 429 ?
                        LoginResult.TOO_MANY_ATTEMPTS :
                        LoginResult.INVALID_CREDENTIALS;

            }

            //Get login tokens
            JSONObject tokensObj = response.getJSONObject().getJSONObject("tokens");
            ArrayList<String> tokens = new ArrayList<>();
            tokens.add(tokensObj.getString("token1"));
            tokens.add(tokensObj.getString("token2"));

            //Try to save new tokens
            if(!save_new_tokens(tokens))
                return LoginResult.SERVER_ERROR;

            //Get user ID
            if(fetch_current_user_id() < 1)
                return LoginResult.SERVER_ERROR;

            //Success
            return LoginResult.SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            return LoginResult.SERVER_ERROR;
        }
    }

    /**
     * Determine whether user is signed in or not
     *
     * @return True if signed in
     */
    public boolean signed_in() {
        //Check if tokens are already loaded
        return tokens.size() >= 1 || load_tokens();
    }

    /**
     * Sign out user
     * @return True in case of success / false else
     */
    public boolean sign_out(){
        return remove_login_tokens();
    }

    /**
     * Fetch on the server the current user ID
     *
     * @return Current user ID / -1 in case of failure
     */
    private int fetch_current_user_id(){
        APIRequest request = new APIRequest(getContext(), "user/getCurrentUserID");

        try {
            APIResponse response = new APIRequestHelper().exec(request);
            if(response.getResponse_code() != 200)
                return -1;

            int userID = response.getJSONObject().getInt("userID");
            return save_new_user_id(userID) ? userID : -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get the current user ID
     *
     * @return The user ID or -1 in case of error
     */
    public int get_current_user_id(){

        //Get file content
        String userIDString = mFilesUtils.file_get_content(USER_ID_FILENAME);

        //Convert into an int
        try {
            int userID = Integer.decode(userIDString);

            //Return user ID
            return userID > 0 ? userID : -1;
        } catch (NumberFormatException e){
            e.printStackTrace();

            //This is a failure
            return -1;
        }
    }

    /**
     * Save new user ID
     *
     * @param id The new ID to save
     * @return True in case of success / false else
     */
    private boolean save_new_user_id(int id){
        //Save new file content
        return mFilesUtils.file_put_contents(USER_ID_FILENAME, ""+id);
    }

    /**
     * Create a new account
     *
     * @param newAccount Information about the new account to create
     * @return TRUE for a success / FALSE else
     */
    public CreateAccountResult createAccount(NewAccount newAccount) {

        APIRequest request = new APIRequest(getContext(), "account/create");
        request.setTryContinueOnError(true);
        request.addString("firstName", newAccount.getFirstName());
        request.addString("lastName", newAccount.getLastName());
        request.addString("emailAddress", newAccount.getEmail());
        request.addString("password", newAccount.getPassword());

        //Perform the request
        try {
            APIResponse response = new APIRequestHelper().exec(request);

            switch (response.getResponse_code()) {
                case 200:
                    return CreateAccountResult.SUCCESS;

                case 409:
                    return CreateAccountResult.ERROR_EXISTING_EMAIL;

                case 429:
                    return CreateAccountResult.ERROR_TOO_MANY_REQUESTS;

                default:
                    return CreateAccountResult.ERROR;

            }

        } catch (Exception e) {
            e.printStackTrace();
            return CreateAccountResult.ERROR;
        }
    }

    /**
     * Try to load tokens in tokens array
     *
     * @return False in case of failure
     */
    private boolean load_tokens(){

        String tokens_file;

        //Get the tokens file content
        tokens_file = mFilesUtils.file_get_content(tokFilename);

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
    private boolean save_new_tokens(ArrayList<String> toks){

        //Create tokens array
        JSONArray tokens = new JSONArray();

        //Populate tokens array with new tokens
        for(int i = 0; i < toks.size(); i++){
            tokens.put(toks.get(i));
        }

        //Convert JSON array into a string
        String tokens_string = tokens.toString();

        //Try to write result to file
        if(!mFilesUtils.file_put_contents(tokFilename, tokens_string)){
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
        if(!mFilesUtils.file_put_contents(tokFilename, "[]"))
            return false;

        //Create new array
        tokens = new ArrayList<>();

        //Success
        return true;
    }


}
