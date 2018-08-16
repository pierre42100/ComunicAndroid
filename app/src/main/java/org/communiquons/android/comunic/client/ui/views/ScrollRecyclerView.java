package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.AbsListView;

import org.communiquons.android.comunic.client.ui.listeners.OnScrollChangeDetectListener;

/**
 * A view that extends RecyclerView in order to add a listener to detect when
 * the user scrolll the view
 *
 * @author Pierre HUBERT
 */
public class ScrollRecyclerView extends RecyclerView {

    private OnScrollChangeDetectListener mOnScrollChangeDetectListener = null;

    public ScrollRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public ScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnScrollChangeDetectListener(OnScrollChangeDetectListener onScrollChangeDetectListener) {
        this.mOnScrollChangeDetectListener = onScrollChangeDetectListener;
    }

    public OnScrollChangeDetectListener getOnScrollChangeDetectListener() {
        return mOnScrollChangeDetectListener;
    }

    /**
     * Initialize the view
     */
    private void init(){
        addOnScrollListener(new PrivateScrollListener());
    }

    /**
     * Private class to handle scroll events
     */
    private class PrivateScrollListener extends OnScrollListener {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(mOnScrollChangeDetectListener == null)
                return;

            if(!canScrollVertically(1))
                mOnScrollChangeDetectListener.onReachBottom();

            else if(!canScrollVertically(-1))
                mOnScrollChangeDetectListener.onReachTop();
        }
    }
}
