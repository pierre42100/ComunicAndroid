package org.communiquons.android.comunic.client.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.asynctasks.CreateCallForConversationTask;
import org.communiquons.android.comunic.client.ui.asynctasks.GetCallConfigurationTask;
import org.communiquons.android.comunic.client.ui.listeners.OnOpenCallListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;


/**
 * MainActivity implementation for video calls
 *
 * @author Pierre HUBERT
 */
public class MainActivity extends AbstractMainActivity implements OnOpenCallListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get calls configuration
        GetCallConfigurationTask callConfigurationTask = new GetCallConfigurationTask(this);
        callConfigurationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getTasksManager().addTask(callConfigurationTask);
    }

    @Override
    public void createCallForConversation(int convID) {
        final Dialog dialog = UiUtils.create_loading_dialog(this);

        //Create the call for the conversation
        CreateCallForConversationTask task = new CreateCallForConversationTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, convID);
        getTasksManager().addTask(task, true);
        task.setOnPostExecuteListener(callInformation -> {

            dialog.dismiss();

            //Check for errors
            if(callInformation == null)
                Toast.makeText(
                        MainActivity.this,
                        R.string.err_create_call_for_conversation,
                        Toast.LENGTH_SHORT).show();

            else
                //Open call
                openCall(callInformation.getId());
        });
    }

    @Override
    public void openCall(int callID) {

        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra(CallActivity.ARGUMENT_CALL_ID, callID);
        startActivity(intent);

    }
}