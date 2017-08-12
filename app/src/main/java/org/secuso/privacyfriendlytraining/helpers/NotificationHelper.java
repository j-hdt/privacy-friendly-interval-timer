package org.secuso.privacyfriendlytraining.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.secuso.privacyfriendlytraining.R;
import org.secuso.privacyfriendlytraining.receivers.MotivationAlertReceiver;

import java.util.Calendar;

import static org.secuso.privacyfriendlytraining.activities.MotivationAlertTextsActivity.LOG_CLASS;

/**
 * Sets the motivation alert event to notify the user about a workout.
 *
 * @author Tobias Neidig
 * @author Alexander Karakuz
 * @version 20170603
 * @license GNU/GPLv3 http://www.gnu.org/licenses/gpl-3.0.html
 */
public class NotificationHelper {

    /**
     * Schedules (or updates) the motivation alert notification alarm
     * @param context The application context
     */
    public static void setMotivationAlert(Context context){
        Log.i(LOG_CLASS, "Setting motivation alert alarm");

        Intent motivationAlertIntent = new Intent(context, MotivationAlertReceiver.class);
        PendingIntent motivationAlertPendingIntent = PendingIntent.getBroadcast(context, 1, motivationAlertIntent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        long timestamp = sharedPref.getLong(context.getString(R.string.pref_notification_motivation_alert_time), 64800000);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Set alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), motivationAlertPendingIntent);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), motivationAlertPendingIntent);
        }else{
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), motivationAlertPendingIntent);
        }
        Log.i(LOG_CLASS, "Scheduled motivation alert at start time " + calendar.toString());
    }

    /**
     * Checks if the motivation alert is enabled in the settings
     * @param context The application context
     */
    public static boolean isMotivationAlertEnabled(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if(sharedPref != null){
            return sharedPref.getBoolean(context.getString(R.string.pref_notification_motivation_alert_enabled), false);
        }
        return false;
    }

    /**
     * Cancels the motivation alert
     * @param context The application context
     */
    public static void cancelMotivationAlert(Context context){
        Log.i(LOG_CLASS, "Canceling motivation alert alarm");
        Intent motivationAlertIntent = new Intent(context, MotivationAlertReceiver.class);
        PendingIntent motivationAlertPendingIntent = PendingIntent.getBroadcast(context, 1, motivationAlertIntent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(motivationAlertPendingIntent);
    }
}
