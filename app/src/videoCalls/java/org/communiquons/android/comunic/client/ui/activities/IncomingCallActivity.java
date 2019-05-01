package org.communiquons.android.comunic.client.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.NextPendingCallInformation;
import org.communiquons.android.comunic.client.data.utils.AccountUtils;
import org.communiquons.android.comunic.client.ui.Constants;
import org.communiquons.android.comunic.client.ui.asynctasks.GetNextPendingCallTask;
import org.communiquons.android.comunic.client.ui.asynctasks.SafeAsyncTask;
import org.communiquons.android.comunic.client.ui.receivers.PendingCallsBroadcastReceiver;
import org.communiquons.android.comunic.client.ui.receivers.RejectCallReceiver;


/**
 * Incoming call activity
 *
 * @author Pierre HUBERT
 */
public class IncomingCallActivity extends BaseActivity implements SafeAsyncTask.OnPostExecuteListener<NextPendingCallInformation>, View.OnClickListener {

    private NextPendingCallInformation mNextPendingCallInformation;

    private RefreshThread mRefreshThread = null;

    private Button mAcceptButton;
    private Button mRejectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        getSupportActionBar().hide();

        PendingCallsBroadcastReceiver.RemoveCallNotification(this);

        mAcceptButton = findViewById(R.id.accept_button);
        mRejectButton = findViewById(R.id.reject_button);

        if(IsActiveActivity(IncomingCallActivity.class))
            finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRefreshThread = new RefreshThread();
        mRefreshThread.start();

    }

    @Override
    protected void onStop() {
        super.onStop();

        mRefreshThread.interrupt();
    }

    private void getNextPendingCall() {
        //Get next pending call
        GetNextPendingCallTask getNextPendingCallTask = new GetNextPendingCallTask(getApplicationContext());
        getNextPendingCallTask.setOnPostExecuteListener(this);
        getNextPendingCallTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getTasksManager().addTask(getNextPendingCallTask);
    }

    @Override
    public void OnPostExecute(@Nullable NextPendingCallInformation nextPendingCallInformation) {

        if(nextPendingCallInformation == null){
            Toast.makeText(this, R.string.err_get_next_pending_call_info, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!nextPendingCallInformation.isHasPendingCall()
                || nextPendingCallInformation.hasAllMembersLeftCallExcept(AccountUtils.getID(this)))
            finish();

        this.mNextPendingCallInformation = nextPendingCallInformation;

        ((TextView)findViewById(R.id.call_title)).setText(nextPendingCallInformation.getCallName());
        mAcceptButton.setOnClickListener(this);
        mRejectButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        //Accept call
        if(v.equals(mAcceptButton)){
            Intent intent = new Intent(this, CallActivity.class);
            intent.putExtra(CallActivity.ARGUMENT_CALL_ID, mNextPendingCallInformation.getId());
            startActivity(intent);
            finish();
        }


        //Reject calls
        if(v.equals(mRejectButton)){
            Intent intent = new Intent(this, RejectCallReceiver.class);
            intent.setAction(Constants.IntentActions.ACTION_REJECT_INCOMING_CALL);
            intent.putExtra(RejectCallReceiver.ARGUMENT_CALL_ID, mNextPendingCallInformation.getId());
            sendBroadcast(intent);
            finish();
        }

    }

    /**
     * Class used to refresh call information
     */
    private class RefreshThread extends Thread {

        private boolean stop = false;
        private final Object o = new Object();

        @Override
        public void run() {
            super.run();

            while(!stop){

                //Execute task
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNextPendingCall();
                    }
                });

                synchronized (o) {

                    //Looping
                    try {
                        o.wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        interrupt();
                    }

                }
            }

        }

        public void interrupt(){
            stop = true;
        }
    }
}
