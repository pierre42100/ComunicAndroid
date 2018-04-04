package org.communiquons.android.comunic.client.data.posts;

import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.comments.Comment;

import java.util.ArrayList;

/**
 * Post model
 *
 * This object contains the information about a single post
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/21/18.
 */

public class Post {

    //Private fields
    private int id;
    private int userID;
    private int post_time;
    private PageType page_type;
    private int page_id;
    private String content;
    private PostTypes type;

    //Related with visibility
    private PostVisibilityLevels visibilityLevel;
    private PostUserAccess user_access_level = PostUserAccess.NO_ACCESS;

    //Likes
    private int numberLike;
    private boolean isLiking;

    //Comments
    private ArrayList<Comment> comments_list;

    //Files specific
    private String file_path_url;


    //Set and get the ID of the post
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    //Set the get the ID of the owner of the post
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }


    //Set and get the post creation time
    void setPost_time(int post_time) {
        this.post_time = post_time;
    }

    public int getPost_time() {
        return post_time;
    }


    //Set and get the type of the page
    public void setPage_type(PageType page_type) {
        this.page_type = page_type;
    }

    public PageType getPage_type() {
        return page_type;
    }


    //Get and set page id
    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public int getPage_id() {
        return page_id;
    }

    //Set and get the content of the post
    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    //Set and get the type of the post
    public void setType(PostTypes type) {
        this.type = type;
    }

    public PostTypes getType() {
        return type;
    }


    //Set and get post visibility level
    public void setVisibilityLevel(PostVisibilityLevels visibilityLevel) {
        this.visibilityLevel = visibilityLevel;
    }

    public PostVisibilityLevels getVisibilityLevel() {
        return visibilityLevel;
    }

    //Set and get comments list
    void setComments_list(ArrayList<Comment> comments_list) {
        this.comments_list = comments_list;
    }

    @Nullable
    public ArrayList<Comment> getComments_list() {
        return comments_list;
    }

    //Set and post user access level
    void setUser_access_level(PostUserAccess user_access_level) {
        this.user_access_level = user_access_level;
    }

    public PostUserAccess getUser_access_level() {
        return user_access_level;
    }


    //Set and get the number of likes other the like
    void setNumberLike(int numberLike) {
        this.numberLike = numberLike;
    }

    public int getNumberLike() {
        return numberLike;
    }


    //Set and get the liking state over the post
    void setLiking(boolean liking) {
        isLiking = liking;
    }

    public boolean isLiking() {
        return isLiking;
    }

    /**
     * Check whether the user can delete the post or not
     *
     * @return TRUE if the post can be deleted by the user / FALSE else
     */
    public boolean canDelete(){
        return getUser_access_level() == PostUserAccess.INTERMEDIATE_ACCESS ||
                getUser_access_level() == PostUserAccess.FULL_ACCESS;
    }

    //Set and get file path url
    void setFile_path_url(String file_path_url) {
        this.file_path_url = file_path_url;
    }

    public String getFile_path_url() {
        return file_path_url;
    }
}

