package org.bxmy.shiftclock;

import org.bxmy.shiftclock.broadcast.BroadcastName;
import org.bxmy.shiftclock.shiftduty.Alarm;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShiftClockActivity extends Activity {

    private Alarm mCurrentAlarm;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        binds();

        // 启动分两种情况：闹铃启动和桌面启动
        mCurrentAlarm = ShiftDuty.getInstance().getNextAlarmTime();
        if (mCurrentAlarm != null) {
            setAlarmTime(mCurrentAlarm.getNextAlarmSeconds());
            setNextWatch(mCurrentAlarm.getWatchTime());
        } else {
            setAlarmTime(0);
            setNextWatch(null);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastName.ACTION_SHUTDOWN);
        registerReceiver(this.mShutdownReceiver, filter);

        checkAlarm(getIntent());
    }

    private void binds() {
        Button disable = (Button) findViewById(R.id.button_disable);
        disable.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarmRing();
                findViewById(R.id.button_pauseAlarm).setEnabled(false);
                if (mCurrentAlarm != null) {
                    mCurrentAlarm.disable();
                    updateCurrentAlarm();
                }
            }
        });

        Button pause = (Button) findViewById(R.id.button_pauseAlarm);
        pause.setEnabled(false);
        pause.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.button_pauseAlarm).setEnabled(false);
                stopAlarmRing();
                if (mCurrentAlarm != null) {
                    mCurrentAlarm.pause();
                    updateCurrentAlarm();
                }
            }
        });

        Button changeJob = (Button) findViewById(R.id.button_changeJob);
        changeJob.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ShiftClockActivity.this, JobActivity.class);

                startActivity(intent);
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

        Button setWatch = (Button) findViewById(R.id.button_setWatch);
        setWatch.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(ShiftClockActivity.this, WatchActivity.class);

                startActivity(intent);
            }
        });

        Button watchHistory = (Button) findViewById(R.id.button_watchHistory);
        watchHistory.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(ShiftClockActivity.this,
                        WatchHistoryListActivity.class);

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
                Intent intent = new Intent();
                intent.setClass(ShiftClockActivity.this, ConfigActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("shiftclock", "newIntent " + intent.toString());
        checkAlarm(intent);
    }

    @Override
    public void onResume() {
        Log.d("shiftclock", "onResume");
        super.onResume();

        updateCurrentWatch();

        Alarm nextAlarm = ShiftDuty.getInstance().getNextAlarmTime();
        if (nextAlarm != null && nextAlarm.isSame(mCurrentAlarm)) {
            mCurrentAlarm = nextAlarm;
            setAlarmTime(mCurrentAlarm.getNextAlarmSeconds());
            setNextWatch(mCurrentAlarm.getWatchTime());
            return;
        }

        mCurrentAlarm = nextAlarm;
        if (mCurrentAlarm != null) {
            setAlarmTime(mCurrentAlarm.getNextAlarmSeconds());
            setNextWatch(mCurrentAlarm.getWatchTime());
        } else {
            setAlarmTime(0);
            setNextWatch(null);
        }
    }

    @Override
    protected void onDestroy() {
        shutdown(false);

        stopAlarmRing();

        super.onDestroy();
    }

    private void checkAlarm(Intent intent) {
        if (mCurrentAlarm == null)
            return;

        if (intent.hasExtra("alarmTime")) {
            long alarmTime = intent.getLongExtra("alarmTime", 0);
            if (mCurrentAlarm.isValidAlarm(alarmTime)) {
                findViewById(R.id.button_pauseAlarm).setEnabled(true);
            }
        }
    }

    private void updateCurrentAlarm() {
        if (mCurrentAlarm != null && !mCurrentAlarm.isDisabled()) {
            setAlarmTime(mCurrentAlarm.getNextAlarmSeconds());
            setNextWatch(mCurrentAlarm.getWatchTime());
        } else {
            setAlarmTime(0);
            setNextWatch("");
        }
    }

    private void setAlarmTime(long timeInSeconds) {
        TextView label = (TextView) findViewById(R.id.text_alarmTime);
        label.setText(R.string.label_alarmTime);
        if (timeInSeconds != 0) {
            String t = Util.formatTimeToNow(timeInSeconds);
            label.append(t);
        } else {
            label.append("无");
        }
    }

    private void setNextWatch(String date) {
        TextView nextWatch = (TextView) findViewById(R.id.text_nextWatch);
        nextWatch.setText(R.string.label_nextWatch);
        if (!TextUtils.isEmpty(date))
            nextWatch.append(date);
        else
            nextWatch.append("尚未设置新的值班");
    }

    private void updateCurrentWatch() {
        TextView currentWatch = (TextView) findViewById(R.id.label_currentWatch);
        String watchInfo = ShiftDuty.getInstance().getCurrentWatchInfo();

        if (TextUtils.isEmpty(watchInfo)) {
            currentWatch.setText("");
        } else {
            currentWatch.setText(R.string.label_currentWatch);
            currentWatch.append(watchInfo);
        }
    }

    public void stopAlarmRing() {
        ShiftClockApp.getInstance().getAlarmPlayer().stopAlarmRing();
    }

    public static boolean isAlarmPlaying() {
        return ShiftClockApp.getInstance().getAlarmPlayer().isPlaying();
    }

    private void shutdown(boolean broadcast) {
        Log.i("shiftclock", "call shutdown() " + this);
        synchronized (BroadcastName.ACTION_SHUTDOWN) {
            if (this.mShutdownReceiver != null) {
                unregisterReceiver(this.mShutdownReceiver);
                this.mShutdownReceiver = null;
            }
        }

        if (broadcast) {
            finish();
        }
    }

    private BroadcastReceiver mShutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("shiftclock", "shutdownReceiver::onReceive()");
            shutdown(true);
        }
    };
}
