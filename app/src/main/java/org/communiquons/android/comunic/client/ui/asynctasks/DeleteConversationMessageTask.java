package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.ConversationMessagesHelper;

/**
 * Delete conversation message async task
 *
 * @author Pierre HUBERT
 */
public class DeleteConversationMessageTask extends SafeAsyncTask<Integer, Void, Boolean> {

    public DeleteConversationMessageTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        return new ConversationMessagesHelper(getContext()).deleteMessage(integers[0]);
    }
}