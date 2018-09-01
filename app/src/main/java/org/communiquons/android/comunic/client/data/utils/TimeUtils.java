package org.communiquons.android.comunic.client.data.utils;

import android.content.Context;
import android.content.res.Resources;

import org.communiquons.android.comunic.client.R;

import java.util.Date;

/**
 * Time utilities
 *
 * @author Pierre HUBERT
 */
public class TimeUtils {

    /**
     * Get current timestamp
     *
     * @return The current timestamp
     */
    public static int time(){
        Date date = new Date();
        return (int) Math.ceil(date.getTime()/1000);
    }

    /**
     * Transform an amount of seconds into a string like "3min" or "10hours"s
     *
     * @param time The Time to convert
     * @return Generated string
     */
    public static String TimeToString(Context context, int time){

        Resources res = context.getResources();

        //Check if the time is inferior to 1 => now
        if(time < 1)
            return res.getString(R.string.date_now);

        //Less than one minute
        else if (time < 60){
            return time + res.getString(R.string.date_s);
        }

        //Less than one hour
        else if (time < 3600){
            int secs = (int) Math.floor(time / 60);
            return secs + res.getString(R.string.date_m);
        }

        //Less than a day
        else if (time < 86400){
            int hours = (int) Math.floor(time / 3600);
            return hours + res.getString(R.string.date_h);
        }

        //Less than a month
        else if (time < 2678400){
            int days = (int) Math.floor(time / 86400);
            return days + res.getString(days > 1 ? R.string.date_days : R.string.date_day);
        }

        //Less than a year
        else if (time < 31536000){
            int months = (int) Math.floor(time / 2678400);
            return months + res.getString(months > 1 ? R.string.date_months : R.string.date_month);
        }

        //A several amount of years
        else {
            int years = (int) Math.floor(time / 31536000);
            return years + res.getString(years > 1 ? R.string.date_years : R.string.date_year);
        }
    }
}

