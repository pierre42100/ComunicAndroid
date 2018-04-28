package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;

/**
 * Base helper
 *
 * This helper is intended to be inherited by all the other helpers
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/28/18.
 */

class BaseHelper {

    /**
     * The context of the application
     */
    private Context mContext;

    /**
     * Package-private constructor
     *
     * @param context The context of the application
     */
    BaseHelper(Context context){
        this.mContext = context.getApplicationContext();
    }

    /**
     * Get the context of the application
     *
     * @return The context
     */
    public Context getContext() {
        return mContext;
    }
}
