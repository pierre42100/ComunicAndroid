package org.communiquons.android.comunic.client.data.ImageLoad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Download an image and save it into a file
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/18/17.
 */

class ImageDownloadRunnable implements Runnable {

    private String url;
    private File dest;

    /**
     * Instantiate the class for a specific image
     *
     * @param url The URL were the image can be retrieved
     * @param dest The destination file to the image
     */
    ImageDownloadRunnable(String url, File dest){
        this.url = url;
        this.dest = dest;
    }


    /**
     * Perform the download
     */
    @Override
    public void run() {
        try {
            //Open the file for writing
            if(!dest.createNewFile())
                return;
            OutputStream os = new FileOutputStream(dest, false);

            //Open the connection
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

            conn.setDoInput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            conn.connect();

            //Get input stream
            InputStream is = conn.getInputStream();

            //Process image
            Bitmap image = BitmapFactory.decodeStream(is);
            image.compress(Bitmap.CompressFormat.PNG, 100, os);

            os.close();
            is.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
