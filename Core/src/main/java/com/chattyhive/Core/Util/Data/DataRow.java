package com.chattyhive.Core.Util.Data;

import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;

/**
 * Created by Jonathan on 18/03/2015.
 */
public class DataRow implements Comparable<DataRow> {
    // Fields
    private DataTable table;
    private long rowID;
    private Object[] values;

    // Constructors
    public DataRow(DataTable table) {
        this.values = new Object[table.Columns().size()];
        this.table = table;
    }
    protected DataRow(DataTable table, long rowID) {
        this(table);
        this.rowID = rowID;
    }

    // Methods
    @Override
    public int compareTo(DataRow o) {
        Object[] oValues = o.ItemArray();
        if (oValues.length != values.length)
            throw new UnsupportedOperationException("Rows are not comparable since don't have the same number of columns");

        for (int i = 0; i < oValues.length; i++) {
            DataColumn thisColumn = this.table.Columns(i);
            DataColumn oColumn = o.table.Columns(i);
            if (thisColumn.DataType() != oColumn.DataType())
                throw new UnsupportedOperationException("Rows are not comparable since column types does not match");

            int res;
            if (((oValues[i] instanceof Comparable) && (this.values[i] instanceof Comparable)))
                res = ((Comparable)oValues[i]).compareTo(this.values[i]);
            else
                res = ((Integer)oValues[i].hashCode()).compareTo(this.values[i].hashCode());

            if (res != 0)
                return res;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof DataRow) && (this.compareTo((DataRow)o) == 0));
    }

    protected void TableColumnsChanged(int newIndex, int oldIndex) {
        if (oldIndex == -1) { //New Column
            Object[] newValues = new Object[this.values.length + 1];
            for (int i=0;i<newValues.length;i++) {
                if (i < newIndex)
                    newValues[i] = this.values[i];
                else if (i == newIndex)
                    newValues[i] = this.table.Columns().get(newIndex).DefaultValue();
                else
                    newValues[i] = this.values[i-1];
            }
            this.values = newValues;
        } else if (newIndex == -1) { //Column deleted
            Object[] newValues = new Object[this.values.length - 1];
            for (int i=0;i<this.values.length;i++) {
                if (i < oldIndex)
                    newValues[i] = this.values[i];
                else if (i > oldIndex)
                    newValues[i-1] = this.values[i];
            }
            this.values = newValues;
        } else { //Column moved
            Object tmpVal = this.values[oldIndex];
            this.values[oldIndex] = this.values[newIndex];
            this.values[newIndex] = tmpVal;
        }
    }

    // Properties
    public DataTable Table() {
        return this.table;
    }
    protected void Table(DataTable value) {
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
        if ((this.table != null) && (this.table.Columns() != null) && (values.length > this.table.Columns().size()))
            throw new ArrayStoreException("Can not set more values than table columns count");
        else {
            if (this.values.length < this.table.Columns().size())
                this.values = new Object[this.table.Columns().size()];

            int i=0;
            for (DataColumn column : this.table.Columns()) {
                if ((i < values.length) && (column.DataType().isInstance(values[i]))) {
                    if ((values[i] == null) && (!column.AllowsNull()) && (column.DefaultValue() == null))
                        throw new NullPointerException(String.format("Column %s does not allow null values",column.ColumnName()));
                    else if ((values[i] == null) && (!column.AllowsNull()) && (column.DefaultValue() != null))
                        this.values[column.Index()] = column.DefaultValue();
                    else if (((values[i] == null) && (column.AllowsNull())) || (values[i] != null))
                        this.values[column.Index()] = values[i];
                    i++;
                } else {
                    if ((column.DefaultValue() == null) && (!column.AllowsNull()))
                        throw new NullPointerException(String.format("Column %s does not allow null values",column.ColumnName()));
                    this.values[column.Index()] = column.DefaultValue();
                }
            }
        }
    }

    public Object get(int index) {
        return this.values[index];
    }
    public void set(int index, Object value) {
        if (!(this.table.Columns().get(index).DataType().isInstance(value)))
            throw new ClassCastException(String.format("Value type is not compatible. Expected type: %s",this.table.Columns().get(index).DataType().getCanonicalName()));
        this.values[index] = value;
    }

    public Object get(String columnName) {
        return this.get(this.table.Columns().get(columnName).Index());
    }
    public void set(String columnName, Object value) {
        this.set(this.table.Columns().get(columnName).Index(), value);
    }
}

