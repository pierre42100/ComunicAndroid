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

        final ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        new GetUsersInfos(getActivity(), new DatabaseHelper(getActivity())).getMultiple(list, new GetUsersInfos.getMultipleUserInfosCallback() {
            @Override
            public void callback(ArrayMap<Integer, UserInfo> info) {
                Log.v("FriendsListFragment", "User infos callback");
                if(info == null)
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();

                for(int ID : list){
                    UserInfo infos = info.get(ID);

                    if(infos == null)
                        Log.e("FriendsListFragment", "Error with user infos for ID " + ID);
                    else
                        Log.v("FriendsListFragment", ID + " is " + infos.getFullName() + " !");
                }
            }
        });
    }
}
