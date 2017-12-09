package org.communiquons.android.comunic.client.data.conversations;

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
    private String name;
    private boolean following;
    private boolean saw_last_message;
    private ArrayList<Integer> members;

    /**
     * Set the ID of the conversation
     *
     * @param ID The ID of the conversation
     */
    public void setID(int ID) {
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
    public void setID_owner(int ID_owner) {
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
    public void setLast_active(int last_active) {
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
    public void setName(String name) {
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
     * Specify whether the user is following the conversation or not
     *
     * Follows means get notifications, and get informed of the changes in the conversation
     *
     * @param following True if the user is following the conversation
     */
    public void setFollowing(boolean following) {
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
    public void setSaw_last_message(boolean saw_last_message) {
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
    public void addMember(Integer id){
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
}
