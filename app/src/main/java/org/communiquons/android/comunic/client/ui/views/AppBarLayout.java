package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * Generic AppBarLayout base class for the fragments of this application
 */
public class AppBarLayout extends BaseFrameLayoutView {

    private android.support.design.widget.AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    public AppBarLayout(@NonNull Context context) {
        this(context, null);
    }

    public AppBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //Set the view
        View view = inflate(getContext(), R.layout.view_appbarlayout, this);
        mAppBarLayout = view.findViewById(R.id.app_bar_layout);
        mToolbar = view.findViewById(R.id.toolbar);
    }

    /**
     * @return Toolbar view
     */
    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * Update the title of the appbar
     *
     * @param title The new title
     */
    public void setTitle(String title){
        mToolbar.setTitle(title);
    }

    /**
     * Add back button to the view
     *
     * @param listener The listener to use when the button is clicked
     */
    public void addBackButton(View.OnClickListener listener){
        Drawable backDrawable = UiUtils.getDrawable(getActivity(), R.drawable.ic_back);
        backDrawable.setColorFilter(UiUtils.getColor(getActivity(), android.R.color.white),
                PorterDuff.Mode.SRC_IN);
        mToolbar.setNavigationIcon(backDrawable);
        mToolbar.setNavigationOnClickListener(listener);
    }

    /**
     * Add a button to the toolbar
     *
     * @param icon The icon associated to the button
     * @param listener OnClickListener for the button
     */
    public void addButton(int icon, @Nullable OnClickListener listener) {

        Drawable drawable = UiUtils.getDrawable(getContext(), icon);
        drawable.setColorFilter(UiUtils.getColor(getContext(), android.R.color.white),
                PorterDuff.Mode.SRC_IN);

        AppCompatImageButton btn = new AppCompatImageButton(getActivity(),
                null, android.support.v7.appcompat.R.attr.toolbarStyle);
        btn.setPadding(5, 0, 5, 0);
        btn.setImageDrawable(drawable);
        btn.setOnClickListener(listener);
        getToolbar().addView(btn);
    }
}
