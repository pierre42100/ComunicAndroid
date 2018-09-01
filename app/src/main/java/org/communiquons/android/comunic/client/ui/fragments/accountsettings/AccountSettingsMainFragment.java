package org.communiquons.android.comunic.client.ui.fragments.accountsettings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.communiquons.android.comunic.client.R;

/**
 * Main account preference fragment
 */
public class AccountSettingsMainFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener {

    private static final String PREFERENCE_CATEGORY_ACCOUNT_IMAGE = "preference_category_account_image";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.account_preference_main);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.findPreference(PREFERENCE_CATEGORY_ACCOUNT_IMAGE).setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.activity_account_settings_label);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        Fragment fragment;

        switch (preference.getKey()){

            case PREFERENCE_CATEGORY_ACCOUNT_IMAGE:
                fragment = new AccountImageSettingsFragment();
                break;

            default:
                throw new AssertionError();
        }

        if(getActivity() == null) return false;

        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return true;
    }
}
