package org.communiquons.android.comunic.client.data.utils;

import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.models.Friend;
import org.communiquons.android.comunic.client.data.models.UserInfo;

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
     * Merge a user information list with a friend list
     *
     * @param friendsList The list of friend of the user
     * @param userInfo Information about the friends
     */
    public static void MergeFriendsListWithUserInfo(ArrayList<Friend> friendsList,
                                                    ArrayMap<Integer, UserInfo> userInfo){

        //Process the list
        for(Friend friend : friendsList){
                friend.setUserInfo(userInfo.get(friend.getId()));
        }

    }
}
