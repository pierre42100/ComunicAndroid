package org.communiquons.android.comunic.client.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.R;

/**
 * Application preferences fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/8/18.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.app_preferences);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update the title
        getActivity().setTitle(R.string.fragment_settings_title);

    }
}
