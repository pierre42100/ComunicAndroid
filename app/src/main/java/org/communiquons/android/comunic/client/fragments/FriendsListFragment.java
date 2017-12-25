package org.communiquons.android.comunic.client.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
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
import android.widget.Toast;

import org.communiquons.android.comunic.client.MainActivity;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersInfos;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.friendsList.Friend;
import org.communiquons.android.comunic.client.data.friendsList.FriendUser;
import org.communiquons.android.comunic.client.data.friendsList.FriendsAdapter;
import org.communiquons.android.comunic.client.data.friendsList.FriendsList;
import org.communiquons.android.comunic.client.data.friendsList.FriendsUtils;
import org.communiquons.android.comunic.client.data.friendsList.GetFriendsListTask;

import java.util.ArrayList;

/**
 * Friends list fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/11/17.
 */

public class FriendsListFragment extends Fragment {

    /**
     * Debug tag
     */
    private String TAG = "FriendsListFragment";

    /**
     * The root view of the fragment
     */
    View rootView;

    /**
     * Application context
     */
    Context mContext;

    /**
     * Database helper
     */
    DatabaseHelper mDbHelper;

    /**
     * The current list of friends
     */
    ArrayList<FriendUser> friendsList;

    /**
     * Friend list operations object
     */
    FriendsList flist;

    /**
     * Friend adapter
     */
    private FriendsAdapter fAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friendslist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        //Save application context
        mContext = getActivity().getApplicationContext();

        //Create database helper
        mDbHelper = new DatabaseHelper(mContext);

        //Create friendlist operation object
        flist = new FriendsList(mDbHelper, mContext);

        //Retain the fragment
        //setRetainInstance(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        //Update the title of the application
        getActivity().setTitle(R.string.fragment_friendslist_title);

        //Update the bottom navigation menu
        ((MainActivity) getActivity())
                .setSelectedNavigationItem(R.id.main_bottom_navigation_friends_list);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Refresh the friends list
        refresh_friend_list();
    }

    /**
     * Refresh the friend list
     */
    void refresh_friend_list(){

        //Display loading bar
        display_progress_bar(true);

        new GetFriendsListTask(mDbHelper){

            @Override
            protected void onPostExecute(final ArrayList<Friend> friendsList) {

                //Remote progress bar
                display_progress_bar(false);

                //Check for errors
                if(friendsList == null){
                    Toast.makeText(mContext, R.string.fragment_friendslist_err_refresh,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                new GetUsersInfos(mContext, mDbHelper).
                        getMultiple(FriendsUtils.getFriendsIDs(friendsList), new GetUsersInfos.getMultipleUserInfosCallback() {
                            @Override
                            public void callback(ArrayMap<Integer, UserInfo> info) {
                                //Check for errors
                                if (info == null) {
                                    Toast.makeText(mContext, R.string.fragment_friendslist_err_get_userinfos,
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //Merge the user informations list and friends List into FriendInfo list
                                ArrayList<FriendUser> friendsUserList = FriendsUtils.merge_friends_user_infos_list(
                                        friendsList,
                                        info
                                );

                                //Refresh friends list
                                apply_friends_list(friendsUserList);
                            }
                        });

            }

        }.execute();
    }

    /**
     * Set the the list of friend attached to the list adapter
     *
     * @param friendsList The friends list to apply
     */
    private void apply_friends_list(final ArrayList<FriendUser> friendsList){

        //Save the list of friends
        this.friendsList = friendsList;

        //Set the adapter
        fAdapter = new FriendsAdapter(this, getActivity(), friendsList);
        ListView listView = rootView.findViewById(R.id.fragment_friendslist_listview);
        listView.setAdapter(fAdapter);

        //Register the view for the context menu
        registerForContextMenu(listView);
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
                        flist.remove(toDelete);

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
                flist.respondRequest(params[0], accept);
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

        //Get the view
        rootView.findViewById(R.id.fragment_friendslist_progressbar).setVisibility(
                display ? View.VISIBLE : View.GONE
        );


    }
}
