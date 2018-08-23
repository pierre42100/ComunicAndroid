package org.communiquons.android.comunic.client.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import org.communiquons.android.comunic.client.R;

/**
 * User Interface utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/31/17.
 */

public class UiUtils {

    /**
     * Get a color from resources
     *
     * @param context The context of the application
     * @param color_id The ID of the resource
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
     * Get a drawable from resources
     *
     * @param context The context of the application
     * @param drawable_id The ID of the drawable to get
     */
    public static Drawable getDrawable(Context context, int drawable_id){
        return context.getResources().getDrawable(drawable_id, context.getTheme());
    }

    /**
     * Get a string from resources
     *
     * @param context The context of the application
     * @param res_id The ID of the resource
     * @return The string
     */
    public static String getString(Context context, int res_id){
        return context.getResources().getString(res_id);
    }

    /**
     * Get a string from resources with parameters
     *
     * @param context The context of the application
     * @param res_id Resource ID
     * @param formatsArgs Additional arguments
     * @return The string
     */
    public static String getString(Context context, int res_id, Object... formatsArgs){
        return context.getResources().getString(res_id, formatsArgs);
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

    /**
     * Get the amount of pixel matching to a dp
     *
     * @param context The context of the activity
     * @param dp The number of dp to convert
     * @return Matching number of pixel
     */
    public static int GetPixel(Context context, int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
