package org.communiquons.android.comunic.client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.communiquons.android.comunic.client.api.APIRequestTask;


/**
 * Main activity of the application
 *
 * @author Pierre HUBERT
 */
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is signed in or not
        if(!account.signed_in()){
            //Open the login activity
            startActivity(new Intent(this, LoginActivity.class));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_signout_title)
                .setMessage(R.string.popup_signout_message)
                .setCancelable(true)
                .setPositiveButton(R.string.popup_signout_confirm_button,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Sign out user
                        account.sign_out();

                        //Redirect to login activity
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }
                })
                .setNegativeButton(R.string.popup_signout_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing now
                    }
                })

                //Show popup
                .create().show();
    }
}
