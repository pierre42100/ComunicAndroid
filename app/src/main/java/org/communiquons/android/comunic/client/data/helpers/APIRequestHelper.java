package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.communiquons.android.comunic.client.BuildConfig;
import org.communiquons.android.comunic.client.data.models.APIFileRequest;
import org.communiquons.android.comunic.client.data.models.APIPostData;
import org.communiquons.android.comunic.client.data.models.APIPostFile;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * API request file
 *
 * Perform an API request
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/12/17.
 */

public class APIRequestHelper {

    /**
     * Public constructor
     */
    public APIRequestHelper(){

    }

    /**
     * Perform an API request on the server and return the result as a JSON code
     *
     * @param parameters The parameters to pass to the server
     * @return The result of the request
     */
    public APIResponse exec(APIRequest parameters) throws Exception {

        //Add API and login tokens
        addAPItokens(parameters);
        addLoginTokens(parameters);

        APIResponse result = new APIResponse();

        InputStream is = null;
        HttpURLConnection conn = null;

        try {

            //Determine the URL of the request
            URL url = new URL(BuildConfig.api_url + parameters.getRequest_uri());

            //The request is being performed on an http server
            conn = (HttpURLConnection) url.openConnection();

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

        } catch (Exception e){

            e.printStackTrace();

            //Check for response code
            if(conn != null && parameters.isTryContinueOnError()){
                //Get response code
                result.setResponse_code(conn.getResponseCode());
            }

            //Else we throw an exception
            else
                throw new Exception("An exception occurred while trying to connect to server !");

        } finally {
            //Close streams
            if(is != null)
                is.close();
        }
        //Return result
        return result;
    }

    /**
     * Execute an API request over the server
     *
     * Note : this methods is based on a StackOverflow answer:
     * https://stackoverflow.com/a/33149413/3781411
     *
     * @param req Information about the request
     * @return The response of the server
     * @throws Exception In case of failure during the connection with the API
     */
    public APIResponse execPostFile(APIFileRequest req) throws Exception {

        //Add API and login tokens to the request
        addAPItokens(req);
        addLoginTokens(req);

        //Prepare response
        APIResponse response = new APIResponse();
        HttpURLConnection conn = null;
        OutputStream out;
        PrintWriter writer;

        //Create unique boundary
        String boundary = "===" + System.currentTimeMillis() + "===";
        String LINE_FEED = "\r\n";

        try {

            //Initialize connection
            URL url = new URL(BuildConfig.api_url + req.getRequest_uri());
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            //Get output stream
            out = conn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            //Append values
            for(APIPostData value : req.getParameters()){

                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"" + value.getEncodedKeyName() + "\"")
                        .append(LINE_FEED);
                writer.append("Content-Type: form-data; charset=UTF-8").append(
                        LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(value.getKey_value()).append(LINE_FEED);
                writer.flush();

            }

            //Append files
            for(APIPostFile file : req.getFiles()){

                String fileName = file.getFileName();
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append(
                        "Content-Disposition: form-data; name=\"" + file.getFieldName()
                                + "\"; filename=\"" + fileName + "\"")
                        .append(LINE_FEED);
                writer.append(
                        "Content-Type: "
                                + URLConnection.guessContentTypeFromName(fileName))
                        .append(LINE_FEED);
                writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                out.write(file.getByteArray());
                out.flush();

                writer.append(LINE_FEED);
                writer.flush();
            }

            //Finish request and get response
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            StringBuilder responseBuffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuffer.append(line);
            }
            reader.close();
            conn.disconnect();

            //Return the response
            response.setResponse_code(conn.getResponseCode());
            response.setResponse(responseBuffer.toString());
        }

        //Malformed URL Exceptions must be fixed by dev
        catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("MalformedURLException should never occur...");
        }

        catch (IOException e) {
            e.printStackTrace();
            response.setResponse_code(0);

            if(req.isTryContinueOnError() && conn != null){
                response.setResponse_code(conn.getResponseCode());
                return response;
            }

            //Throw an exception
            throw new Exception("Could not connect to the server");
        }

        return response;
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
     * Add the API client tokens to API request object
     *
     * @param params The request parameters to update
     */
    private void addAPItokens(APIRequest params){
        params.addString("serviceName", BuildConfig.api_service_name);
        params.addString("serviceToken", BuildConfig.api_service_token);
    }

    /**
     * Add the login tokens to an API request object
     *
     * @param params The parameters of the request to update
     */
    private void addLoginTokens(APIRequest params){

        //Create account object
        AccountHelper accountHelper = new AccountHelper(params.getContext());

        //Check if user is signed in or not
        if(!accountHelper.signed_in())
            return; //Do nothing

        //Get login tokens
        ArrayList<String> tokens = accountHelper.getLoginTokens();

        if(tokens.size() < 2)
            return; //Not enough tokens

        //Add them to the request
        params.addString("userToken1", tokens.get(0));
        params.addString("userToken2", tokens.get(1));

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
