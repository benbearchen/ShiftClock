package org.bxmy.shiftclock;

import java.util.ArrayList;
import java.util.HashMap;

import org.bxmy.shiftclock.shiftduty.Duty;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;
import org.bxmy.shiftclock.shiftduty.Watch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class WatchActivity extends Activity {

    private Watch[] mWatches;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_list);

        ListView list = (ListView) findViewById(R.id.list_watch);
        list.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
                if (mWatches == null || mWatches.length == 0) {
                    if (position == 0) {
                        openWatchEditor(-1);
                    }
                } else {
                    if (position < mWatches.length)
                        openWatchEditor(position);
                    else if (position == mWatches.length)
                        openWatchEditor(-1);
                }

                return true;
            }

        });

        loadWatches();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWatches();
    }

    private void loadWatches() {
        ListView list = (ListView) findViewById(R.id.list_watch);
        SimpleAdapter adapter = new SimpleAdapter(this, initWatches(),
                R.layout.watch_list_item, new String[] { "1", "2", "3", "4",
                        "5" }, new int[] { R.id.label_watchDate,
                        R.id.label_watchDuty, R.id.label_watchBegin,
                        R.id.label_watchBeginToEnd, R.id.label_watchEnd });
        list.setAdapter(adapter);
    }

    private ArrayList<HashMap<String, Object>> initWatches() {
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map;
        mWatches = ShiftDuty.getInstance().getWatches();
        for (Watch watch : mWatches) {
            map = new HashMap<String, Object>();
            map.put("1", Util.formatDate(watch.getDayInSeconds()));

            int dutyId = watch.getDutyId();
            if (dutyId < 0) {
                map.put("2", "请选择休息还是班种");
            } else if (dutyId == 0) {
                map.put("2", "休息");
            } else {
                Duty duty = ShiftDuty.getInstance().getDutyById(dutyId);
                if (duty == null)
                    continue;

                map = new HashMap<String, Object>();
                map.put("2", duty.getName());
                if (watch.getBeforeSeconds() > 0) {
                    String startTime = Util.formatTimeRelatived(
                            watch.getDayInSeconds(), -watch.getBeforeSeconds());
                    map.put("3", startTime + "（提前）");
                } else {
                    map.put("3",
                            Util.formatTimeIn24Hours(watch.getDayInSeconds()));
                }

                map.put("5", "至");

                if (watch.getAfterSeconds() > 0) {
                    String endTime = Util.formatTimeRelatived(
                            watch.getDayInSeconds(), duty.getDurationSeconds()
                                    + watch.getAfterSeconds());
                    map.put("5", endTime + "（推迟）");
                } else {
                    map.put("5", Util.formatTimeRelatived(
                            watch.getDayInSeconds(), duty.getDurationSeconds()));
                }
            }

            items.add(map);
        }

        boolean appendNewItem = true;
        if (appendNewItem) {
            map = new HashMap<String, Object>();
            map.put("1", "<更多日期>");
            items.add(map);
        }

        return items;
    }

    private void openWatchEditor(int position) {
        Watch watch = null;
        if (position >= 0 || position < mWatches.length) {
            watch = mWatches[position];
        }

        Intent intent = new Intent();
        intent.putExtra("watch", watch);
        intent.setClass(this, EditWatchActivity.class);

        startActivity(intent);
    }
}
