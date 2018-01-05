package org.communiquons.android.comunic.client.data.Account;

import android.content.Context;

import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIRequestTask;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.json.JSONObject;

/**
 * Account utilities functions
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/5/17.
 */

public class AccountUtils {

    /**
     * Execution context
     */
    private Context mContext;

    /**
     * Utilities object
     */
    private Utilities utils;

    /**
     * The name of the userID file
     */
    private static final String USER_ID_FILENAME = "user_id.txt";

    /**
     * The constructor of the object
     *
     * @param context The constructor of the application
     */
    public AccountUtils(Context context){

        //Save context
        mContext = context;

        //Create utilities object
        utils = new Utilities(context);

    }

    /**
     * This interface has to be implemented and passed as an argument of refresh_current_user_id()
     */
    public interface onceRefreshedUserID{
        /**
         * Callback function
         *
         * @param success True in case of success / false else
         */
        void callback(boolean success);
    }

    /**
     * Refresh current user ID
     *
     * @param callback What to do once userID is refreshed
     */
    public void refresh_current_user_id(final onceRefreshedUserID callback){

        //Perform an API request
        APIRequestParameters params = new APIRequestParameters(mContext, "user/getCurrentUserID");
        new APIRequestTask(){
            @Override
            protected void onPostExecute(APIResponse result) {

                //Remove old user ID
                save_new_user_id(-1);

                JSONObject response = result.getJSONObject();

                //Check for errors
                if(response == null)
                    callback.callback(false);


                //Try to extract and save user ID
                try {
                    assert response != null;
                    int userID = response.getInt("userID");
                    callback.callback(
                            save_new_user_id(userID)); //The success of the operation depends of the
                                                        //ability to save it too.

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }.execute(params);

    }

    /**
     * Save new user ID
     *
     * @param id The new ID to save
     * @return True in case of success / false else
     */
    private boolean save_new_user_id(int id){

        //Save new file content
        return utils.file_put_contents(USER_ID_FILENAME, ""+id);

    }

    /**
     * Get the current user ID
     *
     * @return The user ID or -1 in case of error
     */
    public int get_current_user_id(){

        //Get file content
        String userIDstring = utils.file_get_content(USER_ID_FILENAME);

        //Convert into an int
        try {
            int userIDid = Integer.decode(userIDstring);

            //Return user ID
            return userIDid > 0 ? userIDid : -1;
        } catch (NumberFormatException e){
            e.printStackTrace();

            //This is a failure
            return -1;
        }
    }

    /**
     * Get the current user ID quickly
     *
     * @param context The context of execution of the application
     * @return The ID of the current user or -1 in case of failure
     */
    public static int getID(Context context){
        return new AccountUtils(context).get_current_user_id();
    }

}
