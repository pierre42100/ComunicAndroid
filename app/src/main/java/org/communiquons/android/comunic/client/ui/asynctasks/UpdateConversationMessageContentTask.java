package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.ConversationMessagesHelper;
import org.communiquons.android.comunic.client.data.models.ConversationMessage;

/**
 * Update the content of a conversation message
 *
 * @author Pierre HUBERT
 */
public class UpdateConversationMessageContentTask extends SafeAsyncTask<ConversationMessage, Void, Boolean> {

    public UpdateConversationMessageContentTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackground(ConversationMessage... conversationMessages) {
        return new ConversationMessagesHelper(getContext()).updateMessage(conversationMessages[0]);
    }
}
