package org.communiquons.android.comunic.client.data.runnables;

import android.graphics.Bitmap;
import android.util.Log;

import org.communiquons.android.comunic.client.data.utils.StreamsUtils;
import org.communiquons.android.comunic.client.ui.utils.BitmapUtils;

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

    /**
     * Debug tag
     */
    private static final String TAG = "ImageDownloadRunnable";

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

            //Get image size
            long img_size = Long.decode(conn.getHeaderField("Content-Length"));

            //Get input stream
            InputStream is = conn.getInputStream();

            //Log action
            Log.v(TAG, "Downloading image (size: "+img_size+") at " + url);

            //Big images have to written byte per byte
            StreamsUtils.InputToOutputStream(is, os);

            //Close streams and disconnect
            is.close();
            os.close();
            conn.disconnect();

            //Reduce image size
            Bitmap bitmap = BitmapUtils.openResized(dest, 500, 500);

            //Write the new bitmap to the file
            os = new FileOutputStream(dest, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();

            //Free memory
            bitmap.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
