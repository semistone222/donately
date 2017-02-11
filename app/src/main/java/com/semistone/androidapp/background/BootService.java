package com.semistone.androidapp.background;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.semistone.androidapp.R;
import com.semistone.androidapp.utility.AlarmUtils;

public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isNotificationOn = sharedPreferences.getBoolean(getString(R.string.pref_notification_key), getResources().getBoolean(R.bool.pref_notification_default));

            if (isNotificationOn) {
                AlarmUtils.registerBroadcastReceiverRepeating(
                        this,
                        NotificationReceiver.class,
                        AlarmUtils.NOTIFY_EVERY_DAY_ID,
                        AlarmUtils.upComingSpecificTime(
                                getResources().getInteger(R.integer.encourage_notification_hour),
                                getResources().getInteger(R.integer.encourage_notification_minute)),
                        AlarmUtils.ONE_DAY
                );
            }
        }
    }
}
