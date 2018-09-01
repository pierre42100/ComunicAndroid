package org.communiquons.android.comunic.client.data.models;

import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.enums.GroupRegistrationLevel;
import org.communiquons.android.comunic.client.data.enums.GroupVisibility;
import org.communiquons.android.comunic.client.data.enums.GroupsMembershipLevels;
import org.communiquons.android.comunic.client.data.enums.GroupPostsCreationLevel;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * Group information base model
 *
 * @author Pierre HUBERT
 */
public class GroupInfo {

    //Private fields
    private int id;
    private String name;
    private String icon_url;
    private int number_members;
    private GroupsMembershipLevels membershipLevel;
    private GroupVisibility visibility;
    private GroupRegistrationLevel registrationLevel;
    private GroupPostsCreationLevel postCreationLevel;
    private String virtualDirectory;
    private boolean following;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The name of the group ready to be shown on a TextView
     */
    public String getDisplayName(){
        return UiUtils.prepareStringTextView(getName());
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public int getNumber_members() {
        return number_members;
    }

    public void setNumber_members(int number_members) {
        this.number_members = number_members;
    }

    public GroupsMembershipLevels getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(GroupsMembershipLevels membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public GroupVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(GroupVisibility visibility) {
        this.visibility = visibility;
    }

    public GroupRegistrationLevel getRegistrationLevel() {
        return registrationLevel;
    }

    public void setRegistrationLevel(GroupRegistrationLevel registrationLevel) {
        this.registrationLevel = registrationLevel;
    }

    public GroupPostsCreationLevel getPostCreationLevel() {
        return postCreationLevel;
    }

    public void setPostCreationLevel(GroupPostsCreationLevel creationLevel) {
        this.postCreationLevel = creationLevel;
    }

    @Nullable
    public String getVirtualDirectory() {
        return virtualDirectory;
    }

    public boolean hasVirtualDirectory(){
        return virtualDirectory != null;
    }

    public void setVirtualDirectory(@Nullable String virtualDirectory) {
        this.virtualDirectory = virtualDirectory;

        if(virtualDirectory != null){
            if(virtualDirectory.equals("null"))
                this.virtualDirectory = null;
        }
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}
