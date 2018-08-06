package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.ImageLoadHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.Comment;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.data.enums.PostTypes;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.ui.listeners.onPostUpdateListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.communiquons.android.comunic.client.ui.views.EditCommentContentView;
import org.communiquons.android.comunic.client.ui.views.LikeButtonView;
import org.communiquons.android.comunic.client.ui.views.MovieView;
import org.communiquons.android.comunic.client.ui.views.PDFLinkButtonView;
import org.communiquons.android.comunic.client.ui.views.WebImageView;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

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
     * Information about the users
     */
    private ArrayMap<Integer, UserInfo> mUsersInfos;

    /**
     * Utilities object
     */
    private Utilities utils;

    /**
     * Actions update listener
     */
    private onPostUpdateListener mListener;

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
                        onPostUpdateListener listener){
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
        WebUserAccountImage userAccountImage = convertView.findViewById(R.id.user_account_image);
        TextView userAccountName = convertView.findViewById(R.id.user_account_name);

        //Set user information if available
        if(userInfo != null){
            userAccountName.setText(userInfo.getDisplayFullName());
            userAccountImage.setUser(userInfo);
        }
        else {
            //Reset user information
            userAccountName.setText("");
            userAccountImage.removeUser();
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

            case MEMBERS:
                visibilityLevel.setText(R.string.post_visibility_members);
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
        WebImageView postImage = convertView.findViewById(R.id.post_image);
        if(post.getType() == PostTypes.IMAGE){

            //Make image visible
            postImage.setVisibility(View.VISIBLE);

            //Load image
            postImage.loadURL(post.getFile_path_url());
        }
        else {

            //Hide the image
            postImage.setVisibility(View.GONE);

            //Remove the image
            postImage.removeImage();

        }


        //Set post movie (if any)
        MovieView movieView = convertView.findViewById(R.id.post_movie);

        if(post.getType() == PostTypes.MOVIE){
            movieView.setVisibility(View.VISIBLE);
            movieView.setMovie(post.getMovie());
        }

        else {
            movieView.setVisibility(View.GONE);
        }


        //Set post file PDF (if any)
        PDFLinkButtonView pdfLinkButtonView = convertView.findViewById(R.id.btn_pdf_link);

        if(post.getType() != PostTypes.PDF)
            pdfLinkButtonView.setVisibility(View.GONE);
        else {
            pdfLinkButtonView.setVisibility(View.VISIBLE);
            pdfLinkButtonView.setPDFUrl(post.getFile_path_url());
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

                //Make the comment input behaves like the send button when the user hit the
                //enter key
                input_comment.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if(event.getAction() == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_ENTER){
                            sendComment(position, finalConvertView);
                        }

                        return false;
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

}
