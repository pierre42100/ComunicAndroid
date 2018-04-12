package org.communiquons.android.comunic.client.data.models;

/**
 * This models handles a friendship status
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/12/18.
 */

public class FriendshipStatus {

    //Private fields
    private boolean isFriend;
    private boolean sentRequest;
    private boolean receivedRequest;
    private boolean following;


    //Set and get the "are friend" value
    public void setAreFriend(boolean areFriend) {
        this.isFriend = areFriend;
    }

    public boolean isFriend() {
        return isFriend;
    }


    //Set and get the sent request status
    public void setSentRequest(boolean sentRequest) {
        this.sentRequest = sentRequest;
    }

    public boolean isSentRequest() {
        return sentRequest;
    }


    //Set and get the received request status
    public void setReceivedRequest(boolean receivedRequest) {
        this.receivedRequest = receivedRequest;
    }

    public boolean isReceivedRequest() {
        return receivedRequest;
    }

    //Set and get the following status
    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isFollowing() {
        return following;
    }
}
