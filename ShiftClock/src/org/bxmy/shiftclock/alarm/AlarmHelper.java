package org.bxmy.shiftclock.alarm;

import java.util.HashMap;

import org.bxmy.shiftclock.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmHelper {

    private Context mContext;

    private HashMap<Integer, PendingIntent> mTimerSender;

    private AlarmManager mAlarmManager;

    private static String ACTION_ALARM = "org.bxmy.shiftclock.action.alarm";

    public AlarmHelper(Context context) {
        this.mContext = context;
        this.mTimerSender = new HashMap<Integer, PendingIntent>();
        this.mAlarmManager = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
    }

    public void createTimer(long timeInSeconds, boolean rtcWakeup, int timerId) {
        PendingIntent sender = initTimer(timerId);
        startAlarmTime(timeInSeconds, rtcWakeup, timerId, sender);
    }

    public void cancelTimer(int timerId) {
        cancelAlarmTime(timerId);
    }

    private PendingIntent initTimer(int timerId) {
        if (mTimerSender.containsKey(timerId)) {
            return mTimerSender.get(timerId);
        }

        Intent intent = new Intent(ACTION_ALARM);
        intent.putExtra("timerId", timerId);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, timerId,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mTimerSender.put(timerId, sender);
        return sender;
    }

    private void startAlarmTime(long timeInSeconds, boolean rtcWakeup,
            int timerId, PendingIntent sender) {
        Log.i("shiftclock",
                "set timer "
                        + Util.formatYear2Minute(Util
                                .secondsToDate(timeInSeconds)) + " " + " id:"
                        + timerId);

        int rtc = rtcWakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC;
        mAlarmManager.setRepeating(rtc, timeInSeconds * 1000, 0, sender);
    }

    private void cancelAlarmTime(int timerId) {
        if (mTimerSender.containsKey(timerId)) {
            PendingIntent sender = mTimerSender.get(timerId);
            mAlarmManager.cancel(sender);
            mTimerSender.remove(timerId);
        }
    }
}
