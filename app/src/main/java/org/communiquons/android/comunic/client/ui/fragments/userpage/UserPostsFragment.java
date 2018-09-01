package org.communiquons.android.comunic.client.ui.fragments.userpage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.asynctasks.LoadUserPostsTask;
import org.communiquons.android.comunic.client.ui.fragments.AbstractPostsListFragment;
import org.communiquons.android.comunic.client.ui.fragments.PostsCreateFormFragment;

/**
 * User posts fragment
 *
 * @author Pierre HUBERT
 */
public class UserPostsFragment extends AbstractPostsListFragment {

    /**
     * Bundle arguments
     */
    public static final String ARGUMENT_USER_ID = "user_id";
    public static final String ARGUMENT_CAN_POST_TEXT = "can_post_text";

    /**
     * The ID of the user
     */
    private int mUserID;

    /**
     * Specify whether the user is allowed to create posts on user page or not
     */
    private boolean mCanPostsText;

    /**
     * Load user posts task
     */
    private LoadUserPostsTask mLoadUserPostsTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDisplayPostsTarget(false);

        //Get arguments
        Bundle bundle = getArguments();
        assert bundle != null;

        mUserID = bundle.getInt(ARGUMENT_USER_ID);
        mCanPostsText = bundle.getBoolean(ARGUMENT_CAN_POST_TEXT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Add create post fragment, if possible
        enablePostFormFragment(mCanPostsText);
        if(mCanPostsText)
            init_create_post_fragment(PostsCreateFormFragment.PAGE_TYPE_USER, mUserID);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        cancel_load_task();
    }

    private void cancel_load_task(){
        if(mLoadUserPostsTask != null)
            mLoadUserPostsTask.setOnPostExecuteListener(null);
    }

    /**
     * Check whether some posts are loading or not
     *
     * @return TRUE or FALSE
     */
    private boolean is_loading_posts(){
        return  mLoadUserPostsTask != null
                && !mLoadUserPostsTask.isCancelled()
                && mLoadUserPostsTask.hasOnPostExecuteListener()
                && mLoadUserPostsTask.getStatus() != AsyncTask.Status.FINISHED;
    }


    @Override
    public void onLoadPosts() {
        cancel_load_task();

        mLoadUserPostsTask = new LoadUserPostsTask(mUserID, getActivity());
        mLoadUserPostsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                if(getActivity() == null)
                    return;

                onGotNewPosts(posts);
            }
        });
        mLoadUserPostsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onLoadMorePosts(int last_post_id) {

        if(is_loading_posts())
            return;

        setProgressBarVisibility(true);

        mLoadUserPostsTask = new LoadUserPostsTask(mUserID, getActivity());
        mLoadUserPostsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                onGotNewPosts(posts);
            }
        });
        mLoadUserPostsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                last_post_id -1);
    }


    @Override
    protected void onGotNewPosts(@Nullable PostsList list) {

        //Check for errors
        if(list == null){
            Toast.makeText(getActivity(), R.string.err_get_user_posts, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check we didn't get user information
        if(!list.hasUsersInfo()){
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        super.onGotNewPosts(list);
    }


}
