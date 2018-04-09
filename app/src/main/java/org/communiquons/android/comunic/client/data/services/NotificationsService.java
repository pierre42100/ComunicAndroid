package org.communiquons.android.comunic.client.data.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

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

        Log.v(TAG, "Start service");

        while(run){

            try {
                //Make a pause
                Thread.sleep(2000);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }

            Log.v(TAG, "Hey there, service !");

        }

        Log.v(TAG, "Stop service");

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
