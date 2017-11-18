package org.communiquons.android.comunic.client.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.communiquons.android.comunic.client.BuildConfig;
import org.communiquons.android.comunic.client.data.Account.Account;
import org.communiquons.android.comunic.client.data.Utilities;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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
 * API request file
 *
 * Perform an API request
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/12/17.
 */

public class APIRequest {

    /**
     * Public constructor
     */
    public APIRequest(){

    }

    /**
     * Perform an API request on the server and return the result as a JSON code
     *
     * @param parameters The parameters to pass to the server
     * @return The result of the request
     */
    public APIResponse exec(APIRequestParameters parameters) throws Exception{

        //Add login tokens
        addLoginTokens(parameters);

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
            String response = readIt(is);
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
    private String readIt(InputStream stream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder out = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            out.append(line);
        }
        reader.close();

        return out.toString();

    }

    /**
     * Add the login tokens to an API request object
     *
     * @param params The parameters of the request to update
     */
    private void addLoginTokens(APIRequestParameters params){

        //Create account object
        Account account = new Account(params.getContext());

        //Check if user is signed in or not
        if(!account.signed_in())
            return; //Do nothing

        //Get login tokens
        ArrayList<String> tokens = account.getLoginTokens();

        if(tokens.size() < 2)
            return; //Not enough tokens

        //Add them to the request
        params.addParameter("userToken1", tokens.get(0));
        params.addParameter("userToken2", tokens.get(1));

    }

    /**
     * Determine it is possible or not to connect to the API now
     *
     * @param context The context used to perform the test
     * @return True if a network connection is available
     */
    public static boolean isAPIavailable(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}