package org.communiquons.android.comunic.client.data.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Streams utilities
 *
 * @author Pierre HUBERT
 */
public class StreamsUtils {

    /**
     * Transfer all the data coming from an InputStream to an Output Stream
     *
     * @param is The Input stream
     * @param os The output stream
     */
    public static void InputToOutputStream(InputStream is, OutputStream os){

        int read;
        byte[] bytes = new byte[2048];

        try {
            while ((read = is.read(bytes)) != -1){
                os.write(bytes, 0, read);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Read an InputStream into a string
     *
     * @param is The input stream
     * @return The string
     */
    public static String isToString(InputStream is){

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            bufferedReader.close();

            //Return the result
            return stringBuilder.toString();

        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
