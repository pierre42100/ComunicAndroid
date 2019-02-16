package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.CallsHelper;

/**
 * Get call configuration task
 *
 * @author Pierre HUBERT
 */
public class GetCallConfigurationTask extends SafeAsyncTask<Void, Void, Void> {

    public GetCallConfigurationTask(Context context) {
        super(context);
    }

    @Override
    protected Void doInBackground(Void[] objects) {

        new CallsHelper(getContext()).getCallConfigurationIfRequired();

        return null;
    }
}
