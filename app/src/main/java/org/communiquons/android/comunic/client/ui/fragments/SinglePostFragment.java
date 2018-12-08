package org.communiquons.android.comunic.client.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.asynctasks.GetSinglePostTask;

/**
 * Single post fragment
 *
 * This fragment allows to display a single post
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/12/18.
 */

public class SinglePostFragment extends AbstractPostsListFragment {

    /**
     * The name of the argument that contains the ID of the post to open
     */
    public static final String ARGUMENT_POST_ID = "post_id";

    /**
     * The ID of the post
     */
    private int mPostID = 0;

    /**
     * Get single post task
     */
    private GetSinglePostTask mGetSinglePostTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get post ID
        assert getArguments() != null;
        mPostID = getArguments().getInt(ARGUMENT_POST_ID);

    }

    @Override
    public void onStop() {
        super.onStop();
        unset_all_tasks();
    }

    public void unset_all_tasks(){
        if(mGetSinglePostTask != null)
            mGetSinglePostTask.setOnPostExecuteListener(null);
    }

    @Override
    public void onLoadPosts() {
        //Perform the request in the background
        unset_all_tasks();

        mGetSinglePostTask = new GetSinglePostTask(getActivity());
        mGetSinglePostTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                onGotNewPosts(posts);
            }
        });
        mGetSinglePostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostID);
        getTasksManager().addTask(mGetSinglePostTask);
    }

    @Override
    public void onLoadMorePosts(int last_post_id) {

    }

    @Override
    protected void onGotNewPosts(@Nullable PostsList list) {

        setProgressBarVisibility(false);

        if (list == null) {
            Toast.makeText(getActivity(), R.string.err_get_post_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check if we could not get user information
        if (!list.hasUsersInfo()) {
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        super.onGotNewPosts(list);
    }

}
