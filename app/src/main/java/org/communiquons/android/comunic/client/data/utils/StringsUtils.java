package org.communiquons.android.comunic.client.data.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

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

    /**
     * Generate a random string
     *
     * @param length The length of the string to generate
     * @return Generated string
     */
    public static String random(int length){

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < length; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }

        return randomStringBuilder.toString();

    }

    /**
     * Format timestamp to string
     *
     * @param time The time to format
     * @return Generated string
     */
    public static String FormatDate(int time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        return simpleDateFormat.format((long)1000*time);
    }

    /**
     * Convert an integer into a string, making sure that the generated string respects an minimum
     * size
     *
     * @param value The integer to convert
     * @param size The size of string
     * @return Generated string
     */
    public static String EnsureZerosInNumberString(int value, int size){
        StringBuilder stringBuilder = new StringBuilder(value + "");

        while (stringBuilder.length() < size)
            stringBuilder.insert(0, "0");

        return stringBuilder.toString();
    }
}
