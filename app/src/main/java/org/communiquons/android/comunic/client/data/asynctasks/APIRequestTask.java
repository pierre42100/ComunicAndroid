package org.communiquons.android.comunic.client.data.asynctasks;

import android.os.AsyncTask;

import org.communiquons.android.comunic.client.data.helpers.APIRequestHelper;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;

/**
 * Perform an API request on the server
 *
 * API requests are made on a separate Thread using AsyncTask.
 *
 * The method requesting an API request has to implement the onPostExecute method in order to be
 * able to have a personalized data process
 *
 * @author Pierre HUBERT
 * Created by pierre on 10/31/17.
 */
@Deprecated
public abstract class APIRequestTask extends AsyncTask<APIRequest, Void, APIResponse> {

    /**
     * API request in a Background task
     *
     * Warning: This method support only one request per object
     *
     * @param params Parameters required to perform the API request
     * @return JSONObject The result of the request
     */
    @Override
    protected APIResponse doInBackground(APIRequest... params) {

        try {
            //Perform the API request
            APIRequestHelper req = new APIRequestHelper();
            return req.exec(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * The next action to do once we got a response is implemented by the method that perform
     * the request.
     *
     * @param result The result of the request
     */
    abstract protected void onPostExecute(APIResponse result);
}
