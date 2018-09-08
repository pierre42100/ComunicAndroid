package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.GroupsHelper;

/**
 * Leave group task
 *
 * @author Pierre HUBERT
 */
public class LeaveGroupTask extends SafeAsyncTask<Integer, Void, Boolean> {
    public LeaveGroupTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        return new GroupsHelper(getContext()).leaveGroup(integers[0]);
    }
}
