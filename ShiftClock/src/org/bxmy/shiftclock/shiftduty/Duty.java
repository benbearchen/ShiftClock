package org.bxmy.shiftclock.shiftduty;

public class Duty {

    private int mId;

    private String mName;

    /**
     * 自然天内值班开始时间
     */
    private int mStartSecondsInDay;

    /**
     * 值班持续时间
     */
    private int mDurationSeconds;

    /**
     * 相对于班种开始提前闹铃的时间。如果小于 0 表示使用默认闹铃设置。
     */
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
