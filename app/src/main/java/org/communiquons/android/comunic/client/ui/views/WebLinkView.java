package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.WebLink;

/**
 * WebLink view
 *
 * Display a web link
 *
 * @author Pierre HUBERT
 */
public class WebLinkView extends FrameLayout implements View.OnClickListener {

    /**
     * Current link associated with the view
     */
    private WebLink mLink;

    /**
     * Container views
     */
    private WebImageView mLinkIcon;
    private TextView mLinkTitle;
    private TextView mLinkURL;
    private TextView mLinkDescription;

    public WebLinkView(@NonNull Context context) {
        super(context);
        init();
    }

    public WebLinkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebLinkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        //Initialize view
        View view = inflate(getContext(), R.layout.view_web_link, null);
        addView(view);

        mLinkIcon = findViewById(R.id.link_image);
        mLinkTitle = findViewById(R.id.link_title);
        mLinkURL = findViewById(R.id.link_url);
        mLinkDescription = findViewById(R.id.link_description);

        mLinkIcon.setDefaultDrawable(R.drawable.world);
        mLinkIcon.applyDefaultDrawable();

        setOnClickListener(this);
    }

    public void setLink(@NonNull WebLink link) {
        this.mLink = link;

        if(link.hasImageURL()) mLinkIcon.loadURL(link.getImageURL());
        else {
            mLinkIcon.removeImage();
            mLinkIcon.applyDefaultDrawable();
        }

        mLinkTitle.setText(link.hasTitle() ? link.getTitle() : "");
        mLinkURL.setText(link.getUrl());
        mLinkDescription.setText(link.hasDescription() ? link.getDescription() : "");
    }

    @Override
    public void onClick(View view) {
        if(mLink == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLink.getUrl()));
        getContext().startActivity(intent);
    }
}
