package org.bxmy.shiftclock;

import org.bxmy.shiftclock.shiftduty.ShiftDuty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class ConfigActivity extends Activity {

    private TimePicker mTimeDefaultAlarmBefore;

    private TimePicker mTimeDefaultAlarmInterval;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        bind();
        init();
    }

    private void bind() {
        mTimeDefaultAlarmBefore = (TimePicker) findViewById(R.id.time_defaultAlarmBefore);
        mTimeDefaultAlarmBefore.setIs24HourView(true);

        mTimeDefaultAlarmInterval = (TimePicker) findViewById(R.id.time_defaultAlarmInterval);
        mTimeDefaultAlarmInterval.setIs24HourView(true);

        Button ok = (Button) findViewById(R.id.button_ok);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onOK();
            }
        });

        Button cancel = (Button) findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
    }

    private void init() {
        int before = ShiftDuty.getInstance().getDefaultAlarmBeforeSeconds();
        Util.updateTime(mTimeDefaultAlarmBefore, before);

        int interval = ShiftDuty.getInstance().getDefaultAlarmIntervalSeconds();
        Util.updateTime(mTimeDefaultAlarmInterval, interval);
    }

    private void onOK() {
        int beforeSeconds = Util.getTime(mTimeDefaultAlarmBefore);
        int intervalSeconds = Util.getTime(mTimeDefaultAlarmInterval);
        if (intervalSeconds > beforeSeconds) {
            Toast.makeText(getApplicationContext(), "闹铃间隔不能大于提前时间",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ShiftDuty.getInstance().setDefaultAlarmBeforeSeconds(beforeSeconds);
        ShiftDuty.getInstance().setDefaultAlarmIntervalSeconds(intervalSeconds);

        finish();
    }

    private void onCancel() {
        finish();
    }
}
