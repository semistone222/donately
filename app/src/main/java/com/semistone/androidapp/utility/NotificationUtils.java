package com.semistone.androidapp.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.semistone.androidapp.R;
import com.semistone.androidapp.main.MainActivity;

/**
 * Created by semistone on 2017-02-10.
 */

public class NotificationUtils {

    private static final int ENCOURAGE_DONATION_NOTIFICATION_ID = 1138;
    private static final int ENCOURAGE_DONATION_PENDING_INTENT_ID = 3417;

    public static void encourageDonation(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_loving_24dp)
                .setLargeIcon(largeIcon(context, R.drawable.ic_loving_24dp))
                .setContentTitle(context.getString(R.string.encourage_donation_notification_title))
                .setContentText(context.getString(R.string.encourage_donation_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.encourage_donation_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ENCOURAGE_DONATION_NOTIFICATION_ID, builder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                ENCOURAGE_DONATION_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context, int resId) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, resId);
        return largeIcon;
    }
}
