package org.bxmy.shiftclock.db;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class ConfigTable extends DBHelper.ITableBase {

    private String mTableName = "config";

    @Override
    public void onUpgrade(int oldVersion, int newVersion) {
        final int CURRENT_VERSION = 3;
        ArrayList<Entry<String, String>> nameValues = null;
        try {
            if (oldVersion < CURRENT_VERSION)
                nameValues = upgradeFrom(oldVersion);
            else
                nameValues = upgradeFrom(CURRENT_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDb.recreateTable(this);
        if (nameValues != null)
            rebuildTable(nameValues);
    }

    @Override
    public String getTableName() {
        return mTableName;
    }

    @Override
    public String getCreateSQL() {
        return "create table " + getTableName() + " (name text primary key, "
                + "value text not null)";
    }

    @Override
    public String[] getAllFields() {
        return new String[] { "name", "value" };
    }

    public String getByName(String name, String defaultValue) {
        if (TextUtils.isEmpty(name))
            return defaultValue;

        if (mDb != null) {
            String where = "name=?";
            String[] whereArgs = new String[] { name };
            Cursor cursor = mDb.select(this, where, whereArgs);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String n = cursor.getString(0);
                if (name.equalsIgnoreCase(n)) {
                    return cursor.getString(1);
                }

                cursor.moveToNext();
            }
        }

        return defaultValue;
    }

    public int getIntByName(String name, int defaultValue) {
        String value = getByName(name, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public boolean setByName(String name, String value) {
        if (TextUtils.isEmpty(name))
            return false;

        if (mDb != null) {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("value", value);
            return mDb.replace(this, values) != -1;
        }

        return false;
    }

    private void rebuildTable(ArrayList<Entry<String, String>> nameValues) {
        ArrayList<ContentValues> listValues = new ArrayList<ContentValues>();
        for (Entry<String, String> entry : nameValues) {
            String name = entry.getKey();
            String value = entry.getValue();

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("value", value);

            listValues.add(values);
        }

        mDb.rebuildTable(this, listValues);
    }

    private ArrayList<Entry<String, String>> upgradeFrom(int oldVersion) {
        // list from newly version to oldest version
        if (oldVersion >= 0x70000000) {
            return null;
        } else if (oldVersion >= 3) {
            String[] fields = new String[] { "name", // text primary key
                    "value", // text
            };

            DBHelper.IDbObjectCreator<Entry<String, String>> creator = new DBHelper.IDbObjectCreator<Entry<String, String>>() {

                @Override
                public Entry<String, String> create(Cursor cursor) {
                    String name = cursor.getString(0);
                    String value = cursor.getString(1);

                    return new AbstractMap.SimpleEntry<String, String>(name,
                            value);
                }
            };

            return mDb.new UpgradeHelper<Entry<String, String>>(getTableName(),
                    fields, creator).selectAll();
        } else {
            return null;
        }
    }
}
