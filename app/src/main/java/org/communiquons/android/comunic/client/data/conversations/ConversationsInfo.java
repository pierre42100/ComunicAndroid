package org.communiquons.android.comunic.client.data.conversations;

import android.support.annotation.Nullable;
import android.util.Log;

import org.communiquons.android.comunic.client.data.utils.ArrayUtils;
import org.communiquons.android.comunic.client.data.utils.Utilities;

import java.util.ArrayList;

/**
 * Contains the information about a single conversation
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/9/17.
 */

public class ConversationsInfo {

    /**
     * Values of the conversation
     */
    private int ID;
    private int ID_owner;
    private int last_active;
    private String name = null;
    private boolean following;
    private boolean saw_last_message;
    private ArrayList<Integer> members;

    /**
     * Additional values used to display conversation information
     */
    private String displayName = null;

    /**
     * Set the ID of the conversation
     *
     * @param ID The ID of the conversation
     */
    void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Get the ID of the conversation
     *
     * @return The ID of the conversation
     */
    public int getID() {
        return ID;
    }

    /**
     * Set the ID of the owner of teh conversation
     *
     * @param ID_owner The ID of the owner of the conversation
     */
    void setID_owner(int ID_owner) {
        this.ID_owner = ID_owner;
    }

    /**
     * Get the ID of the owner of the conversation
     *
     * @return The ID of the owner of the conversation
     */
    public int getID_owner() {
        return ID_owner;
    }

    /**
     * Set last activity of the conversation
     *
     * @param last_active The timestamp of the last activity of the conversation
     */
    void setLast_active(int last_active) {
        this.last_active = last_active;
    }

    /**
     * Get the last activity of the conversation
     *
     * @return The last activity time of the conversation
     */
    public int getLast_active() {
        return last_active;
    }

    /**
     * Set the name of the conversation
     *
     * @param name The name of the conversation
     */
    public void setName(@Nullable String name) {

        //Check the validity of the name
        if(("false").equals(name) || ("null").equals(name) || name == null)
            this.name = null;
        else
            this.name = name;
    }

    /**
     * Get the name of the conversation
     *
     * @return The name of the conversation
     */
    public String getName() {
        return name;
    }

    /**
     * Check if the conversation has a name or not
     *
     * @return True if the conversation has a name / false else
     */
    public boolean hasName(){
        return name != null;
    }

    /**
     * Specify whether the user is following the conversation or not
     *
     * Follows means get notifications, and get informed of the changes in the conversation
     *
     * @param following True if the user is following the conversation
     */
    void setFollowing(boolean following) {
        this.following = following;
    }

    /**
     * Check whether the user is following or not the conversation
     *
     * @return True if user is following the conversation
     */
    public boolean isFollowing() {
        return following;
    }

    /**
     * Specify if the current user has seen the last message of the conversation
     *
     * @param saw_last_message True if the user has seen the last message of the conversation
     */
    void setSaw_last_message(boolean saw_last_message) {
        this.saw_last_message = saw_last_message;
    }

    /**
     * Check if the user has saw the last message of the conversation
     *
     * @return True if the user has seen the last message of the conversation
     */
    public boolean hasSaw_last_message() {
        return saw_last_message;
    }

    /**
     * Set the list of members of the conversation
     *
     * @param members The IDs of the members of the conversation
     */
    public void setMembers(ArrayList<Integer> members) {
        this.members = members;
    }

    /**
     * Add a member to the list of members
     *
     * @param id The ID of the member to add
     */
    void addMember(Integer id){
        if(members == null)
            members = new ArrayList<>();

        members.add(id);
    }

    /**
     * Get the number of members of the conversation
     *
     * @return The number of members of the conversation
     */
    public int countMembers(){
        return members == null ? 0 : members.size();
    }

    /**
     * Get the list of members of the conversation
     *
     * @return The list of members of the conversation
     *
     */
    public ArrayList<Integer> getMembers() {
        return members;
    }

    /**
     * Get the list of members as a string
     *
     * @return The list of members as a string
     */
    public String getMembersString() {
        if(members == null)
            return "";

        return ArrayUtils.int_array_to_string(members, ",");
    }

    /**
     * Set the list of members from a string generated using {@link #getMembersString()}
     *
     * @param input The input string
     */
    public void parseMembersString(String input){

        members = new ArrayList<>();

        String[] membersStr = input.split(",");

        for(String member : membersStr){
            members.add(Integer.decode(member));
        }

    }


    /**
     * Set the displayed name of the conversation
     *
     * @param displayName The displayed name of the conversation
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the displayed name of the conversation
     *
     * @return The displayed name of the conversation
     */
    public String getDisplayName() {
        return Utilities.prepareStringTextView(displayName);
    }

    /**
     * Check if the conversation has a display name or not
     *
     * @return true If the conversation has a display name
     */
    public boolean hasDisplayName(){
        return displayName == null;
    }
}
