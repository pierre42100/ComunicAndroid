package org.communiquons.android.comunic.client.ui.listeners;

import android.view.View;

import org.communiquons.android.comunic.client.data.models.Comment;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.ui.views.EditCommentContentView;

/**
 * This interface is used to redirect post update events to the fragment managing the
 * rendering of the posts
 *
 * @author Pierre HUBERT
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
