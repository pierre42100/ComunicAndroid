package org.communiquons.android.comunic.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * Acount object
     */
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check for connectivity
        if(!APIRequestTask.isAPIavailable(this)){
            Toast.makeText(this, R.string.err_no_internet_connection, Toast.LENGTH_SHORT).show();
        }

        //Initialize account object
        account = new Account(this);

        //Check if user is signed in or not
        if(!account.signed_in()){

            //Open the login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Menu creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Get action id
        int id = item.getItemId();

        //Check for logout request
        if(id == R.id.action_logout){
            confirmUserLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * Ask user to confirm if he really what to sign out or not
     */
    void confirmUserLogout(){
        Toast.makeText(this, "Do you really want to signout ???", Toast.LENGTH_SHORT).show();
    }
}
