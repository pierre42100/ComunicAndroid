package org.communiquons.android.comunic.client.ui.viewholders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.ui.listeners.OnGroupActionListener;
import org.communiquons.android.comunic.client.ui.listeners.OnOpenGroupListener;
import org.communiquons.android.comunic.client.ui.views.GroupImageView;
import org.communiquons.android.comunic.client.ui.views.GroupMembershipStatusView;

/**
 * Single group holder class
 *
 * @author Pierre HUBERT
 */
public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private GroupInfo mGroupInfo;

    private OnOpenGroupListener mOnOpenGroupListener;

    private GroupImageView mGroupImageView;
    private TextView mGroupName;
    private GroupMembershipStatusView mGroupMembershipStatus;


    public GroupViewHolder(@NonNull View itemView, @Nullable OnOpenGroupListener openGroupListener){
        this(itemView, null);
        mOnOpenGroupListener = openGroupListener;
    }

    public GroupViewHolder(@NonNull View itemView, @Nullable OnGroupActionListener actionListener) {
        super(itemView);

        itemView.setOnClickListener(this);

        mGroupImageView = itemView.findViewById(R.id.groupImage);
        mGroupName = itemView.findViewById(R.id.groupName);
        mGroupMembershipStatus = itemView.findViewById(R.id.groupMembershipStatusView);

        if(actionListener != null) {
            mGroupMembershipStatus.setOnGroupMembershipUpdateListener(actionListener);
            mOnOpenGroupListener = actionListener;
        }
        else
            mGroupMembershipStatus.setVisibility(View.GONE);
    }


    /**
     * Bind (apply) a new group to the view
     *
     * @param group The group to apply to the view
     */
    public void bind(GroupInfo group){
        mGroupInfo = group;
        mGroupImageView.setGroup(mGroupInfo);
        mGroupName.setText(mGroupInfo.getDisplayName());
        mGroupMembershipStatus.setGroup(mGroupInfo);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(itemView) && mOnOpenGroupListener != null)
            mOnOpenGroupListener.onOpenGroup(mGroupInfo.getId());
    }
}