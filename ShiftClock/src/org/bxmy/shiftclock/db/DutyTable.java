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
    public void onUpgrade(int oldVersion, int newVersion) {
        final int CURRENT_VERSION = 1;
        if (oldVersion <= CURRENT_VERSION) {
            ArrayList<Duty> duties = upgradeFrom(oldVersion);
            mDb.recreateTable(this);

            if (duties != null)
                rebuildTable(duties);
        }
    }

    @Override
    public String getCreateSQL() {
        return "create table " + getTableName()
                + " (_id integer primary key autoincrement, "
                + " name text not null, " + " start integer not null, "
                + " duration integer not null, " + " alarmBefore integer)";
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
                    e.printStackTrace();
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

    public void update(Duty duty) {
        ContentValues values = new ContentValues();
        values.put("name", duty.getName());
        values.put("start", duty.getStartSecondsInDay());
        values.put("duration", duty.getDurationSeconds());
        values.put("alarmBefore", duty.getAlarmBeforeSeconds());

        String where = "_id=?";
        String[] whereArgs = new String[] { String.valueOf(duty.getId()) };
        this.mDb.update(this, values, where, whereArgs);
    }

    private void rebuildTable(ArrayList<Duty> duties) {
        ArrayList<ContentValues> dutyValues = new ArrayList<ContentValues>();
        for (int i = 0; i < duties.size(); ++i) {
            Duty duty = duties.get(i);

            ContentValues values = new ContentValues();
            values.put("_id", duty.getId());
            values.put("name", duty.getName());
            values.put("start", duty.getStartSecondsInDay());
            values.put("duration", duty.getDurationSeconds());
            values.put("alarmBefore", duty.getAlarmBeforeSeconds());

            dutyValues.add(values);
        }

        mDb.rebuildTable(this, dutyValues);
    }

    private ArrayList<Duty> upgradeFrom(int oldVersion) {
        if (oldVersion >= 0x70000000) {
            return null;
        } else if (oldVersion >= 1) {
            return new DBHelper.ITableBase() {

                @Override
                public void onUpgrade(int oldVersion, int newVersion) {
                }

                @Override
                public String getTableName() {
                    return DutyTable.this.getTableName();
                }

                @Override
                public String getCreateSQL() {
                    return "create table " + getTableName()
                            + " (_id integer primary key autoincrement, "
                            + " name text not null, "
                            + " start integer not null, "
                            + " duration integer not null, "
                            + " alarmBefore integer)";
                }

                @Override
                public String[] getAllFields() {
                    return new String[] { "_id", "name", "start", "duration",
                            "alarmBefore" };
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
                                Duty duty = new Duty(id, name, start, duration,
                                        alarmBefore);
                                duties.add(duty);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            cursor.moveToNext();
                        }
                    }

                    return duties;
                }
            }.selectAll();
        } else {
            return null;
        }
    }
}
