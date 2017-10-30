package org.communiquons.android.comunic.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /**
     * Acount object
     */
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize account object
        account = new Account(this);

        //Check if user is signed in or not
        if(!account.signed_in()){

            //Open the login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
