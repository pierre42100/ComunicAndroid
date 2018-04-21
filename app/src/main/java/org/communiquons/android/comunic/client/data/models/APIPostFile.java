package org.communiquons.android.comunic.client.data.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;

/**
 * Single file information included in a request to the API
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/21/18.
 */

public class APIPostFile {

    //Private fields
    private String fieldName;
    private String fileName;
    private byte[] byteArray;


    //Set and get field name
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }


    //Set and get file name
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    //Set and get byte array
    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    /**
     * Set a bitmap as the file
     *
     * @param bmp Bitmap to set
     */
    public void setBitmap(@NonNull Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        setByteArray(stream.toByteArray());
    }
}
