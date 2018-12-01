package org.communiquons.android.comunic.client.data.models;

/**
 * Advanced group information
 *
 * Contains more information about group than GroupInfo
 *
 * @author Pierre HUBERT
 */
public class AdvancedGroupInfo extends GroupInfo {

    //Private fields
    private int time_create;
    private String url;
    private String description;
    private int number_likes;
    private boolean is_liking;

    public int getTime_create() {
        return time_create;
    }

    public void setTime_create(int time_create) {
        this.time_create = time_create;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumber_likes() {
        return number_likes;
    }

    public void setNumber_likes(int number_likes) {
        this.number_likes = number_likes;
    }

    public boolean isIs_liking() {
        return is_liking;
    }

    public void setIs_liking(boolean is_liking) {
        this.is_liking = is_liking;
    }
}
