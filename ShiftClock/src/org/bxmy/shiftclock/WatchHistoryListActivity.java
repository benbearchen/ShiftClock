package org.bxmy.shiftclock;

import java.util.ArrayList;
import java.util.HashMap;

import org.bxmy.shiftclock.shiftduty.Duty;
import org.bxmy.shiftclock.shiftduty.ShiftDuty;
import org.bxmy.shiftclock.shiftduty.Watch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class WatchHistoryListActivity extends Activity {

    private Watch[] mWatches;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_history_list);

        loadWatches();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWatches();
    }

    private void loadWatches() {
        ListView list = (ListView) findViewById(R.id.list_watch_history);
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
        mWatches = ShiftDuty.getInstance().getWatchHistories();
        for (Watch watch : mWatches) {
            map = new HashMap<String, Object>();
            map.put("1",
                    Util.formatWeek(watch.getDayInSeconds()) + " "
                            + Util.formatDate(watch.getDayInSeconds()));

            int dutyId = watch.getDutyId();
            if (dutyId < 0) {
                map.put("2", "");
            } else if (dutyId == 0) {
                map.put("2", "休息");
            } else {
                Duty duty = ShiftDuty.getInstance().getDutyById(dutyId);
                if (duty == null)
                    continue;

                map.put("2", "值班：" + duty.getName());

                String beginTime = Util.formatTimeByRelatived(
                        watch.getDayInSeconds(), -watch.getBeforeSeconds());
                if (watch.getBeforeSeconds() != 0) {
                    String adj = watch.getBeforeSeconds() > 0 ? "（提前）" : "（推迟）";
                    beginTime = beginTime + adj;
                }

                map.put("3", beginTime);

                map.put("4", "至");

                String afterTime = Util.formatTimeByRelatived(
                        watch.getDayInSeconds(), duty.getDurationSeconds()
                                + watch.getAfterSeconds());
                if (watch.getAfterSeconds() != 0) {
                    String adj = watch.getAfterSeconds() < 0 ? "（提前）" : "（推迟）";
                    afterTime = afterTime + adj;
                }

                map.put("5", afterTime);
            }

            items.add(map);
        }

        return items;
    }
}
