package org.communiquons.android.comunic.client.data.models;

/**
 * Pending call information object
 *
 * @author Pierre HUBERT
 */
public class NextPendingCallInformation extends CallInformation {

    //Private fields
    private boolean hasPendingCall;


    public boolean isHasPendingCall() {
        return hasPendingCall;
    }

    public void setHasPendingCall(boolean hasPendingCall) {
        this.hasPendingCall = hasPendingCall;
    }
}
