package org.communiquons.android.comunic.client.ui.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Base application activity
 *
 * @author Pierre HUBERT
 */
public abstract class BaseActivity extends AppCompatActivity {

    @NonNull
    @Override
    public ActionBar getSupportActionBar() {
        assert super.getSupportActionBar() != null;
        return super.getSupportActionBar();
    }
}
