package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.enums.KindSearchResult;
import org.communiquons.android.comunic.client.data.models.SearchResultWithInfo;
import org.communiquons.android.comunic.client.ui.listeners.OnOpenPageListener;
import org.communiquons.android.comunic.client.ui.viewholders.GroupViewHolder;
import org.communiquons.android.comunic.client.ui.viewholders.UserViewHolder;

import java.util.ArrayList;

/**
 * Search results adapter
 *
 * @author Pierre HUBERT
 */
public class SearchResultsAdapter extends BaseRecyclerViewAdapter {

    /**
     * The list of results to the search
     */
    private ArrayList<SearchResultWithInfo> mList;

    /**
     * Open listener
     */
    private OnOpenPageListener openPageListener;

    /**
     * Get view types
     */
    private final int VIEW_TYPE_USER = 0;
    private final int VIEW_TYPE_GROUP = 1;

    public SearchResultsAdapter(Context context, @Nullable OnOpenPageListener openPageListener) {
        super(context);

        this.mList = new ArrayList<>();
        this.openPageListener = openPageListener;
    }

    /**
     * Set (update) the list of search results
     *
     * @param list New list to apply
     */
    public void setList(ArrayList<SearchResultWithInfo> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getKind() == KindSearchResult.USER ?
                VIEW_TYPE_USER : VIEW_TYPE_GROUP;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {

        RecyclerView.ViewHolder viewHolder;

        if(type == VIEW_TYPE_USER){
            viewHolder = new UserViewHolder(LayoutInflater
                        .from(getContext())
                        .inflate(R.layout.viewholder_user, viewGroup, false)
                    , openPageListener);
        }

        else if(type == VIEW_TYPE_GROUP){
            viewHolder = new GroupViewHolder(LayoutInflater
                        .from(getContext())
                        .inflate(R.layout.viewholder_group, viewGroup, false)
                    , openPageListener);
        }

        else
            throw new RuntimeException("Intend to display a none supported kind of result!");


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        SearchResultWithInfo searchResultWithInfo = mList.get(i);

        if(searchResultWithInfo.getKind() == KindSearchResult.USER){
            ((UserViewHolder)viewHolder).bind(searchResultWithInfo.getUserInfo());
        }

        if(searchResultWithInfo.getKind() == KindSearchResult.GROUP){
            ((GroupViewHolder)viewHolder).bind(searchResultWithInfo.getGroupInfo());
        }
    }


}
