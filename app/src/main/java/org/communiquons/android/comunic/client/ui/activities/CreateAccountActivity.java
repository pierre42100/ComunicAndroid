package org.communiquons.android.comunic.client.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.BuildConfig;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.enums.CreateAccountResult;
import org.communiquons.android.comunic.client.data.helpers.AccountHelper;
import org.communiquons.android.comunic.client.data.models.NewAccount;
import org.communiquons.android.comunic.client.data.utils.Utilities;

import static android.os.AsyncTask.Status.FINISHED;

/**
 * Create account activity
 *
 * @author Pierre HUBERT
 */
public class CreateAccountActivity extends AppCompatActivity
    implements SafeAsyncTask.OnPostExecuteListener<CreateAccountResult> {

    /**
     * Input fields
     */
    private ProgressBar mProgress;
    private ScrollView mForm;
    private TextView mError;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailAddress;
    private EditText mPassword;
    private EditText mRepeatPassword;
    private CheckBox mTermsCheckbox;
    private TextView mTermsLink;
    private Button mSubmitButton;

    /**
     * Create account task
     */
    private static CreateAccountTask mCreateAccountTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Display backward arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the views
        mProgress = findViewById(R.id.loading_progress);
        mForm = findViewById(R.id.create_account_form);
        mError = findViewById(R.id.error_target);
        mFirstName = findViewById(R.id.first_name);
        mLastName = findViewById(R.id.last_name);
        mEmailAddress = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRepeatPassword = findViewById(R.id.confirm_password);
        mTermsCheckbox = findViewById(R.id.terms_checkbox);
        mTermsLink = findViewById(R.id.terms_link);
        mSubmitButton = findViewById(R.id.submit_button);

        //Remove error text
        setError("");

        //Make terms link lives
        mTermsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(BuildConfig.comunic_terms_url));
                startActivity(intent);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        //Check if a request to create an account is running or not
        if(isCreating()){
            mCreateAccountTask.setOnPostExecuteListener(this);
            setLoading(true);
        }
        else
            setLoading(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void submitForm(){

        if(isCreating())
            return;

        //Reset errors
        setError("");
        mFirstName.setError(null);
        mLastName.setError(null);
        mEmailAddress.setError(null);
        mPassword.setError(null);
        mRepeatPassword.setError(null);
        mTermsCheckbox.setError(null);

        //Get the values
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String email = mEmailAddress.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mRepeatPassword.getText().toString();

        //Check values
        boolean cancel = false;
        View focusView = null;

        //Check first name
        if(firstName.length() < 2){
            mFirstName.setError(getString(R.string.err_invalid_first_name));
            cancel = true;
            focusView = mFirstName;
        }

        //Check last name
        if(lastName.length() < 2){
            mLastName.setError(getString(R.string.err_invalid_last_name));
            cancel = true;
            focusView = focusView != null ? focusView : mLastName;
        }

        //Check email address
        if(email.isEmpty()){
            mEmailAddress.setError(getString(R.string.err_no_email));
            cancel = true;
            focusView = focusView != null ? focusView : mEmailAddress;
        }
        else if(!Utilities.isValidMail(email)){
            mEmailAddress.setError(getString(R.string.err_invalid_email));
            cancel = true;
            focusView = focusView != null ? focusView : mEmailAddress;
        }

        //Check password
        if(password.length() < 4){
            mPassword.setError(getString(R.string.err_invalid_password));
            cancel = true;
            focusView = focusView != null ? focusView : mPassword;
        } else if(!password.equals(confirmPassword)){
            mRepeatPassword.setError(getString(R.string.err_password_confirmation_not_identical));
            cancel = true;
            focusView = focusView != null ? focusView : mRepeatPassword;
        }

        //Check the terms
        if(!mTermsCheckbox.isChecked()){
            mTermsCheckbox.setError(getString(R.string.err_need_accept_terms));
            cancel = true;
            focusView = focusView != null ? focusView : mTermsCheckbox;
        }

        //Check if we can not continue
        if(cancel){
            if(focusView != null)
                focusView.requestFocus();
            return;
        }

        //Submit request
        NewAccount newAccount = new NewAccount();
        newAccount.setFirstName(firstName);
        newAccount.setLastName(lastName);
        newAccount.setEmail(email);
        newAccount.setPassword(password);

        //Initialize task
        mCreateAccountTask = new CreateAccountTask(this);
        mCreateAccountTask.setOnPostExecuteListener(this);
        mCreateAccountTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newAccount);

        //Mark the task as running
        setLoading(true);
    }

    /**
     * Check whether an account creation request has already been sent or not
     *
     * @return TRUE if a request is running / FALSE else
     */
    private boolean isCreating() {
        return mCreateAccountTask != null
                && !mCreateAccountTask.isCancelled()
                && mCreateAccountTask.getStatus() != FINISHED;

    }

    /**
     * Set the task as loading
     *
     * @param loading TRUE if the task is running / FALSE else
     */
    private void setLoading(boolean loading){
        mForm.setVisibility(loading ? View.GONE : View.VISIBLE);
        mProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    /**
     * Set a new error message
     *
     * @param message The new error message to display
     */
    private void setError(String message){
        mError.setVisibility(message.isEmpty() ? View.GONE : View.VISIBLE);
        mError.setText(message);
    }

    @Override
    public void OnPostExecute(CreateAccountResult result) {
        setLoading(false);

        //Check for errors
        if(result != CreateAccountResult.SUCCESS){

            //Find the right error to display
            int message = R.string.err_while_creating_account;
            switch (result){

                //Too many account creation requests
                case ERROR_TOO_MANY_REQUESTS:
                    message = R.string.err_create_account_too_many_requests;
                    break;

                //Existing email address
                case ERROR_EXISTING_EMAIL:
                    message = R.string.err_create_account_existing_email;
                    break;
            }

            mForm.scrollTo(1, 1);
            setError(getString(message));
            return;
        }

        //In case of success, inflate a new view
        setContentView(R.layout.activity_account_created);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Class used to create an account across activities
     */
    private static class CreateAccountTask extends SafeAsyncTask<NewAccount, Void, CreateAccountResult> {

        CreateAccountTask(Context context) {
            super(context);
        }

        @Override
        protected CreateAccountResult doInBackground(NewAccount... newAccounts) {
            return new AccountHelper(getContext()).createAccount(newAccounts[0]);
        }

    }
}
