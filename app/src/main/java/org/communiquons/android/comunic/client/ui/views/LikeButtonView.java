package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.utils.UiUtils;

/**
 * Like button view
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/1/18.
 */

public class LikeButtonView extends FrameLayout implements View.OnClickListener {

    /**
     * Like container
     */
    private LinearLayout mContainer;

    /**
     * Like image
     */
    private ImageView mLikeImage;

    /**
     * Like text
     */
    private TextView mLikeText;

    /**
     * Current user liking status
     */
    private boolean mIsLiking = false;

    /**
     * Number of likes
     */
    private int numberLikes = 0;

    /**
     * Like Update listener
     */
    private OnLikeUpdateListener mUpdateListener = null;

    public LikeButtonView(@NonNull Context context) {
        super(context);
        initView();
    }

    public LikeButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LikeButtonView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    /**
     * Initialize view
     */
    private void initView(){

        //Inflate the view
        View view = inflate(getContext(), R.layout.view_like_button, null);

        //Append the view
        addView(view);

        //Get the views
        mContainer = view.findViewById(R.id.like_container);
        mLikeImage = view.findViewById(R.id.like_img);
        mLikeText = view.findViewById(R.id.like_text);

        //Set the click listener
        mContainer.setOnClickListener(this);

        //Refresh the view
        refresh();
    }

    /**
     * Get the number of likes
     *
     * @return The number of likes
     */
    public int getNumberLikes() {
        return numberLikes;
    }

    /**
     * Set the number of likes
     *
     * @param numberLikes The number of likes
     */
    public void setNumberLikes(int numberLikes) {
        this.numberLikes = numberLikes;
        refresh();
    }

    /**
     * Set the user liking state
     *
     * @param mIsLiking User liking state
     */
    public void setIsLiking(boolean mIsLiking) {
        this.mIsLiking = mIsLiking;
        refresh();
    }

    /**
     * Get user liking state
     *
     * @return TRUE if the user is liking / FALSE else
     */
    public boolean isIsLiking() {
        return mIsLiking;
    }

    /**
     * Set like update listener
     *
     * @param updateListener The listener for the like update
     */
    public void setUpdateListener(OnLikeUpdateListener updateListener) {
        this.mUpdateListener = updateListener;
    }

    /**
     * Refresh the like view
     */
    private void refresh(){

        //Update the image
        mLikeImage.setImageDrawable(UiUtils.getDrawable(getContext(),
                mIsLiking ? R.drawable.like_down : R.drawable.like_up));

        //Update the text
        String text = UiUtils.getString(getContext(), mIsLiking ? R.string.like_view_liking :
                R.string.like_view_like);

        if(numberLikes > 0)
            text += " (" + numberLikes + ")";

        mLikeText.setText(text);

    }

    @Override
    public void onClick(View v) {

        //Check if the user want to like or dislike component
        if(mIsLiking){
            mIsLiking = false;
            numberLikes--;
        }
        else {
            mIsLiking = true;
            numberLikes++;
        }

        //Refresh display
        refresh();

        //Call listener (if any)
        if(mUpdateListener != null)
            mUpdateListener.OnLikeUpdate(mIsLiking);
    }

    /**
     * Likes update listener interface
     */
    public interface OnLikeUpdateListener {

        /**
         * This method is called when an update is done on a like
         *
         * @param isLiking New liking status
         */
        void OnLikeUpdate(boolean isLiking);

    }
}
