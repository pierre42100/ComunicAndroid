package org.communiquons.android.comunic.client.data.runnables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ImageView;

import org.communiquons.android.comunic.client.data.utils.ImageLoadUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Image loading runnable
 *
 * The advantages of this manager are the following for image loading : it avoid a single file to be
 * downloaded several times and it several images to be downloaded at once
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/18/17.
 */

public class ImageLoadRunnable implements Runnable {

    /**
     * Debug tag
     */
    private static final String TAG = ImageLoadRunnable.class.getSimpleName();

    /**
     * Image load lock
     */
    private static ReentrantLock ImageLoadLock = null;

    /**
     * An array map with all the pending images associated with their URLs
     */
    private static ArrayMap<String, Thread> pendingOperation = null;

    /**
     * The context of the operation
     */
    private Context mContext;

    /**
     * Target view for the image
     */
    private ImageView imageView;

    /**
     * The URL of the image
     */
    private String url;

    /**
     * The file object of the image
     */
    private File file;

    /**
     * Constructor of the runnable
     *
     * @param context The context of the application
     * @param imageView The imageView of the image
     * @param url The URL of the image
     */
    public ImageLoadRunnable(Context context, ImageView imageView, String url){

        if(ImageLoadLock == null)
            ImageLoadLock = new ReentrantLock();


        //Check if the list of pending operations has to be initialized or not
        if(pendingOperation == null)
            pendingOperation =  new ArrayMap<>();

        //Save the values
        this.imageView = imageView;
        this.url = url;
        this.mContext = context;
    }

    @Override
    public void run() {

        //Create the parent directory if required
        ImageLoadUtils.create_parent_directory(mContext);

        file = ImageLoadUtils.getFileForImage(mContext, url);

        //Check no thread is already running for the following image
        if (!pendingOperation.containsKey(url)) {

            //Check if a file exist or not
            if (file.exists()) {

                //Then the file can be loaded in a bitmap
                safe_load_image();
                return;
            } else {

                //Create the thread and start it
                Thread thread = new Thread(new ImageDownloadRunnable(url, file));
                pendingOperation.put(url, thread);
                thread.start();
            }
        }

        //Get the thread
        Thread operation = pendingOperation.get(url);

        //If we couldn't get the thread, this is an error
        if (operation == null) {
            Log.e("ImageLoadManagerRunnabl", "run : Couldn't get thread !");
            return;
        }

        if (operation.isAlive()) {
            try {
                //Wait for the thread to finish
                operation.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        //Remove the thread from the pending list
        pendingOperation.remove(url);

        safe_load_image();
    }

    /**
     * Load images safely
     */
    private void safe_load_image(){

        Log.v(TAG, "Before lock");
        ImageLoadLock.lock();

        //Load the image
        load_image();

        ImageLoadLock.unlock();
    }

    /**
     * Once the image was downloaded (if it wasn't already) load the image into a bitmap object
     * The apply to the final image view
     */
    private synchronized void load_image(){

        //Check if the file exists
        if(!file.exists()){
            Log.e("ImageLoadManagerRunnabl", "load_image : file does not exists but it should !");
            return;
        }

        //Log action
        Log.v(TAG, "Load image downloaded from " + url);

        try {
            //Load the image
            FileInputStream is = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            //Check if bitmap failed to read
            if(bitmap == null){
                //Return error
                Log.e("ImageLoadManagerRunnabl", "Image file could not be read, therefore it was" +
                        " deleted");

                //Delete file
                if(!file.delete())
                    Log.e(TAG, "Could not delete file " + file.getPath() + " !");

                return;
            }

            //Apply the bitmap on the image view (register operation)
            Object obj = new Object();
            synchronized (obj){
                imageView.post(new ImageLoadApplyRunnable(imageView, bitmap, obj));
                Log.v(TAG, "Locking for " + url);
                obj.wait(1000); //Wait for the image to be applied
                Log.v(TAG, "Unlocking   " + url);
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
