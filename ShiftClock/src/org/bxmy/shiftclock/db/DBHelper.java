package org.bxmy.shiftclock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDb;

    protected String mDbName;

    private boolean isNewDB;

    public DBHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
        this.mDbName = dbName;
        this.mDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        isNewDB = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级
    }

    public static abstract class ITableBase {
        protected DBHelper mDb;

        public void bind(DBHelper db) {
            this.mDb = db;
        }

        public abstract String getTableName();

        public abstract String getCreateSQL();

        public abstract String[] getAllFields();
    }

    public void addTable(ITableBase table) {
        table.bind(this);
        if (isNewDB)
            this.mDb.execSQL(table.getCreateSQL());
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

    public int insert(ITableBase table, ContentValues values) {
        return (int) this.mDb.insert(table.getTableName(), null, values);
    }
}
