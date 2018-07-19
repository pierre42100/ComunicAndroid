package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.models.APIFileRequest;
import org.communiquons.android.comunic.client.data.models.APIPostFile;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.CreatePost;
import org.communiquons.android.comunic.client.data.enums.PageType;
import org.communiquons.android.comunic.client.data.models.Post;
import org.communiquons.android.comunic.client.data.enums.PostTypes;
import org.communiquons.android.comunic.client.data.enums.PostUserAccess;
import org.communiquons.android.comunic.client.data.enums.PostVisibilityLevels;
import org.communiquons.android.comunic.client.data.arrays.PostsList;
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
     * Debug tag
     */
    private final static String TAG = "PostsHelper";

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
        APIRequest params = new APIRequest(mContext, "posts/get_single");
        params.addInt("postID", id);

        try {

            //Send the request to the server
            APIResponse response = new APIRequestHelper().exec(params);

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
        APIRequest params = new APIRequest(mContext, "posts/get_user");
        params.addInt("userID", userID);

        //Perform the request
        try {

            //Make the request on the API
            APIResponse response = new APIRequestHelper().exec(params);

            //Get the list of posts and process it
            JSONArray posts = response.getJSONArray();
            return parse_json_posts_list(posts);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the list of latest posts of a user
     *
     * @return The list of posts / null in case of failure
     */
    @Nullable
    public PostsList get_latest() {
        return get_latest(-1);
    }

    /**
     * Get the list of latest posts of a user
     *
     * @param from The ID of the newest post to start from (-1 to start from the newest post)
     * @return The list of posts / null in case of failure
     */
    @Nullable
    public PostsList get_latest(int from) {
        //Perform a request on the API
        APIRequest params = new APIRequest(mContext, "posts/get_latest");

        //Check if we have to start from a precise post
        if(from > 0)
            params.addInt("startFrom", from);

        //Perform the request
        try {

            //Make the request on the API
            APIResponse response = new APIRequestHelper().exec(params);

            //Get the list of posts and process it
            JSONArray posts = response.getJSONArray();
            return parse_json_posts_list(posts);

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
        APIRequest params = new APIRequest(mContext, "posts/delete");
        params.addInt("postID", postID);

        //Intend to perform the request
        try {

            APIResponse response = new APIRequestHelper().exec(params);

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
        APIFileRequest req = new APIFileRequest(mContext, "posts/create");

        //Put basic information about the post
        req.addString("content", post.getContent());

        //Determine the kind of post
        switch (post.getType()){
            case TEXT:
                req.addString("kind", "text");
                break;

            case IMAGE:
                req.addString("kind", "image");

                //Process image and add it to the request
                APIPostFile file = new APIPostFile();
                file.setFileName("image.png");
                file.setBitmap(post.getNewImage());
                file.setFieldName("image");
                req.addFile(file);
                break;

            default:
                //Throw an exception
                throw new RuntimeException("The kind of post is unsupported!");
        }

        //Determine the visibility level of the post
        switch (post.getVisibilityLevel()){

            case PUBLIC:
                req.addString("visibility", "public");
                break;

            case FRIENDS:
                req.addString("visibility", "friends");
                break;

            case PRIVATE:
                req.addString("visibility", "private");
                break;

            default:
                throw new RuntimeException("Unsupported kind of Visibility level!");
        }

        //Set the kind of target page
        switch (post.getPage_type()){

            case USER_PAGE:
                req.addString("kind-page", "user");
                break;

            default:
                throw new RuntimeException("Unsupported kind of page !");
        }

        //Set the ID of the target page
        req.addInt("kind-id", post.getPage_id());

        //Perform the request on the server
        try {

            //Perform the request (upload a file if required)
            APIResponse response;
            if(req.containsFiles())
                response = new APIRequestHelper().execPostFile(req);
            else
                response = new APIRequestHelper().exec(req);

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
     * Intend to update the content of a post
     *
     * @param postId The ID of the post to update
     * @param content The new content for the post
     * @return TRUE in case of success / FALSE else
     */
    public boolean update_content(int postId, String content) {

        //Perform a request on the API
        APIRequest params = new APIRequest(mContext, "posts/update_content");
        params.addString("new_content", content);
        params.addInt("postID", postId);

        //Try to perform the request
        try {
            APIResponse response = new APIRequestHelper().exec(params);
            return response.getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Turn a JSONArray that contains information about posts into PostList object
     *
     * @param array The list of posts to process
     * @return The list of posts / null in case of failure
     * @throws JSONException in case of failure
     */
    private PostsList parse_json_posts_list(JSONArray array) throws JSONException {
        PostsList list = new PostsList();

        for(int i = 0; i < array.length(); i++){
            list.add(parse_json_post(array.getJSONObject(i)));
        }

        return list;
    }

    /**
     * Parse a JSON post information into POST object
     *
     * @param json Source JSON post information
     * @return The created post element
     * @throws JSONException in case of failure
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

            case "pdf":
                post.setType(PostTypes.PDF);
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

        //Get information about likes
        post.setNumberLike(json.getInt("likes"));
        post.setLiking(json.getBoolean("userlike"));

        //Get file path url (if any)
        if(json.getString("file_path_url") != null){
            post.setFile_path_url(json.getString("file_path_url"));
        }

        return post;
    }
}
