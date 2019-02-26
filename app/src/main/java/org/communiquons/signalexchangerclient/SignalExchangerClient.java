package org.communiquons.signalexchangerclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Signal exchanger client
 *
 * @author Pierre HUBERT
 */
public class SignalExchangerClient extends WebSocketListener {

    /**
     * Debug log
     */
    private static final String TAG = SignalExchangerClient.class.getSimpleName();

    /**
     * Instance configuration
     */
    private SignalExchangerInitConfig mConfig;

    /**
     * Signal exchanger callback
     */
    @Nullable
    private SignalExchangerCallback mCallback;

    /**
     * Http Client
     */
    private OkHttpClient mClient;

    /**
     * Current WebSocket connection
     */
    private WebSocket mWebSocket;

    /**
     * Initialize a SignalExchanger client
     *
     * @param config Configuration of the client
     * @param cb Callback function to call when we got information update
     */
    public SignalExchangerClient(@NonNull SignalExchangerInitConfig config,
                                 @Nullable SignalExchangerCallback cb){

        //Save configuration
        this.mConfig = config;
        this.mCallback = cb;

        //Connect to the WebSocket
        String url = (config.isSecure() ? "wss" : "ws")
                + "://" + config.getDomain() + ":" + config.getPort() + "/socket";

        mClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        mWebSocket = mClient.newWebSocket(request, this);
    }

    /**
     * Get current client configuration
     *
     * @return Configuration of the client
     */
    public SignalExchangerInitConfig getConfig() {
        return mConfig;
    }

    /**
     * Set the callback to use on new updates
     *
     * @param mCallback Callback to use
     */
    public void setCallback(@Nullable SignalExchangerCallback mCallback) {
        this.mCallback = mCallback;
    }

    /**
     * Check out whether the current client is connected to a server or not
     *
     * @return true if the client is connected to a server / false else
     */
    public boolean isConnected(){
        return mWebSocket != null;
    }

    /**
     * Send ready message to a client
     *
     * @param target_client_id The ID of the target client
     */
    public void sendReadyMessage(String target_client_id){
        sendData(new ClientRequest()
                .addBoolean("ready_msg", true)
                .addString("target_id", target_client_id));
    }

    /**
     * Send a signal to a target
     *
     * @param target_id The ID of the target
     * @param signal The signal to send
     */
    public void sendSignal(String target_id, String signal){
        sendData(new ClientRequest()
                .addString("target_id", target_id)
                .addString("signal", signal));
    }

    /**
     * Send a session description to a target
     *
     * @param target_id The ID of the target
     * @param description The description
     */
    public void sendSessionDescription(String target_id, SessionDescription description){
        try {
            JSONObject object = new JSONObject();
            object.put("type", description.type.canonicalForm());
            object.put("sdp", description.description);
            sendSignal(target_id, object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Send an Ice Candidate to a remote peer
     *
     * @param target_id The ID of the target
     * @param candidate The candidate to send
     */
    public void sendIceCandidate(String target_id, IceCandidate candidate){
        try {
            JSONObject candidateObj = new JSONObject();
            candidateObj.put("sdpMid", candidate.sdpMid);
            candidateObj.put("sdpMLineIndex", candidate.sdpMLineIndex);
            candidateObj.put("candidate", candidate.sdp);

            JSONObject object = new JSONObject();
            object.put("candidate", candidateObj);
            sendSignal(target_id, object.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send data to the server
     *
     * @param request The data to send to the server
     */
    private void sendData(@NonNull ClientRequest request){

        //Continues only in case of active connection
        if(!isConnected()) {
            return;
        }

        //Send data to the server
        Log.v(TAG, "Sending " + request.get().toString());
        mWebSocket.send(request.get().toString());
    }

    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     */
    public void onOpen(WebSocket webSocket, Response response) {

        //Save WebSocket object
        this.mWebSocket = webSocket;

        //Send the ID of current client to the server
        sendData(new ClientRequest()
                .addString("client_id", mConfig.getClientID()));

        //Inform we are connected
        if(mCallback != null)
            mCallback.onConnectedToSignalingServer();

    }

    /** Invoked when a text (type {@code 0x1}) message has been received. */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.v(TAG, "Received new message from server: " + text);

        //Decode message
        try {
            JSONObject message = new JSONObject(text);

            //Ready message callback
            if(message.has("ready_message_sent")){

                if(mCallback != null)
                    mCallback.onReadyMessageCallback(
                            message.getString("target_id"),
                            message.getInt("number_of_targets")
                    );

            }

            //Ready message
            else if(message.has("ready_msg")){

                if(mCallback != null)
                    mCallback.onReadyMessage(
                            message.getString("source_id")
                    );

            }

            //Signal
            else if(message.has("signal")) {

                if(mCallback != null)
                    mCallback.onSignal(
                            message.getString("source_id"),
                            message.getString("signal")
                    );

                processReceivedSignal(message.getString("source_id"),
                        message.getString("signal"));
            }

            //Send signal callback
            else if(message.has("signal_sent")){

                if(mCallback != null)
                    mCallback.onSendSignalCallback(
                            message.getInt("number_of_targets")
                    );

            }

            //Success message
            else if(message.has("success"))
                Log.v(TAG, "Success: " + message.getString("success"));

            //Unrecognized message
            else
                Log.e(TAG, "Message from server not understood!");

        } catch (JSONException e) {
            e.printStackTrace();

            if(mCallback != null)
                mCallback.onSignalServerError("Could not parse response from server!", e);
        }
    }

    /**
     * Process a received signal
     *
     * @param source_id The source of the signal
     * @param signal The signal to process
     */
    private void processReceivedSignal(String source_id, String signal) throws JSONException {

        JSONObject object = new JSONObject(signal);

        //Ice candidate
        if(object.has("candidate")) {

            JSONObject candidate = object.getJSONObject("candidate");

            if (mCallback != null)
                mCallback.gotRemoteIceCandidate(
                        source_id, new IceCandidate(
                                candidate.getString("sdpMid"),
                                candidate.getInt("sdpMLineIndex"),
                                candidate.getString("candidate")
                        )
                );

        }

        //Sdp signal
        else if(object.has("sdp") && object.has("type")){

            SessionDescription.Type type = SessionDescription.Type.fromCanonicalForm(
                    object.getString("type"));
            String sdp = object.getString("sdp");

            if(mCallback != null)
                mCallback.gotRemoteSessionDescription(source_id,
                        new SessionDescription(type, sdp));

        }

        else
            Log.e(TAG, "Could not understand received signal!");

    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    public void onClosed(WebSocket webSocket, int code, String reason) {
        mWebSocket = null;
    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {

        if(mCallback != null)
            mCallback.onSignalServerError(t.getMessage(), t);
    }
}
