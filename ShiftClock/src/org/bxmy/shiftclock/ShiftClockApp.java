package org.bxmy.shiftclock;

import org.bxmy.shiftclock.alarm.AlarmPlayer;
import org.bxmy.shiftclock.notification.NotificationHelper;

import android.app.Application;

public class ShiftClockApp extends Application {

    private static ShiftClockApp sSelf;

    private AlarmPlayer mAlarmPlayer;

    public static ShiftClockApp getInstance() {
        return sSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sSelf = this;

        NotificationHelper.createInstance(this);
        mAlarmPlayer = new AlarmPlayer();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        sSelf = null;
    }

    public AlarmPlayer getAlarmPlayer() {
        return mAlarmPlayer;
    }
}
