package org.communiquons.android.comunic.client.data.models;

import org.communiquons.android.comunic.client.data.enums.MemberCallStatus;

/**
 * Single call member information
 *
 * @author Pierre HUBERT
 */
public class CallMember {

    //Private fields
    private int userID;
    private int callID;
    private String userCallID;
    private MemberCallStatus status;

    public CallMember() {
    }

    public CallMember(int userID, int callID, String userCallID, MemberCallStatus status) {
        this.userID = userID;
        this.callID = callID;
        this.userCallID = userCallID;
        this.status = status;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getCallID() {
        return callID;
    }

    public void setCallID(int callID) {
        this.callID = callID;
    }

    public String getUserCallID() {
        return userCallID;
    }

    public void setUserCallID(String userCallID) {
        this.userCallID = userCallID;
    }

    public MemberCallStatus getStatus() {
        return status;
    }

    public void setStatus(MemberCallStatus status) {
        this.status = status;
    }
}
