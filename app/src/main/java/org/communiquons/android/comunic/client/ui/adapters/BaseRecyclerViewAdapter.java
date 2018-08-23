package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;


/**
 * Base RecyclerView adapter
 *
 * @author Pierre HUBERT
 */
public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;

    /**
     * Initialize adapter
     *
     * @param context Activity context
     */
    BaseRecyclerViewAdapter(Context context){
        super();

        mContext = context;
    }

    /**
     * Get the activity context
     *
     * @return The activity contexts
     */
    public Context getContext() {
        return mContext;
    }
}
