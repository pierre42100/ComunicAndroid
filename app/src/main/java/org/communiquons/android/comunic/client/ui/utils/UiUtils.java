package org.communiquons.android.comunic.client.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import org.communiquons.android.comunic.client.R;

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

    /**
     * Get a string from ressources
     */
    public static String getString(Context context, int res_id){
        return context.getResources().getString(res_id);
    }

    /**
     * Create and display a loading dialog
     *
     * Use dismiss() to close this dialog
     *
     * @param context The context of the application
     * @return The created alert dialog
     */
    public static AlertDialog create_loading_dialog(Context context){

        //Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_loading);

        //Display the dialog
        return builder.show();
    }

}
