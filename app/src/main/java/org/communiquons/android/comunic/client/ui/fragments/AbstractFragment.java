package org.communiquons.android.comunic.client.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTasksManager;

/**
 * Base fragment for the fragments of the application
 *
 * Integrates task manager
 *
 * @author Pierre HUBERT
 */
public abstract class AbstractFragment extends Fragment {

    /**
     * Tasks manager
     */
    private SafeAsyncTasksManager mTasksManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTasksManager = new SafeAsyncTasksManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getTasksManager().unsetAllTasks();
    }

    /**
     * Get the task manager associated with the fragment
     *
     * @return The task manager
     */
    public SafeAsyncTasksManager getTasksManager() {
        return mTasksManager;
    }
}
