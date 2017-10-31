package org.communiquons.android.comunic.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    /**
     * Utilities object
     */
    private Utilities utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Create utilities object
        utils = new Utilities(this);

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
        EditText login_mail = (EditText) findViewById(R.id.email_field);
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

        //Stop if required
        if(stop != 0)
            return;


    }
}
