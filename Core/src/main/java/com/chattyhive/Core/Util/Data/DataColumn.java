package com.chattyhive.Core.Util.Data;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jonathan on 18/03/2015.
 */

public class DataColumn implements Comparable<DataColumn> {

    // Fields
    private DataTable table;
    private String columnName;
    private int hashCode;
    private int ordinal;

    private String caption;
    private Class<?> dataType = Object.class;
    private Object defaultValue = null;
    private boolean allowsNull = true;

    // Constructors
    public DataColumn() {
        this("",Object.class);
    }
    public DataColumn(String columnName) {
        this(columnName,Object.class);
    }
    public DataColumn(String columnName, Class<?> dataType) {
        this(columnName,columnName,dataType,true,null);
    }
    public DataColumn(String columnName, String caption, Class<?> dataType) {
        this(columnName,caption,dataType,true,null);
    }
    public DataColumn(String columnName, String caption, Class<?> dataType, boolean allowsNull, Object defaultValue) {
        if ((!allowsNull) && (defaultValue == null))
            throw new NullPointerException("Default value can not be null if column does not allow null values");

        this.ColumnName(columnName);
        this.caption = caption;

        this.dataType = dataType;
        this.allowsNull = allowsNull;
        this.defaultValue = defaultValue;
    }


    // Methods
    @Override
    public int compareTo(DataColumn o) {
        if (this.Table() != o.Table())
            throw new UnsupportedOperationException("Columns are not comparable since they belong to different tables");
        if (this.equals(o))
            return 0;
        else {
            return ((Integer)this.Index()).compareTo(o.Index());
        }
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof DataColumn) && (this.hashCode() == o.hashCode()) && (this.Table() == ((DataColumn) o).Table()));
    }

    public boolean isComparable() {
        Class[] inter = this.dataType.getInterfaces();
        if ((inter != null) && (inter.length > 0))
            return Arrays.asList(inter).contains(Comparable.class);
        return false;
    }
    // Properties

    public String Caption() {
        return this.caption;
    }
    public void Caption(String value) {
        this.caption = value;
    }

    public String ColumnName() {
        return this.columnName;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    protected void ColumnName(String value) {
        if (this.table != null)
            this.table.Columns().UnregisterColumnName(this);

        this.columnName = value;
        this.hashCode = value.toLowerCase().hashCode();

        if (this.table != null)
            this.table.Columns().RegisterColumnName(this);
    }

    public int Index() {
        return this.ordinal;
    }
    protected void Index(int value) {
        this.ordinal = value;
    }

    public Class DataType() {
        return this.dataType;
    }
    public void DataType(Class<?> value) {
        this.dataType = value;
    }

    public boolean AllowsNull() {
        return this.allowsNull;
    }
    public void AllowsNull(boolean value) {
        if (value != this.allowsNull) {
            if ((!value) && (this.defaultValue == null))
                throw new NullPointerException("Can not set NOT ALLOW NULL values while default value is null");
            else
                this.allowsNull = value;
        }
    }

    public Object DefaultValue() {
        return this.defaultValue;
    }
    public void DefaultValue(Object value) {
        if ((value == null) && (!this.allowsNull))
            throw new NullPointerException("Can not set default value to NULL since this column does not allow null values");
        else
            this.defaultValue = value;
    }

    public DataTable Table() {
        return this.table;
    }
    protected void Table(DataTable value) {
        this.table = value;
    }
}

