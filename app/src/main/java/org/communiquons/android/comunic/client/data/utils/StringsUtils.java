package org.communiquons.android.comunic.client.data.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Formatter;
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

    /**
     * Check whether a specified email address is valid or not
     *
     * @param mail The E-Mail address to check
     * @return True if the mail is valid / false else
     */
    public static boolean isValidMail(CharSequence mail){
        return !TextUtils.isEmpty(mail) && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    /**
     * Check whether a specified string is an URL or not
     *
     * @param string The string to check
     * @return TRUE if the string is an URL / FALSE else
     */
    public static boolean isURL(CharSequence string){
        return !TextUtils.isEmpty(string) && Patterns.WEB_URL.matcher(string).matches();
    }

    /**
     * Generate the SHA-1 summary of a given string
     *
     * @param source The source string
     * @return The SHA-1 encoded string
     */
    public static String sha1(String source){

        String sha1;

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(source.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        return sha1;

    }

    /**
     * Convert an array of bytes into a string
     *
     * @param bList The list of bytes
     * @return The result string
     */
    private static String byteToHex(byte[] bList){

        Formatter formatter = new Formatter();

        for(byte b : bList){
            formatter.format("%02x", b);
        }

        String result = formatter.toString();
        formatter.close();
        return result;

    }

    /**
     * Remove all BBCode tags of a given string
     *
     * @param string The string to update
     * @return The same string, without any bbcode tags
     */
    public static String RemoveBBCode(String string){
        return string.replaceAll("\\[[a-zA-Z1-9/=#]{1,10}]", "");
    }
}
