package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

    /**
     * Get a drawable from ressources
     *
     * @param context The context of the application
     * @param drawable_id The ID of the drawable to get
     */
    public static Drawable getDrawable(Context context, int drawable_id){
        return context.getResources().getDrawable(drawable_id, context.getTheme());
    }
}
