package org.communiquons.android.comunic.client.data.notifications;

import android.content.Context;
import android.support.annotation.Nullable;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;
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
        APIRequestParameters params = new APIRequestParameters(mContext,
                "notifications/count_all_news");

        //Try to perform the request and parse results
        try {

            APIResponse response = new APIRequest().exec(params);

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
     * Intend to delete the entire list of notifications
     *
     * @return TRUE in case of success / FALSE else
     */
    public boolean deleteAllNotifs(){

        //Perform a request on the server
        APIRequestParameters params = new APIRequestParameters(mContext,
                "notifications/delete_all");

        //Try to perform the request on the server
        try {
            APIResponse response = new APIRequest().exec(params);
            return response.getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
