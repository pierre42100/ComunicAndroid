package org.communiquons.android.comunic.client.data.runnables;

import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.widget.ImageView;

/**
 * This runnable apply a bitmap on an image view.
 *
 * This runnable is intended to be run on the UI thread
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/18/17.
 */
class ImageLoadApplyRunnable implements Runnable {

    /**
     * The bitmap to apply
     */
    private Bitmap bitmap;

    /**
     * The target image view
     */
    private ImageView imageView;

    /**
     * Construct the class
     *
     * @param imageView The target image view
     * @param bitmap The bitmap to apply
     */
    ImageLoadApplyRunnable(ImageView imageView, Bitmap bitmap){

        //Save the values
        this.bitmap = bitmap;
        this.imageView = imageView;
    }

    /**
     * This operation should be run only on the UI Thread
     */
    @Override
    @UiThread
    public void run() {

        //Apply the image
        imageView.setImageBitmap(bitmap);
    }
}
