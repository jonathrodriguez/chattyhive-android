package com.chattyhive.Core.Util.Data;

import java.lang.reflect.Type;

/**
 * Created by Jonathan on 18/03/2015.
 */

public class DataColumn {
    private String _columnName;
    private String _columnPrefix;
    String _columnUri;
    int _hashCode;
    private final int _objectID;
    private static int _objectTypeCount;
    private int _ordinal;
    private String caption;
    MappingType columnMapping;
    private Type dataType;
    Object defaultValue;
    private boolean defaultValueIsNull;
    String dttype;
    String encodedColumnName;
    int errors;
    PropertyCollection extendedProperties;
    private int maxLength;
    DataTable table;

    public DataColumn() {
        this(null, String.class , MappingType.Element);
    }

    public DataColumn(String columnName) {
        this(columnName, String.class, MappingType.Element);
    }

    public DataColumn(String columnName, Type dataType) {
        this(columnName, dataType, MappingType.Element);
    }

    public DataColumn(String columnName, Type dataType, MappingType type) {

        this.defaultValue = DBNull.Value;

        this.maxLength = -1;
        this._ordinal = -1;
        this.columnMapping = MappingType.Element;
        this.defaultValueIsNull = true;
        this._columnPrefix = "";
        this.dttype = "";
        this._objectID = Interlocked.Increment(ref _objectTypeCount);

        if (dataType == null) {
            throw new IllegalArgumentException("DataType can not be null");
        }

        this._columnName = ((columnName != null)?columnName:"");

        this.UpdateColumnType(dataType);

        this.columnMapping = type;
    }

    boolean CheckMaxLength()
    {
        if (((0 <= this.maxLength) && (this.Table != null)) && (0 < this.Table.Rows.Count))
        {
            foreach (DataRow row in this.Table.Rows)
            {
                if (row.HasVersion(DataRowVersion.Current) && (this.maxLength < this.GetStringLength(row.GetCurrentRecordNo())))
                {
                    return false;
                }
            }
        }
        return true;
    }

    void CheckMaxLength(DataRow dr)
    {
        if ((0 <= this.maxLength) && (this.maxLength < this.GetStringLength(dr.GetDefaultRecord())))
        {
            throw ExceptionBuilder.LongerThanMaxLength(this);
        }
    }

    protected void CheckNotAllowNull()
    {
        if (this._storage != null)
        {
            if (this.sortIndex != null)
            {
                if (this.sortIndex.IsKeyInIndex(this._storage.NullValue))
                {
                    throw ExceptionBuilder.NullKeyValues(this.ColumnName);
                }
            }
            else
            {
                foreach (DataRow row in this.table.Rows)
                {
                    if (row.RowState != DataRowState.Deleted)
                    {
                        if (!this.implementsINullable)
                        {
                            if (row[this] == DBNull.Value)
                            {
                                throw ExceptionBuilder.NullKeyValues(this.ColumnName);
                            }
                        }
                        else if (DataStorage.IsObjectNull(row[this]))
                        {
                            throw ExceptionBuilder.NullKeyValues(this.ColumnName);
                        }
                    }
                }
            }
        }
    }

    void CheckNullable(DataRow row)
    {
        if (!this.AllowDBNull && this._storage.IsNull(row.GetDefaultRecord()))
        {
            throw ExceptionBuilder.NullValues(this.ColumnName);
        }
    }

    protected void CheckUnique()
    {
        if (!this.SortIndex.CheckUnique())
        {
            throw ExceptionBuilder.NonUniqueValues(this.ColumnName);
        }
    }

    [MethodImpl(MethodImplOptions.NoInlining)]
    DataColumn Clone()
    {
        DataColumn column = (DataColumn) Activator.CreateInstance(base.GetType());
        column.SimpleType = this.SimpleType;
        column.allowNull = this.allowNull;
        if (this.autoInc != null)
        {
            column.autoInc = this.autoInc.Clone();
        }
        column.caption = this.caption;
        column.ColumnName = this.ColumnName;
        column._columnUri = this._columnUri;
        column._columnPrefix = this._columnPrefix;
        column.DataType = this.DataType;
        column.defaultValue = this.defaultValue;
        column.defaultValueIsNull = (this.defaultValue == DBNull.Value) || (column.ImplementsINullable && DataStorage.IsObjectSqlNull(this.defaultValue));
        column.columnMapping = this.columnMapping;
        column.final = this.final;
        column.MaxLength = this.MaxLength;
        column.dttype = this.dttype;
        column._dateTimeMode = this._dateTimeMode;
        if (this.extendedProperties != null)
        {
            foreach (Object obj2 in this.extendedProperties.Keys)
            {
                column.ExtendedProperties[obj2] = this.extendedProperties[obj2];
            }
        }
        return column;
    }

    int Compare(int record1, int record2)
    {
        return this._storage.Compare(record1, record2);
    }

    int CompareValueTo(int record1, Object value)
    {
        return this._storage.CompareValueTo(record1, value);
    }

    boolean CompareValueTo(int record1, Object value, boolean checkType)
    {
        if (this.CompareValueTo(record1, value) == 0)
        {
            Type type2 = value.GetType();
            Type type = this._storage.Get(record1).GetType();
            if ((type2 == typeof(String)) && (type == typeof(String)))
            {
                if (String.CompareOrdinal((String) this._storage.Get(record1), (String) value) != 0)
                {
                    return false;
                }
                return true;
            }
            if (type2 == type)
            {
                return true;
            }
        }
        return false;
    }

    String ConvertObjectToXml(Object value)
    {
        this.InsureStorage();
        return this._storage.ConvertObjectToXml(value);
    }

    void ConvertObjectToXml(Object value, XmlWriter xmlWriter, XmlRootAttribute xmlAttrib)
    {
        this.InsureStorage();
        this._storage.ConvertObjectToXml(value, xmlWriter, xmlAttrib);
    }

    Object ConvertValue(Object value)
    {
        return this._storage.ConvertValue(value);
    }

    Object ConvertXmlToObject(String s)
    {
        this.InsureStorage();
        return this._storage.ConvertXmlToObject(s);
    }

    Object ConvertXmlToObject(XmlReader xmlReader, XmlRootAttribute xmlAttrib)
    {
        this.InsureStorage();
        return this._storage.ConvertXmlToObject(xmlReader, xmlAttrib);
    }

    void Copy(int srcRecordNo, int dstRecordNo)
    {
        this._storage.Copy(srcRecordNo, dstRecordNo);
    }

    void CopyValueIntoStore(int record, Object store, BitArray nullbits, int storeIndex)
    {
        this._storage.CopyValueInternal(record, store, nullbits, storeIndex);
    }

    DataRelation FindParentRelation()
    {
        DataRelation[] array = new DataRelation[this.Table.ParentRelations.Count];
        this.Table.ParentRelations.CopyTo(array, 0);
        for (int i = 0; i < array.Length; i++)
        {
            DataRelation relation = array[i];
            DataKey childKey = relation.ChildKey;
            if ((childKey.ColumnsReference.Length == 1) && (childKey.ColumnsReference[0] == this))
            {
                return relation;
            }
        }
        return null;
    }

    void FinishInitInProgress()
    {
        if (this.Computed)
        {
            this.BindExpression();
        }
    }

    void FreeRecord(int record)
    {
        this._storage.Set(record, this._storage.NullValue);
    }

    Object GetAggregateValue(int[] records, AggregateType kind)
    {
        if (this._storage != null)
        {
            return this._storage.Aggregate(records, kind);
        }
        if (kind == AggregateType.Count)
        {
            return 0;
        }
        return DBNull.Value;
    }

    String GetColumnValueAsString(DataRow row, DataRowVersion version)
    {
        Object obj2 = this[row.GetRecordFromVersion(version)];
        if (DataStorage.IsObjectNull(obj2))
        {
            return null;
        }
        return this.ConvertObjectToXml(obj2);
    }

    private DataRow GetDataRow(int index)
    {
        return this.table.recordManager[index];
    }

    Object GetEmptyColumnStore(int recordCount)
    {
        this.InsureStorage();
        return this._storage.GetEmptyStorageInternal(recordCount);
    }

    private int GetStringLength(int record)
    {
        return this._storage.GetStringLength(record);
    }

    void HandleDependentColumnList(System.Data.DataExpression oldExpression, System.Data.DataExpression newExpression)
    {
        if (oldExpression != null)
        {
            foreach (DataColumn column2 in oldExpression.GetDependency())
            {
                column2.RemoveDependentColumn(this);
                if (column2.table != this.table)
                {
                    this.table.RemoveDependentColumn(this);
                }
            }
            this.table.RemoveDependentColumn(this);
        }
        if (newExpression != null)
        {
            foreach (DataColumn column in newExpression.GetDependency())
            {
                column.AddDependentColumn(this);
                if (column.table != this.table)
                {
                    this.table.AddDependentColumn(this);
                }
            }
            this.table.AddDependentColumn(this);
        }
    }

    void Init(int record)
    {
        if (this.AutoIncrement)
        {
            Object current = this.autoInc.Current;
            this.autoInc.MoveAfter();
            this._storage.Set(record, current);
        }
        else
        {
            this[record] = this.defaultValue;
        }
    }

    void InitializeRecord(int record)
    {
        this._storage.Set(record, this.DefaultValue);
    }

    private void InsureStorage()
    {
        if (this._storage == null)
        {
            this._storage = DataStorage.CreateStorage(this, this.dataType, this._storageType);
        }
    }

    void InternalUnique(boolean value)
    {
        this.unique = value;
    }

    static boolean IsAutoIncrementType(Type dataType)
    {
        if (((!(dataType == typeof(int)) && !(dataType == typeof(long))) && (!(dataType == typeof(short)) && !(dataType == typeof(decimal)))) && ((!(dataType == typeof(BigInteger)) && !(dataType == typeof(SqlInt32))) && (!(dataType == typeof(SqlInt64)) && !(dataType == typeof(SqlInt16)))))
        {
            return (dataType == typeof(SqlDecimal));
        }
        return true;
    }

    private boolean IsColumnMappingValid(StorageType typeCode, MappingType mapping)
    {
        if ((mapping != MappingType.Element) && DataStorage.IsTypeCustomType(typeCode))
        {
            return false;
        }
        return true;
    }

    boolean IsInRelation()
    {
        DataRelationCollection parentRelations = this.table.ParentRelations;
        for (int i = 0; i < parentRelations.Count; i++)
        {
            if (parentRelations[i].ChildKey.ContainsColumn(this))
            {
                return true;
            }
        }
        parentRelations = this.table.ChildRelations;
        for (int j = 0; j < parentRelations.Count; j++)
        {
            if (parentRelations[j].ParentKey.ContainsColumn(this))
            {
                return true;
            }
        }
        return false;
    }

    boolean IsMaxLengthViolated()
    {
        if (this.MaxLength < 0)
        {
            return true;
        }
        boolean flag = false;
        String error = null;
        foreach (DataRow row in this.Table.Rows)
        {
            if (row.HasVersion(DataRowVersion.Current))
            {
                Object obj2 = row[this];
                if (!this.isSqlType)
                {
                    if (((obj2 != null) && (obj2 != DBNull.Value)) && (((String) obj2).Length > this.MaxLength))
                    {
                        if (error == null)
                        {
                            error = ExceptionBuilder.MaxLengthViolationText(this.ColumnName);
                        }
                        row.RowError = error;
                        row.SetColumnError(this, error);
                        flag = true;
                    }
                }
                else if (!DataStorage.IsObjectNull(obj2))
                {
                    SqlString str2 = (SqlString) obj2;
                    if (str2.Value.Length > this.MaxLength)
                    {
                        if (error == null)
                        {
                            error = ExceptionBuilder.MaxLengthViolationText(this.ColumnName);
                        }
                        row.RowError = error;
                        row.SetColumnError(this, error);
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    boolean IsNotAllowDBNullViolated()
    {
        Index sortIndex = this.SortIndex;
        DataRow[] rows = sortIndex.GetRows(sortIndex.FindRecords(DBNull.Value));
        for (int i = 0; i < rows.Length; i++)
        {
            String error = ExceptionBuilder.NotAllowDBNullViolationText(this.ColumnName);
            rows[i].RowError = error;
            rows[i].SetColumnError(this, error);
        }
        return (rows.Length > 0);
    }

    boolean IsNull(int record)
    {
        return this._storage.IsNull(record);
    }

    boolean IsValueCustomTypeInstance(Object value)
    {
        return (DataStorage.IsTypeCustomType(value.GetType()) && !(value is Type));
    }

    protected  void OnPropertyChanging(PropertyChangedEventArgs pcevent)
    {
        if (this.onPropertyChangingDelegate != null)
        {
            this.onPropertyChangingDelegate(this, pcevent);
        }
    }

    void OnSetDataSet()
    {
    }

    protected void RaisePropertyChanging(String name)
    {
        this.OnPropertyChanging(new PropertyChangedEventArgs(name));
    }

    void RemoveDependentColumn(DataColumn expressionColumn)
    {
        if ((this.dependentColumns != null) && this.dependentColumns.Contains(expressionColumn))
        {
            this.dependentColumns.Remove(expressionColumn);
        }
        this.table.RemoveDependentColumn(expressionColumn);
    }

    private void ResetCaption()
    {
        if (this.caption != null)
        {
            this.caption = null;
        }
    }

    private void ResetNamespace()
    {
        this.Namespace = null;
    }

    void SetCapacity(int capacity)
    {
        this.InsureStorage();
        this._storage.SetCapacity(capacity);
    }

    private void SetMaxLengthSimpleType()
    {
        if (this.simpleType != null)
        {
            this.simpleType.MaxLength = this.maxLength;
            if (this.simpleType.IsPlainString())
            {
                this.simpleType = null;
            }
            else if ((this.simpleType.Name != null) && (this.dttype != null))
            {
                this.simpleType.ConvertToAnnonymousSimpleType();
                this.dttype = null;
            }
        }
        else if (-1 < this.maxLength)
        {
            this.SimpleType = System.Data.SimpleType.CreateLimitedStringType(this.maxLength);
        }
    }

    public void SetOrdinal(int ordinal)
    {
        if (this._ordinal == -1)
        {
            throw ExceptionBuilder.ColumnNotInAnyTable();
        }
        if (this._ordinal != ordinal)
        {
            this.table.Columns.MoveTo(this, ordinal);
        }
    }

    void SetOrdinalInternal(int ordinal)
    {
        if (this._ordinal != ordinal)
        {
            if ((this.Unique && (this._ordinal != -1)) && (ordinal == -1))
            {
                UniqueConstraint constraint = this.table.Constraints.FindKeyConstraint(this);
                if (constraint != null)
                {
                    this.table.Constraints.Remove(constraint);
                }
            }
            if ((this.sortIndex != null) && (-1 == ordinal))
            {
                this.sortIndex.RemoveRef();
                this.sortIndex.RemoveRef();
                this.sortIndex = null;
            }
            int num = this._ordinal;
            this._ordinal = ordinal;
            if (((num == -1) && (this._ordinal != -1)) && this.Unique)
            {
                UniqueConstraint constraint2 = new UniqueConstraint(this);
                this.table.Constraints.Add(constraint2);
            }
        }
    }

    void SetStorage(Object store, BitArray nullbits)
    {
        this.InsureStorage();
        this._storage.SetStorageInternal(store, nullbits);
    }

    void SetTable(DataTable table)
    {
        if (this.table != table)
        {
            if (this.Computed && ((table == null) || (!table.fInitInProgress && ((table.DataSet == null) || (!table.DataSet.fIsSchemaLoading && !table.DataSet.fInitInProgress)))))
            {
                this.DataExpression.Bind(table);
            }
            if (this.Unique && (this.table != null))
            {
                UniqueConstraint constraint = table.Constraints.FindKeyConstraint(this);
                if (constraint != null)
                {
                    table.Constraints.CanRemove(constraint, true);
                }
            }
            this.table = table;
            this._storage = null;
        }
    }

    void SetValue(int record, Object value)
    {
        try
        {
            this._storage.Set(record, value);
        }
        catch (Exception exception)
        {
            ExceptionBuilder.TraceExceptionForCapture(exception);
            throw ExceptionBuilder.SetFailed(value, this, this.DataType, exception);
        }
        DataRow dataRow = this.GetDataRow(record);
        if (dataRow != null)
        {
            dataRow.LastChangedColumn = this;
        }
    }

    private boolean ShouldSerializeCaption()
    {
        return (this.caption != null);
    }

    private boolean ShouldSerializeDefaultValue()
    {
        return !this.DefaultValueIsNull;
    }

    private boolean ShouldSerializeNamespace()
    {
        return (this._columnUri != null);
    }
    @Override
    public String toString()
    {
        if (this.expression == null)
        {
            return this.ColumnName;
        }
        return (this.ColumnName + " + " + this.Expression);
    }

    private void UpdateColumnType(Type type)
    {
        this.dataType = type;
    }

    public boolean AllowDBNull
    {
        get
        {
            return this.allowNull;
        }
        set
        {
            IntPtr ptr;
            Bid.ScopeEnter(out ptr, "<ds.DataColumn.set_AllowDBNull|API> %d#, %d{boolean}\n", this.ObjectID, value);
            try
            {
                if (this.allowNull != value)
                {
                    if (((this.table != null) && !value) && this.table.EnforceConstraints)
                    {
                        this.CheckNotAllowNull();
                    }
                    this.allowNull = value;
                }
            }
            finally
            {
                Bid.ScopeLeave(ref ptr);
            }
        }
    }

    AutoIncrementValue AutoInc
    {
        get
        {
            return (this.autoInc ?? (this.autoInc = (this.DataType == typeof(BigInteger)) ? ((AutoIncrementValue) new AutoIncrementBigInteger()) : ((AutoIncrementValue) new AutoIncrementInt64())));
        }
    }

    [System.Data.ResCategory("DataCategory_Data"), RefreshProperties(RefreshProperties.All), DefaultValue(false), System.Data.ResDescription("DataColumnAutoIncrementDescr")]
    public boolean AutoIncrement
    {
        get
        {
            return ((this.autoInc != null) && this.autoInc.Auto);
        }
        set
        {
            Bid.Trace("<ds.DataColumn.set_AutoIncrement|API> %d#, %d{boolean}\n", this.ObjectID, value);
            if (this.AutoIncrement != value)
            {
                if (value)
                {
                    if (this.expression != null)
                    {
                        throw ExceptionBuilder.AutoIncrementAndExpression();
                    }
                    if (!this.DefaultValueIsNull)
                    {
                        throw ExceptionBuilder.AutoIncrementAndDefaultValue();
                    }
                    if (!IsAutoIncrementType(this.DataType))
                    {
                        if (this.HasData)
                        {
                            throw ExceptionBuilder.AutoIncrementCannotSetIfHasData(this.DataType.Name);
                        }
                        this.DataType = typeof(int);
                    }
                }
                this.AutoInc.Auto = value;
            }
        }
    }

    Object AutoIncrementCurrent
    {
        get
        {
            if (this.autoInc == null)
            {
                return this.AutoIncrementSeed;
            }
            return this.autoInc.Current;
        }
        set
        {
            if (this.AutoIncrementSeed != BigIntegerStorage.ConvertToBigInteger(value, this.FormatProvider))
            {
                this.AutoInc.SetCurrent(value, this.FormatProvider);
            }
        }
    }

    [System.Data.ResCategory("DataCategory_Data"), DefaultValue((long) 0L), System.Data.ResDescription("DataColumnAutoIncrementSeedDescr")]
    public long AutoIncrementSeed
    {
        get
        {
            if (this.autoInc == null)
            {
                return 0L;
            }
            return this.autoInc.Seed;
        }
        set
        {
            Bid.Trace("<ds.DataColumn.set_AutoIncrementSeed|API> %d#, %I64d\n", this.ObjectID, value);
            if (this.AutoIncrementSeed != value)
            {
                this.AutoInc.Seed = value;
            }
        }
    }

    [DefaultValue((long) 1L), System.Data.ResCategory("DataCategory_Data"), System.Data.ResDescription("DataColumnAutoIncrementStepDescr")]
    public long AutoIncrementStep
    {
        get
        {
            if (this.autoInc == null)
            {
                return 1L;
            }
            return this.autoInc.Step;
        }
        set
        {
            Bid.Trace("<ds.DataColumn.set_AutoIncrementStep|API> %d#, %I64d\n", this.ObjectID, value);
            if (this.AutoIncrementStep != value)
            {
                this.AutoInc.Step = value;
            }
        }
    }

    [System.Data.ResDescription("DataColumnCaptionDescr"), System.Data.ResCategory("DataCategory_Data")]
    public String Caption
    {
        get
        {
            if (this.caption == null)
            {
                return this._columnName;
            }
            return this.caption;
        }
        set
        {
            if (value == null)
            {
                value = "";
            }
            if ((this.caption == null) || (String.Compare(this.caption, value, true, this.Locale) != 0))
            {
                this.caption = value;
            }
        }
    }

    [System.Data.ResDescription("DataColumnMappingDescr"), DefaultValue(1)]
    public  MappingType ColumnMapping
    {
        get
        {
            return this.columnMapping;
        }
        set
        {
            Bid.Trace("<ds.DataColumn.set_ColumnMapping|API> %d#, %d{ds.MappingType}\n", this.ObjectID, (int) value);
            if (value != this.columnMapping)
            {
                if ((value == MappingType.SimpleContent) && (this.table != null))
                {
                    int num = 0;
                    if (this.columnMapping == MappingType.Element)
                    {
                        num = 1;
                    }
                    if (this.dataType == typeof(char))
                    {
                        throw ExceptionBuilder.CannotSetSimpleContent(this.ColumnName, this.dataType);
                    }
                    if ((this.table.XmlText != null) && (this.table.XmlText != this))
                    {
                        throw ExceptionBuilder.CannotAddColumn3();
                    }
                    if (this.table.ElementColumnCount > num)
                    {
                        throw ExceptionBuilder.CannotAddColumn4(this.ColumnName);
                    }
                }
                this.RaisePropertyChanging("ColumnMapping");
                if (this.table != null)
                {
                    if (this.columnMapping == MappingType.SimpleContent)
                    {
                        this.table.xmlText = null;
                    }
                    if (value == MappingType.Element)
                    {
                        this.table.ElementColumnCount++;
                    }
                    else if (this.columnMapping == MappingType.Element)
                    {
                        this.table.ElementColumnCount--;
                    }
                }
                this.columnMapping = value;
                if (value == MappingType.SimpleContent)
                {
                    this._columnUri = null;
                    if (this.table != null)
                    {
                        this.table.XmlText = this;
                    }
                    this.SimpleType = null;
                }
            }
        }
    }

    [System.Data.ResCategory("DataCategory_Data"), RefreshProperties(RefreshProperties.All), System.Data.ResDescription("DataColumnColumnNameDescr"), DefaultValue("")]
    public String ColumnName
    {
        get
        {
            return this._columnName;
        }
        set
        {
            IntPtr ptr;
            Bid.ScopeEnter(out ptr, "<ds.DataColumn.set_ColumnName|API> %d#, '%ls'\n", this.ObjectID, value);
            try
            {
                if (value == null)
                {
                    value = "";
                }
                if (String.Compare(this._columnName, value, true, this.Locale) != 0)
                {
                    if (this.table != null)
                    {
                        if (value.Length == 0)
                        {
                            throw ExceptionBuilder.ColumnNameRequired();
                        }
                        this.table.Columns.RegisterColumnName(value, this);
                        if (this._columnName.Length != 0)
                        {
                            this.table.Columns.UnregisterName(this._columnName);
                        }
                    }
                    this.RaisePropertyChanging("ColumnName");
                    this._columnName = value;
                    this.encodedColumnName = null;
                    if (this.table != null)
                    {
                        this.table.Columns.OnColumnPropertyChanged(new CollectionChangeEventArgs(CollectionChangeAction.Refresh, this));
                    }
                }
                else if (this._columnName != value)
                {
                    this.RaisePropertyChanging("ColumnName");
                    this._columnName = value;
                    this.encodedColumnName = null;
                    if (this.table != null)
                    {
                        this.table.Columns.OnColumnPropertyChanged(new CollectionChangeEventArgs(CollectionChangeAction.Refresh, this));
                    }
                }
            }
            finally
            {
                Bid.ScopeLeave(ref ptr);
            }
        }
    }

    boolean Computed
    {
        get
        {
            return (this.expression != null);
        }
    }

    System.Data.DataExpression DataExpression
    {
        get
        {
            return this.expression;
        }
    }

    [System.Data.ResCategory("DataCategory_Data"), DefaultValue(typeof(String)), System.Data.ResDescription("DataColumnDataTypeDescr"), TypeConverter(typeof(ColumnTypeConverter)), RefreshProperties(RefreshProperties.All)]
    public Type DataType
    {
        get
        {
            return this.dataType;
        }
        set
        {
            if (this.dataType != value)
            {
                if (this.HasData)
                {
                    throw ExceptionBuilder.CantChangeDataType();
                }
                if (value == null)
                {
                    throw ExceptionBuilder.NullDataType();
                }
                StorageType storageType = DataStorage.GetStorageType(value);
                if (DataStorage.ImplementsINullableValue(storageType, value))
                {
                    throw ExceptionBuilder.ColumnTypeNotSupported();
                }
                if ((this.table != null) && this.IsInRelation())
                {
                    throw ExceptionBuilder.ColumnsTypeMismatch();
                }
                if ((storageType == StorageType.BigInteger) && (this.expression != null))
                {
                    throw ExprException.UnsupportedDataType(value);
                }
                if (!this.DefaultValueIsNull)
                {
                    try
                    {
                        if (this.defaultValue is BigInteger)
                        {
                            this.defaultValue = BigIntegerStorage.ConvertFromBigInteger((BigInteger) this.defaultValue, value, this.FormatProvider);
                        }
                        else if (typeof(BigInteger) == value)
                    {
                        this.defaultValue = BigIntegerStorage.ConvertToBigInteger(this.defaultValue, this.FormatProvider);
                    }
                    else if (typeof(String) == value)
                    {
                        this.defaultValue = this.DefaultValue.ToString();
                    }
                    else if (typeof(SqlString) == value)
                    {
                        this.defaultValue = SqlConvert.ConvertToSqlString(this.DefaultValue);
                    }
                    else if (typeof(Object) != value)
                    {
                        this.DefaultValue = SqlConvert.ChangeTypeForDefaultValue(this.DefaultValue, value, this.FormatProvider);
                    }
                    }
                    catch (InvalidCastException exception2)
                    {
                        throw ExceptionBuilder.DefaultValueDataType(this.ColumnName, this.DefaultValue.GetType(), value, exception2);
                    }
                    catch (FormatException exception)
                    {
                        throw ExceptionBuilder.DefaultValueDataType(this.ColumnName, this.DefaultValue.GetType(), value, exception);
                    }
                }
                if ((this.ColumnMapping == MappingType.SimpleContent) && (value == typeof(char)))
                {
                    throw ExceptionBuilder.CannotSetSimpleContentType(this.ColumnName, value);
                }
                this.SimpleType = System.Data.SimpleType.CreateSimpleType(storageType, value);
                if (StorageType.String == storageType)
                {
                    this.maxLength = -1;
                }
                this.UpdateColumnType(value, storageType);
                this.XmlDataType = null;
                if (this.AutoIncrement)
                {
                    if (!IsAutoIncrementType(value))
                    {
                        this.AutoIncrement = false;
                    }
                    if (this.autoInc != null)
                    {
                        AutoIncrementValue autoInc = this.autoInc;
                        this.autoInc = null;
                        this.AutoInc.Auto = autoInc.Auto;
                        this.AutoInc.Seed = autoInc.Seed;
                        this.AutoInc.Step = autoInc.Step;
                        if (this.autoInc.DataType == autoInc.DataType)
                        {
                            this.autoInc.Current = autoInc.Current;
                        }
                        else if (autoInc.DataType == typeof(long))
                        {
                            this.AutoInc.Current = (long) autoInc.Current;
                        }
                        else
                        {
                            this.AutoInc.Current = (long) ((BigInteger) autoInc.Current);
                        }
                    }
                }
            }
        }
    }

    [System.Data.ResDescription("DataColumnDateTimeModeDescr"), System.Data.ResCategory("DataCategory_Data"), RefreshProperties(RefreshProperties.All), DefaultValue(3)]
    public DataSetDateTime DateTimeMode
    {
        get
        {
            return this._dateTimeMode;
        }
        set
        {
            if (this._dateTimeMode != value)
            {
                if ((this.DataType != typeof(DateTime)) && (value != DataSetDateTime.UnspecifiedLocal))
                {
                    throw ExceptionBuilder.CannotSetDateTimeModeForNonDateTimeColumns();
                }
                switch (value)
                {
                    case DataSetDateTime.Local:
                    case DataSetDateTime.Utc:
                        if (this.HasData)
                        {
                            throw ExceptionBuilder.CantChangeDateTimeMode(this._dateTimeMode, value);
                        }
                        break;

                    case DataSetDateTime.Unspecified:
                    case DataSetDateTime.UnspecifiedLocal:
                        if (((this._dateTimeMode != DataSetDateTime.Unspecified) && (this._dateTimeMode != DataSetDateTime.UnspecifiedLocal)) && this.HasData)
                        {
                            throw ExceptionBuilder.CantChangeDateTimeMode(this._dateTimeMode, value);
                        }
                        break;

                    default:
                        throw ExceptionBuilder.InvalidDateTimeMode(value);
                }
                this._dateTimeMode = value;
            }
        }
    }

    [System.Data.ResCategory("DataCategory_Data"), System.Data.ResDescription("DataColumnDefaultValueDescr"), TypeConverter(typeof(DefaultValueTypeConverter))]
    public Object DefaultValue
    {
        get
        {
            if ((this.defaultValue == DBNull.Value) && this.implementsINullable)
            {
                if (this._storage != null)
                {
                    this.defaultValue = this._storage.NullValue;
                }
                else if (this.isSqlType)
                {
                    this.defaultValue = SqlConvert.ChangeTypeForDefaultValue(this.defaultValue, this.dataType, this.FormatProvider);
                }
                else if (this.implementsINullable)
                {
                    PropertyInfo property = this.dataType.GetProperty("Null", BindingFlags.Public | BindingFlags.Static);
                    if (property != null)
                    {
                        this.defaultValue = property.GetValue(null, null);
                    }
                }
            }
            return this.defaultValue;
        }
        set
        {
            Bid.Trace("<ds.DataColumn.set_DefaultValue|API> %d#\n", this.ObjectID);
            if ((this.defaultValue == null) || !this.DefaultValue.Equals(value))
            {
                if (this.AutoIncrement)
                {
                    throw ExceptionBuilder.DefaultValueAndAutoIncrement();
                }
                Object obj2 = (value == null) ? DBNull.Value : value;
                if ((obj2 != DBNull.Value) && (this.DataType != typeof(Object)))
                {
                    try
                    {
                        obj2 = SqlConvert.ChangeTypeForDefaultValue(obj2, this.DataType, this.FormatProvider);
                    }
                    catch (InvalidCastException exception)
                    {
                        throw ExceptionBuilder.DefaultValueColumnDataType(this.ColumnName, obj2.GetType(), this.DataType, exception);
                    }
                }
                this.defaultValue = obj2;
                this.defaultValueIsNull = (obj2 == DBNull.Value) || (this.ImplementsINullable && DataStorage.IsObjectSqlNull(obj2));
            }
        }
    }

    boolean DefaultValueIsNull
    {
        get
        {
            return this.defaultValueIsNull;
        }
    }

    String EncodedColumnName
    {
        get
        {
            if (this.encodedColumnName == null)
            {
                this.encodedColumnName = XmlConvert.EncodeLocalName(this.ColumnName);
            }
            return this.encodedColumnName;
        }
    }

    [DefaultValue(""), System.Data.ResCategory("DataCategory_Data"), RefreshProperties(RefreshProperties.All), System.Data.ResDescription("DataColumnExpressionDescr")]
    public String Expression
    {
        get
        {
            if (this.expression != null)
            {
                return this.expression.Expression;
            }
            return "";
        }
        set
        {
            IntPtr ptr;
            Bid.ScopeEnter(out ptr, "<ds.DataColumn.set_Expression|API> %d#, '%ls'\n", this.ObjectID, value);
            if (value == null)
            {
                value = "";
            }
            try
            {
                System.Data.DataExpression newExpression = null;
                if (value.Length > 0)
                {
                    System.Data.DataExpression expression3 = new System.Data.DataExpression(this.table, value, this.dataType);
                    if (expression3.HasValue)
                    {
                        newExpression = expression3;
                    }
                }
                if ((this.expression == null) && (newExpression != null))
                {
                    if (this.AutoIncrement || this.Unique)
                    {
                        throw ExceptionBuilder.ExpressionAndUnique();
                    }
                    if (this.table != null)
                    {
                        for (int i = 0; i < this.table.Constraints.Count; i++)
                        {
                            if (this.table.Constraints[i].ContainsColumn(this))
                            {
                                throw ExceptionBuilder.ExpressionAndConstraint(this, this.table.Constraints[i]);
                            }
                        }
                    }
                    boolean final = this.final;
                    try
                    {
                        this.final = true;
                    }
                    catch (ReadOnlyException exception3)
                    {
                        ExceptionBuilder.TraceExceptionForCapture(exception3);
                        this.final = final;
                        throw ExceptionBuilder.ExpressionAndReadOnly();
                    }
                }
                if (this.table != null)
                {
                    if ((newExpression != null) && newExpression.DependsOn(this))
                    {
                        throw ExceptionBuilder.ExpressionCircular();
                    }
                    this.HandleDependentColumnList(this.expression, newExpression);
                    System.Data.DataExpression expression = this.expression;
                    this.expression = newExpression;
                    try
                    {
                        if (newExpression == null)
                        {
                            for (int j = 0; j < this.table.RecordCapacity; j++)
                            {
                                this.InitializeRecord(j);
                            }
                        }
                        else
                        {
                            this.table.EvaluateExpressions(this);
                        }
                        this.table.ResetInternalIndexes(this);
                        this.table.EvaluateDependentExpressions(this);
                        return;
                    }
                    catch (Exception exception2)
                    {
                        if (!ADP.IsCatchableExceptionType(exception2))
                        {
                            throw;
                        }
                        ExceptionBuilder.TraceExceptionForCapture(exception2);
                        try
                        {
                            this.expression = expression;
                            this.HandleDependentColumnList(newExpression, this.expression);
                            if (expression == null)
                            {
                                for (int k = 0; k < this.table.RecordCapacity; k++)
                                {
                                    this.InitializeRecord(k);
                                }
                            }
                            else
                            {
                                this.table.EvaluateExpressions(this);
                            }
                            this.table.ResetInternalIndexes(this);
                            this.table.EvaluateDependentExpressions(this);
                        }
                        catch (Exception exception)
                        {
                            if (!ADP.IsCatchableExceptionType(exception))
                            {
                                throw;
                            }
                            ExceptionBuilder.TraceExceptionWithoutRethrow(exception);
                        }
                        throw;
                    }
                }
                this.expression = newExpression;
            }
            finally
            {
                Bid.ScopeLeave(ref ptr);
            }
        }
    }

    [System.Data.ResDescription("ExtendedPropertiesDescr"), System.Data.ResCategory("DataCategory_Data"), Browsable(false)]
    public PropertyCollection ExtendedProperties
    {
        get
        {
            if (this.extendedProperties == null)
            {
                this.extendedProperties = new PropertyCollection();
            }
            return this.extendedProperties;
        }
    }

    IFormatProvider FormatProvider
    {
        get
        {
            if (this.table == null)
            {
                return CultureInfo.CurrentCulture;
            }
            return this.table.FormatProvider;
        }
    }

    boolean HasData
    {
        get
        {
            return (this._storage != null);
        }
    }

    boolean ImplementsIChangeTracking
    {
        get
        {
            return this.implementsIChangeTracking;
        }
    }

    boolean ImplementsINullable
    {
        get
        {
            return this.implementsINullable;
        }
    }

    boolean ImplementsIRevertibleChangeTracking
    {
        get
        {
            return this.implementsIRevertibleChangeTracking;
        }
    }

    boolean ImplementsIXMLSerializable
    {
        get
        {
            return this.implementsIXMLSerializable;
        }
    }

    boolean IsCloneable
    {
        get
        {
            return this._storage.IsCloneable;
        }
    }

    boolean IsCustomType
    {
        get
        {
            if (this._storage != null)
            {
                return this._storage.IsCustomDefinedType;
            }
            return DataStorage.IsTypeCustomType(this.DataType);
        }
    }

    boolean IsSqlType
    {
        get
        {
            return this.isSqlType;
        }
    }

    boolean IsStringType
    {
        get
        {
            return this._storage.IsStringType;
        }
    }

    boolean IsValueType
    {
        get
        {
            return this._storage.IsValueType;
        }
    }

    Object this[int record]
    {
        get
        {
            return this._storage.Get(record);
        }
        set
        {
            try
            {
                this._storage.Set(record, value);
            }
            catch (Exception exception)
            {
                ExceptionBuilder.TraceExceptionForCapture(exception);
                throw ExceptionBuilder.SetFailed(value, this, this.DataType, exception);
            }
            if (this.AutoIncrement && !this._storage.IsNull(record))
            {
                this.AutoInc.SetCurrentAndIncrement(this._storage.Get(record));
            }
            if (this.Computed)
            {
                DataRow dataRow = this.GetDataRow(record);
                if (dataRow != null)
                {
                    dataRow.LastChangedColumn = this;
                }
            }
        }
    }

    CultureInfo Locale
    {
        get
        {
            if (this.table == null)
            {
                return CultureInfo.CurrentCulture;
            }
            return this.table.Locale;
        }
    }

    [DefaultValue(-1), System.Data.ResDescription("DataColumnMaxLengthDescr"), System.Data.ResCategory("DataCategory_Data")]
    public int MaxLength
    {
        get
        {
            return this.maxLength;
        }
        set
        {
            IntPtr ptr;
            Bid.ScopeEnter(out ptr, "<ds.DataColumn.set_MaxLength|API> %d#, %d\n", this.ObjectID, value);
            try
            {
                if (this.maxLength != value)
                {
                    if (this.ColumnMapping == MappingType.SimpleContent)
                    {
                        throw ExceptionBuilder.CannotSetMaxLength2(this);
                    }
                    if ((this.DataType != typeof(String)) && (this.DataType != typeof(SqlString)))
                    {
                        throw ExceptionBuilder.HasToBeStringType(this);
                    }
                    int maxLength = this.maxLength;
                    this.maxLength = Math.Max(value, -1);
                    if (((maxLength < 0) || (value < maxLength)) && (((this.table != null) && this.table.EnforceConstraints) && !this.CheckMaxLength()))
                    {
                        this.maxLength = maxLength;
                        throw ExceptionBuilder.CannotSetMaxLength(this, value);
                    }
                    this.SetMaxLengthSimpleType();
                }
            }
            finally
            {
                Bid.ScopeLeave(ref ptr);
            }
        }
    }

    [System.Data.ResDescription("DataColumnNamespaceDescr"), System.Data.ResCategory("DataCategory_Data")]
    public String Namespace
    {
        get
        {
            if (this._columnUri != null)
            {
                return this._columnUri;
            }
            if ((this.Table != null) && (this.columnMapping != MappingType.Attribute))
            {
                return this.Table.Namespace;
            }
            return "";
        }
        set
        {
            Bid.Trace("<ds.DataColumn.set_Namespace|API> %d#, '%ls'\n", this.ObjectID, value);
            if (this._columnUri != value)
            {
                if (this.columnMapping != MappingType.SimpleContent)
                {
                    this.RaisePropertyChanging("Namespace");
                    this._columnUri = value;
                }
                else if (value != this.Namespace)
                {
                    throw ExceptionBuilder.CannotChangeNamespace(this.ColumnName);
                }
            }
        }
    }

    int ObjectID
    {
        get
        {
            return this._objectID;
        }
    }

    [System.Data.ResDescription("DataColumnOrdinalDescr"), Browsable(false), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden), System.Data.ResCategory("DataCategory_Data")]
    public int Ordinal
    {
        get
        {
            return this._ordinal;
        }
    }

    [System.Data.ResDescription("DataColumnPrefixDescr"), System.Data.ResCategory("DataCategory_Data"), DefaultValue("")]
    public String Prefix
    {
        get
        {
            return this._columnPrefix;
        }
        set
        {
            if (value == null)
            {
                value = "";
            }
            Bid.Trace("<ds.DataColumn.set_Prefix|API> %d#, '%ls'\n", this.ObjectID, value);
            if ((XmlConvert.DecodeName(value) == value) && (XmlConvert.EncodeName(value) != value))
            {
                throw ExceptionBuilder.InvalidPrefix(value);
            }
            this._columnPrefix = value;
        }
    }

    [System.Data.ResCategory("DataCategory_Data"), System.Data.ResDescription("DataColumnReadOnlyDescr"), DefaultValue(false)]
    public boolean final
    {
        get
        {
            return this.final;
        }
        set
        {
            Bid.Trace("<ds.DataColumn.set_ReadOnly|API> %d#, %d{boolean}\n", this.ObjectID, value);
            if (this.final != value)
            {
                if (!value && (this.expression != null))
                {
                    throw ExceptionBuilder.ReadOnlyAndExpression();
                }
                this.final = value;
            }
        }
    }

    private Index SortIndex
    {
        get
        {
            if (this.sortIndex == null)
            {
                IndexField[] indexDesc = new IndexField[] { new IndexField(this, false) };
                this.sortIndex = this.table.GetIndex(indexDesc, DataViewRowState.CurrentRows, null);
                this.sortIndex.AddRef();
            }
            return this.sortIndex;
        }
    }

    public DataTable Table
    {
        get
        {
            return this.table;
        }
    }

    [System.Data.ResDescription("DataColumnUniqueDescr"), DefaultValue(false), System.Data.ResCategory("DataCategory_Data"), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
    public boolean Unique
    {
        get
        {
            return this.unique;
        }
        set
        {
            IntPtr ptr;
            Bid.ScopeEnter(out ptr, "<ds.DataColumn.set_Unique|API> %d#, %d{boolean}\n", this.ObjectID, value);
            try
            {
                if (this.unique != value)
                {
                    if (value && (this.expression != null))
                    {
                        throw ExceptionBuilder.UniqueAndExpression();
                    }
                    UniqueConstraint constraint2 = null;
                    if (this.table != null)
                    {
                        if (value)
                        {
                            this.CheckUnique();
                        }
                        else
                        {
                            IEnumerator enumerator = this.Table.Constraints.GetEnumerator();
                            while (enumerator.MoveNext())
                            {
                                UniqueConstraint current = enumerator.Current as UniqueConstraint;
                                if (((current != null) && (current.ColumnsReference.Length == 1)) && (current.ColumnsReference[0] == this))
                                {
                                    constraint2 = current;
                                }
                            }
                            this.table.Constraints.CanRemove(constraint2, true);
                        }
                    }
                    this.unique = value;
                    if (this.table != null)
                    {
                        if (value)
                        {
                            UniqueConstraint constraint = new UniqueConstraint(this);
                            this.table.Constraints.Add(constraint);
                        }
                        else
                        {
                            this.table.Constraints.Remove(constraint2);
                        }
                    }
                }
            }
            finally
            {
                Bid.ScopeLeave(ref ptr);
            }
        }
    }

    String XmlDataType
    {
        get
        {
            return this.dttype;
        }
        set
        {
            this.dttype = value;
        }
    }
}

