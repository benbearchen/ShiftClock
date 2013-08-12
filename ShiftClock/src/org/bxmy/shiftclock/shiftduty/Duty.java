package org.bxmy.shiftclock.shiftduty;

public class Duty {

    private int mId;

    private String mName;

    private int mStartSecondsInDay;

    private int mDurationSeconds;

    private int mAlarmBeforeSeconds;

    public Duty(int id) {
        this.mId = id;
    }

    public Duty(int id, String name, int startSecondsInDay,
            int durationSeconds, int alarmBeforeSeconds) {
        this.mId = id;
        setName(name);
        setStartSecondsInDay(startSecondsInDay);
        setDurationSeconds(durationSeconds);
        setAlarmBeforeSeconds(alarmBeforeSeconds);
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getStartSecondsInDay() {
        return mStartSecondsInDay;
    }

    public void setStartSecondsInDay(int startSecondsInDay) {
        this.mStartSecondsInDay = startSecondsInDay;
    }

    public int getDurationSeconds() {
        return mDurationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.mDurationSeconds = durationSeconds;
    }

    public int getAlarmBeforeSeconds() {
        return mAlarmBeforeSeconds;
    }

    public void setAlarmBeforeSeconds(int alarmBeforeSeconds) {
        this.mAlarmBeforeSeconds = alarmBeforeSeconds;
    }
}
