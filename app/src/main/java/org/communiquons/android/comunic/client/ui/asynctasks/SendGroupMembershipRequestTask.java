package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.GroupsHelper;

/**
 * Send a group membership request task
 *
 * @author Pierre HUBERT
 */
public class SendGroupMembershipRequestTask extends SafeAsyncTask<Integer, Void, Boolean> {
    public SendGroupMembershipRequestTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        return new GroupsHelper(getContext()).sendRequest(integers[0]);
    }
}
