package org.communiquons.android.comunic.client.data.UsersInfo;

import android.content.Context;

import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIRequestTask;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class handles informations requests about user informations
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/5/17.
 */

public class GetUsersInfos {

    /**
     * User informations database helper
     */
    private UsersInfosDbHelper udbHelper = null;

    /**
     * Operations context
     */
    private Context context;

    /**
     * Public constructor
     *
     * @param context The context of the application
     * @param dbHelper Database helper object
     */
    public GetUsersInfos(Context context, DatabaseHelper dbHelper){

        //Save context
        this.context = context;

        //Save database helper object
        this.udbHelper = new UsersInfosDbHelper(dbHelper);

    }

    /**
     * This interface must be implemented to perform an API request
     */
    public interface getUserInfosCallback{

        /**
         * Callback function called when we got informations about user
         *
         * @param info Information about the user
         */
        void callback(UserInfo info);

    }

    /**
     * Get and return informations about a user
     *
     * @param id The ID of the user to get the informations
     * @param callback What to do once we got the response
     */
    public void get(int id, getUserInfosCallback callback){

        //Check if the ID is positive, error else
        if(id < 1){
            callback.callback(null); //This is an error
        }

        //Check if the user is already present in the database or not
        if(!udbHelper.exists(id))
            //Perform a request on the server
            getOnServer(id, callback);

            //Else we can retrieve user informations from the local database
        else
            callback.callback(udbHelper.get(id));

    }

    /**
     * Get and return the informations about a user on the server
     *
     * @param id The ID of the user to get informations from
     * @param callback What to do once the request is done
     */
    private void getOnServer(final int id, final getUserInfosCallback callback){

        //Perform a request on the API server
        //Setup the request
        APIRequestParameters requestParameters = new APIRequestParameters(context, "user/getInfos");
        requestParameters.addParameter("userID", ""+id);

        //Do it.
        new APIRequestTask(){

            @Override
            protected void onPostExecute(APIResponse result) {

                UserInfo userInfos = null;

                try {
                    if(result != null) {

                        //Try to extract user informations
                        JSONObject userObjectContainer = result.getJSONObject();

                        if (userObjectContainer != null) {

                            //Extract user object
                            JSONObject userObject = userObjectContainer.getJSONObject("" + id);

                            //Continue only if we could extract required informations
                            if (userObject != null) {
                                //Parse user informations
                                userInfos = parse_user_json(userObject);
                            }

                            //Save user information in the local database in case of success
                            if (userInfos != null)
                                udbHelper.insertOrUpdate(userInfos);
                        }

                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }

                //Go to the next function
                callback.callback(userInfos);

            }

        }.execute(requestParameters);

    }

    /**
     * Parse a JSON object into a user object
     *
     * @param userObject Informations about the user in an object
     * @return User object in case of success, null else
     */
    private UserInfo parse_user_json(JSONObject userObject){

        //Check if the JSON object passed is null or not
        if(userObject == null)
            return null; //Failure

        UserInfo userInfos = new UserInfo();

        //Try to retrieve user informations
        try {

            //Retrieve all user informations
            userInfos.setId(userObject.getInt("userID"));
            userInfos.setFirstName(userObject.getString("firstName"));
            userInfos.setLastName(userObject.getString("lastName"));
            userInfos.setAccountImageURL(userObject.getString("accountImage"));

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        //Return result
        return userInfos;

    }

}
