package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;

import org.communiquons.android.comunic.client.data.models.ConversationInfo;

/**
 * Calls helper
 *
 * Placeholder for the flavor of the application where there is no call possible
 *
 * @author Pierre HUBERT
 */
public class CallsHelper extends BaseHelper {

    public CallsHelper(Context context) {
        super(context);
    }


    /**
     * No call possible with this flavour
     */
    public static boolean IsCallSystemAvailable(){
        return false;
    }

    /**
     * No call for no conversation
     *
     * @param c Target conversation
     * @return false
     */
    public static boolean IsCallSystemAvailableForConversation(ConversationInfo c){
        return false;
    }
}
