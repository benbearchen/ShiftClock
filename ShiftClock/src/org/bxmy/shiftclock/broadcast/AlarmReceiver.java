package org.bxmy.shiftclock.broadcast;

import org.bxmy.shiftclock.ShiftClockApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (BroadcastName.ACTION_ALARM.equals(intent.getAction())) {
            int timerId = intent.getIntExtra("timerId", 1);
            ShiftClockApp.getInstance().onAlarmArrival(timerId);
        }
    }
}
