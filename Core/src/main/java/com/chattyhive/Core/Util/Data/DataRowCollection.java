package com.chattyhive.Core.Util.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jonathan on 18/03/2015.
 */
public final class DataRowCollection extends InternalDataCollectionBase<DataRow> {
    // Fields
    private final ArrayList<DataRow> list = new ArrayList<DataRow>();
    int nullInList;
    private final DataTable table;

    // Methods
    DataRowCollection(DataTable table) {
        this.table = table;
    }

    public DataRow Add(Object... values) {
        int record = this.table.NewRecordFromArray(values);
        DataRow row = this.table.NewRow(record);
        this.table.AddRow(row, -1);
        return row;
    }

    @Override
    public boolean contains(Object row) {
        boolean result;
        result = !(((row == null) || (!(row instanceof DataRow)) || (((DataRow) row).Table() != this.table)) || (-1L == ((DataRow) row).rowID())) && this.list.contains(row);
        return result;
    }

    @Override
    public boolean add(DataRow row) {
        int iSize = this.list.size();
        this.table.AddRow(row, -1);
        return iSize != this.list.size();
    }

    DataRow AddWithColumnEvents(Object... values) throws NoSuchFieldException {
        DataRow row = this.table.NewRow(-1);
        row.ItemArray(values);
        this.table.AddRow(row, -1);
        return row;
    }

    void ArrayAdd(DataRow row) {
        this.list.add(row);
    }

    void ArrayClear() {
        this.list.clear();
    }

    void ArrayInsert(DataRow row, int pos) {
        this.list.add(pos, row);
    }

    void ArrayRemove(DataRow row) {
        this.list.remove(row);
    }

    @Override
    public void clear() {
        this.table.Clear(false);
    }

    public void CopyTo(DataRow[] array, int index) {
        if (array == null) {
            throw new IllegalArgumentException("Array can not be null");
        }
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if ((array.length - index) < this.list.size()) {
            throw new IllegalArgumentException("Array is too small to contain list at specified index");
        }
        for (int i = 0; i < this.list.size(); i++) {
            array[index + i] = this.list.get(i);
        }
    }

    void DiffInsertAt(DataRow row, int pos) {
        if ((pos < 0) || (pos == this.list.size())) {
            this.table.AddRow(row, (pos > -1) ? (pos + 1) : -1);
        } else {
            this.table.InsertRow(row, pos + 1, (pos > this.list.size()) ? -1 : pos);
        }
    }

    public int IndexOf(DataRow row) {
        if ((row != null) && (row.Table() == this.table)) {
            return this.list.indexOf(row);
        }
        return -1;
    }

    public void InsertAt(DataRow row, int pos) {
        if (pos < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (pos >= this.list.size()) {
            this.table.AddRow(row, -1);
        } else {
            this.table.InsertRow(row, -1, pos);
        }
    }

    @Override
    public boolean remove(Object row) {
        int iSize = this.list.size();
        if (((row == null) || (!(row instanceof DataRow)) || (((DataRow)row).Table() != this.table)) || (-1L == ((DataRow)row).rowID())) {
            throw new IllegalArgumentException("Row can not be deleted.");
        }
        ((DataRow)row).Delete();
        return iSize != this.list.size();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            try {
                if (!this.contains(o))
                    return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends DataRow> c) {
        int iSize = this.list.size();

        for (DataRow dr : c) {
            try {
                this.add(dr);
            } catch (Exception e) { }
        }

        return iSize != this.list.size();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int iSize = this.list.size();

        for (Object o : c) {
            try {
                this.remove(o);
            } catch (Exception e) { }
        }

        return iSize != this.list.size();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        int iSize = this.list.size();

        for (DataRow dr : this.list) {
            try {
                if (!c.contains(dr))
                    this.remove(dr);
            } catch (Exception e) { }
        }

        return iSize != this.list.size();
    }

    public void RemoveAt(int index) {
        this.remove(this.get(index));
    }

// Properties
    public DataRow get(int index) {
        return this.list.get(index);
    }
}


