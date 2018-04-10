package org.communiquons.android.comunic.client.data.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.helpers.AccountHelper;
import org.communiquons.android.comunic.client.data.models.NotificationsCount;
import org.communiquons.android.comunic.client.data.helpers.NotificationsHelper;
import org.communiquons.android.comunic.client.data.utils.PreferencesUtils;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;

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
     * Keep run status
     */
    private boolean run;

    /**
     * Notifications helper
     */
    private NotificationsHelper mNotifHelper;

    /**
     * Notification channel ID
     */
    private final String CHANNEL_ID = "MainNotifChannel";

    /**
     * Main notification ID
     */
    private final static int MAIN_NOTIFICATION_ID = 0;

    /**
     * Public constructor
     */
    public NotificationsService(){
        super("NotificationsService");
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
                Thread.sleep(30000);
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
            NotificationsCount count = mNotifHelper.pullCount();

            //Check for error
            if(count == null){
                Log.e(TAG, "Could not pull the new number of notifications !");
                removeNotification();
                continue;
            }

            if(count.getNotificationsCount() > 0 || count.getConversationsCount() > 0){

                //Show notification
                showNotification(count);
            }

            else {

                //Make sure the notification has been deleted
                removeNotification();

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

        Notification.Builder mBuilder;

        //Check which version of the notification system to use
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //Create notification channel
            CharSequence name = "MainNotificationChannel";
            String description = "Activity notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            mChannel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);

            //Create notification builder
            mBuilder = new Notification.Builder(this, CHANNEL_ID);
        }
        else {
            //Create notification without channel
            mBuilder = new Notification.Builder(this);
            mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        }

        //Set notification settings
        mBuilder.setSmallIcon(R.drawable.ic_app_rounded);
        mBuilder.setContentTitle(getString(R.string.notification_notif_available_title));
        mBuilder.setContentText(getString(R.string.notification_notif_available_content,
                count.getNotificationsCount(), count.getConversationsCount()));

        //Create and apply an intent
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
        mBuilder.setContentIntent(pendingIntent);

        //Get notification manager to push notification
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(
                MAIN_NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * Remove the notification
     */
    private void removeNotification(){
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(MAIN_NOTIFICATION_ID);
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
