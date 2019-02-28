package org.communiquons.android.comunic.client.ui.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Permissions utilities
 *
 * @author Pierre HUBERT
 */
public class PermissionsUtils {

    /**
     * Request some permissions from the user
     *
     * @param activity Activity that perform the request
     * @param permissions Requested permissions
     */
    public static void RequestPermissions(@NonNull Activity activity, String[] permissions, int c){

        ArrayList<String> permissionsToRequest = new ArrayList<>();

        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(permission);
        }


        //Ask for permissions if required
        if(permissionsToRequest.size() > 0){
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            assert permissionsArray != null;
            ActivityCompat.requestPermissions(activity, permissionsArray, c);
        }

    }

}
