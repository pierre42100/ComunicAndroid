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
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.data.models.UserInfo;

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
     * Information about the post
     */
    private Post mPost;

    /**
     * Post list that contains only a single post
     */
    private PostsList mPostsList;

    /**
     * Information about the related users
     */
    private ArrayMap<Integer, UserInfo> mUserInfo;

    /**
     * Post helper
     */
    private PostsHelper mPostsHelper;

    /**
     * Get user information helper
     */
    private GetUsersHelper mGetUserHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get post ID
        mPostID = getArguments().getInt(ARGUMENT_POST_ID);

        //Create post helper
        mPostsHelper = new PostsHelper(getActivity());

        //Create get user helper
        mGetUserHelper = new GetUsersHelper(getActivity());
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
        if(mPost == null || mUserInfo == null){
            getPostInfo();
        }
        else
            onGotPostInfo();
    }

    /**
     * Get information about the post and its related users
     */
    private void getPostInfo(){

        //Perform the request in the background
        new AsyncTask<Integer, Void, Void>(){

            @Override
            protected Void doInBackground(Integer... params) {

                //Intend to get information about the post
                mPost = mPostsHelper.getSingle(params[0]);

                if(mPost != null) {
                    mPostsList = new PostsList();
                    mPostsList.add(mPost);
                    mUserInfo = mGetUserHelper.getMultiple(mPostsList.getUsersId());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if(getActivity() == null)
                    return;

                onGotPostInfo();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostID);

    }

    /**
     * This method is triggered once we got informations about the post
     */
    private void onGotPostInfo(){

        //Check if we did not get post information
        if(mPost == null){
            Toast.makeText(getActivity(), R.string.err_get_post_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check if we could not get user information
        if(mUserInfo == null){
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Apply the post fragment
        PostsListFragment postsListFragment = new PostsListFragment();
        postsListFragment.setPostsList(mPostsList);
        postsListFragment.setUsersInfos(mUserInfo);

        //Create and commit a transaction
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.posts_list_target, postsListFragment);
        transaction.commit();


    }
}
