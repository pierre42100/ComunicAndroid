package org.communiquons.android.comunic.client.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import org.communiquons.android.comunic.client.ui.fragments.accountsettings.AccountSettingsMainFragment;

/**
 * Account settings activity
 *
 * @author Pierre HUBERT
 */
public class AccountSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Open main fragment if required
        if(savedInstanceState == null){
            AccountSettingsMainFragment fragment = new AccountSettingsMainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, fragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //To go back
        if(item.getItemId() == android.R.id.home){
            if(getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
            else
                finish();
        }

        return super.onContextItemSelected(item);
    }
}
