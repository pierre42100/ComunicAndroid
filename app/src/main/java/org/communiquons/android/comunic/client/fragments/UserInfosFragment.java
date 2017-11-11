package org.communiquons.android.comunic.client.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.communiquons.android.comunic.client.R;

/**
 * User informations fragment
 *
 * This fragment display informations about the user
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/11/17.
 */

public class UserInfosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_userinfos, container, false);
    }
}
