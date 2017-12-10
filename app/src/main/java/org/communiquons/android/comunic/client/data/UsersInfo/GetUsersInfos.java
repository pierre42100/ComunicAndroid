package org.communiquons.android.comunic.client.data.UsersInfo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIRequestTask;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class handles informations requests about user informations
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/5/17.
 */

public class GetUsersInfos {

    /**
     * User information database helper
     */
    private GetUsersHelper uHelper = null;

    /**
     * Public constructor
     *
     * @param context The context of the application
     * @param dbHelper Database helper object
     */
    public GetUsersInfos(Context context, DatabaseHelper dbHelper){

        //Save database helper object
        this.uHelper = new GetUsersHelper(context, dbHelper);

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
     * This interface must be implemented to perform an API request
     */
    public interface getMultipleUserInfosCallback{

        /**
         * Callback function called when we got informations about user
         *
         * @param info Information about the user
         */
        void callback(ArrayMap<Integer, UserInfo> info);
    }

    /**
     * Get and return informations about a user
     *
     * @param id The ID of the user to get the informations
     * @param callback What to do once we got the response
     */
    public void get(int id, final getUserInfosCallback callback){

        //Check if the ID is positive, error else
        if(id < 1){
            callback.callback(null); //This is an error
        }

        new AsyncTask<Integer, Void, UserInfo>(){
            @Override
            protected UserInfo doInBackground(Integer... params) {
                return uHelper.getSingle(params[0], false);
            }

            @Override
            protected void onPostExecute(UserInfo userInfo) {
                callback.callback(userInfo);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);

    }

    /**
     * Get informations about multiple users
     *
     * @param IDs The ID of the user to get
     * @param callback The result once we got all the users
     */
    public void getMultiple(final ArrayList<Integer> IDs, final getMultipleUserInfosCallback callback){

        new AsyncTask<Void, Void, ArrayMap<Integer, UserInfo>>(){
            @Override
            protected ArrayMap<Integer, UserInfo> doInBackground(Void... params) {
                return uHelper.getMultiple(IDs);
            }

            @Override
            protected void onPostExecute(ArrayMap<Integer, UserInfo> integerUserInfoArrayMap) {
                callback.callback(integerUserInfoArrayMap);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



}
