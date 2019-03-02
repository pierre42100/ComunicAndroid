package org.communiquons.android.comunic.client.data.arrays;

import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.models.Friend;
import org.communiquons.android.comunic.client.data.models.UserInfo;

import java.util.ArrayList;

/**
 * Friends list
 *
 * @author Pierre HUBERT
 */
public class FriendsList extends ArrayList<Friend> {

    /**
     * Get the IDs of all the friends of this list
     *
     * @return The list of the id of the friend
     */
    public ArrayList<Integer> getFriendsIDs(){

        ArrayList<Integer> IDs = new ArrayList<>();

        for(Friend friend : this)
            IDs.add(friend.getId());

        return IDs;
    }

    /**
     * Merge a user information list with a friend list
     *
     * @param usersInfo Information about the friends
     */
    public void mergeFriendsListWithUserInfo(ArrayMap<Integer, UserInfo> usersInfo){

        //Process the list
        for(Friend friend : this){
            friend.setUserInfo(usersInfo.get(friend.getId()));
        }

    }

}
