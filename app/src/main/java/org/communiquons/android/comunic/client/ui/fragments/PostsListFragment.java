package org.communiquons.android.comunic.client.ui.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.comments.Comment;
import org.communiquons.android.comunic.client.data.comments.CommentsHelper;
import org.communiquons.android.comunic.client.data.posts.Post;
import org.communiquons.android.comunic.client.data.posts.PostsList;
import org.communiquons.android.comunic.client.data.utils.StringsUtils;
import org.communiquons.android.comunic.client.ui.adapters.PostsAdapter;
import org.communiquons.android.comunic.client.ui.views.EditCommentContentView;

import java.util.ArrayList;

/**
 * Posts list fragment
 *
 * Note : this fragment IS NOT MADE to be used in standalone mode !!!
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/18/18.
 */

public class PostsListFragment extends Fragment
    implements PostsAdapter.onPostUpdate {

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
     * Comments helper
     */
    CommentsHelper mCommentsHelper;

    /**
     * Users helper
     */
    GetUsersHelper mUserHelper;

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

    /**
     * Display the list of posts
     */
    public void show(){

        //Create comment helper
        mCommentsHelper = new CommentsHelper(getActivity());

        //Create user helper
        mUserHelper = new GetUsersHelper(getActivity(), DatabaseHelper.getInstance(getActivity()));

        //Check if the list of posts is not null
        if(mPostsList == null && mUsersInfo == null)
            return;

        //Create posts adapter (if required)
        if(mPostsAdapter == null) {
            mPostsAdapter = new PostsAdapter(getActivity(), mPostsList, mUsersInfo, this);

            //Connect the adapter to the view
            mListView.setAdapter(mPostsAdapter);
        }

        //Notify data set update
        mPostsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateComment(int pos, final View button, final Post post,
                                final EditCommentContentView input) {

        //Get information about the comment
        final int postID = post.getId();
        final String content = input.getText()+"";

        //Check the comment's validity
        if(!StringsUtils.isValidForContent(content)){
            Toast.makeText(getActivity(), R.string.err_invalid_comment_content,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Lock send button
        button.setClickable(false);

        //Submit the comment in a separate thread
        new AsyncTask<Void, Void, Pair<UserInfo, Comment>>(){

            @Override
            @Nullable
            protected Pair<UserInfo, Comment> doInBackground(Void... params) {

                //Try to create the comment
                int commentID = mCommentsHelper.send_comment(postID, content);

                //Check for errors
                if(commentID < 1)
                    return null;

                //Get information about the newly created comment
                Comment comment = mCommentsHelper.getInfosSingle(commentID);

                //Check for errors
                if(comment == null)
                    return null;

                //Get information about the related user
                UserInfo user = mUserHelper.getSingle(comment.getUserID(), false);

                //Check for errors
                if(user == null)
                    return null;

                //Return result
                return Pair.create(user, comment);
            }

            @Override
            protected void onPostExecute(@Nullable Pair<UserInfo, Comment> userInfoCommentPair) {

                //Check if the activity has been destroyed
                if(getActivity() == null)
                    return;

                //Unlock send button
                button.setClickable(true);

                //Check for errors
                if(userInfoCommentPair == null){
                    Toast.makeText(getActivity(), R.string.err_create_comment,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Empty comment input
                input.setText("");

                //Add the comment to the list
                ArrayList<Comment> comments = post.getComments_list();
                assert comments != null;
                comments.add(userInfoCommentPair.second);

                //Add the user to the list
                mUsersInfo.put(userInfoCommentPair.first.getId(), userInfoCommentPair.first);

                //Update data set
                mPostsAdapter.notifyDataSetChanged();

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
