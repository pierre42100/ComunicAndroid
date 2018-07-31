package org.communiquons.android.comunic.client.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import org.communiquons.android.comunic.client.BuildConfig;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.crashreporter.CrashReporter;
import org.communiquons.android.comunic.client.data.helpers.APIRequestHelper;
import org.communiquons.android.comunic.client.data.helpers.AccountHelper;
import org.communiquons.android.comunic.client.data.helpers.ConversationsListHelper;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.DebugHelper;
import org.communiquons.android.comunic.client.data.runnables.FriendRefreshLoopRunnable;
import org.communiquons.android.comunic.client.data.services.NotificationsService;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.data.utils.PreferencesUtils;
import org.communiquons.android.comunic.client.ui.fragments.ConversationFragment;
import org.communiquons.android.comunic.client.ui.fragments.ConversationsListFragment;
import org.communiquons.android.comunic.client.ui.fragments.FriendsListFragment;
import org.communiquons.android.comunic.client.ui.fragments.LatestPostsFragment;
import org.communiquons.android.comunic.client.ui.fragments.NotificationsFragment;
import org.communiquons.android.comunic.client.ui.fragments.SinglePostFragment;
import org.communiquons.android.comunic.client.ui.fragments.UpdateConversationFragment;
import org.communiquons.android.comunic.client.ui.fragments.UserAccessDeniedFragment;
import org.communiquons.android.comunic.client.ui.fragments.UserPageFragment;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;
import org.communiquons.android.comunic.client.ui.listeners.onPostOpenListener;
import org.communiquons.android.comunic.client.ui.listeners.openConversationListener;
import org.communiquons.android.comunic.client.ui.listeners.updateConversationListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;


/**
 * Main activity of the application
 *
 * @author Pierre HUBERT
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, openConversationListener,
        updateConversationListener, onOpenUsersPageListener, onPostOpenListener {

    /**
     * Debug tag
     */
    private static final String TAG = "MainActivity";

    /**
     * Intent code : search a user
     */
    private static final int SEARCH_USER_INTENT = 3;

    /**
     * Account object
     */
    private AccountHelper accountHelper;

    /**
     * Account utils object
     */
    private AccountUtils aUtils;

    /**
     * Friends list refresh thread
     */
    private FriendRefreshLoopRunnable friendsListRefreshRunnable;

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
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize crash reporter
        CrashReporter reporter = new CrashReporter(this, BuildConfig.crash_reporter_url,
                BuildConfig.crash_reporter_key);
        reporter.uploadAwaitingReport();
        Thread.setDefaultUncaughtExceptionHandler(reporter);

        //Initialize account objects
        accountHelper = new AccountHelper(this);

        //Check if user is signed in or not
        if (!accountHelper.signed_in()) {
            //Open the login activity
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        //Set the content of the activity
        setContentView(R.layout.activity_main);

        //Enable drawer
        init_drawer();

        //Check for connectivity
        if (!APIRequestHelper.isAPIavailable(this)) {
            Toast.makeText(this, R.string.err_no_internet_connection, Toast.LENGTH_SHORT).show();
        }

        //Initialize DatabaseHelper
        dbHelper = DatabaseHelper.getInstance(this);

        //Initialize conversation list helper
        conversationsListHelper = new ConversationsListHelper(this, dbHelper);

        //If it is the first time the application is launched, open notifications fragment
        if (savedInstanceState == null) {
            openNotificationsFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is signed in or not
        if (!accountHelper.signed_in()) {
            //Open the login activity
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        //Refresh friends list through a thread
        friendsListRefreshRunnable = new FriendRefreshLoopRunnable(getApplicationContext(),
                dbHelper);
        new Thread(friendsListRefreshRunnable).start();

        //Start notification thread
        Intent intent = new Intent(this, NotificationsService.class);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Stop the friends list refresh thread
        if (friendsListRefreshRunnable != null)
            friendsListRefreshRunnable.interrupt();
    }





    /**
     * Top menu creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //Check if the debug menu has to be shown or not
        if (PreferencesUtils.getBoolean(this, "enable_debug_mode", false)) {
            SubMenu debugMenu = menu.addSubMenu(R.string.menu_debug_title);
            getMenuInflater().inflate(R.menu.debug_menu, debugMenu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Get action id
        int id = item.getItemId();

        //To go backward
        if (id == android.R.id.home) {
            toggleDrawer();
            return true;
        }

        //To search a user
        if (id == R.id.action_search_user) {
            searchUser();
            return true;
        }

        //To display the personal page of the user
        if (id == R.id.action_open_user_page) {
            openUserPage(AccountUtils.getID(MainActivity.this));
            return true;
        }

        //To display the list of friends
        if (id == R.id.action_friends_list) {
            openFriendsFragment();
            return true;
        }

        //To open settings fragment
        if (id == R.id.action_settings) {
            openSettingsFragment();
            return true;
        }

        //Check for logout request
        if (id == R.id.action_logout) {
            confirmUserLogout();
            return true;
        }

        //Check if user wants to clear database
        if (id == R.id.action_clear_local_db) {
            clearLocalDatabase();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }





    /**
     * Drawer menu
     */
    void init_drawer() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }


    /**
     * Set the currently selected item in the drawer
     *
     * @param item The ID of the item
     */
    public void setSelectedNavigationItem(int item) {

    }

    /**
     * Toggle drawer state
     */
    void toggleDrawer(){
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            mDrawer.openDrawer(GravityCompat.START);
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Parent level
        super.onActivityResult(requestCode, resultCode, data);

        //Check if the request was to search a user
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {

                case SEARCH_USER_INTENT:
                    assert data.getData() != null;
                    openUserPage(Integer.decode(data.getData().getQueryParameter("userID")));
                    break;

            }

        }

    }

    /**
     * Ask user to confirm if he really what to sign out or not
     */
    void confirmUserLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_signout_title)
                .setMessage(R.string.popup_signout_message)
                .setCancelable(true)
                .setPositiveButton(R.string.popup_signout_confirm_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Sign out user
                                accountHelper.sign_out();

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
    void openFriendsFragment() {

        FriendsListFragment friendsListFragment = new FriendsListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, friendsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    /**
     * Open settings activity
     */
    void openSettingsFragment() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Open notifications fragment
     */
    void openNotificationsFragment() {
        NotificationsFragment notifications = new NotificationsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, notifications);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Open the page of the specified user
     *
     * @param userID The ID of the user to open
     */
    @Override
    public void openUserPage(int userID) {

        //Prepare arguments
        Bundle args = new Bundle();
        args.putInt(UserPageFragment.ARGUMENT_USER_ID, userID);

        //Create fragment
        UserPageFragment userPageFragment = new UserPageFragment();
        userPageFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, userPageFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    /**
     * Open the page of a user for which the access has been denied
     *
     * @param userID The ID of the target user
     */
    @Override
    public void openUserAccessDeniedPage(int userID) {

        //Prepare the argument
        Bundle args = new Bundle();
        args.putInt(UserAccessDeniedFragment.ARGUMENT_USER_ID, userID);

        //Create fragment
        UserAccessDeniedFragment userAccessDeniedFragment = new UserAccessDeniedFragment();
        userAccessDeniedFragment.setArguments(args);

        //Remove the last entry of the backstack
        //This is important in order to avoid to get the user unable to quit the page.
        //Because it would get the user back to the user page fragment which would
        //redirect immediately to this fragment indefinitely.
        getSupportFragmentManager().popBackStackImmediate();

        //Perform the transition
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.main_fragment, userAccessDeniedFragment);
        transaction.commit();

    }

    /**
     * Open the conversation list fragment
     */
    void openConversationsListFragment() {
        ConversationsListFragment conversationsListFragment = new ConversationsListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
        args.putInt(ConversationFragment.ARG_CONVERSATION_ID, id);

        //Create the fragment
        ConversationFragment conversationFragment = new ConversationFragment();
        conversationFragment.setArguments(args);

        //Display it
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
        new AsyncTask<Integer, Void, Integer>() {

            @Override
            protected Integer doInBackground(Integer... params) {
                return conversationsListHelper.getPrivate(params[0], true);
            }

            @Override
            protected void onPostExecute(@Nullable Integer integer) {

                //Close loading dialog
                dialog.dismiss();

                if (integer != null)
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, updateConversationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onOpenPost(int postID) {

        //Prepare the arguments
        Bundle arguments = new Bundle();
        arguments.putInt(SinglePostFragment.ARGUMENT_POST_ID, postID);

        //Create the fragment
        SinglePostFragment singlePostFragment = new SinglePostFragment();
        singlePostFragment.setArguments(arguments);

        //Perform the transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.main_fragment, singlePostFragment);
        transaction.commit();
    }

    /**
     * Open latest posts fragment
     */
    public void openLatestPostsFragment() {

        //Create the fragment
        LatestPostsFragment latestPostsFragment = new LatestPostsFragment();

        //Perform the transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, latestPostsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Clear the cache database of the application
     */
    private void clearLocalDatabase() {

        if (!new DebugHelper(this).clearLocalDatabase())
            Toast.makeText(this, R.string.err_clear_local_db, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, R.string.success_clear_local_db, Toast.LENGTH_SHORT).show();
    }


    /**
     * Open a search activity to find a user
     */
    private void searchUser() {

        //Make intent
        Intent intent = new Intent(this, SearchUserActivity.class);
        startActivityForResult(intent, SEARCH_USER_INTENT);

    }
}
