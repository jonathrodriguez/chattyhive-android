package com.chattyhive.chattyhive.framework.OSStorageProvider.SQLiteDataBases.StandAloneServer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chattyhive.Core.ContentProvider.OSStorageProvider.LocalDataBaseInterface;
import com.chattyhive.Core.Util.Data.DataRow;
import com.chattyhive.Core.Util.Data.DataTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 07/04/2015.
 */
public class StandAloneServerDataBase implements LocalDataBaseInterface {
    private StandAloneServerDataBaseHelper dbHelper;

    public StandAloneServerDataBase(Context context) {
        this.dbHelper = new StandAloneServerDataBaseHelper(context,"StandAloneServerDB",null,3);
    }

    @Override
    public void executeSQL(String sql) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(db != null) {
            db.execSQL(sql);
            db.close();
        }
    }

    @Override
    public DataTable tableQuerySQL(String sql) {
        DataTable result = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        if(db != null) {
            Cursor cursor = db.rawQuery(sql,null);

            //Convert cursor into DataTable
            if ((cursor.getColumnCount() > 0) && (cursor.getCount() > 0)) {
                result = new DataTable();
                String[] colNames = cursor.getColumnNames();
                for (String colName : colNames)
                    result.Columns().add(colName);

                while (cursor.moveToNext()) {
                    DataRow row = result.NewRow();
                    for (String colName : colNames)
                        /*if (cursor.isNull(cursor.getColumnIndex(colName)))
                            row.set(colName,null);
                        else*/
                        row.set(colName,cursor.getString(cursor.getColumnIndex(colName)));
                    result.Rows().add(row);
                }
            }

            db.close();
        }

        return result;
    }

    @Override
    public Object simpleQuerySQL(String sql) {
        Object result = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        if(db != null) {
            Cursor cursor = db.rawQuery(sql,null);

            //Convert cursor into DataTable
            if ((cursor.getColumnCount() > 0) && (cursor.getCount() > 0)) {
                if ((cursor.moveToFirst()) && (!cursor.isNull(0))) {
                    result = cursor.getString(0);
                }
            }

            db.close();
        }

        return result;
    }

    @Override
    public void beginTransaction(TransactionMode transactionMode) { }

    @Override
    public void commitTransaction() { }

    @Override
    public void finishTransaction() { }

    public List<String> getTables() {
        ArrayList<String> tableNames = new ArrayList<String>();

        DataTable tables = this.tableQuerySQL("SELECT * FROM sqlite_master WHERE type='table'");

        for (DataRow row : tables.Rows())
            tableNames.add(row.get("name").toString());

        return tableNames;
    }
}
