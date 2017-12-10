package org.communiquons.android.comunic.client.data.ImageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

/**
 * Image loading runnable
 *
 * The advantages of this manager are the following for image loading : it avoid a single file to be
 * downloaded several times and it several images to be downloaded at once
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/18/17.
 */

class ImageLoadRunnable implements Runnable {

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
    ImageLoadRunnable(Context context, ImageView imageView, String url){

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

        //Determine the filename for the requested URL
        String filename = ImageLoadUtils.IMAGE_CACHE_DIRECTORY + ImageLoadUtils.get_file_name(url);

        //Create file object
        file = new File(mContext.getCacheDir(),filename);

        //Check no thread is already running for the following image
        if(!pendingOperation.containsKey(url)){

            //Check if a file exist or not
            if(file.exists()){

                //Then the file can be loaded in a bitmap
                load_image();
                return;
            }
            else {

                //Create the thread and start it
                Thread thread = new Thread(new ImageDownloadRunnable(url, file));
                pendingOperation.put(url, thread);
                thread.start();
            }
        }

        //Get the thread
        Thread operation = pendingOperation.get(url);

        //If we couldn't get the thread, this is an error
        if(operation == null){
            Log.e("ImageLoadManagerRunnabl", "run : Couldn't get thread !");
            return;
        }

        if(operation.isAlive()) {
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

        //Load the image
        load_image();
    }

    /**
     * Once the image was downloaded (if it wasn't already) load the image into a bitmap object
     * The apply to the final image view
     */
    private void load_image(){

        //Check if the file exists
        if(!file.exists()){
            Log.e("ImageLoadManagerRunnabl", "load_image : file does not exists but it should !");
            return;
        }

        try {
            //Load the image
            FileInputStream is = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            //Check if bitmap failed to read
            if(bitmap == null){
                //Return error
                Log.e("ImageLoadManagerRunnabl", "Image file could not be read, therefore it was" +
                        "deleted");

                //Delete file
                file.delete();
                return;
            }

            //Apply the bitmap on the image view (register operation)
            imageView.post(new ImageLoadApplyRunnable(imageView, bitmap));

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
