package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.enums.PageType;
import org.communiquons.android.comunic.client.data.enums.PostTypes;
import org.communiquons.android.comunic.client.data.enums.PostVisibilityLevels;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;
import org.communiquons.android.comunic.client.data.models.CreatePost;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.ui.utils.BitmapUtils;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;
import static org.communiquons.android.comunic.client.ui.Constants.IntentRequestCode.POST_CREATE_FORM_PICK_PHOTO;

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
     * Select image button
     */
    private ImageButton mPickImageButton;

    /**
     * Submit form button
     */
    private Button mSendButton;

    /**
     * Post content
     */
    private EditText mPostContent;

    /**
     * User picked bitmap to include with the post
     */
    private Bitmap mNewPicture;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize post helper
        mPostHelper = new PostsHelper(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_create_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get post text area
        mPostContent = view.findViewById(R.id.new_post_content);

        //Get choose image button and makes it lives
        mPickImageButton = view.findViewById(R.id.select_image_button);
        mPickImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                choosePicture();
            }
        });
        mPickImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cancelPictureSelectionDialog();
                return true;
            }
        });

        //Get send button and makes it lives
        mSendButton = view.findViewById(R.id.submit_create_post_form);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_form();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case POST_CREATE_FORM_PICK_PHOTO:
                pick_picture_callback(resultCode, data);
                break;

        }
    }

    /**
     * Offer the user to choose a picture to add to his post
     */
    private void choosePicture() {

        //Make an intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, POST_CREATE_FORM_PICK_PHOTO);

    }

    /**
     * Callback called when a picture has been selected
     *
     * @param resultCode The result of the operation
     * @param data Data to process
     */
    private void pick_picture_callback(int resultCode, Intent data){

        if(resultCode == RESULT_OK){

            try {

                //Get bitmap
                Bitmap bitmap = BitmapUtils.IntentResultToBitmap(getActivity(), data);

                //Check for errors
                if(bitmap == null)
                    return;

                //Apply bitmap
                mPickImageButton.setImageBitmap(bitmap);
                mNewPicture = bitmap;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                cancelPictureSelectionDialog();
            }

        }

    }

    /**
     * Offer the user to cancel his choice of picture
     */
    private void cancelPictureSelectionDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.conversation_message_remove_image_popup_title)
                .setMessage(R.string.conversation_message_remove_image_popup_message)
                .setNegativeButton(R.string.conversation_message_remove_image_popup_cancel, null)

                .setPositiveButton(R.string.conversation_message_remove_image_popup_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeChosenPicture();
                            }
                        })

                .show();
    }

    /**
     * Remove the picture chosen by the user
     */
    private void removeChosenPicture(){

        //Free memory
        if(mNewPicture != null)
            mNewPicture.recycle();
        mNewPicture = null;

        //Reset image button
        mPickImageButton.setImageBitmap(null);
        mPickImageButton.setImageDrawable(UiUtils.getDrawable(getActivity(),
                android.R.drawable.ic_menu_gallery));

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

        //Default values
        post.setType(PostTypes.TEXT);
        post.setVisibilityLevel(PostVisibilityLevels.FRIENDS);

        //Check if the post contains an image
        if(mNewPicture != null){
            post.setType(PostTypes.IMAGE);
            post.setNewImage(mNewPicture);
        }


        //Other post types will be added in new versions

        //Perform post checks
        //If the post is a text
        if(post.getType() == PostTypes.TEXT){

            //Check if the content of the post is empty / too short
            if(mPostContent.getText().length() < 5){
                Toast.makeText(getActivity(), R.string.err_post_content_too_short,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

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
