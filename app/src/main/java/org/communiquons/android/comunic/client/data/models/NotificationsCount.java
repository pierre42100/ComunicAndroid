package org.communiquons.android.comunic.client.data.models;

/**
 * Notifications count class
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/9/18.
 */

public class NotificationsCount {

    //Private fields
    private int notificationsCount;
    private int conversationsCount;
    private int friendsRequestsCount;
    private int pendingCalls = -1;


    //Set and get notifications count
    public void setNotificationsCount(int notificationsCount) {
        this.notificationsCount = notificationsCount;
    }

    public int getNotificationsCount() {
        return notificationsCount;
    }


    //Set and get conversations count
    public void setConversationsCount(int conversationsCount) {
        this.conversationsCount = conversationsCount;
    }

    public int getConversationsCount() {
        return conversationsCount;
    }

    //Getter and setter for friends requests
    public int getFriendsRequestsCount() {
        return friendsRequestsCount;
    }

    public void setFriendsRequestsCount(int friendsRequestsCount) {
        this.friendsRequestsCount = friendsRequestsCount;
    }

    public int getPendingCalls() {
        return pendingCalls;
    }

    public boolean hasPendingCalls() {
        return pendingCalls > 0;
    }

    public void setPendingCalls(int pendingCalls) {
        this.pendingCalls = pendingCalls;
    }
}
