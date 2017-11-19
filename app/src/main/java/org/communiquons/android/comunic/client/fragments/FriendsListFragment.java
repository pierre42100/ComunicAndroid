package org.communiquons.android.comunic.client.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        //Save application context
        mContext = getActivity().getApplicationContext();

        //Create database helper
        mDbHelper = new DatabaseHelper(mContext);

        //Create friendlist operation object
        flist = new FriendsList(mDbHelper, mContext);

        //Retain the fragment
        //setRetainInstance(true);

        //Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_friendslist, container, false);
        return rootView;

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
                else {
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
        FriendsAdapter friendsAdapter = new FriendsAdapter(getActivity(), friendsList);
        ListView listView = rootView.findViewById(R.id.fragment_friendslist_listview);
        listView.setAdapter(friendsAdapter);

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

                //Delete the friend from the list
                flist.remove(friendsList.get(pos).getFriend());

                //Refresh the current friend list
                refresh_friend_list();
            }
        });
        builder.setNegativeButton(R.string.popup_deletefriend_button_cancel, null);
        builder.show();


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
