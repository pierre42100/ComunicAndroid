package org.communiquons.android.comunic.client.ui.fragments;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.asynctasks.GetLatestPostsTask;

/**
 * Latest posts fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 5/10/18.
 */

public class LatestPostsFragment extends AbstractPostsListFragment {

    /**
     * Debug tag
     */
    private static final String TAG = "LatestPostsFragment";

    @Override
    public void onResume() {
        super.onResume();

        //Update dock and activity title
        getActivity().setTitle(R.string.fragment_latestposts_title);
        MainActivity.SetNavbarSelectedOption(getActivity(), R.id.action_latest_posts);
    }

    @Override
    public void onStop() {
        super.onStop();
        getTasksManager().unsetAllTasks();
    }

    /**
     * Check whether are being loaded now or not
     *
     * @return TRUE if some posts are loading / FALSE else
     */
    private boolean is_loading_posts() {

        SafeAsyncTask task = getTasksManager().getTask(GetLatestPostsTask.class);

        return task != null &&
                task.hasOnPostExecuteListener() &&
                !task.isCancelled() &&
                task.getStatus() != AsyncTask.Status.FINISHED;

    }

    @Override
    public void onLoadPosts() {
        //Get the list of latest posts
        getTasksManager().unsetSpecificTasks(GetLatestPostsTask.class);
        GetLatestPostsTask getLatestPostsTask = new GetLatestPostsTask(getActivity());
        getLatestPostsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                onGotNewPosts(posts);
            }
        });
        getLatestPostsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getTasksManager().addTask(getLatestPostsTask, true);
    }

    @Override
    public void onLoadMorePosts(int last_post_id) {

        //Check if post loading is already locked
        if(is_loading_posts())
            return;

        if(!hasPostsList())
            return;

        if(getPostsList().size() == 0)
            return;

        setProgressBarVisibility(true);

        //Get the ID of the oldest post to start from
        int start = last_post_id - 1;

        //Get older posts
        GetLatestPostsTask getLastestPostsTask = new GetLatestPostsTask(getActivity());
        getLastestPostsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                onGotNewPosts(posts);
            }
        });
        getLastestPostsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, start);
        getTasksManager().addTask(getLastestPostsTask, true);
    }

    @Override
    protected void onGotNewPosts(@Nullable PostsList list) {

        setProgressBarVisibility(false);

        //Check for errors
        if (list == null) {
            Toast.makeText(getActivity(), R.string.err_get_latest_posts, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!list.hasUsersInfo()) {
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        super.onGotNewPosts(list);
    }

}
