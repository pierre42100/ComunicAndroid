package org.communiquons.android.comunic.client.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.posts.Post;

/**
 * Posts creation form
 *
 * Created by pierre on 4/1/18.
 * @author Pierre HUBERT
 */

public class PostsCreateFormFragment extends Fragment {

    /**
     * The argument that contains ID of the target page
     */
    public static final String PAGE_ID_ARG = "ID";

    /**
     * The name of the argument that contains the type of the page
     */
    public static final String PAGE_TYPE_ARG = "PAGE_TYPE";

    /**
     * Page type : user page
     */
    public static final int PAGE_TYPE_USER = 1;

    /**
     * On post created interface
     */
    private OnPostCreated mOnPostCreated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_create_form, container, false);
    }

    /**
     * Set the on post created class to trigger when an event occur
     *
     * @param onPostCreated The interface to call
     */
    public void setOnPostCreatedListener(OnPostCreated onPostCreated) {
        this.mOnPostCreated = onPostCreated;
    }

    /**
     * This interface is called when a post is created
     */
    interface OnPostCreated{

        /**
         * This method is called with the created post
         *
         * @param post The created post
         */
        void onPostCreated(Post post);
    }
}
