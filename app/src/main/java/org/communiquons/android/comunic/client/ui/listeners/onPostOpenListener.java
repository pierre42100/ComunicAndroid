package org.communiquons.android.comunic.client.ui.listeners;

/**
 * This interface must be implemented by all the activities that can open post
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/12/18.
 */

public interface onPostOpenListener {

    /**
     * This method is triggered when a request to open a single post is made
     *
     * @param postID The ID of the post to open
     */
    void onOpenPost(int postID);

}
