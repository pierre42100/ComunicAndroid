package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.ConversationMessagesHelper;
import org.communiquons.android.comunic.client.data.models.NewConversationMessage;

/**
 * Send conversation message task
 *
 * @author Pierre HUBERT
 */
public class SendConversationMessageTask extends SafeAsyncTask<NewConversationMessage, Void, Boolean> {

    public SendConversationMessageTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(NewConversationMessage... newConversationMessages) {
        return new ConversationMessagesHelper(getContext()).sendMessage(newConversationMessages[0]);
    }
}
