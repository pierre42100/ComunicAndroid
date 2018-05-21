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
     * Object to notify when the image has been posted
     */
    private Object obj = null;

    /**
     * Construct the class
     *
     * @param imageView The target image view
     * @param bitmap The bitmap to apply
     * @param obj Object to notify once the image has been applied
     */
    ImageLoadApplyRunnable(ImageView imageView, Bitmap bitmap, Object obj){

        //Save the values
        this.bitmap = bitmap;
        this.imageView = imageView;
        this.obj = obj;
    }

    /**
     * This operation should be run only on the UI Thread
     */
    @Override
    @UiThread
    public void run() {

        //Apply the image
        imageView.setImageBitmap(bitmap);

        synchronized (obj){
            //Notify
            obj.notifyAll();
        }

    }
}
