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
    private Class<?> dataType;
    private Object defaultValue;
    private boolean allowsNull;

    public DataTable Table() {
        return this.table;
    }
}

