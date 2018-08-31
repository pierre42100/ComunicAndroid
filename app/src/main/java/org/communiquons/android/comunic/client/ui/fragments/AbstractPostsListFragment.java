package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.enums.LikesType;
import org.communiquons.android.comunic.client.data.helpers.CommentsHelper;
import org.communiquons.android.comunic.client.data.helpers.DatabaseHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.LikesHelper;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;
import org.communiquons.android.comunic.client.data.models.Comment;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.data.utils.StringsUtils;
import org.communiquons.android.comunic.client.ui.adapters.PostsAdapter;
import org.communiquons.android.comunic.client.ui.listeners.OnScrollChangeDetectListener;
import org.communiquons.android.comunic.client.ui.listeners.onPostUpdateListener;
import org.communiquons.android.comunic.client.ui.views.EditCommentContentView;
import org.communiquons.android.comunic.client.ui.views.ScrollRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Posts list fragment
 *
 * Note : this fragment IS NOT MADE to be used in standalone mode !!!
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/18/18.
 */

abstract class AbstractPostsListFragment extends Fragment
    implements onPostUpdateListener, OnScrollChangeDetectListener, PostsCreateFormFragment.OnPostCreated {

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
    private PostsList mPostsList;

    /**
     * Post adapter
     */
    private PostsAdapter mPostsAdapter;

    /**
     * Posts helper
     */
    private PostsHelper mPostsHelper;

    /**
     * Comments helper
     */
    private CommentsHelper mCommentsHelper;

    /**
     * Users helper
     */
    private GetUsersHelper mUserHelper;

    /**
     * Likes helper
     */
    private LikesHelper mLikesHelper;

    /**
     * Views
     */
    private ScrollRecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Button mCreatePostButton;
    private FrameLayout mCreatePostLayout;
    private TextView mNoPostsNotice;

    /**
     * Arguments used to create post form
     */
    Bundle mCreateFormArgs;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_postslist, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get views
        mRecyclerView = view.findViewById(R.id.posts_list);
        mProgressBar = view.findViewById(R.id.progressBar);
        mCreatePostButton = view.findViewById(R.id.create_post_btn);
        mCreatePostLayout = view.findViewById(R.id.create_post_form);
        mNoPostsNotice = view.findViewById(R.id.no_post_notice);

        //Setup posts list
        mRecyclerView.setOnScrollChangeDetectListener(this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        mCreatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPostFormVisibility(mCreatePostLayout.getVisibility() != View.VISIBLE);
            }
        });

        enablePostFormFragment(false);

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

    @Override
    public void onResume() {
        super.onResume();

        if(mPostsList == null) {
            setProgressBarVisibility(true);
            setNoPostsNoticeVisibility(false);
            onLoadPosts();
        }
        else{
            setPostsList(getPostsList());
            show_posts();
        }
    }

    /**
     * Set the list of posts of the fragment
     *
     * @param list The list of post
     */
    protected void setPostsList(PostsList list) {
        this.mPostsList = list;
        mPostsAdapter = null;
    }

    /**
     * Get the current list of posts
     *
     * @return The current list of post / null if none
     */
    @NonNull
    protected PostsList getPostsList(){
        return this.mPostsList;
    }

    /**
     * Check if a PostList has been set or not
     *
     * @return True if a list has been set / FALSE else
     */
    protected boolean hasPostsList(){
        return this.mPostsList != null;
    }

    /**
     * Load posts
     */
    public abstract void onLoadPosts();

    /**
     * Load more posts
     *
     * @param last_post_id The ID of the last post in the list
     */
    public abstract void onLoadMorePosts(int last_post_id);

    /**
     * This method is triggered when we have got a new list of posts to apply
     *
     * @param list The list
     */
    @CallSuper
    protected void onGotNewPosts(@Nullable PostsList list){

        setProgressBarVisibility(false);

        if(getActivity() == null)
            return;

        if(list == null){
            Toast.makeText(getActivity(), R.string.err_get_posts_list, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!list.hasUsersInfo()){
            Toast.makeText(getActivity(), R.string.err_get_user_info, Toast.LENGTH_SHORT).show();
            return;
        }

        if(mPostsList == null)
            mPostsList = list;
        else {
            //Merge posts list
            mPostsList.addAll(list);
            Objects.requireNonNull(mPostsList.getUsersInfo()).putAll(list.getUsersInfo());
        }

        show_posts();
    }

    /**
     * Display the list of posts
     */
    public void show_posts(){

        //Check if the view has not been created yet...
        if(getView() == null)
            return;

        //Check if the list of posts is not null
        if(mPostsList == null)
            return;

        //Create posts adapter (if required)
        if(mPostsAdapter == null) {
            mPostsAdapter = new PostsAdapter(getActivity(), mPostsList, this);

            //Connect the adapter to the view
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setAdapter(mPostsAdapter);
        }

        //Notify data set update
        mPostsAdapter.notifyDataSetChanged();

        //Update no post notice visibility
        setProgressBarVisibility(false);
        setNoPostsNoticeVisibility(mPostsList.size() == 0);
    }

    /**
     * Get the ID of the last post in the list
     *
     * @return The ID of the last post in the list / -1 in case of failure
     */
    public int getLastPostID(){
        if(!hasPostsList())
            return -1;
        if(getPostsList().size() < 1)
            return -1;
        return getPostsList().get(getPostsList().size() - 1).getId();
    }

    @Override
    public void onReachTop() {
        //Nothing
    }

    @Override
    public void onReachBottom() {
        if(hasPostsList() && getPostsList().size() > 0)
            onLoadMorePosts(getLastPostID());
    }


    protected void setProgressBarVisibility(boolean visible){
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    protected void setNoPostsNoticeVisibility(boolean visible){
        mNoPostsNotice.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    //
    //
    //
    //
    // Posts form
    //
    //
    //
    //

    /**
     * Specify whether post form should be enabled or not
     *
     * @param enable TRUE to enable / FALSE else
     */
    protected void enablePostFormFragment(boolean enable){
        mCreatePostButton.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    /**
     * Initialize posts fragment
     *
     * @param page_type The type of the page
     * @param page_id The ID of the page
     */
    protected void init_create_post_fragment(int page_type, int page_id){

        //Can not perform a transaction if the state has been saved
        if(isStateSaved())
            return;

        //Create bundle
        Bundle args = new Bundle();
        args.putInt(PostsCreateFormFragment.PAGE_TYPE_ARG, page_type);
        args.putInt(PostsCreateFormFragment.PAGE_ID_ARG, page_id);
        mCreateFormArgs = new Bundle(args);

        //Create fragment
        PostsCreateFormFragment fragment = new PostsCreateFormFragment();
        fragment.setArguments(args);
        fragment.setOnPostCreatedListener(this);

        //Perform transaction
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.create_post_form, fragment);
        transaction.commit();

        //Hide the post form by default
        setPostFormVisibility(false);
    }


    /**
     * Set post form visibility
     */
    protected void setPostFormVisibility(boolean visible){
        mCreatePostLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPostCreated(Post post) {
        init_create_post_fragment(mCreateFormArgs.getInt(PostsCreateFormFragment.PAGE_TYPE_ARG),
                mCreateFormArgs.getInt(PostsCreateFormFragment.PAGE_ID_ARG));
        setPostsList(null);
        setProgressBarVisibility(true);
        setNoPostsNoticeVisibility(false);
        onLoadPosts();
    }



    //
    //
    //
    //
    // Posts actions
    //
    //
    //
    //

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
                mPostsList.getUsersInfo().put(userInfoCommentPair.first.getId(), userInfoCommentPair.first);

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

                //Check if the current user can update the post or not
                if(!post.canUpdate())
                    menu.findItem(R.id.action_edit_post).setEnabled(false);
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
    public void onPostContentUpdate(final Post post) {

        //Inflate a view
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_post,
                null);
        ((EditText)view.findViewById(R.id.post_content_input)).setText(post.getContent());

        //Create a dialog
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.popup_editpost_title)
                .setNegativeButton(R.string.popup_editpost_cancel, null)
                .setView(view)

                .setPositiveButton(R.string.popup_editpost_confirm,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processPostUpdate(post, view);
                    }
                })

                .show();

    }

    /**
     * Process the update of a post content
     *
     * @param post The target post
     * @param editForm The form that contains the updated post
     */
    private void processPostUpdate(final Post post, View editForm){

        //Get the content of the post
        final String newContent = ((EditText)editForm.findViewById(R.id.post_content_input)).getText()+"";

        //Update the content of the post
        new AsyncTask<Void, Void, Post>(){

            @Override
            protected Post doInBackground(Void... params) {
                if(!mPostsHelper.update_content(post.getId(), newContent))
                    return null;

                return mPostsHelper.getSingle(post.getId());
            }

            @Override
            protected void onPostExecute(@Nullable Post newPost) {

                if(getActivity() == null)
                    return;

                if(newPost == null){
                    Toast.makeText(getActivity(), R.string.err_update_post_content,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Update the content of the post
                post.setContent(newPost.getContent());
                mPostsAdapter.notifyDataSetChanged();
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
                if(comment.getUserID() != AccountUtils.getID(getActivity())) {
                    menu.findItem(R.id.action_edit_comment).setEnabled(false);
                    menu.findItem(R.id.action_delete).setEnabled(false);
                }

                //Save information about the comment in the fragment
                MENU_ACTION = MENU_ACTION_COMMENTS;
                mCurrCommentInContextMenu = comment;
            }
        });

        //Show the context menu of the button
        button.showContextMenu();

    }

    @Override
    public void onCommentLikeUpdate(final Comment comment, final boolean is_liking) {

        //Update information in the comment object
        comment.setUser_like(is_liking);
        comment.setLikes(comment.getLikes() + (is_liking ? 1 : -1));

        //Perform the operation in the background
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return mLikesHelper.update(LikesType.COMMENT, comment.getId(), is_liking);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onUpdateCommentContent(final Comment comment) {

        //Inflate the content of the dialog
        View content = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_comment, null);
        final EditCommentContentView commentInput = content.findViewById(R.id.input_comment_content);
        commentInput.setText(comment.getContent());

        //Display a dialog
        new AlertDialog.Builder(getActivity())

                //Set general information
                .setTitle(R.string.popup_editcomment_title)
                .setNegativeButton(R.string.popup_editcomment_cancel, null)
                .setView(content)

                //Set positive action
                .setPositiveButton(R.string.popup_editcomment_confirm,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitNewComment(comment, commentInput.getText()+"");
                    }
                })

                .show();

    }

    /**
     * Submit the new content of the comment back to the server, after having done security check
     *
     * @param comment The comment to update
     * @param newContent The new content for the comment
     */
    private void submitNewComment(final Comment comment, final String newContent){

        //Check the length of the comment
        if(!StringsUtils.isValidForContent(newContent)){
            Toast.makeText(getActivity(), R.string.err_invalid_comment_content,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Try to update the content of the comment on the server
        new AsyncTask<Void, Void, Comment>(){

            @Override
            protected Comment doInBackground(Void... params) {

                //Try to update the comment
                if(!mCommentsHelper.editContent(comment.getId(), newContent))
                    return null;

                //Get a new version of the comment
                return mCommentsHelper.getInfosSingle(comment.getId());

            }

            @Override
            protected void onPostExecute(@Nullable Comment newComment) {

                //Check if the activity has been destroyed
                if(getActivity() == null)
                    return;

                //Check for errors
                if(newComment == null){
                    Toast.makeText(getActivity(), R.string.err_update_comment_content,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Update the comment content
                comment.setContent(newComment.getContent());
                mPostsAdapter.notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

            //Edit the content of the post if required
            if(item.getItemId() == R.id.action_edit_post){
                onPostContentUpdate(mPostsList.get(mNumCurrPostInContextMenu));
            }

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

            //Edit the comment
            if(item.getItemId() == R.id.action_edit_comment){
                onUpdateCommentContent(mCurrCommentInContextMenu);
                return true;
            }

            //Delete the comment
            if(item.getItemId() == R.id.action_delete){
                deleteComment(mCurrCommentInContextMenu);
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }
}
