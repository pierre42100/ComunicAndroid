package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.CallsHelper;
import org.communiquons.android.comunic.client.data.models.NextPendingCallInformation;

/**
 * Get next pending call task
 *
 * @author Pierre HUBERT
 */
public class GetNextPendingCallTask extends SafeAsyncTask<Void, Void, NextPendingCallInformation> {

    public GetNextPendingCallTask(Context context) {
        super(context);
    }

    @Override
    protected NextPendingCallInformation doInBackground(Void... voids) {

        CallsHelper callsHelper =  new CallsHelper(getContext());
        NextPendingCallInformation call = callsHelper.getNextPendingCall();

        //Load call name if possible
        if(call == null || (call.isHasPendingCall() && callsHelper.getCallName(call) == null))
            return null;

        return call;
    }

}
