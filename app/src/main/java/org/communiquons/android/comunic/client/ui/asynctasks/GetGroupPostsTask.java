package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;

/**
 * Get group posts safe async task
 *
 * @author Pierre HUBERT
 */
public class GetGroupPostsTask extends SafeAsyncTask<Integer, Void, PostsList> {

    public GetGroupPostsTask(Context context) {
        super(context);
    }

    @Override
    protected PostsList doInBackground(Integer... integers) {
        PostsHelper helper = new PostsHelper(getContext());
        PostsList list;

        //Check if a start point has been specified
        if(integers.length == 1)
            list = helper.get_group(integers[0]);
        else
            list = helper.get_group(integers[0], integers[1]);

        //Check if the list of posts could not be retrieved
        if(list == null)
            return null;

        //Try to load related information
        if(!helper.load_related_information(list))
            return null;

        //Success. Now return information
        return list;
    }
}
