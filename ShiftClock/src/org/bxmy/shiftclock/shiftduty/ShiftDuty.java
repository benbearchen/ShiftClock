package org.bxmy.shiftclock.shiftduty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.bxmy.shiftclock.Util;
import org.bxmy.shiftclock.db.ConfigTable;
import org.bxmy.shiftclock.db.DBHelper;
import org.bxmy.shiftclock.db.DutyTable;
import org.bxmy.shiftclock.db.WatchTable;

import android.content.Context;
import android.util.Log;

public class ShiftDuty implements DBHelper.IDBEvent {

    private static ShiftDuty sShiftDuty;

    private ArrayList<Duty> mDuties = new ArrayList<Duty>();

    private ArrayList<Watch> mWatches = new ArrayList<Watch>();

    private DBHelper mDb;

    private DutyTable mDutyTable;

    private WatchTable mWatchTable;

    private ConfigTable mConfigTable;

    private boolean mLoaded;

    private IShiftDutyEvent mEvent;

    public static synchronized ShiftDuty getInstance() {
        if (sShiftDuty == null) {
            sShiftDuty = new ShiftDuty();
        }

        return sShiftDuty;
    }

    private ShiftDuty() {
    }

    public void init(Context context, IShiftDutyEvent event) {
        initDb(context);
        mEvent = event;

        load();
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

    public Watch newWatch(int dutyId, long dayInSeconds, int durationSeconds,
            int beforeSeconds, int afterSeconds) {
        if (mDb != null && mWatchTable != null) {
            Watch watch = mWatchTable.insert(dutyId, dayInSeconds,
                    durationSeconds, beforeSeconds, afterSeconds);
            mWatches.add(watch);

            updateTimerNextWatch();
            updateTimerFutureWatchHint();

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

                updateTimerNextWatch();
                updateTimerFutureWatchHint();
                break;
            }
        }
    }

    public void removeWatch(int watchId) {
        for (int i = 0; i < mWatches.size(); ++i) {
            if (mWatches.get(i).getId() == watchId) {
                mWatches.remove(i);
                if (this.mDb != null && this.mWatchTable != null) {
                    this.mWatchTable.delete(watchId);
                }

                updateTimerNextWatch();
                updateTimerFutureWatchHint();
                break;
            }
        }
    }

    public Watch[] getFutureWatches() {
        long today = Util.getDateInSeconds(new Date());
        long lastDay = 0;
        ArrayList<Watch> sorted = new ArrayList<Watch>();
        if (!mWatches.isEmpty()) {
            for (Watch w : mWatches) {
                if (w.getDayInSeconds() >= today)
                    sorted.add(w);
            }
        }

        if (!sorted.isEmpty()) {
            Collections.sort(sorted, Watch.createCompareByDate());
            lastDay = sorted.get(sorted.size() - 1).getDayInSeconds();
        }

        Watch[] watches = new Watch[sorted.size() + 7];
        watches = sorted.toArray(watches);
        if (watches != null && watches.length > sorted.size()) {
            for (int i = sorted.size(); i < watches.length; ++i) {
                int days = i - sorted.size();
                watches[i] = Watch.createEmptyInDays(lastDay, days);
            }
        }

        return watches;
    }

    public String getCurrentWatchInfo() {
        long now = Util.now();
        for (Watch w : mWatches) {
            if (w.isInWatch(now)) {
                if (w.getDutyId() <= 0) {
                    return "休息 "
                            + Util.formatYear2Date(Util.secondsToDate(w
                                    .getDayInSeconds()));
                } else {
                    String dutyName = getDutyName(w.getDutyId());
                    String watchTime = Util.formatDateTimeToNow(w
                            .getRealWatchBeginSeconds())
                            + " 至 "
                            + Util.formatTimeByOther(
                                    w.getRealWatchBeginSeconds(),
                                    w.getRealWatchEndSeconds());
                    return dutyName + " " + watchTime;
                }
            }
        }

        return "";
    }

    public Watch[] getWatchHistories() {
        long today = Util.getDateInSeconds(new Date());
        ArrayList<Watch> sorted = new ArrayList<Watch>();
        if (!mWatches.isEmpty()) {
            for (Watch w : mWatches) {
                if (w.getDayInSeconds() < today)
                    sorted.add(w);
            }
        }

        if (!sorted.isEmpty()) {
            Collections.sort(sorted, Watch.createCompareByDate());
            Collections.reverse(sorted);
        }

        return sorted.toArray(new Watch[0]);
    }

    public boolean watchExistsDay(long dayInSeconds) {
        Date day = Util.secondsToDate(dayInSeconds);
        for (Watch w : mWatches) {
            if (Util.isSameDay(day, Util.secondsToDate(w.getDayInSeconds())))
                return true;
        }

        return false;
    }

    /**
     * 获取下一次闹钟的时间
     */
    public Alarm getNextAlarmTime() {
        long current = Util.dateToSeconds(new Date());
        long next = 0;
        Watch nextWatch = null;
        for (Watch w : mWatches) {
            long begin = w.getRealWatchBeginSeconds();
            if (begin == 0 || w.getAlarmStopped() != 0)
                continue;

            Duty duty = null;
            if (w.getDutyId() > 0)
                duty = getDutyById(w.getDutyId());

            if (duty == null)
                continue;

            long end = w.getDayInSeconds() + duty.getDurationSeconds()
                    + w.getAfterSeconds();
            if (begin < current && end < current)
                continue;

            if (next == 0 || begin < next) {
                next = begin;
                nextWatch = w;
            }
        }

        if (nextWatch != null) {
            Duty duty = getDutyById(nextWatch.getDutyId());
            int alarmBefore = getDefaultAlarmBeforeSeconds();
            String dutyName = duty != null ? duty.getName() : "";
            if (duty != null && duty.getAlarmBeforeSeconds() > 0) {
                alarmBefore = duty.getAlarmBeforeSeconds();
            }

            int interval = getDefaultAlarmIntervalSeconds();
            return new Alarm(nextWatch, dutyName, alarmBefore, interval);
        }

        return null;
    }

    /**
     * 获取默认提前闹铃时间。初始为半小时
     */
    public int getDefaultAlarmBeforeSeconds() {
        int defaultAlarmBeforeSeconds = 1800;
        if (mConfigTable != null) {
            return mConfigTable.getIntByName("alarmBefore",
                    defaultAlarmBeforeSeconds);
        }

        return defaultAlarmBeforeSeconds;
    }

    public void setDefaultAlarmBeforeSeconds(int alarmBeforeSeconds) {
        if (alarmBeforeSeconds > 0 && mConfigTable != null) {
            mConfigTable.setByName("alarmBefore",
                    String.valueOf(alarmBeforeSeconds));
            updateTimerNextWatch();
        }
    }

    /**
     * 获取默认的闹铃间隔。默认为十分钟
     */
    public int getDefaultAlarmIntervalSeconds() {
        int defaultAlarmIntervalSeconds = 600;
        if (mConfigTable != null) {
            return mConfigTable.getIntByName("alarmInterval",
                    defaultAlarmIntervalSeconds);
        }

        return defaultAlarmIntervalSeconds;
    }

    public void setDefaultAlarmIntervalSeconds(int alarmIntervalSeconds) {
        if (alarmIntervalSeconds > 0 && mConfigTable != null) {
            mConfigTable.setByName("alarmInterval",
                    String.valueOf(alarmIntervalSeconds));
            updateTimerNextWatch();
        }
    }

    /**
     * 获取将来（如明天）需要设置的日期
     * 
     * @return
     */
    private int getFutureDayNeedToSet() {
        int todayId = Util.getDayIdOfTime(Util.now());

        ArrayList<Integer> dayIdArray = new ArrayList<Integer>();
        int count = 0;
        for (int i = 0; i < mWatches.size(); ++i) {
            int d = Util.getDayIdOfTime(mWatches.get(i).getDayInSeconds());
            if (d > todayId) {
                ++count;
                dayIdArray.add(d);
            }
        }

        if (count == 0)
            return todayId + 1;

        int[] dayIds = new int[count];
        for (int i = 0; i < count; ++i) {
            dayIds[i] = dayIdArray.get(i);
        }

        Arrays.sort(dayIds, 0, count);
        int f = todayId + 1;
        for (int i = 0; i < count; ++i) {
            int d = dayIds[i];
            if (d <= f) {
                if (d == f) {
                    ++f;
                }

                continue;
            } else {
                return f + 1;
            }
        }

        return f;
    }

    public int getWatchHintSecondsInDay() {
        int defaultHintSeconds = 20 * 3600; // 晚 8 点
        if (mConfigTable != null) {
            return mConfigTable.getIntByName("futureWatchHint",
                    defaultHintSeconds);
        }

        return defaultHintSeconds;
    }

    public void setWatchHintSecondsInDay(int hintSecondsInDay) {
        if (hintSecondsInDay > 0 && mConfigTable != null) {
            mConfigTable.setByName("futureWatchHint",
                    String.valueOf(hintSecondsInDay));
            updateTimerFutureWatchHint();
        }
    }

    private void initDb(Context context) {
        mDutyTable = new DutyTable();
        DBHelper.addTable(mDutyTable);

        mWatchTable = new WatchTable();
        DBHelper.addTable(mWatchTable);

        mConfigTable = new ConfigTable();
        DBHelper.addTable(mConfigTable);

        mDb = DBHelper.createInstance(context, "shiftduty", this);
    }

    private void load() {
        if (mLoaded)
            return;

        mLoaded = true;

        loadDuties();
        loadWatches();
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

    @Override
    public void bind(DBHelper db) {
        mDb = db;
    }

    @Override
    public void onCreated() {
        load();

        // do some initialization
        if (mDb != null && mConfigTable != null) {
            mConfigTable.setByName("alarmBefore", String.valueOf(1800));
        }
    }

    @Override
    public void onUpgraded(int oldVersion, int newVersion) {
        load();

        if (oldVersion < newVersion) {
            // upgrade fix
            if (oldVersion < 4) {
                // fix watch duration, alarmStopped, alarmPaused
                for (Watch w : mWatches) {
                    if (w.getDutyId() > 0) {
                        Duty duty = getDutyById(w.getDutyId());
                        if (duty != null) {
                            Log.d("shiftclock",
                                    "update Watch: "
                                            + Util.formatDate(w
                                                    .getDayInSeconds()));
                            w.setDutyDurationSeconds(duty.getDurationSeconds());
                            if (mWatchTable != null) {
                                mWatchTable.update(w);
                            }
                        }
                    }
                }
            }
        } else {
            // down-grade fix
        }
    }

    public void startUp() {
        updateTimerNextWatch();
        updateTimerFutureWatchHint();
    }

    public void timeUp(int timerId) {
        long now = Util.now();

        if (timerId == 1) {
            Alarm alarm = getNextAlarmTime();
            if (alarm.isValidAlarm(now)) {
                eventAlarm(alarm);
            }

            // updateTimerNextWatch();
        }

        if (timerId == 2) {
            checkTimerFutureWatchHint();
        }
    }

    private void updateTimerNextWatch() {
        long time = 0;
        Alarm alarm = getNextAlarmTime();
        if (alarm != null)
            time = alarm.getNextAlarmSeconds();

        eventSetTimer(time, true, 1);
    }

    private long calcFutureWatchHintTimeOfDayId(int dayId) {
        return Util.getTimeOfDayId(dayId - 1) + getWatchHintSecondsInDay();
    }

    private void updateTimerFutureWatchHint() {
        long time = 0;
        int dayId = getFutureDayNeedToSet();
        if (dayId >= 0) {
            time = calcFutureWatchHintTimeOfDayId(dayId);
            long now = Util.now();
            if (time < now + 10) {
                time = now;
            }
        }

        eventSetTimer(time, false, 2);
    }

    private void checkTimerFutureWatchHint() {
        int dayId = getFutureDayNeedToSet();
        if (dayId < 0)
            return;

        long now = Util.now();
        long futureHintTime = calcFutureWatchHintTimeOfDayId(dayId);
        long beginOfFutureDay = Util.getTimeOfDayId(dayId);
        long nextHintTime = futureHintTime;
        if (now > futureHintTime && now < beginOfFutureDay) {
            eventFutureWatchHint(dayId);
            int hintAgainSeconds = 3600;
            nextHintTime = Math.min(now + hintAgainSeconds, beginOfFutureDay);
        }

        eventSetTimer(nextHintTime, false, 2);
    }

    private void eventSetTimer(long timeInSeconds, boolean rtcWakeup,
            int timerId) {
        if (mEvent != null) {
            mEvent.onSetTimer(timeInSeconds, rtcWakeup, timerId);
        }
    }

    private void eventAlarm(Alarm alarm) {
        if (mEvent != null)
            mEvent.onAlarm(alarm);
    }

    private void eventFutureWatchHint(int dayId) {
        if (mEvent != null)
            mEvent.onFutureWatchHint(dayId);
    }

}
