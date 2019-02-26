package org.communiquons.android.comunic.client.data.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Single call information
 *
 * @author Pierre HUBERT
 */
public class CallInformation {

    //Private fields
    private int id;
    private int conversationID;
    private int lastActive;
    private ArrayList<CallMember> members = null;


    private String callName = null;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConversationID() {
        return conversationID;
    }

    public void setConversationID(int conversationID) {
        this.conversationID = conversationID;
    }

    public int getLastActive() {
        return lastActive;
    }

    public void setLastActive(int lastActive) {
        this.lastActive = lastActive;
    }

    public ArrayList<CallMember> getMembers() {
        return members;
    }

    public void addMember(@NonNull CallMember member){

        if(members == null)
            members = new ArrayList<>();

        members.add(member);
    }

    /**
     * Check out whether all the members of the call left it or not except a specific user
     *
     * @return TRUE if all members except specified user ID left the call / FALSE else
     */
    public boolean hasAllMembersLeftCallExcept(int userID){

        for(CallMember member : members)
            if(!member.leftCall() && member.getUserID() != userID)
                return false;

        return true;
    }

    public void setMembers(ArrayList<CallMember> members) {
        this.members = members;
    }

    /**
     * Find a member by user ID
     *
     * @param userID The ID of the user to search
     * @return Information about the target user
     */
    public CallMember findMember(int userID){
        for(CallMember member : members)
            if(member.getUserID() == userID)
                return member;

        throw new RuntimeException("Specified user was not found in the conversation!");
    }

    /**
     * Find a member by call ID
     *
     * @param userCallID The ID of the target user
     * @return Information about the user / null in case of failure
     */
    @Nullable
    public CallMember findMember(String userCallID){
        for(CallMember member : members)
            if(member.getUserCallID().equals(userCallID))
                return member;

        throw new RuntimeException("Specified user was not found in the conversation!");
    }


    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }
}
