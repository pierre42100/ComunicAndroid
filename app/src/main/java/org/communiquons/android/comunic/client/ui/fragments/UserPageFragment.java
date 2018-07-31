package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.AdvancedUserInfo;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.adapters.FragmentPagerBaseAdapter;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * User page fragment
 *
 * Display the page of a user
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/13/18.
 */

public class UserPageFragment extends Fragment {

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
     * Loading alert dialog
     */
    private AlertDialog loadingDialog;

    /**
     * Get user helper
     */
    private GetUsersHelper getUsersHelper;

    /**
     * User page open listener
     */
    private onOpenUsersPageListener mOpenUsersPageListener;

    /**
     * View pager of the fragment
     */
    private ViewPager mPager;

    /**
     * Tab layout
     */
    private TabLayout mTabLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Save user ID
        mUserID = getArguments().getInt(ARGUMENT_USER_ID);

        //Get database helper
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());

        //Create getUserHelper instance
        getUsersHelper = new GetUsersHelper(getActivity(), dbHelper);

        //Get the open user page listener
        mOpenUsersPageListener = (onOpenUsersPageListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get required views
        mPager = view.findViewById(R.id.viewpager);
        mTabLayout = view.findViewById(R.id.tab_layout);
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

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * This function is called on the main thread once we got user informations
     *
     * @param info Information about the user
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

        //Check if the user is not allowed to access user information
        if(info.isAccessForbidden()){
            //Open appropriate fragment
            mOpenUsersPageListener.openUserAccessDeniedPage(mUserID);
            return;
        }


        //Save user information
        userInfo = info;

        //Set activity title
        getActivity().setTitle(userInfo.getDisplayFullName());

        //Update activity menu dock
        ((MainActivity) getActivity()).setSelectedNavigationItem(
                    R.id.main_bottom_navigation_me_view);

        //Initialize view pager
        FragmentPagerBaseAdapter adapter = new FragmentPagerBaseAdapter(getChildFragmentManager());

        //User advanced information fragment
        AdvancedUserInfoFragment infoFragment = new AdvancedUserInfoFragment();
        infoFragment.setAdvancedUserInfo(userInfo);
        adapter.addFragment(infoFragment, UiUtils.getString(getActivity(),
                R.string.tab_user_advanced_info));

        //Posts fragment
        Bundle args = new Bundle();
        args.putInt(UserPostsFragment.ARGUMENT_USER_ID, userInfo.getId());
        args.putBoolean(UserPostsFragment.ARGUMENT_CAN_POST_TEXT, userInfo.isCanPostText());

        UserPostsFragment postsFragment = new UserPostsFragment();
        postsFragment.setArguments(args);
        adapter.addFragment(postsFragment, UiUtils.getString(getActivity(),
                R.string.tab_posts));


        mPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mPager);
    }
}
