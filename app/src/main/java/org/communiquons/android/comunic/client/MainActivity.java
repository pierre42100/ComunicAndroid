package org.communiquons.android.comunic.client;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.data.Account.Account;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.conversations.ConversationsListHelper;
import org.communiquons.android.comunic.client.data.friendsList.FriendRefreshLoopRunnable;
import org.communiquons.android.comunic.client.fragments.ConversationFragment;
import org.communiquons.android.comunic.client.fragments.ConversationsListFragment;
import org.communiquons.android.comunic.client.fragments.FriendsListFragment;
import org.communiquons.android.comunic.client.fragments.UserInfosFragment;


/**
 * Main activity of the application
 *
 * @author Pierre HUBERT
 */
public class MainActivity extends AppCompatActivity
        implements ConversationsListHelper.openConversationListener {

    /**
     * Account object
     */
    private Account account;

    /**
     * Account utils object
     */
    private AccountUtils aUtils;

    /**
     * Friends list refresh thread
     */
    private Thread friendsListRefreshThread;

    /**
     * Database helper
     */
    private DatabaseHelper dbHelper;

    /**
     * Bottom navigation view
     */
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize account objects
        account = new Account(this);

        //Check if user is signed in or not
        if(!account.signed_in()){
            //Open the login activity
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        //Set the content of the activity
        setContentView(R.layout.activity_main);

        //Enable bottom navigation menu
        init_bottom_menu();

        //Check for connectivity
        if(!APIRequest.isAPIavailable(this)){
            Toast.makeText(this, R.string.err_no_internet_connection, Toast.LENGTH_SHORT).show();
        }

        //Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(getApplicationContext());

        //If it is the first time the application is launched, start the user friends tab
        if(savedInstanceState == null){
            openFriendsFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is signed in or not
        if(!account.signed_in()){
            //Open the login activity
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        //Refresh friends list through a thread
        friendsListRefreshThread = new Thread(
                new FriendRefreshLoopRunnable(getApplicationContext(), dbHelper));
        friendsListRefreshThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Stop the friends list refresh thread
        if(friendsListRefreshThread != null)
            friendsListRefreshThread.interrupt();
    }

    /**
     * Top menu creation
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
     * Bottom menu creation
     */
    void init_bottom_menu(){
        navigationView = (BottomNavigationView) findViewById(R.id.main_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //Check which option was selected
                switch (item.getItemId()) {

                    //If the user clicked to show the friends list
                    case R.id.main_bottom_navigation_friends_list:
                        openFriendsFragment();
                        return true;

                    //If the user chose to show information about him
                    case R.id.main_bottom_navigation_me_view:
                        openUserInfosFragment();
                        return true;

                    //If the user wants to switch to the conversation fragment
                    case R.id.main_bottom_navigation_conversations:
                        openConversationsListFragment();
                        return true;

                }

                //Selected element not found
                return false;
            }
        });
    }

    /**
     * Set the currently selected item in the bottom navigation view
     *
     * @param item The ID of the item
     */
    public void setSelectedNavigationItem(int item){
        navigationView.getMenu().findItem(item).setChecked(true);
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

    /**
     * Open the friends list fragment
     */
    void openFriendsFragment(){

        FriendsListFragment friendsListFragment = new FriendsListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, friendsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    /**
     * Open the user information fragment
     */
    void openUserInfosFragment(){
        UserInfosFragment userInfosFragment = new UserInfosFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, userInfosFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Open the conversation list fragment
     */
    void openConversationsListFragment(){
        ConversationsListFragment conversationsListFragment = new ConversationsListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, conversationsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Open a conversation
     *
     * @param id The ID of the conversation to open
     */
    @Override
    public void openConversation(int id) {

        //Set the arguments for the conversation
        Bundle args = new Bundle();
        args.putInt(ConversationFragment.ARG_CONVERSATION_ID ,id);

        //Create the fragment
        ConversationFragment conversationFragment = new ConversationFragment();
        conversationFragment.setArguments(args);

        //Display it
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, conversationFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
