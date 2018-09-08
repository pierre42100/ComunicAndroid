package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.ui.listeners.OnGroupMembershipUpdateListener;
import org.communiquons.android.comunic.client.ui.views.GroupImageView;
import org.communiquons.android.comunic.client.ui.views.GroupMembershipStatusView;

import java.util.ArrayList;

/**
 * Groups list adapter
 *
 * @author Pierre HUBERT
 */
public class GroupsListAdapter extends BaseRecyclerViewAdapter {

    /**
     * The list of groups
     */
    private ArrayList<GroupInfo> mList = new ArrayList<>();

    private OnGroupMembershipUpdateListener mOnGroupMembershipUpdateListener;

    public GroupsListAdapter(Context context) {
        super(context);
    }

    /**
     * Set the list of groups
     *
     * @param List The list of groups
     */
    public void setList(ArrayList<GroupInfo> List) {
        this.mList = List;
    }

    /**
     * Set the group membership update listener
     *
     * @param onGroupMembershipUpdateListener The listener
     */
    public void setOnGroupMembershipUpdateListener(OnGroupMembershipUpdateListener onGroupMembershipUpdateListener) {
        this.mOnGroupMembershipUpdateListener = onGroupMembershipUpdateListener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.viewholder_group, viewGroup, false);
        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((GroupHolder)viewHolder).bind(i);
    }


    /**
     * Single group holder class
     */
    private class GroupHolder extends RecyclerView.ViewHolder {

        private GroupImageView mGroupImageView;
        private TextView mGroupName;
        private GroupMembershipStatusView mGroupMembershipStatus;

        GroupHolder(@NonNull View itemView) {
            super(itemView);

            mGroupImageView = itemView.findViewById(R.id.groupImage);
            mGroupName = itemView.findViewById(R.id.groupName);
            mGroupMembershipStatus = itemView.findViewById(R.id.groupMembershipStatusView);

            mGroupMembershipStatus.setOnGroupMembershipUpdateListener(mOnGroupMembershipUpdateListener);
        }

        GroupInfo getGroup(int pos){
            return mList.get(pos);
        }

        void bind(int pos){
            GroupInfo groupInfo = getGroup(pos);
            mGroupImageView.setGroup(groupInfo);
            mGroupName.setText(groupInfo.getDisplayName());
            mGroupMembershipStatus.setGroup(groupInfo);
        }
    }
}
