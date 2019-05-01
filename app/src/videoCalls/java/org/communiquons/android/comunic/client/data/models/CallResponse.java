package org.communiquons.android.comunic.client.data.models;

/**
 * Response of a user to a call
 *
 * @author Pierre HUBERT
 */
public class CallResponse {

    //Private fields
    private int callID;
    private boolean accept;

    public CallResponse(int callID, boolean accept) {
        this.callID = callID;
        this.accept = accept;
    }

    public int getCallID() {
        return callID;
    }

    public void setCallID(int callID) {
        this.callID = callID;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }
}
