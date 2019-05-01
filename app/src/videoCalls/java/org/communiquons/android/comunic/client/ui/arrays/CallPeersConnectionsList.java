package org.communiquons.android.comunic.client.ui.arrays;

import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.models.CallMember;
import org.communiquons.android.comunic.client.ui.models.CallPeerConnection;

import java.util.ArrayList;

/**
 * List of clients connections
 *
 * @author Pierre HUBERT
 */
public class CallPeersConnectionsList extends ArrayList<CallPeerConnection> {

    /**
     * Find the connection matching a specific call member
     *
     *
     * @param member Information about the target member
     * @return Full client connection
     */
    @Nullable
    public CallPeerConnection find(CallMember member){
        for(CallPeerConnection connection : this)
            if(connection.getMember().getUserID() == member.getUserID())
                return connection;

        return null;
    }


    /**
     * Try to find a peer connection using call ID
     *
     * @param id The ID of the user call ID
     * @return Information about the peer connection / null object in case of failure
     */
    @Nullable
    public CallPeerConnection findByCallID(String id){
        for(CallPeerConnection connection : this)
            if(connection.getMember().getUserCallID().equals(id))
                return connection;

        return null;
    }
}
