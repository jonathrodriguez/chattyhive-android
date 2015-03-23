package com.chattyhive.Core.Util.Data;

/**
 * Created by Jonathan on 18/03/2015.
 */

public class DataColumn {
    private DataTable table;
    private String columnName;
    private int hashCode;
    private int ordinal;

    private String caption;
    private Class<?> dataType = Object.class;
    private Object defaultValue = null;
    private boolean allowsNull = true;

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

    public String Caption() {
        return this.caption;
    }
    public void Caption(String value) {
        this.caption = value;
    }

    public String ColumnName() {
        return this.columnName;
    }
    protected int HashCode() {
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

