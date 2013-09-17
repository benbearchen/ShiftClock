package org.bxmy.shiftclock;

import java.util.ArrayList;

import org.bxmy.shiftclock.shiftduty.Duty;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;
import org.bxmy.shiftclock.shiftduty.Watch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditWatchActivity extends Activity {

    private Watch mWatch;

    private DatePicker mWatchDay;

    private Spinner mComboDuty;

    private TimePicker mBeforeTime;

    private TimePicker mAfterTime;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_watch);

        bind();

        mWatch = getIntent().getParcelableExtra("watch");
        initWatch();
    }

    private void bind() {
        mWatchDay = (DatePicker) findViewById(R.id.date_watchDay);
        mWatchDay.setEnabled(false);

        mComboDuty = (Spinner) findViewById(R.id.combo_duty);
        ArrayList<String> dutyNames = new ArrayList<String>();
        dutyNames.add("休息");
        for (String duty : ShiftDuty.getInstance().getDutyNames())
            dutyNames.add(duty);

        ArrayAdapter<String> dutyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dutyNames);
        mComboDuty.setAdapter(dutyAdapter);
        mComboDuty.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                if (arg2 == 0) {
                    mBeforeTime.setEnabled(false);
                    mAfterTime.setEnabled(false);
                } else {
                    mBeforeTime.setEnabled(true);
                    mAfterTime.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        mBeforeTime = (TimePicker) findViewById(R.id.time_beforeWatch);
        mBeforeTime.setIs24HourView(true);

        mAfterTime = (TimePicker) findViewById(R.id.time_afterWatch);
        mAfterTime.setIs24HourView(true);

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

    private void initWatch() {
        if (mWatch == null) {
            mWatchDay.setEnabled(true);
            mWatch = Watch.createEmptyInDays(0, 0);
        }

        int dutyId = mWatch.getDutyId();
        if (dutyId < 0)
            dutyId = 0;

        mComboDuty.setSelection(dutyId);

        Util.updateDate(mWatchDay, mWatch.getDayInSeconds());
        Util.updateTime(mBeforeTime, mWatch.getBeforeSeconds());
        Util.updateTime(mAfterTime, mWatch.getAfterSeconds());
    }

    private void onOK() {
        long dayInSeconds = Util.getDate(mWatchDay);
        if (mWatchDay.isEnabled()) {
            if (ShiftDuty.getInstance().watchExistsDay(dayInSeconds)) {
                Toast.makeText(getApplicationContext(),
                        "您选择的日期已经有值班安排！未来或有支持重复排班…", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int dutyId = mComboDuty.getSelectedItemPosition();
        Duty duty = null;
        if (dutyId > 0) {
            duty = ShiftDuty.getInstance().getDutyById(dutyId);
            if (duty == null) {
                Toast.makeText(getApplicationContext(), "班种有误！sorry",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                dutyId = duty.getId();
            }
        }

        int beforeSeconds = 0;
        int afterSeconds = 0;
        if (duty != null) {
            dayInSeconds += duty.getStartSecondsInDay();
            beforeSeconds = Util.getTime(mBeforeTime);
            afterSeconds = Util.getTime(mAfterTime);
        }

        if (mWatch.getId() < 0) {
            ShiftDuty.getInstance().newWatch(dutyId, dayInSeconds,
                    beforeSeconds, afterSeconds);
        } else {
            Watch newWatch = new Watch(mWatch.getId(), dutyId, dayInSeconds,
                    beforeSeconds, afterSeconds);
            ShiftDuty.getInstance().updateWatch(newWatch);
        }

        finish();
    }

    private void onCancel() {
        finish();
    }
}
