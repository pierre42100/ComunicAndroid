package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.enums.PageType;
import org.communiquons.android.comunic.client.data.models.Comment;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.utils.TimeUtils;
import org.communiquons.android.comunic.client.ui.listeners.onPostUpdateListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.android.comunic.client.ui.views.ContentTextView;
import org.communiquons.android.comunic.client.ui.views.CountDownView;
import org.communiquons.android.comunic.client.ui.views.EditCommentContentView;
import org.communiquons.android.comunic.client.ui.views.EnlargeableWebImageView;
import org.communiquons.android.comunic.client.ui.views.LikeButtonView;
import org.communiquons.android.comunic.client.ui.views.MovieView;
import org.communiquons.android.comunic.client.ui.views.PDFLinkButtonView;
import org.communiquons.android.comunic.client.ui.views.SurveyView;
import org.communiquons.android.comunic.client.ui.views.WebLinkView;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

import java.util.ArrayList;

/**
 * Posts adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/21/18.
 */

public class PostsAdapter extends BaseRecyclerViewAdapter {

    /**
     * Debug tag
     */
    private static final String TAG = PostsAdapter.class.getCanonicalName();

    /**
     * View types
     */
    private static final int VIEW_TYPE_POST_TEXT = 0;
    private static final int VIEW_TYPE_POST_IMAGE = 1;
    private static final int VIEW_TYPE_POST_MOVIE = 2;
    private static final int VIEW_TYPE_POST_PDF = 3;
    private static final int VIEW_TYPE_POST_WEBLINK = 4;
    private static final int VIEW_TYPE_POST_COUNTDOWN = 5;
    private static final int VIEW_TYPE_POST_SURVEY = 6;

    /**
     * Posts list
     */
    private PostsList mList;

    /**
     * Specify whether the posts target should be shown or not
     */
    private boolean mDisplayPostsTarget = true;

    /**
     * Actions update listener
     */
    private onPostUpdateListener mListener;

    /**
     * Create the Post Adapter
     *
     * @param context The context of execution of the application
     * @param list The list of posts
     * @param listener Specify the listener to perform callback actions such as create a comment
     *                 for example
     */
    public PostsAdapter(Context context, PostsList list, onPostUpdateListener listener){
        super(context);

        mList = list;

        mListener = listener;
    }

    /**
     * Specify whether the target of the posts should be shown or not
     *
     * @param displayPostsTarget TRUE to display posts target / FALSE else
     */
    public void setDisplayPostsTarget(boolean displayPostsTarget) {
        this.mDisplayPostsTarget = displayPostsTarget;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (mList.get(position).getType()){

            case IMAGE:
                return VIEW_TYPE_POST_IMAGE;

            case PDF:
                return VIEW_TYPE_POST_PDF;

            case WEBLINK:
                return VIEW_TYPE_POST_WEBLINK;

            case MOVIE:
                return VIEW_TYPE_POST_MOVIE;

            case COUNTDOWN:
                return VIEW_TYPE_POST_COUNTDOWN;

            case SURVEY:
                return VIEW_TYPE_POST_SURVEY;

            case TEXT:
            default:
                return VIEW_TYPE_POST_TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.post_item, viewGroup,
                false);

        switch (type){
            case VIEW_TYPE_POST_IMAGE:
                return new ImagePostHolder(view);

            case VIEW_TYPE_POST_PDF:
                return new PDFPostHolder(view);

            case VIEW_TYPE_POST_WEBLINK:
                return new WebLinkPostHolder(view);

            case VIEW_TYPE_POST_MOVIE:
                return new MoviePostHolder(view);

            case VIEW_TYPE_POST_COUNTDOWN:
                return new CountdownPostHolder(view);

            case VIEW_TYPE_POST_SURVEY:
                return new SurveyPostHolder(view);

                default:
                    return new TextPostHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((TextPostHolder)viewHolder).bind(position);
    }


    /**
     * Text posts holder
     */
    private class TextPostHolder extends RecyclerView.ViewHolder {

        private WebUserAccountImage mUserAccountImage;
        private TextView mUserAccountName;
        private ImageView mPostTargetArrow;
        private TextView mTargetPageName;
        private TextView mPostDate;
        private ImageView mPostVisibility;
        private ImageView mPostActions;
        private FrameLayout mAdditionnalViews;
        private ContentTextView mPostContent;
        private LikeButtonView mLikeButton;
        private LinearLayout mCommentsList;
        private LinearLayout mCreateCommentForm;
        private EditCommentContentView mEditCommentContentView;
        private ImageView mSendCommentButton;

        TextPostHolder(@NonNull View itemView) {
            super(itemView);

            mUserAccountImage = itemView.findViewById(R.id.user_account_image);
            mUserAccountName = itemView.findViewById(R.id.user_account_name);
            mPostTargetArrow = itemView.findViewById(R.id.target_arrow);
            mTargetPageName = itemView.findViewById(R.id.target_page_name);
            mPostDate = itemView.findViewById(R.id.post_creation_time);
            mPostVisibility = itemView.findViewById(R.id.post_visibility);
            mPostActions = itemView.findViewById(R.id.post_actions_btn);
            mAdditionnalViews = itemView.findViewById(R.id.additional_views);
            mPostContent = itemView.findViewById(R.id.post_content);
            mLikeButton = itemView.findViewById(R.id.like_button);
            mCommentsList = itemView.findViewById(R.id.comments_list);
            mCreateCommentForm = itemView.findViewById(R.id.create_comment_form);
            mEditCommentContentView = itemView.findViewById(R.id.input_comment_content);
            mSendCommentButton = itemView.findViewById(R.id.comment_send_button);
        }

        Post getPost(int position){
            return mList.get(position);
        }

        /**
         * @return Additional views container
         */
        FrameLayout getAdditionalViewsLayout(){
            return mAdditionnalViews;
        }

        @CallSuper
        void bind(final int position){

            Post post = getPost(position);
            UserInfo user = null;
            if(mList.getUsersInfo().containsKey(post.getUserID()))
                user = mList.getUsersInfo().get(post.getUserID());


            //Apply user information
            if(user != null){
                mUserAccountImage.setUser(user);
                mUserAccountName.setText(user.getDisplayFullName());
            }
            else {
                mUserAccountImage.removeUser();
                mUserAccountName.setText("");
            }

            //Specify post target
            setTargetNameVisibility(true);

            //Check if posts target has not to be shown
            if(!mDisplayPostsTarget)
                setTargetNameVisibility(false);

            //Do not display post target if the user who created the post created it on his page
            else if(post.getPage_type() == PageType.USER_PAGE
                    && post.getPage_id() == post.getUserID())
                setTargetNameVisibility(false);

            //For user page
            else if(post.getPage_type() == PageType.USER_PAGE
                    && mList.getUsersInfo().containsKey(post.getPage_id())){

                mTargetPageName.setText(mList.getUsersInfo().get(post.getPage_id())
                        .getDisplayFullName());

            }

            //For group page
            else if(post.getPage_type() == PageType.GROUP_PAGE
                    && mList.getGroupsInfo().containsKey(post.getPage_id())){

                mTargetPageName.setText(mList.getGroupsInfo().get(post.getPage_id())
                        .getDisplayName());

            }

            //Information about user / group not found
            else
                setTargetNameVisibility(false);


            //Post date
            mPostDate.setText(TimeUtils.TimeToString(getContext(),
                    TimeUtils.time() - post.getPost_time()));


            //Display post visibility
            int visibility_res_id;
            switch (post.getVisibilityLevel()){

                case PUBLIC:
                    visibility_res_id = R.drawable.ic_public;
                    break;

                case FRIENDS:
                    visibility_res_id = R.drawable.ic_friends;
                    break;

                case MEMBERS:
                    visibility_res_id = R.drawable.ic_friends;
                    break;

                case PRIVATE:
                default:
                    visibility_res_id = R.drawable.ic_user;
                    break;
            }
            Drawable drawable = UiUtils.getDrawable(getContext(), visibility_res_id);
            drawable.setTint(UiUtils.getColor(getContext(), R.color.darker_gray));
            mPostVisibility.setImageDrawable(drawable);

            //Set post actions
            mPostActions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.showPostActions(v, position, getPost(position));
                }
            });


            //Set post content
            mPostContent.setParsedText(UiUtils.prepareStringTextView(post.getContent()));


            //Post likes
            mLikeButton.setNumberLikes(post.getNumberLike());
            mLikeButton.setIsLiking(post.isLiking());
            mLikeButton.setUpdateListener(new LikeButtonView.OnLikeUpdateListener() {
                @Override
                public void OnLikeUpdate(boolean isLiking) {
                    mListener.onPostLikeUpdate(getPost(position), isLiking);
                }
            });


            //Post comments
            ArrayList<Comment> comments = post.getComments_list();
            mCommentsList.removeAllViews();
            if(comments != null){
                //Show list
                mCommentsList.setVisibility(View.VISIBLE);

                for(Comment comment : comments){

                    if(comment.isDeleted())
                        continue;

                    UserInfo commentUser = mList.getUsersInfo().containsKey(comment.getUserID()) ?
                            mList.getUsersInfo().get(comment.getUserID()) : null;

                    View commentView = CommentsAdapter.getInflatedView(getContext(), comment,
                            mListener, commentUser, mCommentsList);
                    mCommentsList.addView(commentView);

                }
            }
            else
                //Hide comments list
                mCommentsList.setVisibility(View.GONE);


            //Comments creation form
            if(comments == null)
                mCreateCommentForm.setVisibility(View.GONE);
            else {
                mCreateCommentForm.setVisibility(View.VISIBLE);

                if(mEditCommentContentView.getPostID() != post.getId()){

                    mEditCommentContentView.setPostID(post.getId());
                    mEditCommentContentView.setText("");

                    mSendCommentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendComment();
                        }
                    });

                }

                mEditCommentContentView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(event.getAction() == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_ENTER)
                            sendComment();

                        return false;
                    }
                });
            }
        }

        private void setTargetNameVisibility(boolean visible){
            mPostTargetArrow.setVisibility(visible ? View.VISIBLE : View.GONE);
            mTargetPageName.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        private void sendComment(){
            mListener.onCreateComment(getLayoutPosition(), mSendCommentButton,
                    getPost(getLayoutPosition()), mEditCommentContentView);
        }
    }

    /**
     * Image posts holder
     */
    private class ImagePostHolder extends TextPostHolder {

        private EnlargeableWebImageView mPostImage;

        ImagePostHolder(@NonNull View itemView) {
            super(itemView);

            mPostImage = new EnlargeableWebImageView(getContext());
            getAdditionalViewsLayout().addView(mPostImage,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            UiUtils.GetPixel(getContext(), 200)));
        }

        @Override
        void bind(int position) {
            super.bind(position);

            mPostImage.loadURL(getPost(position).getFile_path_url());
        }
    }

    /**
     * PDF posts holder
     */
    private class PDFPostHolder extends TextPostHolder {

        private PDFLinkButtonView mPDFLinkButton;

        PDFPostHolder(@NonNull View itemView) {
            super(itemView);

            mPDFLinkButton = new PDFLinkButtonView(getContext(), null, R.style.PostPDFButton);
            getAdditionalViewsLayout().addView(mPDFLinkButton);
        }

        @Override
        void bind(int position) {
            super.bind(position);

            mPDFLinkButton.setPDFUrl(getPost(position).getFile_path_url());
        }
    }

    /**
     * Web link post holder
     */
    private class WebLinkPostHolder extends TextPostHolder {

        private WebLinkView mWebLinkView;

        WebLinkPostHolder(@NonNull View itemView) {
            super(itemView);

            mWebLinkView = new WebLinkView(getContext());
            getAdditionalViewsLayout().addView(mWebLinkView,
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }

        @Override
        void bind(int position) {
            super.bind(position);

            mWebLinkView.setLink(getPost(position).getWebLink());
        }
    }

    /**
     * Movie post holder
     */
    private class MoviePostHolder extends TextPostHolder {

        MovieView mMovieView;

        MoviePostHolder(@NonNull View itemView) {
            super(itemView);

            mMovieView = new MovieView(getContext(), null, R.style.PostMovie);
            getAdditionalViewsLayout().addView(mMovieView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    UiUtils.GetPixel(getContext(), 200)
            ));
        }

        @Override
        void bind(int position) {
            super.bind(position);

            mMovieView.setMovie(getPost(position).getMovie());
        }
    }

    /**
     * Countdown post holder
     */
    private class CountdownPostHolder extends TextPostHolder {

        private CountDownView mCountDownView;

        CountdownPostHolder(@NonNull View itemView) {
            super(itemView);

            mCountDownView = new CountDownView(getContext(), null);
            getAdditionalViewsLayout().addView(mCountDownView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    UiUtils.GetPixel(getContext(), 30)));
        }

        @Override
        void bind(int position) {
            super.bind(position);

            mCountDownView.setTime_end(getPost(position).getTime_end());
        }
    }

    /**
     * Survey post holder
     */
    private class SurveyPostHolder extends TextPostHolder {

        private SurveyView mSurveyView;

        SurveyPostHolder(@NonNull View itemView) {
            super(itemView);

            mSurveyView = new SurveyView(getContext());
            mSurveyView.setOnSurveyUpdateListener(mListener);
            getAdditionalViewsLayout().addView(mSurveyView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        @Override
        void bind(int position) {
            super.bind(position);

            mSurveyView.setSurvey(getPost(position).getSurvey());
        }
    }
}
