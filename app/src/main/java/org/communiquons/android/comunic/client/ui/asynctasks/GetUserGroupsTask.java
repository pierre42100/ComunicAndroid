package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.helpers.GroupsHelper;
import org.communiquons.android.comunic.client.data.models.GroupInfo;

import java.util.ArrayList;

/**
 * Get user groups AsyncTask
 *
 * @author Pierre HUBERT
 */
public class GetUserGroupsTask extends SafeAsyncTask<Void, Void, ArrayMap<Integer, GroupInfo>> {
    public GetUserGroupsTask(Context context) {
        super(context);
    }


    @Override
    protected ArrayMap<Integer, GroupInfo> doInBackground(Void... voids) {

        GroupsHelper groupsHelper = new GroupsHelper(getContext());

        ArrayList<Integer> groups = groupsHelper.getUserList();

        if(groups == null)
            return null;

        return groupsHelper.getInfoMultiple(groups, true);
    }
}
