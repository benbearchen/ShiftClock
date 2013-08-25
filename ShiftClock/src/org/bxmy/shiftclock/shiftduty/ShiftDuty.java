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

        return (String[]) names.toArray();
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
}
