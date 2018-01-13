package org.communiquons.android.comunic.client;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.data.Account.Account;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.conversations.ConversationsListHelper;
import org.communiquons.android.comunic.client.data.friendsList.FriendRefreshLoopRunnable;
import org.communiquons.android.comunic.client.data.utils.UiUtils;
import org.communiquons.android.comunic.client.fragments.ConversationFragment;
import org.communiquons.android.comunic.client.fragments.ConversationsListFragment;
import org.communiquons.android.comunic.client.fragments.FriendsListFragment;
import org.communiquons.android.comunic.client.fragments.UpdateConversationFragment;
import org.communiquons.android.comunic.client.fragments.UserInfosFragment;
import org.communiquons.android.comunic.client.fragments.UserPageFragment;


/**
 * Main activity of the application
 *
 * @author Pierre HUBERT
 */
public class MainActivity extends AppCompatActivity
        implements ConversationsListHelper.openConversationListener,
        ConversationsListHelper.updateConversationListener {

    /**
     * Debug tag
     */
    private static final String TAG = "MainActivity";

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
     * Conversations list helper
     */
    private ConversationsListHelper conversationsListHelper;

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
        dbHelper = DatabaseHelper.getInstance(this);

        //Intialize conversation list helper
        conversationsListHelper = new ConversationsListHelper(this, dbHelper);

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

                        //Old version
                        //openUserInfosFragment();

                        //New version
                        openUserPage(AccountUtils.getID(MainActivity.this));
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
     * Open the page of the specified user
     *
     * @param userID The ID of the user to open
     */
    void openUserPage(int userID){

        //Prepare arguments
        Bundle args = new Bundle();
        args.putInt(UserPageFragment.ARGUMENT_USER_ID, userID);

        //Create fragment
        UserPageFragment userPageFragment = new UserPageFragment();
        userPageFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, userPageFragment);
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

    @Override
    public void openPrivateConversation(int userID) {
        //Log action
        Log.v(TAG, "Open private conversation with user ID " + userID);

        //Create a loading dialog
        final AlertDialog dialog = UiUtils.create_loading_dialog(this);

        //Get conversation ID in the background
        new AsyncTask<Integer, Void, Integer>(){

            @Override
            protected Integer doInBackground(Integer... params) {
                return conversationsListHelper.getPrivate(params[0], true);
            }

            @Override
            protected void onPostExecute(@Nullable Integer integer) {

                //Close loading dialog
                dialog.dismiss();

                if(integer != null)
                    openConversation(integer);
                else
                    Toast.makeText(MainActivity.this, R.string.err_get_private_conversation,
                            Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userID);
    }

    @Override
    public void createConversation() {
        updateConversation(0);
    }

    @Override
    public void updateConversation(int convID) {

        //Set the arguments of the fragment
        Bundle args = new Bundle();
        args.putInt(UpdateConversationFragment.ARG_CONVERSATION_ID, convID);

        //Create the fragment
        UpdateConversationFragment updateConversationFragment = new UpdateConversationFragment();
        updateConversationFragment.setArguments(args);

        //Display the fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, updateConversationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
