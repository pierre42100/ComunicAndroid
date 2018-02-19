package org.communiquons.android.comunic.client.data.comments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Comments helper
 *
 * @author Pierre HUBERT
 * Created by pierre on 2/19/18.
 */

public class CommentsHelper {

    /**
     * Parse a json array that contains comment and return the list of comments as an object
     *
     * @param array The JSON array to parse
     * @return The generated list of comments objects / null if comments are disabled for the post
     * @throws JSONException In case of failure while decoding the list
     */
    @Nullable
    public static ArrayList<Comment> parse_json_array(@Nullable JSONArray array) throws JSONException
    {
        //Check if the comments are disabled on the post
        if(array == null)
            return null;

        ArrayList<Comment> list = new ArrayList<>();

        //Process each element
        for (int i = 0; i < array.length(); i++){

            //Parse JSON object
            list.add(parse_json_comment(array.getJSONObject(i)));

        }

        return list;
    }

    /**
     * Parse a JSON Object into a comment object
     *
     * @param json The JSON Object to parse
     * @return Generated comment object
     * @throws JSONException if the object is invalid
     */
    @NonNull
    private static Comment parse_json_comment(@NonNull JSONObject json) throws JSONException{

        Comment comment = new Comment();

        //Parse comment object
        comment.setId(json.getInt("ID"));
        comment.setUserID(json.getInt("userID"));
        comment.setPostID(json.getInt("postID"));
        comment.setTime_sent(json.getInt("time_sent"));
        comment.setContent(json.getString("content"));
        comment.setImage_path(json.getString("img_path"));
        comment.setImage_url(json.getString("img_url"));
        comment.setLikes(json.getInt("likes"));
        comment.setUser_like(json.getBoolean("userlike"));

        return comment;

    }
}
