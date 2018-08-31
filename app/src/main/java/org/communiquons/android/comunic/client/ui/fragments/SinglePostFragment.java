package org.communiquons.android.comunic.client.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.asynctasks.GetSinglePostTask;

/**
 * Single post fragment
 *
 * This fragment allows to display a single post
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/12/18.
 */

public class SinglePostFragment extends Fragment {

    /**
     * The name of the argument that contains the ID of the post to open
     */
    public static final String ARGUMENT_POST_ID = "post_id";

    /**
     * The ID of the post
     */
    private int mPostID = 0;

    /**
     * Post list that contains only a single post
     */
    private PostsList mPostsList;

    /**
     * Get single post task
     */
    private GetSinglePostTask mGetSinglePostTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get post ID
        mPostID = getArguments().getInt(ARGUMENT_POST_ID);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_post, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Check if the fragment contains information about the post
        if(mPostsList == null){
            getPostInfo();
        }
        else
            show_posts();
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

    /**
     * Get information about the post and its related users
     */
    private void getPostInfo(){

        //Perform the request in the background
        unset_all_tasks();

        mGetSinglePostTask = new GetSinglePostTask(getActivity());
        mGetSinglePostTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                onGotPostInfo(posts);
            }
        });
        mGetSinglePostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostID);
    }

    /**
     * This method is triggered once we got information about the post
     */
    private void onGotPostInfo(@Nullable PostsList list) {

        //Check if we did not get post information
        if (list == null) {
            Toast.makeText(getActivity(), R.string.err_get_post_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check if we could not get user information
        if (!list.hasUsersInfo()) {
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        mPostsList = list;

        show_posts();
    }

    /**
     * Show the list of posts
     */
    private void show_posts(){

        //Apply the post fragment
        PostsListFragment postsListFragment = new PostsListFragment();
        postsListFragment.setPostsList(mPostsList);

        //Create and commit a transaction
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.posts_list_target, postsListFragment);
        transaction.commit();


    }
}
