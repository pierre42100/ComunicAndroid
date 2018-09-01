package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;

/**
 * Load user posts task
 *
 * @author Pierre HUBERT
 */
public class LoadUserPostsTask extends SafeAsyncTask<Integer, Void, PostsList> {

    private int mUserID;

    /**
     * Constructor
     *
     * @param userID The ID of the target user
     * @param context Application context
     */
    public LoadUserPostsTask(int userID, Context context) {
        super(context);

        this.mUserID = userID;
    }

    @Override
    protected PostsList doInBackground(Integer ...integers) {

        PostsHelper helper = new PostsHelper(getContext());

        PostsList list;

        if(integers.length == 0)
            list = helper.get_user(mUserID);
        else
            list = helper.get_user(mUserID, integers[0]);

        //Get associated user information, if possible
        if(list == null) return null;

        if(!helper.load_related_information(list))
            return null;

        return list;
    }
}
