package org.communiquons.android.comunic.client.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.receivers.PendingCallsBroadcastReceiver;

import java.util.Objects;

/**
 * Call activity
 *
 * @author Pierre HUBERT
 */
public class CallActivity extends BaseActivity {

    /**
     * Mandatory argument that includes call id
     */
    public static final String ARGUMENT_CALL_ID = "call_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        //Hide call bar
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Hide call notifications
        PendingCallsBroadcastReceiver.RemoveCallNotification(this);

        ((TextView)findViewById(R.id.call_id)).setText(
                "Call " + getIntent().getExtras().getInt(ARGUMENT_CALL_ID));
    }
}
