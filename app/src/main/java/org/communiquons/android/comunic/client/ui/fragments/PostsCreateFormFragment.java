package org.communiquons.android.comunic.client.ui.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.CreatePost;
import org.communiquons.android.comunic.client.data.enums.PageType;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.data.enums.PostTypes;
import org.communiquons.android.comunic.client.data.enums.PostVisibilityLevels;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;

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

        //Determine the type and the ID of the page
        switch (getArguments().getInt(PAGE_TYPE_ARG)){
            case PAGE_TYPE_USER:
                post.setPage_type(PageType.USER_PAGE);
        }
        post.setPage_id(getArguments().getInt(PAGE_ID_ARG));

        //Set post content
        post.setContent(""+mPostContent.getText());

        //Default value, will be updated in a new version
        post.setType(PostTypes.TEXT);
        post.setVisibilityLevel(PostVisibilityLevels.FRIENDS);

        //Try to create post
        mSendButton.setEnabled(false);
        new AsyncTask<CreatePost, Void, Post>(){

            @Override
            protected Post doInBackground(CreatePost... params) {
                int postID = mPostHelper.create(params[0]);
                return postID == -1 ? null : mPostHelper.getSingle(postID);
            }

            @Override
            protected void onPostExecute(Post post) {
                if(getActivity() == null)
                    return;

                postCreationCallback(post);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, post);
    }

    /**
     * Post creation callback
     *
     * @param post The created post
     */
    private void postCreationCallback(@Nullable Post post){

        //Check for errors
        if(post == null){
            mSendButton.setEnabled(true);
            Toast.makeText(getActivity(), R.string.err_submit_post, Toast.LENGTH_SHORT).show();
            return;
        }

        //Success - call callback (if any)
        if(mOnPostCreated != null)
            mOnPostCreated.onPostCreated(post);
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
