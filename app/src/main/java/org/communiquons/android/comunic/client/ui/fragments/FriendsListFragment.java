package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.Friend;
import org.communiquons.android.comunic.client.data.models.FriendUser;
import org.communiquons.android.comunic.client.ui.adapters.FriendsAdapter;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.utils.FriendsUtils;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;
import org.communiquons.android.comunic.client.ui.listeners.openConversationListener;

import java.util.ArrayList;

/**
 * Friends list fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/11/17.
 */

public class FriendsListFragment extends Fragment
    implements AdapterView.OnItemClickListener{

    /**
     * Debug tag
     */
    private static final String TAG = "FriendsListFragment";

    /**
     * Application context
     */
    private Context mContext;

    /**
     * Database helper
     */
    private DatabaseHelper mDbHelper;

    /**
     * Get user helper
     */
    private GetUsersHelper usersHelper;

    /**
     * The current list of friends
     */
    private ArrayList<FriendUser> friendsList;

    /**
     * Friend list operations object
     */
    private FriendsListHelper flistHelper;

    /**
     * Conversation opener
     */
    private openConversationListener convOpener;

    /**
     * Users page opener
     */
    private onOpenUsersPageListener usersPageOpener;

    /**
     * Friend adapter
     */
    private FriendsAdapter fAdapter;

    /**
     * Loading progress bar
     */
    private ProgressBar mProgressBar;

    /**
     * No friend notice
     */
    private TextView mNoFriendNotice;

    /**
     * Friends list view
     */
    private ListView mFriendsListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Save application context
        mContext = getActivity().getApplicationContext();

        //Create database helper
        mDbHelper = DatabaseHelper.getInstance(mContext);

        //Create friend list helper object
        flistHelper = new FriendsListHelper(mDbHelper, mContext);

        //Create get user helper
        usersHelper = new GetUsersHelper(mContext, mDbHelper);

        //Cast activity to convOpener
        try {
            convOpener = (openConversationListener) getActivity();
            usersPageOpener = (onOpenUsersPageListener) getActivity();
        } catch (ClassCastException e){
            e.printStackTrace();

            throw new RuntimeException(getActivity().getClass().getName() + " must implement" +
                    "ConversationsListHelper.openConversationListener " +
                    "and GetUsersHelper.onOpenUsersPageListener !");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friendslist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get loading progress bar
        mProgressBar = view.findViewById(R.id.fragment_friendslist_progressbar);

        //Get the no friend notice
        mNoFriendNotice = view.findViewById(R.id.no_friend_notice);
        mNoFriendNotice.setVisibility(View.GONE);

        //Get friends listview
        mFriendsListView = view.findViewById(R.id.fragment_friendslist_listview);

        //Retain the fragment
        //setRetainInstance(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        //Update the title of the application
        getActivity().setTitle(R.string.fragment_friendslist_title);
        MainActivity.SetNavbarSelectedOption(getActivity(), R.id.action_friendslist);

        //Refresh the friends list
        refresh_friend_list();
    }

    /**
     * Refresh the friend list
     */
    void refresh_friend_list(){

        //Display loading bar
        display_progress_bar(true);

        new AsyncTask<Void, Void, ArrayList<FriendUser>>(){

            @Override
            protected ArrayList<FriendUser> doInBackground(Void... params) {

                //Fetch the list of friends
                ArrayList<Friend> friendsList = flistHelper.get();

                //Check for errors
                if(friendsList == null)
                    return null;

                //Get user infos
                ArrayMap<Integer, UserInfo> userInfos = usersHelper.getMultiple(
                        FriendsUtils.getFriendsIDs(friendsList));

                //Check for errors
                if(userInfos == null)
                    return null;

                //Merge friend and user and return result
                return FriendsUtils.merge_friends_user_infos_list(friendsList, userInfos);

            }

            @Override
            protected void onPostExecute(ArrayList<FriendUser> friendUsers) {

                //Check the activity still exists
                if(getActivity() == null)
                    return;

                apply_friends_list(friendUsers);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Set the the list of friend attached to the list adapter
     *
     * @param friendsList The friends list to apply
     */
    private void apply_friends_list(@Nullable ArrayList<FriendUser> friendsList){

        //Remove progress bar
        display_progress_bar(false);

        //Check for errors
        if(friendsList == null){
            Toast.makeText(mContext, R.string.fragment_friendslist_err_refresh,
                    Toast.LENGTH_LONG).show();
            return;
        }

        //Save the list of friends
        this.friendsList = friendsList;

        //Update the visibility of the no friend notice
        updateNoFriendNoticeVisibility();

        //Set the adapter
        fAdapter = new FriendsAdapter(this, getActivity(), friendsList);
        mFriendsListView.setAdapter(fAdapter);

        //Register the view for the context menu
        registerForContextMenu(mFriendsListView);

        mFriendsListView.setOnItemClickListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_fragment_friendslist_item, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Get the friend position in the list
        int friendPos = info.position;

        switch (item.getItemId()){

            //To open a private conversation with the friend
            case R.id.menu_fragment_friendslist_private_conversation:
                convOpener.openPrivateConversation(friendsList.get(friendPos).getFriend().getId());
                return true;

            //To delete the friend
            case R.id.menu_fragment_friendslist_delete_friend:
                delete_friend(friendPos);
                return true;
        }

        //If it is not for us, it is for someone else
        return super.onContextItemSelected(item);
    }

    /**
     * Ask user to confirm friend deletion, the friend is specified by its position in the list
     *
     * @param pos the position of the friend to delete
     */
    private void delete_friend(final int pos){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.popup_deletefriend_title)
                .setMessage(R.string.popup_deletefriend_message);

        builder.setPositiveButton(R.string.popup_deletefriend_button_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Get the friend to delete
                final Friend toDelete = friendsList.get(pos).getFriend();

                //Apply new list version
                friendsList.remove(pos);
                fAdapter.notifyDataSetChanged();

                //Remove the friend list on a parallel thread
                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer[] params) {

                        //Delete the friend from the list
                        flistHelper.remove(toDelete);

                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pos);
            }
        });
        builder.setNegativeButton(R.string.popup_deletefriend_button_cancel, null);
        builder.show();


    }

    /**
     * Show a popup to offer the user to respond to a friendship request
     *
     * @param pos The position of the friend in the list
     */
    public void showPopupRequestResponse(final int pos){

        new AlertDialog.Builder(getActivity())

                .setTitle(R.string.popup_respond_friendship_request_title)
                .setMessage(R.string.popup_respond_friendship_request_message)

                .setNegativeButton(R.string.action_friends_deny_request, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        respondRequest(pos, false);
                    }
                })

                .setPositiveButton(R.string.action_friends_accept_request, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        respondRequest(pos, true);
                    }
                })

                .show();

    }

    /**
     * Respond to a friendship request
     *
     * @param pos The position of the friend respond the request
     * @param accept Specify wether the user accepted the request or not
     */
    private void respondRequest(int pos, final boolean accept){

        //Get the Friend object
        Friend targetFriend = friendsList.get(pos).getFriend();

        if(accept)
            //Mark the friend as accepted
            targetFriend.setAccepted(true);
        else
            //Remove the friend from the list
            friendsList.remove(pos);

        //Inform the adapter the list has changed
        fAdapter.notifyDataSetChanged();

        //Accept the request on a separate thread
        new AsyncTask<Friend, Void, Void>(){
            @Override
            protected Void doInBackground(Friend... params) {
                flistHelper.respondRequest(params[0], accept);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetFriend);
    }

    /**
     * Hide (or display) progress bar
     *
     * @param display Specify whether the loading bar has to be shown or not
     */
    private void display_progress_bar(boolean display){
        mProgressBar.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    /**
     * Update the visibility of the no friend notice
     */
    private void updateNoFriendNoticeVisibility(){
        if(friendsList != null)
            mNoFriendNotice.setVisibility(friendsList.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Get clicked user ID
        int userID = friendsList.get(position).get_user_id();

        //Open user page
        usersPageOpener.openUserPage(userID);
    }
}
