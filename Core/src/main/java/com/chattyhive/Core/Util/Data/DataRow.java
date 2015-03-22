package com.chattyhive.Core.Util.Data;

/**
 * Created by Jonathan on 18/03/2015.
 */
public class DataRow {
    private DataTable table;
    private long rowID;
    private Object[] values;

    private DataRow() {
           this.values = new Object[0];
    }
    private DataRow(DataTable table) {
        this.values = new Object[table.Columns().size()];
        this.table = table;
    }
    private DataRow(long rowID) {
        this();
        this.rowID = rowID;
    }
    private DataRow(DataTable table, long rowID) {
        this(table);
        this.rowID = rowID;
    }

    public DataTable Table() {
        return this.table;
    }
    private void Table(DataTable value) {
        if (this.table != value)
            this.table = value;
    }

    public long RowID() {
        return this.rowID;
    }
    private void RowID(long value){
        this.rowID = value;
    }

    public Object[] ItemArray() {
        return this.values;
    }
    public void ItemArray(Object... values) {
        this.values = values;
    }

    public Object get(int index) {
        return this.values[index];
    }
    public void set(int index, Object value) {
        this.values[index] = value;
    }
}

