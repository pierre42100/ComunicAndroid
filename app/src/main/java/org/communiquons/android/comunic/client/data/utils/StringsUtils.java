package org.communiquons.android.comunic.client.data.utils;

/**
 * Strings utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/12/18.
 */

public class StringsUtils {

    /**
     * Check the validity of a string to be send to the server
     *
     * @param string The string to check
     * @return Depends of the validity of the string
     */
    public static boolean isValidForContent(String string){

        //Check the length of the string
        if(string.length() < 5)
            return false;

        //The string appears to be valid
        return true;
    }

}
