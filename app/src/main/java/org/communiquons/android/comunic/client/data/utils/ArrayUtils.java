package org.communiquons.android.comunic.client.data.utils;

import java.util.ArrayList;

/**
 * Array utilities functions
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/5/18.
 */

public class ArrayUtils {

    /**
     * Convert an ArrayList of Integer into a string
     *
     * @param input The input array
     * @param separator The separator of the values of the array
     * @return Generated string
     */
    public static String int_array_to_string(ArrayList<Integer> input, String separator){

        String result = "";

        for(int value : input){
            result += value + separator;
        }

        return result;

    }
}
