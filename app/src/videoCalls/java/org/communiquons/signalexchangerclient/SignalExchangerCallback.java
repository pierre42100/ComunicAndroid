package org.communiquons.signalexchangerclient;

import android.support.annotation.Nullable;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * This interface should be implemented by the classes
 * that makes use of the {@link SignalExchangerClient}
 * in order to get updated about new information
 * availability
 *
 * @author Pierre HUBERT
 */
public interface SignalExchangerCallback {

    /**
     * Method called when an error occur
     *
     * @param msg Message associated to the error
     * @param t Optional associated throwable
     */
    void onSignalServerError(String msg, @Nullable Throwable t);

    /**
     * Method called once we are connected to the server
     */
    void onConnectedToSignalingServer();

    /**
     * Method called on ready message callback
     *
     * @param target_id The ID of the target
     * @param number_targets The number of peers who received the message
     */
    void onReadyMessageCallback(String target_id, int number_targets);

    /**
     * Method called when this client receive a new ready message signal
     *
     * @param source_id The source of the message
     */
    void onReadyMessage(String source_id);

    /**
     * Method called when the client received a signal
     *
     * @param source_id The source of the signal
     * @param signal The signal
     */
    void onSignal(String source_id, String signal);

    /**
     * Send signals callback
     *
     * @param number_targets The number of targets for the signal
     */
    void onSendSignalCallback(int number_targets);

    /**
     * This method is called once we received a remote Ice Candidate
     *
     * @param source_id The source of the signal
     * @param iceCandidate The candidate itself
     */
    void gotRemoteIceCandidate(String source_id, IceCandidate iceCandidate);

    /**
     * This method is called when we have got a new remote session description
     *
     * @param source_id The source of the signal
     * @param sessionDescription The session description
     */
    void gotRemoteSessionDescription(String source_id, SessionDescription sessionDescription);
}
