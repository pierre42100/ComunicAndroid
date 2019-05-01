package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.models.ConversationInfo;

/**
 * Calls helper
 *
 * Placeholder for the flavor of the application where there is no call possible
 *
 * @author Pierre HUBERT
 */
public class CallsHelper extends BaseHelper {

    /**
     * Debug tag
     */
    private static final String TAG = CallsHelper.class.getSimpleName();


    public CallsHelper(Context context) {
        super(context);
    }

    /**
     * Get call configuration if required
     */
    public void getCallConfigurationIfRequired(){
        // Do nothing
    }

    /**
     * Get Calls configuration, if available
     *
     * Note if IsCallSystemAvailable returned TRUE, it is guaranteed that this method WILL NOT
     * return null
     *
     * @return Calls configuration
     */
    @Nullable
    public static Object GetCallsConfiguration(){
        return null;
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
     * @param conversation Target conversation
     * @return false
     */
    public static boolean IsCallSystemAvailableForConversation(ConversationInfo conversation){
        return false;
    }
}
