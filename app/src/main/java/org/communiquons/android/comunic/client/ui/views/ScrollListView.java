package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;

import org.communiquons.android.comunic.client.ui.listeners.OnScrollChangeDetectListener;

/**
 * Custom ListView with extended functions
 *
 * This class use some concepts from maXp StackOverFlow answer
 * on question "Detect Scroll Up & Scroll down in ListView"
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/28/18.
 */

public class ScrollListView extends android.widget.ListView {

    /**
     * Debug tag
     */
    private static final String TAG = "ScrollListView";

    /**
     * Optional additional scroll listener
     */
    private OnScrollListener onScrollListener;

    /**
     * Scroll change detection listener
     */
    private OnScrollChangeDetectListener onScrollChangeDetectListener;

    public ScrollListView(Context context) {
        super(context);
        init();
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Initialize this view
     */
    private void init(){

        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                //Call additional listener (if any)
                if(onScrollListener != null)
                    onScrollListener.onScrollStateChanged(view, scrollState);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                //Call additional listener (if any)
                if(onScrollListener != null)
                    onScrollListener.onScroll(view, firstVisibleItem,
                            visibleItemCount, totalItemCount);


                if(onScrollChangeDetectListener != null && firstVisibleItem == 0
                        && visibleItemCount > 0)
                    onScrollChangeDetectListener.onReachTop();
            }
        });

    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setOnScrollChangeDetectListener(OnScrollChangeDetectListener onScrollChangeDetectListener) {
        this.onScrollChangeDetectListener = onScrollChangeDetectListener;
    }

    public OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }
}
