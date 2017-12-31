package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.os.Build;

/**
 * User Interface utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/31/17.
 */

public class UiUtils {

    /**
     * Get a color from ressources
     *
     * @param context The context of the application
     * @param color_id The ID of the ressource
     * @return The ID of the color
     */
    public static int getColor(Context context, int color_id){
        //Check which version of getColor to use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(color_id, context.getTheme());
        }
        else {
            return context.getResources().getColor(color_id);
        }
    }
}
