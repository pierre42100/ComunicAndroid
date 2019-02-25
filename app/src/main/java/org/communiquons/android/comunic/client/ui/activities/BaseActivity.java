package org.communiquons.android.comunic.client.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    /**
     * Current active class
     */
    @Nullable
    private static String mActiveActivityName = null;

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

    @Override
    protected void onStart() {
        super.onStart();

        mActiveActivityName = getClass().getSimpleName();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if(getClass().getSimpleName().equals(mActiveActivityName))
            mActiveActivityName = null;
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

    /**
     * Check out whether an activity is the active activity or not
     *
     * @param activity The activity to check
     * @return True if the activity is the active one / false else
     */
    public static boolean IsActiveActivity(Class<? extends BaseActivity> activity){
        return activity.getSimpleName().equals(mActiveActivityName);
    }
}
