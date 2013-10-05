package org.bxmy.shiftclock.shiftduty;

import java.util.Comparator;
import java.util.Date;

import org.bxmy.shiftclock.Util;

import android.os.Parcel;
import android.os.Parcelable;

public class Watch implements Parcelable {

    private int mId;

    /**
     * 对应的班种。-1 表示未设置（内存状态，不写入数据库）；0 表示休息；大于 0 表示按某班种上班
     */
    private int mDutyId;

    /**
     * 值班所在天
     */
    private long mDayInSeconds;

    /**
     * 班种指定的值班时长
     */
    private int mDutyDurationSeconds;

    /**
     * 是否提前值班。0 表示不提前，正数表示提前的时间
     */
    private int mBeforeSeconds;

    /**
     * 是否延后落班。0 表示不延后，正数表示延后的时间
     */
    private int mAfterSeconds;

    /**
     * 闹钟是否已经被停止
     */
    private int mAlarmStopped;

    /**
     * 闹钟被暂停闹铃的时刻
     */
    private long mAlarmPasuedInSeconds;

    public static Watch createEmptyInDays(long lastDayInSeconds, int days) {
        Date date;
        if (lastDayInSeconds == 0) {
            date = new Date();
        } else {
            days += 1;
            date = Util.secondsToDate(lastDayInSeconds);
        }

        date = Util.secondsToDate(Util.dateToSeconds(date) + days * 86400L);
        date = new Date(date.getYear(), date.getMonth(), date.getDate());
        return new Watch(-1, -1, Util.dateToSeconds(date), 0, 0, 0, 0, 0);
    }

    public static Watch createEmptyByDayId(int dayId) {
        return new Watch(-1, -1, Util.getTimeOfDayId(dayId), 0, 0, 0, 0, 0);
    }

    public Watch(int id) {
        this.mId = id;
    }

    public Watch(int id, int dutyId, long dayInSeconds, int durationSeconds,
            int beforeSeconds, int afterSeconds, int alarmStopped,
            long alarmPasuedInSeconds) {
        this.mId = id;
        setDutyId(dutyId);
        setDayInSeconds(dayInSeconds);
        setDutyDurationSeconds(durationSeconds);
        setBeforeSeconds(beforeSeconds);
        setAfterSeconds(afterSeconds);
        setAlarmStopped(alarmStopped);
        setAlarmPasuedInSeconds(alarmPasuedInSeconds);
    }

    public int getId() {
        return this.mId;
    }

    public int getDutyId() {
        return this.mDutyId;
    }

    public void setDutyId(int dutyId) {
        this.mDutyId = dutyId;
    }

    public long getDayInSeconds() {
        return this.mDayInSeconds;
    }

    public void setDayInSeconds(long dayInSeconds) {
        this.mDayInSeconds = dayInSeconds;
    }

    public int getDutyDurationSeconds() {
        return this.mDutyDurationSeconds;
    }

    public void setDutyDurationSeconds(int durationSeconds) {
        this.mDutyDurationSeconds = durationSeconds;
    }

    public int getBeforeSeconds() {
        return this.mBeforeSeconds;
    }

    public void setBeforeSeconds(int beforeSeconds) {
        this.mBeforeSeconds = beforeSeconds;
    }

    public int getAfterSeconds() {
        return this.mAfterSeconds;
    }

    public void setAfterSeconds(int afterSeconds) {
        this.mAfterSeconds = afterSeconds;
    }

    public int getAlarmStopped() {
        return this.mAlarmStopped;
    }

    public void setAlarmStopped(int alarmStopped) {
        this.mAlarmStopped = alarmStopped;
    }

    public long getAlarmPausedInSeconds() {
        return this.mAlarmPasuedInSeconds;
    }

    public void setAlarmPasuedInSeconds(long alarmPasuedInSeconds) {
        this.mAlarmPasuedInSeconds = alarmPasuedInSeconds;
    }

    public long getRealWatchBeginSeconds() {
        if (this.mDutyId <= 0)
            return 0;

        return this.mDayInSeconds - this.mBeforeSeconds;
    }

    public long getRealWatchEndSeconds() {
        if (this.mDutyId <= 0)
            return 0;

        return this.mDayInSeconds + this.mDutyDurationSeconds
                + this.mAfterSeconds;
    }

    public boolean isInWatch(long time) {
        if (this.mDutyId <= 0) {
            return Util.isSameDay(getDayInSeconds(), time);
        } else {
            return time >= getRealWatchBeginSeconds()
                    && time < getRealWatchEndSeconds();
        }
    }

    public static Comparator<Watch> createCompareByDate() {
        return new Comparator<Watch>() {

            @Override
            public int compare(Watch left, Watch right) {
                if (left.mDayInSeconds < right.mDayInSeconds)
                    return -1;
                else if (left.mDayInSeconds > right.mDayInSeconds)
                    return 1;
                else
                    return 0;
            }

        };
    }

    public static final Parcelable.Creator<Watch> CREATOR = new Creator<Watch>() {

        @Override
        public Watch createFromParcel(Parcel source) {
            int id = source.readInt();
            int dutyId = source.readInt();
            long dayInSeconds = source.readLong();
            int dutyDurationSeconds = source.readInt();
            int beforeSeconds = source.readInt();
            int afterSeconds = source.readInt();
            int alarmStopped = source.readInt();
            long alarmPausedInSeconds = source.readLong();

            return new Watch(id, dutyId, dayInSeconds, dutyDurationSeconds,
                    beforeSeconds, afterSeconds, alarmStopped,
                    alarmPausedInSeconds);
        }

        @Override
        public Watch[] newArray(int size) {
            return new Watch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mDutyId);
        dest.writeLong(mDayInSeconds);
        dest.writeInt(mDutyDurationSeconds);
        dest.writeInt(mBeforeSeconds);
        dest.writeInt(mAfterSeconds);
        dest.writeInt(mAlarmStopped);
        dest.writeLong(mAlarmPasuedInSeconds);
    }

}
