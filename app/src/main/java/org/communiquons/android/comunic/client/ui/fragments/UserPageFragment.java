package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.ImageLoadHelper;
import org.communiquons.android.comunic.client.data.models.AdvancedUserInfo;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;

/**
 * User page fragment
 *
 * Display the page of a user
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/13/18.
 */

public class UserPageFragment extends Fragment implements PostsCreateFormFragment.OnPostCreated {

    /**
     * Debug tag
     */
    private static final String TAG = "UserPageFragment";

    /**
     * The name of the argument that contains user id
     */
    public static final String ARGUMENT_USER_ID = "userID";

    /**
     * The ID of the current user
     */
    private int mUserID;

    /**
     * Page's user information
     */
    private AdvancedUserInfo userInfo;

    /**
     * User posts
     */
    private PostsList mPostsList;

    /**
     * User information
     */
    private ArrayMap<Integer, UserInfo> mUsersInfo;

    /**
     * Get user helper
     */
    private GetUsersHelper getUsersHelper;

    /**
     * Posts helper
     */
    private PostsHelper mPostsHelper;

    /**
     * Loading alert dialog
     */
    private AlertDialog loadingDialog;

    /**
     * User account name
     */
    private TextView user_name;

    /**
     * User account image
     */
    private ImageView user_image;

    /**
     * Posts list fragment
     */
    private PostsListFragment mPostsListFragment;

    /**
     * Create a post on user page button
     */
    private ImageView mCreatePostButton;

    /**
     * Create a post on user page form
     */
    private View mCreatePostForm;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Save user ID
        mUserID = getArguments().getInt(ARGUMENT_USER_ID);

        //Get database helper
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());

        //Create getUserHelper instance
        getUsersHelper = new GetUsersHelper(getActivity(), dbHelper);

        //Create posts helper instance
        mPostsHelper = new PostsHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the user views
        user_image = view.findViewById(R.id.user_account_image);
        user_name = view.findViewById(R.id.user_account_name);

        //Get the view related to the create post form
        mCreatePostButton = view.findViewById(R.id.create_post_button);
        mCreatePostForm = view.findViewById(R.id.create_post_form);

        //Trigger the form
        mCreatePostForm.setVisibility(View.GONE);
        mCreatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreatePostForm.setVisibility(
                        mCreatePostForm.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        //Create the fragment
        init_create_post_fragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        //Check if we got information about the user
        if(userInfo == null){

            //Show loading alert dialog
            loadingDialog = UiUtils.create_loading_dialog(getActivity());

            //Fetch user information
            new AsyncTask<Integer, Void, AdvancedUserInfo>(){
                @Override
                protected AdvancedUserInfo doInBackground(Integer... params) {
                    return getUsersHelper.get_advanced_infos(params[0]);
                }

                @Override
                protected void onPostExecute(AdvancedUserInfo advancedUserInfo) {
                    //Continue only if the activity hasn't ended
                    if(getActivity() != null)
                        onGotUserInfo(advancedUserInfo);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUserID);
        }
        else
            onGotUserInfo(userInfo);

        //Render the list of post (if available)
        render_list_posts();
    }

    @Override
    public void onPause() {
        super.onPause();

        //Remove loading dialog
        if(loadingDialog != null)
            loadingDialog.dismiss();
    }

    /**
     * This function is called on the main thread once we got user informations
     *
     * @param info Informations about the user
     */
    public void onGotUserInfo(@Nullable AdvancedUserInfo info){

        //Remove loading dialog
        if(loadingDialog != null)
            loadingDialog.dismiss();

        //Check for errors
        if(info == null){
            Toast.makeText(getActivity(), R.string.err_get_user_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Save user information
        userInfo = info;

        //Set activity title
        getActivity().setTitle(userInfo.getDisplayFullName());

        //Update activity menu dock
        //if(AccountUtils.getID(getActivity()) == mUserID)
            ((MainActivity) getActivity()).setSelectedNavigationItem(
                    R.id.main_bottom_navigation_me_view);
        /*else
            ((MainActivity) getActivity()).setSelectedNavigationItem(
                    R.id.main_bottom_navigation_users_view);*/

        //Update user name and account image
        user_name.setText(userInfo.getDisplayFullName());
        ImageLoadHelper.remove(user_image);
        ImageLoadHelper.load(getActivity(), userInfo.getAcountImageURL(), user_image);

        //Load the list of posts of the user
        load_posts();
    }

    /**
     * Load the posts of the user
     */
    private void load_posts(){

        new AsyncTask<Void, Void, PostsList>(){

            @Override
            protected PostsList doInBackground(Void... params) {
                PostsList list = mPostsHelper.get_user(mUserID);

                //Get the information about the users who created the posts
                if(list != null)
                    mUsersInfo = getUsersHelper.getMultiple(list.getUsersId());

                return list;
            }

            @Override
            protected void onPostExecute(PostsList posts) {
                if(getActivity() != null)
                    display_posts(posts);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Display the posts of the user
     *
     * @param list the list of posts / null in case of failure
     */
    private void display_posts(@Nullable PostsList list){

        //Check for errors
        if(list == null){
            Toast.makeText(getActivity(), R.string.err_get_user_posts, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check we didn't get user information
        if(mUsersInfo == null){
            Toast.makeText(getActivity(), R.string.err_get_users_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Save the list of posts
        mPostsList = list;

        //Render the list of posts
        render_list_posts();
    }

    /**
     * Render the list of posts
     */
    private void render_list_posts(){

        //Check we have got required information
        if(mPostsList == null || mUsersInfo == null)
            return;

        //Create the fragment
        mPostsListFragment = new PostsListFragment();
        mPostsListFragment.setPostsList(mPostsList);
        mPostsListFragment.setUsersInfos(mUsersInfo);

        //Set the fragment
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_user_page, mPostsListFragment);
        transaction.commit();
    }

    /**
     * Create and create post fragment
     */
    private void init_create_post_fragment(){

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
        transaction.replace(R.id.create_post_form, fragment);
        transaction.commit();

    }

    @Override
    public void onPostCreated(Post post) {
        //Reload the list of post
        mPostsList = null;
        init_create_post_fragment();
        load_posts();
    }
}
