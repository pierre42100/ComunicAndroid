package org.communiquons.android.comunic.client.ui.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.NextPendingCallInformation;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.ui.Constants;
import org.communiquons.android.comunic.client.ui.activities.BaseActivity;
import org.communiquons.android.comunic.client.ui.activities.CallActivity;
import org.communiquons.android.comunic.client.ui.activities.IncomingCallActivity;
import org.communiquons.android.comunic.client.ui.asynctasks.GetNextPendingCallTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

import java.util.Objects;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;
import static org.communiquons.android.comunic.client.ui.Constants.Notifications.CALL_NOTIFICATION_ID;
import static org.communiquons.android.comunic.client.ui.Constants.NotificationsChannels.CALL_CHANNEL_DESCRIPTION;
import static org.communiquons.android.comunic.client.ui.Constants.NotificationsChannels.CALL_CHANNEL_ID;

/**
 * Receiver for pending calls
 *
 * This receiver get calls when new calls are reported to be available for the user
 *
 * @author Pierre HUBERT
 */
public class PendingCallsBroadcastReceiver extends BroadcastReceiver {

    /**
     * Debug tag
     */
    private static final String TAG = PendingCallsBroadcastReceiver.class.getSimpleName();

    /**
     * This variable is set to true if another call is being processed
     */
    private boolean locked = false;

    /**
     * The ID of the last shown notification
     */
    private static int mLastNotificationCallID = 0;


    @Override
    public void onReceive(final Context context, Intent intent) {

        //Check if the intent is valid
        if(intent == null
                || !Objects.equals(intent.getAction(),
                Constants.IntentActions.ACTION_NOTIFY_NEW_CALLS_AVAILABLE))
            throw new RuntimeException("Unexpected call of " + TAG);


        //Check if user is already calling
        if(CheckIfUseless(context, null)) {
            Log.v(TAG, "Reported that a new call is available but skipped because considered as useless.");
            return;
        }

        //Check if service is currently locked
        if(locked) {
            Log.e(TAG, "New call skipped because this class is locked");
            return;
        }
        locked = true;

        //Get next pending notification
        GetNextPendingCallTask task = new GetNextPendingCallTask(context);
        task.setOnPostExecuteListener(new SafeAsyncTask.OnPostExecuteListener<NextPendingCallInformation>() {
            @Override
            public void OnPostExecute(NextPendingCallInformation nextPendingCallInformation) {
                locked = false;
                onGotCall(context, nextPendingCallInformation);
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Method called when we have got new pending call information
     *
     * @param context Application context
     * @param info Information about the call
     */
    private void onGotCall(Context context, @Nullable NextPendingCallInformation info){

        if(info == null){
            Log.e(TAG, "Could not get information about next pending call!");
            return;
        }


        //Check if it is useless to continue
        if(CheckIfUseless(context, info)) {
            return;
        }


        //Create notification

        //Full screen call request intent
        Intent fullScreenCallRequestIntent = new Intent(context, IncomingCallActivity.class);
        PendingIntent pendingFullScreenRequestIntent = PendingIntent.getActivity(context,
                0, fullScreenCallRequestIntent, 0);


        //Create and show notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CALL_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_call)
                        .setContentTitle(info.getCallName())
                        .setContentText(UiUtils.getString(context, R.string.notification_call_content, info.getCallName()))
                        //.setContentIntent(pendingAcceptIntent)
                        .setFullScreenIntent(pendingFullScreenRequestIntent, true)
                        .setPriority(PRIORITY_HIGH);


        //Create notification channel if required
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CALL_CHANNEL_ID,
                    CALL_CHANNEL_DESCRIPTION, IMPORTANCE_HIGH);
            channel.setDescription(CALL_CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification n = builder.build();
        n.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        mLastNotificationCallID = info.getId();
        NotificationManagerCompat.from(context).notify(GetNotificationID(mLastNotificationCallID), n);
    }

    /**
     * Check if calling this receiver is useless for now
     *
     * @param context Context of application
     * @param call Optional information about a pending call
     * @return TRUE if useless / FALSE else
     */
    public static boolean CheckIfUseless(Context context, @Nullable NextPendingCallInformation call){
        boolean useless = BaseActivity.IsActiveActivity(IncomingCallActivity.class)
                || BaseActivity.IsActiveActivity(CallActivity.class)
                || (call != null && (
                        !call.isHasPendingCall()
                        || call.hasAllMembersLeftCallExcept(AccountUtils.getID(context))));

        if(useless)
            RemoveCallNotification(context);

        return useless;
    }

    /**
     * Remove any visible call notification
     *
     * @param context The context of the application
     */
    public static void RemoveCallNotification(Context context){
        NotificationManagerCompat.from(context).cancel(GetNotificationID(mLastNotificationCallID));
    }

    /**
     * Get the ID of a notification for a call
     *
     * @param callID The ID of the target call
     */
    private static int GetNotificationID(int callID){
        return CALL_NOTIFICATION_ID*1000 + callID;
    }
}
