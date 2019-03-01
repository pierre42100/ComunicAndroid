package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.ConversationsListHelper;
import org.communiquons.android.comunic.client.data.models.ConversationInfo;

import java.util.ArrayList;

/**
 * Get conversations list task
 *
 * @author Pierre HUBERT
 */
public class GetConversationsListTask extends SafeAsyncTask<Boolean, Void, ArrayList<ConversationInfo>> {

    public GetConversationsListTask(Context context) {
        super(context);
    }

    @Override
    protected ArrayList<ConversationInfo> doInBackground(Boolean... booleans) {
        ConversationsListHelper conversationsListHelper = new ConversationsListHelper(getContext());

        boolean getOnline = booleans[0];
        ArrayList<ConversationInfo> list;


        if(getOnline)
            list = conversationsListHelper.getOnline();
        else
            list = conversationsListHelper.getCachedList();

        if(list == null || !conversationsListHelper.getConversationsDisplayName(list))
            return null;

        return list;
    }
}
