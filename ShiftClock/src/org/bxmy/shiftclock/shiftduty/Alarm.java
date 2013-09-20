package org.bxmy.shiftclock.shiftduty;

import org.bxmy.shiftclock.Util;

public class Alarm {

    private String date;

    private long beginSeconds;

    private long alarmBeforeSeconds;

    private long intervalSeconds;

    private boolean disabled;

    private long pausedSeconds;

    public Alarm(String date, long beginSeconds, long alarmBeforeSeconds,
            long intervalSeconds) {
        this.date = date;
        this.beginSeconds = beginSeconds;
        this.alarmBeforeSeconds = alarmBeforeSeconds;
        this.intervalSeconds = intervalSeconds;
        this.disabled = false;
        this.pausedSeconds = 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getBeginSeconds() {
        return beginSeconds;
    }

    public void setBeginSeconds(long beginSeconds) {
        this.beginSeconds = beginSeconds;
    }

    public long getAlarmBeforeSeconds() {
        return alarmBeforeSeconds;
    }

    public void setAlarmBeforeSeconds(long alarmBeforeSeconds) {
        this.alarmBeforeSeconds = alarmBeforeSeconds;
    }

    public long getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(long intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public void disable() {
        this.disabled = true;
    }

    public void pause() {
        this.pausedSeconds = Util.now();
    }

    public boolean isValidAlarm(long alarmTime) {
        if (disabled)
            return false;

        if (pausedSeconds > 0 && alarmTime < pausedSeconds) {
            return false;
        }

        long first = getBeginSeconds() - getAlarmBeforeSeconds();
        if (alarmTime < first)
            return false;

        long paused = pausedSeconds;
        if (paused >= first)
            return false;

        if (alarmTime > getBeginSeconds())
            ;// TODO: 是否已过上班时间了，就不认为是合法的？

        return true;
    }

    public long getNextAlarmSeconds() {
        if (disabled)
            return 0;

        long first = getBeginSeconds() - getAlarmBeforeSeconds();
        long now = Util.now();
        if (now < first)
            return first;

        return first + (now - first + intervalSeconds - 1) / intervalSeconds
                * intervalSeconds;
    }

    public String getWatchBeginTime() {
        return Util.formatDateTimeToNow(getBeginSeconds());
    }
}
