package org.communiquons.android.comunic.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.data.Account.Account;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import org.communiquons.android.comunic.client.api.*;

/**
 * Login activity of the application
 *
 * @author Pierre HUBERT
 */

public class LoginActivity extends AppCompatActivity {

    /**
     * Utilities object
     */
    private Utilities utils;

    /**
     * Account utilities object
     */
    private AccountUtils aUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Create utilities object
        utils = new Utilities(this);

        //Create account utilities object
        aUtils = new AccountUtils(this);

        //Check for connectivity
        if(!APIRequestTask.isAPIavailable(this)){
            Toast.makeText(this, R.string.err_no_internet_connection, Toast.LENGTH_SHORT).show();
        }

        //Make login submit button lives
        findViewById(R.id.login_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Submit login form
                submitLogin();
            }
        });


    }

    /**
     * Handle login form submission
     */
    void submitLogin(){

        //Get the fields
        final EditText login_mail = (EditText) findViewById(R.id.email_field);
        EditText login_password = (EditText) findViewById(R.id.password_field);


        int stop = 0;

        //Check password
        if(login_password.length()<3){
            login_password.setError(getString(R.string.activity_login_err_invalid_password));
            login_password.requestFocus();
            stop = 1;
        }

        //Check email address
        if(login_mail.length() < 3 || !utils.isValidMail(login_mail.getText())){
            login_mail.setError(getString(R.string.activity_login_err_invalid_email));
            login_mail.requestFocus();
            stop = 1;
        }

        //Check internet connection
        if(!APIRequestTask.isAPIavailable(this)){
            show_form_error(getString(R.string.err_no_internet_connection));
            stop = 1;
        }


        //Stop if required
        if(stop != 0)
            return;

        show_form_error("");
        enter_loading_state(true);


        //Perform a request on the API to check user credentials and get login tokens
        APIRequestParameters params = new APIRequestParameters(this, "user/connectUSER");
        params.addParameter("userMail", ""+login_mail.getText());
        params.addParameter("userPassword", ""+login_password.getText());

        //Create Request
        new APIRequestTask(){
            @Override
            protected void onPostExecute(APIResponse result) {

                //Check for errors
                if(result == null) {

                    //Hide loading wheel
                    enter_loading_state(false);

                    //Put the error on the login mail field
                    show_form_error(getString(R.string.activity_login_err_invalid_credentials));
                }

                else
                    //Perform next actions
                    handle_server_response(result);

            }
        }.execute(params);
    }

    /**
     * Handle server responses that seems to accept a response
     *
     * @param response The server reponse
     */
    void handle_server_response(APIResponse response){

        JSONObject data = response.getJSONObject();

        //Check for decoding response errors
        if(data == null) {
            show_err_server_response();
            return;
        }

        try {
            //Search for tokens
            JSONObject tokensObj = data.getJSONObject("tokens");

            //Extract tokens
            ArrayList<String> tokens = new ArrayList<>();
            tokens.add(tokensObj.getString("token1"));
            tokens.add(tokensObj.getString("token2"));

            //Save tokens
            Account account = new Account(this);
            if(!account.save_new_tokens(tokens)) {
                show_err_server_response();
                return;
            }

        } catch (JSONException e){
            e.printStackTrace();
            show_err_server_response();
            return;
        }

        //Refresh current user ID
        aUtils.refresh_current_user_id(new AccountUtils.onceRefreshedUserID(){
            @Override
            public void callback(boolean success) {

                //Check if it is a success or a failure
                if(!success){
                    show_err_server_response();
                }
                else {
                    //Redirect to the main activity
                    Intent redirect = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(redirect);
                }

            }
        });

    }

    /**
     * Switch between loading state and ready state for the login form
     *
     * @param show_progress Specify wether a progress bar should be shown or not
     */
    void enter_loading_state(boolean show_progress){
        //Grab elements
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        ScrollView login_form = (ScrollView) findViewById(R.id.login_form);

        progressBar.setVisibility(show_progress ? View.VISIBLE : View.GONE);
        login_form.setVisibility(show_progress ? View.GONE : View.VISIBLE);
    }

    /**
     * Display an error message on the login screen
     *
     * @param message The message to display on the screen
     */
    void show_form_error(String message){

        //Retrieve error field
        TextView v_error = (TextView) findViewById(R.id.login_error_message);

        //Check what to do
        boolean display = message.length() > 0;

        //Make it visible (or hide it)
        v_error.setVisibility(display ? View.VISIBLE : View.GONE);

        //Set the text
        v_error.setText(message);

    }

    /**
     * Display an error message to say the server responded incorrectly to the request
     */
    void show_err_server_response(){
        show_form_error(getString(R.string.activity_login_err_server_response));
        enter_loading_state(false);
    }
}
