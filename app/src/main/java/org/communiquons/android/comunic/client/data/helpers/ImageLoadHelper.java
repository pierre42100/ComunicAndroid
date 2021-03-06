package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;

import org.communiquons.android.comunic.client.data.runnables.ImageLoadRunnable;

import java.util.Objects;

/**
 * Image load manager / helper
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/18/17.
 */

public class ImageLoadHelper {

    /**
     * The list of running operations
     */
    private static ArrayMap<View, Thread> threads = null;

    /**
     * Private constructor
     */
    private static void construct(){

        //Initializate threads list
        threads = new ArrayMap<>();

    }

    /**
     * Check wether the class has been already initialized or not
     *
     * @return True if the class is already initialized
     */
    private static boolean is_constructed(){
        return threads != null;
    }

    /**
     * Create a new operation
     *
     * @param context The context of the application
     * @param url The URL of the image to load
     * @param imageView The target view for the image
     */
    public static void load(Context context, String url, ImageView imageView){

        //Initialize class if required
        if(!is_constructed())
            construct();

        //Remove any previously existing operation
        remove(imageView);

        //Create the new thread
        Thread thread = new Thread(new ImageLoadRunnable(context, imageView, url));
        threads.put(imageView, thread);

        //Run it
        thread.start();
    }

    /**
     * Remove (and kill) a thread associated to a view
     *
     * @param view The target view
     */
    public static void remove(ImageView view){

        //Initialize class if required
        if(!is_constructed())
            construct();

        //Check if the view has an associated thread
        if(threads.containsKey(view)){

            Thread thread = threads.get(view);

            //Kill the thread if it is alive
            if(thread != null){
                if(thread.isAlive())
                    thread.interrupt();
            }

            //Remove the thread from the list
            threads.remove(view);

        }

        //Clean the list
        clean();
    }

    /**
     * Check out whether an image view is loading or not
     *
     * @param v The target view
     * @return TRUE if loading / FALSE else
     */
    public static boolean IsLoading(View v){

        return threads.containsKey(v)
                && threads.get(v) != null
                && Objects.requireNonNull(threads.get(v)).isAlive()
                && !Objects.requireNonNull(threads.get(v)).isInterrupted();
    }

    /**
     * Clean the list of pending operations
     */
    private static void clean(){

        //Get the list of threads
        int i = 0;
        for(View view : threads.keySet()){

            if(threads.get(view) != null)
                //Check if the associated thread with the view is still alive or not
                if(!threads.get(view).isAlive())
                    threads.remove(view);

            //Avoid ArrayIndexOutOfBoundsException
            i++;
            if(threads.size() < i)
                return;
        }

    }
}
