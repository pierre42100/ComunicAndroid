package org.communiquons.android.comunic.client.data.notifications;

import android.support.annotation.Nullable;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.UsersInfo.UserInfo;

import java.util.ArrayList;

/**
 * Notifications list, combined with user information
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/10/18.
 */

public class NotifsList extends ArrayList<Notif> {

    /**
     * Information about the users of the notifications
     */
    private ArrayMap<Integer, UserInfo> mUsersInfo;

    /**
     * Get and return the IDs of the users related to the notifications
     *
     * @return The list of users
     */
    public ArrayList<Integer> getUsersID(){

        ArrayList<Integer> IDs = new ArrayList<>();

        //Process each notification
        for(Notif notif : this){

            if(!IDs.contains(notif.getFrom_user_id()))
                IDs.add(notif.getFrom_user_id());

            if(!IDs.contains(notif.getDest_user_id()))
                IDs.add(notif.getDest_user_id());

            if(notif.getOn_elem_type() == NotifElemType.FRIEND_REQUEST ||
                    notif.getOn_elem_type() == NotifElemType.USER_PAGE){

                if(!IDs.contains(notif.getOn_elem_id()))
                    IDs.add(notif.getOn_elem_id());

            }

            if(notif.getFrom_container_type() == NotifElemType.FRIEND_REQUEST ||
                    notif.getFrom_container_type() == NotifElemType.USER_PAGE){

                if(!IDs.contains(notif.getFrom_container_id()))
                    IDs.add(notif.getFrom_container_id());

            }

        }

        return IDs;
    }

    /**
     * Set information about the users related to the notifications
     *
     * @param mUsersInfo Information about the users
     */
    public void setUsersInfo(ArrayMap<Integer, UserInfo> mUsersInfo) {
        this.mUsersInfo = mUsersInfo;
    }

    /**
     * Get the information about the users related to the notifications
     *
     * @return Information about the users related to the notifications
     */
    @Nullable
    public ArrayMap<Integer, UserInfo> getUsersInfo() {
        return mUsersInfo;
    }
}
