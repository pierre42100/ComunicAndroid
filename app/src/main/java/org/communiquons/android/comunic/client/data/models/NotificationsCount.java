package org.communiquons.android.comunic.client.data.models;

/**
 * Notifications count service
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/9/18.
 */

public class NotificationsCount {

    //Private fields
    private int notificationsCount;
    private int conversationsCount;
    private int friendsRequestsCount;


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
}
