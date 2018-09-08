package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.GroupsHelper;

/**
 * Cancel a group membership request task
 *
 * @author Pierre HUBERT
 */
public class CancelGroupMembershipRequestTask extends SafeAsyncTask<Integer, Void, Boolean> {
    public CancelGroupMembershipRequestTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        return new GroupsHelper(getContext()).cancelRequest(integers[0]);
    }
}
