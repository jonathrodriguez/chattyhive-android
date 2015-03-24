package com.chattyhive.Core.Util.Data;

import java.lang.reflect.Array;

/**
 * Created by Jonathan on 14/03/2015.
 */
public class DataTable {

    protected  final DataColumnCollection columnCollection;
    protected  final DataRowCollection rowCollection;
    protected long nextRowID;


    public DataTable() {
        this.nextRowID = 1L;
        this.columnCollection = new DataColumnCollection(this);
        this.rowCollection = new DataRowCollection(this);
    }

    public void clear() {
        this.clear(true);
    }
    protected void clear(boolean clearAll) {
        if (clearAll)
            this.Columns().clear();
        else
            this.Rows().clear();
    }

    public DataRow NewRow() {
        DataRow row = new DataRow(this,this.nextRowID++);
        return row;
    }

    public DataColumnCollection Columns() {
        return this.columnCollection;
    }
    public DataColumn Columns(int index) {
        return this.columnCollection.get(index);
    }


    public DataRowCollection Rows() {
        return this.rowCollection;
    }
    public DataRow Rows(int index) {
        return this.rowCollection.get(index);
    }

}

