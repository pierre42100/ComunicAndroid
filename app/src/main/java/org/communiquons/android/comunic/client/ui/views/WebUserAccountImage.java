package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * WebAccountImage - This view is used to display user account image
 *
 * Created by pierre on 4/14/18.
 * @author Pierre HUBERT
 */

public class WebUserAccountImage extends WebImageView {
    public WebUserAccountImage(Context context) {
        super(context);
        init();
    }

    public WebUserAccountImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebUserAccountImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize the view
     *
     * This method should be called by all the constructor of the view
     */
    private void init(){

        //Update the default drawable
        setDefaultDrawable(R.drawable.default_account_image);

    }

    /**
     * Set the user for the view
     *
     * @param user Information about the user
     */
    public void setUser(@NonNull UserInfo user){
        loadURL(user.getAcountImageURL());
    }

    /**
     * Remove currently loaded user and display the default account image
     */
    public void removeUser(){
        removeImage();
        setImageDrawable(UiUtils.getDrawable(getContext(), getDefaultDrawable()));
    }
}
