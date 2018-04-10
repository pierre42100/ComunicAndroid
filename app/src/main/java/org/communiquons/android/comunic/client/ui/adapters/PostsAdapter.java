package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.ImageLoadHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.Comment;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.data.enums.PostTypes;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.communiquons.android.comunic.client.ui.views.EditCommentContentView;
import org.communiquons.android.comunic.client.ui.views.LikeButtonView;

import java.util.ArrayList;

/**
 * Posts adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/21/18.
 */

public class PostsAdapter extends ArrayAdapter<Post>{

    /**
     * Debug tag
     */
    private static final String TAG = "PostsAdapter";

    /**
     * Informations about the users
     */
    private ArrayMap<Integer, UserInfo> mUsersInfos;

    /**
     * Utilities object
     */
    private Utilities utils;

    /**
     * Actions update listener
     */
    private onPostUpdate mListener;

    /**
     * Create the Post Adapter
     *
     * @param context The context of execution of the application
     * @param list The list of posts
     * @param usersInfos Informations about the user
     * @param listener Specify the listener to perform callback actions such as create a comment
     *                 for example
     */
    public PostsAdapter(Context context, PostsList list, ArrayMap<Integer, UserInfo> usersInfos,
                        onPostUpdate listener){
        super(context, 0, list);

        //Save the users info object
        mUsersInfos = usersInfos;

        //Create utilities object
        utils = new Utilities(getContext());

        mListener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        //Check if the view has to be inflated
        if(convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.post_item, parent, false);

        //Get information about the post and the user
        final Post post = getItem(position);
        assert post != null;
        UserInfo userInfo = null;
        if(mUsersInfos.containsKey(post.getUserID()))
            userInfo = mUsersInfos.get(post.getUserID());

        //Get the views related to user Information
        ImageView userAccountImage = convertView.findViewById(R.id.user_account_image);
        TextView userAccountName = convertView.findViewById(R.id.user_account_name);

        //Reset user information
        userAccountName.setText("");
        ImageLoadHelper.remove(userAccountImage);
        userAccountImage.setImageDrawable(UiUtils.getDrawable(getContext(),
                R.drawable.default_account_image));

        //Set user information if available
        if(userInfo != null){
            userAccountName.setText(userInfo.getDisplayFullName());
            ImageLoadHelper.load(getContext(), userInfo.getAcountImageURL(), userAccountImage);
        }


        //Set post creation time
        ((TextView) convertView.findViewById(R.id.post_creation_time)).setText(utils.
                timeToString(Utilities.time() - post.getPost_time()));


        //Set post visibility level
        TextView visibilityLevel = convertView.findViewById(R.id.post_visibility);
        switch (post.getVisibilityLevel()){

            case PUBLIC:
                visibilityLevel.setText(R.string.post_visibility_public);
                break;

            case FRIENDS:
                visibilityLevel.setText(R.string.post_visibility_friends);
                break;

            case PRIVATE:
            default:
                visibilityLevel.setText(R.string.post_visibility_private);
                break;
        }

        //Set post actions
        convertView.findViewById(R.id.post_actions_btn).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showPostActions(v, position, post);
            }
        });


        //Set post content
        ((TextView) convertView.findViewById(R.id.post_content)).setText(Utilities.prepareStringTextView(post.getContent()));

        //Set post image (if any)
        ImageView postImage = convertView.findViewById(R.id.post_image);
        postImage.setVisibility(View.GONE);
        postImage.setImageDrawable(null);
        ImageLoadHelper.remove(postImage);
        if(post.getType() == PostTypes.IMAGE){

            //Make image visible
            postImage.setVisibility(View.VISIBLE);

            //Load image
            ImageLoadHelper.load(getContext(), post.getFile_path_url(), postImage);
        }

        //Set posts likes
        LikeButtonView likeButtonView = convertView.findViewById(R.id.like_button);
        likeButtonView.setNumberLikes(post.getNumberLike());
        likeButtonView.setIsLiking(post.isLiking());
        likeButtonView.setUpdateListener(new LikeButtonView.OnLikeUpdateListener() {
            @Override
            public void OnLikeUpdate(boolean isLiking) {
                //Call listener
                mListener.onPostLikeUpdate(post, isLiking);
            }
        });

        //Process post comments
        ArrayList<Comment> comments = post.getComments_list();
        LinearLayout commentsView = convertView.findViewById(R.id.comments_list);
        commentsView.removeAllViews();
        if(comments != null) {

            //Show comments list
            convertView.findViewById(R.id.comments_list).setVisibility(View.VISIBLE);

            for (Comment comment : comments) {

                //Check if the comment has been deleted
                if(comment.isDeleted())
                    continue; //Skip comment

                //Try to find information about the user
                UserInfo commentUser = mUsersInfos.containsKey(comment.getUserID()) ?
                        mUsersInfos.get(comment.getUserID()) : null;

                //Inflate the view
                View commentView = CommentsAdapter.getInflatedView(getContext(), comment,
                        mListener, commentUser, commentsView);
                commentsView.addView(commentView);
            }
        }
        else {
            //Hide comments list
            convertView.findViewById(R.id.comments_list).setVisibility(View.GONE);
        }


        //Update comment creation form
        View commentCreationForm = convertView.findViewById(R.id.create_comment_form);
        EditCommentContentView input_comment = convertView.findViewById(R.id.input_comment_content);
        if(comments == null){

            //Hide comment creation form
            commentCreationForm.setVisibility(View.GONE);

        }
        else {

            //Display comment creation form
            commentCreationForm.setVisibility(View.VISIBLE);

            //Make sure the form is correctly set up
            if(input_comment.getPostID() != post.getId()){

                //Reset input comment
                input_comment.setPostID(post.getId());
                input_comment.setText("");

                //Make the send button lives
                final View finalConvertView = convertView;
                convertView.findViewById(R.id.comment_send_button)
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendComment(position, finalConvertView);
                    }
                });

            }
        }

        return convertView;
    }

    /**
     * Intend to send a new comment to the server
     *
     * @param pos The position of the post to update
     * @param container The container of the post item
     */
    private void sendComment(int pos, View container){

        //Get post information
        final Post post = getItem(pos);
        if(post==null)
            return;

        //Get view about the comment
        final EditCommentContentView commentInput = container.findViewById(R.id.input_comment_content);
        final ImageView sendButton = container.findViewById(R.id.comment_send_button);

        //Call interface
        mListener.onCreateComment(pos, sendButton, post, commentInput);

    }

    /**
     * This interface is used to redirect post update events to the fragment managing the
     * rendering of the posts
     */
    public interface onPostUpdate {

        /**
         * This method is called when a comment creation request is made
         *
         * @param pos The position of the post in the list
         * @param button The button triggered to submit comment creation
         * @param post Information about the target post
         * @param input The input where the comment comment was typed
         */
        void onCreateComment(int pos, View button, Post post, EditCommentContentView input);

        /**
         * Show the available actions for a post
         *
         * @param button The button that provoked event
         * @param pos The position of the comment
         * @param post the target post
         */
        void showPostActions(View button, int pos, Post post);

        /**
         * Handles the update of the likes of a post
         *
         * @param post The target post
         * @param is_liking New liking status
         */
        void onPostLikeUpdate(Post post, boolean is_liking);

        /**
         * Handles the update request of the content of a post
         *
         * @param post Target post
         */
        void onPostContentUpdate(Post post);

        /**
         * Handles the deletion process of a post
         *
         * @param pos The position of the post to delete
         */
        void deletePost(int pos);

        /**
         * Show the available actions for a comment
         *
         * @param button The button that provoked the event
         * @param comment Target comment for the actions
         */
        void showCommentActions(View button, Comment comment);

        /**
         * Handles the update of the likes of a comment
         *
         * @param comment The comment to update
         * @param is_liking The new liking status
         */
        void onCommentLikeUpdate(Comment comment, boolean is_liking);

        /**
         * Handles the update of the content of a comment
         *
         * @param comment The target comment
         */
        void onUpdateCommentContent(Comment comment);

        /**
         * Handles the process of deletion of a comment.
         *
         * @param comment The comment to delete
         */
        void deleteComment(Comment comment);

    }
}
