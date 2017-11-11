package org.communiquons.android.comunic.client.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.communiquons.android.comunic.client.R;

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

        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friendslist, container, false);

    }
}
