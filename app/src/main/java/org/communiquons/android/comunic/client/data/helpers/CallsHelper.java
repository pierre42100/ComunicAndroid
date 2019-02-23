package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.enums.MemberCallStatus;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.CallInformation;
import org.communiquons.android.comunic.client.data.models.CallMember;
import org.communiquons.android.comunic.client.data.models.CallsConfiguration;
import org.communiquons.android.comunic.client.data.models.NextPendingCallInformation;
import org.json.JSONArray;
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
            mCallsConfiguration = JSONObjectToCallsConfiguration(response.getJSONObject());


        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public static CallsConfiguration GetCallsConfiguration(){
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
    public static boolean IsCallSystemAvailable(){
        return mCallsConfiguration != null && mCallsConfiguration.isEnabled();
    }

    /**
     * Create a call for a conversation, returns information about this call then
     *
     * Note : if a similar call already exists, it will be returned instead of
     * creating a new call
     *
     * @param convID The ID of the target conversation
     * @return Information about the created call / null in case of failure
     */
    @Nullable
    public CallInformation createForConversation(int convID){

        APIRequest request = new APIRequest(getContext(), "calls/createForConversation");
        request.addInt("conversationID", convID);
        try {

            //Execute request
            APIResponse response = request.exec();

            return JSONObjectToCallInformation(response.getJSONObject(), null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Get the next pending call for a user
     *
     * @return Next pending call for a user
     */
    @Nullable
    public NextPendingCallInformation getNextPendingCall(){

        APIRequest request = new APIRequest(getContext(), "calls/nextPending");

        try {
            JSONObject object = request.exec().getJSONObject();

            //Check if there is no pending call available
            NextPendingCallInformation call = new NextPendingCallInformation();
            if(object.has("notice")){
                call.setHasPendingCall(false);
                return call;
            }


            call.setHasPendingCall(true);
            JSONObjectToCallInformation(object, call);
            return call;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


    /**
     * Try to get and return call information
     *
     * @param call Target call information
     * @return The name of call / null in case of failure
     */
    @Nullable
    public String getCallName(@NonNull CallInformation call){

        //Get call name
        String name = new ConversationsListHelper(getContext())
                .getConversationName(call.getConversationID());

        if(name == null)
            return null;

        call.setCallName(name);
        return name;
    }


    /**
     * Turn a {@link JSONObject} object into a {@link CallsConfiguration} object.
     *
     * @param object Object to convert
     * @return The result of the operation
     * @throws JSONException Exception thrown in case of failure
     */
    private static CallsConfiguration JSONObjectToCallsConfiguration(JSONObject object)
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

    /**
     * Turn a {@link JSONObject} into {@link CallInformation} object
     *
     * @param object object to convert
     * @param call Call object to fill (null = none)
     * @return Generated CallInformation object
     * @throws JSONException in case of failure
     */
    private static CallInformation JSONObjectToCallInformation(JSONObject object,
                                                               @Nullable CallInformation call)
            throws JSONException {

        //Check if object has to be instanced
        if(call == null)
            call = new CallInformation();

        call.setId(object.getInt("id"));
        call.setConversationID(object.getInt("conversation_id"));
        call.setLastActive(object.getInt("last_active"));

        JSONArray members = object.getJSONArray("members");
        for (int i = 0; i < members.length(); i++) {

            JSONObject member_obj = members.getJSONObject(i);

            call.addMember(new CallMember(
                    member_obj.getInt("userID"),
                    member_obj.getInt("call_id"),
                    member_obj.getString("user_call_id"),
                    StringToMemberCallStatus(member_obj.getString("status"))
            ));
        }

        return call;

    }

    /**
     * Turn a string into a {@link MemberCallStatus}
     *
     * @param s String to convert
     * @return Generated MemberCallStatus
     */
    private static MemberCallStatus StringToMemberCallStatus(String s){
        switch (s){

            case "accepted":
                return MemberCallStatus.ACCEPTED;

            case "rejected":
                return MemberCallStatus.REJECTED;

            case "hang_up":
                return MemberCallStatus.HANG_UP;

            case "unknown":
            default:
                return MemberCallStatus.UNKNOWN;
        }
    }
}
