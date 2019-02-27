package org.communiquons.android.comunic.client.ui.models;

import org.appspot.apprtc.PeerConnectionClient;
import org.communiquons.android.comunic.client.data.models.CallMember;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSink;

import java.util.ArrayList;

/**
 * Single remote connection information
 *
 * @author Pierre HUBERT
 */
public class CallPeerConnection {

    //Private fields
    private CallMember member;
    private PeerConnectionClient peerConnectionClient;
    private boolean connected = false;
    private VideoSink remoteProxyRenderer;
    private ArrayList<VideoSink> remoteSinks = new ArrayList<>();

    //Views
    private SurfaceViewRenderer mRemoteViewView;

    public CallPeerConnection(CallMember member) {
        this.member = member;
    }

    public CallMember getMember() {
        return member;
    }

    public void setMember(CallMember member) {
        this.member = member;
    }

    public PeerConnectionClient getPeerConnectionClient() {
        return peerConnectionClient;
    }

    public void setPeerConnectionClient(PeerConnectionClient peerConnectionClient) {
        this.peerConnectionClient = peerConnectionClient;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public ArrayList<VideoSink> getRemoteSinks() {
        return remoteSinks;
    }

    public void setRemoteSinks(ArrayList<VideoSink> remoteSinks) {
        this.remoteSinks = remoteSinks;
    }

    public SurfaceViewRenderer getRemoteViewView() {
        return mRemoteViewView;
    }

    public void setRemoteViewView(SurfaceViewRenderer mRemoteViewView) {
        this.mRemoteViewView = mRemoteViewView;
    }

    public VideoSink getRemoteProxyRenderer() {
        return remoteProxyRenderer;
    }

    public void setRemoteProxyRenderer(VideoSink remoteProxyRenderer) {
        this.remoteProxyRenderer = remoteProxyRenderer;
    }
}
