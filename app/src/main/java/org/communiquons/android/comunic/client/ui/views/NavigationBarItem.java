package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.utils.DrawableUtils;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * Navigation bar item view
 *
 * @author Pierre HUBERT
 */
class NavigationBarItem extends BaseFrameLayoutView {

    /**
     * Debug tag
     */
    private static final String TAG = NavigationBarItem.class.getCanonicalName();

    /**
     * Image icon
     */
    private ImageView mIcon;

    /**
     * Source drawable
     */
    private Drawable mSrcDrawable;

    /**
     * Selected state of the drawable
     */
    private boolean mSelected;

    public NavigationBarItem(@NonNull Context context) {
        super(context);

        //Inflate view
        View view = inflate(getContext(), R.layout.navigation_bar_item, this);
        mIcon = view.findViewById(R.id.icon);

    }

    /**
     * Set item icon drawable
     *
     * @param iconDrawable The drawable to apply
     */
    public void setIconDrawable(Drawable iconDrawable){
        mSrcDrawable = iconDrawable;
        draw();
    }

    /**
     * Draw (refresh) the view
     */
    public void draw(){
        Drawable drawable = DrawableUtils.DuplicateDrawable(mSrcDrawable);

        int color = isSelected() ? R.color.navbar_selected : R.color.navbar_default;
        drawable.setColorFilter(UiUtils.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);

        mIcon.setImageDrawable(drawable);
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }
}
