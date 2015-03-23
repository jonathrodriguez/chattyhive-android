package com.chattyhive.Core.Util.Data;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by Jonathan on 18/03/2015.
 */
public final class DataColumnCollection extends InternalDataCollectionBase<DataColumn> {

    private final TreeMap<Integer, DataColumn> columnFromName;
    private int defaultNameIndex = 0;
    private final DataTable table;

    protected DataColumnCollection(DataTable table) {
        this.table = table;
        this.columnFromName = new TreeMap<Integer, DataColumn>();
    }

    @Override
    public boolean add(DataColumn column) {
        return this.add(this.size(), column);
    }
    public boolean add(int index, DataColumn column) {
        if ((column.Table() != null) && (column.Table() != this.table))
            throw new UnsupportedOperationException("Column already belongs to another table");

        int ISize = this.size();

        column.Index(index);
        this.List().add(index,column);

        if ((column.ColumnName() == null) || (column.ColumnName().isEmpty()))
            column.ColumnName(this.AssignName());

        this.RegisterColumnName(column);

        column.Table(this.table);

        if (index < ISize) {
            for (int i = index + 1; i < this.size(); i++) {
                for (DataRow row : this.table.Rows())
                    row.TableColumnsChanged(i,i-1);
                this.List().get(i).Index(i);
            }
        }
        return (ISize > this.size());
    }
    public DataColumn add(String columnName) {
        DataColumn column = new DataColumn(columnName);
        this.add(column);
        return column;
    }
    public DataColumn add(String columnName, Class<?> type) {
        DataColumn column = new DataColumn(columnName, type);
        this.add(column);
        return column;
    }

    private String AssignName() {
        String key = this.MakeName(this.defaultNameIndex++);
        while (this.columnFromName.containsKey(key)) {
            key = this.MakeName(this.defaultNameIndex++);
        }
        return key;
    }

    @Override
    public void clear() {
        int count = this.size();

        for (DataRow row : this.table.Rows()) {
            for (int i = 0; i < count; i++) {
                row.TableColumnsChanged(-1,i);
            }
        }

        this.List().clear();
        this.columnFromName.clear();
    }

    public boolean contains(String name) {
        DataColumn column = null;
        int nameHash = name.toLowerCase().hashCode();
        return this.columnFromName.containsKey(nameHash);
    }

    public int indexOf(DataColumn column) {
        if (this.List().contains(column))
            return this.List().indexOf(column);
        else
            return -1;
    }
    public int indexOf(String columnName) {
        if ((columnName != null) && (!columnName.isEmpty())) {
            DataColumn column = null;
            int columnNameHash = columnName.toLowerCase().hashCode();
            if (this.columnFromName.containsKey(columnNameHash))
                column = this.columnFromName.get(columnNameHash);
            else
                return -1;

            return this.indexOf(column);
        }
        return -1;
    }

    private String MakeName(int index) {
        return String.format("Column%d",index);
    }

    public void moveTo(DataColumn column, int newPosition) {
        if ((0 > newPosition) || (newPosition > (this.size() - 1))) {
            throw new ArrayIndexOutOfBoundsException(newPosition);
        }
        this.List().remove(column);
        this.List().add(newPosition, column);
        this.updateOrdinals();
    }

    protected void RegisterColumnName (DataColumn column) {
        if (column == null) {
            throw new NullPointerException("Column can not be null");
        }
        if (this.columnFromName.containsKey(column.HashCode())) {
            throw new IllegalArgumentException(String.format("Table already has a column named: %s",column.ColumnName()));
        }
        if (this.columnFromName.values().contains(column)) {
            throw new IllegalArgumentException("Table already the specified column");
        }

        this.columnFromName.put(column.HashCode(),column);
    }

    @Override
    public boolean remove(Object column) {
        if (!(column instanceof DataColumn))
            throw new ClassCastException("column must be a DataColumn");

        int iSize = this.size();

        this.columnFromName.remove(((DataColumn) column).HashCode());
        this.List().remove(column);

        for (DataRow row : this.table.Rows()) {
            row.TableColumnsChanged(-1,((DataColumn) column).Index());
        }

        return iSize != this.size();
    }
    public void remove(String name) {
        DataColumn column = this.get(name);
        if (column == null)
            throw new IllegalArgumentException(String.format("Table has not column named: %s",name));

        this.remove(column);
    }
    public void remove(int index) {
        DataColumn column = this.get(index);
        if (column == null)
            throw new ArrayIndexOutOfBoundsException(index);

        this.remove(column);
    }

    protected void UnregisterColumnName(DataColumn column) {
        if (column == null) {
            throw new NullPointerException("Column can not be null");
        }
        if (!this.columnFromName.containsKey(column.HashCode())) {
            throw new IllegalArgumentException(String.format("Column %s is not present.",column.ColumnName()));
        }
        if (!this.columnFromName.values().contains(column)) {
            throw new IllegalArgumentException("Table does not contain the specified column");
        }

        this.columnFromName.remove(column.HashCode());
    }

    private void updateOrdinals() {
        for (int i = 0; i < this.size(); i++) {
            for (DataRow row : this.table.Rows())
                row.TableColumnsChanged(i,this.get(i).Index());
            this.get(i).Index(i);
        }
    }

    public DataColumn get(int index) {
        DataColumn column;
        column = this.List().get(index);
        return column;
    }

    public DataColumn get(String name) {
        DataColumn column = null;
        if (name == null)
            throw new NullPointerException("Argument name can not be null");
        else if (name.isEmpty())
            throw new IllegalArgumentException("Argument name can not be empty");

        int nameHash = name.toLowerCase().hashCode();

        if (this.columnFromName.containsKey(nameHash))
            column = this.columnFromName.get(nameHash);

        return column;
    }

}