package org.bxmy.shiftclock.shiftduty;

public interface IShiftDutyEvent {

    public void onSetTimer(long timeInSeconds, boolean rtcWakeup, int timerId);

    public void onAlarm(Alarm alarm);

    public void onFutureWatchHint(int dayId);

}
