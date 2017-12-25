package org.communiquons.android.comunic.client.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import org.communiquons.android.comunic.client.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;

/**
 * Application utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 10/29/17.
 */

public class Utilities {

    /**
     * Application context
     */
    private Context mContext;

    /**
     * Account class constructor
     *
     * @param context Context of the application
     */
    public Utilities(Context context){
        mContext = context;
    }

    /**
     * Get the content of a file
     *
     * @param filename the name of the file to get
     * @return The content of the file
     */
    public String file_get_content(String filename){

        FileInputStream fileInputStream;

        try {
            fileInputStream = mContext.openFileInput(filename);

            String result = "";
            int currByte = 0;
            while(currByte != -1){

                currByte = fileInputStream.read();

                if(currByte != -1)
                    result += (char) currByte;
            }

            fileInputStream.close();

            return result;
        }
        catch (Exception e){

            //Print error stack
            Log.e("Utilities", "Couldn't get file content !");
            e.printStackTrace();

            return ""; //This is a failure
        }


    }

    /**
     * Write something to a file
     *
     * @param filename The name of the file to write
     * @param content The new content of the file
     * @return FALSE in case of failure
     */
    public boolean file_put_contents(String filename, String content){

        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (Exception e){
            e.printStackTrace();

            return false;
        }

        //Success
        return true;
    }

    /**
     * Check whether a specified email address is valid or not
     *
     * @param mail The E-Mail address to check
     * @return True if the mail is valid / false else
     */
    public boolean isValidMail(CharSequence mail){
        return !TextUtils.isEmpty(mail) && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e){
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
     * Transfer all the data coming from an InputStream to an Output Stream
     *
     * @param is The Input stream
     * @param os The output stream
     * @return The number of byte transfered
     */
    public static int InputToOutputStream(InputStream is, OutputStream os){

        int count = 0;

        try {
            int b = is.read();
            while (b != -1){
                os.write(b);
                count++;
                b = is.read();
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Read an InputStream into a string
     *
     * @param is The input stream
     * @return The string
     */
    public static String isToString(InputStream is){

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            bufferedReader.close();

            //Return the result
            return stringBuilder.toString();

        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a Bitmap into a base64-encoded string
     *
     * @param bitmap The bitmap to convert
     * @return Encoded string
     */
    public static String bitmapToBase64(@NonNull Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    /**
     * Prepare a string sent through the API to be shown in a TextView element
     *
     * @param input The string to prepare
     * @return The string ready to be shown
     */
    public static String prepareStringTextView(String input){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(input, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH).toString();
        }
        else
            return Html.fromHtml(input).toString();
    }

    /**
     * Get current timestamp
     *
     * @return The current timestamp
     */
    public static int time(){
        Date date = new Date();
        return (int) Math.ceil(date.getTime()/1000);
    }

    /**
     * Transform an amount of seconds into a string like "3min" or "10hours"s
     *
     * @param time The Time to convert
     * @return Generated string
     */
    public String timeToString(int time){

        Resources res = mContext.getResources();

        //Check if the time is inferior to 1 => now
        if(time < 1)
            return res.getString(R.string.date_now);

        //Less than one minute
        else if (time < 60){
            return time + res.getString(R.string.date_s);
        }

        //Less than one hour
        else if (time < 3600){
            int secs = (int) Math.floor(time / 60);
            return secs + res.getString(R.string.date_m);
        }

        //Less than a day
        else if (time < 86400){
            int hours = (int) Math.floor(time / 3600);
            return hours + res.getString(R.string.date_h);
        }

        //Less than a month
        else if (time < 2678400){
            int days = (int) Math.floor(time / 86400);
            return days + res.getString(days > 1 ? R.string.date_days : R.string.date_day);
        }

        //Less than a year
        else if (time < 31536000){
            int months = (int) Math.floor(time / 2678400);
            return months + res.getString(months > 1 ? R.string.date_months : R.string.date_month);
        }

        //A several amount of years
        else {
            int years = (int) Math.floor(31536000);
            return years + res.getString(years > 1 ? R.string.date_years : R.string.date_year);
        }
    }

}
