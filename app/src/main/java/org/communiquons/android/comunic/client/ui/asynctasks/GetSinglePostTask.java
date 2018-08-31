package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.arrays.PostsList;
import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.PostsHelper;
import org.communiquons.android.comunic.client.data.models.Post;

/**
 * Get single post async task
 *
 * @author Pierre HUBERT
 */
public class GetSinglePostTask extends SafeAsyncTask<Integer, Void, PostsList> {

    public GetSinglePostTask(Context context) {
        super(context);
    }

    @Override
    protected PostsList doInBackground(Integer... integers) {

        PostsHelper helper = new PostsHelper(getContext());

        Post post = helper.getSingle(integers[0]);
        if(post == null) return null;

        PostsList list = new PostsList();
        list.add(post);

        if(!helper.load_related_information(list))
            return null;

        return list;
    }
}
