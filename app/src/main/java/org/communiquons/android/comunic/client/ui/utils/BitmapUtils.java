package org.communiquons.android.comunic.client.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import org.communiquons.android.comunic.client.data.utils.FilesUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Bitmap utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/25/17.
 */

public class BitmapUtils {

    /**
     * Debug tag
     */
    private static final String TAG = "BitmapUtils";

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

    /**
     * Open a resized format of an image to avoid OOM Fatal error
     *
     * @param file The file to open
     * @param reqWidth The required width
     * @param reqHeight The required height
     */
    public static Bitmap openResized(@NonNull File file, int reqWidth, int reqHeight){

        //Get the size of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        //Calculate the reduction ratio
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //Decode Bitmap with new parameters
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    /**
     * Calculate the in sample size
     *
     * @param options The Options of the Bitmap Factory that already contains the with and the
     *                height of the image
     * @param reqW The required width
     * @param reqH The required Height
     * @return The reduction ratio required for the image
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqW, int reqH){

        //Raw the width and the height of the image
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        //Check if the image has to be resized
        if(height > reqH || width > reqW){

            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while((halfHeight / inSampleSize) >= reqH && (halfWidth / inSampleSize) >= reqW)
                inSampleSize *= 2;

        }

        return inSampleSize;

    }

    /**
     * Turn an intent data into Bitmap object
     *
     * @param context The context of the activity
     * @param data Intent data
     * @return Generated bitmap
     * @throws FileNotFoundException In case of failure during the process
     */
    @Nullable
    public static Bitmap IntentResultToBitmap(Context context, Intent data)
            throws FileNotFoundException {

        Uri imageUri = data.getData();
        InputStream imageStream = context.getContentResolver()
                .openInputStream(imageUri);

        //Create a temporary file
        File tempFile = FilesUtils.getTempFile(context);

        if(tempFile == null){
            Log.e(TAG, "Could not create temporary file to store intent image!");
            return null;
        }

        //Intend to transfer file
        if(!FilesUtils.InputStreamToFile(imageStream, tempFile)){
            Log.e(TAG, "Could not transfer the content of the image to the file !");
            return null;
        }

        //Load bitmap
        Bitmap bitmap = BitmapUtils.openResized(tempFile, 1198, 1198);

        if(bitmap == null){
            Log.e(TAG, "Could not open temporary file!");
            return null;
        }

        //Schedule file deletion
        tempFile.deleteOnExit();

        return bitmap;
    }
}
