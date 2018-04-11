package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.enums.NotifElemType;
import org.communiquons.android.comunic.client.data.enums.NotificationTypes;
import org.communiquons.android.comunic.client.data.models.Notif;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

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
     * @return The message associated to the notification
     */
    public static String getNotificationMessage(Context context, Notif notif,
                                                ArrayMap<Integer, UserInfo> userInfos){

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
        if(notif.getFrom_container_type() == NotifElemType.USER_PAGE){

            if(notif.getFrom_user_id() == notif.getFrom_container_id())
                message += UiUtils.getString(context, R.string.notif_on_creator_page);
            else
                message += UiUtils.getString(context, R.string.notif_on_user_page,
                        userInfos.get(notif.getFrom_container_id()).getDisplayFullName());

        }

        //Return the message
        return message;

    }
}
