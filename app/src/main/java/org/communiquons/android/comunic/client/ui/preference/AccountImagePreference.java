package org.communiquons.android.comunic.client.ui.preference;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

/**
 * Account image preference
 */
public class AccountImagePreference extends Preference {

    private String image_url;
    private WebUserAccountImage mWebUserAccountImage;

    public AccountImagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public AccountImagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public AccountImagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public AccountImagePreference(Context context) {
        super(context);
        initialize();
    }

    private void initialize(){
        setLayoutResource(R.layout.preference_account_image);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mWebUserAccountImage = (WebUserAccountImage) holder.findViewById(R.id.account_image);
        refresh();
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
        refresh();
    }

    public void refresh(){
        if(mWebUserAccountImage != null && image_url != null)
            mWebUserAccountImage.loadURL(image_url);
    }
}
