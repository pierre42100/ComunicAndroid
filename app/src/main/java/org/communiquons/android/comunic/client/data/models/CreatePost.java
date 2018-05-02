package org.communiquons.android.comunic.client.data.models;

import android.graphics.Bitmap;

/**
 * This object extends the Post object in order to include all the required informations to
 * create a new post
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/1/18.
 */

public class CreatePost extends Post {

    //Private fields
    private Bitmap newImage;


    //Get and set new image
    public void setNewImage(Bitmap newImage) {
        this.newImage = newImage;
    }

    public Bitmap getNewImage() {
        return newImage;
    }

    public boolean hasNewImage(){
        return newImage != null;
    }
}
