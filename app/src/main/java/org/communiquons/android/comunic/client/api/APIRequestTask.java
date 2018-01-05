package org.communiquons.android.comunic.client.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.communiquons.android.comunic.client.data.Account.Account;
import org.communiquons.android.comunic.client.BuildConfig;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
public abstract class APIRequestTask extends AsyncTask<APIRequestParameters, Void, APIResponse> {

    /**
     * API request in a Background task
     *
     * Warning: This method support only one request per object
     *
     * @param params Parameters required to perform the API request
     * @return JSONObject The result of the request
     */
    @Override
    protected APIResponse doInBackground(APIRequestParameters... params) {

        try {
            //Perform the API request
            APIRequest req = new APIRequest();
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
