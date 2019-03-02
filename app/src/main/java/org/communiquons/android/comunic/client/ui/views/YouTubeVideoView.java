package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import org.communiquons.android.comunic.client.R;

import static org.communiquons.android.comunic.client.ui.Constants.YOUTUBE_VIDEOS_URL_PREFIX;

/**
 * YouTube video view
 *
 * @author Pierre HUBERT
 */
public class YouTubeVideoView extends BaseFrameLayoutView implements View.OnClickListener {

    /**
     * YouTube video ID
     */
    private String mVideoID;

    public YouTubeVideoView(@NonNull Context context) {
        this(context, null);
    }

    public YouTubeVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YouTubeVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public YouTubeVideoView(@NonNull Context context, @Nullable AttributeSet attrs,
                            int defStyleAttr, int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_youtube_video, this, false);
        addView(v);

        v.setOnClickListener(this);
    }

    public void setVideoID(String youTubeID) {
        this.mVideoID = youTubeID;
    }

    public String getVideoID() {
        return mVideoID;
    }

    @Override
    public void onClick(View v) {

        //Open the video
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_VIDEOS_URL_PREFIX + getVideoID()));
        getContext().startActivity(intent);

    }
}
