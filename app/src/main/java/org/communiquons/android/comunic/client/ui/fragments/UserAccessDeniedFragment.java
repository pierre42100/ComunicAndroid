package org.communiquons.android.comunic.client.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.listeners.OnFriendsStatusUpdateListener;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;
import org.communiquons.android.comunic.client.ui.views.FriendshipStatusButton;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

/**
 * User access denied fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/11/18.
 */

public class UserAccessDeniedFragment extends Fragment implements OnFriendsStatusUpdateListener {

    /**
     * The name in the bundle of the target user ID
     */
    public static final String ARGUMENT_USER_ID = "user_id";

    /**
     * The ID of the target user
     */
    private int mUserID;

    /**
     * Get user helper
     */
    private GetUsersHelper mUserHelper;

    /**
     * Friend list helper
     */
    private FriendsListHelper mFriendListHelper;

    /**
     * User page opener
     */
    private onOpenUsersPageListener mOpenUsersPageListener;

    /**
     * Information about the user
     */
    private UserInfo mUserInfo;

    /**
     * User account image
     */
    private WebUserAccountImage mUserImage;

    /**
     * User account name
     */
    private TextView mUserName;

    /**
     * Friendship status button
     */
    private FriendshipStatusButton mFriendshipStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create get user helper
        mUserHelper = new GetUsersHelper(getActivity());

        //Create friend info helper
        mFriendListHelper = new FriendsListHelper(getActivity());

        //Save the ID of the target user
        mUserID = getArguments().getInt(ARGUMENT_USER_ID);

        //Get user page opener
        try {
            mOpenUsersPageListener = (onOpenUsersPageListener) getActivity();
        } catch (ClassCastException e){
            e.printStackTrace();
            throw new RuntimeException(getActivity().getClass().getName() + "must implement "
                    + onOpenUsersPageListener.class.getName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_access_denied, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserImage = view.findViewById(R.id.user_account_image);
        mUserName = view.findViewById(R.id.user_account_name);
        mFriendshipStatus = view.findViewById(R.id.friendship_status);

        mFriendshipStatus.setOnFriendsStatusUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Initialize FrienshipStatus button
        mFriendshipStatus.setUserID(mUserID);
        mFriendshipStatus.refreshIfRequired();

        //Check if it is required to fetch user information
        if(mUserInfo == null){
            getUserInfo();
        }
        else
            onGotUserInfos(mUserInfo);
    }

    /**
     * This method get user information. Once it got it, it shows these information on the screen
     */
    private void getUserInfo(){

        //Perform the task in the background
        new AsyncTask<Integer, Void, UserInfo>(){

            @Override
            protected UserInfo doInBackground(Integer... params) {
                //Force information about the user to be pulled
                return mUserHelper.getSingle(mUserID, true);
            }

            @Override
            protected void onPostExecute(@Nullable UserInfo userInfo) {
                if(getActivity() == null)
                    return;

                onGotUserInfos(userInfo);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUserID);
    }

    /**
     * This method is called once we got information about the user
     *
     * @param info Information about the user / null in case of failure
     */
    private void onGotUserInfos(@Nullable UserInfo info){

        //Check for error
        if(info == null){
            Toast.makeText(getActivity(), R.string.err_get_user_info, Toast.LENGTH_SHORT).show();
            return;
        }

        //Save user information
        mUserInfo = info;

        //Update activity name
        getActivity().setTitle(mUserInfo.getDisplayFullName());

        //Append user information
        mUserName.setText(mUserInfo.getDisplayFullName());
        mUserImage.setUser(mUserInfo);

    }


    @Override
    public void onAreFriend() {
        mOpenUsersPageListener.openUserPage(mUserID);
    }
}
