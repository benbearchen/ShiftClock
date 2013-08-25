package org.bxmy.shiftclock;

import android.widget.TimePicker;

public final class Util {

    public static void updateTime(TimePicker picker, int seconds) {
        picker.setCurrentHour(seconds / 3600 % 24);
        picker.setCurrentMinute(seconds / 60 % 60);
    }

    public static int getTime(TimePicker picker) {
        return picker.getCurrentHour() * 3600 + picker.getCurrentMinute() * 60;
    }

    public static String formatSecondsInDay(int seconds) {
        return String.format("%02d:%02d", seconds / 3600 % 24,
                seconds / 60 % 60);
    }
}
