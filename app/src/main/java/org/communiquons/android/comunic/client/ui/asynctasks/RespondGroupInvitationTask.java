package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.GroupsHelper;

/**
 * Respond to a group membership invitation task
 *
 * @author Pierre HUBERT
 */
public class RespondGroupInvitationTask extends SafeAsyncTask<Integer, Void, Boolean> {

    private boolean mAccept;


    public RespondGroupInvitationTask(Context context, boolean accept) {
        super(context);
        this.mAccept = accept;
    }


    @Override
    protected Boolean doInBackground(Integer... integers) {
        return new GroupsHelper(getContext()).respondInvitation(integers[0], mAccept);
    }
}
