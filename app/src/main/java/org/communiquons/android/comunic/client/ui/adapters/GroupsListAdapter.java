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
import org.communiquons.android.comunic.client.ui.listeners.OnGroupActionListener;
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

    private OnGroupActionListener mOnGroupActionListener;

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
     * Set the group action listener
     *
     * @param onGroupActionListener The listener
     */
    public void setOnGroupActionListener(OnGroupActionListener onGroupActionListener) {
        this.mOnGroupActionListener = onGroupActionListener;
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
    private class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private GroupInfo mGroupInfo;


        private GroupImageView mGroupImageView;
        private TextView mGroupName;
        private GroupMembershipStatusView mGroupMembershipStatus;

        GroupHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mGroupImageView = itemView.findViewById(R.id.groupImage);
            mGroupName = itemView.findViewById(R.id.groupName);
            mGroupMembershipStatus = itemView.findViewById(R.id.groupMembershipStatusView);

            mGroupMembershipStatus.setOnGroupMembershipUpdateListener(mOnGroupActionListener);
        }

        GroupInfo getGroup(int pos){
            return mList.get(pos);
        }

        void bind(int pos){
            mGroupInfo = getGroup(pos);
            mGroupImageView.setGroup(mGroupInfo);
            mGroupName.setText(mGroupInfo.getDisplayName());
            mGroupMembershipStatus.setGroup(mGroupInfo);
        }

        @Override
        public void onClick(View v) {
            if(v.equals(itemView))
                mOnGroupActionListener.onOpenGroup(mGroupInfo.getId());
        }
    }
}
