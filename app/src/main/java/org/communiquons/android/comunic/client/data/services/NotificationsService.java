package org.communiquons.android.comunic.client.data.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.AccountHelper;
import org.communiquons.android.comunic.client.data.helpers.CallsHelper;
import org.communiquons.android.comunic.client.data.helpers.NotificationsHelper;
import org.communiquons.android.comunic.client.data.models.NotificationsCount;
import org.communiquons.android.comunic.client.data.utils.PreferencesUtils;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.receivers.PendingCallsBroadcastReceiver;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static org.communiquons.android.comunic.client.ui.Constants.IntentActions.ACTION_NOTIFY_NEW_CALLS_AVAILABLE;
import static org.communiquons.android.comunic.client.ui.Constants.Notifications.MAIN_NOTIFICATION_ID;
import static org.communiquons.android.comunic.client.ui.Constants.NotificationsChannels.GLOBAL_CHANNEL_DESCRIPTION;
import static org.communiquons.android.comunic.client.ui.Constants.NotificationsChannels.GLOBAL_CHANNEL_ID;
import static org.communiquons.android.comunic.client.ui.Constants.NotificationsChannels.GLOBAL_CHANNEL_NAME;
import static org.communiquons.android.comunic.client.ui.Constants.PreferencesKeys.PREFERENCE_ACCELERATE_NOTIFICATIONS_REFRESH;

/**
 * Notifications service
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/9/18.
 */

public class NotificationsService extends IntentService {

    /**
     * Debug tag
     */
    private static final String TAG = "NotificationsService";

    /**
     * Broadcast action
     */
    public static final String BROADCAST_ACTION =
            "org.communiquons.android.comunic.NotificationService.BROADCAST";

    /**
     * Notification extras
     */
    public static final String BROADCAST_EXTRA_NUMBER_NOTIFICATIONS = "NumberNotifications";
    public static final String BROADCAST_EXTRA_UNREAD_CONVERSATIONS = "UnreadConversations";
    public static final String BROADCAST_EXTRA_NUMBER_FRIENDSHIP_REQUESTS = "NumberFriendsRequests";

    /**
     * Keep run status
     */
    private boolean run;

    /**
     * Notifications helper
     */
    private NotificationsHelper mNotifHelper;


    /**
     * Last notification count
     */
    private static NotificationsCount mLastCount;


    /**
     * Public constructor
     */
    public NotificationsService(){
        super("NotificationsService");

        //Register calls broadcast register
        IntentFilter callFilters = new IntentFilter();
        callFilters.addAction(ACTION_NOTIFY_NEW_CALLS_AVAILABLE);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(new PendingCallsBroadcastReceiver(), callFilters);

    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Create notifications helper
        mNotifHelper = new NotificationsHelper(getApplicationContext());

        Log.v(TAG, "Start service");

        while(run){

            try {
                //Make a pause
                int secs = PreferencesUtils.getBoolean(
                        this, PREFERENCE_ACCELERATE_NOTIFICATIONS_REFRESH, false) ?
                        2 /* high frequency */ : 30 /* low frequency */;
                Thread.sleep(secs*1000);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }

            //Check if the user is signed in or not
            if(!new AccountHelper(this).signed_in()){
                Log.v(TAG, "Skip notifications refresh because the user is not signed in.");
                removeNotification();
                continue;
            }

            //Check if notification pull is disabled
            if(!PreferencesUtils.getBoolean(this, "enable_background_notification_refresh", true)) {
                Log.v(TAG, "Skip notifications refresh because the user disabled the option.");
                removeNotification();
                continue;
            }

            //Pull the number of notifications
            NotificationsCount count = mNotifHelper.pullCount(CallsHelper.IsCallSystemAvailable());

            //Check for error
            if(count == null){
                Log.e(TAG, "Could not pull the new number of notifications !");
                removeNotification();
                continue;
            }

            if(count.getNotificationsCount() > 0 || count.getConversationsCount() > 0){

                //Check notification count
                if(mLastCount != null){

                    //Check if it is required to push a new notification
                    if(mLastCount.getNotificationsCount() != count.getNotificationsCount()
                            || mLastCount.getConversationsCount() != count.getConversationsCount())

                        showNotification(count);
                }
                else
                    //Show notification
                    showNotification(count);
            }

            else {

                //Make sure the notification has been deleted
                removeNotification();

            }

            //Save last notifications count
            mLastCount = count;

            //Create an intent and push nut data
            Intent pushIntent = new Intent(BROADCAST_ACTION)
                    .putExtra(BROADCAST_EXTRA_NUMBER_NOTIFICATIONS, count.getNotificationsCount())
                    .putExtra(BROADCAST_EXTRA_UNREAD_CONVERSATIONS, count.getConversationsCount())
                    .putExtra(BROADCAST_EXTRA_NUMBER_FRIENDSHIP_REQUESTS, count.getFriendsRequestsCount());

            //Send new calls information
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushIntent);

            //If new calls are available, notify system
            if(CallsHelper.IsCallSystemAvailable()){

                if(count.hasPendingCalls()) {
                    Intent callIntent = new Intent(this, PendingCallsBroadcastReceiver.class);
                    callIntent.setAction(ACTION_NOTIFY_NEW_CALLS_AVAILABLE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(callIntent);
                }
                else
                    PendingCallsBroadcastReceiver.RemoveCallNotification(this);

            }

        }

        Log.v(TAG, "Stop service");

    }

    /**
     * Create and display the notification accordingly to the given count information
     *
     * @param count The number of new notifications
     */
    private void showNotification(NotificationsCount count){

        createGlobalNotificationChannel();

        //Create pending intent
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        //Build the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, GLOBAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_rounded)
            .setContentTitle(getString(R.string.notification_notif_available_title))
            .setContentText(getString(R.string.notification_notif_available_content,
                count.getNotificationsCount(), count.getConversationsCount()))
            .setContentIntent(pendingIntent);

        //Get notification manager to push notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(MAIN_NOTIFICATION_ID, mBuilder.build());

    }

    /**
     * Remove the notification
     */
    private void removeNotification(){
        NotificationManagerCompat.from(this).cancel(MAIN_NOTIFICATION_ID);
    }

    /**
     * Create global notification channel
     */
    private void createGlobalNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    GLOBAL_CHANNEL_ID,
                    GLOBAL_CHANNEL_NAME,
                    IMPORTANCE_DEFAULT
            );
            channel.setDescription(GLOBAL_CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.run = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.run = false;
    }
}
