package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;

import java.util.ArrayList;

/**
 * Get the list of friends of a specific user with their information
 *
 * @author Pierre HUBERT
 */
public class GetUserFriendsListTask extends SafeAsyncTask<Integer, Void, ArrayMap<Integer, UserInfo>> {

    public GetUserFriendsListTask(Context context) {
        super(context);
    }

    @Override
    protected ArrayMap<Integer, UserInfo> doInBackground(Integer... integers) {
        ArrayList<Integer> list = new FriendsListHelper(getContext()).getUserFriends(integers[0]);

        if(list == null)
            return null;

        return new GetUsersHelper(getContext()).getMultiple(list);
    }
}
