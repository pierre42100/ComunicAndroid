package org.communiquons.android.comunic.client.data.UsersInfo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIRequestTask;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * This class handles informations requests about user informations
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/5/17.
 */

public abstract class GetUsersInfos extends AsyncTask<Void, Void, UserInfo> {

    /**
     * User informations database helper
     */
    private UsersInfosDbHelper udbHelper = null;

    /**
     * Operations context
     */
    private Context context;

    /**
     * UserID to retrieve
     */
    private int id;

    /**
     * Public constructor
     *
     * @param id The ID of the user to get the information about
     * @param context The context of the application
     * @param dbHelper Database helper object
     */
    public GetUsersInfos(int id, Context context, DatabaseHelper dbHelper){

        //Save user ID
        this.id = id;

        //Save context
        this.context = context;

        //Save database helper object
        this.udbHelper = new UsersInfosDbHelper(dbHelper);

    }

    /**
     * Each script must implement specifically what will be done once the request is done
     *
     * @param info Informations about the user / null in case of failure
     */
    @Override
    abstract protected void onPostExecute(UserInfo info);

    /**
     * Get and return information about a user
     */
    @Override
    protected UserInfo doInBackground(Void... params) {

        //Check if the ID is positive, error else
        if(id < 1)
            return null;

        //Check if the user is already present in the database or not
        if(!udbHelper.exists(id))
            //Perform a request on the server
            return getOnServer(id);
        else
            //Return the cached values about the user
            return udbHelper.get(id);
    }

    /**
     * Get and return the informations about a user on the server
     *
     * @param id The ID of the user to get informations from
     */
    private UserInfo getOnServer(int id){

        //Perform a request on the API server
        //Setup the request
        APIRequestParameters requestParameters = new APIRequestParameters(context, "user/getInfos");
        requestParameters.addParameter("userID", ""+id);

        //Do it.
        APIRequestTask req = new APIRequestTask(){

            @Override
            protected void onPostExecute(APIResponse result) {
                //Nothing
            }

        };
        req.execute(requestParameters);

        //Get the result and process it when it becomes available
        try {
            APIResponse result = req.get();
            UserInfo userInfos = null;
            Log.v("GetUsersInfos", "test2 test test");

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

            return userInfos;

        } catch (Exception e){
            e.printStackTrace();

            //Failure
            return null;
        }
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
