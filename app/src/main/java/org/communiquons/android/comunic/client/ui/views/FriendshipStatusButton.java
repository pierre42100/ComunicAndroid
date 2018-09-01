package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.models.FriendshipStatus;
import org.communiquons.android.comunic.client.ui.listeners.OnFriendsStatusUpdateListener;

/**
 * Friendship status button
 *
 * @author Pierre HUBERT
 */
public class FriendshipStatusButton extends BaseFrameLayoutView implements View.OnClickListener {

    /**
     * Debug tag
     */
    private static final String TAG = FriendshipStatusButton.class.getCanonicalName();

    /**
     * The ID of the target user
     */
    private int mUserID;

    /**
     * Information about the friendship status
     */
    private FriendshipStatus mFriendshipStatus;

    /**
     * Buttons list container
     */
    private LinearLayout mButtonsList;

    /**
     * Actions buttons
     */
    private Button mSendRequestButton;
    private Button mCancelRequestButton;
    private Button mAcceptRequestButton;
    private Button mRejectRequestButton;
    private Button mFollowButton;
    private Button mFollowingButton;

    /**
     * Progress bar
     */
    private ProgressBar mProgressBar;

    /**
     * AsyncTasks
     */
    private GetFriendshipStatus mGetFriendShipStatus;
    private UpdateStatusClass mUpdateStatusClass;

    /**
     * On friend status update listener
     */
    private OnFriendsStatusUpdateListener mOnFriendsStatusUpdateListener;

    public FriendshipStatusButton(@NonNull Context context) {
        this(context, null);
    }

    public FriendshipStatusButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FriendshipStatusButton(@NonNull Context context, @Nullable AttributeSet attrs,
                                  int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = inflate(getContext(), R.layout.view_frienship_status, this);

        mProgressBar = view.findViewById(R.id.progressBar);
        mButtonsList = view.findViewById(R.id.buttons_list);
        mButtonsList = view.findViewById(R.id.buttons_list);
        mSendRequestButton = view.findViewById(R.id.button_send_request);
        mCancelRequestButton = view.findViewById(R.id.button_cancel_request);
        mAcceptRequestButton = view.findViewById(R.id.button_accept_request);
        mRejectRequestButton = view.findViewById(R.id.button_reject_request);
        mFollowButton = view.findViewById(R.id.button_follow);
        mFollowingButton = view.findViewById(R.id.button_following);

        mSendRequestButton.setOnClickListener(this);
        mCancelRequestButton.setOnClickListener(this);
        mAcceptRequestButton.setOnClickListener(this);
        mRejectRequestButton.setOnClickListener(this);
        mFollowButton.setOnClickListener(this);
        mFollowingButton.setOnClickListener(this);

        hideAllButtons();
        setProgressBarVisibility(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if(mGetFriendShipStatus != null)
            mGetFriendShipStatus.setOnPostExecuteListener(null);

        if(mUpdateStatusClass != null)
            mUpdateStatusClass.setOnPostExecuteListener(null);
    }

    /**
     * Set the ID of the target user
     *
     * @param mUserID The ID of the target user
     */
    public void setUserID(int mUserID) {
        this.mUserID = mUserID;
    }

    public OnFriendsStatusUpdateListener getOnFriendsStatusUpdateListener() {
        return mOnFriendsStatusUpdateListener;
    }

    public void setOnFriendsStatusUpdateListener(OnFriendsStatusUpdateListener onFriendsStatusUpdateListener) {
        this.mOnFriendsStatusUpdateListener = onFriendsStatusUpdateListener;
    }

    /**
     * Refresh current user status, only if required
     */
    public void refreshIfRequired(){
        if(mFriendshipStatus == null)
            refresh();
    }

    /**
     * Refresh current friendship status
     */
    public void refresh(){

        hideAllButtons();
        setProgressBarVisibility(true);

        mGetFriendShipStatus = new GetFriendshipStatus(getContext());
        mGetFriendShipStatus.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUserID);
        mGetFriendShipStatus.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<FriendshipStatus>() {
            @Override
            public void OnPostExecute(FriendshipStatus friendshipStatus) {
                onGotFriendshipsStatus(friendshipStatus);
            }
        });
    }



    /**
     * Method called when we have got friendship status
     *
     * @param friendshipStatus New friendship status
     */
    private void onGotFriendshipsStatus(@Nullable FriendshipStatus friendshipStatus){

        hideAllButtons();
        setProgressBarVisibility(false);

        mFriendshipStatus = friendshipStatus;

        if(friendshipStatus == null) {
            Toast.makeText(getContext(), R.string.err_get_friendship_status,
                    Toast.LENGTH_SHORT).show();
            return;
        }


        //Update buttons visibility
        if(!mFriendshipStatus.isFriend()){

            if(mFriendshipStatus.isSentRequest())
                mCancelRequestButton.setVisibility(View.VISIBLE);

            else if(mFriendshipStatus.isReceivedRequest()){
                mAcceptRequestButton.setVisibility(View.VISIBLE);
                mRejectRequestButton.setVisibility(View.VISIBLE);
            }

            else
                mSendRequestButton.setVisibility(View.VISIBLE);
        }

        else {

            //The two people are friends
            if(mOnFriendsStatusUpdateListener != null)
                mOnFriendsStatusUpdateListener.onAreFriend();

            if(mFriendshipStatus.isFollowing())
                mFollowingButton.setVisibility(View.VISIBLE);
            else
                mFollowButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int action_id = v.getId();
        hideAllButtons();
        setProgressBarVisibility(true);

        mUpdateStatusClass = new UpdateStatusClass(mUserID, action_id, getContext());
        mUpdateStatusClass.setOnPostExecuteListener(new SafeAsyncTask.
                OnPostExecuteListener<FriendshipStatus>() {
            @Override
            public void OnPostExecute(FriendshipStatus friendshipStatus) {
                onGotFriendshipsStatus(friendshipStatus);
            }
        });
        mUpdateStatusClass.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Hide all view buttons
     */
    private void hideAllButtons(){
        mSendRequestButton.setVisibility(View.GONE);
        mCancelRequestButton.setVisibility(View.GONE);
        mAcceptRequestButton.setVisibility(View.GONE);
        mRejectRequestButton.setVisibility(View.GONE);
        mFollowButton.setVisibility(View.GONE);
        mFollowingButton.setVisibility(View.GONE);
    }

    /**
     * Update progress bar visibility
     *
     * @param visible TRUE : visible / FALSE : else
     */
    private void setProgressBarVisibility(boolean visible){
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Safe AsyncTask used to get current friendship status
     */
    private static class GetFriendshipStatus extends SafeAsyncTask<Integer, Void, FriendshipStatus>{

        GetFriendshipStatus(Context context) {
            super(context);
        }

        @Override
        protected FriendshipStatus doInBackground(Integer... integers) {
            return new FriendsListHelper(getContext()).getFrienshipStatus(integers[0]);
        }

    }

    /**
     * Class used to update friendship status
     */
    private static class UpdateStatusClass extends SafeAsyncTask<Void, Void, FriendshipStatus>{

        private int mFriendID;
        private int mAction;

        /**
         * Construct the class
         *
         * @param friendID The ID of the target friend
         * @param action The action to perform
         * @param context The context of the application
         */
        UpdateStatusClass(int friendID, int action, Context context) {
            super(context);
            this.mFriendID = friendID;
            this.mAction = action;
        }

        @Override
        protected FriendshipStatus doInBackground(Void... voids) {

            //Friends helper
            FriendsListHelper helper = new FriendsListHelper(getContext());
            performUpdate(mAction, mFriendID, helper);
            return helper.getFrienshipStatus(mFriendID);
        }

        /**
         * Perform the update of the friendship status, as requested per the click on the buttons
         *
         * @param action_id The ID of the button that has been clicked
         * @param friend_id The ID of the target friend
         * @param helper Helper object to use
         */
        private void performUpdate(int action_id, int friend_id, FriendsListHelper helper){

            switch (action_id){

                //Send a friendship request
                case R.id.button_send_request:
                    helper.sendRequest(friend_id);
                    break;

                //Accept a friendship request
                case R.id.button_accept_request:
                    helper.respondRequest(friend_id, true);
                    break;

                //Reject a friendship request
                case R.id.button_reject_request:
                    helper.respondRequest(friend_id, false);
                    break;

                //Cancel a friendship request
                case R.id.button_cancel_request:
                    helper.cancelRequest(friend_id);
                    break;

                //Update following status
                case R.id.button_follow:
                    helper.setFollowing(friend_id, true);
                    break;
                case R.id.button_following:
                    helper.setFollowing(friend_id, false);
                    break;

                default:
                    throw new RuntimeException("Unsupported action by updater!");
            }

        }
    }
}
