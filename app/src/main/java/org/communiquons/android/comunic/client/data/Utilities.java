package org.communiquons.android.comunic.client.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
}
