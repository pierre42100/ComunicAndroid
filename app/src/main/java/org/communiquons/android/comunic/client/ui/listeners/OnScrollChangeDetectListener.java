package org.communiquons.android.comunic.client.ui.listeners;

/**
 * On Scroll change listener
 *
 * This listener works with {@link org.communiquons.android.comunic.client.ui.views.ScrollListView}
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/28/18.
 */

public interface OnScrollChangeDetectListener {

    /**
     * This method is triggered when the user reach the top (first item) of the list view
     */
    void onReachTop();

}
