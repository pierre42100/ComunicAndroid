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
public class GetConversationsListTask extends SafeAsyncTask<Void, Void, ArrayList<ConversationInfo>> {

    public GetConversationsListTask(Context context) {
        super(context);
    }

    @Override
    protected ArrayList<ConversationInfo> doInBackground(Void... voids) {
        ConversationsListHelper conversationsListHelper = new ConversationsListHelper(getContext());

        ArrayList<ConversationInfo> list = conversationsListHelper.getOnline();

        if(list == null || !conversationsListHelper.getConversationsDisplayName(list))
            return null;

        return list;
    }
}
