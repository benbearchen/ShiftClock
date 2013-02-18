package org.bxmy.shiftclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bxmy.shiftclock.shiftduty.Duty;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class DutyListActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duty_list);

        GridView grid = (GridView) findViewById(R.id.grid_duty_list);
        SimpleAdapter adapter = new SimpleAdapter(this, addTestDuties(),
                R.layout.duty_list_item, new String[] { "1", "2", "3" },
                new int[] { R.id.text_dutyName, R.id.time_dutyStart,
                        R.id.time_dutyEnd });
        grid.setAdapter(adapter);
    }

    private ArrayList<HashMap<String, Object>> addTestDuties() {
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        Duty[] duties = ShiftDuty.getInstance().getDuties();
        for (Duty duty : duties) {
            map = new HashMap<String, Object>();
            map.put("1", duty.getName());
            int start = duty.getStartSecondsInDay();
            map.put("2", formatSecondsInDay(start));
            int end = duty.getStartSecondsInDay() + duty.getDurationSeconds();
            map.put("3", formatSecondsInDay(end));
            items.add(map);
        }

        return items;
    }

    private String formatSecondsInDay(int seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date(seconds * 1000L));
    }
}
