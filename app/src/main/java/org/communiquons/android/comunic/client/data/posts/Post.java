package org.communiquons.android.comunic.client.data.posts;

import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.comments.Comment;

import java.util.ArrayList;

/**
 * Post model
 *
 * This object contains the informations about a single post
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/21/18.
 */

public class Post {

    //Private fields
    private int id;
    private int userID;
    private int post_time;
    private String content;
    private PostTypes type;
    private PostVisibilityLevels visibilityLevel;
    private ArrayList<Comment> comments_list;
    private PostUserAccess user_access_level = PostUserAccess.NO_ACCESS;

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


    //Set and get the content of the post
    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    //Set and get the type of the post
    void setType(PostTypes type) {
        this.type = type;
    }

    public PostTypes getType() {
        return type;
    }


    //Set and get post visibility level
    void setVisibilityLevel(PostVisibilityLevels visibilityLevel) {
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

    //Set and get file path url
    void setFile_path_url(String file_path_url) {
        this.file_path_url = file_path_url;
    }

    public String getFile_path_url() {
        return file_path_url;
    }
}

