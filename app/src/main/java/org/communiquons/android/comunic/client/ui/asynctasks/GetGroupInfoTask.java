package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.GroupsHelper;
import org.communiquons.android.comunic.client.data.models.GroupInfo;

public class GetGroupInfoTask extends SafeAsyncTask<Integer, Void, GroupInfo> {

    public GetGroupInfoTask(Context context) {
        super(context);
    }

    @Override
    protected GroupInfo doInBackground(Integer... integers) {

        //By default, we force the request
        return new GroupsHelper(getContext()).getInfoSingle(integers[0], true);

    }
}
