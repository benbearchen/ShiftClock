package org.bxmy.shiftclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class ShiftClockActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setAlarmTime(this, System.currentTimeMillis() + 20 * 1000, 10 * 1000);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHUTDOWN);
        registerReceiver(this.mShutdownReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        cancelAlarmTime(this);
        shutdown(false);

        super.onDestroy();
    }

    private void setAlarmTime(Context context, long timeInMillis, long interval) {
        Log.i("shiftclock", "set alarm " + this);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_ALARM);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, interval, sender);
        this.mAlarmSender = sender;
    }

    private void cancelAlarmTime(Context context) {
        Log.i("shiftclock", "cancel alarm");
        if (this.mAlarmSender == null)
            return;

        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(this.mAlarmSender);
        this.mAlarmSender = null;
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (ACTION_ALARM.equals(intent.getAction())) {
                Log.i("shiftclock", "alarm");
                // 第1步中设置的闹铃时间到，这里可以弹出闹铃提示并播放响铃
                // 可以继续设置下一次闹铃时间;
                return;
            }
        }
    }

    public static class BootReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                Log.i("shiftclock", "receive boot completed");
            } else if (action.equals(Intent.ACTION_SHUTDOWN)) {
                Log.i("shiftclock", "receive system shutdown");
                Intent shutdownIntent = new Intent();
                shutdownIntent.setAction(ACTION_SHUTDOWN);
                context.sendBroadcast(shutdownIntent);
            }
        }
    }

    private void shutdown(boolean broadcast) {
        Log.i("shiftclock", "call shutdown()" + this);
        synchronized (ACTION_SHUTDOWN) {
            if (this.mShutdownReceiver != null) {
                unregisterReceiver(this.mShutdownReceiver);
                this.mShutdownReceiver = null;
            }
        }

        if (broadcast) {
            finish();
        }
    }

    private static String ACTION_ALARM = "org.bxmy.shiftclock.action.alarm";

    private static String ACTION_SHUTDOWN = "org.bxmy.shiftclock.action.shutdown";

    private PendingIntent mAlarmSender;

    private BroadcastReceiver mShutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("shiftclock", "shutdownReceiver::onReceive()");
            shutdown(true);
        }
    };
}
