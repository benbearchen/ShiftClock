package org.bxmy.shiftclock.shiftduty;

public class Duty {

    private String mName;

    private int mStartSecondsInDay;

    private int mDurationSeconds;

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
}
