package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;

/**
 * Get latest posts task
 *
 * @author Pierre HUBERT
 */
public class GetLatestPostsTask extends SafeAsyncTask<Integer, Void, PostsList> {

    public GetLatestPostsTask(Context context) {
        super(context);
    }

    @Override
    protected PostsList doInBackground(Integer... integers) {

        PostsHelper helper = new PostsHelper(getContext());

        //Get the list of posts
        int from  = integers[0] == 0 ? -1 : integers[0];
        PostsList list = helper.get_latest(from);
        if(list == null) return null;

        //Load related information
        if(!helper.load_related_information(list))
            return null;

        return list;
    }
}
