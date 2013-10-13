package org.bxmy.shiftclock.notification;

import org.bxmy.shiftclock.Util;

import android.content.Intent;

public class NotificationFutureWatch {

    private static NotificationFutureWatch sSelf;

    private int mHintDayId = -1;

    public static synchronized NotificationFutureWatch getInstance() {
        if (sSelf == null)
            sSelf = new NotificationFutureWatch();

        return sSelf;
    }

    public void show(int dayId, Intent intent) {
        if (mHintDayId >= 0 && mHintDayId != dayId) {
            cancel();
        }

        mHintDayId = dayId;

        String date = Util.formatDateByDayId(dayId);
        String title = "设置 " + date + " 值班";
        NotificationHelper.getInstance().showHint(dayId, title,
                "还没有设置 " + date + " 的值班或休息", intent);
    }

    public void cancel() {
        if (mHintDayId >= 0) {
            NotificationHelper.getInstance().cancelHint(mHintDayId);
            mHintDayId = -1;
        }
    }
}
