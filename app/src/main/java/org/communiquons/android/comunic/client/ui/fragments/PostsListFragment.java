package org.communiquons.android.comunic.client.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.posts.PostsList;
import org.communiquons.android.comunic.client.ui.adapters.PostsAdapter;

/**
 * Posts list fragment
 *
 * Note : this fragment IS NOT MADE to be used in standalone mode !!!
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/18/18.
 */

public class PostsListFragment extends Fragment {

    /**
     * The list of posts
     */
    PostsList mPostsList;

    /**
     * Informations about the related users
     */
    ArrayMap<Integer, UserInfo> mUsersInfo;

    /**
     * Post adapter
     */
    PostsAdapter mPostsAdapter;

    /**
     * The list of posts
     */
    ListView mListView;

    /**
     * Set the list of posts of the fragment
     *
     * @param list The list of post
     */
    public void setPostsList(PostsList list) {
        this.mPostsList = list;
    }

    /**
     * Set the list of users informations
     *
     * @param list The list
     */
    public void setUsersInfos(ArrayMap<Integer, UserInfo> list){
        this.mUsersInfo = list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_postslist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the list view
        mListView = view.findViewById(R.id.posts_list);

        //Show the posts
        show();
    }

    public void show(){

        //Check if the list of posts is not null
        if(mPostsList == null && mUsersInfo == null)
            return;

        //Create posts adapter (if required)
        if(mPostsAdapter == null) {
            mPostsAdapter = new PostsAdapter(getActivity(), mPostsList, mUsersInfo);

            //Connect the adapter to the view
            mListView.setAdapter(mPostsAdapter);
        }

        //Notify dataset update
        mPostsAdapter.notifyDataSetChanged();
    }
}
