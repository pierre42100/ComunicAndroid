package org.communiquons.android.comunic.client.data.posts;

import android.content.Context;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.comments.CommentsHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Posts helper
 *
 * This helper contains methods that allows to manipulate posts
 *
 * @author Pierre HUBERT
 * Created by pierre on 1/21/18.
 */

public class PostsHelper {

    /**
     * The context of the application
     */
    private Context mContext;

    /**
     * Public constructor
     *
     * @param context The context of the application
     */
    public PostsHelper(Context context){
        mContext = context;
    }

    /**
     * Get information about a single post
     *
     * @param id The ID of the target post
     * @return Information about the post / null in case of failure
     */
    @Nullable
    public Post getSingle(int id){

        //Perform an API request
        APIRequestParameters params = new APIRequestParameters(mContext, "posts/get_single");
        params.addInt("postID", id);

        try {

            //Send the request to the server
            APIResponse response = new APIRequest().exec(params);

            //Check for errors
            if(response.getResponse_code() != 200)
                return null;

            //Parse and return information about the server
            return parse_json_post(response.getJSONObject());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the list of the posts of a user
     *
     * @param userID The ID of the user to get the post from
     * @return The list of posts / null in case of failure
     */
    @Nullable
    public PostsList get_user(int userID){

        //Perform a request on the API
        APIRequestParameters params = new APIRequestParameters(mContext, "posts/get_user");
        params.addInt("userID", userID);

        //Perform the request
        try {

            //Make the request on the API
            APIResponse response = new APIRequest().exec(params);

            //Get the list of posts and process it
            JSONArray posts = response.getJSONArray();
            PostsList list = new PostsList();

            for(int i = 0; i < posts.length(); i++){
                list.add(parse_json_post(posts.getJSONObject(i)));
            }

            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Intend to delete a post specified by its ID
     *
     * @param postID The ID of the post to delete
     * @return TRUE in case of SUCCESS / FALSE else
     */
    public boolean delete(int postID){

        //Perform the request on the server
        APIRequestParameters params = new APIRequestParameters(mContext, "posts/delete");
        params.addInt("postID", postID);

        //Intend to perform the request
        try {

            APIResponse response = new APIRequest().exec(params);

            return response.getResponse_code() == 200;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Intend to create a post
     *
     * @param post The post to create
     * @return The ID of the create post / -1 in case of failure
     */
    public int create(CreatePost post){

        //Prepare the request on the server
        APIRequestParameters params = new APIRequestParameters(mContext, "posts/create");

        //Put basic information about the post
        params.addString("content", post.getContent());

        //Determine the kind of post
        switch (post.getType()){
            case TEXT:
                params.addString("kind", "text");
                break;

            default:
                //Throw an exception
                throw new RuntimeException("The kind of post is unsupported!");
        }

        //Determine the visibility level of the post
        switch (post.getVisibilityLevel()){

            case PUBLIC:
                params.addString("visibility", "public");
                break;

            case FRIENDS:
                params.addString("visibility", "friends");
                break;

            case PRIVATE:
                params.addString("visibility", "private");
                break;

            default:
                throw new RuntimeException("Unsupported kind of Visibility level!");
        }

        //Set the kind of target page
        switch (post.getPage_type()){

            case USER_PAGE:
                params.addString("kind-page", "user");
                break;

            default:
                throw new RuntimeException("Unsupported kind of page !");
        }

        //Set the ID of the target page
        params.addInt("kind-id", post.getPage_id());

        //Perform the request on the server
        try {

            //Perform the request
            APIResponse response = new APIRequest().exec(params);

            //Check for errors
            if(response.getResponse_code() != 200)
                return -1;

            //Get and return the ID of the created post
            return response.getJSONObject().getInt("postID");

        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Parse a JSON post information into POST object
     *
     * @param json Source JSON post information
     * @return The created post element
     */
    private Post parse_json_post(JSONObject json) throws JSONException {

        Post post = new Post();

        //Parse JSON object
        post.setId(json.getInt("ID"));
        post.setUserID(json.getInt("userID"));
        post.setPost_time(json.getInt("post_time"));

        //Determine the type and the id of the page
        if(json.getInt("user_page_id") != 0){
            //Set information about the user
            post.setPage_type(PageType.USER_PAGE);
            post.setPage_id(json.getInt("user_page_id"));
        }


        post.setContent(json.getString("content"));
        post.setComments_list(CommentsHelper.parse_json_array(json.getJSONArray("comments")));

        //Determine the visibility level of the post
        switch (json.getString("visibility_level")){

            case "public":
                post.setVisibilityLevel(PostVisibilityLevels.PUBLIC);
                break;

            case "friends":
                post.setVisibilityLevel(PostVisibilityLevels.FRIENDS);
                break;

            case "private":
            default :
                post.setVisibilityLevel(PostVisibilityLevels.PRIVATE);
                break;

        }

        //Determine the type of the post
        switch (json.getString("kind")){

            case "text":
                post.setType(PostTypes.TEXT);
                break;

            case "image":
                post.setType(PostTypes.IMAGE);
                break;

            case "movie":
                post.setType(PostTypes.MOVIE);
                break;

            default:
                post.setType(PostTypes.UNKNOWN);

        }

        //Determine the user access level to the post
        switch (json.getString("user_access")){

            case "basic":
                post.setUser_access_level(PostUserAccess.BASIC_ACCESS);
                break;

            case "intermediate":
                post.setUser_access_level(PostUserAccess.INTERMEDIATE_ACCESS);
                break;

            case "full":
                post.setUser_access_level(PostUserAccess.FULL_ACCESS);
                break;

            default:
                post.setUser_access_level(PostUserAccess.NO_ACCESS);
        }

        //Get file path url (if any)
        if(json.getString("file_path_url") != null){
            post.setFile_path_url(json.getString("file_path_url"));
        }

        return post;
    }
}
