package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.CallsHelper;
import org.communiquons.android.comunic.client.data.models.CallInformation;

/**
 * Task to get information about a call
 *
 * @author Pierre HUBERT
 */
public class GetCallInformationTask extends SafeAsyncTask<Integer, Void, CallInformation> {

    public GetCallInformationTask(Context context) {
        super(context);
    }

    @Override
    protected CallInformation doInBackground(Integer... integers) {

        CallsHelper callsHelper = new CallsHelper(getContext());

        CallInformation callInformation = callsHelper.getInfo(integers[0]);

        //Try to get call name
        if(callInformation == null || callsHelper.getCallName(callInformation) == null)
            return null;

        return callInformation;
    }
}
