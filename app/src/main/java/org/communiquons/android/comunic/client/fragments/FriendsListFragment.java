package org.communiquons.android.comunic.client.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersInfos;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.friendsList.Friend;
import org.communiquons.android.comunic.client.data.friendsList.FriendUser;
import org.communiquons.android.comunic.client.data.friendsList.FriendsAdapter;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        //Save application context
        mContext = getActivity().getApplicationContext();

        //Create database helper
        mDbHelper = new DatabaseHelper(mContext);

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
            protected void onPostExecute(ArrayList<Friend> friendsList) {

                //Remote progress bar
                display_progress_bar(false);

                //Check for errors
                if(friendsList == null){
                    Toast.makeText(mContext, R.string.fragment_friendslist_err_refresh,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                else
                    //Update the friends list
                    update_friends_list(friendsList);
            }

        }.execute();
    }

    /**
     * Set the the list of friend attached to the list adapter
     *
     * @param friendsList The friends list to apply
     */
    private void update_friends_list(final ArrayList<Friend> friendsList){

        //Get user informations
        new GetUsersInfos(mContext, mDbHelper).
                getMultiple(FriendsUtils.getFriendsIDs(friendsList), new GetUsersInfos.getMultipleUserInfosCallback() {
                    @Override
                    public void callback(ArrayMap<Integer, UserInfo> info) {
                        //Check for errors
                        if(info == null){
                            Toast.makeText(mContext, R.string.fragment_friendslist_err_get_userinfos,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //Merge the user informations list and friends List into FriendInfo list
                        ArrayList<FriendUser> friendsUserList = FriendsUtils.merge_friends_user_infos_list(
                                friendsList,
                                info
                        );

                        //Set the adapter
                        FriendsAdapter friendsAdapter = new FriendsAdapter(getActivity(), friendsUserList);
                        ListView listView = rootView.findViewById(R.id.fragment_friendslist_listview);
                        listView.setAdapter(friendsAdapter);
                    }
                });

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
