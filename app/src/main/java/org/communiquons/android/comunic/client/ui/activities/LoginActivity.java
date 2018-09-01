package org.communiquons.android.comunic.client.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.enums.LoginResult;
import org.communiquons.android.comunic.client.data.helpers.APIRequestHelper;
import org.communiquons.android.comunic.client.data.helpers.AccountHelper;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.communiquons.android.comunic.client.ui.asynctasks.LoginTask;

/**
 * Login activity of the application
 *
 * @author Pierre HUBERT
 */

public class LoginActivity extends AppCompatActivity {

    /**
     * API request task (to perform login)
     */
    private LoginTask mLoginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        assert getSupportActionBar() != null;
        getSupportActionBar().hide();

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
        if(mLoginTask != null)
            mLoginTask.setOnPostExecuteListener(null);
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

        //Create Request
        undoRunningTasks();
        mLoginTask = new LoginTask(this);
        mLoginTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<LoginResult>() {

            @Override
            public void OnPostExecute(LoginResult loginResult) {
                handle_server_response(loginResult);
            }
        });
        mLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ""+login_mail.getText(), ""+login_password.getText());

    }


    /**
     * Handle server response
     *
     * @param response The server response
     */
    void handle_server_response(LoginResult response){

        enter_loading_state(false);

        switch (response){

            case SUCCESS:
                openMainActivity();
                break;

            case TOO_MANY_ATTEMPTS:
                show_form_error(getString(R.string.activity_login_too_many_request));
                break;

            case INVALID_CREDENTIALS:
                show_form_error(getString(R.string.activity_login_err_invalid_credentials));
                break;

            case SERVER_ERROR:
            default:
                show_err_server_response();
                break;

        }

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
