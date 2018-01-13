package org.communiquons.android.comunic.client.data.friendsList;

import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;

/**
 * This class contains information about a friend but also about user himself
 *
 * @author Pierre Hubert
 * Created by pierre on 11/15/17.
 */

public class FriendUser {

    /**
     * Friendship informations
     */
    private Friend friend;

    /**
     * Informations about the user
     */
    private UserInfo userInfo;

    /**
     * Create a FriendUser object
     *
     * @param friend Informations about the friend
     */
    public FriendUser(Friend friend){
        this.friend = friend;
    }

    /**
     * Set a new user informations
     *
     * @param userInfo Informations about the user
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * Get informations about the user
     *
     * @return User informations
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }


    /**
     * Get informations about the friendship
     *
     * @return Informations about the friendship
     */
    public Friend getFriend() {
        return friend;
    }

    /**
     * Set friendship informations
     *
     * @param friend New friendship informations
     */
    public void setFriend(Friend friend) {
        this.friend = friend;
    }


    /***
     * Get the ID of the user
     *
     * @return The id of the user
     */
    public int get_user_id(){
        return userInfo.getId();
    }
}
