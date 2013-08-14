package org.bxmy.shiftclock.db;

import java.util.ArrayList;

import org.bxmy.shiftclock.shiftduty.Watch;

import android.content.ContentValues;
import android.database.Cursor;

public class WatchTable extends DBHelper.ITableBase {

    private String mTableName = "watch";

    @Override
    public String getTableName() {
        return mTableName;
    }

    @Override
    public String getCreateSQL() {
        return "create table " + this.mTableName
                + " (_id integer primary key autoincrement, "
                + " dutyid integer not null, " + " day bigint not null, "
                + " before integer not null, " + " after integer not null) ";
    }

    @Override
    public String[] getAllFields() {
        return new String[] { "_id", "dutyid", "day", "before", "after" };
    }

    public ArrayList<Watch> selectAll() {
        ArrayList<Watch> watches = new ArrayList<Watch>();
        if (mDb != null) {
            Cursor cursor = mDb.cursorListAll(this);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                int dutyId = cursor.getInt(1);
                long day = cursor.getLong(2);
                int before = cursor.getInt(3);
                int after = cursor.getInt(4);

                try {
                    Watch watch = new Watch(id, dutyId, day, before, after);
                    watches.add(watch);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                cursor.moveToNext();
            }
        }

        return watches;
    }

    public Watch insert(int dutyId, long dayInSeconds, int beforeSeconds,
            int afterSeconds) {
        ContentValues values = new ContentValues();
        values.put("dutyid", dutyId);
        values.put("day", dayInSeconds);
        values.put("before", beforeSeconds);
        values.put("after", afterSeconds);

        int id = this.mDb.insert(this, values);
        return new Watch(id, dutyId, dayInSeconds, beforeSeconds, afterSeconds);
    }

    public void update(Watch watch) {
        ContentValues values = new ContentValues();
        values.put("dutyid", watch.getDutyId());
        values.put("day", watch.getDayInSeconds());
        values.put("before", watch.getBeforeSeconds());
        values.put("after", watch.getAfterSeconds());

        String where = "_id=?";
        String[] whereArgs = new String[] { String.valueOf(watch.getId()) };
        this.mDb.update(this, values, where, whereArgs);
    }
}