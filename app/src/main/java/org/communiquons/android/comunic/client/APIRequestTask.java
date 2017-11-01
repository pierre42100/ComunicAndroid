package org.communiquons.android.comunic.client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.json.JSONObject;

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

/**
 * Perform an API request on the server
 *
 * Created by pierre on 10/31/17.
 */
abstract class APIRequestTask extends AsyncTask<APIRequestParameters, Void, APIResponse> {

    /**
     * Background task
     *
     * Warning: This method support only one request per object
     *
     * @param params Parametres required to perform the API request
     * @return JSONObject The result of the request
     */
    @Override
    protected APIResponse doInBackground(APIRequestParameters... params) {

        try {
            //Perform the API request
            return downloadUrl(params[0]);
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

    /**
     * Peform an API request on the server and return the result as a JSON code
     *
     * @param parameters The parameters to pass to the server
     * @return The result of the request
     */
    private APIResponse downloadUrl(APIRequestParameters parameters) throws Exception{

        APIResponse result = new APIResponse();

        InputStream is = null;

        try {

            //Determine the URL of the request
            URL url = new URL(BuildConfig.api_url + parameters.getRequest_uri());

            //The request is being performed on an http server
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Configure the connection
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);



            //Send request parameters
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
            writer.write(parameters.get_parameters_encoded());
            writer.flush();
            writer.close();
            out.close();

            //Connect to the server
            conn.connect();

            //Get response code
            result.setResponse_code(conn.getResponseCode());

            is = conn.getInputStream();
            String response = readIt(is, 5000);
            result.setResponse(response);

            conn.disconnect();

        } finally {
            //Close streams
            if(is != null)
                is.close();
        }
        //Return result
        return result;
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /**
     * Determine it is possible or not to connect to the API now
     *
     * @param context The context used to perform the test
     * @return True if a network connection is available
     */
    static boolean isAPIavailable(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
