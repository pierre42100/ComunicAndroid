package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.Account.AccountUtils;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.comments.Comment;
import org.communiquons.android.comunic.client.data.comments.CommentsHelper;
import org.communiquons.android.comunic.client.data.likes.LikesHelper;
import org.communiquons.android.comunic.client.data.likes.LikesType;
import org.communiquons.android.comunic.client.data.posts.Post;
import org.communiquons.android.comunic.client.data.posts.PostsHelper;
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
     * Menu action : no action
     */
    private int MENU_ACTION_NONE = 0;

    /**
     * Menu action : comment actions
     */
    private int MENU_ACTION_COMMENTS = 1;

    /**
     * Menu action : post actions
     */
    private int MENU_ACTIONS_POST = 2;

    /**
     * The current menu action
     */
    private int MENU_ACTION = MENU_ACTION_NONE;

    /**
     * Current processed comment that context menu display actions for
     */
    private Comment mCurrCommentInContextMenu;

    /**
     * Current processed post that context menu displays actions for
     */
    private int mNumCurrPostInContextMenu;

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
     * Posts helper
     */
    PostsHelper mPostsHelper;


    /**
     * Comments helper
     */
    CommentsHelper mCommentsHelper;

    /**
     * Users helper
     */
    GetUsersHelper mUserHelper;

    /**
     * Likes helper
     */
    LikesHelper mLikesHelper;

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

    @Override
    public void onStart() {
        super.onStart();

        //Create post helper
        mPostsHelper = new PostsHelper(getActivity());

        //Create comment helper
        mCommentsHelper = new CommentsHelper(getActivity());

        //Create user helper
        mUserHelper = new GetUsersHelper(getActivity(), DatabaseHelper.getInstance(getActivity()));

        //Create likes helper
        mLikesHelper = new LikesHelper(getActivity());
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

    @Override
    public void showPostActions(View button, final int pos, final Post post) {

        button.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {

                //Inflate the menu
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.menu_post_actions, menu);

                //Save information about the post
                MENU_ACTION = MENU_ACTIONS_POST;
                mNumCurrPostInContextMenu = pos;

                //Disable some options if the user is not the post owner
                if(!post.canDelete()){

                    //Disable delete action
                    menu.findItem(R.id.action_delete).setEnabled(false);

                }
            }
        });

        //Show context menu
        button.showContextMenu();
    }

    @Override
    public void onPostLikeUpdate(final Post post, final boolean is_liking) {

        //Save new post information
        post.setNumberLike(post.getNumberLike() + (is_liking ? 1 : -1));
        post.setLiking(is_liking);

        //Perform the update in the background
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return mLikesHelper.update(LikesType.POST, post.getId(), is_liking);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void deletePost(final int pos) {

        //Get information about the related post
        final Post post = mPostsList.get(pos);

        //Ask user confirmation
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.popup_deletepost_title)
                .setMessage(R.string.popup_deletepost_message)
                .setNegativeButton(R.string.popup_deletepost_cancel, null)


                .setPositiveButton(R.string.popup_deletepost_confirm,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Remove the post from the list
                        mPostsList.remove(pos);
                        mPostsAdapter.notifyDataSetChanged();

                        //Perform post deletion
                        new AsyncTask<Integer, Void, Boolean>(){

                            @Override
                            protected Boolean doInBackground(Integer... params) {
                                return mPostsHelper.delete(params[0]);
                            }

                            @Override
                            protected void onPostExecute(Boolean result) {

                                if(getActivity() == null)
                                    return;

                                if(!result){
                                    Toast.makeText(getActivity(), R.string.err_delete_post,
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, post.getId());

                    }
                })


                .show();

    }

    @Override
    public void showCommentActions(View button, final Comment comment) {

        //Prepare context menu button rendering
        button.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {

                //Inflate the menu
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.menu_comments_actions, menu);

                //Disable moderation actions if required
                if(comment.getUserID() != AccountUtils.getID(getActivity()))
                    menu.findItem(R.id.action_delete).setEnabled(false);

                //Save information about the comment in the fragment
                MENU_ACTION = MENU_ACTION_COMMENTS;
                mCurrCommentInContextMenu = comment;
            }
        });

        //Show the context menu of the button
        button.showContextMenu();

    }

    @Override
    public void deleteComment(final Comment comment) {

        //Show a confirmation dialog
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.popup_deletecomment_title)
                .setMessage(R.string.popup_deletecomment_message)
                .setNegativeButton(R.string.popup_deletecomment_cancel, null)

                //Set confirmation action
                .setPositiveButton(R.string.popup_deletecomment_confirm,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Mark the comment as deleted and refresh the list of comments
                        comment.setDeleted(true);
                        mPostsAdapter.notifyDataSetChanged();

                        //Perform a deletion request on the server
                        new AsyncTask<Integer, Void, Boolean>(){

                            @Override
                            protected Boolean doInBackground(Integer... params) {
                                return mCommentsHelper.delete(params[0]);
                            }

                            @Override
                            protected void onPostExecute(Boolean res) {
                                if(getActivity() == null)
                                    return;

                                if(!res)
                                    Toast.makeText(getActivity(), R.string.err_delete_comment,
                                            Toast.LENGTH_SHORT).show();
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, comment.getId());
                    }
                })
                .show();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //Check if this fragment has recently created a menu or not
        if(MENU_ACTION == MENU_ACTION_NONE)
            return false;

        //Check if the action is related to a post
        if(MENU_ACTION == MENU_ACTIONS_POST){

            //Check whether the related post exists or not
            if(!(mPostsList.size() > mNumCurrPostInContextMenu))
                return false;

            //Check if the request is to delete the comment
            if(item.getItemId() == R.id.action_delete) {
                deletePost(mNumCurrPostInContextMenu);
                return true;
            }
        }

        //Check if a comment action context menu has been created
        if(MENU_ACTION == MENU_ACTION_COMMENTS){

            //Check for comment information
            if(mCurrCommentInContextMenu == null)
                return false;

            //Check the action to perform
            if(item.getItemId() == R.id.action_delete){
                deleteComment(mCurrCommentInContextMenu);
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }
}
