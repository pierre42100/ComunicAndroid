package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.ImageLoadHelper;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * WebImageView is a view that extends image view in order to ease rendering of web images
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/14/18.
 */

public class WebImageView extends android.support.v7.widget.AppCompatImageView {

    /**
     * Currently loaded image image
     */
    private String mCurrURL;

    public WebImageView(Context context) {
        super(context);
    }

    public WebImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Load an image specified by its URL in the image
     *
     * @param url The URL pointing on the image
     */
    public void loadURL(String url){

        //Check if the same URL is already being loaded
        if(url.equals(mCurrURL)){
            //Do nothing
            return;
        }

        //Reset image loader
        ImageLoadHelper.remove(this);
        setImageDrawable(UiUtils.getDrawable(getContext(), R.drawable.img_placeholder));
        ImageLoadHelper.load(getContext(), url, this);

        //Save image URL
        mCurrURL = url;
    }

    /**
     * Remove any image that was being loaded for this view
     */
    public void removeImage() {
        mCurrURL = null;
        ImageLoadHelper.remove(this);
    }
}
