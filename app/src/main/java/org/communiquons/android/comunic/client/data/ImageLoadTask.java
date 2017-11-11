package org.communiquons.android.comunic.client.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Web image loader and renderer
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/8/17.
 */

public class ImageLoadTask extends AsyncTask<Void, Void, Void> {

    /**
     * The URL pointing on the image
     */
    private String url;

    /**
     * The target image view
     */
    private ImageView view;

    /**
     * The context of execution of the request
     */
    private Context mContext;

    /**
     * The main folder in the cache directory that stores the file
     */
    private final String IMAGE_CACHE_DIRECTORY = "img_cache/";

    /**
     * Image file object
     */
    private File img_file = null;

    /**
     * Bitmap object
     */
    private Bitmap bitmap = null;

    /**
     * Class constructor
     *
     * @param context The context of the request, in order to be able to access the cache directory
     * @param url The URL of the image to display
     * @param view The target image view for the image
     */
    public ImageLoadTask(Context context, String url, ImageView view){
        //Save the values
        this.mContext = context;
        this.url = url;
        this.view = view;
    }

    /**
     * Background task
     */
    @Override
    protected Void doInBackground(Void... param) {

        //Determine the file name for the view
        String filename = get_file_name(url);
        if (filename == null) {
            Log.e("ImageLoadTask", "Couldn't generate file storage name !");
            return null; //An error occured
        }
        String full_filename = IMAGE_CACHE_DIRECTORY + filename;

        //Try to open the file
        img_file = new File(mContext.getCacheDir(), full_filename);

        //Check if file exists or not
        if (!img_file.exists())
            //Download it
            download_image();

        //Check if there is still no file
        if(!img_file.exists())
            return null;

        //Try to read file
        try {
            FileInputStream is = new FileInputStream(img_file);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        //Check for errors
        if(bitmap == null){

            //Delete cached file
            img_file.delete();

            Log.e("ImageLoadTask", "File is corrupted, will have to be downloaded again !");
        }

        return null;
    }

    /**
     * On post execution operations
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        if(bitmap != null)
            view.setImageBitmap(bitmap);
    }

    /**
     * Download the file and store it in the cache
     *
     * @return True in case of success
     */
    private boolean download_image(){

        //Create cache parent directory
        if(!create_parent_directory())
            return false;

        try {

            //Open the file for writing
            if(!img_file.createNewFile())
                return false;
            OutputStream os = new FileOutputStream(img_file, false);

            //Open the connection
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

            conn.setDoInput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            conn.connect();

            //Get input stream
            InputStream is = conn.getInputStream();

            //Transfer bytes
            Utilities.InputToOutputStream(is, os);

            os.close();
            is.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Get the file name, based on the URL name
     *
     * @param url The URL of the file
     * @return The name of the file, composed of characters that can be used in filename
     */
    private String get_file_name(String url){
        return Utilities.sha1(url);
    }

    /**
     * Create cache images files parent directory if it does not exist
     *
     * @return True in case of success
     */
    private boolean create_parent_directory(){
        File parent = new File(mContext.getCacheDir(), IMAGE_CACHE_DIRECTORY);

        //Check if parent directory already exists
        if(parent.exists())
            return true;


        //Try to create directories
        boolean success = parent.mkdirs();

        //Return error if required
        if(!success)
            Log.e("ImageLoadTask", "Couldn't create cache parent directory !");

        return success;
    }
}
