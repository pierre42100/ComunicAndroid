package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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
class NavigationBarItem extends BaseFrameLayoutView implements View.OnClickListener {

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
     * Item index
     */
    private int mItemIndex;

    /**
     * Click listener
     */
    private OnNavigationBarItemClickListener mOnNavigationBarItemClickListener;

    /**
     * Selected state of the drawable
     */
    private boolean mSelected;

    public NavigationBarItem(@NonNull Context context) {
        super(context);

        //Inflate view
        View view = inflate(getContext(), R.layout.navigation_bar_item, this);
        mIcon = view.findViewById(R.id.icon);
        view.setOnClickListener(this);
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

        int fgColor = isSelected() ? R.color.navbar_fg_selected : R.color.navbar_fg_default;
        int bgColor = isSelected() ? R.color.navbar_bg_selected : R.color.navbar_bg_default;

        drawable.setColorFilter(UiUtils.getColor(getContext(), fgColor), PorterDuff.Mode.SRC_IN);
        setBackgroundColor(UiUtils.getColor(getContext(), bgColor));


        mIcon.setImageDrawable(drawable);
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
        draw();
    }

    public int getItemIndex() {
        return mItemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.mItemIndex = itemIndex;
    }

    public void setOnNavigationBarItemClickListener(OnNavigationBarItemClickListener onNavigationBarItemClickListener) {
        this.mOnNavigationBarItemClickListener = onNavigationBarItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if(mOnNavigationBarItemClickListener != null)
            mOnNavigationBarItemClickListener.onItemClick(getItemIndex());
    }

    /**
     * Interface used to handle navigation bar items click
     */
    interface OnNavigationBarItemClickListener {
        /**
         * Called on item click
         *
         * @param index The index of the clicked item
         */
        void onItemClick(int index);
    }
}
