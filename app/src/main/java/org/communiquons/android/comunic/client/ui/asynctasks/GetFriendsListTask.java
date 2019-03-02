package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.arrays.FriendsList;
import org.communiquons.android.comunic.client.data.helpers.FriendsListHelper;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.models.UserInfo;

/**
 * Get the list of friends of the user
 *
 * @author Pierre HUBERT
 */
public class GetFriendsListTask extends SafeAsyncTask<Void, Void, FriendsList> {

    public GetFriendsListTask(Context context) {
        super(context);
    }

    @Override
    protected FriendsList doInBackground(Void... voids) {

        //Fetch the list of friends
        FriendsList friendsList = new FriendsListHelper(getContext()).get();

        ArrayMap<Integer, UserInfo> userInfo;

        //Try to get information about related users if possible
        if (friendsList == null || (userInfo = new GetUsersHelper(getContext()).getMultiple(
                friendsList.getFriendsIDs())) == null)
            return null;

        //Merge friend and user and return result
        friendsList.mergeFriendsListWithUserInfo(userInfo);
        return friendsList;
    }
}
