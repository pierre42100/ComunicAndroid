package org.communiquons.android.comunic.client.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;

/**
 * Notifications fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/1/18.
 */

public class NotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update the title of the application
        getActivity().setTitle(R.string.fragment_notifications_title);

        //Update the bottom navigation menu
        ((MainActivity) getActivity())
                .setSelectedNavigationItem(R.id.main_bottom_navigation_notif);
    }
}
