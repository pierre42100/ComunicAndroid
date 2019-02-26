package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.CallsHelper;

/**
 * Hang up a call
 *
 * @author Pierre HUBERT
 */
public class HangUpCallTask extends SafeAsyncTask<Integer, Void, Boolean> {

    public HangUpCallTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        return new CallsHelper(getContext()).hangUp(integers[0]);
    }

}
