package com.semistone.androidapp.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

/**
 * Created by semistone on 2017-02-10.
 */

public class AlarmUtils {
    public final static long ONE_DAY = 1000 * 60 * 60 * 24;
    public final static int NOTIFY_EVERY_DAY_ID = 222;

    // TODO: 2017-02-10 설정 창에서 알람 키면 bash에서 키도록
    public static void registerBroadcastReceiverRepeating(Context context, Class<? extends BroadcastReceiver> target, int requestCode, long triggerAtMiils, long intervalMills) {
        Intent intent = new Intent(context, target);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMiils, intervalMills, pIntent);
    }

    // TODO: 2017-02-10 설정 창에서 알람 끄면 bash에서 끄도록
    public static void unRegisterBroadcastReceiver(Context context, Class<? extends BroadcastReceiver> target, int requestCode) {
        Intent intent = new Intent(context, target);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (pIntent != null) {
            alarmManager.cancel(pIntent);
            pIntent.cancel();
        }
    }

    public static long upComingSpecificTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();

        long hourDiff = hourOfDay - calendar.get(Calendar.HOUR_OF_DAY);
        long minuteDiff = minute - calendar.get(Calendar.MINUTE);

        if (hourDiff < 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else if (hourDiff == 0) {
            if (minuteDiff < 0) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hourOfDay,
                minute
        );

        return calendar.getTimeInMillis();
    }
}
