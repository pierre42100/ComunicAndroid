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

        Post post = new PostsHelper(getContext()).getSingle(integers[0]);
        if(post == null) return null;

        PostsList list = new PostsList();
        list.add(post);

        list.setUsersInfo(new GetUsersHelper(getContext()).getMultiple(list.getUsersId()));

        return list;
    }
}
