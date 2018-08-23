package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.Friend;
import org.communiquons.android.comunic.client.data.models.FriendUser;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.listeners.OnFriendListActionListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

import java.util.ArrayList;

/**
 * Adapter that render the list of friend on the friends list fragment
 *
 * @author Pierre Hubert
 * Created by pierre on 11/15/17.
 */

public class FriendsAdapter extends BaseRecyclerViewAdapter {

    /**
     * View type
     */
    private static final int VIEW_TYPE_ACCEPTED_FRIEND = 1;
    private static final int VIEW_TYPE_PENDING_FRIEND = 2;

    /**
     * The list of friends, with their information
     */
    private ArrayList<FriendUser> mList;

    /**
     * Actions listener
     */
    private OnFriendListActionListener mListener;

    /**
     * Class constructor
     *
     * @param context The context of execution of the application
     * @param friendsList The list of friends to display (with user information)
     * @param listener Actions on friendlist listener
     */
    public FriendsAdapter(Context context, ArrayList<FriendUser> friendsList,
                          OnFriendListActionListener listener){
        super(context);

        mList = friendsList;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getFriend().isAccepted() ? VIEW_TYPE_ACCEPTED_FRIEND :
                VIEW_TYPE_PENDING_FRIEND;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View view;

        if(type == VIEW_TYPE_ACCEPTED_FRIEND){
            view = LayoutInflater.from(getContext()).inflate(R.layout.friend_accepted_item,
                    viewGroup, false);
            return new AcceptedFriendHolder(view);
        }

        if(type == VIEW_TYPE_PENDING_FRIEND){
            view = LayoutInflater.from(getContext()).inflate(R.layout.friend_pending_item,
                    viewGroup, false);
            return new PendingFriendHolder(view);
        }

        throw new RuntimeException("Undefined view type: " + type);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
        ((BaseFriendHolder)viewHolder).bind(pos);
    }


    /**
     * Base Friend ViewHolder
     */
    private class BaseFriendHolder extends RecyclerView.ViewHolder {

        private WebUserAccountImage mUserAccountImage;
        private TextView mUserName;
        private TextView mUserStatus;

        BaseFriendHolder(@NonNull View itemView) {
            super(itemView);

            mUserAccountImage = itemView.findViewById(R.id.account_image);
            mUserName = itemView.findViewById(R.id.account_name);
            mUserStatus = itemView.findViewById(R.id.user_status);
        }

        Friend getFriend(int pos){
            return mList.get(pos).getFriend();
        }

        UserInfo getUserInfo(int pos){
            return mList.get(pos).getUserInfo();
        }

        int getCurrentUserID(){
            return getUserInfo(getLayoutPosition()).getId();
        }

        @CallSuper
        void bind(int pos){

            //Update user information
            mUserAccountImage.setUser(getUserInfo(pos));
            mUserName.setText(getUserInfo(pos).getDisplayFullName());

            //Update user status
            boolean signed_in = getFriend(pos).signed_in();
            mUserStatus.setText(UiUtils.getString(getContext(), signed_in ?
                    R.string.user_status_online : R.string.user_status_offline));
            mUserStatus.setTextColor(UiUtils.getColor(getContext(),
                    signed_in ? R.color.holo_green_dark : R.color.darker_gray));


            //Open user page on click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onOpenUserPage(getCurrentUserID());
                }
            });
        }
    }


    /**
     * Accepted friend holder
     */
    private class AcceptedFriendHolder extends BaseFriendHolder implements View.OnLongClickListener, View.OnClickListener {

        private ImageView mMoreBtn;

        AcceptedFriendHolder(@NonNull View itemView) {
            super(itemView);

            mMoreBtn = itemView.findViewById(R.id.more_btn);

            itemView.setOnLongClickListener(this);
            mMoreBtn.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {

            if(!v.equals(itemView))
                return false;

            mListener.onOpenContextMenuForFriend(itemView, getLayoutPosition());
            return true;
        }

        @Override
        public void onClick(View v) {
            if(v.equals(mMoreBtn))
                mListener.onOpenContextMenuForFriend(v, getLayoutPosition());
        }
    }


    /**
     * Pending friend view holder
     */
    private class PendingFriendHolder extends BaseFriendHolder implements View.OnClickListener {

        private Button mAcceptButton;
        private Button mRejectButton;

        PendingFriendHolder(@NonNull View itemView) {
            super(itemView);

            mAcceptButton = itemView.findViewById(R.id.accept_button);
            mRejectButton = itemView.findViewById(R.id.reject_button);

            mAcceptButton.setOnClickListener(this);
            mRejectButton.setOnClickListener(this);
        }

        @Override
        void bind(int pos) {
            super.bind(pos);

            mAcceptButton.setVisibility(View.VISIBLE);
            mRejectButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View v) {

            mAcceptButton.setVisibility(View.INVISIBLE);
            mRejectButton.setVisibility(View.INVISIBLE);

            boolean accept = v.equals(mAcceptButton);
            mListener.onRespondFrienshipRequest(getLayoutPosition(), accept);
        }
    }
}
