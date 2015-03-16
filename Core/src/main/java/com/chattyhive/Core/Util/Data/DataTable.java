package com.chattyhive.Core.Util.Data;

import java.lang.reflect.Array;

/**
 * Created by Jonathan on 14/03/2015.
 */
public class DataTable {

    private byte _isTypedDataTable;

    protected  final DataColumnCollection columnCollection;
    protected boolean fInitInProgress;
    protected long nextRowID;
    protected  final RecordManager recordManager;
    private  final DataRowBuilder rowBuilder;
    protected  final DataRowCollection rowCollection;
    private DataRow[] EmptyDataRowArray;
    protected static final DataRow[] zeroRows = new DataRow[0];




    public DataTable() {
        this.nextRowID = 1L;
        this.recordManager = new RecordManager(this);
        this.columnCollection = new DataColumnCollection(this);
        this.rowCollection = new DataRowCollection(this);
        this.rowBuilder = new DataRowBuilder(this, -1);
    }

    public void Clear() {
        this.Clear(true);
    }

    protected void Clear(boolean clearAll) {
            this.recordManager.Clear(clearAll);
            this.Rows.ArrayClear();
    }


    protected Class GetRowType() {
        return DataRow.class;
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
        int count = this.columnCollection.Count;
        if (-1 == sourceRecord)
        {
            for (int j = 0; j < count; j++)
            {
                this.columnCollection[j].Init(record);
            }
            return record;
        }
        for (int i = 0; i < count; i++)
        {
            this.columnCollection[i].Copy(sourceRecord, record);
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


    public DataRowCollection Rows()
    {
        return this.rowCollection;
    }

}

