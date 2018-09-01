package org.communiquons.android.comunic.client.data.models;

import android.graphics.Bitmap;

/**
 * New conversation message information
 *
 * @author Pierre HUBERT
 */
public class NewConversationMessage {

    //Private fields
    private int conversationID;
    private String message;
    private Bitmap image;

    public int getConversationID() {
        return conversationID;
    }

    public void setConversationID(int conversationID) {
        this.conversationID = conversationID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean hasImage(){
        return image != null;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
