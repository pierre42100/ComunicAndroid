package org.communiquons.android.comunic.client.data.models;

/**
 * Weblink model
 *
 * @author Pierre HUBERT
 */
public class WebLink {

    //Private fields
    private String url;
    private String title;
    private String description;
    private String imageURL;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasTitle() {
        return title != null;
    }

    public void setTitle(String title) {
        this.title = title;

        if(title != null)
            if(title.equals("null"))
                this.title = null;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public void setDescription(String description) {
        this.description = description;

        if(description != null)
            if(description.equals("null"))
                this.description = null;
    }

    public String getImageURL() {
        return imageURL;
    }

    public boolean hasImageURL(){
        return imageURL != null;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;

        if(imageURL != null)
            if(imageURL.equals("null"))
                this.imageURL = null;
    }
}
