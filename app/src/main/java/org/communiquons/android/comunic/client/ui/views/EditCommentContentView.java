package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Comments creation input view
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/12/18.
 */

public class EditCommentContentView extends android.support.v7.widget.AppCompatEditText {

    /**
     * The ID of the target post
     */
    private int postID;

    public EditCommentContentView(Context context) {
        super(context);
    }

    public EditCommentContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditCommentContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set the ID of the related post
     *
     * @param postID The ID of the post
     */
    public void setPostID(int postID) {
        this.postID = postID;
    }

    /**
     * Get the ID of the related post
     *
     * @return The ID of the related post
     */
    public int getPostID() {
        return postID;
    }
}
