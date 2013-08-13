package org.bxmy.shiftclock;

import org.bxmy.shiftclock.shiftduty.Duty;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditDutyActivity extends Activity {

    private int mIndex = -1;

    private int mDutyId = -1;

    private EditText mDutyName;

    private TimePicker mStartTime;

    private TimePicker mEndTime;

    private CheckBox mDefaultAlarm;

    private TimePicker mAlarmBefore;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_duty);

        bind();

        mIndex = getIntent().getIntExtra("index", -1);
        initDuty(mIndex);
    }

    private void bind() {
        mDutyName = (EditText) findViewById(R.id.edit_dutyName);
        mStartTime = (TimePicker) findViewById(R.id.time_startTime);
        mStartTime.setIs24HourView(true);
        mEndTime = (TimePicker) findViewById(R.id.time_endTime);
        mEndTime.setIs24HourView(true);

        mDefaultAlarm = (CheckBox) findViewById(R.id.checkbox_defaultAlarm);
        mAlarmBefore = (TimePicker) findViewById(R.id.time_alarmBefore);
        mAlarmBefore.setIs24HourView(true);

        mDefaultAlarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked) {
                    mAlarmBefore.setEnabled(false);
                } else {
                    mAlarmBefore.setEnabled(true);
                }

            }

        });

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

    private void initDuty(int position) {
        Duty duty = ShiftDuty.getInstance().getDutyInIndex(mIndex);

        if (duty == null) {
            updateTime(mStartTime, 9 * 3600);
            updateTime(mEndTime, 18 * 3600);

            mDefaultAlarm.setChecked(true);
            updateTime(mAlarmBefore, 1800);
        } else {
            mDutyId = duty.getId();
            mDutyName.setText(duty.getName());

            updateTime(mStartTime, duty.getStartSecondsInDay());
            updateTime(mEndTime,
                    duty.getStartSecondsInDay() + duty.getDurationSeconds());

            if (duty.getAlarmBeforeSeconds() >= 0) {
                mDefaultAlarm.setChecked(false);
                updateTime(mAlarmBefore, duty.getAlarmBeforeSeconds());
            } else {
                mDefaultAlarm.setChecked(true);
                updateTime(mAlarmBefore, 1800);
            }
        }
    }

    private void updateTime(TimePicker picker, int seconds) {
        picker.setCurrentHour(seconds / 3600 % 24);
        picker.setCurrentMinute(seconds / 60 % 60);
    }

    private int getTime(TimePicker picker) {
        return picker.getCurrentHour() * 3600 + picker.getCurrentMinute() * 60;
    }

    private void onOK() {
        if (mDutyName.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "班种名不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String dutyName = mDutyName.getText().toString();

        int start = getTime(mStartTime);
        int end = getTime(mEndTime);
        if (end <= start)
            end += 86400; // 加一天

        int duration = end - start;

        int alarmBefore = -1;
        if (!mDefaultAlarm.isChecked()) {
            alarmBefore = getTime(mAlarmBefore);
        }

        if (mDutyId < 0) {
            ShiftDuty.getInstance().newDuty(dutyName, start, duration,
                    alarmBefore);
        } else {
            Duty newDuty = new Duty(mDutyId, dutyName, start, duration,
                    alarmBefore);
            ShiftDuty.getInstance().updateDuty(newDuty);
        }

        finish();
    }

    private void onCancel() {
        finish();
    }
}
