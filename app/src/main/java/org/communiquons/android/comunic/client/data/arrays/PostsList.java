package org.communiquons.android.comunic.client.data.arrays;

import org.communiquons.android.comunic.client.data.models.Comment;
import org.communiquons.android.comunic.client.data.models.Post;

import java.util.ArrayList;

/**
 * Posts list
 *
 * @author PIerre HUBERT
 * Created by pierre on 1/21/18.
 */

public class PostsList extends ArrayList<Post> {

    /**
     * Debug tag
     */
    private static final String TAG = "PostsList";

    /**
     * Get the IDs of the users who created the posts and their comments
     *
     * @return The list of users of the post
     */
    public ArrayList<Integer> getUsersId(){

        ArrayList<Integer> ids = new ArrayList<>();

        for(Post post : this){
            int userID = post.getUserID();

            //Add User ID if required
            if(!ids.contains(userID))
                ids.add(userID);

            if(post.getComments_list() != null){

                //Process the list of comments
                for(Comment comment : post.getComments_list()){

                    if(ids.contains(comment.getUserID()))
                        ids.add(comment.getUserID());

                }

            }
        }

        return ids;

    }

}
