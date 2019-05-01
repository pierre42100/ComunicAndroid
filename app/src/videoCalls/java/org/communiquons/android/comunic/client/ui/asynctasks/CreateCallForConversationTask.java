package org.communiquons.android.comunic.client.ui.asynctasks;

import android.content.Context;

import org.communiquons.android.comunic.client.data.helpers.CallsHelper;
import org.communiquons.android.comunic.client.data.models.CallInformation;

/**
 * Task used to create new calls from conversations
 *
 * @author Pierre HUBERT
 */
public class CreateCallForConversationTask extends SafeAsyncTask<Integer, Void, CallInformation> {

    public CreateCallForConversationTask(Context context) {
        super(context);
    }

    @Override
    protected CallInformation doInBackground(Integer... integers) {
        return new CallsHelper(getContext()).createForConversation(integers[0]);
    }
}
