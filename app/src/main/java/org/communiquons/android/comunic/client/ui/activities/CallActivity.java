package org.communiquons.android.comunic.client.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.appspot.apprtc.AppRTCClient;
import org.appspot.apprtc.PeerConnectionClient;
import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.enums.MemberCallStatus;
import org.communiquons.android.comunic.client.data.helpers.CallsHelper;
import org.communiquons.android.comunic.client.data.models.CallInformation;
import org.communiquons.android.comunic.client.data.models.CallMember;
import org.communiquons.android.comunic.client.data.models.CallResponse;
import org.communiquons.android.comunic.client.data.models.CallsConfiguration;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.ui.arrays.CallPeersConnectionsList;
import org.communiquons.android.comunic.client.ui.asynctasks.GetCallInformationTask;
import org.communiquons.android.comunic.client.ui.asynctasks.HangUpCallTask;
import org.communiquons.android.comunic.client.ui.asynctasks.RespondToCallTask;
import org.communiquons.android.comunic.client.ui.models.CallPeerConnection;
import org.communiquons.android.comunic.client.ui.receivers.PendingCallsBroadcastReceiver;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;
import org.communiquons.signalexchangerclient.SignalExchangerCallback;
import org.communiquons.signalexchangerclient.SignalExchangerClient;
import org.communiquons.signalexchangerclient.SignalExchangerInitConfig;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

import java.util.Objects;

import static org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FILL;

/**
 * Call activity
 *
 * @author Pierre HUBERT
 */
public class CallActivity extends BaseActivity implements SignalExchangerCallback {

    /**
     * Debug tag
     */
    private static final String TAG = CallActivity.class.getSimpleName();

    /**
     * Mandatory argument that includes call id
     */
    public static final String ARGUMENT_CALL_ID = "call_id";


    /**
     * Permissions requests codes
     */
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private static final int MY_PERMISSIONS_REQUEST = 102;

    /**
     * Refresh call information thread
     */
    private RefreshCallInformation mRefreshCallInformation = null;

    /**
     * Current call ID and information
     */
    private int mCallID = -1;
    private CallInformation mCallInformation = null;

    /**
     * Signal exchanger client
     */
    private SignalExchangerClient mSignalExchangerClient = null;

    /**
     * Specify whether call was stopped or not
     */
    private boolean mStopped = false;
    private boolean mIsCameraStopped = false;
    private boolean mIsMicrophoneStopped = false;

    /**
     * Connections list
     */
    private CallPeersConnectionsList mList = new CallPeersConnectionsList();


    /**
     * WebRTC attributes
     */
    private EglBase rootEglBase;
    ProxyVideoSink mLocalProxyVideoSink;


    /**
     * Views
     */
    private ProgressBar mProgressBar;
    private ImageButton mHangUpButton;
    private LinearLayout mRemoteVideosLayout;
    private SurfaceViewRenderer mLocalVideoView;
    private View mButtonsView;
    private ImageButton mStopCameraButton;
    private ImageButton mStopMicrophoneButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        mCallID = getIntent().getIntExtra(ARGUMENT_CALL_ID, 0);

        //Get views
        initViews();
        initVideos();

        //Mark the call as accepted
        RespondToCallTask respondToCallTask = new RespondToCallTask(this);
        respondToCallTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                new CallResponse(mCallID, true));
        getTasksManager().addTask(respondToCallTask);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Refresh at a regular interval information about the call
        mRefreshCallInformation = new RefreshCallInformation();
        mRefreshCallInformation.start();


    }

    @Override
    protected void onResume() {
        super.onResume();

        //Hide call notifications
        PendingCallsBroadcastReceiver.RemoveCallNotification(this);

        //Make sure we have access to user camera and microphone
        askForPermissions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRefreshCallInformation.interrupt();
    }

    /**
     * Request access to user camera and microphone devices
     *
     * Based on https://github.com/sergiopaniego/WebRTCAndroidExample
     */
    private void askForPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST);
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    /**
     * Get views
     */
    private void initViews(){

        mProgressBar = findViewById(R.id.progressBar);
        mRemoteVideosLayout = findViewById(R.id.remoteVideosLayout);
        mLocalVideoView = findViewById(R.id.local_video);
        mButtonsView = findViewById(R.id.buttonsLayout);

        mHangUpButton = findViewById(R.id.hangUp);
        mHangUpButton.setOnClickListener(v -> hangUp());

        ImageButton switchCameraButton = findViewById(R.id.switchCameraButton);
        switchCameraButton.setOnClickListener(v -> switchCamera());

        mStopCameraButton = findViewById(R.id.stopCameraButton);
        mStopCameraButton.setOnClickListener(v -> toggleStopCamera());

        mStopMicrophoneButton = findViewById(R.id.stopMicrophoneButton);
        mStopMicrophoneButton.setOnClickListener(v -> toggleStopMicrophone());
    }


    private void initVideos(){
        rootEglBase = EglBase.create();

        mLocalVideoView.init(rootEglBase.getEglBaseContext(), null);
        mLocalVideoView.setZOrderMediaOverlay(true);

        mLocalProxyVideoSink = new ProxyVideoSink();
        mLocalProxyVideoSink.setTarget(mLocalVideoView);
    }


    /**
     * Refresh call information
     */
    private void getCallInformation(){

        GetCallInformationTask getCallInformationTask = new GetCallInformationTask(this);
        getCallInformationTask.setOnPostExecuteListener(this::onGotCallInformation);
        getCallInformationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCallID);

    }

    /**
     * Once we have got information about the call
     *
     * @param info Information about the call
     */
    private void onGotCallInformation(@Nullable CallInformation info){

        if(mStopped) return;

        if(info == null){
            Toast.makeText(this, R.string.err_get_call_info, Toast.LENGTH_SHORT).show();
            return;
        }

        setTitle(info.getCallName());
        mCallInformation = info;

        //Check if everyone left the conversation
        if(mCallInformation.hasAllMembersLeftCallExcept(AccountUtils.getID(this))){
            Toast.makeText(this, R.string.notice_call_terminated, Toast.LENGTH_SHORT).show();
            hangUp();
            return;
        }

        //Connect to signaling server
        if(mSignalExchangerClient == null){
            initializeSignalClient();
            return;
        }

        //Check connection is establish
        if(!mSignalExchangerClient.isConnected())
            return;

        processClientsConnections();
    }


    private void initializeSignalClient(){

        CallsConfiguration callsConfiguration = CallsHelper.GetCallsConfiguration();

        assert callsConfiguration != null;
        mSignalExchangerClient = new SignalExchangerClient(new SignalExchangerInitConfig(
                callsConfiguration.getSignalServerName(),
                callsConfiguration.getSignalServerPort(),
                mCallInformation.findMember(AccountUtils.getID(this)).getUserCallID(),
                callsConfiguration.isSignalServerSecure()
        ), this);
    }


    private void processClientsConnections(){

        if(mStopped) return;

        //Process each peer connection
        for(CallMember member : mCallInformation.getMembers())
            processClientConnection(member);
    }

    private void processClientConnection(CallMember member){

        //Skip current user
        if(member.getUserID() == AccountUtils.getID(this))
            return;

        //Check if the member left the conversation
        if(member.getStatus() != MemberCallStatus.ACCEPTED){
            disconnectFromPeer(member);

            return;
        }

        if(mList.find(member) == null && member.getUserID() > AccountUtils.getID(this)) {
            createPeerConnection(member, false);
        }

        CallPeerConnection connection = mList.find(member);

        if(connection != null) {

            if(!connection.isConnected())
                mSignalExchangerClient.sendReadyMessage(connection.getMember().getUserCallID());

            Objects.requireNonNull(connection).setMember(member);
        }
    }

    /**
     * Create the peer connection for a specific call member
     *
     * @param member Target member
     * @param isInitiator Specify whether if we should send the offer or not to this user
     */
    private void createPeerConnection(CallMember member, boolean isInitiator){

        if(mStopped) return;

        Log.v(TAG, "Create peer connection for connection with user " + member.getUserID());

        CallPeerConnection callPeer = new CallPeerConnection(member);
        mList.add(callPeer);

        EglBase eglBase = EglBase.create();

        //Create peer connection
        PeerConnectionClient peerConnectionClient = new PeerConnectionClient(
                getApplicationContext(),
                eglBase,
                new PeerConnectionClient.PeerConnectionParameters(
                        true,
                        false,
                        false,
                        640,
                        480,
                        15,
                        0,
                        "",
                        true,
                        false,
                        0,
                        null,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        null
                ),
                new PeerConnectionEvents(callPeer)
        );
        callPeer.setPeerConnectionClient(peerConnectionClient);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionClient.createPeerConnectionFactory(options);


        //Signaling parameters
        AppRTCClient.SignalingParameters parameters = new AppRTCClient.SignalingParameters(
                CallsHelper.GetPeerServers(), isInitiator, null,
                null, null, null, null
        );




        //Initialize video view
        SurfaceViewRenderer remoteView = new SurfaceViewRenderer(this);
        remoteView.init(eglBase.getEglBaseContext(), null);
        remoteView.setZOrderMediaOverlay(false);
        remoteView.setScalingType(SCALE_ASPECT_FILL);
        remoteView.setEnableHardwareScaler(false);
        callPeer.setRemoteViewView(remoteView);
        remoteView.setOnClickListener(v -> switchButtonsVisibility());

        mRemoteVideosLayout.addView(remoteView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));



        ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
        remoteProxyRenderer.setTarget(callPeer.getRemoteViewView());
        callPeer.getRemoteSinks().add(remoteProxyRenderer);
        callPeer.setRemoteProxyRenderer(remoteProxyRenderer);

        //Start connection
        peerConnectionClient.createPeerConnection(
                mLocalProxyVideoSink,
                callPeer.getRemoteSinks(),
                createCameraCapturer(new Camera1Enumerator(false)),
                parameters
        );

        if(isInitiator)
            peerConnectionClient.createOffer();
    }

    /**
     * Hang up call
     */
    private void hangUp(){

        mHangUpButton.setVisibility(View.GONE);
        mStopped = true;

        if(mRefreshCallInformation != null)
            mRefreshCallInformation.interrupt();

        if(mSignalExchangerClient != null)
            mSignalExchangerClient.close();

        while(mList.size() > 0)
            disconnectFromPeer(mList.get(0).getMember());

        HangUpCallTask hangUpCallTask = new HangUpCallTask(getApplicationContext());
        hangUpCallTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCallID);

        finish();
    }

    /**
     * Disconnect from a specific peer
     *
     * @param member Information about related call member
     */
    private void disconnectFromPeer(CallMember member){

        CallPeerConnection callPeer = mList.find(member);
        if(callPeer == null)
            return;

        ((ProxyVideoSink)callPeer.getRemoteProxyRenderer()).setTarget(null);

        callPeer.getPeerConnectionClient().close();

        //Remove the views
        mRemoteVideosLayout.removeView(callPeer.getRemoteViewView());

        mList.remove(callPeer);
    }


    private void switchCamera(){
        for(CallPeerConnection c : mList)
            c.getPeerConnectionClient().switchCamera();
    }

    private void toggleStopCamera(){
        mIsCameraStopped = !mIsCameraStopped;
        for(CallPeerConnection c : mList) {
            if(mIsCameraStopped)
                c.getPeerConnectionClient().stopVideoSource();
            else
                c.getPeerConnectionClient().startVideoSource();
        }

        mStopCameraButton.setImageDrawable(UiUtils.getDrawable(this,
                mIsCameraStopped ? R.drawable.ic_videocam_off : R.drawable.ic_videocam));
    }

    private void toggleStopMicrophone(){
        mIsMicrophoneStopped = !mIsMicrophoneStopped;

        for(CallPeerConnection c : mList)
            c.getPeerConnectionClient().setAudioEnabled(!mIsMicrophoneStopped);

        mStopMicrophoneButton.setImageDrawable(UiUtils.getDrawable(this,
                mIsMicrophoneStopped ? R.drawable.ic_mic_off : R.drawable.ic_mic));
    }

    private void switchButtonsVisibility(){

        boolean show = !getSupportActionBar().isShowing();

        if(show)
            getSupportActionBar().show();
        else
            getSupportActionBar().hide();

        mButtonsView.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    //Based on https://github.com/vivek1794/webrtc-android-codelab
    @Nullable
    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator){
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        Log.e(TAG, "Could not get user camera!");

        return null;
    }


    @Override
    public void onSignalServerError(String msg, @Nullable Throwable t) {
        runOnUiThread(() -> Toast.makeText(this,
                R.string.err_connect_signaling_server, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onConnectedToSignalingServer() {
        runOnUiThread(this::processClientsConnections);
    }

    @Override
    public void onReadyMessageCallback(String target_id, int number_targets) {
        Log.e(TAG, "Send ready message callback");
    }

    @Override
    public void onReadyMessage(String source_id) {

        runOnUiThread(() -> {

            //Ignore message if a connection has already been established
            if (mList.findByCallID(source_id) != null) {
                Log.e(TAG, "Ignored ready message from " + source_id + " because a connection has already be made!");
                return;
            }


            CallMember member = mCallInformation.findMember(source_id);

            if (member == null) {
                Log.e(TAG, source_id + " sent a ready message but it does not belong to the conversation!");
                return;
            }

            Log.v(TAG, source_id + " informed it is ready to establish connection.");
            createPeerConnection(member, true);

        });
    }

    @Override
    public void onSignal(String source_id, String signal) {
        Log.e(TAG, "Received new signal from " + source_id);
    }

    @Override
    public void onSendSignalCallback(int number_targets) {
        Log.e(TAG, "Send signal callback, number of targets: " + number_targets);
    }

    @Override
    public void gotRemoteIceCandidate(String source_id, IceCandidate iceCandidate) {

        runOnUiThread(() -> {

            CallPeerConnection connection = mList.findByCallID(source_id);
            if(connection == null) {
                Log.e(TAG, "Dropped ICE candidate from " + source_id +  " no peer connection was ready to receive it!");
                return;
            }

            connection.getPeerConnectionClient().addRemoteIceCandidate(iceCandidate);
        });
    }

    @Override
    public void gotRemoteSessionDescription(String source_id, SessionDescription sessionDescription) {

        runOnUiThread(() -> {

            CallPeerConnection connection = mList.findByCallID(source_id);
            if(connection == null) {
                Log.e(TAG, "Dropped session description from " + source_id +  " no peer connection was ready to receive it!");
                return;
            }

            connection.getPeerConnectionClient().setRemoteDescription(sessionDescription);
            connection.getPeerConnectionClient().createAnswer();
        });


    }


    /**
     * Class used to received events that comes from a connection
     */
    private class PeerConnectionEvents implements PeerConnectionClient.PeerConnectionEvents {

        private CallPeerConnection connection;

        PeerConnectionEvents(CallPeerConnection connection) {
            this.connection = connection;
        }

        @Override
        public void onLocalDescription(SessionDescription sdp) {
            Log.v(TAG, "Got a new local description");
            runOnUiThread(() ->
                    mSignalExchangerClient.sendSessionDescription(
                            connection.getMember().getUserCallID(), sdp));
        }

        @Override
        public void onIceCandidate(IceCandidate candidate) {
            Log.v(TAG, "Got a new ICE candidate");
            runOnUiThread(() -> mSignalExchangerClient.sendIceCandidate(
                    connection.getMember().getUserCallID(),
                    candidate));
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] candidates) {
            Log.v(TAG, "Some ice candidates removed with peer  " +
                    connection.getMember().getUserID());
        }

        @Override
        public void onIceConnected() {
            Log.v(TAG, "Ice connected with peer  " +
                    connection.getMember().getUserID());
            connection.setConnected(true);
        }

        @Override
        public void onIceDisconnected() {
            Log.v(TAG, "Ice disconnected from peer  " +
                    connection.getMember().getUserID());
            connection.setConnected(false);
        }

        @Override
        public void onConnected() {
            Log.v(TAG, "Connected to peer  " +
                    connection.getMember().getUserID());
            connection.setConnected(true);
        }

        @Override
        public void onDisconnected() {
            Log.v(TAG, "Disconnected from peer  " +
                    connection.getMember().getUserID());
            connection.setConnected(false);
        }

        @Override
        public void onPeerConnectionClosed() {
            Log.v(TAG, "Connection close from user " +
                    connection.getMember().getUserID());
            runOnUiThread(() -> disconnectFromPeer(connection.getMember()));
            connection.setConnected(false);
        }

        @Override
        public void onPeerConnectionStatsReady(StatsReport[] reports) {
            Log.v(TAG, "Stats ready for peer connection with " +
                    connection.getMember().getUserID());
        }

        @Override
        public void onPeerConnectionError(String description) {
            Log.e(TAG, "Peer connection error with " +
                    connection.getMember().getUserID() + " " + description);
            connection.setConnected(false);
        }
    }

    /**
     * Refresh call information thread
     */
    private class RefreshCallInformation extends Thread {

        private final Object o = new Object();
        private boolean stop = false;

        @Override
        public void run() {
            super.run();

            synchronized (o){

                while(!stop) {

                    runOnUiThread(CallActivity.this::getCallInformation);


                    try {
                        o.wait((long) (1.5 * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

        public void interrupt(){
            stop = true;
        }
    }

    /**
     * I don't know why, but this is an absolute requirement ! (to show videos)
     */
    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.");
                return;
            }

            target.onFrame(frame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }
}
