package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.Movie;

/**
 * Movie view
 *
 * This view is used to display a movie
 *
 * @author Pierre HUBERT
 */
public class MovieView extends FrameLayout {

    /**
     * Current movie in view
     */
    private Movie mMovie;

    /**
     * VideoView that contains the video
     */
    private VideoView mVideoView;

    /**
     * Related media controller
     */
    private MediaController mMediaController;

    public MovieView(@NonNull Context context) {
        super(context);
        init();
    }

    public MovieView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovieView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MovieView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init(){

        View view = inflate(getContext(), R.layout.view_movie, null);
        addView(view);

        mVideoView = view.findViewById(R.id.video);
        mMediaController = new MediaController(getContext());
        mMediaController.setAnchorView(mVideoView);
        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mMediaController);
    }

    /**
     * Set (apply) a new movie in the view
     *
     * @param movie The movie to apply
     */
    public void setMovie(@NonNull Movie movie) {
        this.mMovie = movie;

        mVideoView.setVideoURI(Uri.parse(mMovie.getUrl()));
    }
}
