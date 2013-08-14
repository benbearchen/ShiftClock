package org.bxmy.shiftclock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bxmy.shiftclock.shiftduty.ShiftDuty;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShiftClockActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ShiftDuty.getInstance().init(this);

        long nextAlarmTime = ShiftDuty.getInstance().getNextAlarmTimeMS();
        if (nextAlarmTime > 0) {
            setAlarmTime(this, nextAlarmTime, 10 * 1000);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHUTDOWN);
        registerReceiver(this.mShutdownReceiver, filter);

        Button disable = (Button) findViewById(R.id.button_disable);
        disable.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarmTime();
                stopAlarmRing();
            }
        });

        Button setDuty = (Button) findViewById(R.id.button_setDuty);
        setDuty.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ShiftClockActivity.this, DutyListActivity.class);

                startActivity(intent);
            }
        });

        Button adjustDuty = (Button) findViewById(R.id.button_adjustDuty);
        adjustDuty.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(ShiftClockActivity.this,
                        AdjustDutyActivity.class);

                startActivity(intent);
            }
        });

        Button about = (Button) findViewById(R.id.button_about);
        about.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(ShiftClockActivity.this, AboutActivity.class);

                startActivity(intent);
            }
        });

        Button regularDuty = (Button) findViewById(R.id.button_regularDuty);
        regularDuty.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 固定值班设置
            }
        });

        Button adjustHoliday = (Button) findViewById(R.id.button_adjustHoliday);
        adjustHoliday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 节假日值班调整
            }
        });

        Button setConfig = (Button) findViewById(R.id.button_config);
        setConfig.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 全局设置
            }
        });
    }

    @Override
    protected void onDestroy() {
        shutdown(false);

        ShiftDuty.getInstance().close();

        super.onDestroy();
    }

    private void setAlarmTime(long timeInMillis) {
        TextView label = (TextView) findViewById(R.id.text_alarmTime);
        label.setText(R.string.label_alarmTime);
        if (timeInMillis != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
            String t = sdf.format(new Date(timeInMillis));
            label.append(t);
        } else {
            label.append("禁用");
        }
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
        setAlarmTime(timeInMillis);
    }

    private void cancelAlarmTime() {
        cancelAlarmTime(this);
    }

    private void cancelAlarmTime(Context context) {
        Log.i("shiftclock", "cancel alarm");
        if (this.mAlarmSender == null)
            return;

        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(this.mAlarmSender);
        this.mAlarmSender = null;
        setAlarmTime(0);
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (ACTION_ALARM.equals(intent.getAction())) {
                Log.i("shiftclock", "alarm");
                // 第1步中设置的闹铃时间到，这里可以弹出闹铃提示并播放响铃
                // 可以继续设置下一次闹铃时间;
                playAlarmRing(context);
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
        Log.i("shiftclock", "call shutdown() " + this);
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

    private static void playAlarmRing(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, uri);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    private static void stopAlarmRing() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
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

    private static MediaPlayer mMediaPlayer;
}
