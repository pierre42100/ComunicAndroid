package org.communiquons.android.comunic.client.data.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Bitmap utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/25/17.
 */

public class BitmapUtils {

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
     * Create a bitmap based on another Bitmap by proportionally reducing its size
     *
     * Note : If the size of the Bitmap is smaller than the maximum width and height specified,
     * The original bitmap is returned with no reduction
     *
     * @param bitmap The Bitmap to reduce
     * @param maxW The maximum width of the new bitmap
     * @param maxH The maximum height of the new bitmap
     * @return The new bitmap
     */
    public static Bitmap reduceBitmap(@NonNull Bitmap bitmap, int maxW, int maxH){

        //Get the current dimensions
        int currW = bitmap.getWidth();
        int currH = bitmap.getHeight();

        //Check if the source bitmap is small enough
        if(currW <= maxW && currH <= maxH)
            return bitmap;

        //Compute new sizes for the bitmap
        int newW = maxW;
        int newH = (int) Math.floor((currH*maxW)/currW);

        //Scale and return bitmap
        return Bitmap.createScaledBitmap(bitmap, newW, newH, true);
    }
}
