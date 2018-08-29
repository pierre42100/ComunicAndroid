package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.utils.StringsUtils;
import org.communiquons.android.comunic.client.data.utils.Utilities;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

/**
 * CountDown view
 *
 * @author Pierre HUBERT
 */
public class CountDownView extends BaseFrameLayoutView {

    private static final String TAG = CountDownView.class.getCanonicalName();

    private TextView mTimerView;

    private int time_end;

    private CountDownTimer mTimer;

    public CountDownView(@NonNull Context context) {
        this(context, null);
    }

    public CountDownView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = inflate(getContext(), R.layout.view_countdown, this);

        mTimerView = view.findViewById(R.id.timer);
    }


    public int getTime_end() {
        return time_end;
    }

    public void setTime_end(final int time_end) {
        this.time_end = time_end;

        final int remaining = time_end - Utilities.time();

        if(mTimer != null)
            mTimer.cancel();

        if(remaining < 1){
            mTimerView.setText(timeToString(0));
            return;
        }


        mTimer = new CountDownTimer(remaining*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimerView.setText(timeToString(time_end - Utilities.time()));
            }

            @Override
            public void onFinish() {
                mTimerView.setText(timeToString(0));
            }

        }.start();
    }

    /**
     * Parse an amount of time into a string
     *
     * @param time The time to parse
     * @return Result of operation
     */
    private String timeToString(int time){

        int days = (int)Math.floor(time / 86400);
        int remaining_time = time % 86400;

        int hours = (int)Math.floor(remaining_time / 3600);
        remaining_time %= 3600;

        int minutes = (int)Math.floor(remaining_time / 60);
        int seconds = remaining_time % 60;

        Log.v(TAG, days + "d " + hours + ":" + minutes + ":" + seconds);
        return StringsUtils.EnsureZerosInNumberString(days, 2)
                + UiUtils.getString(getContext(), R.string.date_days_short) + " "
                + StringsUtils.EnsureZerosInNumberString(hours, 2) + ":"
                + StringsUtils.EnsureZerosInNumberString(minutes, 2) + ":"
                + StringsUtils.EnsureZerosInNumberString(seconds, 2);
    }
}
