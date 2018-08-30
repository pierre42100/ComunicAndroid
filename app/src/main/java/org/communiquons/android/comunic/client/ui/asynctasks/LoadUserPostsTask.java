package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;

/**
 * Load user posts task
 *
 * @author Pierre HUBERT
 */
public class LoadUserPostsTask extends SafeAsyncTask<Void, Void, PostsList> {

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
    protected PostsList doInBackground(Void... voids) {
        PostsList list = new PostsHelper(getContext()).get_user(mUserID);

        //Get associated user information, if possible
        if(list != null)
            list.setUsersInfo(new GetUsersHelper(getContext()).getMultiple(list.getUsersId()));

        return list;
    }
}
