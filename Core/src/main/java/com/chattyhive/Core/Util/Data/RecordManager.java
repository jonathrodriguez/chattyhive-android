package com.chattyhive.Core.Util.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 16/03/2015.
 */
class RecordManager {
    // Fields
    private final List<Integer> freeRecordList = new ArrayList<Integer>();
    private int lastFreeRecord;
    private int minimumCapacity = 50;
    private int recordCapacity;
    private DataRow[] rows;
    private final DataTable table;

    // Methods
    RecordManager(DataTable table) {
        if (table == null)
            throw new IllegalArgumentException("Table must not be null");

        this.table = table;
    }

    void Clear(boolean clearAll) {
        if (clearAll) {
            for (int i = 0; i < this.recordCapacity; i++) {
                this.rows[i] = null;
            }
            int count = this.table.columnCollection.size();
            for (int j = 0; j < count; j++) {
                DataColumn column = this.table.columnCollection.get(j);
                for (int k = 0; k < this.recordCapacity; k++) {
                    column.FreeRecord(k);
                }
            }
            this.lastFreeRecord = 0;
            this.freeRecordList.clear();
        } else {
            //this.freeRecordList.Capacity = this.freeRecordList.Count + this.table.Rows.Count;
            for (int m = 0; m < this.recordCapacity; m++) {
                if ((this.rows[m] != null) && (this.rows[m].rowID != -1L)) {
                    int record = m;
                    this.FreeRecord(record);
                }
            }
        }
    }

    void FreeRecord(int record) {
        if (-1 != record) {
            set(record,null);
            int count = this.table.columnCollection.size();
            for (int i = 0; i < count; i++) {
                this.table.columnCollection.get(i).FreeRecord(record);
            }
            if (this.lastFreeRecord == (record + 1)) {
                this.lastFreeRecord--;
            } else if (record < this.lastFreeRecord) {
                this.freeRecordList.add(record);
            }
            record = -1;
        }
    }

    private void GrowRecordCapacity() {
        if (NewCapacity(this.recordCapacity) < this.NormalizedMinimumCapacity(this.minimumCapacity))
        {
            RecordCapacity(this.NormalizedMinimumCapacity(this.minimumCapacity));
        }
        else
        {
            RecordCapacity(NewCapacity(this.recordCapacity));
        }
        DataRow[] destinationArray = this.table.NewRowArray(this.recordCapacity);
        if (this.rows != null)
        {
            System.arraycopy(this.rows, 0, destinationArray, 0, Math.min(this.lastFreeRecord, this.rows.length));
        }
        this.rows = destinationArray;
    }

    static int NewCapacity(int capacity) {
        if (capacity >= 0x80) {
            return (capacity + capacity);
        }
        return 0x80;
    }

    int NewRecordBase() {
        int lastFreeRecord;
        if (this.freeRecordList.size() != 0) {
            lastFreeRecord = this.freeRecordList.get(this.freeRecordList.size() - 1);
            this.freeRecordList.remove(this.freeRecordList.size() - 1);
            return lastFreeRecord;
        }
        if (this.lastFreeRecord >= this.recordCapacity) {
            this.GrowRecordCapacity();
        }
        lastFreeRecord = this.lastFreeRecord;
        this.lastFreeRecord++;
        return lastFreeRecord;
    }

    private int NormalizedMinimumCapacity(int capacity) {
        if (capacity >= 0x3f6) {
            return ((((capacity + 10) >> 10) + 1) << 10);
        }
        if (capacity >= 0xf6) {
            return 0x400;
        }
        if (capacity < 0x36) {
            return 0x40;
        }
        return 0x100;
    }

        // Properties
    DataRow get(int record)
    {
        return this.rows[record];
    }
    void set(int record,DataRow value) {
        this.rows[record] = value;
    }

    int LastFreeRecord() {
        return this.lastFreeRecord;
    }

    int MinimumCapacity() {
        return this.minimumCapacity;
    }
    void MinimumCapacity(int value) {
        if (this.minimumCapacity != value) {
            if (value < 0)
                throw new IllegalArgumentException("Capacity can not be negative.");
            this.minimumCapacity = value;
        }
    }

    int RecordCapacity() {
        return this.recordCapacity;
    }

    void RecordCapacity(int value) {
        if (this.recordCapacity != value)
        {
            for (int i = 0; i < this.table.Columns().size(); i++)
                this.table.Columns(i).SetCapacity(value);

            this.recordCapacity = value;
        }
    }

}
