/**
 * refer:
 * 
 * http://www.ibm.com/developerworks/cn/xml/x-androidstorage/
 * 
 */

package org.bxmy.shiftclock.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDb;

    protected String mDbName;

    private IDBEvent mDbEvent;

    private static final int DATABASE_VERSION = 3;

    private static ArrayList<ITableBase> sTables = new ArrayList<ITableBase>();

    public static DBHelper createInstance(Context context, String dbName,
            IDBEvent dbEvent) {
        return new DBHelper(context, dbName, dbEvent);
    }

    private DBHelper(Context context, String dbName, IDBEvent dbEvent) {
        super(context, dbName, null, DATABASE_VERSION);

        this.mDbName = dbName;
        this.mDbEvent = dbEvent;
        if (this.mDbEvent != null)
            this.mDbEvent.bind(this);

        for (int i = 0; i < sTables.size(); ++i) {
            ITableBase table = sTables.get(i);
            table.bind(this);
        }

        this.mDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.mDb = db;

        for (int i = 0; i < sTables.size(); ++i) {
            ITableBase table = sTables.get(i);
            db.execSQL(table.getCreateSQL());
        }

        if (this.mDbEvent != null)
            this.mDbEvent.onCreated();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.mDb = db;

        for (int i = 0; i < sTables.size(); ++i) {
            sTables.get(i).onUpgrade(oldVersion, newVersion);
        }

        if (this.mDbEvent != null)
            this.mDbEvent.onUpgraded(oldVersion, newVersion);
    }

    public static interface IDBEvent {

        public void bind(DBHelper db);

        public void onCreated();

        public void onUpgraded(int oldVersion, int newVersion);
    }

    public static abstract class ITableBase {
        protected DBHelper mDb;

        public void bind(DBHelper db) {
            this.mDb = db;
        }

        public abstract void onUpgrade(int oldVersion, int newVersion);

        public abstract String getTableName();

        public abstract String getCreateSQL();

        public abstract String[] getAllFields();
    }

    public static interface IDbObjectCreator<T> {
        public T create(Cursor cursor);
    }

    public class UpgradeHelper<T> {

        private String mTableName;

        private String[] mFields;

        private IDbObjectCreator<T> mCreator;

        public UpgradeHelper(String tableName, String[] fields,
                IDbObjectCreator<T> creator) {
            this.mTableName = tableName;
            this.mFields = fields;
            this.mCreator = creator;
        }

        public ArrayList<T> selectAll() {
            ArrayList<T> objects = new ArrayList<T>();
            if (mDb != null) {
                Cursor cursor = mDb.query(mTableName, // Table Name
                        mFields, // Columns to return
                        null, // SQL WHERE
                        null, // Selection Args
                        null, // SQL GROUP BY
                        null, // SQL HAVING
                        mFields[0]); // SQL ORDER BY
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    try {
                        objects.add(this.mCreator.create(cursor));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    cursor.moveToNext();
                }
            }

            return objects;
        }
    }

    public static void addTable(ITableBase table) {
        sTables.add(table);
    }

    public Cursor cursorListAll(ITableBase table) {
        String tableName = table.getTableName();
        String[] fields = table.getAllFields();
        Cursor cursor = this.mDb.query(tableName, // Table Name
                fields, // Columns to return
                null, // SQL WHERE
                null, // Selection Args
                null, // SQL GROUP BY
                null, // SQL HAVING
                fields[0]); // SQL ORDER BY
        return cursor;
    }

    public Cursor select(ITableBase table, String where, String[] whereArgs) {
        String tableName = table.getTableName();
        String[] fields = table.getAllFields();
        Cursor cursor = this.mDb.query(tableName, // Table Name
                fields, // Columns to return
                where, // SQL WHERE
                whereArgs, // Selection Args
                null, // SQL GROUP BY
                null, // SQL HAVING
                fields[0]); // SQL ORDER BY
        return cursor;
    }

    public int insert(ITableBase table, ContentValues values) {
        return (int) this.mDb.insert(table.getTableName(), null, values);
    }

    public int update(ITableBase table, ContentValues values, String where,
            String[] whereArgs) {
        return this.mDb.update(table.getTableName(), values, where, whereArgs);
    }

    public int replace(ITableBase table, ContentValues values) {
        return (int) this.mDb.replace(table.getTableName(), null, values);
    }

    public void recreateTable(ITableBase table) {
        this.mDb.execSQL("drop table if exists " + table.getTableName());
        this.mDb.execSQL(table.getCreateSQL());
    }

    public void rebuildTable(ITableBase table, ArrayList<ContentValues> values) {
        this.mDb.beginTransaction();

        for (int i = 0; i < values.size(); ++i) {
            insert(table, values.get(i));
        }

        this.mDb.setTransactionSuccessful();
        this.mDb.endTransaction();
    }
}
