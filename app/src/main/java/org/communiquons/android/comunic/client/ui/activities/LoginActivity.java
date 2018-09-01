package org.communiquons.android.comunic.client.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.APIRequestHelper;
import org.communiquons.android.comunic.client.data.helpers.AccountHelper;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.communiquons.android.comunic.client.ui.asynctasks.APIRequestTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Login activity of the application
 *
 * @author Pierre HUBERT
 */

public class LoginActivity extends AppCompatActivity {

    /**
     * Account utilities object
     */
    private AccountUtils aUtils;

    /**
     * API request task (to perform login)
     */
    private APIRequestTask mApiRequestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        assert getSupportActionBar() != null;
        getSupportActionBar().hide();

        //Create account utilities object
        aUtils = new AccountUtils(this);

        //Check for connectivity
        if(!APIRequestHelper.isAPIavailable(this)){
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


        //Make create account button lives
        findViewById(R.id.btn_create_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,
                        CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //If the user is signed in, open the main activity
        if(new AccountHelper(this).signed_in())
            openMainActivity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        undoRunningTasks();
    }

    /**
     * Cancel any running task
     */
    private void undoRunningTasks(){
        if(mApiRequestTask != null)
            mApiRequestTask.setOnPostExecuteListener(null);
    }

    /**
     * Handle login form submission
     */
    void submitLogin(){

        //Get the fields
        EditText login_mail = findViewById(R.id.email_field);
        EditText login_password = findViewById(R.id.password_field);


        int stop = 0;

        //Check password
        if(login_password.length()<3){
            login_password.setError(getString(R.string.activity_login_err_invalid_password));
            login_password.requestFocus();
            stop = 1;
        }

        //Check email address
        if(login_mail.length() < 3 || !Utilities.isValidMail(login_mail.getText())){
            login_mail.setError(getString(R.string.activity_login_err_invalid_email));
            login_mail.requestFocus();
            stop = 1;
        }

        //Check internet connection
        if(!APIRequestHelper.isAPIavailable(this)){
            show_form_error(getString(R.string.err_no_internet_connection));
            stop = 1;
        }


        //Stop if required
        if(stop != 0)
            return;

        show_form_error("");
        enter_loading_state(true);


        //Perform a request on the API to check user credentials and get login tokens
        APIRequest params = new APIRequest(this, "user/connectUSER");
        params.setTryContinueOnError(true);
        params.addString("userMail", ""+login_mail.getText());
        params.addString("userPassword", ""+login_password.getText());

        //Create Request
        undoRunningTasks();
        mApiRequestTask = new APIRequestTask(this);
        mApiRequestTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<APIResponse>() {
            @Override
            public void OnPostExecute(APIResponse apiResponse) {
                handle_server_response(apiResponse);
            }
        });
        mApiRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

    }


    /**
     * Handle server response
     *
     * @param response The server response
     */
    void handle_server_response(@Nullable APIResponse response){

        if(response == null){
           show_err_server_response();
            return;
        }

        if(response.getResponse_code() != 200){

            enter_loading_state(false);

            if(response.getResponse_code() == 429)
                show_form_error(getString(R.string.activity_login_too_many_request));
            else
                show_form_error(getString(R.string.activity_login_err_invalid_credentials));

            return;
        }

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
            AccountHelper accountHelper = new AccountHelper(this);
            if(!accountHelper.save_new_tokens(tokens)) {
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
                    openMainActivity();
                }

            }
        });

    }

    /**
     * Open the main activity
     */
    private void openMainActivity(){
        Intent redirect = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(redirect);
    }

    /**
     * Switch between loading state and ready state for the login form
     *
     * @param show_progress Specify whether a progress bar should be shown or not
     */
    void enter_loading_state(boolean show_progress){
        //Grab elements
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        ScrollView login_form = findViewById(R.id.login_form);

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
        TextView v_error = findViewById(R.id.login_error_message);

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
