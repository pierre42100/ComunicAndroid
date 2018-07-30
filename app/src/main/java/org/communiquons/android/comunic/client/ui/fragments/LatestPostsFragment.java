package org.communiquons.android.comunic.client.ui.fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
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
     * Posts helper
     */
    PostsHelper mPostsHelper;

    /**
     * User information helper
     */
    GetUsersHelper mUserHelper;

    /**
     * The list of posts
     */
    PostsList mPostsList;

    /**
     * Information about the related users
     */
    ArrayMap<Integer, UserInfo> mUserInfo;

    /**
     * Fragment that displays the list of posts
     */
    private PostsListFragment mPostsListFragment;

    /**
     * Loading progress bar
     */
    ProgressBar mProgressBar;

    /**
     * No posts notice
     */
    TextView mNoPostNotice;

    /**
     * Posts load lock
     */
    private boolean mLoadPostsLock = false;

    @Override
    public void onStart() {
        super.onStart();

        //Create posts helper
        mPostsHelper = new PostsHelper(getActivity());

        //Create user helper
        mUserHelper = new GetUsersHelper(getActivity());
    }

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

        //Update the bottom navigation menu
        ((MainActivity) getActivity())
                .setSelectedNavigationItem(R.id.main_bottom_navigation_me_view);

        //Refresh the list of posts of the user
        refresh_posts_list();
    }

    /**
     * Refresh the list of posts of the user
     */
    private void refresh_posts_list(){

        //Show progress bar / hide no posts notice
        toggleLoadingBarVisibility(true);
        toggleNoPostNoticeVisibility(false);

        //Get the list of latest posts
        new AsyncTask<Void, Void, PostsList>(){
            @Override
            protected PostsList doInBackground(Void... params) {
                PostsList postsList =  mPostsHelper.get_latest();

                //Get user information, if possible
                if(postsList != null)
                    mUserInfo = mUserHelper.getMultiple(postsList.getUsersId());

                return postsList;
            }

            @Override
            protected void onPostExecute(PostsList posts) {

                //Check if the activity still exists or not
                if(getActivity() == null)
                    return;

                on_got_new_posts_list(posts);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * This function is called when we have got a new post list
     *
     * @param list The new list of posts
     */
    private void on_got_new_posts_list(@Nullable PostsList list){

        //Hide loading bar
        toggleLoadingBarVisibility(false);

        //Check for errors
        if(list == null){
            Toast.makeText(getActivity(), R.string.err_get_latest_posts, Toast.LENGTH_SHORT).show();
            return;
        }
        if(mUserInfo == null){
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Save the list of posts
        mPostsList = list;

        //Check if the posts list is empty
        if(mPostsList.size() == 0)
            toggleNoPostNoticeVisibility(true);


        //Append the new posts list
        //Apply the post fragment
        mPostsListFragment = new PostsListFragment();
        mPostsListFragment.setPostsList(mPostsList);
        mPostsListFragment.setUsersInfos(mUserInfo);
        mPostsListFragment.setOnPostListFragmentsUpdateListener(this);

        //Create and commit a transaction
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.posts_list_target, mPostsListFragment);
        transaction.commit();
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

    @Override
    public void onLoadMorePosts() {

        //Check if post loading is already locked
        if(mLoadPostsLock)
            return;

        if(mPostsList == null)
            return;

        if(mPostsList.size() == 0)
            return;

        //Display loading bar
        mLoadPostsLock = true;
        toggleLoadingBarVisibility(true);

        //Get the ID of the oldest post to start from
        final int start = mPostsList.get(mPostsList.size()-1).getId() - 1;

        //Get older posts
        new GetOlderPosts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, start);
    }

    /**
     * This class get and apply older posts
     */
    private class GetOlderPosts extends AsyncTask<Integer, Void, PostsList> {

        @Override
        protected PostsList doInBackground(Integer... id) {

            //Get the list of older posts
            PostsList postsList = mPostsHelper.get_latest(id[0]);

            //Check for errors
            if(postsList == null)
                return null;

            //Merge posts list
            mPostsList.addAll(postsList);

            //Get information about the users
            ArrayMap<Integer, UserInfo> usersInfo
                    = mUserHelper.getMultiple(mPostsList.getUsersId());

            //Check for errors
            if(usersInfo == null)
                return null;

            //Save new user information
            mUserInfo = usersInfo;

            return postsList;
        }

        @Override
        protected void onPostExecute(PostsList posts) {

            //Check if the activity has been detached
            if(getActivity() == null)
                return;

            //Unlock post loading
            mLoadPostsLock = false;
            toggleLoadingBarVisibility(false);

            //Apply new posts list
            mPostsListFragment.setPostsList(mPostsList);
            mPostsListFragment.setUsersInfos(mUserInfo);
            mPostsListFragment.show();

        }
    }
}
