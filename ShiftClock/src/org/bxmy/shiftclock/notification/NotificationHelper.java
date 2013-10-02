package org.bxmy.shiftclock.notification;

import android.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {

    private static NotificationHelper sSelf;

    private NotificationManager mManager;

    public static synchronized NotificationHelper getInstance(Context context) {
        if (sSelf == null) {
            sSelf = new NotificationHelper(context);
        }

        return sSelf;
    }

    private NotificationHelper(Context context) {
        mManager = (NotificationManager) context
                .getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    public void showHint(int dayId, String title, String message,
            Context context, Intent intent) {
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notify = new Notification(
                R.drawable.ic_notification_overlay, title,
                System.currentTimeMillis());
        notify.setLatestEventInfo(context, title, message, pi);
        notify.number = 0;
        mManager.notify(dayId, notify);
    }

    public void cancelHint(int dayId) {
        mManager.cancel(dayId);
    }
}
