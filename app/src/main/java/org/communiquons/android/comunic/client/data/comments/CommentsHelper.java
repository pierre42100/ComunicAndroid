package org.communiquons.android.comunic.client.data.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;
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
     * Application context
     */
    private Context mContext;

    /**
     * Comments helper public constructor
     *
     * @param context The context of the application
     */
    public CommentsHelper(Context context){

        //Save application context (avoid to leak activities context)
        mContext = context.getApplicationContext();

    }


    /**
     * Intend to submit a new comment to a post
     *
     * @param postID The target post
     * @param comment The content of the comment
     * @return The ID of the created comment / -1 in case of failure
     */
    public int send_comment(int postID, String comment){

        //Create and perform an API request
        APIRequestParameters params = new APIRequestParameters(mContext, "comments/create");
        params.addInt("postID", postID);
        params.addString("content", comment);

        //Perform the request
        try {

            //Try to perform the request
            APIResponse response = new APIRequest().exec(params);

            //Check and return success
            return response.getJSONObject().getInt("commentID");

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get informations about a single comment
     *
     * @param commentID The ID of the comment to get
     * @return Informations about the comment or NULL in case of failure
     */
    @Nullable
    public Comment getInfosSingle(int commentID){

        //Prepare API request
        APIRequestParameters params = new APIRequestParameters(mContext, "comments/get_single");
        params.addInt("commentID", commentID);

        //Perform the request
        try {

            //Perform the request
            APIResponse response = new APIRequest().exec(params);

            //Process result (if any)
            if(response.getResponse_code() != 200)
                return null;

            //Parse and return response
            return parse_json_comment(response.getJSONObject());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

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
