package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.CallsConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Calls helper
 *
 * @author Pierre HUBERT
 */
public class CallsHelper extends BaseHelper {

    /**
     * Debug tag
     */
    private static final String TAG = CallsHelper.class.getSimpleName();

    /**
     * We consider that it is not required to get call configuration twice on single
     * application lifetime
     */
    private static CallsConfiguration mCallsConfiguration = null;

    public CallsHelper(Context context) {
        super(context);
    }

    /**
     * Get call configuration if required
     */
    public void getCallConfigurationIfRequired(){

        //If call configuration has already been retrieved, nothing to be done
        if(mCallsConfiguration != null)
            return;

        APIRequest request = new APIRequest(getContext(), "calls/config");

        try {

            //Execute request
            APIResponse response = request.exec();

            //Parse response
            mCallsConfiguration = JSONObjectToCallConfiguration(response.getJSONObject());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Get Calls configuration, if available
     *
     * @return Calls configuration
     */
    @Nullable
    public static CallsConfiguration getCallConfiguration(){
        return mCallsConfiguration;
    }


    /**
     * Checkout whether call system is currently available now or not.
     *
     * Notice : this value may return false even if call system is enabled, based
     * on the fact that call configuration may have not been already retrieved
     *
     * @return TRUE if call system is available / FALSE else
     */
    public static boolean isCallSystemAvailable(){
        return mCallsConfiguration != null && mCallsConfiguration.isEnabled();
    }


    /**
     * Turn a {@link JSONObject} object into a {@link CallsConfiguration} object.
     *
     * @param object Object to convert
     * @return The result of the operation
     * @throws JSONException Exception thrown in case of failure
     */
    private static CallsConfiguration JSONObjectToCallConfiguration(JSONObject object)
            throws JSONException {

        CallsConfiguration config = new CallsConfiguration();
        config.setEnabled(object.getBoolean("enabled"));

        //Get further information only if required
        if(config.isEnabled()){
            config.setMaximumNumberMembers(object.getInt("maximum_number_members"));
            config.setSignalServerName(object.getString("signal_server_name"));
            config.setSignalServerPort(object.getInt("signal_server_port"));
            config.setSignalServerSecure(object.getBoolean("is_signal_server_secure"));
            config.setStunServer(object.getString("stun_server"));
            config.setTurnServer(object.getString("turn_server"));
            config.setTurnUsername(object.getString("turn_username"));
            config.setTurnPassword(object.getString("turn_password"));
        }

        return  config;

    }
}
