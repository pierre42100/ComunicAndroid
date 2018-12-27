package org.communiquons.android.comunic.client.ui.activities;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTasksManager;

/**
 * Base application activity
 *
 * @author Pierre HUBERT
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Tasks manager
     */
    private SafeAsyncTasksManager mSafeAsyncTasksManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize task manager
        mSafeAsyncTasksManager = new SafeAsyncTasksManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Unset all task
        mSafeAsyncTasksManager.unsetAllTasks();
    }

    @NonNull
    @Override
    public ActionBar getSupportActionBar() {
        assert super.getSupportActionBar() != null;
        return super.getSupportActionBar();
    }

    /**
     * Get tasks manager associated with this activity
     *
     * @return Task manager associated to this activity
     */
    public SafeAsyncTasksManager getTasksManager() {
        return mSafeAsyncTasksManager;
    }
}
