package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.ui.listeners.OnGroupActionListener;
import org.communiquons.android.comunic.client.ui.viewholders.GroupViewHolder;

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
        return new GroupViewHolder(view, mOnGroupActionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((GroupViewHolder)viewHolder).bind(mList.get(i));
    }

}
