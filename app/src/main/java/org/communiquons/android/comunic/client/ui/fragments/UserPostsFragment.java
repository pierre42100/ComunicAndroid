package org.communiquons.android.comunic.client.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.ui.asynctasks.LoadUserPostsTask;

/**
 * User posts fragment
 *
 * @author Pierre HUBERT
 */
public class UserPostsFragment extends Fragment
    implements PostsCreateFormFragment.OnPostCreated {

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
     * The list of posts of the user
     */
    private PostsList mPostsList;

    /**
     * Posts helper
     */
    private PostsHelper mPostsHelper;

    /**
     * User information helper
     */
    private GetUsersHelper mUserHelper;

    /**
     * Create post button
     */
    private Button mCreatePostButton;

    /**
     * No post on user page notice
     */
    private TextView mNoPostNotice;

    /**
     * Create post layout
     */
    private FrameLayout mCreatePostLayout;

    /**
     * Load user posts task
     */
    private LoadUserPostsTask mLoadUserPostsTask;

    /**
     * Posts list fragment
     */
    private PostsListFragment mPostsListFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get arguments
        Bundle bundle = getArguments();
        assert bundle != null;

        mUserID = bundle.getInt(ARGUMENT_USER_ID);
        mCanPostsText = bundle.getBoolean(ARGUMENT_CAN_POST_TEXT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_postswithform, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Get the views
        mCreatePostButton = view.findViewById(R.id.create_post_btn);
        mCreatePostLayout = view.findViewById(R.id.create_posts_form_target);
        mNoPostNotice = view.findViewById(R.id.no_post_notice);

        setNoPostNoticeVisibility(false);

        mCreatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Invert form visibility
                setPostFormVisibility(mCreatePostLayout.getVisibility() != View.VISIBLE);
            }
        });

        //Initialize helpers
        assert getActivity() != null;
        mPostsHelper = new PostsHelper(getActivity());
        mUserHelper = new GetUsersHelper(getActivity());

        //Add create post fragment, if possible
        if(mCanPostsText)
            init_create_post_fragment();
        else
            mCreatePostButton.setVisibility(View.GONE);

        //Load user posts
        mPostsList = new PostsList();
        load_posts();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        cancel_load_task();
    }

    /**
     * Load user posts
     */
    private void load_posts(){

        cancel_load_task();

        mLoadUserPostsTask = new LoadUserPostsTask(mUserID, getActivity());
        mLoadUserPostsTask.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<PostsList>() {
            @Override
            public void OnPostExecute(PostsList posts) {
                if(getActivity() == null)
                    return;

                apply_posts(posts);
            }
        });
        mLoadUserPostsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void cancel_load_task(){
        if(mLoadUserPostsTask != null)
            mLoadUserPostsTask.setOnPostExecuteListener(null);
    }

    /**
     * Apply the list of posts
     */
    @UiThread
    private void apply_posts(@Nullable PostsList posts){

        if(mPostsList == null)
            return;

        if(isStateSaved())
            return;

        //Check for errors
        if(posts == null){
            Toast.makeText(getActivity(), R.string.err_get_user_posts, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check we didn't get user information
        if(!posts.hasUsersInfo()){
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Merge post information with existing one
        mPostsList.addAll(posts);
        assert mPostsList.getUsersInfo() != null;
        mPostsList.getUsersInfo().putAll(posts.getUsersInfo());

        //Create fragment if required
        if(mPostsListFragment == null){
            mPostsListFragment = new PostsListFragment();

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.posts_list_target, mPostsListFragment);
            transaction.commit();
        }


        mPostsListFragment.setPostsList(mPostsList);
        mPostsListFragment.show();


        setNoPostNoticeVisibility(mPostsList.size() < 1);
    }

    /**
     * Create and create post fragment
     */
    private void init_create_post_fragment(){

        //Can not perform a transaction if the state has been saved
        if(isStateSaved())
            return;

        //Create bundle
        Bundle args = new Bundle();
        args.putInt(PostsCreateFormFragment.PAGE_TYPE_ARG, PostsCreateFormFragment.PAGE_TYPE_USER);
        args.putInt(PostsCreateFormFragment.PAGE_ID_ARG, mUserID);

        //Create fragment
        PostsCreateFormFragment fragment = new PostsCreateFormFragment();
        fragment.setArguments(args);
        fragment.setOnPostCreatedListener(this);

        //Perform transaction
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.create_posts_form_target, fragment);
        transaction.commit();

        //Hide the post form by default
        setPostFormVisibility(false);
    }

    /**
     * Update post creation form visibility
     *
     * @param visible New visibility
     */
    private void setPostFormVisibility(boolean visible){
        mCreatePostLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        mCreatePostButton.setActivated(visible);
    }

    private void setNoPostNoticeVisibility(boolean visible){
        mNoPostNotice.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPostCreated(Post post) {
        mPostsList = new PostsList();
        load_posts();
        init_create_post_fragment();
    }
}
