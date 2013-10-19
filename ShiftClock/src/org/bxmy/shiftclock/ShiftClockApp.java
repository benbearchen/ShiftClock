package org.bxmy.shiftclock;

import org.bxmy.shiftclock.alarm.AlarmHelper;
import org.bxmy.shiftclock.alarm.AlarmPlayer;
import org.bxmy.shiftclock.notification.NotificationFutureWatch;
import org.bxmy.shiftclock.notification.NotificationHelper;
import org.bxmy.shiftclock.shiftduty.Alarm;
import org.bxmy.shiftclock.shiftduty.IShiftDutyEvent;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShiftClockApp extends Application implements IShiftDutyEvent {

    private static ShiftClockApp sSelf;

    private AlarmPlayer mAlarmPlayer;

    private AlarmHelper mTimerHelper;

    public static ShiftClockApp getInstance() {
        return sSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sSelf = this;

        ShiftDuty.getInstance().init(getApplicationContext(), this);

        NotificationHelper.createInstance(this);
        mAlarmPlayer = new AlarmPlayer();
        mTimerHelper = new AlarmHelper(getApplicationContext());

        ShiftDuty.getInstance().startUp();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        sSelf = null;
        ShiftDuty.getInstance().close();
    }

    public AlarmPlayer getAlarmPlayer() {
        return mAlarmPlayer;
    }

    @Override
    public void onSetTimer(long timeInSeconds, boolean rtcWakeup, int timerId) {
        Log.d("shiftclock",
                "set timer: " + timerId + " at "
                        + Util.formatTimeToNow(timeInSeconds));
        if (timeInSeconds > 0)
            mTimerHelper.createTimer(timeInSeconds, rtcWakeup, timerId);
        else
            mTimerHelper.cancelTimer(timerId);
    }

    @Override
    public void onAlarm(Alarm alarm) {
        if (!mAlarmPlayer.isPlaying()) {
            ShiftClockApp.getInstance().getAlarmPlayer().playAlarmRing();

            Context context = getApplicationContext();
            Intent alarmIntent = new Intent();
            alarmIntent.setClass(context, ShiftClockActivity.class);
            alarmIntent.putExtra("alarmTime", Util.now());
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(alarmIntent);
        }
    }

    @Override
    public void onFutureWatchHint(int dayId) {
        if (dayId < 0) {
            NotificationFutureWatch.getInstance().cancel();
        } else {
            Intent intent = new Intent(this, EditWatchActivity.class).putExtra(
                    "day", dayId);
            NotificationFutureWatch.getInstance().show(dayId, intent);
        }
    }

    public void onAlarmArrival(int timerId) {
        ShiftDuty.getInstance().timeUp(timerId);
    }
}
