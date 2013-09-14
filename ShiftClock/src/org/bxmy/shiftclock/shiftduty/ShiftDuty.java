package org.bxmy.shiftclock.shiftduty;

import java.util.ArrayList;

import org.bxmy.shiftclock.db.DBHelper;
import org.bxmy.shiftclock.db.DutyTable;
import org.bxmy.shiftclock.db.WatchTable;

import android.content.Context;
import android.util.Log;

public class ShiftDuty {

    private static ShiftDuty sShiftDuty;

    private ArrayList<Duty> mDuties = new ArrayList<Duty>();

    private ArrayList<Watch> mWatches = new ArrayList<Watch>();

    private DBHelper mDb;

    private DutyTable mDutyTable;

    private WatchTable mWatchTable;

    public static synchronized ShiftDuty getInstance() {
        if (sShiftDuty == null) {
            sShiftDuty = new ShiftDuty();
        }

        return sShiftDuty;
    }

    private ShiftDuty() {
    }

    public void init(Context context) {
        initDb(context);
        loadDuties();
        loadWatches();
    }

    public void close() {
        if (this.mDb != null) {
            this.mDb.close();
        }
    }

    public String[] getDutyNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Duty duty : mDuties) {
            names.add(duty.getName());
        }

        return (String[]) names.toArray(new String[0]);
    }

    public String getDutyName(int dutyId) {
        Duty duty = getDutyById(dutyId);
        if (duty != null) {
            return duty.getName();
        } else {
            return "";
        }
    }

    public Duty newDuty(String name, int startSecondsInDay,
            int durationSeconds, int alarmBeforeSeconds) {
        if (mDb != null && mDutyTable != null) {
            Duty duty = mDutyTable.insert(name, startSecondsInDay,
                    durationSeconds, alarmBeforeSeconds);
            mDuties.add(duty);
            return duty;
        }

        Log.e("DB", "newDuty(): db not valid");
        return null;
    }

    public void updateDuty(Duty duty) {
        for (int i = 0; i < mDuties.size(); ++i) {
            if (mDuties.get(i).getId() == duty.getId()) {
                mDuties.set(i, duty);
                if (this.mDb != null && this.mDutyTable != null) {
                    this.mDutyTable.update(duty);
                }
            }
        }
    }

    public Duty[] getDuties() {
        return (Duty[]) mDuties.toArray(new Duty[0]);
    }

    public Duty getDutyInIndex(int index) {
        if (index < 0 || index >= mDuties.size())
            return null;

        return mDuties.get(index);
    }

    public Duty getDutyById(int dutyId) {
        for (int i = 0; i < mDuties.size(); ++i) {
            if (mDuties.get(i).getId() == dutyId)
                return mDuties.get(i);
        }

        return null;
    }

    public Watch newWatch(int dutyId, long dayInSeconds, int beforeSeconds,
            int afterSeconds) {
        if (mDb != null && mWatchTable != null) {
            Watch watch = mWatchTable.insert(dutyId, dayInSeconds,
                    beforeSeconds, afterSeconds);
            mWatches.add(watch);
            return watch;
        }

        Log.e("DB", "newWatch(): db not valid");
        return null;
    }

    public void updateWatch(Watch watch) {
        for (int i = 0; i < mWatches.size(); ++i) {
            if (mWatches.get(i).getId() == watch.getId()) {
                mWatches.set(i, watch);
                if (this.mDb != null && this.mWatchTable != null) {
                    this.mWatchTable.update(watch);
                }
            }
        }
    }

    public Watch[] getWatches() {
        Watch[] watches = new Watch[mWatches.size() + 7];
        watches = mWatches.toArray(watches);
        if (watches != null && watches.length > mWatches.size()) {
            for (int i = mWatches.size(); i < watches.length; ++i) {
                watches[i] = Watch.createEmptyInDays(i - mWatches.size());
            }
        }

        return watches;
    }

    /**
     * 获取下一次闹钟的时间
     */
    public long getNextAlarmTimeMS() {
        // TODO: implements real alarm time
        return 0;
    }

    /**
     * 获取默认提前闹铃时间。初始为半小时
     */
    public int getDefaultAlarmBeforeSeconds() {
        return 1800;
    }

    public void setDefaultAlarmBeforeSeconds(int alarmBeforeSeconds) {
        // TODO:
    }

    /**
     * 获取默认的闹铃间隔。默认为十分钟
     */
    public int getDefaultAlarmIntervalSeconds() {
        return 600;
    }

    public void setDefaultAlarmIntervalSeconds(int alarmIntervalSeconds) {
        // TODO:
    }

    private void initDb(Context context) {
        mDutyTable = new DutyTable();
        DBHelper.addTable(mDutyTable);

        mWatchTable = new WatchTable();
        DBHelper.addTable(mWatchTable);

        mDb = DBHelper.createInstance(context, "shiftduty");
    }

    private void loadDuties() {
        if (mDb != null && mDutyTable != null) {
            mDuties = mDutyTable.selectAll();
        }
    }

    private void loadWatches() {
        if (mDb != null && mWatchTable != null) {
            mWatches = mWatchTable.selectAll();
        }
    }
}
