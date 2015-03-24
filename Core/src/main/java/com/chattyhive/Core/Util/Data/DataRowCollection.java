package com.chattyhive.Core.Util.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jonathan on 18/03/2015.
 */
public final class DataRowCollection extends InternalDataCollectionBase<DataRow> {
    // Fields
    private final DataTable table;

    // Methods
    DataRowCollection(DataTable table) {
        super();
        this.table = table;
    }

    @Override
    public boolean add(DataRow row) {
        return this.add(this.size(), row);
    }
    public boolean add(int index, DataRow row) {
        if ((row.Table() != null) && (row.Table() != this.table))
            throw new UnsupportedOperationException("Row already belongs to another table");

        if ((row.Table() != null) && (row.Table() == this.table) && (this.List().contains(row))) {
            if (this.List().indexOf(row) != index) {
                this.moveTo(row,index);
            }
            return false;
        }

        int ISize = this.size();

        this.List().add(index, row);

        row.Table(this.table);

        return (ISize > this.size());
    }
    public DataRow add(Object... values) {
        DataRow row = this.table.NewRow();
        row.ItemArray(values);
        this.add(row);
        return row;
    }


    public int indexOf(DataRow row) {
        if ((row != null) && (row.Table() == this.table)) {
            return this.List().indexOf(row);
        }
        return -1;
    }

    public void moveTo(DataRow row, int newPosition) {
        if ((0 > newPosition) || (newPosition > (this.size() - 1))) {
            throw new ArrayIndexOutOfBoundsException(newPosition);
        }
        this.List().remove(row);
        this.List().add(newPosition, row);
    }


    public void remove(int index) {
        this.remove(this.get(index));
    }

// Properties
    public DataRow get(int index) {
        return this.List().get(index);
    }

    public Object get(int column, int row) {
        return this.List().get(row).ItemArray()[column];
    }
    public void set(int column, int row, Object value) {
        this.List().get(row).set(column,value);
    }

    public Object get(String columnName, int row) {
        DataColumn column = this.table.Columns().get(columnName);
        if (column != null)
            return this.List().get(row).ItemArray()[column.Index()];

        return null;
    }
    public void set(String columnName, int row, Object value) {
        this.List().get(row).set(columnName,value);
    }

}


