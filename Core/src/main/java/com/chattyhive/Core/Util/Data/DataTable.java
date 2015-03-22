package com.chattyhive.Core.Util.Data;

import java.lang.reflect.Array;

/**
 * Created by Jonathan on 14/03/2015.
 */
public class DataTable {

    private byte _isTypedDataTable;

    protected  final DataColumnCollection columnCollection;
    private int elementColumnCount;
    protected boolean fInitInProgress;
    protected long nextRowID;
    protected  final RecordManager recordManager;
    private  final DataRowBuilder rowBuilder;
    protected  final DataRowCollection rowCollection;
    private DataRow[] EmptyDataRowArray;
    protected static final DataColumn[] zeroColumns = new DataColumn[0];
    protected static final DataRow[] zeroRows = new DataRow[0];

    public DataTable() {
        this.nextRowID = 1L;
        this.recordManager = new RecordManager(this);
        this.columnCollection = new DataColumnCollection(this);
        this.rowCollection = new DataRowCollection(this);
        this.rowBuilder = new DataRowBuilder(this, -1);
    }

    void AddRow(DataRow row) {
        this.AddRow(row, -1);
    }

    void AddRow(DataRow row, int proposedID) {
        this.InsertRow(row, proposedID, -1);
    }


    public void Clear() {
        this.Clear(true);
    }

    protected void Clear(boolean clearAll) {
            this.recordManager.Clear(clearAll);
            this.Rows().clear();
    }


    protected Class GetRowType() {
        return DataRow.class;
    }

    int GetSpecialHashCode(String name) {
        return name.toLowerCase().hashCode();
    }

    void InsertRow(DataRow row, long proposedID) {
            if (row.Table() != this) {
                throw new IllegalArgumentException("Row already belongs to another table");
            }
            if (row.rowID() != -1L) {
                throw new IllegalArgumentException("Row yet into table");
            }
            if ((row.oldRecord == -1) && (row.newRecord == -1)) {
                throw new IllegalArgumentException("Row is empty");
            }
            if (proposedID == -1L) {
                proposedID = this.nextRowID;
            }
            row.rowID(proposedID);
            if (this.nextRowID <= proposedID) {
                this.nextRowID = proposedID + 1L;
            }

            if (row.oldRecord != -1) {
                this.recordManager.set(row.oldRecord,row);
            }
            if (row.newRecord != -1) {
                this.recordManager.set(row.newRecord,row);
            }

            this.Rows().ArrayAdd(row);
    }

    void InsertRow(DataRow row, long proposedID, int pos) {

        boolean flag;
        if (row == null) {
            throw new IllegalArgumentException("Row can not be null");
        }
        if (row.Table() != this) {
            throw new IllegalArgumentException("Row already belongs to another table");
        }
        if (row.rowID() != -1L) {
            throw new IllegalArgumentException("Row yet into table");
        }

        if (proposedID == -1L) {
            proposedID = this.nextRowID;
        }
        if (flag = this.nextRowID <= proposedID) {
            this.nextRowID = proposedID + 1L;
        }
        try {
            row.rowID(proposedID);
            this.rowCollection.ArrayInsert(row,pos);
        } catch (Exception e) {
            row.rowID(-1L);
            if ((flag) && (this.nextRowID == (proposedID + 1L)))
                this.nextRowID = proposedID;
        } finally {
            row.ResetLastChangedColumn();
        }
    }

    int NewRecordFromArray(Object... values) {
        int num5;
        int count = this.columnCollection.size();
        if (count < values.length) {
            throw new IllegalArgumentException("There are more parameters than columns in table");
        }
        int record = this.recordManager.NewRecordBase();

        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                this.columnCollection.get(i).set(record,values[i]);
            } else {
                this.columnCollection.get(i).Init(record);
            }
        }
        for (int j = values.length; j < count; j++) {
            this.columnCollection.get(j).Init(record);
        }
        num5 = record;

        return num5;
    }


    protected DataRow NewEmptyRow() {
        this.rowBuilder._record = -1;
        DataRow row = this.NewRowFromBuilder(this.rowBuilder);

        return row;
    }

    protected int NewRecord()
    {
        return this.NewRecord(-1);
    }

    protected int NewRecord(int sourceRecord) {
        int record = this.recordManager.NewRecordBase();
        int count = this.columnCollection.size();
        if (-1 == sourceRecord)
        {
            for (int j = 0; j < count; j++)
            {
                this.columnCollection.get(j).Init(record);
            }
            return record;
        }
        for (int i = 0; i < count; i++)
        {
            this.columnCollection.get(i).Copy(sourceRecord, record);
        }
        return record;
    }


    public DataRow NewRow() {
        DataRow row = this.NewRow(-1);
        return row;
    }

    protected DataRow NewRow(int record) {
        if (-1 == record)
        {
            record = this.NewRecord(-1);
        }
        this.rowBuilder._record = record;
        DataRow row = this.NewRowFromBuilder(this.rowBuilder);
        this.recordManager.set(record,row);

        return row;
    }

    protected DataRow[] NewRowArray(int size) {
        if (this.IsTypedDataTable())
        {
            if (size != 0)
            {
                return (DataRow[]) Array.newInstance(this.GetRowType(), size);
            }
            if (this.EmptyDataRowArray == null)
            {
                this.EmptyDataRowArray = (DataRow[]) Array.newInstance(this.GetRowType(), 0);
            }
            return this.EmptyDataRowArray;
        }
        if (size != 0)
        {
            return new DataRow[size];
        }
        return zeroRows;
    }


    protected DataRow NewRowFromBuilder(DataRowBuilder builder)
    {
        return new DataRow(builder);
    }

    public DataColumnCollection Columns() {
        return this.columnCollection;
    }
    public DataColumn Columns(int index) {
        return this.columnCollection.get(index);
    }

    int ElementColumnCount() {
        return this.elementColumnCount;
    }

    void ElementColumnCount(int value) {
        this.elementColumnCount = value;
    }



    public boolean IsInitialized()
    {
        return !this.fInitInProgress;
    }

    private boolean IsTypedDataTable() {
            switch (this._isTypedDataTable)
            {
                case 0:
                    this._isTypedDataTable = (super.getClass() != DataTable.class) ? ((byte) 1) : ((byte) 2);
                    return (1 == this._isTypedDataTable);

                case 1:
                    return true;
            }
            return false;
    }

    int RecordCapacity() {
        return this.recordManager.RecordCapacity();
    }


    public DataRowCollection Rows() {
        return this.rowCollection;
    }

    public DataRow Rows(int index) {
        return this.rowCollection.get(index);
    }

}

