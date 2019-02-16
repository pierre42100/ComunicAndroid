package org.communiquons.android.comunic.client.data.models;

import android.support.annotation.NonNull;

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

    public void setMembers(ArrayList<CallMember> members) {
        this.members = members;
    }
}
