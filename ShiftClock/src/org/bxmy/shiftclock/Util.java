package org.bxmy.shiftclock;

import java.util.Date;

import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

public final class Util {

    public static void updateTime(TimePicker picker, int seconds) {
        picker.setCurrentHour(seconds / 3600 % 24);
        picker.setCurrentMinute(seconds / 60 % 60);
    }

    public static int getTime(TimePicker picker) {
        return picker.getCurrentHour() * 3600 + picker.getCurrentMinute() * 60;
    }

    public static void updateDate(DatePicker picker, long dayInSeconds,
            DatePicker.OnDateChangedListener changed) {
        Date date = secondsToDate(dayInSeconds);
        picker.init(date.getYear() + 1900, date.getMonth(), date.getDate(),
                changed);
    }

    public static long getDate(DatePicker picker) {
        int year = picker.getYear();
        int month = picker.getMonth();
        int date = picker.getDayOfMonth();
        return new Date(year - 1900, month, date).getTime() / 1000;
    }

    public static int getCurrentYear() {
        Date date = new Date();
        return date.getYear() + 1900;
    }

    public static long getDateOfTimeInSeconds(long timeInSeconds) {
        Date date = secondsToDate(timeInSeconds);
        return dateToSeconds(new Date(date.getYear(), date.getMonth(),
                date.getDate()));
    }

    public static long getDateInSeconds(Date date) {
        return dateToSeconds(new Date(date.getYear(), date.getMonth(),
                date.getDate()));
    }

    public static int getDayIdOfTime(long timeInSeconds) {
        Date time = secondsToDate(timeInSeconds);
        long utcDate = Date.UTC(time.getYear(), time.getMonth(),
                time.getDate(), 0, 0, 0);
        utcDate = dateToSeconds(new Date(utcDate));

        int dayId = (int) (utcDate / 86400);
        Date check = secondsToDate(getTimeOfDayId(dayId));
        if (check.getDate() != time.getDate())
            Log.e("shiftclock",
                    time.toString() + "   got day id  " + check.toString());

        return dayId;
    }

    public static long getTimeOfDayId(int dayId) {
        return dayId * 86400L + new Date().getTimezoneOffset() * 60;
    }

    public static String formatTimeIn24Hours(long seconds) {
        return String.format("%02d:%02d", seconds / 3600 % 24,
                seconds / 60 % 60);
    }

    public static String formatHourMinute(Date time) {
        return String.format("%02d:%02d", time.getHours(), time.getMinutes());
    }

    public static String formatMonthDate(Date date) {
        return String.format("%d-%d", date.getMonth() + 1, date.getDate());
    }

    public static String formatMonth2Minute(Date time) {
        return formatMonthDate(time) + " " + formatHourMinute(time);
    }

    public static String formatYear2Date(Date date) {
        return String.format("%04d-%d-%d", date.getYear() + 1900,
                date.getMonth() + 1, date.getDate());
    }

    public static String formatYear2Minute(Date time) {
        return formatYear2Date(time) + " " + formatHourMinute(time);
    }

    public static Date secondsToDate(long seconds) {
        return new Date(seconds * 1000);
    }

    public static long now() {
        return dateToSeconds(new Date());
    }

    public static long dateToSeconds(Date time) {
        return time.getTime() / 1000;
    }

    public static boolean isSameDay(long first, long second) {
        return isSameDay(secondsToDate(first), secondsToDate(second));
    }

    public static boolean isSameDay(Date first, Date second) {
        return first.getYear() == second.getYear()
                && first.getMonth() == second.getMonth()
                && first.getDate() == second.getDate();
    }

    public static boolean isSameYear(Date first, Date second) {
        return first.getYear() == second.getYear();
    }

    public static String formatDate(long dayInSeconds) {
        Date now = new Date();
        Date date = secondsToDate(dayInSeconds);
        if (now.getYear() == date.getYear()) {
            return formatMonthDate(date);
        } else {
            return formatYear2Date(date);
        }
    }

    public static String formatWeek(long dayInSeconds) {
        Date date = secondsToDate(dayInSeconds);
        String[] weeks = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        return weeks[date.getDay() % 7];
    }

    public static String formatTimeByRelatived(long beginSeconds,
            int relativeSeconds) {
        return formatTimeByOther(beginSeconds, beginSeconds + relativeSeconds);
    }

    public static String formatTimeByOther(long firstTimeInSeconds,
            long otherTimeInSeconds) {
        Date begin = secondsToDate(firstTimeInSeconds);
        Date relative = secondsToDate(otherTimeInSeconds);
        if (isSameDay(begin, relative)) {
            return formatHourMinute(relative);
        } else if (isSameYear(begin, relative)) {
            return formatMonth2Minute(relative);
        } else {
            return formatYear2Minute(relative);
        }
    }

    public static String formatTimeToNow(long timeInSeconds) {
        long now = now();
        return formatTimeByOther(now, timeInSeconds);
    }

    public static String formatDateTimeToNow(long timeInSeconds) {
        Date now = new Date();
        Date t = secondsToDate(timeInSeconds);
        if (isSameYear(now, t))
            return formatMonth2Minute(t);
        else
            return formatYear2Minute(t);
    }

    public static String formatDateByDayId(int dayId) {
        return formatYear2Date(secondsToDate(getTimeOfDayId(dayId)));
    }
}
