package org.communiquons.android.comunic.client.data.models;

import org.communiquons.android.comunic.client.data.enums.AccountImageVisibility;

/**
 * Account image settings container
 *
 * @author Pierre HUBERT
 */
public class AccountImageSettings {

    //Private fields
    private boolean has_image;
    private String imageURL;
    private AccountImageVisibility visibility;

    public boolean isHas_image() {
        return has_image;
    }

    public void setHas_image(boolean has_image) {
        this.has_image = has_image;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


    public AccountImageVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(AccountImageVisibility visibility) {
        this.visibility = visibility;
    }
}
