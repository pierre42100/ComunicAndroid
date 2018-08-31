package org.communiquons.android.comunic.client.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.asynctasks.GetLatestPostsTask;
import org.communiquons.android.comunic.client.ui.listeners.OnPostListFragmentsUpdateListener;

/**
 * Latest posts fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 5/10/18.
 */

public class LatestPostsFragment extends Fragment
        implements OnPostListFragmentsUpdateListener {

    /**
     * Debug tag
     */
    private static final String TAG = "LatestPostsFragment";

    /**
     * The list of posts
     */
    PostsList mPostsList;

    /**
     * Fragment that displays the list of posts
     */
    private PostsListFragment mPostsListFragment;

    /**
     * Load posts task
     */
    private GetLatestPostsTask mGetLatestPostsTask;

    /**
     * Loading progress bar
     */
    ProgressBar mProgressBar;

    /**
     * No posts notice
     */
    TextView mNoPostNotice;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_latest_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get some of the views
        mProgressBar = view.findViewById(R.id.loading_progress);
        mNoPostNotice = view.findViewById(R.id.no_post_notice);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update dock and activity title
        getActivity().setTitle(R.string.fragment_latestposts_title);
        MainActivity.SetNavbarSelectedOption(getActivity(), R.id.action_latest_posts);

        //Refresh the list of posts of the user
        if(mPostsList == null)
            refresh_posts_list();
        else {
            mPostsListFragment = null;
            show_posts_list();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        unset_all_load_tasks();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        unset_all_load_tasks();
    }

    /**
     * Unset all pending load tasks
     */
    private void unset_all_load_tasks(){
        if(mGetLatestPostsTask != null)
            mGetLatestPostsTask.setOnPostExecuteListener(null);
    }

    /**
     * Check whether are being loaded now or not
     *
     * @return TRUE if some posts are loading / FALSE else
     */
    private boolean is_loading_posts() {

        return mGetLatestPostsTask != null &&
                mGetLatestPostsTask.hasOnPostExecuteListener() &&
                !mGetLatestPostsTask.isCancelled() &&
                mGetLatestPostsTask.getStatus() != AsyncTask.Status.FINISHED;

    }

    /**
     * Refresh the list of posts of the user
     */
    private void refresh_posts_list(){

        //Show progress bar / hide no posts notice
        toggleLoadingBarVisibility(true);
        toggleNoPostNoticeVisibility(false);

        //Get the list of latest posts
        unset_all_load_tasks();
        mGetLatestPostsTask = new GetLatestPostsTask(getActivity());
        mGetLatestPostsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                on_got_new_posts_list(posts);
            }
        });
        mGetLatestPostsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
    }

    /**
     * This function is called when we have got a new post list
     *
     * @param list The new list of posts
     */
    private void on_got_new_posts_list(@Nullable PostsList list) {

        //Hide loading bar
        toggleLoadingBarVisibility(false);

        //Check for errors
        if (list == null) {
            Toast.makeText(getActivity(), R.string.err_get_latest_posts, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!list.hasUsersInfo()) {
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Save the list of posts
        if (mPostsList == null)
            mPostsList = list;
        else {
            mPostsList.addAll(list);
            assert mPostsList.getUsersInfo() != null;
            mPostsList.getUsersInfo().putAll(list.getUsersInfo());
        }

        show_posts_list();
    }

    /**
     * Show posts list
     */
    private void show_posts_list(){

        //Hide loading bar
        toggleLoadingBarVisibility(false);

        if(mPostsListFragment == null){

            //Apply the post fragment
            mPostsListFragment = new PostsListFragment();
            mPostsListFragment.setPostsList(mPostsList);
            mPostsListFragment.setOnPostListFragmentsUpdateListener(this);

            //Create and commit a transaction
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.posts_list_target, mPostsListFragment);
            transaction.commit();
        }
        else
            //Append the new posts list
            mPostsListFragment.show();

        //Check if the posts list is empty
        toggleNoPostNoticeVisibility(mPostsList.size() == 0);
    }

    @Override
    public void onLoadMorePosts() {

        //Check if post loading is already locked
        if(is_loading_posts())
            return;

        if(mPostsList == null)
            return;

        if(mPostsList.size() == 0)
            return;

        //Display loading bar
        toggleLoadingBarVisibility(true);

        //Get the ID of the oldest post to start from
        int start = mPostsList.get(mPostsList.size()-1).getId() - 1;

        //Get older posts
        mGetLatestPostsTask = new GetLatestPostsTask(getActivity());
        mGetLatestPostsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                on_got_new_posts_list(posts);
            }
        });
        mGetLatestPostsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, start);
    }

    /**
     * Toggle progress bar visibility
     *
     * @param visible Specify whether the progress bar should be visible or not
     */
    private void toggleLoadingBarVisibility(boolean visible){
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Toggle no post notice visibility
     *
     * @param visible The visibility of the no post notice
     */
    private void toggleNoPostNoticeVisibility(boolean visible){
        mNoPostNotice.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
