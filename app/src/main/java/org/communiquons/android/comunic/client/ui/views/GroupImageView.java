package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.GroupInfo;

/**
 * Special view for group images
 *
 * @author Pierre HUBERT
 */
public class GroupImageView extends WebImageView {

    public GroupImageView(Context context) {
        this(context, null);
    }

    public GroupImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDefaultDrawable(R.drawable.ic_friends);
    }

    /**
     * Set a new group to this view
     *
     * @param group The new group to set
     */
    public void setGroup(GroupInfo group){
        loadURL(group.getIcon_url());
    }

    /**
     * Remove any group currently set in this view
     */
    public void removeGroup(){
        removeImage();
        applyDefaultDrawable();
    }
}
