package org.communiquons.android.comunic.client.data.utils;

import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.models.Friend;
import org.communiquons.android.comunic.client.data.models.FriendUser;

import java.util.ArrayList;

/**
 * Friends utilities
 *
 * @author Pierre Hubert
 * Created by pierre on 11/15/17.
 */

public class FriendsUtils {

    /**
     * Given a friend list, it will return the IDs of the friends
     *
     * @param friendsList the friends list to process
     * @return The list of the id of the friend
     */
    public static ArrayList<Integer> getFriendsIDs(ArrayList<Friend> friendsList){
        ArrayList<Integer> IDs = new ArrayList<>();

        for(Friend friend : friendsList)
            IDs.add(friend.getId());

        return IDs;
    }

    /**
     * Merge a friends user list with a user info list, return the result by a FriendUser list
     *
     * @param friendsList The list of friend of the user
     * @param userInfos Informations about the user
     * @return The result of the operation
     */
    public static ArrayList<FriendUser> merge_friends_user_infos_list(ArrayList<Friend> friendsList,
                                                                      ArrayMap<Integer, UserInfo> userInfos){

        ArrayList<FriendUser> list = new ArrayList<>();

        //Process the list
        for(Friend friend : friendsList){

            UserInfo userInfo;

            if((userInfo = userInfos.get(friend.getId())) != null){
                FriendUser item = new FriendUser(friend);
                item.setUserInfo(userInfo);
                list.add(item);
            }

        }

        return list;

    }
}
