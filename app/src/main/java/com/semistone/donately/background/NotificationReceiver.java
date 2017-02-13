package com.semistone.donately.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.semistone.donately.utility.NotificationUtils;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.encourageDonation(context);
    }
}