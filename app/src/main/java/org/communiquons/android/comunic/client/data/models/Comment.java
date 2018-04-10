package org.communiquons.android.comunic.client.data.models;

import android.support.annotation.Nullable;

/**
 * Comment model
 *
 * @author Pierre HUBERT
 * Created by pierre on 2/19/18.
 */

public class Comment {

    //Private fields
    private int id;
    private int userID;
    private int postID;
    private int time_sent;
    private String content;
    private String image_path;
    private String image_url;
    private int likes;
    private boolean user_like;

    //This field is used to indicate the corresponding comment has been deleted
    private boolean deleted = false;


    //Get and set comment ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    //Get and set user ID
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }


    //Get and set post ID
    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }


    //Get and set time_sent
    public int getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(int time_sent) {
        this.time_sent = time_sent;
    }

    //Get and set comment content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    //Get and set comment image path
    @Nullable
    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(@Nullable String image_path) {
        this.image_path = image_path;
    }

    //Get and set comment image URL
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }



    //Get and set the number of likes over the comment
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }


    //Get and set if the current user like the comment or not
    public boolean isLiking() {
        return user_like;
    }

    public void setUser_like(boolean user_like) {
        this.user_like = user_like;
    }

    //Get and set the current deleted state of the comment
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
