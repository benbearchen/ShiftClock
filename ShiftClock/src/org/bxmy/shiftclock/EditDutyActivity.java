package org.bxmy.shiftclock;

import org.bxmy.shiftclock.shiftduty.Duty;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditDutyActivity extends Activity {

    private int mIndex = -1;

    private EditText mDutyName;

    private TimePicker mStartTime;

    private TimePicker mEndTime;

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
        mEndTime = (TimePicker) findViewById(R.id.time_endTime);

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
        } else {
            mDutyName.setText(duty.getName());

            updateTime(mStartTime, duty.getStartSecondsInDay());
            updateTime(mEndTime,
                    duty.getStartSecondsInDay() + duty.getDurationSeconds());
        }
    }

    private void updateTime(TimePicker picker, int seconds) {
        picker.setCurrentHour(seconds / 3600 % 24);
        picker.setCurrentMinute(seconds / 60 % 60);
    }

    private int getTime(TimePicker picker) {
        return picker.getCurrentHour() * 3600 + picker.getCurrentMinute();
    }

    private void onOK() {
        if (mDutyName.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "班种名不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int start = getTime(mStartTime);
        int end = getTime(mEndTime);
        if (end <= start)
            end += 86400; // 加一天

        int duration = end - start;
        
        Duty newDuty = new Duty();
        newDuty.setName(mDutyName.getText().toString());
        newDuty.setStartSecondsInDay(start);
        newDuty.setDurationSeconds(duration);
        
        if (mIndex < 0)
            ShiftDuty.getInstance().addDuty(newDuty);
        else
            ShiftDuty.getInstance().updateDuty(mIndex, newDuty);

        finish();
    }

    private void onCancel() {
        finish();
    }
}
