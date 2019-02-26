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
    private VideoSink localProxyVideoSink;
    private VideoSink remoteProxyRenderer;
    private ArrayList<VideoSink> remoteSinks = new ArrayList<>();

    //Views
    private SurfaceViewRenderer mLocalVideoView;
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

    public SurfaceViewRenderer getLocalVideoView() {

        return mLocalVideoView;
    }

    public void setLocalVideoView(SurfaceViewRenderer mLocalVideoView) {
        this.mLocalVideoView = mLocalVideoView;
    }

    public VideoSink getLocalProxyVideoSink() {
        return localProxyVideoSink;
    }

    public void setLocalProxyVideoSink(VideoSink localProxyVideoSink) {
        this.localProxyVideoSink = localProxyVideoSink;
    }

    public VideoSink getRemoteProxyRenderer() {
        return remoteProxyRenderer;
    }

    public void setRemoteProxyRenderer(VideoSink remoteProxyRenderer) {
        this.remoteProxyRenderer = remoteProxyRenderer;
    }
}
