package org.bxmy.shiftclock.db;

import java.util.ArrayList;

import org.bxmy.shiftclock.shiftduty.Duty;

import android.content.ContentValues;
import android.database.Cursor;

public class DutyTable extends DBHelper.ITableBase {

    private String mTableName = "duty";

    public DutyTable() {
    }

    @Override
    public String getCreateSQL() {
        return "create table " + this.mTableName
                + " (_id integer primary key autoincrement, "
                + " name text not null, " + " start integer not null, "
                + " duration integer not null, " + " alarmBefore integer) ";
    }

    @Override
    public String getTableName() {
        return this.mTableName;
    }

    @Override
    public String[] getAllFields() {
        return new String[] { "_id", "name", "start", "duration", "alarmBefore" };
    }

    public ArrayList<Duty> selectAll() {
        ArrayList<Duty> duties = new ArrayList<Duty>();
        if (mDb != null) {
            Cursor cursor = mDb.cursorListAll(this);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int start = cursor.getInt(2);
                int duration = cursor.getInt(3);
                int alarmBefore = cursor.getInt(4);
                try {
                    Duty duty = new Duty(id, name, start, duration, alarmBefore);
                    duties.add(duty);
                } catch (Exception e) {

                }

                cursor.moveToNext();
            }
        }

        return duties;
    }

    public Duty insert(String name, int startSecondsInDay, int durationSeconds,
            int alarmBeforeSeconds) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("start", startSecondsInDay);
        values.put("duration", durationSeconds);
        values.put("alarmBefore", alarmBeforeSeconds);
        int id = this.mDb.insert(this, values);
        return new Duty(id, name, startSecondsInDay, durationSeconds,
                alarmBeforeSeconds);
    }
}
