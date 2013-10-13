package org.bxmy.shiftclock;

import org.bxmy.shiftclock.notification.NotificationHelper;

import android.app.Application;

public class ShiftClockApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationHelper.createInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
