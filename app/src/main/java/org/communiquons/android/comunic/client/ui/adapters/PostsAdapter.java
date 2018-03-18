package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.DatabaseHelper;
import org.communiquons.android.comunic.client.data.ImageLoad.ImageLoadManager;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersHelper;
import org.communiquons.android.comunic.client.data.UsersInfo.GetUsersInfos;
import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;
import org.communiquons.android.comunic.client.data.comments.Comment;
import org.communiquons.android.comunic.client.data.comments.CommentsHelper;
import org.communiquons.android.comunic.client.data.posts.Post;
import org.communiquons.android.comunic.client.data.posts.PostTypes;
import org.communiquons.android.comunic.client.data.posts.PostsList;
import org.communiquons.android.comunic.client.data.utils.StringsUtils;
import org.communiquons.android.comunic.client.data.utils.UiUtils;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.communiquons.android.comunic.client.ui.views.EditCommentContentView;

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
     * Comments helper
     */
    private CommentsHelper mCommentsHelper;

    /**
     * Get user helper
     */
    private GetUsersHelper mUserHelper;

    /**
     * Create the Post Adapter
     *
     * @param context The context of execution of the application
     * @param list The list of posts
     * @param usersInfos Informations about the user
     */
    public PostsAdapter(Context context, PostsList list, ArrayMap<Integer, UserInfo> usersInfos){
        super(context, 0, list);

        //Save the users info object
        mUsersInfos = usersInfos;

        //Create utilities object
        utils = new Utilities(getContext());

        //Create get user helper object
        mUserHelper = new GetUsersHelper(getContext(), DatabaseHelper.getInstance(getContext()));

        //Create comments helper object
        mCommentsHelper = new CommentsHelper(context);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        //Check if the view has to be inflated
        if(convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.post_item, parent, false);

        //Get informations about the post and the user
        Post post = getItem(position);
        assert post != null;
        UserInfo userInfo = null;
        if(mUsersInfos.containsKey(post.getUserID()))
            userInfo = mUsersInfos.get(post.getUserID());

        //Get the views related to user Informations
        ImageView userAccountImage = convertView.findViewById(R.id.user_account_image);
        TextView userAccountName = convertView.findViewById(R.id.user_account_name);

        //Reset user informations
        userAccountName.setText("");
        ImageLoadManager.remove(userAccountImage);
        userAccountImage.setImageDrawable(UiUtils.getDrawable(getContext(),
                R.drawable.default_account_image));

        //Set user informations if available
        if(userInfo != null){
            userAccountName.setText(userInfo.getDisplayFullName());
            ImageLoadManager.load(getContext(), userInfo.getAcountImageURL(), userAccountImage);
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


        //Set post content
        ((TextView) convertView.findViewById(R.id.post_content)).setText(Utilities.prepareStringTextView(post.getContent()));

        //Set post image (if any)
        ImageView postImage = convertView.findViewById(R.id.post_image);
        postImage.setVisibility(View.GONE);
        postImage.setImageDrawable(null);
        ImageLoadManager.remove(postImage);
        if(post.getType() == PostTypes.IMAGE){

            //Make image visible
            postImage.setVisibility(View.VISIBLE);

            //Load image
            ImageLoadManager.load(getContext(), post.getFile_path_url(), postImage);
        }

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
                        commentUser, commentsView);
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

        //Get post informations
        final Post post = getItem(pos);
        if(post==null)
            return;

        //Get view about the comment
        final EditCommentContentView commentInput = container.findViewById(R.id.input_comment_content);
        final ImageButton sendButton = container.findViewById(R.id.comment_send_button);

        //Get informations about the comment
        final int postID = post.getId();
        final String content = commentInput.getText()+"";

        //Check the comment's validity
        if(!StringsUtils.isValidForContent(content)){
            Toast.makeText(getContext(), R.string.err_invalid_comment_content,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Lock send button
        sendButton.setClickable(false);

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

                //Get informations about the related user
                UserInfo user = mUserHelper.getSingle(comment.getUserID(), false);

                //Check for errors
                if(user == null)
                    return null;

                //Return result
                return Pair.create(user, comment);
            }

            @Override
            protected void onPostExecute(@Nullable Pair<UserInfo, Comment> userInfoCommentPair) {

                //Unlock send button
                sendButton.setClickable(true);

                //Check for errors
                if(userInfoCommentPair == null){
                    Toast.makeText(getContext(), R.string.err_create_comment,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Empty comment input
                commentInput.setText("");

                //Add the comment to the list
                ArrayList<Comment> comments = post.getComments_list();
                assert comments != null;
                comments.add(userInfoCommentPair.second);

                //Add the user to the list
                mUsersInfos.put(userInfoCommentPair.first.getId(), userInfoCommentPair.first);

                //Update data set
                notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
