package org.communiquons.android.comunic.client.ui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.communiquons.android.comunic.client.data.models.CallResponse;
import org.communiquons.android.comunic.client.ui.asynctasks.RespondToCallTask;

import java.util.Objects;

import static org.communiquons.android.comunic.client.ui.Constants.IntentActions.ACTION_REJECT_INCOMING_CALL;

/**
 * This broadcast receiver is used to reject incoming calls
 *
 * @author Pierre HUBERT
 */
public class RejectCallReceiver extends BroadcastReceiver {

    /**
     * Debug tag
     */
    private static final String TAG = RejectCallReceiver.class.getSimpleName();

    /**
     * Mandatory argument that includes call id
     */
    public static final String ARGUMENT_CALL_ID = "call_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent == null || !ACTION_REJECT_INCOMING_CALL.equals(intent.getAction()))
            throw new RuntimeException(TAG + " was incorrectly triggered!");

        int callID = Objects.requireNonNull(intent.getExtras()).getInt(ARGUMENT_CALL_ID);

        //Send the response back to the server
        Log.v(TAG, "Reject call " + callID);
        RespondToCallTask respondToCallTask = new RespondToCallTask(context);
        respondToCallTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new CallResponse(
                callID,
                false
        ));
    }
}
