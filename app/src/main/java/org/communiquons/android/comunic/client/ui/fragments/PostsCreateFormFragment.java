package org.communiquons.android.comunic.client.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.posts.CreatePost;
import org.communiquons.android.comunic.client.data.posts.Post;
import org.communiquons.android.comunic.client.data.posts.PostsHelper;

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

    /**
     * Post helper
     */
    private PostsHelper mPostHelper;

    /**
     * Submit form button
     */
    private Button mSendButton;

    /**
     * Post content
     */
    private EditText mPostContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize post helper
        mPostHelper = new PostsHelper(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_create_form, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get post text area
        mPostContent = view.findViewById(R.id.new_post_content);

        //Get send button and makes it lives
        mSendButton = view.findViewById(R.id.submit_create_post_form);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_form();
            }
        });
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
     * Submit create post form
     */
    private void submit_form(){

        //Check if the content of the post is empty / too short
        if(mPostContent.getText().length() < 5){
            Toast.makeText(getActivity(), R.string.err_post_content_too_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Create a post object and fill it with the required information
        CreatePost post = new CreatePost();

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
