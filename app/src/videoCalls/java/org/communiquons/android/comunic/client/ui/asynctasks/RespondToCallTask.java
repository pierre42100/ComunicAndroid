package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.CallsHelper;
import org.communiquons.android.comunic.client.data.models.CallResponse;

/**
 * Respond to call task
 *
 * @author Pierre HUBERT
 */
public class RespondToCallTask extends SafeAsyncTask<CallResponse, Void, Boolean> {

    public RespondToCallTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(CallResponse... callResponses) {
        return new CallsHelper(getContext()).respondToCall(callResponses[0]);
    }
}
