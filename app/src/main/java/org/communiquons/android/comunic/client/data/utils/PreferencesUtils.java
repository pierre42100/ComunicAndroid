package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Preference utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/9/18.
 */

public class PreferencesUtils {

    /**
     * Get a boolean preference
     *
     * @param context The context of the application
     * @param key The name of the key to get
     * @param def The default value in case the value was not found
     * @return The preference value (if found) or the default value
     */
    public static boolean getBoolean(Context context, String key, boolean def){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext());
        return sharedPrefs.getBoolean(key, def);
    }

    /**
     * Set (save) a new boolean preference
     *
     * @param context Application context
     * @param key The name of the key to change
     * @param value New value for the key
     */
    public static void setBoolean(Context context, String key, boolean value){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(key, value).apply();
    }
}
