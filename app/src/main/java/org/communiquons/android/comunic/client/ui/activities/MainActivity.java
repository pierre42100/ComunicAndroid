package org.communiquons.android.comunic.client.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.communiquons.android.comunic.client.BuildConfig;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.crashreporter.CrashReporter;
import org.communiquons.android.comunic.client.data.enums.VirtualDirectoryType;
import org.communiquons.android.comunic.client.data.helpers.APIRequestHelper;
import org.communiquons.android.comunic.client.data.helpers.AccountHelper;
import org.communiquons.android.comunic.client.data.helpers.ConversationsListHelper;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.DebugHelper;
import org.communiquons.android.comunic.client.data.models.CallInformation;
import org.communiquons.android.comunic.client.data.models.NotificationsCount;
import org.communiquons.android.comunic.client.data.models.VirtualDirectory;
import org.communiquons.android.comunic.client.data.runnables.FriendRefreshLoopRunnable;
import org.communiquons.android.comunic.client.data.services.NotificationsService;
import org.communiquons.android.comunic.client.data.utils.PreferencesUtils;
import org.communiquons.android.comunic.client.ui.asynctasks.CreateCallForConversationTask;
import org.communiquons.android.comunic.client.ui.asynctasks.FindVirtualDirectoryTask;
import org.communiquons.android.comunic.client.ui.asynctasks.GetCallConfigurationTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.fragments.ConversationFragment;
import org.communiquons.android.comunic.client.ui.fragments.ConversationsListFragment;
import org.communiquons.android.comunic.client.ui.fragments.FriendsListFragment;
import org.communiquons.android.comunic.client.ui.fragments.LatestPostsFragment;
import org.communiquons.android.comunic.client.ui.fragments.NotificationsFragment;
import org.communiquons.android.comunic.client.ui.fragments.SinglePostFragment;
import org.communiquons.android.comunic.client.ui.fragments.UpdateConversationFragment;
import org.communiquons.android.comunic.client.ui.fragments.groups.GroupAccessDeniedFragment;
import org.communiquons.android.comunic.client.ui.fragments.groups.GroupPageMainFragment;
import org.communiquons.android.comunic.client.ui.fragments.groups.UserGroupsFragment;
import org.communiquons.android.comunic.client.ui.fragments.userpage.UserAccessDeniedFragment;
import org.communiquons.android.comunic.client.ui.fragments.userpage.UserPageFragment;
import org.communiquons.android.comunic.client.ui.listeners.OnOpenCallListener;
import org.communiquons.android.comunic.client.ui.listeners.OnOpenPageListener;
import org.communiquons.android.comunic.client.ui.listeners.onPostOpenListener;
import org.communiquons.android.comunic.client.ui.listeners.openConversationListener;
import org.communiquons.android.comunic.client.ui.listeners.updateConversationListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.views.NavigationBar;

import java.util.Objects;

import static org.communiquons.android.comunic.client.ui.Constants.IntentRequestCode.MAIN_ACTIVITY_GLOBAL_SEARCH_INTENT;
import static org.communiquons.android.comunic.client.ui.Constants.IntentRequestCode.MAIN_ACTIVITY_SEARCH_USER_INTENT;
import static org.communiquons.android.comunic.client.ui.Constants.PreferencesKeys.PREFERENCE_ACCELERATE_NOTIFICATIONS_REFRESH;
import static org.communiquons.android.comunic.client.ui.Constants.PreferencesKeys.PREFERENCE_ENABLE_DEBUG_MODE;


/**
 * Main activity of the application
 *
 * @author Pierre HUBERT
 */
public class MainActivity extends BaseActivity implements
        openConversationListener, updateConversationListener, OnOpenPageListener,
        onPostOpenListener, NavigationBar.OnNavigationItemSelectedListener, OnOpenCallListener {

    /**
     * Debug tag
     */
    private static final String TAG = "MainActivity";

    /**
     * Account object
     */
    private AccountHelper accountHelper;

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
     * Main navigation bar
     */
    private NavigationBar mNavBar;

    /**
     * Tasks
     */
    private FindVirtualDirectoryTask mFindVirtualDirectoryTask;

    /**
     * Broadcast receiver
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() == null)
                return;

            //Notifications number update
            if(intent.getAction().equals(NotificationsService.BROADCAST_ACTION)){
                NotificationsCount count = new NotificationsCount();
                assert intent.getExtras() != null;
                count.setNotificationsCount(intent.getExtras().getInt(
                        NotificationsService.BROADCAST_EXTRA_NUMBER_NOTIFICATIONS));
                count.setConversationsCount(intent.getExtras().getInt(
                        NotificationsService.BROADCAST_EXTRA_UNREAD_CONVERSATIONS));
                count.setFriendsRequestsCount(intent.getExtras().getInt(
                        NotificationsService.BROADCAST_EXTRA_NUMBER_FRIENDSHIP_REQUESTS));
                updateNumberNotifications(count);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize crash reporter (if enabled)
        if(PreferencesUtils.getBoolean(this, "enable_crash_reporting", true)){
            CrashReporter reporter = new CrashReporter(this, BuildConfig.crash_reporter_url,
                    BuildConfig.crash_reporter_key);
            reporter.uploadAwaitingReport();
            Thread.setDefaultUncaughtExceptionHandler(reporter);
        }

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

        //Check for connectivity
        if (!APIRequestHelper.isAPIavailable(this)) {
            Toast.makeText(this, R.string.err_no_internet_connection, Toast.LENGTH_SHORT).show();
        }

        //Initialize DatabaseHelper
        dbHelper = DatabaseHelper.getInstance(this);

        //Initialize conversation list helper
        conversationsListHelper = new ConversationsListHelper(this, dbHelper);

        //Use navigation bar
        getSupportActionBar().hide();
        mNavBar = findViewById(R.id.nav_bar);
        mNavBar.setOnNavigationItemSelectedListener(this);

        //If it is the first time the application is launched, open notifications fragment
        if (savedInstanceState == null){
            openNotificationsFragment(false);
        }

        //Receive broadcasts
        IntentFilter intentFilter = new IntentFilter(NotificationsService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);

        //Get calls configuration
        GetCallConfigurationTask callConfigurationTask = new GetCallConfigurationTask(this);
        callConfigurationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getTasksManager().addTask(callConfigurationTask);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unsetFindVirtualDirectoryTask();
    }

    /**
     * Activity context menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //Check if the debug menu has to be shown or not
        if (PreferencesUtils.getBoolean(this, PREFERENCE_ENABLE_DEBUG_MODE, false)) {
            SubMenu debugMenu = menu.addSubMenu(R.string.menu_debug_title);
            getMenuInflater().inflate(R.menu.debug_menu, debugMenu);

            debugMenu.findItem(R.id.action_accelerate_notifications_refresh).setChecked(
                    PreferencesUtils.getBoolean(
                            this,
                            PREFERENCE_ACCELERATE_NOTIFICATIONS_REFRESH,
                            false
                    )
            );
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Get action id
        int id = item.getItemId();

        //Display user groups
        if(id == R.id.action_user_groups){
            openUserGroups();
            return true;
        }

        //To perform a wider search
        if(id == R.id.action_search) {
            searchGlobal();
            return true;
        }

        //To open account settings
        if(id == R.id.action_account_settings){
            openAccountSettings();
            return true;
        }

        //To open settings fragment
        if (id == R.id.action_settings) {
            openSettingsFragment();
            return true;
        }

        //To learn more about the application
        if(id == R.id.action_about){
            startActivity(new Intent(this, AboutActivity.class));
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

        //Check if we have to accelerate notifications refresh
        if(id == R.id.action_accelerate_notifications_refresh){

            boolean enable = !PreferencesUtils.getBoolean(
                    this, PREFERENCE_ACCELERATE_NOTIFICATIONS_REFRESH, false);

            PreferencesUtils.setBoolean(
                    this,
                    PREFERENCE_ACCELERATE_NOTIFICATIONS_REFRESH,
                    enable);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



    /**
     * Navigation bar options
     *
     * @param menuItem Selected item
     * @return TRUE if the event is consumed / FALSE else
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        //Notification fragment
        if(id == R.id.action_notifications){
            openNotificationsFragment(true);
        }

        //Friends list
        else if(id == R.id.action_friendslist){
            openFriendsFragment();
        }

        //User personal page
        else if(id == R.id.action_personal_page){
            openUserPage(new AccountHelper(this).get_current_user_id());
        }

        //Latest posts
        else if(id == R.id.action_latest_posts){
            openLatestPostsFragment();
        }

        //Conversations fragment
        else if(id == R.id.action_conversations) {
            openConversationsListFragment();
        }

        //More options
        else if(id == R.id.action_more){
            PopupMenu popupMenu = new PopupMenu(this,
                    mNavBar.getItemIdentifierView(R.id.action_more));
            onCreateOptionsMenu(popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
            popupMenu.show();
            return false;
        }

        else
            //Option not found
            return false;

        return true;
    }

    /**
     * Specify what is the selected
     *
     * @param activity An instance of {@link MainActivity}
     * @param id The identifier of the menu to choose
     */
    public static void SetNavbarSelectedOption(@NonNull Activity activity, int id){

        if(!(activity instanceof MainActivity))
            throw new RuntimeException("Specified activity is not an instance of activity!");

        ((MainActivity)activity).mNavBar.setIdentifierSelected(id);

    }

    /**
     * Update the number of unread notifications
     *
     * @param count New number of notifications
     */
    public void updateNumberNotifications(NotificationsCount count){
        mNavBar.getItemIdentifierView(R.id.action_notifications).setNumberNews(
                count.getNotificationsCount());

        mNavBar.getItemIdentifierView(R.id.action_conversations).setNumberNews(
                count.getConversationsCount());

        mNavBar.getItemIdentifierView(R.id.action_friendslist).setNumberNews(
                count.getFriendsRequestsCount());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Parent level
        super.onActivityResult(requestCode, resultCode, data);

        //Check if the request was to search a user
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {

                //User search
                case MAIN_ACTIVITY_SEARCH_USER_INTENT:
                    assert data.getData() != null;
                    openUserPage(Integer.decode(
                            Objects.requireNonNull(
                                    data.getData().getQueryParameter("userID"))));
                    break;


                //Global search result
                case MAIN_ACTIVITY_GLOBAL_SEARCH_INTENT:
                    assert data.getData() != null;

                    //Open user page or group accordingly to the choice of the user
                    Uri result = data.getData();
                    String type = result.getQueryParameter(SearchActivity.INTENT_ARG_RESULT_TYPE);
                    int id = Integer.decode(Objects.requireNonNull(result.getQueryParameter(SearchActivity.INTENT_ARG_RESULT_ID)));
                    if(Objects.equals(type, SearchActivity.INTENT_RESULT_USER))
                        openUserPage(id);
                    else
                        onOpenGroup(id);

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
     * Go backward in the application
     *
     * @param activity Activity object
     */
    public static void goBackward(@NonNull Activity activity){
        if(!(activity instanceof MainActivity))
            throw new RuntimeException("Called goBackward using an Activity different " +
                    "from MainActivity!");

        ((MainActivity)activity).getSupportFragmentManager().popBackStack();
    }


    /**
     * Follow a tag to open it
     *
     * @param activity Activity object
     * @param tag The tag to follow
     */
    public static void FollowTag(@NonNull Activity activity, String tag){
        if(!(activity instanceof MainActivity))
            throw new RuntimeException("Need a MainActivity!");

        ((MainActivity)activity).followTag(tag);
    }

    /**
     * Unset FindVirtualDirectoryTask callback in order to prevent memory leaks
     */
    private void unsetFindVirtualDirectoryTask(){
        if(mFindVirtualDirectoryTask != null)
            mFindVirtualDirectoryTask.setOnPostExecuteListener(null);
    }

    /**
     * Follow a tag to open it
     *
     * @param tag The tag to find
     */
    public void followTag(String tag){
        final AlertDialog dialog = UiUtils.create_loading_dialog(this);

        unsetFindVirtualDirectoryTask();
        mFindVirtualDirectoryTask = new FindVirtualDirectoryTask(this);
        mFindVirtualDirectoryTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<VirtualDirectory>() {
            @Override
            public void OnPostExecute(VirtualDirectory virtualDirectory) {
                dialog.dismiss();
                openDirectory(virtualDirectory);
            }
        });
        mFindVirtualDirectoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tag);

    }

    /**
     * Open a directory
     *
     * @param directory The directory to open
     */
    public void openDirectory(@Nullable VirtualDirectory directory){
        if(directory == null){
            Toast.makeText(this, R.string.err_find_tag, Toast.LENGTH_SHORT).show();
            return;
        }

        if (directory.getKind() == VirtualDirectoryType.GROUP){
            onOpenGroup(directory.getId());
        }

        else if(directory.getKind() == VirtualDirectoryType.USER){
            openUserPage(directory.getId());
        }
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
     * Open account settings
     */
    void openAccountSettings(){
        startActivity(new Intent(this, AccountSettingsActivity.class));
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
     *
     * @param addToBackStack Specify whether the change must
     *                       be added to backstack or not
     */
    void openNotificationsFragment(boolean addToBackStack) {
        NotificationsFragment notifications = new NotificationsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, notifications);
        if(addToBackStack)
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
     * Open user groups
     */
    public void openUserGroups(){
        UserGroupsFragment userGroupsFragment = new UserGroupsFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, userGroupsFragment);
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
        startActivityForResult(intent, MAIN_ACTIVITY_SEARCH_USER_INTENT);

    }

    /**
     * Perform a global search
     */
    private void searchGlobal(){

        //Make intent
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, MAIN_ACTIVITY_GLOBAL_SEARCH_INTENT);

    }

    /**
     * Open group page
     *
     * @param groupID The ID of the group to open
     */
    @Override
    public void onOpenGroup(int groupID) {
        //Specify fragment arguments
        Bundle args = new Bundle();
        args.putInt(GroupPageMainFragment.ARGUMENT_GROUP_ID, groupID);

        //Initialize fragment
        GroupPageMainFragment fragment = new GroupPageMainFragment();
        fragment.setArguments(args);

        //Perform fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onOpenGroupAccessDenied(int groupID) {

        //Add arguments
        Bundle args = new Bundle();
        args.putInt(GroupAccessDeniedFragment.ARGUMENT_GROUP_ID, groupID);

        //Create fragment
        Fragment fragment = new GroupAccessDeniedFragment();
        fragment.setArguments(args);

        //Remove last transaction from list
        getSupportFragmentManager().popBackStackImmediate();

        //Perform fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void createCallForConversation(int convID) {
        final Dialog dialog = UiUtils.create_loading_dialog(this);

        //Create the call for the conversation
        CreateCallForConversationTask task = new CreateCallForConversationTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, convID);
        getTasksManager().addTask(task, true);
        task.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<CallInformation>() {
            @Override
            public void OnPostExecute(@Nullable CallInformation callInformation) {

                dialog.dismiss();

                //Check for errors
                if(callInformation == null)
                    Toast.makeText(
                            MainActivity.this,
                            R.string.err_create_call_for_conversation,
                            Toast.LENGTH_SHORT).show();

                else
                    //Open call
                    openCall(callInformation.getId());
            }
        });
    }

    @Override
    public void openCall(int callID) {

        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra(CallActivity.ARGUMENT_CALL_ID, callID);
        startActivity(intent);

    }
}
