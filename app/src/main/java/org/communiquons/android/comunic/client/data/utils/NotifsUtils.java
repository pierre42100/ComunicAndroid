package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.enums.NotifElemType;
import org.communiquons.android.comunic.client.data.enums.NotificationTypes;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.data.models.Notif;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

import java.security.acl.Group;

/**
 * Notifications utilities
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/10/18.
 */

public class NotifsUtils {

    /**
     * Generate and return the message associated to a notification
     *
     * @param context The context of the application
     * @param notif The target notification
     * @param userInfos Information about the user of the notification
     * @param groupsInfo Information about related groups
     * @return The message associated to the notification
     */
    public static String getNotificationMessage(Context context, Notif notif,
                                                ArrayMap<Integer, UserInfo> userInfos,
                                                ArrayMap<Integer, GroupInfo> groupsInfo){

        //First, put the name of the user
        String message = userInfos.get(notif.getFrom_user_id()).getDisplayFullName();
        message += " ";

        //For friendship request
        if(notif.getType() == NotificationTypes.SENT_FRIEND_REQUEST)
            message += UiUtils.getString(context, R.string.notif_sent_friend_request);
        if(notif.getType() == NotificationTypes.ACCEPTED_FRIEND_REQUEST)
            message += UiUtils.getString(context, R.string.notif_accepted_friend_request);
        if(notif.getType() == NotificationTypes.REJECTED_FRIEND_REQUEST)
            message += UiUtils.getString(context, R.string.notif_rejected_friend_request);

        //For group membership
        if(notif.getType() == NotificationTypes.SENT_GROUP_MEMBERSHIP_INVITATION)
            message += UiUtils.getString(context, R.string.notif_sent_group_membership_invitation);
        if(notif.getType() == NotificationTypes.ACCEPTED_GROUP_MEMBERSHIP_INVITATION)
            message += UiUtils.getString(context, R.string.notif_accepted_group_membership_invitation);
        if(notif.getType() == NotificationTypes.REJECTED_GROUP_MEMBERSHIP_INVITATION)
            message += UiUtils.getString(context, R.string.notif_rejected_group_membership_invitation);
        if(notif.getType() == NotificationTypes.SENT_GROUP_MEMBERSHIP_REQUEST)
            message += UiUtils.getString(context, R.string.notif_sent_group_membership_request);
        if(notif.getType() == NotificationTypes.ACCEPTED_GROUP_MEMBERSHIP_REQUEST)
            message += UiUtils.getString(context, R.string.notif_accepted_group_membership_request);
        if(notif.getType() == NotificationTypes.REJECTED_GROUP_MEMBERSHIP_REQUEST)
            message += UiUtils.getString(context, R.string.notif_rejected_group_membership_request);

        //In case of creation of an element
        if(notif.getType() == NotificationTypes.ELEM_CREATED){

            //For the posts
            if(notif.getOn_elem_type() == NotifElemType.POST)
                message += UiUtils.getString(context, R.string.notif_created_post);

        }

        //For comments creation
        if(notif.getType() == NotificationTypes.COMMENT_CREATED){
            message += UiUtils.getString(context, R.string.notif_posted_comment);
        }

        //Add a separator
        message += " ";


        //Notification target
        //User page
        if(notif.getFrom_container_type() == NotifElemType.USER_PAGE){

            if(notif.getFrom_user_id() == notif.getFrom_container_id())
                message += UiUtils.getString(context, R.string.notif_on_creator_page);
            else
                message += UiUtils.getString(context, R.string.notif_on_user_page,
                        userInfos.get(notif.getFrom_container_id()).getDisplayFullName());

        }

        //Group page
        else if(notif.getFrom_container_type() == NotifElemType.GROUP_PAGE){
            message += UiUtils.getString(context, R.string.notif_on_group_page,
                    groupsInfo.get(notif.getFrom_container_id()).getName());
        }

        //Group membership
        else if(notif.getOn_elem_type() == NotifElemType.GROUPS_MEMBERSHIP){
            message += groupsInfo.get(notif.getOn_elem_id()).getName();
        }

        //Return the message
        return message;

    }
}
