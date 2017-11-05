package org.communiquons.android.comunic.client.data.UsersInfo;

import android.content.Context;

import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIRequestTask;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.DatabaseHelper;

/**
 * This class handles informations requests about user informations
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/5/17.
 */

public class GetUsersInfos {

    /**
     * Database helper
     */
    private DatabaseHelper dbHelper = null;

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
        this.dbHelper = dbHelper;

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
     * Get and return the informations about a user
     *
     * @param id The ID of the user to get informations from
     */
    public void get(int id, getUserInfosCallback callback){

        //Perform a request on the API server

        //Setup the request
        APIRequestParameters requestParameters = new APIRequestParameters(context, "user/getInfos");
        requestParameters.addParameter("userID", ""+id);

        //Do it.
        new APIRequestTask(){

            @Override
            protected void onPostExecute(APIResponse result) {



            }

        }.execute(requestParameters);

    }

}
