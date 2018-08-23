package org.communiquons.android.comunic.client.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.FriendshipStatus;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

/**
 * User access denied fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/11/18.
 */

public class UserAccessDeniedFragment extends Fragment implements View.OnClickListener {

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
     * Information about the friendship status
     */
    private FriendshipStatus mFriendshipStatus;

    /**
     * User account image
     */
    private WebUserAccountImage mUserImage;

    /**
     * User account name
     */
    private TextView mUserName;

    /**
     * Buttons list container
     */
    private LinearLayout mButtonsList;

    /**
     * Send request button
     */
    private Button mSendRequestButton;

    /**
     * Cancel request button
     */
    private Button mCancelRequestButton;

    /**
     * Accept request button
     */
    private Button mAcceptRequestButton;

    /**
     * Reject request button
     */
    private Button mRejectRequestButton;

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

        //Get user related fields
        mUserImage = view.findViewById(R.id.user_account_image);
        mUserName = view.findViewById(R.id.user_account_name);

        //Get the buttons
        mButtonsList = view.findViewById(R.id.buttons_list);
        mSendRequestButton = view.findViewById(R.id.button_send_request);
        mCancelRequestButton = view.findViewById(R.id.button_cancel_request);
        mAcceptRequestButton = view.findViewById(R.id.button_accept_request);
        mRejectRequestButton = view.findViewById(R.id.button_reject_request);
    }

    @Override
    public void onResume() {
        super.onResume();

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

        //Check if we have got the friends
        if(mFriendshipStatus == null){
            getFrienshipStatus();
        }
        else {
            onGotFriendshipStatus(mFriendshipStatus);
        }
    }

    /**
     * Get the information about the friendship between the two users
     */
    private void getFrienshipStatus(){

        //Perform the request in the background
        new AsyncTask<Integer, Void, FriendshipStatus>(){

            @Override
            protected FriendshipStatus doInBackground(Integer... params) {
                return mFriendListHelper.getFrienshipStatus(params[0]);
            }

            @Override
            protected void onPostExecute(FriendshipStatus status) {
                if(getActivity() == null)
                    return;

                onGotFriendshipStatus(status);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUserID);

    }

    /**
     * This method is called once we got information about the friendship status
     *
     * @param status The status of the frienship or null in case of failure
     */
    private void onGotFriendshipStatus(@Nullable FriendshipStatus status){

        //Check for errors
        if(status == null){
            Toast.makeText(getActivity(), R.string.err_get_friendship_status,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Save the friendship status
        mFriendshipStatus = status;

        //Hide all the button by default but the list
        mButtonsList.setVisibility(View.VISIBLE);
        mSendRequestButton.setVisibility(View.GONE);
        mCancelRequestButton.setVisibility(View.GONE);
        mAcceptRequestButton.setVisibility(View.GONE);
        mRejectRequestButton.setVisibility(View.GONE);

        //Check if the users are friend
        if(mFriendshipStatus.isFriend()) {
            mOpenUsersPageListener.openUserPage(mUserID);
            return;
        }

        //Check if the current user has sent a friendship request
        if(mFriendshipStatus.isSentRequest()){
            mCancelRequestButton.setVisibility(View.VISIBLE);
        }

        //Check if the current user has received a friendship request
        else if(mFriendshipStatus.isReceivedRequest()){
            mAcceptRequestButton.setVisibility(View.VISIBLE);
            mRejectRequestButton.setVisibility(View.VISIBLE);
        }

        //Else the users have not pending request
        else {
            mSendRequestButton.setVisibility(View.VISIBLE);
        }

        //Make all the buttons click point on the fragment
        mSendRequestButton.setOnClickListener(this);
        mCancelRequestButton.setOnClickListener(this);
        mAcceptRequestButton.setOnClickListener(this);
        mRejectRequestButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        //Make buttons list disappear
        mButtonsList.setVisibility(View.INVISIBLE);

        //Get the ID of the button
        int action_id = v.getId();

        //Process the request
        new AsyncTask<Integer, Void, FriendshipStatus>(){

            @Override
            protected FriendshipStatus doInBackground(Integer... params) {

                //Perform requested action
                performFriendshipUpdate(params[0]);

                //Return new friendship status
                return mFriendListHelper.getFrienshipStatus(mUserID);
            }

            @Override
            protected void onPostExecute(FriendshipStatus status) {
                if(getActivity() == null)
                    return;

                onGotFriendshipStatus(status);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, action_id);

    }

    /**
     * Perform the update of the friendship status, as requested per the click on the buttons
     *
     * @param action_id The ID of the button that has been clicked
     */
    private void performFriendshipUpdate(int action_id){

        switch (action_id){

            //Send a friendship request
            case R.id.button_send_request:
                mFriendListHelper.sendRequest(mUserID);
                break;

            //Accept a friendship request
            case R.id.button_accept_request:
                mFriendListHelper.respondRequest(mUserID, true);
                break;

            //Reject a friendship request
            case R.id.button_reject_request:
                mFriendListHelper.respondRequest(mUserID, false);
                break;

            //Cancel a friendship request
            case R.id.button_cancel_request:
                mFriendListHelper.cancelRequest(mUserID);
                break;

            default:
                throw new RuntimeException("Unsupported action by updater!");
        }

    }
}
