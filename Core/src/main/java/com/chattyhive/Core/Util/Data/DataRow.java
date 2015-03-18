package com.chattyhive.Core.Util.Data;

/**
 * Created by Jonathan on 18/03/2015.
 */
public class DataRow {
    DataRowAction _action;
    private final DataColumnCollection _columns;
    private int _countColumnChange;
    private Object _element;
    private DataColumn _lastChangedColumn;
    private static int _ObjectTypeCount;
    private int _rbTreeNodeId;
    long _rowID = -1L;
    private final DataTable _table;
    private DataError error;
    boolean inCascade;
    boolean inChangingEvent;
    boolean inDeletingEvent;
    int newRecord = -1;
    final int ObjectID = Interlocked.Increment(ref _ObjectTypeCount);
    int oldRecord = -1;
    int tempRecord;

    protected DataRow(DataRowBuilder builder)
    {
        this.tempRecord = builder._record;
        this._table = builder._table;
        this._columns = this._table.Columns;
    }

    public void AcceptChanges()
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataRow.AcceptChanges|API> %d#\n", this.ObjectID);
        try
        {
            this.EndEdit();
            if (((this.RowState != DataRowState.Detached) && (this.RowState != DataRowState.Deleted)) && (this._columns.ColumnsImplementingIChangeTrackingCount > 0))
            {
                foreach (DataColumn column in this._columns.ColumnsImplementingIChangeTracking)
                {
                    Object obj2 = this[column];
                    if (DBNull.Value != obj2)
                    {
                        IChangeTracking tracking = (IChangeTracking) obj2;
                        if (tracking.IsChanged)
                        {
                            tracking.AcceptChanges();
                        }
                    }
                }
            }
            this._table.CommitRow(this);
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    [EditorBrowsable(EditorBrowsableState.Advanced)]
    public void BeginEdit()
    {
        this.BeginEditInternal();
    }

    private boolean BeginEditInternal()
    {
        if (this.inChangingEvent)
        {
            throw ExceptionBuilder.BeginEditInRowChanging();
        }
        if (this.tempRecord != -1)
        {
            if (this.tempRecord < this._table.recordManager.LastFreeRecord)
            {
                return false;
            }
            this.tempRecord = -1;
        }
        if ((this.oldRecord != -1) && (this.newRecord == -1))
        {
            throw ExceptionBuilder.DeletedRowInaccessible();
        }
        this.ResetLastChangedColumn();
        this.tempRecord = this._table.NewRecord(this.newRecord);
        return true;
    }

    [EditorBrowsable(EditorBrowsableState.Advanced)]
    public void CancelEdit()
    {
        if (this.inChangingEvent)
        {
            throw ExceptionBuilder.CancelEditInRowChanging();
        }
        this._table.FreeRecord(ref this.tempRecord);
        this.ResetLastChangedColumn();
    }

    private void CheckColumn(DataColumn column)
    {
        if (column == null)
        {
            throw ExceptionBuilder.ArgumentNull("column");
        }
        if (column.Table != this._table)
        {
            throw ExceptionBuilder.ColumnNotInTheTable(column.ColumnName, this._table.TableName);
        }
    }

    void CheckForLoops(DataRelation rel)
{
    if (!this._table.fInLoadDiffgram && ((this._table.DataSet == null) || !this._table.DataSet.fInLoadDiffgram))
    {
        int count = this._table.Rows.Count;
        int num = 0;
        for (DataRow row = this.GetParentRow(rel); row != null; row = row.GetParentRow(rel))
        {
            if ((row == this) || (num > count))
            {
                throw ExceptionBuilder.NestedCircular(this._table.TableName);
            }
            num++;
        }
    }
}

    void CheckInTable()
{
    if (this.rowID == -1L)
    {
        throw ExceptionBuilder.RowNotInTheTable();
    }
}

    void ClearError(DataColumn column)
{
    if (this.error != null)
    {
        this.error.Clear(column);
        this.RowErrorChanged();
    }
}

    public void ClearErrors()
    {
        if (this.error != null)
        {
            this.error.Clear();
            this.RowErrorChanged();
        }
    }

    int CopyValuesIntoStore(ArrayList storeList, ArrayList nullbitList, int storeIndex)
{
    int num = 0;
    if (this.oldRecord != -1)
    {
        for (int i = 0; i < this._columns.Count; i++)
        {
            this._columns[i].CopyValueIntoStore(this.oldRecord, storeList[i], (BitArray) nullbitList[i], storeIndex);
        }
        num++;
        storeIndex++;
    }
    DataRowState rowState = this.RowState;
    if ((DataRowState.Added == rowState) || (DataRowState.Modified == rowState))
    {
        for (int j = 0; j < this._columns.Count; j++)
        {
            this._columns[j].CopyValueIntoStore(this.newRecord, storeList[j], (BitArray) nullbitList[j], storeIndex);
        }
        num++;
        storeIndex++;
    }
    if (-1 != this.tempRecord)
    {
        for (int k = 0; k < this._columns.Count; k++)
        {
            this._columns[k].CopyValueIntoStore(this.tempRecord, storeList[k], (BitArray) nullbitList[k], storeIndex);
        }
        num++;
        storeIndex++;
    }
    return num;
}

    public void Delete()
    {
        if (this.inDeletingEvent)
        {
            throw ExceptionBuilder.DeleteInRowDeleting();
        }
        if (this.newRecord != -1)
        {
            this._table.DeleteRow(this);
        }
    }

    [EditorBrowsable(EditorBrowsableState.Advanced)]
    public void EndEdit()
    {
        if (this.inChangingEvent)
        {
            throw ExceptionBuilder.EndEditInRowChanging();
        }
        if ((this.newRecord != -1) && (this.tempRecord != -1))
        {
            try
            {
                this._table.SetNewRecord(this, this.tempRecord, DataRowAction.Change, false, true, true);
            }
            finally
            {
                this.ResetLastChangedColumn();
            }
        }
    }

    public DataRow[] GetChildRows(DataRelation relation)
    {
        return this.GetChildRows(relation, DataRowVersion.Default);
    }

    public DataRow[] GetChildRows(String relationName)
    {
        return this.GetChildRows(this._table.ChildRelations[relationName], DataRowVersion.Default);
    }

    public DataRow[] GetChildRows(DataRelation relation, DataRowVersion version)
    {
        if (relation == null)
        {
            return this._table.NewRowArray(0);
        }
        if (relation.DataSet != this._table.DataSet)
        {
            throw ExceptionBuilder.RowNotInTheDataSet();
        }
        if (relation.ParentKey.Table != this._table)
        {
            throw ExceptionBuilder.RelationForeignTable(relation.ParentTable.TableName, this._table.TableName);
        }
        return DataRelation.GetChildRows(relation.ParentKey, relation.ChildKey, this, version);
    }

    public DataRow[] GetChildRows(String relationName, DataRowVersion version)
    {
        return this.GetChildRows(this._table.ChildRelations[relationName], version);
    }

    public String GetColumnError(DataColumn column)
    {
        this.CheckColumn(column);
        if (this.error == null)
        {
            this.error = new DataError();
        }
        return this.error.GetColumnError(column);
    }

    public String GetColumnError(int columnIndex)
    {
        DataColumn column = this._columns[columnIndex];
        return this.GetColumnError(column);
    }

    public String GetColumnError(String columnName)
    {
        DataColumn dataColumn = this.GetDataColumn(columnName);
        return this.GetColumnError(dataColumn);
    }

    public DataColumn[] GetColumnsInError()
    {
        if (this.error == null)
        {
            return DataTable.zeroColumns;
        }
        return this.error.GetColumnsInError();
    }

    Object[] GetColumnValues(DataColumn[] columns)
{
    return this.GetColumnValues(columns, DataRowVersion.Default);
}

    Object[] GetColumnValues(DataColumn[] columns, DataRowVersion version)
{
    DataKey key = new DataKey(columns, false);
    return this.GetKeyValues(key, version);
}

    int GetCurrentRecordNo()
{
    if (this.newRecord == -1)
    {
        throw ExceptionBuilder.NoCurrentData();
    }
    return this.newRecord;
}

    DataColumn GetDataColumn(String columnName)
{
    DataColumn column = this._columns[columnName];
    if (column == null)
    {
        throw ExceptionBuilder.ColumnNotInTheTable(columnName, this._table.TableName);
    }
    return column;
}

    int GetDefaultRecord()
{
    if (this.tempRecord != -1)
    {
        return this.tempRecord;
    }
    if (this.newRecord != -1)
    {
        return this.newRecord;
    }
    if (this.oldRecord == -1)
    {
        throw ExceptionBuilder.RowRemovedFromTheTable();
    }
    throw ExceptionBuilder.DeletedRowInaccessible();
}

    DataRowVersion GetDefaultRowVersion(DataViewRowState viewState)
{
    if (this.oldRecord == this.newRecord)
    {
        if (this.oldRecord == -1)
        {
            return DataRowVersion.Default;
        }
        return DataRowVersion.Default;
    }
    if (this.oldRecord == -1)
    {
        return DataRowVersion.Default;
    }
    if ((this.newRecord != -1) && ((DataViewRowState.ModifiedCurrent & viewState) != DataViewRowState.None))
    {
        return DataRowVersion.Default;
    }
    return DataRowVersion.Original;
}

    Object[] GetKeyValues(DataKey key)
{
    int defaultRecord = this.GetDefaultRecord();
    return key.GetKeyValues(defaultRecord);
}

    Object[] GetKeyValues(DataKey key, DataRowVersion version)
{
    int recordFromVersion = this.GetRecordFromVersion(version);
    return key.GetKeyValues(recordFromVersion);
}

    int GetNestedParentCount()
{
    int num2 = 0;
    foreach (DataRelation relation in this._table.NestedParentRelations)
    {
        if (relation != null)
        {
            if (relation.ParentTable == this._table)
            {
                this.CheckForLoops(relation);
            }
            if (this.GetParentRow(relation) != null)
            {
                num2++;
            }
        }
    }
    return num2;
}

    DataRow GetNestedParentRow(DataRowVersion version)
{
    foreach (DataRelation relation in this._table.NestedParentRelations)
    {
        if (relation != null)
        {
            if (relation.ParentTable == this._table)
            {
                this.CheckForLoops(relation);
            }
            DataRow parentRow = this.GetParentRow(relation, version);
            if (parentRow != null)
            {
                return parentRow;
            }
        }
    }
    return null;
}

    int GetOriginalRecordNo()
{
    if (this.oldRecord == -1)
    {
        throw ExceptionBuilder.NoOriginalData();
    }
    return this.oldRecord;
}

    public DataRow GetParentRow(DataRelation relation)
    {
        return this.GetParentRow(relation, DataRowVersion.Default);
    }

    public DataRow GetParentRow(String relationName)
    {
        return this.GetParentRow(this._table.ParentRelations[relationName], DataRowVersion.Default);
    }

    public DataRow GetParentRow(DataRelation relation, DataRowVersion version)
    {
        if (relation == null)
        {
            return null;
        }
        if (relation.DataSet != this._table.DataSet)
        {
            throw ExceptionBuilder.RelationForeignRow();
        }
        if (relation.ChildKey.Table != this._table)
        {
            throw ExceptionBuilder.GetParentRowTableMismatch(relation.ChildTable.TableName, this._table.TableName);
        }
        return DataRelation.GetParentRow(relation.ParentKey, relation.ChildKey, this, version);
    }

    public DataRow GetParentRow(String relationName, DataRowVersion version)
    {
        return this.GetParentRow(this._table.ParentRelations[relationName], version);
    }

    public DataRow[] GetParentRows(DataRelation relation)
    {
        return this.GetParentRows(relation, DataRowVersion.Default);
    }

    public DataRow[] GetParentRows(String relationName)
    {
        return this.GetParentRows(this._table.ParentRelations[relationName], DataRowVersion.Default);
    }

    public DataRow[] GetParentRows(DataRelation relation, DataRowVersion version)
    {
        if (relation == null)
        {
            return this._table.NewRowArray(0);
        }
        if (relation.DataSet != this._table.DataSet)
        {
            throw ExceptionBuilder.RowNotInTheDataSet();
        }
        if (relation.ChildKey.Table != this._table)
        {
            throw ExceptionBuilder.GetParentRowTableMismatch(relation.ChildTable.TableName, this._table.TableName);
        }
        return DataRelation.GetParentRows(relation.ParentKey, relation.ChildKey, this, version);
    }

    public DataRow[] GetParentRows(String relationName, DataRowVersion version)
    {
        return this.GetParentRows(this._table.ParentRelations[relationName], version);
    }

    private int GetProposedRecordNo()
    {
        if (this.tempRecord == -1)
        {
            throw ExceptionBuilder.NoProposedData();
        }
        return this.tempRecord;
    }

    int GetRecordFromVersion(DataRowVersion version)
{
    switch (version)
    {
        case DataRowVersion.Original:
            return this.GetOriginalRecordNo();

        case DataRowVersion.Current:
            return this.GetCurrentRecordNo();

        case DataRowVersion.Proposed:
            return this.GetProposedRecordNo();

        case DataRowVersion.Default:
            return this.GetDefaultRecord();
    }
    throw ExceptionBuilder.InvalidRowVersion();
}

    DataViewRowState GetRecordState(int record)
{
    if (record == -1)
    {
        return DataViewRowState.None;
    }
    if ((record == this.oldRecord) && (record == this.newRecord))
    {
        return DataViewRowState.Unchanged;
    }
    if (record == this.oldRecord)
    {
        if (this.newRecord == -1)
        {
            return DataViewRowState.Deleted;
        }
        return DataViewRowState.ModifiedOriginal;
    }
    if (record != this.newRecord)
    {
        return DataViewRowState.None;
    }
    if (this.oldRecord == -1)
    {
        return DataViewRowState.Added;
    }
    return DataViewRowState.ModifiedCurrent;
}

    boolean HasChanges()
{
    if (!this.HasVersion(DataRowVersion.Original) || !this.HasVersion(DataRowVersion.Current))
    {
        return true;
    }
    foreach (DataColumn column in this.Table.Columns)
    {
        if (column.Compare(this.oldRecord, this.newRecord) != 0)
        {
            return true;
        }
    }
    return false;
}

    boolean HasKeyChanged(DataKey key)
{
    return this.HasKeyChanged(key, DataRowVersion.Current, DataRowVersion.Proposed);
}

    boolean HasKeyChanged(DataKey key, DataRowVersion version1, DataRowVersion version2)
{
    if (this.HasVersion(version1) && this.HasVersion(version2))
    {
        return !key.RecordsEqual(this.GetRecordFromVersion(version1), this.GetRecordFromVersion(version2));
    }
    return true;
}

    public boolean HasVersion(DataRowVersion version)
    {
        switch (version)
        {
            case DataRowVersion.Proposed:
                return (this.tempRecord != -1);

            case DataRowVersion.Default:
                if (this.tempRecord == -1)
                {
                    return (this.newRecord != -1);
                }
                return true;

            case DataRowVersion.Original:
                return (this.oldRecord != -1);

            case DataRowVersion.Current:
                return (this.newRecord != -1);
        }
        throw ExceptionBuilder.InvalidRowVersion();
    }

    boolean HaveValuesChanged(DataColumn[] columns)
{
    return this.HaveValuesChanged(columns, DataRowVersion.Current, DataRowVersion.Proposed);
}

    boolean HaveValuesChanged(DataColumn[] columns, DataRowVersion version1, DataRowVersion version2)
{
    for (int i = 0; i < columns.Length; i++)
    {
        this.CheckColumn(columns[i]);
    }
    DataKey key = new DataKey(columns, false);
    return this.HasKeyChanged(key, version1, version2);
}

    public boolean IsNull(DataColumn column)
    {
        this.CheckColumn(column);
        int defaultRecord = this.GetDefaultRecord();
        return column.IsNull(defaultRecord);
    }

    public boolean IsNull(int columnIndex)
    {
        DataColumn column = this._columns[columnIndex];
        int defaultRecord = this.GetDefaultRecord();
        return column.IsNull(defaultRecord);
    }

    public boolean IsNull(String columnName)
    {
        DataColumn dataColumn = this.GetDataColumn(columnName);
        int defaultRecord = this.GetDefaultRecord();
        return dataColumn.IsNull(defaultRecord);
    }

    public boolean IsNull(DataColumn column, DataRowVersion version)
    {
        this.CheckColumn(column);
        int recordFromVersion = this.GetRecordFromVersion(version);
        return column.IsNull(recordFromVersion);
    }

    public void RejectChanges()
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataRow.RejectChanges|API> %d#\n", this.ObjectID);
        try
        {
            if (this.RowState != DataRowState.Detached)
            {
                if (this._columns.ColumnsImplementingIChangeTrackingCount != this._columns.ColumnsImplementingIRevertibleChangeTrackingCount)
                {
                    foreach (DataColumn column in this._columns.ColumnsImplementingIChangeTracking)
                    {
                        if (!column.ImplementsIRevertibleChangeTracking)
                        {
                            Object obj3 = null;
                            if (this.RowState != DataRowState.Deleted)
                            {
                                obj3 = this[column];
                            }
                            else
                            {
                                obj3 = this[column, DataRowVersion.Original];
                            }
                            if ((DBNull.Value != obj3) && ((IChangeTracking) obj3).IsChanged)
                            {
                                throw ExceptionBuilder.UDTImplementsIChangeTrackingButnotIRevertible(column.DataType.AssemblyQualifiedName);
                            }
                        }
                    }
                }
                foreach (DataColumn column2 in this._columns.ColumnsImplementingIChangeTracking)
                {
                    Object obj2 = null;
                    if (this.RowState != DataRowState.Deleted)
                    {
                        obj2 = this[column2];
                    }
                    else
                    {
                        obj2 = this[column2, DataRowVersion.Original];
                    }
                    if (DBNull.Value != obj2)
                    {
                        IChangeTracking tracking = (IChangeTracking) obj2;
                        if (tracking.IsChanged)
                        {
                            ((IRevertibleChangeTracking) obj2).RejectChanges();
                        }
                    }
                }
            }
            this._table.RollbackRow(this);
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    void ResetLastChangedColumn()
{
    this._lastChangedColumn = null;
    this._countColumnChange = 0;
}

    private void RowErrorChanged()
    {
        if (this.oldRecord != -1)
        {
            this._table.RecordChanged(this.oldRecord);
        }
        if (this.newRecord != -1)
        {
            this._table.RecordChanged(this.newRecord);
        }
    }

    public void SetAdded()
    {
        if (this.RowState != DataRowState.Unchanged)
        {
            throw ExceptionBuilder.SetAddedAndModifiedCalledOnnonUnchanged();
        }
        this._table.SetOldRecord(this, -1);
    }

    public void SetColumnError(DataColumn column, String error)
    {
        IntPtr ptr;
        this.CheckColumn(column);
        Bid.ScopeEnter(out ptr, "<ds.DataRow.SetColumnError|API> %d#, column=%d, error='%ls'\n", this.ObjectID, column.ObjectID, error);
        try
        {
            if (this.error == null)
            {
                this.error = new DataError();
            }
            if (this.GetColumnError(column) != error)
            {
                this.error.SetColumnError(column, error);
                this.RowErrorChanged();
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    public void SetColumnError(int columnIndex, String error)
    {
        DataColumn column = this._columns[columnIndex];
        if (column == null)
        {
            throw ExceptionBuilder.ColumnOutOfRange(columnIndex);
        }
        this.SetColumnError(column, error);
    }

    public void SetColumnError(String columnName, String error)
    {
        DataColumn dataColumn = this.GetDataColumn(columnName);
        this.SetColumnError(dataColumn, error);
    }

    void SetKeyValues(DataKey key, Object[] keyValues)
{
    boolean flag = true;
    boolean flag2 = this.tempRecord == -1;
    for (int i = 0; i < keyValues.Length; i++)
    {
        Object obj2 = this[key.ColumnsReference[i]];
        if (!obj2.Equals(keyValues[i]))
        {
            if (flag2 && flag)
            {
                flag = false;
                this.BeginEditInternal();
            }
            this[key.ColumnsReference[i]] = keyValues[i];
        }
    }
    if (!flag)
    {
        this.EndEdit();
    }
}

    public void SetModified()
    {
        if (this.RowState != DataRowState.Unchanged)
        {
            throw ExceptionBuilder.SetAddedAndModifiedCalledOnnonUnchanged();
        }
        this.tempRecord = this._table.NewRecord(this.newRecord);
        if (this.tempRecord != -1)
        {
            this._table.SetNewRecord(this, this.tempRecord, DataRowAction.Change, false, true, true);
        }
    }

    void SetNestedParentRow(DataRow parentRow, boolean setNonNested)
{
    if (parentRow == null)
    {
        this.SetParentRowToDBNull();
    }
    else
    {
        foreach (DataRelation relation in this._table.ParentRelations)
        {
            if ((relation.Nested || setNonNested) && (relation.ParentKey.Table == parentRow._table))
            {
                Object[] keyValues = parentRow.GetKeyValues(relation.ParentKey);
                this.SetKeyValues(relation.ChildKey, keyValues);
                if (relation.Nested)
                {
                    if (parentRow._table == this._table)
                    {
                        this.CheckForLoops(relation);
                    }
                    else
                    {
                        this.GetParentRow(relation);
                    }
                }
            }
        }
    }
}

    protected void SetNull(DataColumn column)
    {
        this[column] = DBNull.Value;
    }

    public void SetParentRow(DataRow parentRow)
    {
        this.SetNestedParentRow(parentRow, true);
    }

    public void SetParentRow(DataRow parentRow, DataRelation relation)
    {
        if (relation == null)
        {
            this.SetParentRow(parentRow);
        }
        else if (parentRow == null)
        {
            this.SetParentRowToDBNull(relation);
        }
        else
        {
            if (this._table.DataSet != parentRow._table.DataSet)
            {
                throw ExceptionBuilder.ParentRowNotInTheDataSet();
            }
            if (relation.ChildKey.Table != this._table)
            {
                throw ExceptionBuilder.SetParentRowTableMismatch(relation.ChildKey.Table.TableName, this._table.TableName);
            }
            if (relation.ParentKey.Table != parentRow._table)
            {
                throw ExceptionBuilder.SetParentRowTableMismatch(relation.ParentKey.Table.TableName, parentRow._table.TableName);
            }
            Object[] keyValues = parentRow.GetKeyValues(relation.ParentKey);
            this.SetKeyValues(relation.ChildKey, keyValues);
        }
    }

    void SetParentRowToDBNull()
{
    foreach (DataRelation relation in this._table.ParentRelations)
    {
        this.SetParentRowToDBNull(relation);
    }
}

    void SetParentRowToDBNull(DataRelation relation)
{
    if (relation.ChildKey.Table != this._table)
    {
        throw ExceptionBuilder.SetParentRowTableMismatch(relation.ChildKey.Table.TableName, this._table.TableName);
    }
    Object[] keyValues = new Object[] { DBNull.Value };
    this.SetKeyValues(relation.ChildKey, keyValues);
}

    [Conditional("DEBUG")]
    private void VerifyValueFromStorage(DataColumn column, DataRowVersion version, Object valueFromStorage)
    {
        if ((((column.DataExpression != null) && !this.inChangingEvent) && ((this.tempRecord == -1) && (this.newRecord != -1))) && ((version == DataRowVersion.Original) && (this.oldRecord == this.newRecord)))
        {
            version = DataRowVersion.Current;
        }
    }

    XmlBoundElement Element
    {
        get
        {
            return (XmlBoundElement) this._element;
        }
        set
        {
            this._element = value;
        }
    }

    public boolean HasErrors
    {
        get
        {
            return ((this.error != null) && this.error.HasErrors);
        }
    }

    boolean HasPropertyChanged
    {
        get
        {
            return (0 < this._countColumnChange);
        }
    }

    public Object this[int columnIndex]
    {
        get
        {
            DataColumn column = this._columns[columnIndex];
            int defaultRecord = this.GetDefaultRecord();
            return column[defaultRecord];
        }
        set
        {
            DataColumn column = this._columns[columnIndex];
            this[column] = value;
        }
    }

    public Object this[String columnName]
    {
        get
        {
            DataColumn dataColumn = this.GetDataColumn(columnName);
            int defaultRecord = this.GetDefaultRecord();
            return dataColumn[defaultRecord];
        }
        set
        {
            DataColumn dataColumn = this.GetDataColumn(columnName);
            this[dataColumn] = value;
        }
    }

    public Object this[DataColumn column]
    {
        get
        {
            this.CheckColumn(column);
            int defaultRecord = this.GetDefaultRecord();
            return column[defaultRecord];
        }
        set
        {
            this.CheckColumn(column);
            if (this.inChangingEvent)
            {
                throw ExceptionBuilder.EditInRowChanging();
            }
            if ((-1L != this.rowID) && column.final)
            {
                throw ExceptionBuilder.final(column.ColumnName);
            }
            DataColumnChangeEventArgs e = null;
            if (this._table.NeedColumnChangeEvents)
            {
                e = new DataColumnChangeEventArgs(this, column, value);
                this._table.OnColumnChanging(e);
            }
            if (column.Table != this._table)
            {
                throw ExceptionBuilder.ColumnNotInTheTable(column.ColumnName, this._table.TableName);
            }
            if ((-1L != this.rowID) && column.final)
            {
                throw ExceptionBuilder.final(column.ColumnName);
            }
            Object obj2 = (e != null) ? e.ProposedValue : value;
            if (obj2 == null)
            {
                if (column.IsValueType)
                {
                    throw ExceptionBuilder.CannotSetToNull(column);
                }
                obj2 = DBNull.Value;
            }
            boolean flag = this.BeginEditInternal();
            try
            {
                int proposedRecordNo = this.GetProposedRecordNo();
                column[proposedRecordNo] = obj2;
            }
            catch (Exception exception)
            {
                if (ADP.IsCatchableOrSecurityExceptionType(exception) && flag)
                {
                    this.CancelEdit();
                }
                throw;
            }
            this.LastChangedColumn = column;
            if (e != null)
            {
                this._table.OnColumnChanged(e);
            }
            if (flag)
            {
                this.EndEdit();
            }
        }
    }

    public Object this[int columnIndex, DataRowVersion version]
    {
        get
        {
            DataColumn column = this._columns[columnIndex];
            int recordFromVersion = this.GetRecordFromVersion(version);
            return column[recordFromVersion];
        }
    }

    public Object this[String columnName, DataRowVersion version]
    {
        get
        {
            DataColumn dataColumn = this.GetDataColumn(columnName);
            int recordFromVersion = this.GetRecordFromVersion(version);
            return dataColumn[recordFromVersion];
        }
    }

    public Object this[DataColumn column, DataRowVersion version]
    {
        get
        {
            this.CheckColumn(column);
            int recordFromVersion = this.GetRecordFromVersion(version);
            return column[recordFromVersion];
        }
    }

    public Object[] ItemArray
    {
        get
        {
            int defaultRecord = this.GetDefaultRecord();
            Object[] objArray = new Object[this._columns.Count];
            for (int i = 0; i < objArray.Length; i++)
            {
                DataColumn column = this._columns[i];
                objArray[i] = column[defaultRecord];
            }
            return objArray;
        }
        set
        {
            if (value == null)
            {
                throw ExceptionBuilder.ArgumentNull("ItemArray");
            }
            if (this._columns.Count < value.Length)
            {
                throw ExceptionBuilder.ValueArrayLength();
            }
            DataColumnChangeEventArgs e = null;
            if (this._table.NeedColumnChangeEvents)
            {
                e = new DataColumnChangeEventArgs(this);
            }
            boolean flag = this.BeginEditInternal();
            for (int i = 0; i < value.Length; i++)
            {
                if (value[i] != null)
                {
                    DataColumn column = this._columns[i];
                    if ((-1L != this.rowID) && column.final)
                    {
                        throw ExceptionBuilder.final(column.ColumnName);
                    }
                    if (e != null)
                    {
                        e.InitializeColumnChangeEvent(column, value[i]);
                        this._table.OnColumnChanging(e);
                    }
                    if (column.Table != this._table)
                    {
                        throw ExceptionBuilder.ColumnNotInTheTable(column.ColumnName, this._table.TableName);
                    }
                    if ((-1L != this.rowID) && column.final)
                    {
                        throw ExceptionBuilder.final(column.ColumnName);
                    }
                    if (this.tempRecord == -1)
                    {
                        this.BeginEditInternal();
                    }
                    Object obj2 = (e != null) ? e.ProposedValue : value[i];
                    if (obj2 == null)
                    {
                        if (column.IsValueType)
                        {
                            throw ExceptionBuilder.CannotSetToNull(column);
                        }
                        obj2 = DBNull.Value;
                    }
                    try
                    {
                        int proposedRecordNo = this.GetProposedRecordNo();
                        column[proposedRecordNo] = obj2;
                    }
                    catch (Exception exception)
                    {
                        if (ADP.IsCatchableOrSecurityExceptionType(exception) && flag)
                        {
                            this.CancelEdit();
                        }
                        throw;
                    }
                    this.LastChangedColumn = column;
                    if (e != null)
                    {
                        this._table.OnColumnChanged(e);
                    }
                }
            }
            this.EndEdit();
        }
    }

    DataColumn LastChangedColumn
    {
        get
        {
            if (this._countColumnChange != 1)
            {
                return null;
            }
            return this._lastChangedColumn;
        }
        set
        {
            this._countColumnChange++;
            this._lastChangedColumn = value;
        }
    }

    int RBTreeNodeId
    {
        get
        {
            return this._rbTreeNodeId;
        }
        set
        {
            Bid.Trace("<ds.DataRow.set_RBTreeNodeId|INFO> %d#, value=%d\n", this.ObjectID, value);
            this._rbTreeNodeId = value;
        }
    }

    public String RowError
    {
        get
        {
            if (this.error != null)
            {
                return this.error.Text;
            }
            return String.Empty;
        }
        set
        {
            Bid.Trace("<ds.DataRow.set_RowError|API> %d#, value='%ls'\n", this.ObjectID, value);
            if (this.error == null)
            {
                if (!ADP.IsEmpty(value))
                {
                    this.error = new DataError(value);
                }
                this.RowErrorChanged();
            }
            else if (this.error.Text != value)
            {
                this.error.Text = value;
                this.RowErrorChanged();
            }
        }
    }

    long rowID
    {
        get
        {
            return this._rowID;
        }
        set
        {
            this.ResetLastChangedColumn();
            this._rowID = value;
        }
    }

    public DataRowState RowState
    {
        get
        {
            if (this.oldRecord == this.newRecord)
            {
                if (this.oldRecord == -1)
                {
                    return DataRowState.Detached;
                }
                if (0 < this._columns.ColumnsImplementingIChangeTrackingCount)
                {
                    foreach (DataColumn column in this._columns.ColumnsImplementingIChangeTracking)
                    {
                        Object obj2 = this[column];
                        if ((DBNull.Value != obj2) && ((IChangeTracking) obj2).IsChanged)
                        {
                            return DataRowState.Modified;
                        }
                    }
                }
                return DataRowState.Unchanged;
            }
            if (this.oldRecord == -1)
            {
                return DataRowState.Added;
            }
            if (this.newRecord == -1)
            {
                return DataRowState.Deleted;
            }
            return DataRowState.Modified;
        }
    }

    public DataTable Table
    {
        get
        {
            return this._table;
        }
    }
}

