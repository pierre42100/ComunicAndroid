package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Nested ListView class
 *
 * @from https://stackoverflow.com/a/17503823
 * @author Muhammad Aamir Ali
 * @author Pierre HUBERT
 */

public class NestedListView extends ListView /*implements View.OnTouchListener, AbsListView.OnScrollListener*/ {

    /*/**
     * Debug tag
     *//*
    private static final String TAG = "NestedListView";

    private int listViewTouchAction;
    private static final int MAXIMUM_LIST_ITEMS_VIEWABLE = 99;


    private int lastItemCounted = 0;
    private int itemsHeight = 2;*/


    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*listViewTouchAction = -1;
        setOnScrollListener(this);
        setOnTouchListener(this);*/
    }

    /*@Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        /*if (getAdapter() != null && getAdapter().getCount() > MAXIMUM_LIST_ITEMS_VIEWABLE) {
            if (listViewTouchAction == MotionEvent.ACTION_MOVE) {
                scrollBy(0, -1);
            }
        }*//*
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(getAdapter() == null)
            return;

        /*View view = null;

        for(int i = lastItemCounted; i < getLastVisiblePosition() || i < 2; i++){
            if(getAdapter().getCount() < i+1)
                break;

            view = getAdapter().getView(i, view, this);

            view.measure(widthMeasureSpec, heightMeasureSpec);
            itemsHeight += view.getMeasuredHeight();

            lastItemCounted++;
        }

        //Add average height for the remaining items
        itemsHeight += (lastItemCounted - getAdapter().getCount() + 1)*(itemsHeight/(lastItemCounted+1));

        setMeasuredDimension(getMeasuredWidth(), itemsHeight);*//*



        //setMeasuredDimension(getMeasuredWidth(), 700);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        /*if (getAdapter() != null && getAdapter().getCount() > MAXIMUM_LIST_ITEMS_VIEWABLE) {
            if (listViewTouchAction == MotionEvent.ACTION_MOVE) {
                scrollBy(0, 1);
            }
        }
        //return false;*/
       /* Log.v(TAG, "scroll:" );
        v.getParent().requestDisallowInterceptTouchEvent(false);
        return false;*/
   /* }*/
}