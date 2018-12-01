package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.GroupsHelper;
import org.communiquons.android.comunic.client.data.models.AdvancedGroupInfo;

/**
 * This task is used to get a group advanced information
 *
 * @author Pierre HUBERT
 */
public class GetGroupAdvancedInfoTask extends SafeAsyncTask<Integer, Void, AdvancedGroupInfo> {
    public GetGroupAdvancedInfoTask(Context context) {
        super(context);
    }

    @Override
    protected AdvancedGroupInfo doInBackground(Integer... integers) {
        return new GroupsHelper(getContext()).getAdvancedInformation(integers[0]);
    }
}
