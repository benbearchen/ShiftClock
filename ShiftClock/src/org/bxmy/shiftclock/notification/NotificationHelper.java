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

    private Context mContext;

    public static synchronized void createInstance(Context context) {
        if (sSelf == null) {
            sSelf = new NotificationHelper(context);
        }
    }

    public static synchronized NotificationHelper getInstance() {
        return sSelf;
    }

    private NotificationHelper(Context context) {
        mContext = context;
        mManager = (NotificationManager) context
                .getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    public void showHint(int dayId, String title, String message, Intent intent) {
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
        Notification notify = new Notification(
                R.drawable.ic_notification_overlay, title,
                System.currentTimeMillis());
        notify.setLatestEventInfo(mContext, title, message, pi);
        notify.number = 0;
        mManager.notify(dayId, notify);
    }

    public void cancelHint(int dayId) {
        mManager.cancel(dayId);
    }
}
