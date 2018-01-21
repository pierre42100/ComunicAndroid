package org.communiquons.android.comunic.client.data.posts;

import java.util.ArrayList;

/**
 * Posts list
 *
 * @author PIerre HUBERT
 * Created by pierre on 1/21/18.
 */

public class PostsList extends ArrayList<Post> {

    /**
     * Get the IDs of the users who created the posts
     *
     * @return The list of users of the post
     */
    public ArrayList<Integer> getUsersId(){

        ArrayList<Integer> ids = new ArrayList<>();

        for(Post post : this){
            int userID = post.getUserID();

            //Add User ID if required
            if(ids.contains(userID))
                ids.add(userID);
        }

        return ids;

    }

}
