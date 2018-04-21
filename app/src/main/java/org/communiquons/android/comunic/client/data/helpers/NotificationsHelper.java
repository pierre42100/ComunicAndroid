package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.Notif;
import org.communiquons.android.comunic.client.data.enums.NotifElemType;
import org.communiquons.android.comunic.client.data.enums.NotificationTypes;
import org.communiquons.android.comunic.client.data.enums.NotificationVisibility;
import org.communiquons.android.comunic.client.data.models.NotificationsCount;
import org.communiquons.android.comunic.client.data.arrays.NotifsList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Notifications helper
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/9/18.
 */

public class NotificationsHelper {

    /**
     * Application context
     */
    private Context mContext;

    /**
     * Notifications helper constructor
     *
     * @param context The context of the application
     */
    public NotificationsHelper(Context context){

        //Save context
        this.mContext = context.getApplicationContext();

    }

    /**
     * Get the notifications count
     *
     * @return Notifications count / NULL in case of failure
     */
    @Nullable
    public NotificationsCount pullCount(){

        //Perform an API request
        APIRequest params = new APIRequest(mContext,
                "notifications/count_all_news");

        //Try to perform the request and parse results
        try {

            APIResponse response = new APIRequestHelper().exec(params);

            //Try to parse results
            JSONObject object = response.getJSONObject();
            NotificationsCount res = new NotificationsCount();
            res.setNotificationsCount(object.getInt("notifications"));
            res.setConversationsCount(object.getInt("conversations"));
            return res;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mark a notification as seen
     *
     * @param notifID The ID of the target notification
     * @return TRUE in case of success / FALSE else
     */
    public boolean markSeen(int notifID){

        //Perform a request on the server
        APIRequest params = new APIRequest(mContext, "notifications/mark_seen");
        params.addInt("notifID", notifID);

        //Try to send the request to the server
        try {
            APIResponse response = new APIRequestHelper().exec(params);
            return response.getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Intend to delete the entire list of notifications
     *
     * @return TRUE in case of success / FALSE else
     */
    public boolean deleteAllNotifs(){

        //Perform a request on the server
        APIRequest params = new APIRequest(mContext,
                "notifications/delete_all");

        //Try to perform the request on the server
        try {
            APIResponse response = new APIRequestHelper().exec(params);
            return response.getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the list of unread notifications
     *
     * @return TRUE in case of success / FALSE else
     */
    @Nullable
    public NotifsList getListUnread(){

        //Perform a request on the server
        APIRequest params = new APIRequest(mContext,
                "notifications/get_list_unread");

        //Try to perform the request on the server
        try {

            //Try to perform the request on the server
            APIResponse response = new APIRequestHelper().exec(params);

            //Check for errors
            if(response.getResponse_code() != 200)
                return null;

            //Parse the results
            JSONArray array = response.getJSONArray();
            NotifsList list = new NotifsList();
            for (int i = 0; i < array.length(); i++){
                list.add(parseNotifJSONObject(array.getJSONObject(i)));
            }

            //Return the list of notifications
            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse a JSON object into a Notif object
     *
     * @param object The JSON object
     * @return The generated notif (notification) object
     * @throws JSONException If an error occurred while trying to parse the information
     */
    private Notif parseNotifJSONObject(JSONObject object) throws JSONException {

        Notif notif = new Notif();

        //Parse object information
        notif.setId(object.getInt("id"));
        notif.setTime_create(object.getInt("time_create"));
        notif.setSeen(object.getBoolean("seen"));
        notif.setFrom_user_id(object.getInt("from_user_id"));
        notif.setDest_user_id(object.getInt("dest_user_id"));
        notif.setOn_elem_id(object.getInt("on_elem_id"));
        notif.setOn_elem_type(getNotifElemTypeFromString(object.getString("on_elem_type")));
        notif.setType(getNotifTypeFromString(object.getString("type")));
        notif.setVisibility(getNotifVisiblityFromString(object.getString("event_visibility")));
        notif.setFrom_container_id(object.getInt("from_container_id"));
        notif.setFrom_container_type(getNotifElemTypeFromString(object.getString("from_container_type")));

        return notif;

    }

    /**
     * Determine notification element type from a string
     *
     * @param string The string to parse
     * @return Matching element type
     */
    private NotifElemType getNotifElemTypeFromString(String string){

        switch (string){

            case "user_page":
                return NotifElemType.USER_PAGE;

            case "conversation":
                return NotifElemType.CONVERSATION;

            case "conversation_message":
                return NotifElemType.CONVERSATION_MESSAGE;

            case "post":
                return NotifElemType.POST;

            case "comment":
                return NotifElemType.COMMENT;

            case "friend_request":
                return NotifElemType.FRIEND_REQUEST;

            //Default : unknown type of elem
            default:
                return NotifElemType.UNKNOWN;
        }

    }

    /**
     * Turn a string into a notifications types value
     *
     * @param string The input string
     * @return Matching notification type
     */
    private NotificationTypes getNotifTypeFromString(String string){

        switch (string){

            case "comment_created":
                return NotificationTypes.COMMENT_CREATED;

            case "sent_friend_request":
                return NotificationTypes.SENT_FRIEND_REQUEST;

            case "accepted_friend_request":
                return NotificationTypes.ACCEPTED_FRIEND_REQUEST;

            case "rejected_friend_request":
                return NotificationTypes.REJECTED_FRIEND_REQUEST;

            case "elem_created":
                return NotificationTypes.ELEM_CREATED;

            case "elem_updated":
                return NotificationTypes.ELEM_UPDATED;

            //Default : Unknown notification type
            default:
                return NotificationTypes.UNKNOWN;
        }

    }

    /**
     * Turn a string into a notification visibility value
     *
     * @param string The input string
     * @return Matching notification visibility
     */
    private NotificationVisibility getNotifVisiblityFromString(String string){

        switch (string){

            case "event_public":
                return NotificationVisibility.EVENT_PUBLIC;

            case "event_private":
                return NotificationVisibility.EVENT_PRIVATE;

            //In case the visibility was not found
            default:
                return NotificationVisibility.UNKNOWN;
        }

    }
}
