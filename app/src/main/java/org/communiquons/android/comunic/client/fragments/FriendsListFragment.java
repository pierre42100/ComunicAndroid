package org.communiquons.android.comunic.client.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersInfos;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.friendsList.Friend;
import org.communiquons.android.comunic.client.data.friendsList.GetFriendsListTask;

import java.util.ArrayList;

/**
 * Friends list fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/11/17.
 */

public class FriendsListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        //Retain the fragment
        //setRetainInstance(true);

        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friendslist, container, false);

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
        new GetFriendsListTask(getActivity().getApplicationContext()){

            @Override
            protected void onPostExecute(ArrayList<Friend> friendsList) {

                //Display informations in the console
                for(Friend friend : friendsList){
                    Log.v("FriendsListFragment", "Friend: " + friend.getId() + " " +
                            (friend.isAccepted() ? 1 : 0 ) + " " + (friend.isFollowing() ? 1 : 0) +
                                    " " + friend.getLast_activity() + " "
                            );
                }

            }

        }.execute();
    }
}
