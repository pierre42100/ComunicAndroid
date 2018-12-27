package org.communiquons.android.comunic.client.data.models;

/**
 * This object handles a search result and all the information associated with it
 *
 * @author Pierre HUBERT
 */
public class SearchResultWithInfo extends SearchResult {

    private GroupInfo groupInfo;
    private UserInfo userInfo;

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
