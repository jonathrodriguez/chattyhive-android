package com.chattyhive.backend.Util.Data;

import java.math.BigInteger;

/**
 * Created by Jonathan on 14/03/2015.
 */
public class DataTable {
    private boolean _caseSensitive;
    private boolean _caseSensitiveUserSet;
    protected DataColumn _colUnique;
    private CompareOptions _compareFlags;
    private System.Globalization.CompareInfo _compareInfo;
    private CultureInfo _culture;
    private boolean _cultureUserSet;
    private  final List<DataViewListener> _dataViewListeners;
    private IFormatProvider _formatProvider;
    private StringComparer _hashCodeProvider;
    private byte _isTypedDataTable;
    private DataRelation[] _nestedParentRelations;
    private final int _objectID;
    private static int _objectTypeCount;
    protected IndexField[] _primaryIndex;
    private SerializationFormat _remotingFormat;
    protected boolean _suspendEnforceConstraints;
    private int _suspendIndexEvents;
    protected DataRelationCollection childRelationsCollection;
    protected  final DataColumnCollection columnCollection;
    private  final ConstraintCollection constraintCollection;
    private System.Data.DataSet dataSet;
    private DataView defaultView;
    private DataColumn[] delayedSetPrimaryKey;
    protected  final List<DataView> delayedViews;
    protected List<DataColumn> dependentColumns;
    protected DataExpression displayExpression;
    private int elementColumnCount;
    private static  final DataRelation[] EmptyArrayDataRelation = new DataRelation[0];
    private DataRow[] EmptyDataRowArray;
    protected String encodedTableName;
    private boolean enforceConstraints;
    protected PropertyCollection extendedProperties;
    protected boolean fInitInProgress;
    protected boolean fInLoadDiffgram;
    protected boolean fNestedInDataset;
    private boolean inDataLoad;
    protected  final List<Index> indexes;
    protected  final ReaderWriterLock indexesLock;
    private boolean initialLoad;
    private boolean inLoad;
    private final String KEY_NAME = "TableName";
    private final String KEY_XMLDIFFGRAM = "XmlDiffGram";
    private final String KEY_XMLSCHEMA = "XmlSchema";
    private Index loadIndex;
    private Index loadIndexwithCurrentDeleted;
    private Index loadIndexwithOriginalAdded;
    protected BigInteger maxOccurs;
    private boolean mergingData;
    protected BigInteger minOccurs;
    protected long nextRowID;
    protected DataRelationCollection parentRelationsCollection;
    protected UniqueConstraint primaryKey;
    private PropertyDescriptorCollection propertyDescriptorCollectionCache;
    protected  final RecordManager recordManager;
    protected boolean repeatableElement;
    private  final DataRowBuilder rowBuilder;
    protected  final DataRowCollection rowCollection;
    protected Hashtable rowDiffId;
    private boolean savedEnforceConstraints;
    private boolean schemaLoading;
    private int shadowCount;
    private List<Index> shadowIndexes;
    private String tableName;
    protected String tableNamespace;
    private String tablePrefix;
    protected boolean textOnly;
    private  Object typeName;
    protected int ukColumnPositionForInference;
    protected DataColumn xmlText;
    protected static  final DataColumn[] zeroColumns = new DataColumn[0];
    protected static  final IndexField[] zeroIndexField = new IndexField[0];
    private static  final int[] zeroIntegers = new int[0];
    protected static  final DataRow[] zeroRows = new DataRow[0];


    public DataTable()
    {
        this.tableName = "";
        this.tablePrefix = "";
        this.fNestedInDataset = true;
        this._compareFlags = CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreCase;
        this.minOccurs = 1M;
        this.maxOccurs = 1M;
        this._primaryIndex = zeroIndexField;
        this.enforceConstraints = true;
        this._nestedParentRelations = EmptyArrayDataRelation;
        this.delayedViews = new List<DataView>();
        this._dataViewListeners = new List<DataViewListener>();
        this.indexesLock = new ReaderWriterLock();
        this.ukColumnPositionForInference = -1;
        this._objectID = Interlocked.Increment(ref _objectTypeCount);
        GC.SuppressFinalize(this);
        Bid.Trace("<ds.DataTable.DataTable|API> %d#\n", this.ObjectID);
        this.nextRowID = 1L;
        this.recordManager = new RecordManager(this);
        this._culture = CultureInfo.CurrentCulture;
        this.columnCollection = new DataColumnCollection(this);
        this.constraintCollection = new ConstraintCollection(this);
        this.rowCollection = new DataRowCollection(this);
        this.indexes = new List<Index>();
        this.rowBuilder = new DataRowBuilder(this, -1);
    }

    public DataTable(String tableName) : this()
    {
        this.tableName = (tableName == null) ? "" : tableName;
    }

    private DataTable(SerializationInfo info, StreamingContext context) : this()
    {
        boolean isSingleTable = (context.Context != null) ? Convert.ToBoolean(context.Context, CultureInfo.InvariantCulture) : true;
        SerializationFormat xml = SerializationFormat.Xml;
        SerializationInfoEnumerator enumerator = info.GetEnumerator();
        while (enumerator.MoveNext())
        {
            String str;
            if (((str = enumerator.Name) != null) && (str == "DataTable.RemotingFormat"))
            {
                xml = (SerializationFormat) enumerator.Value;
            }
        }
        this.DeserializeDataTable(info, context, isSingleTable, xml);
    }

    public DataTable(String tableName, String tableNamespace) : this(tableName)
    {
        this.Namespace = tableNamespace;
    }

    public void AcceptChanges()
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.AcceptChanges|API> %d#\n", this.ObjectID);
        try
        {
            DataRow[] array = new DataRow[this.Rows.Count];
            this.Rows.CopyTo(array, 0);
            this.SuspendIndexEvents();
            try
            {
                for (int i = 0; i < array.Length; i++)
                {
                    if (array[i].rowID != -1L)
                    {
                        array[i].AcceptChanges();
                    }
                }
            }
            finally
            {
                this.RestoreIndexEvents(false);
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    protected void AddDependentColumn(DataColumn expressionColumn)
    {
        if (this.dependentColumns == null)
        {
            this.dependentColumns = new List<DataColumn>();
        }
        if (!this.dependentColumns.Contains(expressionColumn))
        {
            this.dependentColumns.Add(expressionColumn);
        }
    }

    protected DataColumn AddForeignKey(DataColumn parentKey)
    {
        DataColumn column = new DataColumn(XMLSchema.GenUniqueColumnName(parentKey.ColumnName, this), parentKey.DataType, null, MappingType.Hidden);
        this.Columns.Add(column);
        return column;
    }

    protected DataRow AddRecords(int oldRecord, int newRecord)
    {
        DataRow row;
        if ((oldRecord == -1) && (newRecord == -1))
        {
            row = this.NewRow(-1);
            this.AddRow(row);
            return row;
        }
        row = this.NewEmptyRow();
        row.oldRecord = oldRecord;
        row.newRecord = newRecord;
        this.InsertRow(row, -1L);
        return row;
    }

    protected void AddRow(DataRow row)
    {
        this.AddRow(row, -1);
    }

    protected void AddRow(DataRow row, int proposedID)
    {
        this.InsertRow(row, proposedID, -1);
    }

    protected DataColumn AddUniqueKey()
    {
        return this.AddUniqueKey(-1);
    }

    protected DataColumn AddUniqueKey(int position)
    {
        if (this._colUnique == null)
        {
            DataColumn[] primaryKey = this.PrimaryKey;
            if (primaryKey.Length == 1)
            {
                return primaryKey[0];
            }
            DataColumn column = new DataColumn(XMLSchema.GenUniqueColumnName(this.TableName + "_Id", this), typeof(int), null, MappingType.Hidden) {
            Prefix = this.tablePrefix,
                    AutoIncrement = true,
                    AllowDBNull = false,
                    Unique = true
        };
            if (position == -1)
            {
                this.Columns.Add(column);
            }
            else
            {
                for (int i = this.Columns.Count - 1; i >= position; i--)
                {
                    this.Columns[i].SetOrdinalInternal(i + 1);
                }
                this.Columns.AddAt(position, column);
                column.SetOrdinalInternal(position);
            }
            if (primaryKey.Length == 0)
            {
                this.PrimaryKey = new DataColumn[] { column };
            }
            this._colUnique = column;
        }
        return this._colUnique;
    }

    public void BeginInit()
    {
        this.fInitInProgress = true;
    }

    public void BeginLoadData()
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.BeginLoadData|API> %d#\n", this.ObjectID);
        try
        {
            if (!this.inDataLoad)
            {
                this.inDataLoad = true;
                this.loadIndex = null;
                this.initialLoad = this.Rows.Count == 0;
                if (this.initialLoad)
                {
                    this.SuspendIndexEvents();
                }
                else
                {
                    if (this.primaryKey != null)
                    {
                        this.loadIndex = this.primaryKey.Key.GetSortIndex(DataViewRowState.OriginalRows);
                    }
                    if (this.loadIndex != null)
                    {
                        this.loadIndex.AddRef();
                    }
                }
                if (this.DataSet != null)
                {
                    this.savedEnforceConstraints = this.DataSet.EnforceConstraints;
                    this.DataSet.EnforceConstraints = false;
                }
                else
                {
                    this.EnforceConstraints = false;
                }
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    protected void CacheNestedParent()
    {
        this._nestedParentRelations = this.FindNestedParentRelations();
    }

    protected void CascadeAll(DataRow row, DataRowAction action)
    {
        if ((this.DataSet != null) && this.DataSet.fEnableCascading)
        {
            ParentForeignKeyConstraintEnumerator enumerator = new ParentForeignKeyConstraintEnumerator(this.dataSet, this);
            while (enumerator.GetNext())
            {
                enumerator.GetForeignKeyConstraint().CheckCascade(row, action);
            }
        }
    }

    protected void CheckCascadingNamespaceConflict(String realNamespace)
    {
        for(DataRelation relation : this.ChildRelations)
        {
            if ((relation.Nested && (relation.ChildTable != this)) && (relation.ChildTable.tableNamespace == null))
            {
                DataTable childTable = relation.ChildTable;
                if (this.dataSet.Tables.Contains(childTable.TableName, realNamespace, false, true))
                {
                    throw ExceptionBuilder.DuplicateTableName2(this.TableName, realNamespace);
                }
                childTable.CheckCascadingNamespaceConflict(realNamespace);
            }
        }
    }

    private boolean CheckForClosureOnExpressions(DataTable dt, boolean writeHierarchy)
    {
        List<DataTable> tableList = new List<DataTable> {
        dt
    };
        if (writeHierarchy)
        {
            this.CreateTableList(dt, tableList);
        }
        return this.CheckForClosureOnExpressionTables(tableList);
    }

    private boolean CheckForClosureOnExpressionTables(List<DataTable> tableList)
    {
        for(DataTable table : tableList)
        {
            for(DataColumn column : table.Columns)
            {
                if (column.Expression.Length != 0)
                {
                    DataColumn[] dependency = column.DataExpression.GetDependency();
                    for (int i = 0; i < dependency.Length; i++)
                    {
                        if (!tableList.Contains(dependency[i].Table))
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    protected void CheckNamespaceValidityForNestedParentRelations(String ns, DataTable parentTable)
    {
        for(DataRelation relation : this.ParentRelations)
        {
            if ((relation.Nested && (relation.ParentTable != parentTable)) && (relation.ParentTable.Namespace != ns))
            {
                throw ExceptionBuilder.InValidNestedRelation(this.TableName);
            }
        }
    }

    protected void CheckNamespaceValidityForNestedRelations(String realNamespace)
    {
        for(DataRelation relation : this.ChildRelations)
        {
            if (relation.Nested)
            {
                if (realNamespace != null)
                {
                    relation.ChildTable.CheckNamespaceValidityForNestedParentRelations(realNamespace, this);
                }
                else
                {
                    relation.ChildTable.CheckNamespaceValidityForNestedParentRelations(this.GetInheritedNamespace(new List<DataTable>()), this);
                }
            }
        }
        if (realNamespace == null)
        {
            this.CheckNamespaceValidityForNestedParentRelations(this.GetInheritedNamespace(new List<DataTable>()), this);
        }
    }

    protected void CheckNotModifying(DataRow row)
    {
        if (row.tempRecord != -1)
        {
            row.EndEdit();
        }
    }

    private void CheckPrimaryKey()
    {
        if (this.primaryKey == null)
        {
            throw ExceptionBuilder.TableMissingPrimaryKey();
        }
    }

    public void Clear()
    {
        this.Clear(true);
    }

    protected void Clear(boolean clearAll)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.Clear|INFO> %d#, clearAll=%d{boolean}\n", this.ObjectID, clearAll);
        try
        {
            this.rowDiffId = null;
            if (this.dataSet != null)
            {
                this.dataSet.OnClearFunctionCalled(this);
            }
            boolean flag = this.Rows.Count != 0;
            DataTableClearEventArgs e = null;
            if (flag)
            {
                e = new DataTableClearEventArgs(this);
                this.OnTableClearing(e);
            }
            if ((this.dataSet != null) && this.dataSet.EnforceConstraints)
            {
                ParentForeignKeyConstraintEnumerator enumerator3 = new ParentForeignKeyConstraintEnumerator(this.dataSet, this);
                while (enumerator3.GetNext())
                {
                    enumerator3.GetForeignKeyConstraint().CheckCanClearParentTable(this);
                }
            }
            this.recordManager.Clear(clearAll);
            for(DataRow row : this.Rows)
            {
                row.oldRecord = -1;
                row.newRecord = -1;
                row.tempRecord = -1;
                row.rowID = -1L;
                row.RBTreeNodeId = 0;
            }
            this.Rows.ArrayClear();
            this.ResetIndexes();
            if (flag)
            {
                this.OnTableCleared(e);
            }
            for(DataColumn column : this.Columns)
            {
                this.EvaluateDependentExpressions(column);
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    public DataTable Clone()
    {
        return this.Clone(null);
    }

    protected DataTable Clone(System.Data.DataSet cloneDS)
    {
        DataTable table2;
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.Clone|INFO> %d#, cloneDS=%d\n", this.ObjectID, (cloneDS != null) ? cloneDS.ObjectID : 0);
        try
        {
            DataTable clone = this.CreateInstance();
            if (clone.Columns.Count > 0)
            {
                clone.Reset();
            }
            table2 = this.CloneTo(clone, cloneDS, false);
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
        return table2;
    }

    private DataTable CloneHierarchy(DataTable sourceTable, System.Data.DataSet ds, Hashtable visitedMap)
    {
        if (visitedMap == null)
        {
            visitedMap = new Hashtable();
        }
        if (visitedMap.Contains(sourceTable))
        {
            return (DataTable) visitedMap[sourceTable];
        }
        DataTable targetTable = ds.Tables[sourceTable.TableName, sourceTable.Namespace];
        if ((targetTable != null) && (targetTable.Columns.Count > 0))
        {
            targetTable = this.IncrementalCloneTo(sourceTable, targetTable);
        }
        else
        {
            if (targetTable == null)
            {
                targetTable = new DataTable();
                ds.Tables.Add(targetTable);
            }
            targetTable = sourceTable.CloneTo(targetTable, ds, true);
        }
        visitedMap[sourceTable] = targetTable;
        for(DataRelation relation : sourceTable.ChildRelations)
        {
            this.CloneHierarchy(relation.ChildTable, ds, visitedMap);
        }
        return targetTable;
    }

    private DataTable CloneTo(DataTable clone, System.Data.DataSet cloneDS, boolean skipExpressionColumns)
    {
        clone.tableName = this.tableName;
        clone.tableNamespace = this.tableNamespace;
        clone.tablePrefix = this.tablePrefix;
        clone.fNestedInDataset = this.fNestedInDataset;
        clone._culture = this._culture;
        clone._cultureUserSet = this._cultureUserSet;
        clone._compareInfo = this._compareInfo;
        clone._compareFlags = this._compareFlags;
        clone._formatProvider = this._formatProvider;
        clone._hashCodeProvider = this._hashCodeProvider;
        clone._caseSensitive = this._caseSensitive;
        clone._caseSensitiveUserSet = this._caseSensitiveUserSet;
        clone.displayExpression = this.displayExpression;
        clone.typeName = this.typeName;
        clone.repeatableElement = this.repeatableElement;
        clone.MinimumCapacity = this.MinimumCapacity;
        clone.RemotingFormat = this.RemotingFormat;
        DataColumnCollection columns = this.Columns;
        for (int i = 0; i < columns.Count; i++)
        {
            clone.Columns.Add(columns[i].Clone());
        }
        if (!skipExpressionColumns && (cloneDS == null))
        {
            for (int m = 0; m < columns.Count; m++)
            {
                clone.Columns[columns[m].ColumnName].Expression = columns[m].Expression;
            }
        }
        DataColumn[] primaryKey = this.PrimaryKey;
        if (primaryKey.Length > 0)
        {
            DataColumn[] columnArray2 = new DataColumn[primaryKey.Length];
            for (int n = 0; n < primaryKey.Length; n++)
            {
                columnArray2[n] = clone.Columns[primaryKey[n].Ordinal];
            }
            clone.PrimaryKey = columnArray2;
        }
        for (int j = 0; j < this.Constraints.Count; j++)
        {
            ForeignKeyConstraint constraint2 = this.Constraints[j] as ForeignKeyConstraint;
            UniqueConstraint constraint8 = this.Constraints[j] as UniqueConstraint;
            if (constraint2 != null)
            {
                if (constraint2.Table == constraint2.RelatedTable)
                {
                    ForeignKeyConstraint constraint9 = constraint2.Clone(clone);
                    Constraint constraint7 = clone.Constraints.FindConstraint(constraint9);
                    if (constraint7 != null)
                    {
                        constraint7.ConstraintName = this.Constraints[j].ConstraintName;
                    }
                }
            }
            else if (constraint8 != null)
            {
                UniqueConstraint constraint4 = constraint8.Clone(clone);
                Constraint constraint3 = clone.Constraints.FindConstraint(constraint4);
                if (constraint3 != null)
                {
                    constraint3.ConstraintName = this.Constraints[j].ConstraintName;
                    for( Object obj3 : constraint4.ExtendedProperties.Keys)
                    {
                        constraint3.ExtendedProperties[obj3] = constraint4.ExtendedProperties[obj3];
                    }
                }
            }
        }
        for (int k = 0; k < this.Constraints.Count; k++)
        {
            if (!clone.Constraints.Contains(this.Constraints[k].ConstraintName, true))
            {
                ForeignKeyConstraint constraint = this.Constraints[k] as ForeignKeyConstraint;
                UniqueConstraint constraint6 = this.Constraints[k] as UniqueConstraint;
                if (constraint != null)
                {
                    if (constraint.Table == constraint.RelatedTable)
                    {
                        ForeignKeyConstraint constraint5 = constraint.Clone(clone);
                        if (constraint5 != null)
                        {
                            clone.Constraints.Add(constraint5);
                        }
                    }
                }
                else if (constraint6 != null)
                {
                    clone.Constraints.Add(constraint6.Clone(clone));
                }
            }
        }
        if (this.extendedProperties != null)
        {
            for( Object obj2 : this.extendedProperties.Keys)
            {
                clone.ExtendedProperties[obj2] = this.extendedProperties[obj2];
            }
        }
        return clone;
    }

    protected void CommitRow(DataRow row)
    {
        DataRowChangeEventArgs args = this.OnRowChanging(null, row, DataRowAction.Commit);
        if (!this.inDataLoad)
        {
            this.CascadeAll(row, DataRowAction.Commit);
        }
        this.SetOldRecord(row, row.newRecord);
        this.OnRowChanged(args, row, DataRowAction.Commit);
    }

    protected int Compare(String s1, String s2)
    {
        return this.Compare(s1, s2, null);
    }

    protected int Compare(String s1, String s2, System.Globalization.CompareInfo comparer)
    {
         Object obj3 = s1;
         Object obj2 = s2;
        if (obj3 == obj2)
        {
            return 0;
        }
        if (obj3 == null)
        {
            return -1;
        }
        if (obj2 == null)
        {
            return 1;
        }
        int length = s1.Length;
        int num = s2.Length;
        while (length > 0)
        {
            if ((s1[length - 1] != ' ') && (s1[length - 1] != ' '))
            {
                break;
            }
            length--;
        }
        while (num > 0)
        {
            if ((s2[num - 1] != ' ') && (s2[num - 1] != ' '))
            {
                break;
            }
            num--;
        }

        return ((comparer!=null)?comparer:this.CompareInfo).Compare(s1, 0, length, s2, 0, num, this._compareFlags);
    }

    public  Object Compute(String expression, String filter)
    {
        DataRow[] rows = this.Select(filter, "", DataViewRowState.CurrentRows);
        DataExpression expression2 = new DataExpression(this, expression);
        return expression2.Evaluate(rows);
    }

    private void ConvertToRowError(int rowIndex, Hashtable rowErrors, Hashtable colErrors)
    {
        DataRow row = this.Rows[rowIndex];
        if (rowErrors.ContainsKey(rowIndex))
        {
            row.RowError = (String) rowErrors[rowIndex];
        }
        if (colErrors.ContainsKey(rowIndex))
        {
            ArrayList list = (ArrayList) colErrors[rowIndex];
            int[] numArray = (int[]) list[0];
            String[] strArray = (String[]) list[1];
            for (int i = 0; i < numArray.Length; i++)
            {
                row.SetColumnError(numArray[i], strArray[i]);
            }
        }
    }

    private DataRowState ConvertToRowState(BitArray bitStates, int bitIndex)
    {
        boolean flag2 = bitStates[bitIndex];
        boolean flag = bitStates[bitIndex + 1];
        if (!flag2 && !flag)
        {
            return DataRowState.Unchanged;
        }
        if (!flag2 && flag)
        {
            return DataRowState.Added;
        }
        if (flag2 && !flag)
        {
            return DataRowState.Modified;
        }
        if (!flag2 || !flag)
        {
            throw ExceptionBuilder.InvalidRowBitPattern();
        }
        return DataRowState.Deleted;
    }

    public DataTable Copy()
    {
        DataTable table2;
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.Copy|API> %d#\n", this.ObjectID);
        try
        {
            DataTable table = this.Clone();
            for(DataRow row : this.Rows)
            {
                this.CopyRow(table, row);
            }
            table2 = table;
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
        return table2;
    }

    protected void CopyRow(DataTable table, DataRow row)
    {
        int oldRecord = -1;
        int newRecord = -1;
        if (row != null)
        {
            if (row.oldRecord != -1)
            {
                oldRecord = table.recordManager.ImportRecord(row.Table, row.oldRecord);
            }
            if (row.newRecord != -1)
            {
                if (row.newRecord != row.oldRecord)
                {
                    newRecord = table.recordManager.ImportRecord(row.Table, row.newRecord);
                }
                else
                {
                    newRecord = oldRecord;
                }
            }
            DataRow row2 = table.AddRecords(oldRecord, newRecord);
            if (row.HasErrors)
            {
                row2.RowError = row.RowError;
                DataColumn[] columnsInError = row.GetColumnsInError();
                for (int i = 0; i < columnsInError.Length; i++)
                {
                    DataColumn column = row2.Table.Columns[columnsInError[i].ColumnName];
                    row2.SetColumnError(column, row.GetColumnError(columnsInError[i]));
                }
            }
        }
    }

    public DataTableReader CreateDataReader()
    {
        return new DataTableReader(this);
    }

    protected DataRow CreateEmptyRow()
    {
        DataRow row = this.NewUninitializedRow();
        for(DataColumn column : this.Columns)
        {
            if (!XmlToDatasetMap.IsMappedColumn(column))
            {
                if (!column.AutoIncrement)
                {
                    if (column.AllowDBNull)
                    {
                        row[column] = DBNull.Value;
                    }
                    else if (column.DefaultValue != null)
                    {
                        row[column] = column.DefaultValue;
                    }
                }
                else
                {
                    column.Init(row.tempRecord);
                }
            }
        }
        return row;
    }

    [MethodImpl(MethodImplOptions.NoInlining)]
    protected DataTable CreateInstance()
    {
        return (DataTable) Activator.CreateInstance(base.GetType(), true);
    }

    private void CreateRelationList(List<DataTable> tableList, List<DataRelation> relationList)
    {
        for(DataTable table : tableList)
        {
            for(DataRelation relation : table.ChildRelations)
            {
                if (tableList.Contains(relation.ChildTable) && tableList.Contains(relation.ParentTable))
                {
                    relationList.Add(relation);
                }
            }
        }
    }

    private void CreateTableList(DataTable currentTable, List<DataTable> tableList)
    {
        for(DataRelation relation : currentTable.ChildRelations)
        {
            if (!tableList.Contains(relation.ChildTable))
            {
                tableList.Add(relation.ChildTable);
                this.CreateTableList(relation.ChildTable, tableList);
            }
        }
    }

    protected void DeleteRow(DataRow row)
    {
        if (row.newRecord == -1)
        {
            throw ExceptionBuilder.RowAlreadyDeleted();
        }
        this.SetNewRecord(row, -1, DataRowAction.Delete, false, true, false);
    }

    protected void DeserializeConstraints(SerializationInfo info, StreamingContext context, int serIndex, boolean allConstraints)
    {
        ArrayList list2 = (ArrayList) info.GetValue(String.Format(CultureInfo.InvariantCulture, "DataTable_{0}.Constraints", new  Object[] { serIndex }), typeof(ArrayList));
        for(ArrayList list : list2)
        {
            String str3 = (String) list[0];
            if (str3.Equals("U"))
            {
                String name = (String) list[1];
                int[] numArray4 = (int[]) list[2];
                boolean isPrimaryKey = (boolean) list[3];
                PropertyCollection propertys2 = (PropertyCollection) list[4];
                DataColumn[] columns = new DataColumn[numArray4.Length];
                for (int i = 0; i < numArray4.Length; i++)
                {
                    columns[i] = this.Columns[numArray4[i]];
                }
                UniqueConstraint constraint2 = new UniqueConstraint(name, columns, isPrimaryKey) {
                    extendedProperties = propertys2
                };
                this.Constraints.Add(constraint2);
            }
            else
            {
                String constraintName = (String) list[1];
                int[] numArray3 = (int[]) list[2];
                int[] numArray2 = (int[]) list[3];
                int[] numArray = (int[]) list[4];
                PropertyCollection propertys = (PropertyCollection) list[5];
                DataTable table2 = !allConstraints ? this : this.DataSet.Tables[numArray3[0]];
                DataColumn[] parentColumns = new DataColumn[numArray3.Length - 1];
                for (int j = 0; j < parentColumns.Length; j++)
                {
                    parentColumns[j] = table2.Columns[numArray3[j + 1]];
                }
                DataTable table = !allConstraints ? this : this.DataSet.Tables[numArray2[0]];
                DataColumn[] childColumns = new DataColumn[numArray2.Length - 1];
                for (int k = 0; k < childColumns.Length; k++)
                {
                    childColumns[k] = table.Columns[numArray2[k + 1]];
                }
                ForeignKeyConstraint constraint = new ForeignKeyConstraint(constraintName, parentColumns, childColumns) {
                    AcceptRejectRule = (AcceptRejectRule) numArray[0],
                    UpdateRule = (Rule) numArray[1],
                    DeleteRule = (Rule) numArray[2],
                    extendedProperties = propertys
                };
                this.Constraints.Add(constraint, false);
            }
        }
    }

    protected void DeserializeDataTable(SerializationInfo info, StreamingContext context, boolean isSingleTable, SerializationFormat remotingFormat)
    {
        if (remotingFormat != SerializationFormat.Xml)
        {
            this.DeserializeTableSchema(info, context, isSingleTable);
            if (isSingleTable)
            {
                this.DeserializeTableData(info, context, 0);
                this.ResetIndexes();
            }
        }
        else
        {
            String s = (String) info.GetValue("XmlSchema", typeof(String));
            String str = (String) info.GetValue("XmlDiffGram", typeof(String));
            if (s != null)
            {
                System.Data.DataSet set = new System.Data.DataSet();
                set.ReadXmlSchema(new XmlTextReader(new StringReader(s)));
                DataTable table = set.Tables[0];
                table.CloneTo(this, null, false);
                this.Namespace = table.Namespace;
                if (str != null)
                {
                    set.Tables.Remove(set.Tables[0]);
                    set.Tables.Add(this);
                    set.ReadXml(new XmlTextReader(new StringReader(str)), XmlReadMode.DiffGram);
                    set.Tables.Remove(this);
                }
            }
        }
    }

    protected void DeserializeExpressionColumns(SerializationInfo info, StreamingContext context, int serIndex)
    {
        int count = this.Columns.Count;
        for (int i = 0; i < count; i++)
        {
            String str = info.GetString(String.Format(CultureInfo.InvariantCulture, "DataTable_{0}.DataColumn_{1}.Expression", new  Object[] { serIndex, i }));
            if (str.Length != 0)
            {
                this.Columns[i].Expression = str;
            }
        }
    }

    protected void DeserializeTableData(SerializationInfo info, StreamingContext context, int serIndex)
    {
        boolean enforceConstraints = this.enforceConstraints;
        boolean inDataLoad = this.inDataLoad;
        try
        {
            this.enforceConstraints = false;
            this.inDataLoad = true;
            IFormatProvider invariantCulture = CultureInfo.InvariantCulture;
            int num6 = info.GetInt32(String.Format(invariantCulture, "DataTable_{0}.Rows.Count", new  Object[] { serIndex }));
            int num5 = info.GetInt32(String.Format(invariantCulture, "DataTable_{0}.Records.Count", new  Object[] { serIndex }));
            BitArray bitStates = (BitArray) info.GetValue(String.Format(invariantCulture, "DataTable_{0}.RowStates", new  Object[] { serIndex }), typeof(BitArray));
            ArrayList list2 = (ArrayList) info.GetValue(String.Format(invariantCulture, "DataTable_{0}.Records", new  Object[] { serIndex }), typeof(ArrayList));
            ArrayList list = (ArrayList) info.GetValue(String.Format(invariantCulture, "DataTable_{0}.NullBits", new  Object[] { serIndex }), typeof(ArrayList));
            Hashtable rowErrors = (Hashtable) info.GetValue(String.Format(invariantCulture, "DataTable_{0}.RowErrors", new  Object[] { serIndex }), typeof(Hashtable));
            rowErrors.OnDeserialization(this);
            Hashtable colErrors = (Hashtable) info.GetValue(String.Format(invariantCulture, "DataTable_{0}.ColumnErrors", new  Object[] { serIndex }), typeof(Hashtable));
            colErrors.OnDeserialization(this);
            if (num5 > 0)
            {
                for (int i = 0; i < this.Columns.Count; i++)
                {
                    this.Columns[i].SetStorage(list2[i], (BitArray) list[i]);
                }
                int index = 0;
                DataRow[] newRows = new DataRow[num5];
                for (int j = 0; j < num6; j++)
                {
                    DataRow row = this.NewEmptyRow();
                    newRows[index] = row;
                    int bitIndex = j * 3;
                    switch (this.ConvertToRowState(bitStates, bitIndex))
                    {
                        case DataRowState.Unchanged:
                            row.oldRecord = index;
                            row.newRecord = index;
                            index++;
                            break;

                        case DataRowState.Added:
                            row.oldRecord = -1;
                            row.newRecord = index;
                            index++;
                            break;

                        case DataRowState.Deleted:
                            row.oldRecord = index;
                            row.newRecord = -1;
                            index++;
                            break;

                        case DataRowState.Modified:
                            row.oldRecord = index;
                            row.newRecord = index + 1;
                            newRows[index + 1] = row;
                            index += 2;
                            break;
                    }
                    if (bitStates[bitIndex + 2])
                    {
                        row.tempRecord = index;
                        newRows[index] = row;
                        index++;
                    }
                    else
                    {
                        row.tempRecord = -1;
                    }
                    this.Rows.ArrayAdd(row);
                    row.rowID = this.nextRowID;
                    this.nextRowID += 1L;
                    this.ConvertToRowError(j, rowErrors, colErrors);
                }
                this.recordManager.SetRowCache(newRows);
                this.ResetIndexes();
            }
        }
        finally
        {
            this.enforceConstraints = enforceConstraints;
            this.inDataLoad = inDataLoad;
        }
    }

    protected void DeserializeTableSchema(SerializationInfo info, StreamingContext context, boolean isSingleTable)
    {
        this.tableName = info.GetString("DataTable.TableName");
        this.tableNamespace = info.GetString("DataTable.Namespace");
        this.tablePrefix = info.GetString("DataTable.Prefix");
        boolean boolean = info.GetBoolean("DataTable.CaseSensitive");
        this.SetCaseSensitiveValue(boolean, true, false);
        this._caseSensitiveUserSet = !info.GetBoolean("DataTable.caseSensitiveAmbient");
        int culture = (int) info.GetValue("DataTable.LocaleLCID", typeof(int));
        CultureInfo info2 = new CultureInfo(culture);
        this.SetLocaleValue(info2, true, false);
        this._cultureUserSet = true;
        this.MinimumCapacity = info.GetInt32("DataTable.MinimumCapacity");
        this.fNestedInDataset = info.GetBoolean("DataTable.NestedInDataSet");
        String name = info.GetString("DataTable.TypeName");
        this.typeName = new XmlQualifiedName(name);
        this.repeatableElement = info.GetBoolean("DataTable.RepeatableElement");
        this.extendedProperties = (PropertyCollection) info.GetValue("DataTable.ExtendedProperties", typeof(PropertyCollection));
        int num3 = info.GetInt32("DataTable.Columns.Count");
        String[] strArray = new String[num3];
        IFormatProvider invariantCulture = CultureInfo.InvariantCulture;
        for (int i = 0; i < num3; i++)
        {
            DataColumn column = new DataColumn {
            ColumnName = info.GetString(String.Format(invariantCulture, "DataTable.DataColumn_{0}.ColumnName", new  Object[] { i })),
                    _columnUri = info.GetString(String.Format(invariantCulture, "DataTable.DataColumn_{0}.Namespace", new  Object[] { i })),
                    Prefix = info.GetString(String.Format(invariantCulture, "DataTable.DataColumn_{0}.Prefix", new  Object[] { i })),
                    DataType = (Type) info.GetValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.DataType", new  Object[] { i }), typeof(Type)),
                    XmlDataType = (String) info.GetValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.XmlDataType", new  Object[] { i }), typeof(String)),
                    SimpleType = (SimpleType) info.GetValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.SimpleType", new  Object[] { i }), typeof(SimpleType)),
                    ColumnMapping = (MappingType) info.GetValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.ColumnMapping", new  Object[] { i }), typeof(MappingType)),
                    DateTimeMode = (DataSetDateTime) info.GetValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.DateTimeMode", new  Object[] { i }), typeof(DataSetDateTime)),
                    AllowDBNull = info.GetBoolean(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AllowDBNull", new  Object[] { i })),
                    AutoIncrement = info.GetBoolean(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AutoIncrement", new  Object[] { i })),
                    AutoIncrementStep = info.GetInt64(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AutoIncrementStep", new  Object[] { i })),
                    AutoIncrementSeed = info.GetInt64(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AutoIncrementSeed", new  Object[] { i })),
                    Caption = info.GetString(String.Format(invariantCulture, "DataTable.DataColumn_{0}.Caption", new  Object[] { i })),
                    DefaultValue = info.GetValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.DefaultValue", new  Object[] { i }), typeof( Object)),
                    ReadOnly = info.GetBoolean(String.Format(invariantCulture, "DataTable.DataColumn_{0}.ReadOnly", new  Object[] { i })),
                    MaxLength = info.GetInt32(String.Format(invariantCulture, "DataTable.DataColumn_{0}.MaxLength", new  Object[] { i })),
                    AutoIncrementCurrent = info.GetValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AutoIncrementCurrent", new  Object[] { i }), typeof( Object))
        };
            if (isSingleTable)
            {
                strArray[i] = info.GetString(String.Format(invariantCulture, "DataTable.DataColumn_{0}.Expression", new  Object[] { i }));
            }
            column.extendedProperties = (PropertyCollection) info.GetValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.ExtendedProperties", new  Object[] { i }), typeof(PropertyCollection));
            this.Columns.Add(column);
        }
        if (isSingleTable)
        {
            for (int j = 0; j < num3; j++)
            {
                if (strArray[j] != null)
                {
                    this.Columns[j].Expression = strArray[j];
                }
            }
        }
        if (isSingleTable)
        {
            this.DeserializeConstraints(info, context, 0, false);
        }
    }

    protected void DoRaiseNamespaceChange()
    {
        this.RaisePropertyChanging("Namespace");
        for(DataColumn column : this.Columns)
        {
            if (column._columnUri == null)
            {
                column.RaisePropertyChanging("Namespace");
            }
        }
        for(DataRelation relation : this.ChildRelations)
        {
            if (relation.Nested && (relation.ChildTable != this))
            {
                DataTable childTable = relation.ChildTable;
                relation.ChildTable.DoRaiseNamespaceChange();
            }
        }
    }

    protected void EnableConstraints()
    {
        boolean flag = false;
        for(Constraint constraint : this.Constraints)
        {
            if (constraint is UniqueConstraint)
            {
                flag |= constraint.IsConstraintViolated();
            }
        }
        for(DataColumn column : this.Columns)
        {
            if (!column.AllowDBNull)
            {
                flag |= column.IsNotAllowDBNullViolated();
            }
            if (column.MaxLength >= 0)
            {
                flag |= column.IsMaxLengthViolated();
            }
        }
        if (flag)
        {
            this.EnforceConstraints = false;
            throw ExceptionBuilder.EnforceConstraint();
        }
    }

    public void EndInit()
    {
        if ((this.dataSet == null) || !this.dataSet.fInitInProgress)
        {
            this.Columns.FinishInitCollection();
            this.Constraints.FinishInitConstraints();
            for(DataColumn column : this.Columns)
            {
                if (column.Computed)
                {
                    column.Expression = column.Expression;
                }
            }
        }
        this.fInitInProgress = false;
        if (this.delayedSetPrimaryKey != null)
        {
            this.PrimaryKey = this.delayedSetPrimaryKey;
            this.delayedSetPrimaryKey = null;
        }
        if (this.delayedViews.Count > 0)
        {
            for(DataView view : this.delayedViews)
            {
                view.EndInit();
            }
            this.delayedViews.Clear();
        }
        this.OnInitialized();
    }

    public void EndLoadData()
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.EndLoadData|API> %d#\n", this.ObjectID);
        try
        {
            if (this.inDataLoad)
            {
                if (this.loadIndex != null)
                {
                    this.loadIndex.RemoveRef();
                }
                if (this.loadIndexwithOriginalAdded != null)
                {
                    this.loadIndexwithOriginalAdded.RemoveRef();
                }
                if (this.loadIndexwithCurrentDeleted != null)
                {
                    this.loadIndexwithCurrentDeleted.RemoveRef();
                }
                this.loadIndex = null;
                this.loadIndexwithOriginalAdded = null;
                this.loadIndexwithCurrentDeleted = null;
                this.inDataLoad = false;
                this.RestoreIndexEvents(false);
                if (this.DataSet != null)
                {
                    this.DataSet.EnforceConstraints = this.savedEnforceConstraints;
                }
                else
                {
                    this.EnforceConstraints = true;
                }
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    protected void EvaluateDependentExpressions(DataColumn column)
    {
        if (column.dependentColumns != null)
        {
            for(DataColumn column2 : column.dependentColumns)
            {
                if ((column2.table != null) && ! Object.ReferenceEquals(column, column2))
                {
                    this.EvaluateExpressions(column2);
                }
            }
        }
    }

    protected void EvaluateDependentExpressions(List<DataColumn> columns, DataRow row, DataRowVersion version, List<DataRow> cachedRows)
    {
        if (columns != null)
        {
            int count = columns.Count;
            for (int i = 0; i < count; i++)
            {
                if (columns[i].Table == this)
                {
                    DataColumn dc = columns[i];
                    if ((dc.DataExpression != null) && dc.DataExpression.HasLocalAggregate())
                    {
                        DataRowVersion version3 = (version == DataRowVersion.Proposed) ? DataRowVersion.Default : version;
                        boolean flag = dc.DataExpression.IsTableAggregate();
                         Object newValue = null;
                        if (flag)
                        {
                            newValue = dc.DataExpression.Evaluate(row, version3);
                        }
                        for (int k = 0; k < this.Rows.Count; k++)
                        {
                            DataRow row5 = this.Rows[k];
                            if ((row5.RowState != DataRowState.Deleted) && ((version3 != DataRowVersion.Original) || ((row5.oldRecord != -1) && (row5.oldRecord != row5.newRecord))))
                            {
                                if (!flag)
                                {
                                    newValue = dc.DataExpression.Evaluate(row5, version3);
                                }
                                this.SilentlySetValue(row5, dc, version3, newValue);
                            }
                        }
                    }
                    else if ((row.RowState != DataRowState.Deleted) && ((version != DataRowVersion.Original) || ((row.oldRecord != -1) && (row.oldRecord != row.newRecord))))
                    {
                        this.SilentlySetValue(row, dc, version, (dc.DataExpression == null) ? dc.DefaultValue : dc.DataExpression.Evaluate(row, version));
                    }
                }
            }
            count = columns.Count;
            for (int j = 0; j < count; j++)
            {
                DataColumn column = columns[j];
                if ((column.Table != this) || ((column.DataExpression != null) && !column.DataExpression.HasLocalAggregate()))
                {
                    DataRowVersion version2 = (version == DataRowVersion.Proposed) ? DataRowVersion.Default : version;
                    if (cachedRows != null)
                    {
                        for(DataRow row4 : cachedRows)
                        {
                            if (((row4.Table == column.Table) && ((version2 != DataRowVersion.Original) || (row4.newRecord != row4.oldRecord))) && (((row4 != null) && (row4.RowState != DataRowState.Deleted)) && ((version != DataRowVersion.Original) || (row4.oldRecord != -1))))
                            {
                                 Object obj5 = column.DataExpression.Evaluate(row4, version2);
                                this.SilentlySetValue(row4, column, version2, obj5);
                            }
                        }
                    }
                    for (int m = 0; m < this.ParentRelations.Count; m++)
                    {
                        DataRelation relation2 = this.ParentRelations[m];
                        if (relation2.ParentTable == column.Table)
                        {
                            for(DataRow row3 : row.GetParentRows(relation2, version))
                            {
                                if ((((cachedRows == null) || !cachedRows.Contains(row3)) && ((version2 != DataRowVersion.Original) || (row3.newRecord != row3.oldRecord))) && (((row3 != null) && (row3.RowState != DataRowState.Deleted)) && ((version != DataRowVersion.Original) || (row3.oldRecord != -1))))
                                {
                                     Object obj4 = column.DataExpression.Evaluate(row3, version2);
                                    this.SilentlySetValue(row3, column, version2, obj4);
                                }
                            }
                        }
                    }
                    for (int n = 0; n < this.ChildRelations.Count; n++)
                    {
                        DataRelation relation = this.ChildRelations[n];
                        if (relation.ChildTable == column.Table)
                        {
                            for(DataRow row2 : row.GetChildRows(relation, version))
                            {
                                if ((((cachedRows == null) || !cachedRows.Contains(row2)) && ((version2 != DataRowVersion.Original) || (row2.newRecord != row2.oldRecord))) && (((row2 != null) && (row2.RowState != DataRowState.Deleted)) && ((version != DataRowVersion.Original) || (row2.oldRecord != -1))))
                                {
                                     Object obj3 = column.DataExpression.Evaluate(row2, version2);
                                    this.SilentlySetValue(row2, column, version2, obj3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void EvaluateExpressions()
    {
        if ((this.dependentColumns != null) && (0 < this.dependentColumns.Count))
        {
            for(DataRow row : this.Rows)
            {
                if ((row.oldRecord != -1) && (row.oldRecord != row.newRecord))
                {
                    this.EvaluateDependentExpressions(this.dependentColumns, row, DataRowVersion.Original, null);
                }
                if (row.newRecord != -1)
                {
                    this.EvaluateDependentExpressions(this.dependentColumns, row, DataRowVersion.Current, null);
                }
                if (row.tempRecord != -1)
                {
                    this.EvaluateDependentExpressions(this.dependentColumns, row, DataRowVersion.Proposed, null);
                }
            }
        }
    }

    protected void EvaluateExpressions(DataColumn column)
    {
        int count = column.table.Rows.Count;
        if (column.DataExpression.IsTableAggregate() && (count > 0))
        {
             Object obj2 = column.DataExpression.Evaluate();
            for (int i = 0; i < count; i++)
            {
                DataRow row2 = column.table.Rows[i];
                if ((row2.oldRecord != -1) && (row2.oldRecord != row2.newRecord))
                {
                    column[row2.oldRecord] = obj2;
                }
                if (row2.newRecord != -1)
                {
                    column[row2.newRecord] = obj2;
                }
                if (row2.tempRecord != -1)
                {
                    column[row2.tempRecord] = obj2;
                }
            }
        }
        else
        {
            for (int j = 0; j < count; j++)
            {
                DataRow row = column.table.Rows[j];
                if ((row.oldRecord != -1) && (row.oldRecord != row.newRecord))
                {
                    column[row.oldRecord] = column.DataExpression.Evaluate(row, DataRowVersion.Original);
                }
                if (row.newRecord != -1)
                {
                    column[row.newRecord] = column.DataExpression.Evaluate(row, DataRowVersion.Current);
                }
                if (row.tempRecord != -1)
                {
                    column[row.tempRecord] = column.DataExpression.Evaluate(row, DataRowVersion.Proposed);
                }
            }
        }
        column.Table.ResetInternalIndexes(column);
        this.EvaluateDependentExpressions(column);
    }

    protected void EvaluateExpressions(DataRow row, DataRowAction action, List<DataRow> cachedRows)
    {
        if (((action == DataRowAction.Add) || (action == DataRowAction.Change)) || ((action == DataRowAction.Rollback) && ((row.oldRecord != -1) || (row.newRecord != -1))))
        {
            if ((row.oldRecord != -1) && (row.oldRecord != row.newRecord))
            {
                this.EvaluateDependentExpressions(this.dependentColumns, row, DataRowVersion.Original, cachedRows);
            }
            if (row.newRecord != -1)
            {
                this.EvaluateDependentExpressions(this.dependentColumns, row, DataRowVersion.Current, cachedRows);
            }
            if (row.tempRecord != -1)
            {
                this.EvaluateDependentExpressions(this.dependentColumns, row, DataRowVersion.Proposed, cachedRows);
            }
        }
        else if (((action == DataRowAction.Delete) || (((action == DataRowAction.Rollback) && (row.oldRecord == -1)) && (row.newRecord == -1))) && (this.dependentColumns != null))
        {
            for(DataColumn column : this.dependentColumns)
            {
                if (((column.DataExpression != null) && column.DataExpression.HasLocalAggregate()) && (column.Table == this))
                {
                    for (int i = 0; i < this.Rows.Count; i++)
                    {
                        DataRow row3 = this.Rows[i];
                        if ((row3.oldRecord != -1) && (row3.oldRecord != row3.newRecord))
                        {
                            this.EvaluateDependentExpressions(this.dependentColumns, row3, DataRowVersion.Original, null);
                        }
                    }
                    for (int j = 0; j < this.Rows.Count; j++)
                    {
                        DataRow row5 = this.Rows[j];
                        if (row5.tempRecord != -1)
                        {
                            this.EvaluateDependentExpressions(this.dependentColumns, row5, DataRowVersion.Proposed, null);
                        }
                    }
                    for (int k = 0; k < this.Rows.Count; k++)
                    {
                        DataRow row4 = this.Rows[k];
                        if (row4.newRecord != -1)
                        {
                            this.EvaluateDependentExpressions(this.dependentColumns, row4, DataRowVersion.Current, null);
                        }
                    }
                    break;
                }
            }
            if (cachedRows != null)
            {
                for(DataRow row2 : cachedRows)
                {
                    if ((row2.oldRecord != -1) && (row2.oldRecord != row2.newRecord))
                    {
                        row2.Table.EvaluateDependentExpressions(row2.Table.dependentColumns, row2, DataRowVersion.Original, null);
                    }
                    if (row2.newRecord != -1)
                    {
                        row2.Table.EvaluateDependentExpressions(row2.Table.dependentColumns, row2, DataRowVersion.Current, null);
                    }
                    if (row2.tempRecord != -1)
                    {
                        row2.Table.EvaluateDependentExpressions(row2.Table.dependentColumns, row2, DataRowVersion.Proposed, null);
                    }
                }
            }
        }
    }

    protected DataRow FindByIndex(Index ndx,  Object[] key)
    {
        System.Data.Range range = ndx.FindRecords(key);
        if (range.IsNull)
        {
            return null;
        }
        return this.recordManager[ndx.GetRecord(range.Min)];
    }

    protected DataRow FindByPrimaryKey( Object[] values)
    {
        this.CheckPrimaryKey();
        return this.FindRow(this.primaryKey.Key, values);
    }

    protected DataRow FindByPrimaryKey( Object value)
    {
        this.CheckPrimaryKey();
        return this.FindRow(this.primaryKey.Key, value);
    }

    protected DataRow FindMergeTarget(DataRow row, DataKey key, Index ndx)
    {
        DataRow row2 = null;
        if (key.HasValue)
        {
            int record = (row.oldRecord == -1) ? row.newRecord : row.oldRecord;
             Object[] keyValues = key.GetKeyValues(record);
            row2 = this.FindByIndex(ndx, keyValues);
        }
        return row2;
    }

    private DataRelation[] FindNestedParentRelations()
    {
        List<DataRelation> list = null;
        for(DataRelation relation : this.ParentRelations)
        {
            if (relation.Nested)
            {
                if (list == null)
                {
                    list = new List<DataRelation>();
                }
                list.Add(relation);
            }
        }
        if ((list != null) && (list.Count != 0))
        {
            return list.ToArray();
        }
        return EmptyArrayDataRelation;
    }

    private DataRow FindRow(DataKey key,  Object[] values)
    {
        Index index = this.GetIndex(this.NewIndexDesc(key));
        System.Data.Range range = index.FindRecords(values);
        if (range.IsNull)
        {
            return null;
        }
        return this.recordManager[index.GetRecord(range.Min)];
    }

    private DataRow FindRow(DataKey key,  Object value)
    {
        Index index = this.GetIndex(this.NewIndexDesc(key));
        System.Data.Range range = index.FindRecords(value);
        if (range.IsNull)
        {
            return null;
        }
        return this.recordManager[index.GetRecord(range.Min)];
    }

    protected String FormatSortString(IndexField[] indexDesc)
    {
        StringBuilder builder = new StringBuilder();
        for(IndexField field : indexDesc)
        {
            if (0 < builder.Length)
            {
                builder.Append(", ");
            }
            builder.Append(field.Column.ColumnName);
            if (field.IsDescending)
            {
                builder.Append(" DESC");
            }
        }
        return builder.ToString();
    }

    protected void FreeRecord(ref int record)
    {
        this.recordManager.FreeRecord(ref record);
    }

    public DataTable GetChanges()
    {
        DataTable table2;
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.GetChanges|API> %d#\n", this.ObjectID);
        try
        {
            DataTable table = this.Clone();
            DataRow row = null;
            for (int i = 0; i < this.Rows.Count; i++)
            {
                row = this.Rows[i];
                if (row.oldRecord != row.newRecord)
                {
                    table.ImportRow(row);
                }
            }
            if (table.Rows.Count == 0)
            {
                return null;
            }
            table2 = table;
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
        return table2;
    }

    public DataTable GetChanges(DataRowState rowStates)
    {
        DataTable table2;
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.GetChanges|API> %d#, rowStates=%d{ds.DataRowState}\n", this.ObjectID, (int) rowStates);
        try
        {
            DataTable table = this.Clone();
            DataRow row = null;
            for (int i = 0; i < this.Rows.Count; i++)
            {
                row = this.Rows[i];
                if ((row.RowState & rowStates) != 0)
                {
                    table.ImportRow(row);
                }
            }
            if (table.Rows.Count == 0)
            {
                return null;
            }
            table2 = table;
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
        return table2;
    }

    public static XmlSchemaComplexType GetDataTableSchema(XmlSchemaSet schemaSet)
    {
        XmlSchemaComplexType type = new XmlSchemaComplexType();
        XmlSchemaSequence sequence = new XmlSchemaSequence();
        XmlSchemaAny item = new XmlSchemaAny {
        Namespace = "http://www.w3.org/2001/XMLSchema",
                MinOccurs = 0M,
                MaxOccurs = 79228162514264337593543950335M,
                ProcessContents = XmlSchemaContentProcessing.Lax
    };
        sequence.Items.Add(item);
        item = new XmlSchemaAny {
        Namespace = "urn:schemas-microsoft-com:xml-diffgram-v1",
                MinOccurs = 1M,
                ProcessContents = XmlSchemaContentProcessing.Lax
    };
        sequence.Items.Add(item);
        type.Particle = sequence;
        return type;
    }

    public DataRow[] GetErrors()
    {
        List<DataRow> list = new List<DataRow>();
        for (int i = 0; i < this.Rows.Count; i++)
        {
            DataRow item = this.Rows[i];
            if (item.HasErrors)
            {
                list.Add(item);
            }
        }
        DataRow[] array = this.NewRowArray(list.Count);
        list.CopyTo(array);
        return array;
    }

    protected Index GetIndex(IndexField[] indexDesc)
    {
        return this.GetIndex(indexDesc, DataViewRowState.CurrentRows, null);
    }

    protected Index GetIndex(String sort, DataViewRowState recordStates, IFilter rowFilter)
    {
        return this.GetIndex(this.ParseSortString(sort), recordStates, rowFilter);
    }

    protected Index GetIndex(IndexField[] indexDesc, DataViewRowState recordStates, IFilter rowFilter)
    {
        this.indexesLock.AcquireReaderLock(-1);
        try
        {
            for (int i = 0; i < this.indexes.Count; i++)
            {
                Index index = this.indexes[i];
                if ((index != null) && index.Equal(indexDesc, recordStates, rowFilter))
                {
                    return index;
                }
            }
        }
        finally
        {
            this.indexesLock.ReleaseReaderLock();
        }
        Index index2 = new Index(this, indexDesc, recordStates, rowFilter);
        index2.AddRef();
        return index2;
    }

    private String GetInheritedNamespace(List<DataTable> visitedTables)
    {
        DataRelation[] nestedParentRelations = this.NestedParentRelations;
        if (nestedParentRelations.Length > 0)
        {
            for (int i = 0; i < nestedParentRelations.Length; i++)
            {
                DataRelation relation = nestedParentRelations[i];
                if (relation.ParentTable.tableNamespace != null)
                {
                    return relation.ParentTable.tableNamespace;
                }
            }
            int index = 0;
            while ((index < nestedParentRelations.Length) && ((nestedParentRelations[index].ParentTable == this) || visitedTables.Contains(nestedParentRelations[index].ParentTable)))
            {
                index++;
            }
            if (index < nestedParentRelations.Length)
            {
                DataTable parentTable = nestedParentRelations[index].ParentTable;
                if (!visitedTables.Contains(parentTable))
                {
                    visitedTables.Add(parentTable);
                }
                return parentTable.GetInheritedNamespace(visitedTables);
            }
        }
        if (this.DataSet != null)
        {
            return this.DataSet.Namespace;
        }
        return String.Empty;
    }

    protected List<DataViewListener> GetListeners()
    {
        return this._dataViewListeners;
    }

    [SecurityPermission(SecurityAction.LinkDemand, Flags=SecurityPermissionFlag.SerializationFormatter)]
    public void GetObjectData(SerializationInfo info, StreamingContext context)
    {
        SerializationFormat remotingFormat = this.RemotingFormat;
        boolean isSingleTable = (context.Context != null) ? Convert.ToBoolean(context.Context, CultureInfo.InvariantCulture) : true;
        this.SerializeDataTable(info, context, isSingleTable, remotingFormat);
    }

    protected PropertyDescriptorCollection GetPropertyDescriptorCollection(Attribute[] attributes)
    {
        if (this.propertyDescriptorCollectionCache == null)
        {
            int count = this.Columns.Count;
            int num4 = this.ChildRelations.Count;
            PropertyDescriptor[] properties = new PropertyDescriptor[count + num4];
            for (int i = 0; i < count; i++)
            {
                properties[i] = new DataColumnPropertyDescriptor(this.Columns[i]);
            }
            for (int j = 0; j < num4; j++)
            {
                properties[count + j] = new DataRelationPropertyDescriptor(this.ChildRelations[j]);
            }
            this.propertyDescriptorCollectionCache = new PropertyDescriptorCollection(properties);
        }
        return this.propertyDescriptorCollectionCache;
    }

    protected void GetRowAndColumnErrors(int rowIndex, Hashtable rowErrors, Hashtable colErrors)
    {
        DataRow row = this.Rows[rowIndex];
        if (row.HasErrors)
        {
            rowErrors.Add(rowIndex, row.RowError);
            DataColumn[] columnsInError = row.GetColumnsInError();
            if (columnsInError.Length > 0)
            {
                int[] numArray = new int[columnsInError.Length];
                String[] strArray = new String[columnsInError.Length];
                for (int i = 0; i < columnsInError.Length; i++)
                {
                    numArray[i] = columnsInError[i].Ordinal;
                    strArray[i] = row.GetColumnError(columnsInError[i]);
                }
                ArrayList list = new ArrayList();
                list.Add(numArray);
                list.Add(strArray);
                colErrors.Add(rowIndex, list);
            }
        }
    }

    protected Type GetRowType()
    {
        return typeof(DataRow);
    }

    protected XmlSchema GetSchema()
    {
        if (base.GetType() == typeof(DataTable))
        {
            return null;
        }
        MemoryStream w = new MemoryStream();
        XmlWriter xw = new XmlTextWriter(w, null);
        if (xw != null)
        {
            new XmlTreeGen(SchemaFormat.WebService).Save(this, xw);
        }
        w.Position = 0L;
        return XmlSchema.Read(new XmlTextReader(w), null);
    }

    protected int GetSpecialHashCode(String name)
    {
        int num = 0;
        while ((num < name.Length) && (' ' > name[num]))
        {
            num++;
        }
        if (name.Length != num)
        {
            return 0;
        }
        if (this._hashCodeProvider == null)
        {
            this._hashCodeProvider = StringComparer.Create(this.Locale, true);
        }
        return this._hashCodeProvider.GetHashCode(name);
    }

    public void ImportRow(DataRow row)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.ImportRow|API> %d#\n", this.ObjectID);
        try
        {
            int oldRecord = -1;
            int newRecord = -1;
            if (row != null)
            {
                if (row.oldRecord != -1)
                {
                    oldRecord = this.recordManager.ImportRecord(row.Table, row.oldRecord);
                }
                if (row.newRecord != -1)
                {
                    if (row.RowState != DataRowState.Unchanged)
                    {
                        newRecord = this.recordManager.ImportRecord(row.Table, row.newRecord);
                    }
                    else
                    {
                        newRecord = oldRecord;
                    }
                }
                if ((oldRecord != -1) || (newRecord != -1))
                {
                    DataRow row2 = this.AddRecords(oldRecord, newRecord);
                    if (row.HasErrors)
                    {
                        row2.RowError = row.RowError;
                        DataColumn[] columnsInError = row.GetColumnsInError();
                        for (int i = 0; i < columnsInError.Length; i++)
                        {
                            DataColumn column = row2.Table.Columns[columnsInError[i].ColumnName];
                            row2.SetColumnError(column, row.GetColumnError(columnsInError[i]));
                        }
                    }
                }
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    private DataTable IncrementalCloneTo(DataTable sourceTable, DataTable targetTable)
    {
        for(DataColumn column : sourceTable.Columns)
        {
            if (targetTable.Columns[column.ColumnName] == null)
            {
                targetTable.Columns.Add(column.Clone());
            }
        }
        return targetTable;
    }

    protected int IndexOf(String s1, String s2)
    {
        return this.CompareInfo.IndexOf(s1, s2, this._compareFlags);
    }

    protected int[] InsertRecordToIndexes(DataRow row, DataRowVersion version)
    {
        int count = this.LiveIndexes.Count;
        int[] numArray = new int[count];
        int recordFromVersion = row.GetRecordFromVersion(version);
        DataViewRowState recordState = row.GetRecordState(recordFromVersion);
        while (--count >= 0)
        {
            if (row.HasVersion(version))
            {
                if ((recordState & this.indexes[count].RecordStates) != DataViewRowState.None)
                {
                    numArray[count] = this.indexes[count].InsertRecordToIndex(recordFromVersion);
                }
                else
                {
                    numArray[count] = -1;
                }
            }
        }
        return numArray;
    }

    protected void InsertRow(DataRow row, long proposedID)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.InsertRow|INFO> %d#, row=%d\n", this.ObjectID, row.ObjectID);
        try
        {
            if (row.Table != this)
            {
                throw ExceptionBuilder.RowAlreadyInOtherCollection();
            }
            if (row.rowID != -1L)
            {
                throw ExceptionBuilder.RowAlreadyInTheCollection();
            }
            if ((row.oldRecord == -1) && (row.newRecord == -1))
            {
                throw ExceptionBuilder.RowEmpty();
            }
            if (proposedID == -1L)
            {
                proposedID = this.nextRowID;
            }
            row.rowID = proposedID;
            if (this.nextRowID <= proposedID)
            {
                this.nextRowID = proposedID + 1L;
            }
            DataRowChangeEventArgs args = null;
            if (row.newRecord != -1)
            {
                row.tempRecord = row.newRecord;
                row.newRecord = -1;
                try
                {
                    args = this.RaiseRowChanging(null, row, DataRowAction.Add, true);
                }
                catch
                {
                    row.tempRecord = -1;
                    throw;
                }
                row.newRecord = row.tempRecord;
                row.tempRecord = -1;
            }
            if (row.oldRecord != -1)
            {
                this.recordManager[row.oldRecord] = row;
            }
            if (row.newRecord != -1)
            {
                this.recordManager[row.newRecord] = row;
            }
            this.Rows.ArrayAdd(row);
            if (row.RowState == DataRowState.Unchanged)
            {
                this.RecordStateChanged(row.oldRecord, DataViewRowState.None, DataViewRowState.Unchanged);
            }
            else
            {
                this.RecordStateChanged(row.oldRecord, DataViewRowState.None, row.GetRecordState(row.oldRecord), row.newRecord, DataViewRowState.None, row.GetRecordState(row.newRecord));
            }
            if ((this.dependentColumns != null) && (this.dependentColumns.Count > 0))
            {
                this.EvaluateExpressions(row, DataRowAction.Add, null);
            }
            this.RaiseRowChanged(args, row, DataRowAction.Add);
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    protected void InsertRow(DataRow row, int proposedID, int pos)
    {
        this.InsertRow(row, (long) proposedID, pos, true);
    }

    protected void InsertRow(DataRow row, long proposedID, int pos, boolean fireEvent)
    {
        Exception deferredException = null;
        boolean flag;
        if (row == null)
        {
            throw ExceptionBuilder.ArgumentNull("row");
        }
        if (row.Table != this)
        {
            throw ExceptionBuilder.RowAlreadyInOtherCollection();
        }
        if (row.rowID != -1L)
        {
            throw ExceptionBuilder.RowAlreadyInTheCollection();
        }
        row.BeginEdit();
        int tempRecord = row.tempRecord;
        row.tempRecord = -1;
        if (proposedID == -1L)
        {
            proposedID = this.nextRowID;
        }
        if (flag = this.nextRowID <= proposedID)
        {
            this.nextRowID = proposedID + 1L;
        }
        try
        {
            try
            {
                row.rowID = proposedID;
                this.SetNewRecordWorker(row, tempRecord, DataRowAction.Add, false, false, pos, fireEvent, out deferredException);
            }
            catch
            {
                if (flag && (this.nextRowID == (proposedID + 1L)))
                {
                    this.nextRowID = proposedID;
                }
                row.rowID = -1L;
                row.tempRecord = tempRecord;
                throw;
            }
            if (deferredException != null)
            {
                throw deferredException;
            }
            if (this.EnforceConstraints && !this.inLoad)
            {
                int count = this.columnCollection.Count;
                for (int i = 0; i < count; i++)
                {
                    DataColumn column = this.columnCollection[i];
                    if (column.Computed)
                    {
                        column.CheckColumnConstraint(row, DataRowAction.Add);
                    }
                }
            }
        }
        finally
        {
            row.ResetLastChangedColumn();
        }
    }

    private boolean IsEmptyXml(XmlReader reader)
    {
        if (reader.IsEmptyElement)
        {
            if ((reader.AttributeCount == 0) || ((reader.LocalName == "diffgram") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1")))
            {
                return true;
            }
            if (reader.AttributeCount == 1)
            {
                reader.MoveToAttribute(0);
                if (((this.Namespace == reader.Value) && (this.Prefix == reader.LocalName)) && ((reader.Prefix == "xmlns") && (reader.NamespaceURI == "http://www.w3.org/2000/xmlns/")))
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean IsNamespaceInherited()
    {
        return (null == this.tableNamespace);
    }

    protected boolean IsSuffix(String s1, String s2)
    {
        return this.CompareInfo.IsSuffix(s1, s2, this._compareFlags);
    }

    public void Load(IDataReader reader)
    {
        this.Load(reader, LoadOption.PreserveChanges, null);
    }

    public void Load(IDataReader reader, LoadOption loadOption)
    {
        this.Load(reader, loadOption, null);
    }

    public void Load(IDataReader reader, LoadOption loadOption, FillErrorEventHandler errorHandler)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.Load|API> %d#, loadOption=%d{ds.LoadOption}\n", this.ObjectID, (int) loadOption);
        try
        {
            if (this.PrimaryKey.Length == 0)
            {
                DataTableReader reader2 = reader as DataTableReader;
                if ((reader2 != null) && (reader2.CurrentDataTable == this))
                {
                    return;
                }
            }
            LoadAdapter adapter = new LoadAdapter {
            FillLoadOption = loadOption,
                    MissingSchemaAction = MissingSchemaAction.AddWithKey
        };
            if (errorHandler != null)
            {
                adapter.FillError += errorHandler;
            }
            adapter.FillFromReader(new DataTable[] { this }, reader, 0, 0);
            if (!reader.IsClosed && !reader.NextResult())
            {
                reader.Close();
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    public DataRow LoadDataRow( Object[] values, boolean fAcceptChanges)
    {
        DataRow row2;
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.LoadDataRow|API> %d#, fAcceptChanges=%d{boolean}\n", this.ObjectID, fAcceptChanges);
        try
        {
            DataRow row;
            if (this.inDataLoad)
            {
                int record = this.NewRecordFromArray(values);
                if (this.loadIndex != null)
                {
                    int recordIndex = this.loadIndex.FindRecord(record);
                    if (recordIndex != -1)
                    {
                        int num3 = this.loadIndex.GetRecord(recordIndex);
                        row = this.recordManager[num3];
                        row.CancelEdit();
                        if (row.RowState == DataRowState.Deleted)
                        {
                            this.SetNewRecord(row, row.oldRecord, DataRowAction.Rollback, false, true, false);
                        }
                        this.SetNewRecord(row, record, DataRowAction.Change, false, true, false);
                        if (fAcceptChanges)
                        {
                            row.AcceptChanges();
                        }
                        return row;
                    }
                }
                row = this.NewRow(record);
                this.AddRow(row);
                if (fAcceptChanges)
                {
                    row.AcceptChanges();
                }
                return row;
            }
            row = this.UpdatingAdd(values);
            if (fAcceptChanges)
            {
                row.AcceptChanges();
            }
            row2 = row;
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
        return row2;
    }

    public DataRow LoadDataRow( Object[] values, LoadOption loadOption)
    {
        DataRow row;
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.LoadDataRow|API> %d#, loadOption=%d{ds.LoadOption}\n", this.ObjectID, (int) loadOption);
        try
        {
            Index searchIndex = null;
            if (this.primaryKey != null)
            {
                if (loadOption == LoadOption.Upsert)
                {
                    if (this.loadIndexwithCurrentDeleted == null)
                    {
                        this.loadIndexwithCurrentDeleted = this.primaryKey.Key.GetSortIndex(DataViewRowState.CurrentRows | DataViewRowState.Deleted);
                        if (this.loadIndexwithCurrentDeleted != null)
                        {
                            this.loadIndexwithCurrentDeleted.AddRef();
                        }
                    }
                    searchIndex = this.loadIndexwithCurrentDeleted;
                }
                else
                {
                    if (this.loadIndexwithOriginalAdded == null)
                    {
                        this.loadIndexwithOriginalAdded = this.primaryKey.Key.GetSortIndex(DataViewRowState.OriginalRows | DataViewRowState.Added);
                        if (this.loadIndexwithOriginalAdded != null)
                        {
                            this.loadIndexwithOriginalAdded.AddRef();
                        }
                    }
                    searchIndex = this.loadIndexwithOriginalAdded;
                }
            }
            if (this.inDataLoad && !this.AreIndexEventsSuspended)
            {
                this.SuspendIndexEvents();
            }
            row = this.LoadRow(values, loadOption, searchIndex);
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
        return row;
    }

    private DataRow LoadRow( Object[] values, LoadOption loadOption, Index searchIndex)
    {
        DataRow dataRow = null;
        int num;
        DataRowAction changeCurrentAndOriginal;
        if (searchIndex != null)
        {
            int[] numArray = new int[0];
            if (this.primaryKey != null)
            {
                numArray = new int[this.primaryKey.ColumnsReference.Length];
                for (int j = 0; j < this.primaryKey.ColumnsReference.Length; j++)
                {
                    numArray[j] = this.primaryKey.ColumnsReference[j].Ordinal;
                }
            }
             Object[] key = new  Object[numArray.Length];
            for (int i = 0; i < numArray.Length; i++)
            {
                key[i] = values[numArray[i]];
            }
            System.Data.Range range = searchIndex.FindRecords(key);
            if (!range.IsNull)
            {
                int num8 = 0;
                for (int k = range.Min; k <= range.Max; k++)
                {
                    int record = searchIndex.GetRecord(k);
                    dataRow = this.recordManager[record];
                    num = this.NewRecordFromArray(values);
                    for (int m = 0; m < values.Length; m++)
                    {
                        if (values[m] == null)
                        {
                            this.columnCollection[m].Copy(record, num);
                        }
                    }
                    for (int n = values.Length; n < this.columnCollection.Count; n++)
                    {
                        this.columnCollection[n].Copy(record, num);
                    }
                    if ((loadOption != LoadOption.Upsert) || (dataRow.RowState != DataRowState.Deleted))
                    {
                        this.SetDataRowWithLoadOption(dataRow, num, loadOption, true);
                    }
                    else
                    {
                        num8++;
                    }
                }
                if (num8 == 0)
                {
                    return dataRow;
                }
            }
        }
        num = this.NewRecordFromArray(values);
        dataRow = this.NewRow(num);
        DataRowChangeEventArgs args = null;
        switch (loadOption)
        {
            case LoadOption.OverwriteChanges:
            case LoadOption.PreserveChanges:
                changeCurrentAndOriginal = DataRowAction.ChangeCurrentAndOriginal;
                break;

            case LoadOption.Upsert:
                changeCurrentAndOriginal = DataRowAction.Add;
                break;

            default:
                throw ExceptionBuilder.ArgumentOutOfRange("LoadOption");
        }
        args = this.RaiseRowChanging(null, dataRow, changeCurrentAndOriginal);
        this.InsertRow(dataRow, -1L, -1, false);
        switch (loadOption)
        {
            case LoadOption.OverwriteChanges:
            case LoadOption.PreserveChanges:
                this.SetOldRecord(dataRow, num);
                break;

            case LoadOption.Upsert:
                break;

            default:
                throw ExceptionBuilder.ArgumentOutOfRange("LoadOption");
        }
        this.RaiseRowChanged(args, dataRow, changeCurrentAndOriginal);
        return dataRow;
    }

    public void Merge(DataTable table)
    {
        this.Merge(table, false, MissingSchemaAction.Add);
    }

    public void Merge(DataTable table, boolean preserveChanges)
    {
        this.Merge(table, preserveChanges, MissingSchemaAction.Add);
    }

    public void Merge(DataTable table, boolean preserveChanges, MissingSchemaAction missingSchemaAction)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.Merge|API> %d#, table=%d, preserveChanges=%d{boolean}, missingSchemaAction=%d{ds.MissingSchemaAction}\n", this.ObjectID, (table != null) ? table.ObjectID : 0, preserveChanges, (int) missingSchemaAction);
        try
        {
            if (table == null)
            {
                throw ExceptionBuilder.ArgumentNull("table");
            }
            switch (missingSchemaAction)
            {
                case MissingSchemaAction.Add:
                case MissingSchemaAction.Ignore:
                case MissingSchemaAction.Error:
                case MissingSchemaAction.AddWithKey:
                    new Merger(this, preserveChanges, missingSchemaAction).MergeTable(table);
                    return;
            }
            throw ADP.InvalidMissingSchemaAction(missingSchemaAction);
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    protected DataRow MergeRow(DataRow row, DataRow targetRow, boolean preserveChanges, Index idxSearch)
    {
        if (targetRow == null)
        {
            targetRow = this.NewEmptyRow();
            targetRow.oldRecord = this.recordManager.ImportRecord(row.Table, row.oldRecord);
            targetRow.newRecord = targetRow.oldRecord;
            if (row.oldRecord != row.newRecord)
            {
                targetRow.newRecord = this.recordManager.ImportRecord(row.Table, row.newRecord);
            }
            this.InsertRow(targetRow, -1L);
        }
        else
        {
            int tempRecord = targetRow.tempRecord;
            targetRow.tempRecord = -1;
            try
            {
                int oldRecord;
                int newRecord;
                int num4;
                DataRowState rowState = targetRow.RowState;
                num4 = (rowState == DataRowState.Added) ? targetRow.newRecord : (num4 = targetRow.oldRecord);
                if ((targetRow.RowState == DataRowState.Unchanged) && (row.RowState == DataRowState.Unchanged))
                {
                    oldRecord = targetRow.oldRecord;
                    newRecord = preserveChanges ? this.recordManager.CopyRecord(this, oldRecord, -1) : targetRow.newRecord;
                    oldRecord = this.recordManager.CopyRecord(row.Table, row.oldRecord, targetRow.oldRecord);
                    this.SetMergeRecords(targetRow, newRecord, oldRecord, DataRowAction.Change);
                }
                else if (row.newRecord == -1)
                {
                    oldRecord = targetRow.oldRecord;
                    if (preserveChanges)
                    {
                        newRecord = (targetRow.RowState == DataRowState.Unchanged) ? this.recordManager.CopyRecord(this, oldRecord, -1) : targetRow.newRecord;
                    }
                    else
                    {
                        newRecord = -1;
                    }
                    oldRecord = this.recordManager.CopyRecord(row.Table, row.oldRecord, oldRecord);
                    if (num4 != ((rowState == DataRowState.Added) ? newRecord : oldRecord))
                    {
                        this.SetMergeRecords(targetRow, newRecord, oldRecord, (newRecord == -1) ? DataRowAction.Delete : DataRowAction.Change);
                        idxSearch.Reset();
                        num4 = (rowState == DataRowState.Added) ? newRecord : oldRecord;
                    }
                    else
                    {
                        this.SetMergeRecords(targetRow, newRecord, oldRecord, (newRecord == -1) ? DataRowAction.Delete : DataRowAction.Change);
                    }
                }
                else
                {
                    oldRecord = targetRow.oldRecord;
                    newRecord = targetRow.newRecord;
                    if (targetRow.RowState == DataRowState.Unchanged)
                    {
                        newRecord = this.recordManager.CopyRecord(this, oldRecord, -1);
                    }
                    oldRecord = this.recordManager.CopyRecord(row.Table, row.oldRecord, oldRecord);
                    if (!preserveChanges)
                    {
                        newRecord = this.recordManager.CopyRecord(row.Table, row.newRecord, newRecord);
                    }
                    this.SetMergeRecords(targetRow, newRecord, oldRecord, DataRowAction.Change);
                }
                if ((rowState == DataRowState.Added) && (targetRow.oldRecord != -1))
                {
                    idxSearch.Reset();
                }
            }
            finally
            {
                targetRow.tempRecord = tempRecord;
            }
        }
        if (row.HasErrors)
        {
            if (targetRow.RowError.Length == 0)
            {
                targetRow.RowError = row.RowError;
            }
            else
            {
                targetRow.RowError = targetRow.RowError + " ]:[ " + row.RowError;
            }
            DataColumn[] columnsInError = row.GetColumnsInError();
            for (int i = 0; i < columnsInError.Length; i++)
            {
                DataColumn column = targetRow.Table.Columns[columnsInError[i].ColumnName];
                targetRow.SetColumnError(column, row.GetColumnError(columnsInError[i]));
            }
            return targetRow;
        }
        if (!preserveChanges)
        {
            targetRow.ClearErrors();
        }
        return targetRow;
    }

    protected boolean MoveToElement(XmlReader reader, int depth)
    {
        while ((!reader.EOF && (reader.NodeType != XmlNodeType.EndElement)) && ((reader.NodeType != XmlNodeType.Element) && (reader.Depth > depth)))
        {
            reader.Read();
        }
        return (reader.NodeType == XmlNodeType.Element);
    }

    protected DataRow NewEmptyRow()
    {
        this.rowBuilder._record = -1;
        DataRow row = this.NewRowFromBuilder(this.rowBuilder);
        if (this.dataSet != null)
        {
            this.DataSet.OnDataRowCreated(row);
        }
        return row;
    }

    private IndexField[] NewIndexDesc(DataKey key)
    {
        IndexField[] indexDesc = key.GetIndexDesc();
        IndexField[] destinationArray = new IndexField[indexDesc.Length];
        Array.Copy(indexDesc, 0, destinationArray, 0, indexDesc.Length);
        return destinationArray;
    }

    protected int NewRecord()
    {
        return this.NewRecord(-1);
    }

    protected int NewRecord(int sourceRecord)
    {
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

    protected int NewRecordFromArray( Object[] value)
    {
        int num5;
        int count = this.columnCollection.Count;
        if (count < value.Length)
        {
            throw ExceptionBuilder.ValueArrayLength();
        }
        int record = this.recordManager.NewRecordBase();
        try
        {
            for (int i = 0; i < value.Length; i++)
            {
                if (value[i] != null)
                {
                    this.columnCollection[i][record] = value[i];
                }
                else
                {
                    this.columnCollection[i].Init(record);
                }
            }
            for (int j = value.Length; j < count; j++)
            {
                this.columnCollection[j].Init(record);
            }
            num5 = record;
        }
        catch (Exception exception)
        {
            if (ADP.IsCatchableOrSecurityExceptionType(exception))
            {
                this.FreeRecord(ref record);
            }
            throw;
        }
        return num5;
    }

    public DataRow NewRow()
    {
        DataRow row = this.NewRow(-1);
        this.NewRowCreated(row);
        return row;
    }

    protected DataRow NewRow(int record)
    {
        if (-1 == record)
        {
            record = this.NewRecord(-1);
        }
        this.rowBuilder._record = record;
        DataRow row = this.NewRowFromBuilder(this.rowBuilder);
        this.recordManager[record] = row;
        if (this.dataSet != null)
        {
            this.DataSet.OnDataRowCreated(row);
        }
        return row;
    }

    [MethodImpl(MethodImplOptions.NoInlining)]
    protected DataRow[] NewRowArray(int size)
    {
        if (this.IsTypedDataTable)
        {
            if (size != 0)
            {
                return (DataRow[]) Array.CreateInstance(this.GetRowType(), size);
            }
            if (this.EmptyDataRowArray == null)
            {
                this.EmptyDataRowArray = (DataRow[]) Array.CreateInstance(this.GetRowType(), 0);
            }
            return this.EmptyDataRowArray;
        }
        if (size != 0)
        {
            return new DataRow[size];
        }
        return zeroRows;
    }

    private void NewRowCreated(DataRow row)
    {
        if (this.onTableNewRowDelegate != null)
        {
            DataTableNewRowEventArgs e = new DataTableNewRowEventArgs(row);
            this.OnTableNewRow(e);
        }
    }

    protected DataRow NewRowFromBuilder(DataRowBuilder builder)
    {
        return new DataRow(builder);
    }

    protected int NewUninitializedRecord()
    {
        return this.recordManager.NewRecordBase();
    }

    private DataRow NewUninitializedRow()
    {
        return this.NewRow(this.NewUninitializedRecord());
    }

    protected void OnColumnChanged(DataColumnChangeEventArgs e)
    {
        if (this.onColumnChangedDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnColumnChanged|INFO> %d#\n", this.ObjectID);
            this.onColumnChangedDelegate(this, e);
        }
    }

    protected void OnColumnChanging(DataColumnChangeEventArgs e)
    {
        if (this.onColumnChangingDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnColumnChanging|INFO> %d#\n", this.ObjectID);
            this.onColumnChangingDelegate(this, e);
        }
    }

    private void OnInitialized()
    {
        if (this.onInitialized != null)
        {
            Bid.Trace("<ds.DataTable.OnInitialized|INFO> %d#\n", this.ObjectID);
            this.onInitialized(this, EventArgs.Empty);
        }
    }

    protected void OnPropertyChanging(PropertyChangedEventArgs pcevent)
    {
        if (this.onPropertyChangingDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnPropertyChanging|INFO> %d#\n", this.ObjectID);
            this.onPropertyChangingDelegate(this, pcevent);
        }
    }

    protected void OnRemoveColumn(DataColumn column)
    {
    }

    protected void OnRemoveColumnInternal(DataColumn column)
    {
        this.OnRemoveColumn(column);
    }

    protected void OnRowChanged(DataRowChangeEventArgs e)
    {
        if (this.onRowChangedDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnRowChanged|INFO> %d#\n", this.ObjectID);
            this.onRowChangedDelegate(this, e);
        }
    }

    private DataRowChangeEventArgs OnRowChanged(DataRowChangeEventArgs args, DataRow eRow, DataRowAction eAction)
    {
        if ((this.onRowChangedDelegate != null) || this.IsTypedDataTable)
        {
            if (args == null)
            {
                args = new DataRowChangeEventArgs(eRow, eAction);
            }
            this.OnRowChanged(args);
        }
        return args;
    }

    protected void OnRowChanging(DataRowChangeEventArgs e)
    {
        if (this.onRowChangingDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnRowChanging|INFO> %d#\n", this.ObjectID);
            this.onRowChangingDelegate(this, e);
        }
    }

    private DataRowChangeEventArgs OnRowChanging(DataRowChangeEventArgs args, DataRow eRow, DataRowAction eAction)
    {
        if ((this.onRowChangingDelegate != null) || this.IsTypedDataTable)
        {
            if (args == null)
            {
                args = new DataRowChangeEventArgs(eRow, eAction);
            }
            this.OnRowChanging(args);
        }
        return args;
    }

    protected void OnRowDeleted(DataRowChangeEventArgs e)
    {
        if (this.onRowDeletedDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnRowDeleted|INFO> %d#\n", this.ObjectID);
            this.onRowDeletedDelegate(this, e);
        }
    }

    protected void OnRowDeleting(DataRowChangeEventArgs e)
    {
        if (this.onRowDeletingDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnRowDeleting|INFO> %d#\n", this.ObjectID);
            this.onRowDeletingDelegate(this, e);
        }
    }

    protected void OnTableCleared(DataTableClearEventArgs e)
    {
        if (this.onTableClearedDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnTableCleared|INFO> %d#\n", this.ObjectID);
            this.onTableClearedDelegate(this, e);
        }
    }

    protected void OnTableClearing(DataTableClearEventArgs e)
    {
        if (this.onTableClearingDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnTableClearing|INFO> %d#\n", this.ObjectID);
            this.onTableClearingDelegate(this, e);
        }
    }

    protected void OnTableNewRow(DataTableNewRowEventArgs e)
    {
        if (this.onTableNewRowDelegate != null)
        {
            Bid.Trace("<ds.DataTable.OnTableNewRow|INFO> %d#\n", this.ObjectID);
            this.onTableNewRowDelegate(this, e);
        }
    }

    protected IndexField[] ParseSortString(String sortString)
    {
        IndexField[] zeroIndexField = DataTable.zeroIndexField;
        if ((sortString != null) && (0 < sortString.Length))
        {
            String[] strArray = sortString.Split(new char[] { ',' });
            zeroIndexField = new IndexField[strArray.Length];
            for (int i = 0; i < strArray.Length; i++)
            {
                String strA = strArray[i].Trim();
                int length = strA.Length;
                boolean isDescending = false;
                if ((length >= 5) && (String.Compare(strA, length - 4, " ASC", 0, 4, StringComparison.OrdinalIgnoreCase) == 0))
                {
                    strA = strA.Substring(0, length - 4).Trim();
                }
                else if ((length >= 6) && (String.Compare(strA, length - 5, " DESC", 0, 5, StringComparison.OrdinalIgnoreCase) == 0))
                {
                    isDescending = true;
                    strA = strA.Substring(0, length - 5).Trim();
                }
                if (strA.StartsWith("[", StringComparison.Ordinal))
                {
                    if (!strA.EndsWith("]", StringComparison.Ordinal))
                    {
                        throw ExceptionBuilder.InvalidSortString(strArray[i]);
                    }
                    strA = strA.Substring(1, strA.Length - 2);
                }
                DataColumn column = this.Columns[strA];
                if (column == null)
                {
                    throw ExceptionBuilder.ColumnOutOfRange(strA);
                }
                zeroIndexField[i] = new IndexField(column, isDescending);
            }
        }
        return zeroIndexField;
    }

    protected void RaisePropertyChanging(String name)
    {
        this.OnPropertyChanging(new PropertyChangedEventArgs(name));
    }

    private DataRowChangeEventArgs RaiseRowChanged(DataRowChangeEventArgs args, DataRow eRow, DataRowAction eAction)
    {
        try
        {
            if (this.UpdatingCurrent(eRow, eAction) && (this.IsTypedDataTable || (this.onRowChangedDelegate != null)))
            {
                args = this.OnRowChanged(args, eRow, eAction);
                return args;
            }
            if (((DataRowAction.Delete != eAction) || (eRow.newRecord != -1)) || (!this.IsTypedDataTable && (this.onRowDeletedDelegate == null)))
            {
                return args;
            }
            if (args == null)
            {
                args = new DataRowChangeEventArgs(eRow, eAction);
            }
            this.OnRowDeleted(args);
        }
        catch (Exception exception)
        {
            if (!ADP.IsCatchableExceptionType(exception))
            {
                throw;
            }
            ExceptionBuilder.TraceExceptionWithoutRethrow(exception);
        }
        return args;
    }

    private DataRowChangeEventArgs RaiseRowChanging(DataRowChangeEventArgs args, DataRow eRow, DataRowAction eAction)
    {
        if (this.UpdatingCurrent(eRow, eAction) && (this.IsTypedDataTable || (this.onRowChangingDelegate != null)))
        {
            eRow.inChangingEvent = true;
            try
            {
                args = this.OnRowChanging(args, eRow, eAction);
                return args;
            }
            finally
            {
                eRow.inChangingEvent = false;
            }
        }
        if (((DataRowAction.Delete == eAction) && (eRow.newRecord != -1)) && (this.IsTypedDataTable || (this.onRowDeletingDelegate != null)))
        {
            eRow.inDeletingEvent = true;
            try
            {
                if (args == null)
                {
                    args = new DataRowChangeEventArgs(eRow, eAction);
                }
                this.OnRowDeleting(args);
            }
            finally
            {
                eRow.inDeletingEvent = false;
            }
        }
        return args;
    }

    private DataRowChangeEventArgs RaiseRowChanging(DataRowChangeEventArgs args, DataRow eRow, DataRowAction eAction, boolean fireEvent)
    {
        if (this.EnforceConstraints && !this.inLoad)
        {
            int count = this.columnCollection.Count;
            for (int i = 0; i < count; i++)
            {
                DataColumn column = this.columnCollection[i];
                if (!column.Computed || (eAction != DataRowAction.Add))
                {
                    column.CheckColumnConstraint(eRow, eAction);
                }
            }
            int num3 = this.constraintCollection.Count;
            for (int j = 0; j < num3; j++)
            {
                this.constraintCollection[j].CheckConstraint(eRow, eAction);
            }
        }
        if (fireEvent)
        {
            args = this.RaiseRowChanging(args, eRow, eAction);
        }
        if ((!this.inDataLoad && !this.MergingData) && ((eAction != DataRowAction.Nothing) && (eAction != DataRowAction.ChangeOriginal)))
        {
            this.CascadeAll(eRow, eAction);
        }
        return args;
    }

    protected void ReadEndElement(XmlReader reader)
    {
        while (reader.NodeType == XmlNodeType.Whitespace)
        {
            reader.Skip();
        }
        if (reader.NodeType == XmlNodeType.None)
        {
            reader.Skip();
        }
        else if (reader.NodeType == XmlNodeType.EndElement)
        {
            reader.ReadEndElement();
        }
    }

    protected void ReadXDRSchema(XmlReader reader)
    {
        new XmlDocument().ReadNode(reader);
    }

    public XmlReadMode ReadXml(Stream stream)
    {
        if (stream == null)
        {
            return XmlReadMode.Auto;
        }
        return this.ReadXml(new XmlTextReader(stream), false);
    }

    public XmlReadMode ReadXml(TextReader reader)
    {
        if (reader == null)
        {
            return XmlReadMode.Auto;
        }
        return this.ReadXml(new XmlTextReader(reader), false);
    }

    public XmlReadMode ReadXml(String fileName)
    {
        XmlReadMode mode;
        XmlTextReader reader = new XmlTextReader(fileName);
        try
        {
            mode = this.ReadXml(reader, false);
        }
        finally
        {
            reader.Close();
        }
        return mode;
    }

    public XmlReadMode ReadXml(XmlReader reader)
    {
        return this.ReadXml(reader, false);
    }

    protected XmlReadMode ReadXml(XmlReader reader, boolean denyResolving)
    {
        XmlReadMode mode;
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.ReadXml|INFO> %d#, denyResolving=%d{boolean}\n", this.ObjectID, denyResolving);
        try
        {
            RowDiffIdUsageSection section = new RowDiffIdUsageSection();
            boolean flag5 = false;
            boolean flag2 = false;
            boolean flag4 = false;
            boolean isXdr = false;
            int depth = -1;
            XmlReadMode auto = XmlReadMode.Auto;
            section.Prepare(this);
            if (reader == null)
            {
                return auto;
            }
            boolean originalEnforceConstraint = false;
            if (this.DataSet != null)
            {
                originalEnforceConstraint = this.DataSet.EnforceConstraints;
                this.DataSet.EnforceConstraints = false;
            }
            else
            {
                originalEnforceConstraint = this.EnforceConstraints;
                this.EnforceConstraints = false;
            }
            if (reader is XmlTextReader)
            {
                ((XmlTextReader) reader).WhitespaceHandling = WhitespaceHandling.Significant;
            }
            XmlDocument document = new XmlDocument();
            XmlDataLoader loader = null;
            reader.MoveToContent();
            if ((this.Columns.Count == 0) && this.IsEmptyXml(reader))
            {
                reader.Read();
                return auto;
            }
            if (reader.NodeType == XmlNodeType.Element)
            {
                depth = reader.Depth;
                if ((reader.LocalName == "diffgram") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1"))
                {
                    if (this.Columns.Count == 0)
                    {
                        if (!reader.IsEmptyElement)
                        {
                            throw ExceptionBuilder.DataTableInferenceNotSupported();
                        }
                        reader.Read();
                        return XmlReadMode.DiffGram;
                    }
                    this.ReadXmlDiffgram(reader);
                    this.ReadEndElement(reader);
                    this.RestoreConstraint(originalEnforceConstraint);
                    return XmlReadMode.DiffGram;
                }
                if ((reader.LocalName == "Schema") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-data"))
                {
                    this.ReadXDRSchema(reader);
                    this.RestoreConstraint(originalEnforceConstraint);
                    return XmlReadMode.ReadSchema;
                }
                if ((reader.LocalName == "schema") && (reader.NamespaceURI == "http://www.w3.org/2001/XMLSchema"))
                {
                    this.ReadXmlSchema(reader, denyResolving);
                    this.RestoreConstraint(originalEnforceConstraint);
                    return XmlReadMode.ReadSchema;
                }
                if ((reader.LocalName == "schema") && reader.NamespaceURI.StartsWith("http://www.w3.org/", StringComparison.Ordinal))
                {
                    if (this.DataSet != null)
                    {
                        this.DataSet.RestoreEnforceConstraints(originalEnforceConstraint);
                    }
                    else
                    {
                        this.enforceConstraints = originalEnforceConstraint;
                    }
                    throw ExceptionBuilder.DataSetUnsupportedSchema("http://www.w3.org/2001/XMLSchema");
                }
                XmlElement topNode = document.CreateElement(reader.Prefix, reader.LocalName, reader.NamespaceURI);
                if (reader.HasAttributes)
                {
                    int attributeCount = reader.AttributeCount;
                    for (int i = 0; i < attributeCount; i++)
                    {
                        reader.MoveToAttribute(i);
                        if (reader.NamespaceURI.Equals("http://www.w3.org/2000/xmlns/"))
                        {
                            topNode.SetAttribute(reader.Name, reader.GetAttribute(i));
                        }
                        else
                        {
                            XmlAttribute attribute = topNode.SetAttributeNode(reader.LocalName, reader.NamespaceURI);
                            attribute.Prefix = reader.Prefix;
                            attribute.Value = reader.GetAttribute(i);
                        }
                    }
                }
                reader.Read();
                while (this.MoveToElement(reader, depth))
                {
                    if ((reader.LocalName == "diffgram") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1"))
                    {
                        this.ReadXmlDiffgram(reader);
                        this.ReadEndElement(reader);
                        this.RestoreConstraint(originalEnforceConstraint);
                        return XmlReadMode.DiffGram;
                    }
                    if ((!flag2 && !flag5) && ((reader.LocalName == "Schema") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-data")))
                    {
                        this.ReadXDRSchema(reader);
                        flag2 = true;
                        isXdr = true;
                    }
                    else
                    {
                        if ((reader.LocalName == "schema") && (reader.NamespaceURI == "http://www.w3.org/2001/XMLSchema"))
                        {
                            this.ReadXmlSchema(reader, denyResolving);
                            flag2 = true;
                            continue;
                        }
                        if ((reader.LocalName == "schema") && reader.NamespaceURI.StartsWith("http://www.w3.org/", StringComparison.Ordinal))
                        {
                            if (this.DataSet != null)
                            {
                                this.DataSet.RestoreEnforceConstraints(originalEnforceConstraint);
                            }
                            else
                            {
                                this.enforceConstraints = originalEnforceConstraint;
                            }
                            throw ExceptionBuilder.DataSetUnsupportedSchema("http://www.w3.org/2001/XMLSchema");
                        }
                        if ((reader.LocalName == "diffgram") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1"))
                        {
                            this.ReadXmlDiffgram(reader);
                            flag4 = true;
                            auto = XmlReadMode.DiffGram;
                        }
                        else
                        {
                            flag5 = true;
                            if (!flag2 && (this.Columns.Count == 0))
                            {
                                XmlNode newChild = document.ReadNode(reader);
                                topNode.AppendChild(newChild);
                                continue;
                            }
                            if (loader == null)
                            {
                                loader = new XmlDataLoader(this, isXdr, topNode, false);
                            }
                            loader.LoadData(reader);
                            if (flag2)
                            {
                                auto = XmlReadMode.ReadSchema;
                                continue;
                            }
                            auto = XmlReadMode.IgnoreSchema;
                        }
                    }
                }
                this.ReadEndElement(reader);
                document.AppendChild(topNode);
                if (!flag2 && (this.Columns.Count == 0))
                {
                    if (!this.IsEmptyXml(reader))
                    {
                        throw ExceptionBuilder.DataTableInferenceNotSupported();
                    }
                    reader.Read();
                    return auto;
                }
                if (loader == null)
                {
                    loader = new XmlDataLoader(this, isXdr, false);
                }
            }
            this.RestoreConstraint(originalEnforceConstraint);
            mode = auto;
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
        return mode;
    }

    protected XmlReadMode ReadXml(XmlReader reader, XmlReadMode mode, boolean denyResolving)
    {
        RowDiffIdUsageSection section = new RowDiffIdUsageSection();
        boolean flag3 = false;
        boolean flag4 = false;
        boolean isXdr = false;
        int depth = -1;
        XmlReadMode diffGram = mode;
        section.Prepare(this);
        if (reader != null)
        {
            boolean originalEnforceConstraint = false;
            if (this.DataSet != null)
            {
                originalEnforceConstraint = this.DataSet.EnforceConstraints;
                this.DataSet.EnforceConstraints = false;
            }
            else
            {
                originalEnforceConstraint = this.EnforceConstraints;
                this.EnforceConstraints = false;
            }
            if (reader is XmlTextReader)
            {
                ((XmlTextReader) reader).WhitespaceHandling = WhitespaceHandling.Significant;
            }
            XmlDocument document = new XmlDocument();
            if ((mode != XmlReadMode.Fragment) && (reader.NodeType == XmlNodeType.Element))
            {
                depth = reader.Depth;
            }
            reader.MoveToContent();
            if ((this.Columns.Count == 0) && this.IsEmptyXml(reader))
            {
                reader.Read();
                return diffGram;
            }
            XmlDataLoader loader = null;
            if (reader.NodeType == XmlNodeType.Element)
            {
                XmlElement topNode = null;
                if (mode == XmlReadMode.Fragment)
                {
                    document.AppendChild(document.CreateElement("ds_sqlXmlWraPPeR"));
                    topNode = document.DocumentElement;
                }
                else
                {
                    if ((reader.LocalName == "diffgram") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1"))
                    {
                        if ((mode == XmlReadMode.DiffGram) || (mode == XmlReadMode.IgnoreSchema))
                        {
                            if (this.Columns.Count == 0)
                            {
                                if (!reader.IsEmptyElement)
                                {
                                    throw ExceptionBuilder.DataTableInferenceNotSupported();
                                }
                                reader.Read();
                                return XmlReadMode.DiffGram;
                            }
                            this.ReadXmlDiffgram(reader);
                            this.ReadEndElement(reader);
                        }
                        else
                        {
                            reader.Skip();
                        }
                        this.RestoreConstraint(originalEnforceConstraint);
                        return diffGram;
                    }
                    if ((reader.LocalName == "Schema") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-data"))
                    {
                        if ((mode != XmlReadMode.IgnoreSchema) && (mode != XmlReadMode.InferSchema))
                        {
                            this.ReadXDRSchema(reader);
                        }
                        else
                        {
                            reader.Skip();
                        }
                        this.RestoreConstraint(originalEnforceConstraint);
                        return diffGram;
                    }
                    if ((reader.LocalName == "schema") && (reader.NamespaceURI == "http://www.w3.org/2001/XMLSchema"))
                    {
                        if ((mode != XmlReadMode.IgnoreSchema) && (mode != XmlReadMode.InferSchema))
                        {
                            this.ReadXmlSchema(reader, denyResolving);
                        }
                        else
                        {
                            reader.Skip();
                        }
                        this.RestoreConstraint(originalEnforceConstraint);
                        return diffGram;
                    }
                    if ((reader.LocalName == "schema") && reader.NamespaceURI.StartsWith("http://www.w3.org/", StringComparison.Ordinal))
                    {
                        if (this.DataSet != null)
                        {
                            this.DataSet.RestoreEnforceConstraints(originalEnforceConstraint);
                        }
                        else
                        {
                            this.enforceConstraints = originalEnforceConstraint;
                        }
                        throw ExceptionBuilder.DataSetUnsupportedSchema("http://www.w3.org/2001/XMLSchema");
                    }
                    topNode = document.CreateElement(reader.Prefix, reader.LocalName, reader.NamespaceURI);
                    if (reader.HasAttributes)
                    {
                        int attributeCount = reader.AttributeCount;
                        for (int i = 0; i < attributeCount; i++)
                        {
                            reader.MoveToAttribute(i);
                            if (reader.NamespaceURI.Equals("http://www.w3.org/2000/xmlns/"))
                            {
                                topNode.SetAttribute(reader.Name, reader.GetAttribute(i));
                            }
                            else
                            {
                                XmlAttribute attribute = topNode.SetAttributeNode(reader.LocalName, reader.NamespaceURI);
                                attribute.Prefix = reader.Prefix;
                                attribute.Value = reader.GetAttribute(i);
                            }
                        }
                    }
                    reader.Read();
                }
                while (this.MoveToElement(reader, depth))
                {
                    if ((reader.LocalName == "Schema") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-data"))
                    {
                        if ((!flag3 && !flag4) && ((mode != XmlReadMode.IgnoreSchema) && (mode != XmlReadMode.InferSchema)))
                        {
                            this.ReadXDRSchema(reader);
                            flag3 = true;
                            isXdr = true;
                        }
                        else
                        {
                            reader.Skip();
                        }
                    }
                    else
                    {
                        if ((reader.LocalName == "schema") && (reader.NamespaceURI == "http://www.w3.org/2001/XMLSchema"))
                        {
                            if ((mode != XmlReadMode.IgnoreSchema) && (mode != XmlReadMode.InferSchema))
                            {
                                this.ReadXmlSchema(reader, denyResolving);
                                flag3 = true;
                            }
                            else
                            {
                                reader.Skip();
                            }
                            continue;
                        }
                        if ((reader.LocalName == "diffgram") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1"))
                        {
                            if ((mode == XmlReadMode.DiffGram) || (mode == XmlReadMode.IgnoreSchema))
                            {
                                if (this.Columns.Count == 0)
                                {
                                    if (!reader.IsEmptyElement)
                                    {
                                        throw ExceptionBuilder.DataTableInferenceNotSupported();
                                    }
                                    reader.Read();
                                    return XmlReadMode.DiffGram;
                                }
                                this.ReadXmlDiffgram(reader);
                                diffGram = XmlReadMode.DiffGram;
                            }
                            else
                            {
                                reader.Skip();
                            }
                            continue;
                        }
                        if ((reader.LocalName == "schema") && reader.NamespaceURI.StartsWith("http://www.w3.org/", StringComparison.Ordinal))
                        {
                            if (this.DataSet != null)
                            {
                                this.DataSet.RestoreEnforceConstraints(originalEnforceConstraint);
                            }
                            else
                            {
                                this.enforceConstraints = originalEnforceConstraint;
                            }
                            throw ExceptionBuilder.DataSetUnsupportedSchema("http://www.w3.org/2001/XMLSchema");
                        }
                        if (mode == XmlReadMode.DiffGram)
                        {
                            reader.Skip();
                        }
                        else
                        {
                            flag4 = true;
                            if (mode == XmlReadMode.InferSchema)
                            {
                                XmlNode newChild = document.ReadNode(reader);
                                topNode.AppendChild(newChild);
                                continue;
                            }
                            if (this.Columns.Count == 0)
                            {
                                throw ExceptionBuilder.DataTableInferenceNotSupported();
                            }
                            if (loader == null)
                            {
                                loader = new XmlDataLoader(this, isXdr, topNode, mode == XmlReadMode.IgnoreSchema);
                            }
                            loader.LoadData(reader);
                        }
                    }
                }
                this.ReadEndElement(reader);
                document.AppendChild(topNode);
                if (loader == null)
                {
                    loader = new XmlDataLoader(this, isXdr, mode == XmlReadMode.IgnoreSchema);
                }
                if (mode == XmlReadMode.DiffGram)
                {
                    this.RestoreConstraint(originalEnforceConstraint);
                    return diffGram;
                }
                if ((mode == XmlReadMode.InferSchema) && (this.Columns.Count == 0))
                {
                    throw ExceptionBuilder.DataTableInferenceNotSupported();
                }
            }
            this.RestoreConstraint(originalEnforceConstraint);
        }
        return diffGram;
    }

    private void ReadXmlDiffgram(XmlReader reader)
    {
        DataTable table;
        boolean flag;
        int depth = reader.Depth;
        boolean enforceConstraints = this.EnforceConstraints;
        this.EnforceConstraints = false;
        if (this.Rows.Count == 0)
        {
            flag = true;
            table = this;
        }
        else
        {
            flag = false;
            table = this.Clone();
            table.EnforceConstraints = false;
        }
        table.Rows.nullInList = 0;
        reader.MoveToContent();
        if ((reader.LocalName == "diffgram") || (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1"))
        {
            reader.Read();
            if (reader.NodeType == XmlNodeType.Whitespace)
            {
                this.MoveToElement(reader, reader.Depth - 1);
            }
            table.fInLoadDiffgram = true;
            if (reader.Depth > depth)
            {
                if ((reader.NamespaceURI != "urn:schemas-microsoft-com:xml-diffgram-v1") && (reader.NamespaceURI != "urn:schemas-microsoft-com:xml-msdata"))
                {
                    XmlElement topNode = new XmlDocument().CreateElement(reader.Prefix, reader.LocalName, reader.NamespaceURI);
                    reader.Read();
                    if ((reader.Depth - 1) > depth)
                    {
                        new XmlDataLoader(table, false, topNode, false) { isDiffgram = true }.LoadData(reader);
                    }
                    this.ReadEndElement(reader);
                }
                if (((reader.LocalName == "before") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1")) || ((reader.LocalName == "errors") && (reader.NamespaceURI == "urn:schemas-microsoft-com:xml-diffgram-v1")))
                {
                    new XMLDiffLoader().LoadDiffGram(table, reader);
                }
                while (reader.Depth > depth)
                {
                    reader.Read();
                }
                this.ReadEndElement(reader);
            }
            if (table.Rows.nullInList > 0)
            {
                throw ExceptionBuilder.RowInsertMissing(table.TableName);
            }
            table.fInLoadDiffgram = false;
            List<DataTable> tableList = new List<DataTable> {
            this
        };
            this.CreateTableList(this, tableList);
            for (int i = 0; i < tableList.Count; i++)
            {
                DataRelation[] nestedParentRelations = tableList[i].NestedParentRelations;
                for(DataRelation relation : nestedParentRelations)
                {
                    if ((relation != null) && (relation.ParentTable == tableList[i]))
                    {
                        for(DataRow row : tableList[i].Rows)
                        {
                            for(DataRelation relation2 : nestedParentRelations)
                            {
                                row.CheckForLoops(relation2);
                            }
                        }
                    }
                }
            }
            if (!flag)
            {
                this.Merge(table);
            }
            this.EnforceConstraints = enforceConstraints;
        }
    }

    public void ReadXmlSchema(Stream stream)
    {
        if (stream != null)
        {
            this.ReadXmlSchema(new XmlTextReader(stream), false);
        }
    }

    public void ReadXmlSchema(TextReader reader)
    {
        if (reader != null)
        {
            this.ReadXmlSchema(new XmlTextReader(reader), false);
        }
    }

    public void ReadXmlSchema(String fileName)
    {
        XmlTextReader reader = new XmlTextReader(fileName);
        try
        {
            this.ReadXmlSchema(reader, false);
        }
        finally
        {
            reader.Close();
        }
    }

    public void ReadXmlSchema(XmlReader reader)
    {
        this.ReadXmlSchema(reader, false);
    }

    protected void ReadXmlSchema(XmlReader reader, boolean denyResolving)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.ReadXmlSchema|INFO> %d#, denyResolving=%d{boolean}\n", this.ObjectID, denyResolving);
        try
        {
            System.Data.DataSet set = new System.Data.DataSet();
            SerializationFormat remotingFormat = this.RemotingFormat;
            set.ReadXmlSchema(reader, denyResolving);
            String mainTableName = set.MainTableName;
            if (!ADP.IsEmpty(this.tableName) || !ADP.IsEmpty(mainTableName))
            {
                DataTable currentTable = null;
                if (!ADP.IsEmpty(this.tableName))
                {
                    if (!ADP.IsEmpty(this.Namespace))
                    {
                        currentTable = set.Tables[this.tableName, this.Namespace];
                    }
                    else
                    {
                        int num3 = set.Tables.InternalIndexOf(this.tableName);
                        if (num3 > -1)
                        {
                            currentTable = set.Tables[num3];
                        }
                    }
                }
                else
                {
                    String str3 = "";
                    int index = mainTableName.IndexOf(':');
                    if (index > -1)
                    {
                        str3 = mainTableName.Substring(0, index);
                    }
                    String str4 = mainTableName.Substring(index + 1, (mainTableName.Length - index) - 1);
                    currentTable = set.Tables[str4, str3];
                }
                if (currentTable == null)
                {
                    String tableName = String.Empty;
                    if (!ADP.IsEmpty(this.tableName))
                    {
                        tableName = (this.Namespace.Length > 0) ? (this.Namespace + ":" + this.tableName) : this.tableName;
                    }
                    else
                    {
                        tableName = mainTableName;
                    }
                    throw ExceptionBuilder.TableNotFound(tableName);
                }
                currentTable._remotingFormat = remotingFormat;
                List<DataTable> tableList = new List<DataTable> {
                currentTable
            };
                this.CreateTableList(currentTable, tableList);
                List<DataRelation> relationList = new List<DataRelation>();
                this.CreateRelationList(tableList, relationList);
                if (relationList.Count == 0)
                {
                    if (this.Columns.Count == 0)
                    {
                        DataTable table5 = currentTable;
                        if (table5 != null)
                        {
                            table5.CloneTo(this, null, false);
                        }
                        if ((this.DataSet == null) && (this.tableNamespace == null))
                        {
                            this.tableNamespace = table5.Namespace;
                        }
                    }
                }
                else
                {
                    if (ADP.IsEmpty(this.TableName))
                    {
                        this.TableName = currentTable.TableName;
                        if (!ADP.IsEmpty(currentTable.Namespace))
                        {
                            this.Namespace = currentTable.Namespace;
                        }
                    }
                    if (this.DataSet == null)
                    {
                        System.Data.DataSet set2 = new System.Data.DataSet(set.DataSetName);
                        set2.SetLocaleValue(set.Locale, set.ShouldSerializeLocale());
                        set2.CaseSensitive = set.CaseSensitive;
                        set2.Namespace = set.Namespace;
                        set2.mainTableName = set.mainTableName;
                        set2.RemotingFormat = set.RemotingFormat;
                        set2.Tables.Add(this);
                    }
                    this.CloneHierarchy(currentTable, this.DataSet, null);
                    for(DataTable table2 : tableList)
                    {
                        DataTable table4 = this.DataSet.Tables[table2.tableName, table2.Namespace];
                        DataTable table6 = set.Tables[table2.tableName, table2.Namespace];
                        for(Constraint constraint3 : table6.Constraints)
                        {
                            ForeignKeyConstraint constraint = constraint3 as ForeignKeyConstraint;
                            if (((constraint != null) && (constraint.Table != constraint.RelatedTable)) && (tableList.Contains(constraint.Table) && tableList.Contains(constraint.RelatedTable)))
                            {
                                ForeignKeyConstraint constraint2 = (ForeignKeyConstraint) constraint.Clone(table4.DataSet);
                                if (!table4.Constraints.Contains(constraint2.ConstraintName))
                                {
                                    table4.Constraints.Add(constraint2);
                                }
                            }
                        }
                    }
                    for(DataRelation relation : relationList)
                    {
                        if (!this.DataSet.Relations.Contains(relation.RelationName))
                        {
                            this.DataSet.Relations.Add(relation.Clone(this.DataSet));
                        }
                    }
                    boolean flag = false;
                    for(DataTable table3 : tableList)
                    {
                        for(DataColumn column : table3.Columns)
                        {
                            flag = false;
                            if (column.Expression.Length != 0)
                            {
                                DataColumn[] dependency = column.DataExpression.GetDependency();
                                for (int i = 0; i < dependency.Length; i++)
                                {
                                    if (!tableList.Contains(dependency[i].Table))
                                    {
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            if (!flag)
                            {
                                this.DataSet.Tables[table3.TableName, table3.Namespace].Columns[column.ColumnName].Expression = column.Expression;
                            }
                        }
                        flag = false;
                    }
                }
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    protected void ReadXmlSerializable(XmlReader reader)
    {
        this.ReadXml(reader, XmlReadMode.DiffGram, true);
    }

    protected void ReadXSDSchema(XmlReader reader, boolean denyResolving)
    {
        XmlSchemaSet schemaSet = new XmlSchemaSet();
        while ((reader.LocalName == "schema") && (reader.NamespaceURI == "http://www.w3.org/2001/XMLSchema"))
        {
            XmlSchema schema = XmlSchema.Read(reader, null);
            schemaSet.Add(schema);
            this.ReadEndElement(reader);
        }
        schemaSet.Compile();
        new XSDSchema().LoadSchema(schemaSet, this);
    }

    protected void RecordChanged(int record)
    {
        this.SetShadowIndexes();
        try
        {
            int count = this.shadowIndexes.Count;
            for (int i = 0; i < count; i++)
            {
                Index index = this.shadowIndexes[i];
                if (0 < index.RefCount)
                {
                    index.RecordChanged(record);
                }
            }
        }
        finally
        {
            this.RestoreShadowIndexes();
        }
    }

    protected void RecordChanged(int[] oldIndex, int[] newIndex)
    {
        this.SetShadowIndexes();
        try
        {
            int count = this.shadowIndexes.Count;
            for (int i = 0; i < count; i++)
            {
                Index index = this.shadowIndexes[i];
                if (0 < index.RefCount)
                {
                    index.RecordChanged(oldIndex[i], newIndex[i]);
                }
            }
        }
        finally
        {
            this.RestoreShadowIndexes();
        }
    }

    protected void RecordStateChanged(int record, DataViewRowState oldState, DataViewRowState newState)
    {
        this.SetShadowIndexes();
        try
        {
            int count = this.shadowIndexes.Count;
            for (int i = 0; i < count; i++)
            {
                Index index = this.shadowIndexes[i];
                if (0 < index.RefCount)
                {
                    index.RecordStateChanged(record, oldState, newState);
                }
            }
        }
        finally
        {
            this.RestoreShadowIndexes();
        }
    }

    protected void RecordStateChanged(int record1, DataViewRowState oldState1, DataViewRowState newState1, int record2, DataViewRowState oldState2, DataViewRowState newState2)
    {
        this.SetShadowIndexes();
        try
        {
            int count = this.shadowIndexes.Count;
            for (int i = 0; i < count; i++)
            {
                Index index = this.shadowIndexes[i];
                if (0 < index.RefCount)
                {
                    if ((record1 != -1) && (record2 != -1))
                    {
                        index.RecordStateChanged(record1, oldState1, newState1, record2, oldState2, newState2);
                    }
                    else if (record1 != -1)
                    {
                        index.RecordStateChanged(record1, oldState1, newState1);
                    }
                    else if (record2 != -1)
                    {
                        index.RecordStateChanged(record2, oldState2, newState2);
                    }
                }
            }
        }
        finally
        {
            this.RestoreShadowIndexes();
        }
    }

    public void RejectChanges()
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.RejectChanges|API> %d#\n", this.ObjectID);
        try
        {
            DataRow[] array = new DataRow[this.Rows.Count];
            this.Rows.CopyTo(array, 0);
            for (int i = 0; i < array.Length; i++)
            {
                this.RollbackRow(array[i]);
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    protected void RemoveDependentColumn(DataColumn expressionColumn)
    {
        if ((this.dependentColumns != null) && this.dependentColumns.Contains(expressionColumn))
        {
            this.dependentColumns.Remove(expressionColumn);
        }
    }

    protected int[] RemoveRecordFromIndexes(DataRow row, DataRowVersion version)
    {
        int count = this.LiveIndexes.Count;
        int[] numArray = new int[count];
        int recordFromVersion = row.GetRecordFromVersion(version);
        DataViewRowState recordState = row.GetRecordState(recordFromVersion);
        while (--count >= 0)
        {
            if (row.HasVersion(version) && ((recordState & this.indexes[count].RecordStates) != DataViewRowState.None))
            {
                int index = this.indexes[count].GetIndex(recordFromVersion);
                if (index > -1)
                {
                    numArray[count] = index;
                    this.indexes[count].DeleteRecordFromIndex(index);
                }
                else
                {
                    numArray[count] = -1;
                }
            }
            else
            {
                numArray[count] = -1;
            }
        }
        return numArray;
    }

    protected void RemoveRow(DataRow row, boolean check)
    {
        if (row.rowID == -1L)
        {
            throw ExceptionBuilder.RowAlreadyRemoved();
        }
        if (check && (this.dataSet != null))
        {
            ParentForeignKeyConstraintEnumerator enumerator = new ParentForeignKeyConstraintEnumerator(this.dataSet, this);
            while (enumerator.GetNext())
            {
                enumerator.GetForeignKeyConstraint().CheckCanRemoveParentRow(row);
            }
        }
        int oldRecord = row.oldRecord;
        int newRecord = row.newRecord;
        DataViewRowState recordState = row.GetRecordState(oldRecord);
        DataViewRowState state = row.GetRecordState(newRecord);
        row.oldRecord = -1;
        row.newRecord = -1;
        if (oldRecord == newRecord)
        {
            oldRecord = -1;
        }
        this.RecordStateChanged(oldRecord, recordState, DataViewRowState.None, newRecord, state, DataViewRowState.None);
        this.FreeRecord(ref oldRecord);
        this.FreeRecord(ref newRecord);
        row.rowID = -1L;
        this.Rows.ArrayRemove(row);
    }

    public void Reset()
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.Reset|API> %d#\n", this.ObjectID);
        try
        {
            this.Clear();
            this.ResetConstraints();
            DataRelationCollection parentRelations = this.ParentRelations;
            int count = parentRelations.Count;
            while (count > 0)
            {
                count--;
                parentRelations.RemoveAt(count);
            }
            parentRelations = this.ChildRelations;
            count = parentRelations.Count;
            while (count > 0)
            {
                count--;
                parentRelations.RemoveAt(count);
            }
            this.Columns.Clear();
            this.indexes.Clear();
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    private void ResetCaseSensitive()
    {
        this.SetCaseSensitiveValue((this.dataSet != null) && this.dataSet.CaseSensitive, true, true);
        this._caseSensitiveUserSet = false;
    }

    private void ResetColumns()
    {
        this.Columns.Clear();
    }

    private void ResetConstraints()
    {
        this.Constraints.Clear();
    }

    protected void ResetIndexes()
    {
        this.ResetInternalIndexes(null);
    }

    protected void ResetInternalIndexes(DataColumn column)
    {
        this.SetShadowIndexes();
        try
        {
            int count = this.shadowIndexes.Count;
            for (int i = 0; i < count; i++)
            {
                Index index = this.shadowIndexes[i];
                if (0 < index.RefCount)
                {
                    if (column == null)
                    {
                        index.Reset();
                        continue;
                    }
                    boolean flag = false;
                    for(IndexField field : index.IndexFields)
                    {
                        if ( Object.ReferenceEquals(column, field.Column))
                        {
                            flag = true;
                            break;
                        }
                    }
                    if (flag)
                    {
                        index.Reset();
                    }
                }
            }
        }
        finally
        {
            this.RestoreShadowIndexes();
        }
    }

    private void ResetNamespace()
    {
        this.Namespace = null;
    }

    private void ResetPrimaryKey()
    {
        this.PrimaryKey = null;
    }

    private void RestoreConstraint(boolean originalEnforceConstraint)
    {
        if (this.DataSet != null)
        {
            this.DataSet.EnforceConstraints = originalEnforceConstraint;
        }
        else
        {
            this.EnforceConstraints = originalEnforceConstraint;
        }
    }

    protected void RestoreIndexEvents(boolean forceReset)
    {
        Bid.Trace("<ds.DataTable.RestoreIndexEvents|Info> %d#, %d\n", this.ObjectID, this._suspendIndexEvents);
        if (0 < this._suspendIndexEvents)
        {
            this._suspendIndexEvents--;
            if (this._suspendIndexEvents == 0)
            {
                Exception exception = null;
                this.SetShadowIndexes();
                try
                {
                    int count = this.shadowIndexes.Count;
                    for (int i = 0; i < count; i++)
                    {
                        Index index = this.shadowIndexes[i];
                        try
                        {
                            if (forceReset || index.HasRemoteAggregate)
                            {
                                index.Reset();
                            }
                            else
                            {
                                index.FireResetEvent();
                            }
                        }
                        catch (Exception exception2)
                        {
                            if (!ADP.IsCatchableExceptionType(exception2))
                            {
                                throw;
                            }
                            ExceptionBuilder.TraceExceptionWithoutRethrow(exception2);
                            if (exception == null)
                            {
                                exception = exception2;
                            }
                        }
                    }
                    if (exception != null)
                    {
                        throw exception;
                    }
                }
                finally
                {
                    this.RestoreShadowIndexes();
                }
            }
        }
    }

    private void RestoreShadowIndexes()
    {
        this.shadowCount--;
        if (this.shadowCount == 0)
        {
            this.shadowIndexes = null;
        }
    }

    protected void RollbackRow(DataRow row)
    {
        row.CancelEdit();
        this.SetNewRecord(row, row.oldRecord, DataRowAction.Rollback, false, true, false);
    }

    public DataRow[] Select()
    {
        Bid.Trace("<ds.DataTable.Select|API> %d#\n", this.ObjectID);
        return new System.Data.Select(this, "", "", DataViewRowState.CurrentRows).SelectRows();
    }

    public DataRow[] Select(String filterExpression)
    {
        Bid.Trace("<ds.DataTable.Select|API> %d#, filterExpression='%ls'\n", this.ObjectID, filterExpression);
        return new System.Data.Select(this, filterExpression, "", DataViewRowState.CurrentRows).SelectRows();
    }

    public DataRow[] Select(String filterExpression, String sort)
    {
        Bid.Trace("<ds.DataTable.Select|API> %d#, filterExpression='%ls', sort='%ls'\n", this.ObjectID, filterExpression, sort);
        return new System.Data.Select(this, filterExpression, sort, DataViewRowState.CurrentRows).SelectRows();
    }

    public DataRow[] Select(String filterExpression, String sort, DataViewRowState recordStates)
    {
        Bid.Trace("<ds.DataTable.Select|API> %d#, filterExpression='%ls', sort='%ls', recordStates=%d{ds.DataViewRowState}\n", this.ObjectID, filterExpression, sort, (int) recordStates);
        return new System.Data.Select(this, filterExpression, sort, recordStates).SelectRows();
    }

    protected void SerializeConstraints(SerializationInfo info, StreamingContext context, int serIndex, boolean allConstraints)
    {
        ArrayList list3 = new ArrayList();
        for (int i = 0; i < this.Constraints.Count; i++)
        {
            Constraint constraint3 = this.Constraints[i];
            UniqueConstraint constraint2 = constraint3 as UniqueConstraint;
            if (constraint2 != null)
            {
                int[] numArray4 = new int[constraint2.Columns.Length];
                for (int j = 0; j < numArray4.Length; j++)
                {
                    numArray4[j] = constraint2.Columns[j].Ordinal;
                }
                ArrayList list2 = new ArrayList();
                list2.Add("U");
                list2.Add(constraint2.ConstraintName);
                list2.Add(numArray4);
                list2.Add(constraint2.IsPrimaryKey);
                list2.Add(constraint2.ExtendedProperties);
                list3.Add(list2);
            }
            else
            {
                ForeignKeyConstraint constraint = constraint3 as ForeignKeyConstraint;
                if (allConstraints || ((constraint.Table == this) && (constraint.RelatedTable == this)))
                {
                    int[] numArray3 = new int[constraint.RelatedColumns.Length + 1];
                    numArray3[0] = allConstraints ? this.DataSet.Tables.IndexOf(constraint.RelatedTable) : 0;
                    for (int k = 1; k < numArray3.Length; k++)
                    {
                        numArray3[k] = constraint.RelatedColumns[k - 1].Ordinal;
                    }
                    int[] numArray2 = new int[constraint.Columns.Length + 1];
                    numArray2[0] = allConstraints ? this.DataSet.Tables.IndexOf(constraint.Table) : 0;
                    for (int m = 1; m < numArray2.Length; m++)
                    {
                        numArray2[m] = constraint.Columns[m - 1].Ordinal;
                    }
                    ArrayList list = new ArrayList();
                    list.Add("F");
                    list.Add(constraint.ConstraintName);
                    list.Add(numArray3);
                    list.Add(numArray2);
                    list.Add(new int[] { constraint.AcceptRejectRule, constraint.UpdateRule, constraint.DeleteRule });
                    list.Add(constraint.ExtendedProperties);
                    list3.Add(list);
                }
            }
        }
        info.AddValue(String.Format(CultureInfo.InvariantCulture, "DataTable_{0}.Constraints", new  Object[] { serIndex }), list3);
    }

    private void SerializeDataTable(SerializationInfo info, StreamingContext context, boolean isSingleTable, SerializationFormat remotingFormat)
    {
        info.AddValue("DataTable.RemotingVersion", new Version(2, 0));
        if (remotingFormat != SerializationFormat.Xml)
        {
            info.AddValue("DataTable.RemotingFormat", remotingFormat);
        }
        if (remotingFormat != SerializationFormat.Xml)
        {
            this.SerializeTableSchema(info, context, isSingleTable);
            if (isSingleTable)
            {
                this.SerializeTableData(info, context, 0);
            }
        }
        else
        {
            String str = "";
            boolean flag = false;
            if (this.dataSet == null)
            {
                System.Data.DataSet set = new System.Data.DataSet("tmpDataSet");
                set.SetLocaleValue(this._culture, this._cultureUserSet);
                set.CaseSensitive = this.CaseSensitive;
                set.namespaceURI = this.Namespace;
                set.Tables.Add(this);
                flag = true;
            }
            else
            {
                str = this.DataSet.Namespace;
                this.DataSet.namespaceURI = this.Namespace;
            }
            info.AddValue("XmlSchema", this.dataSet.GetXmlSchemaForRemoting(this));
            info.AddValue("XmlDiffGram", this.dataSet.GetRemotingDiffGram(this));
            if (flag)
            {
                this.dataSet.Tables.Remove(this);
            }
            else
            {
                this.dataSet.namespaceURI = str;
            }
        }
    }

    protected void SerializeExpressionColumns(SerializationInfo info, StreamingContext context, int serIndex)
    {
        int count = this.Columns.Count;
        for (int i = 0; i < count; i++)
        {
            info.AddValue(String.Format(CultureInfo.InvariantCulture, "DataTable_{0}.DataColumn_{1}.Expression", new  Object[] { serIndex, i }), this.Columns[i].Expression);
        }
    }

    protected void SerializeTableData(SerializationInfo info, StreamingContext context, int serIndex)
    {
        int count = this.Columns.Count;
        int num4 = this.Rows.Count;
        int num9 = 0;
        int num8 = 0;
        BitArray array = new BitArray(num4 * 3, false);
        for (int i = 0; i < num4; i++)
        {
            int num2 = i * 3;
            DataRow row = this.Rows[i];
            DataRowState rowState = row.RowState;
            switch (rowState)
            {
                case DataRowState.Unchanged:
                    break;

                case DataRowState.Added:
                    array[num2 + 1] = true;
                    break;

                case DataRowState.Deleted:
                    array[num2] = true;
                    array[num2 + 1] = true;
                    break;

                case DataRowState.Modified:
                    array[num2] = true;
                    num9++;
                    break;

                default:
                    throw ExceptionBuilder.InvalidRowState(rowState);
            }
            if (-1 != row.tempRecord)
            {
                array[num2 + 2] = true;
                num8++;
            }
        }
        int recordCount = (num4 + num9) + num8;
        ArrayList storeList = new ArrayList();
        ArrayList nullbitList = new ArrayList();
        if (recordCount > 0)
        {
            for (int k = 0; k < count; k++)
            {
                 Object emptyColumnStore = this.Columns[k].GetEmptyColumnStore(recordCount);
                storeList.Add(emptyColumnStore);
                BitArray array2 = new BitArray(recordCount);
                nullbitList.Add(array2);
            }
        }
        int storeIndex = 0;
        Hashtable rowErrors = new Hashtable();
        Hashtable colErrors = new Hashtable();
        for (int j = 0; j < num4; j++)
        {
            int num10 = this.Rows[j].CopyValuesIntoStore(storeList, nullbitList, storeIndex);
            this.GetRowAndColumnErrors(j, rowErrors, colErrors);
            storeIndex += num10;
        }
        IFormatProvider invariantCulture = CultureInfo.InvariantCulture;
        info.AddValue(String.Format(invariantCulture, "DataTable_{0}.Rows.Count", new  Object[] { serIndex }), num4);
        info.AddValue(String.Format(invariantCulture, "DataTable_{0}.Records.Count", new  Object[] { serIndex }), recordCount);
        info.AddValue(String.Format(invariantCulture, "DataTable_{0}.RowStates", new  Object[] { serIndex }), array);
        info.AddValue(String.Format(invariantCulture, "DataTable_{0}.Records", new  Object[] { serIndex }), storeList);
        info.AddValue(String.Format(invariantCulture, "DataTable_{0}.NullBits", new  Object[] { serIndex }), nullbitList);
        info.AddValue(String.Format(invariantCulture, "DataTable_{0}.RowErrors", new  Object[] { serIndex }), rowErrors);
        info.AddValue(String.Format(invariantCulture, "DataTable_{0}.ColumnErrors", new  Object[] { serIndex }), colErrors);
    }

    protected void SerializeTableSchema(SerializationInfo info, StreamingContext context, boolean isSingleTable)
    {
        info.AddValue("DataTable.TableName", this.TableName);
        info.AddValue("DataTable.Namespace", this.Namespace);
        info.AddValue("DataTable.Prefix", this.Prefix);
        info.AddValue("DataTable.CaseSensitive", this._caseSensitive);
        info.AddValue("DataTable.caseSensitiveAmbient", !this._caseSensitiveUserSet);
        info.AddValue("DataTable.LocaleLCID", this.Locale.LCID);
        info.AddValue("DataTable.MinimumCapacity", this.recordManager.MinimumCapacity);
        info.AddValue("DataTable.NestedInDataSet", this.fNestedInDataset);
        info.AddValue("DataTable.TypeName", this.TypeName.ToString());
        info.AddValue("DataTable.RepeatableElement", this.repeatableElement);
        info.AddValue("DataTable.ExtendedProperties", this.ExtendedProperties);
        info.AddValue("DataTable.Columns.Count", this.Columns.Count);
        if (isSingleTable)
        {
            List<DataTable> tableList = new List<DataTable> {
            this
        };
            if (!this.CheckForClosureOnExpressionTables(tableList))
            {
                throw ExceptionBuilder.CanNotRemoteDataTable();
            }
        }
        IFormatProvider invariantCulture = CultureInfo.InvariantCulture;
        for (int i = 0; i < this.Columns.Count; i++)
        {
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.ColumnName", new  Object[] { i }), this.Columns[i].ColumnName);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.Namespace", new  Object[] { i }), this.Columns[i]._columnUri);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.Prefix", new  Object[] { i }), this.Columns[i].Prefix);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.ColumnMapping", new  Object[] { i }), this.Columns[i].ColumnMapping);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AllowDBNull", new  Object[] { i }), this.Columns[i].AllowDBNull);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AutoIncrement", new  Object[] { i }), this.Columns[i].AutoIncrement);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AutoIncrementStep", new  Object[] { i }), this.Columns[i].AutoIncrementStep);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AutoIncrementSeed", new  Object[] { i }), this.Columns[i].AutoIncrementSeed);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.Caption", new  Object[] { i }), this.Columns[i].Caption);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.DefaultValue", new  Object[] { i }), this.Columns[i].DefaultValue);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.ReadOnly", new  Object[] { i }), this.Columns[i].ReadOnly);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.MaxLength", new  Object[] { i }), this.Columns[i].MaxLength);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.DataType", new  Object[] { i }), this.Columns[i].DataType);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.XmlDataType", new  Object[] { i }), this.Columns[i].XmlDataType);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.SimpleType", new  Object[] { i }), this.Columns[i].SimpleType);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.DateTimeMode", new  Object[] { i }), this.Columns[i].DateTimeMode);
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.AutoIncrementCurrent", new  Object[] { i }), this.Columns[i].AutoIncrementCurrent);
            if (isSingleTable)
            {
                info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.Expression", new  Object[] { i }), this.Columns[i].Expression);
            }
            info.AddValue(String.Format(invariantCulture, "DataTable.DataColumn_{0}.ExtendedProperties", new  Object[] { i }), this.Columns[i].extendedProperties);
        }
        if (isSingleTable)
        {
            this.SerializeConstraints(info, context, 0, false);
        }
    }

    protected boolean SetCaseSensitiveValue(boolean isCaseSensitive, boolean userSet, boolean resetIndexes)
    {
        if (!userSet && (this._caseSensitiveUserSet || (this._caseSensitive == isCaseSensitive)))
        {
            return false;
        }
        this._caseSensitive = isCaseSensitive;
        if (isCaseSensitive)
        {
            this._compareFlags = CompareOptions.None;
        }
        else
        {
            this._compareFlags = CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreCase;
        }
        if (resetIndexes)
        {
            this.ResetIndexes();
            for(Constraint constraint : this.Constraints)
            {
                constraint.CheckConstraint();
            }
        }
        return true;
    }

    private void SetDataRowWithLoadOption(DataRow dataRow, int recordNo, LoadOption loadOption, boolean checkReadOnly)
    {
        boolean flag = false;
        if (checkReadOnly)
        {
            for(DataColumn column : this.Columns)
            {
                if (column.ReadOnly && !column.Computed)
                {
                    switch (loadOption)
                    {
                        case LoadOption.OverwriteChanges:
                            goto Label_0058;

                        case LoadOption.PreserveChanges:
                            if (dataRow[column, DataRowVersion.Original] != column[recordNo])
                        {
                            flag = true;
                        }
                        break;

                        case LoadOption.Upsert:
                            if (dataRow[column, DataRowVersion.Current] != column[recordNo])
                        {
                            flag = true;
                        }
                        break;
                    }
                }
                continue;
                Label_0058:
                if ((dataRow[column, DataRowVersion.Current] != column[recordNo]) || (dataRow[column, DataRowVersion.Original] != column[recordNo]))
                {
                    flag = true;
                }
            }
        }
        DataRowChangeEventArgs args = null;
        DataRowAction nothing = DataRowAction.Nothing;
        int tempRecord = dataRow.tempRecord;
        dataRow.tempRecord = recordNo;
        switch (loadOption)
        {
            case LoadOption.OverwriteChanges:
                nothing = DataRowAction.ChangeCurrentAndOriginal;
                break;

            case LoadOption.PreserveChanges:
                if (dataRow.RowState != DataRowState.Unchanged)
                {
                    nothing = DataRowAction.ChangeOriginal;
                    break;
                }
                nothing = DataRowAction.ChangeCurrentAndOriginal;
                break;

            case LoadOption.Upsert:
                switch (dataRow.RowState)
                {
                    case DataRowState.Unchanged:
                        for(DataColumn column3 : dataRow.Table.Columns)
                    {
                        if (column3.Compare(dataRow.newRecord, recordNo) != 0)
                        {
                            nothing = DataRowAction.Change;
                            break;
                        }
                    }
                    goto Label_01A4;
                }
                nothing = DataRowAction.Change;
                break;

            default:
                throw ExceptionBuilder.ArgumentOutOfRange("LoadOption");
        }
        Label_01A4:;
        try
        {
            args = this.RaiseRowChanging(null, dataRow, nothing);
            if (nothing == DataRowAction.Nothing)
            {
                dataRow.inChangingEvent = true;
                try
                {
                    args = this.OnRowChanging(args, dataRow, nothing);
                }
                finally
                {
                    dataRow.inChangingEvent = false;
                }
            }
        }
        finally
        {
            if (DataRowState.Detached == dataRow.RowState)
            {
                if (-1 != tempRecord)
                {
                    this.FreeRecord(ref tempRecord);
                }
            }
            else if (dataRow.tempRecord != recordNo)
            {
                if (-1 != tempRecord)
                {
                    this.FreeRecord(ref tempRecord);
                }
                if (-1 != recordNo)
                {
                    this.FreeRecord(ref recordNo);
                }
                recordNo = dataRow.tempRecord;
            }
            else
            {
                dataRow.tempRecord = tempRecord;
            }
        }
        if (dataRow.tempRecord != -1)
        {
            dataRow.CancelEdit();
        }
        switch (loadOption)
        {
            case LoadOption.OverwriteChanges:
                this.SetNewRecord(dataRow, recordNo, DataRowAction.Change, false, false, false);
                this.SetOldRecord(dataRow, recordNo);
                break;

            case LoadOption.PreserveChanges:
                if (dataRow.RowState != DataRowState.Unchanged)
                {
                    this.SetOldRecord(dataRow, recordNo);
                    break;
                }
                this.SetOldRecord(dataRow, recordNo);
                this.SetNewRecord(dataRow, recordNo, DataRowAction.Change, false, false, false);
                break;

            case LoadOption.Upsert:
                if (dataRow.RowState != DataRowState.Unchanged)
                {
                    if (dataRow.RowState == DataRowState.Deleted)
                    {
                        dataRow.RejectChanges();
                    }
                    this.SetNewRecord(dataRow, recordNo, DataRowAction.Change, false, false, false);
                    break;
                }
                this.SetNewRecord(dataRow, recordNo, DataRowAction.Change, false, false, false);
                if (!dataRow.HasChanges())
                {
                    this.SetOldRecord(dataRow, recordNo);
                }
                break;

            default:
                throw ExceptionBuilder.ArgumentOutOfRange("LoadOption");
        }
        if (flag)
        {
            String error = System.Data.Res.GetString("Load_ReadOnlyDataModified");
            if (dataRow.RowError.Length == 0)
            {
                dataRow.RowError = error;
            }
            else
            {
                dataRow.RowError = dataRow.RowError + " ]:[ " + error;
            }
            for(DataColumn column2 : this.Columns)
            {
                if (column2.ReadOnly && !column2.Computed)
                {
                    dataRow.SetColumnError(column2, error);
                }
            }
        }
        args = this.RaiseRowChanged(args, dataRow, nothing);
        if (nothing == DataRowAction.Nothing)
        {
            dataRow.inChangingEvent = true;
            try
            {
                this.OnRowChanged(args, dataRow, nothing);
            }
            finally
            {
                dataRow.inChangingEvent = false;
            }
        }
    }

    protected void SetDataSet(System.Data.DataSet dataSet)
    {
        if (this.dataSet != dataSet)
        {
            this.dataSet = dataSet;
            DataColumnCollection columns = this.Columns;
            for (int i = 0; i < columns.Count; i++)
            {
                columns[i].OnSetDataSet();
            }
            if (this.DataSet != null)
            {
                this.defaultView = null;
            }
            if (dataSet != null)
            {
                this._remotingFormat = dataSet.RemotingFormat;
            }
        }
    }

    protected void SetKeyValues(DataKey key,  Object[] keyValues, int record)
    {
        for (int i = 0; i < keyValues.Length; i++)
        {
            key.ColumnsReference[i][record] = keyValues[i];
        }
    }

    protected boolean SetLocaleValue(CultureInfo culture, boolean userSet, boolean resetIndexes)
    {
        if ((!userSet && !resetIndexes) && (this._cultureUserSet || this._culture.Equals(culture)))
        {
            return false;
        }
        this._culture = culture;
        this._compareInfo = null;
        this._formatProvider = null;
        this._hashCodeProvider = null;
        for(DataColumn column : this.Columns)
        {
            column._hashCode = this.GetSpecialHashCode(column.ColumnName);
        }
        if (resetIndexes)
        {
            this.ResetIndexes();
            for(Constraint constraint : this.Constraints)
            {
                constraint.CheckConstraint();
            }
        }
        return true;
    }

    private void SetMergeRecords(DataRow row, int newRecord, int oldRecord, DataRowAction action)
    {
        if (newRecord != -1)
        {
            this.SetNewRecord(row, newRecord, action, true, true, false);
            this.SetOldRecord(row, oldRecord);
        }
        else
        {
            this.SetOldRecord(row, oldRecord);
            if (row.newRecord != -1)
            {
                this.SetNewRecord(row, newRecord, action, true, true, false);
            }
        }
    }

    protected void SetNewRecord(DataRow row, int proposedRecord, DataRowAction action = 2, boolean isInMerge = false, boolean fireEvent = true, boolean suppressEnsurePropertyChanged = false)
    {
        Exception deferredException = null;
        this.SetNewRecordWorker(row, proposedRecord, action, isInMerge, suppressEnsurePropertyChanged, -1, fireEvent, out deferredException);
        if (deferredException != null)
        {
            throw deferredException;
        }
    }

    private void SetNewRecordWorker(DataRow row, int proposedRecord, DataRowAction action, boolean isInMerge, boolean suppressEnsurePropertyChanged, int position, boolean fireEvent, out Exception deferredException)
    {
        deferredException = null;
        if (row.tempRecord != proposedRecord)
        {
            if (!this.inDataLoad)
            {
                row.CheckInTable();
                this.CheckNotModifying(row);
            }
            if (proposedRecord == row.newRecord)
            {
                if (isInMerge)
                {
                    this.RaiseRowChanged(null, row, action);
                }
                return;
            }
            row.tempRecord = proposedRecord;
        }
        DataRowChangeEventArgs args = null;
        try
        {
            row._action = action;
            args = this.RaiseRowChanging(null, row, action, fireEvent);
        }
        catch
        {
            row.tempRecord = -1;
            throw;
        }
        finally
        {
            row._action = DataRowAction.Nothing;
        }
        row.tempRecord = -1;
        int newRecord = row.newRecord;
        int record = (proposedRecord != -1) ? proposedRecord : ((row.RowState != DataRowState.Unchanged) ? row.oldRecord : -1);
        if (action == DataRowAction.Add)
        {
            if (position == -1)
            {
                this.Rows.ArrayAdd(row);
            }
            else
            {
                this.Rows.ArrayInsert(row, position);
            }
        }
        List<DataRow> cachedRows = null;
        if (((action == DataRowAction.Delete) || (action == DataRowAction.Change)) && ((this.dependentColumns != null) && (this.dependentColumns.Count > 0)))
        {
            cachedRows = new List<DataRow>();
            for (int i = 0; i < this.ParentRelations.Count; i++)
            {
                DataRelation relation2 = this.ParentRelations[i];
                if (relation2.ChildTable == row.Table)
                {
                    cachedRows.InsertRange(cachedRows.Count, row.GetParentRows(relation2));
                }
            }
            for (int j = 0; j < this.ChildRelations.Count; j++)
            {
                DataRelation relation = this.ChildRelations[j];
                if (relation.ParentTable == row.Table)
                {
                    cachedRows.InsertRange(cachedRows.Count, row.GetChildRows(relation));
                }
            }
        }
        if (((!suppressEnsurePropertyChanged && !row.HasPropertyChanged) && ((row.newRecord != proposedRecord) && (-1 != proposedRecord))) && (-1 != row.newRecord))
        {
            row.LastChangedColumn = null;
            row.LastChangedColumn = null;
        }
        if (this.LiveIndexes.Count != 0)
        {
            if (((-1 == newRecord) && (-1 != proposedRecord)) && ((-1 != row.oldRecord) && (proposedRecord != row.oldRecord)))
            {
                newRecord = row.oldRecord;
            }
            DataViewRowState recordState = row.GetRecordState(newRecord);
            DataViewRowState state3 = row.GetRecordState(record);
            row.newRecord = proposedRecord;
            if (proposedRecord != -1)
            {
                this.recordManager[proposedRecord] = row;
            }
            DataViewRowState state2 = row.GetRecordState(newRecord);
            DataViewRowState state = row.GetRecordState(record);
            this.RecordStateChanged(newRecord, recordState, state2, record, state3, state);
        }
        else
        {
            row.newRecord = proposedRecord;
            if (proposedRecord != -1)
            {
                this.recordManager[proposedRecord] = row;
            }
        }
        row.ResetLastChangedColumn();
        if ((((-1 != newRecord) && (newRecord != row.oldRecord)) && ((newRecord != row.tempRecord) && (newRecord != row.newRecord))) && (row == this.recordManager[newRecord]))
        {
            this.FreeRecord(ref newRecord);
        }
        if ((row.RowState == DataRowState.Detached) && (row.rowID != -1L))
        {
            this.RemoveRow(row, false);
        }
        if ((this.dependentColumns != null) && (this.dependentColumns.Count > 0))
        {
            try
            {
                this.EvaluateExpressions(row, action, cachedRows);
            }
            catch (Exception exception2)
            {
                if (action != DataRowAction.Add)
                {
                    throw exception2;
                }
                deferredException = exception2;
            }
        }
        try
        {
            if (fireEvent)
            {
                this.RaiseRowChanged(args, row, action);
            }
        }
        catch (Exception exception)
        {
            if (!ADP.IsCatchableExceptionType(exception))
            {
                throw;
            }
            ExceptionBuilder.TraceExceptionWithoutRethrow(exception);
        }
    }

    protected void SetOldRecord(DataRow row, int proposedRecord)
    {
        if (!this.inDataLoad)
        {
            row.CheckInTable();
            this.CheckNotModifying(row);
        }
        if (proposedRecord != row.oldRecord)
        {
            int oldRecord = row.oldRecord;
            try
            {
                if (this.LiveIndexes.Count != 0)
                {
                    if (((-1 == oldRecord) && (-1 != proposedRecord)) && ((-1 != row.newRecord) && (proposedRecord != row.newRecord)))
                    {
                        oldRecord = row.newRecord;
                    }
                    DataViewRowState recordState = row.GetRecordState(oldRecord);
                    DataViewRowState state3 = row.GetRecordState(proposedRecord);
                    row.oldRecord = proposedRecord;
                    if (proposedRecord != -1)
                    {
                        this.recordManager[proposedRecord] = row;
                    }
                    DataViewRowState state2 = row.GetRecordState(oldRecord);
                    DataViewRowState state = row.GetRecordState(proposedRecord);
                    this.RecordStateChanged(oldRecord, recordState, state2, proposedRecord, state3, state);
                }
                else
                {
                    row.oldRecord = proposedRecord;
                    if (proposedRecord != -1)
                    {
                        this.recordManager[proposedRecord] = row;
                    }
                }
            }
            finally
            {
                if (((oldRecord != -1) && (oldRecord != row.tempRecord)) && ((oldRecord != row.oldRecord) && (oldRecord != row.newRecord)))
                {
                    this.FreeRecord(ref oldRecord);
                }
                if ((row.RowState == DataRowState.Detached) && (row.rowID != -1L))
                {
                    this.RemoveRow(row, false);
                }
            }
        }
    }

    private void SetShadowIndexes()
    {
        if (this.shadowIndexes == null)
        {
            this.shadowIndexes = this.LiveIndexes;
            this.shadowCount = 1;
        }
        else
        {
            this.shadowCount++;
        }
    }

    protected void ShadowIndexCopy()
    {
        if (this.shadowIndexes == this.indexes)
        {
            this.shadowIndexes = new List<Index>(this.indexes);
        }
    }

    protected boolean ShouldSerializeCaseSensitive()
    {
        return this._caseSensitiveUserSet;
    }

    protected boolean ShouldSerializeLocale()
    {
        return this._cultureUserSet;
    }

    private boolean ShouldSerializeNamespace()
    {
        return (this.tableNamespace != null);
    }

    private boolean ShouldSerializePrimaryKey()
    {
        return (this.primaryKey != null);
    }

    protected void SilentlySetValue(DataRow dr, DataColumn dc, DataRowVersion version,  Object newValue)
    {
        int recordFromVersion = dr.GetRecordFromVersion(version);
        boolean flag = false;
        if (DataStorage.IsTypeCustomType(dc.DataType) && (newValue != dc[recordFromVersion]))
        {
            flag = false;
        }
        else
        {
            flag = dc.CompareValueTo(recordFromVersion, newValue, true);
        }
        if (!flag)
        {
            int[] oldIndex = dr.Table.RemoveRecordFromIndexes(dr, version);
            dc.SetValue(recordFromVersion, newValue);
            int[] newIndex = dr.Table.InsertRecordToIndexes(dr, version);
            if (dr.HasVersion(version))
            {
                if (version != DataRowVersion.Original)
                {
                    dr.Table.RecordChanged(oldIndex, newIndex);
                }
                if (dc.dependentColumns != null)
                {
                    dc.Table.EvaluateDependentExpressions(dc.dependentColumns, dr, version, null);
                }
            }
        }
        dr.ResetLastChangedColumn();
    }

    protected void SuspendIndexEvents()
    {
        Bid.Trace("<ds.DataTable.SuspendIndexEvents|Info> %d#, %d\n", this.ObjectID, this._suspendIndexEvents);
        this._suspendIndexEvents++;
    }

    private IList IListSource.GetList()
    {
        return this.DefaultView;
    }

    private XmlSchema IXmlSerializable.GetSchema()
    {
        return this.GetSchema();
    }

    private void IXmlSerializable.ReadXml(XmlReader reader)
    {
        IXmlTextParser parser = reader as IXmlTextParser;
        boolean normalized = true;
        if (parser != null)
        {
            normalized = parser.Normalized;
            parser.Normalized = false;
        }
        this.ReadXmlSerializable(reader);
        if (parser != null)
        {
            parser.Normalized = normalized;
        }
    }

    private void IXmlSerializable.WriteXml(XmlWriter writer)
    {
        this.WriteXmlSchema(writer, false);
        this.WriteXml(writer, XmlWriteMode.DiffGram, false);
    }

    public override String ToString()
    {
        if (this.displayExpression == null)
        {
            return this.TableName;
        }
        return (this.TableName + " + " + this.DisplayExpressionInternal);
    }

    protected void UpdatePropertyDescriptorCollectionCache()
    {
        this.propertyDescriptorCollectionCache = null;
    }

    protected DataRow UpdatingAdd( Object[] values)
    {
        Index sortIndex = null;
        if (this.primaryKey != null)
        {
            sortIndex = this.primaryKey.Key.GetSortIndex(DataViewRowState.OriginalRows);
        }
        if (sortIndex == null)
        {
            return this.Rows.Add(values);
        }
        int record = this.NewRecordFromArray(values);
        int recordIndex = sortIndex.FindRecord(record);
        if (recordIndex != -1)
        {
            int num3 = sortIndex.GetRecord(recordIndex);
            DataRow row = this.recordManager[num3];
            row.RejectChanges();
            this.SetNewRecord(row, record, DataRowAction.Change, false, true, false);
            return row;
        }
        DataRow row2 = this.NewRow(record);
        this.Rows.Add(row2);
        return row2;
    }

    protected boolean UpdatingCurrent(DataRow row, DataRowAction action)
    {
        if (((action != DataRowAction.Add) && (action != DataRowAction.Change)) && ((action != DataRowAction.Rollback) && (action != DataRowAction.ChangeOriginal)))
        {
            return (action == DataRowAction.ChangeCurrentAndOriginal);
        }
        return true;
    }

    public void WriteXml(Stream stream)
    {
        this.WriteXml(stream, XmlWriteMode.IgnoreSchema, false);
    }

    public void WriteXml(TextWriter writer)
    {
        this.WriteXml(writer, XmlWriteMode.IgnoreSchema, false);
    }

    public void WriteXml(String fileName)
    {
        this.WriteXml(fileName, XmlWriteMode.IgnoreSchema, false);
    }

    public void WriteXml(XmlWriter writer)
    {
        this.WriteXml(writer, XmlWriteMode.IgnoreSchema, false);
    }

    public void WriteXml(Stream stream, boolean writeHierarchy)
    {
        this.WriteXml(stream, XmlWriteMode.IgnoreSchema, writeHierarchy);
    }

    public void WriteXml(Stream stream, XmlWriteMode mode)
    {
        this.WriteXml(stream, mode, false);
    }

    public void WriteXml(TextWriter writer, boolean writeHierarchy)
    {
        this.WriteXml(writer, XmlWriteMode.IgnoreSchema, writeHierarchy);
    }

    public void WriteXml(TextWriter writer, XmlWriteMode mode)
    {
        this.WriteXml(writer, mode, false);
    }

    public void WriteXml(String fileName, boolean writeHierarchy)
    {
        this.WriteXml(fileName, XmlWriteMode.IgnoreSchema, writeHierarchy);
    }

    public void WriteXml(String fileName, XmlWriteMode mode)
    {
        this.WriteXml(fileName, mode, false);
    }

    public void WriteXml(XmlWriter writer, boolean writeHierarchy)
    {
        this.WriteXml(writer, XmlWriteMode.IgnoreSchema, writeHierarchy);
    }

    public void WriteXml(XmlWriter writer, XmlWriteMode mode)
    {
        this.WriteXml(writer, mode, false);
    }

    public void WriteXml(Stream stream, XmlWriteMode mode, boolean writeHierarchy)
    {
        if (stream != null)
        {
            XmlTextWriter writer = new XmlTextWriter(stream, null) {
                Formatting = Formatting.Indented
            };
            this.WriteXml(writer, mode, writeHierarchy);
        }
    }

    public void WriteXml(TextWriter writer, XmlWriteMode mode, boolean writeHierarchy)
    {
        if (writer != null)
        {
            XmlTextWriter writer2 = new XmlTextWriter(writer) {
                Formatting = Formatting.Indented
            };
            this.WriteXml(writer2, mode, writeHierarchy);
        }
    }

    public void WriteXml(String fileName, XmlWriteMode mode, boolean writeHierarchy)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.WriteXml|API> %d#, fileName='%ls', mode=%d{ds.XmlWriteMode}\n", this.ObjectID, fileName, (int) mode);
        try
        {
            using (XmlTextWriter writer = new XmlTextWriter(fileName, null))
            {
                writer.Formatting = Formatting.Indented;
                writer.WriteStartDocument(true);
                this.WriteXml(writer, mode, writeHierarchy);
                writer.WriteEndDocument();
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    public void WriteXml(XmlWriter writer, XmlWriteMode mode, boolean writeHierarchy)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.WriteXml|API> %d#, mode=%d{ds.XmlWriteMode}\n", this.ObjectID, (int) mode);
        try
        {
            if (this.tableName.Length == 0)
            {
                throw ExceptionBuilder.CanNotSerializeDataTableWithEmptyName();
            }
            if (writer != null)
            {
                if (mode == XmlWriteMode.DiffGram)
                {
                    new NewDiffgramGen(this, writeHierarchy).Save(writer, this);
                }
                else if (mode == XmlWriteMode.WriteSchema)
                {
                    System.Data.DataSet set = null;
                    String tableNamespace = this.tableNamespace;
                    if (this.DataSet == null)
                    {
                        set = new System.Data.DataSet();
                        set.SetLocaleValue(this._culture, this._cultureUserSet);
                        set.CaseSensitive = this.CaseSensitive;
                        set.Namespace = this.Namespace;
                        set.RemotingFormat = this.RemotingFormat;
                        set.Tables.Add(this);
                    }
                    if (writer != null)
                    {
                        new XmlDataTreeWriter(this, writeHierarchy).Save(writer, true);
                    }
                    if (set != null)
                    {
                        set.Tables.Remove(this);
                        this.tableNamespace = tableNamespace;
                    }
                }
                else
                {
                    new XmlDataTreeWriter(this, writeHierarchy).Save(writer, false);
                }
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    public void WriteXmlSchema(Stream stream)
    {
        this.WriteXmlSchema(stream, false);
    }

    public void WriteXmlSchema(TextWriter writer)
    {
        this.WriteXmlSchema(writer, false);
    }

    public void WriteXmlSchema(String fileName)
    {
        this.WriteXmlSchema(fileName, false);
    }

    public void WriteXmlSchema(XmlWriter writer)
    {
        this.WriteXmlSchema(writer, false);
    }

    public void WriteXmlSchema(Stream stream, boolean writeHierarchy)
    {
        if (stream != null)
        {
            XmlTextWriter writer = new XmlTextWriter(stream, null) {
                Formatting = Formatting.Indented
            };
            this.WriteXmlSchema(writer, writeHierarchy);
        }
    }

    public void WriteXmlSchema(TextWriter writer, boolean writeHierarchy)
    {
        if (writer != null)
        {
            XmlTextWriter writer2 = new XmlTextWriter(writer) {
                Formatting = Formatting.Indented
            };
            this.WriteXmlSchema(writer2, writeHierarchy);
        }
    }

    public void WriteXmlSchema(String fileName, boolean writeHierarchy)
    {
        XmlTextWriter writer = new XmlTextWriter(fileName, null);
        try
        {
            writer.Formatting = Formatting.Indented;
            writer.WriteStartDocument(true);
            this.WriteXmlSchema(writer, writeHierarchy);
            writer.WriteEndDocument();
        }
        finally
        {
            writer.Close();
        }
    }

    public void WriteXmlSchema(XmlWriter writer, boolean writeHierarchy)
    {
        IntPtr ptr;
        Bid.ScopeEnter(out ptr, "<ds.DataTable.WriteXmlSchema|API> %d#\n", this.ObjectID);
        try
        {
            if (this.tableName.Length == 0)
            {
                throw ExceptionBuilder.CanNotSerializeDataTableWithEmptyName();
            }
            if (!this.CheckForClosureOnExpressions(this, writeHierarchy))
            {
                throw ExceptionBuilder.CanNotSerializeDataTableHierarchy();
            }
            System.Data.DataSet set = null;
            String tableNamespace = this.tableNamespace;
            if (this.DataSet == null)
            {
                set = new System.Data.DataSet();
                set.SetLocaleValue(this._culture, this._cultureUserSet);
                set.CaseSensitive = this.CaseSensitive;
                set.Namespace = this.Namespace;
                set.RemotingFormat = this.RemotingFormat;
                set.Tables.Add(this);
            }
            if (writer != null)
            {
                new XmlTreeGen(SchemaFormat.Public).Save(null, this, writer, writeHierarchy);
            }
            if (set != null)
            {
                set.Tables.Remove(this);
                this.tableNamespace = tableNamespace;
            }
        }
        finally
        {
            Bid.ScopeLeave(ref ptr);
        }
    }

    protected boolean AreIndexEventsSuspended
    {
        get
        {
            return (0 < this._suspendIndexEvents);
        }
    }

    [System.Data.ResDescription("DataTableCaseSensitiveDescr")]
    public boolean CaseSensitive
    {
        get
        {
            return this._caseSensitive;
        }
        set
        {
            if (this._caseSensitive != value)
            {
                boolean flag2 = this._caseSensitive;
                boolean flag = this._caseSensitiveUserSet;
                this._caseSensitive = value;
                this._caseSensitiveUserSet = true;
                if ((this.DataSet != null) && !this.DataSet.ValidateCaseConstraint())
                {
                    this._caseSensitive = flag2;
                    this._caseSensitiveUserSet = flag;
                    throw ExceptionBuilder.CannotChangeCaseLocale();
                }
                this.SetCaseSensitiveValue(value, true, true);
            }
            this._caseSensitiveUserSet = true;
        }
    }

    [System.Data.ResDescription("DataTableChildRelationsDescr"), Browsable(false), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
    public DataRelationCollection ChildRelations
    {
        get
        {
            if (this.childRelationsCollection == null)
            {
                this.childRelationsCollection = new DataRelationCollection.DataTableRelationCollection(this, false);
            }
            return this.childRelationsCollection;
        }
    }

    [System.Data.ResDescription("DataTableColumnsDescr"), System.Data.ResCategory("DataCategory_Data"), DesignerSerializationVisibility(DesignerSerializationVisibility.Content)]
    public DataColumnCollection Columns
    {
        get
        {
            return this.columnCollection;
        }
    }

    private System.Globalization.CompareInfo CompareInfo
    {
        get
        {
            if (this._compareInfo == null)
            {
                this._compareInfo = this.Locale.CompareInfo;
            }
            return this._compareInfo;
        }
    }

    [DesignerSerializationVisibility(DesignerSerializationVisibility.Content), System.Data.ResDescription("DataTableConstraintsDescr"), System.Data.ResCategory("DataCategory_Data")]
    public ConstraintCollection Constraints
    {
        get
        {
            return this.constraintCollection;
        }
    }

    [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden), System.Data.ResDescription("DataTableDataSetDescr"), Browsable(false)]
    public System.Data.DataSet DataSet
    {
        get
        {
            return this.dataSet;
        }
    }

    [System.Data.ResDescription("DataTableDefaultViewDescr"), Browsable(false)]
    public DataView DefaultView
    {
        get
        {
            DataView defaultView = this.defaultView;
            if (defaultView == null)
            {
                if (this.dataSet != null)
                {
                    defaultView = this.dataSet.DefaultViewManager.CreateDataView(this);
                }
                else
                {
                    defaultView = new DataView(this, true);
                    defaultView.SetIndex2("", DataViewRowState.CurrentRows, null, true);
                }
                defaultView = Interlocked.CompareExchange<DataView>(ref this.defaultView, defaultView, null);
                if (defaultView == null)
                {
                    defaultView = this.defaultView;
                }
            }
            return defaultView;
        }
    }

    [System.Data.ResCategory("DataCategory_Data"), System.Data.ResDescription("DataTableDisplayExpressionDescr"), DefaultValue("")]
    public String DisplayExpression
    {
        get
        {
            return this.DisplayExpressionInternal;
        }
        set
        {
            if ((value != null) && (value.Length > 0))
            {
                this.displayExpression = new DataExpression(this, value);
            }
            else
            {
                this.displayExpression = null;
            }
        }
    }

    protected String DisplayExpressionInternal
    {
        get
        {
            if (this.displayExpression == null)
            {
                return "";
            }
            return this.displayExpression.Expression;
        }
    }

    protected int ElementColumnCount
    {
        get
        {
            return this.elementColumnCount;
        }
        set
        {
            if ((value > 0) && (this.xmlText != null))
            {
                throw ExceptionBuilder.TableCannotAddToSimpleContent();
            }
            this.elementColumnCount = value;
        }
    }

    protected String EncodedTableName
    {
        get
        {
            String encodedTableName = this.encodedTableName;
            if (encodedTableName == null)
            {
                encodedTableName = XmlConvert.EncodeLocalName(this.TableName);
                this.encodedTableName = encodedTableName;
            }
            return encodedTableName;
        }
    }

    protected boolean EnforceConstraints
    {
        get
        {
            if (this.SuspendEnforceConstraints)
            {
                return false;
            }
            if (this.dataSet != null)
            {
                return this.dataSet.EnforceConstraints;
            }
            return this.enforceConstraints;
        }
        set
        {
            if ((this.dataSet == null) && (this.enforceConstraints != value))
            {
                if (value)
                {
                    this.EnableConstraints();
                }
                this.enforceConstraints = value;
            }
        }
    }

    [System.Data.ResCategory("DataCategory_Data"), Browsable(false), System.Data.ResDescription("ExtendedPropertiesDescr")]
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

    protected IFormatProvider FormatProvider
    {
        get
        {
            if (this._formatProvider == null)
            {
                CultureInfo locale = this.Locale;
                if (locale.IsNeutralCulture)
                {
                    locale = CultureInfo.InvariantCulture;
                }
                this._formatProvider = locale;
            }
            return this._formatProvider;
        }
    }

    [Browsable(false), System.Data.ResDescription("DataTableHasErrorsDescr")]
    public boolean HasErrors
    {
        get
        {
            for (int i = 0; i < this.Rows.Count; i++)
            {
                if (this.Rows[i].HasErrors)
                {
                    return true;
                }
            }
            return false;
        }
    }

    [Browsable(false)]
    public boolean IsInitialized
    {
        get
        {
            return !this.fInitInProgress;
        }
    }

    private boolean IsTypedDataTable
    {
        get
        {
            switch (this._isTypedDataTable)
            {
                case 0:
                    this._isTypedDataTable = (base.GetType() != typeof(DataTable)) ? ((byte) 1) : ((byte) 2);
                    return (1 == this._isTypedDataTable);

                case 1:
                    return true;
            }
            return false;
        }
    }

    [DebuggerBrowsable(DebuggerBrowsableState.Never)]
    protected List<Index> LiveIndexes
    {
        get
        {
            if (!this.AreIndexEventsSuspended)
            {
                for (int i = this.indexes.Count - 1; 0 <= i; i--)
                {
                    Index index = this.indexes[i];
                    if (index.RefCount <= 1)
                    {
                        index.RemoveRef();
                    }
                }
            }
            return this.indexes;
        }
    }

    [System.Data.ResDescription("DataTableLocaleDescr")]
    public CultureInfo Locale
    {
        get
        {
            return this._culture;
        }
        set
        {
            IntPtr ptr;
            Bid.ScopeEnter(out ptr, "<ds.DataTable.set_Locale|API> %d#\n", this.ObjectID);
            try
            {
                boolean flag3 = true;
                if (value == null)
                {
                    flag3 = false;
                    value = (this.dataSet != null) ? this.dataSet.Locale : this._culture;
                }
                if ((this._culture != value) && !this._culture.Equals(value))
                {
                    boolean flag = false;
                    boolean flag2 = false;
                    CultureInfo culture = this._culture;
                    boolean flag4 = this._cultureUserSet;
                    try
                    {
                        this._cultureUserSet = true;
                        this.SetLocaleValue(value, true, false);
                        if ((this.DataSet == null) || this.DataSet.ValidateLocaleConstraint())
                        {
                            flag = false;
                            this.SetLocaleValue(value, true, true);
                            flag = true;
                        }
                    }
                    catch
                    {
                        flag2 = true;
                        throw;
                    }
                    finally
                    {
                        if (!flag)
                        {
                            try
                            {
                                this.SetLocaleValue(culture, true, true);
                            }
                            catch (Exception exception)
                            {
                                if (!ADP.IsCatchableExceptionType(exception))
                                {
                                    throw;
                                }
                                ADP.TraceExceptionWithoutRethrow(exception);
                            }
                            this._cultureUserSet = flag4;
                            if (!flag2)
                            {
                                throw ExceptionBuilder.CannotChangeCaseLocale(null);
                            }
                        }
                    }
                    this.SetLocaleValue(value, true, true);
                }
                this._cultureUserSet = flag3;
            }
            finally
            {
                Bid.ScopeLeave(ref ptr);
            }
        }
    }

    protected BigInteger MaxOccurs
    {
        get
        {
            return this.maxOccurs;
        }
        set
        {
            this.maxOccurs = value;
        }
    }

    protected boolean MergingData
    {
        get
        {
            return this.mergingData;
        }
        set
        {
            this.mergingData = value;
        }
    }

    [DefaultValue(50), System.Data.ResCategory("DataCategory_Data"), System.Data.ResDescription("DataTableMinimumCapacityDescr")]
    public int MinimumCapacity
    {
        get
        {
            return this.recordManager.MinimumCapacity;
        }
        set
        {
            if (value != this.recordManager.MinimumCapacity)
            {
                this.recordManager.MinimumCapacity = value;
            }
        }
    }

    protected BigInteger MinOccurs
    {
        get
        {
            return this.minOccurs;
        }
        set
        {
            this.minOccurs = value;
        }
    }

    [System.Data.ResDescription("DataTableNamespaceDescr"), System.Data.ResCategory("DataCategory_Data")]
    public String Namespace
    {
        get
        {
            if (this.tableNamespace == null)
            {
                return this.GetInheritedNamespace(new List<DataTable>());
            }
            return this.tableNamespace;
        }
        set
        {
            IntPtr ptr;
            Bid.ScopeEnter(out ptr, "<ds.DataTable.set_Namespace|API> %d#, value='%ls'\n", this.ObjectID, value);
            try
            {
                if (value != this.tableNamespace)
                {
                    if (this.dataSet != null)
                    {
                        String tableNamespace = (value == null) ? this.GetInheritedNamespace(new List<DataTable>()) : value;
                        if (tableNamespace != this.Namespace)
                        {
                            if (this.dataSet.Tables.Contains(this.TableName, tableNamespace, true, true))
                            {
                                throw ExceptionBuilder.DuplicateTableName2(this.TableName, tableNamespace);
                            }
                            this.CheckCascadingNamespaceConflict(tableNamespace);
                        }
                    }
                    this.CheckNamespaceValidityForNestedRelations(value);
                    this.DoRaiseNamespaceChange();
                }
                this.tableNamespace = value;
            }
            finally
            {
                Bid.ScopeLeave(ref ptr);
            }
        }
    }

    protected boolean NeedColumnChangeEvents
    {
        get
        {
            if (!this.IsTypedDataTable && (this.onColumnChangingDelegate == null))
            {
                return (null != this.onColumnChangedDelegate);
            }
            return true;
        }
    }

    protected DataRelation[] NestedParentRelations
    {
        get
        {
            return this._nestedParentRelations;
        }
    }

    protected int NestedParentsCount
    {
        get
        {
            int num = 0;
            for(DataRelation relation : this.ParentRelations)
            {
                if (relation.Nested)
                {
                    num++;
                }
            }
            return num;
        }
    }

    protected int ObjectID
    {
        get
        {
            return this._objectID;
        }
    }

    [System.Data.ResDescription("DataTableParentRelationsDescr"), Browsable(false), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
    public DataRelationCollection ParentRelations
    {
        get
        {
            if (this.parentRelationsCollection == null)
            {
                this.parentRelationsCollection = new DataRelationCollection.DataTableRelationCollection(this, true);
            }
            return this.parentRelationsCollection;
        }
    }

    [System.Data.ResDescription("DataTablePrefixDescr"), System.Data.ResCategory("DataCategory_Data"), DefaultValue("")]
    public String Prefix
    {
        get
        {
            return this.tablePrefix;
        }
        set
        {
            if (value == null)
            {
                value = "";
            }
            Bid.Trace("<ds.DataTable.set_Prefix|API> %d#, value='%ls'\n", this.ObjectID, value);
            if ((XmlConvert.DecodeName(value) == value) && (XmlConvert.EncodeName(value) != value))
            {
                throw ExceptionBuilder.InvalidPrefix(value);
            }
            this.tablePrefix = value;
        }
    }

    [TypeConverter(typeof(PrimaryKeyTypeConverter)), System.Data.ResCategory("DataCategory_Data"), Editor("Microsoft.VSDesigner.Data.Design.PrimaryKeyEditor, Microsoft.VSDesigner, Version=10.0.0.0, Culture=neutral, PublicKeyToken=b03f5f7f11d50a3a", "System.Drawing.Design.UITypeEditor, System.Drawing, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b03f5f7f11d50a3a"), System.Data.ResDescription("DataTablePrimaryKeyDescr")]
    public DataColumn[] PrimaryKey
    {
        get
        {
            UniqueConstraint primaryKey = this.primaryKey;
            if (primaryKey != null)
            {
                return primaryKey.Key.ToArray();
            }
            return zeroColumns;
        }
        set
        {
            UniqueConstraint constraint = null;
            UniqueConstraint constraint2 = null;
            if (this.fInitInProgress && (value != null))
            {
                this.delayedSetPrimaryKey = value;
            }
            else
            {
                if ((value != null) && (value.Length != 0))
                {
                    int num = 0;
                    for (int i = 0; i < value.Length; i++)
                    {
                        if (value[i] == null)
                        {
                            break;
                        }
                        num++;
                    }
                    if (num != 0)
                    {
                        DataColumn[] columns = value;
                        if (num != value.Length)
                        {
                            columns = new DataColumn[num];
                            for (int j = 0; j < num; j++)
                            {
                                columns[j] = value[j];
                            }
                        }
                        constraint = new UniqueConstraint(columns);
                        if (constraint.Table != this)
                        {
                            throw ExceptionBuilder.TableForeignPrimaryKey();
                        }
                    }
                }
                if ((constraint != this.primaryKey) && ((constraint == null) || !constraint.Equals(this.primaryKey)))
                {
                    constraint2 = (UniqueConstraint) this.Constraints.FindConstraint(constraint);
                    if (constraint2 != null)
                    {
                        constraint.ColumnsReference.CopyTo(constraint2.Key.ColumnsReference, 0);
                        constraint = constraint2;
                    }
                    UniqueConstraint primaryKey = this.primaryKey;
                    this.primaryKey = null;
                    if (primaryKey != null)
                    {
                        primaryKey.ConstraintIndex.RemoveRef();
                        if (this.loadIndex != null)
                        {
                            this.loadIndex.RemoveRef();
                            this.loadIndex = null;
                        }
                        if (this.loadIndexwithOriginalAdded != null)
                        {
                            this.loadIndexwithOriginalAdded.RemoveRef();
                            this.loadIndexwithOriginalAdded = null;
                        }
                        if (this.loadIndexwithCurrentDeleted != null)
                        {
                            this.loadIndexwithCurrentDeleted.RemoveRef();
                            this.loadIndexwithCurrentDeleted = null;
                        }
                        this.Constraints.Remove(primaryKey);
                    }
                    if ((constraint != null) && (constraint2 == null))
                    {
                        this.Constraints.Add(constraint);
                    }
                    this.primaryKey = constraint;
                    this._primaryIndex = (constraint != null) ? constraint.Key.GetIndexDesc() : zeroIndexField;
                    if (this.primaryKey != null)
                    {
                        constraint.ConstraintIndex.AddRef();
                        for (int k = 0; k < constraint.ColumnsReference.Length; k++)
                        {
                            constraint.ColumnsReference[k].AllowDBNull = false;
                        }
                    }
                }
            }
        }
    }

    protected int RecordCapacity
    {
        get
        {
            return this.recordManager.RecordCapacity;
        }
    }

    [DefaultValue(0)]
    public SerializationFormat RemotingFormat
    {
        get
        {
            return this._remotingFormat;
        }
        set
        {
            if ((value != SerializationFormat.Binary) && (value != SerializationFormat.Xml))
            {
                throw ExceptionBuilder.InvalidRemotingFormat(value);
            }
            if ((this.DataSet != null) && (value != this.DataSet.RemotingFormat))
            {
                throw ExceptionBuilder.CanNotSetRemotingFormat();
            }
            this._remotingFormat = value;
        }
    }

    protected Hashtable RowDiffId
    {
        get
        {
            if (this.rowDiffId == null)
            {
                this.rowDiffId = new Hashtable();
            }
            return this.rowDiffId;
        }
    }

    [System.Data.ResDescription("DataTableRowsDescr"), Browsable(false)]
    public DataRowCollection Rows
    {
        get
        {
            return this.rowCollection;
        }
    }

    protected boolean SchemaLoading
    {
        get
        {
            return this.schemaLoading;
        }
    }

    protected boolean SelfNested
    {
        get
        {
            for(DataRelation relation : this.ParentRelations)
            {
                if (relation.Nested && (relation.ParentTable == this))
                {
                    return true;
                }
            }
            return false;
        }
    }

    [Browsable(false), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
    public override ISite Site
    {
        get
        {
            return base.Site;
        }
        set
        {
            ISite site = this.Site;
            if ((value == null) && (site != null))
            {
                IContainer container = site.Container;
                if (container != null)
                {
                    for (int i = 0; i < this.Columns.Count; i++)
                    {
                        if (this.Columns[i].Site != null)
                        {
                            container.Remove(this.Columns[i]);
                        }
                    }
                }
            }
            base.Site = value;
        }
    }

    protected boolean SuspendEnforceConstraints
    {
        get
        {
            return this._suspendEnforceConstraints;
        }
        set
        {
            this._suspendEnforceConstraints = value;
        }
    }

    boolean IListSource.ContainsListCollection
    {
        get
        {
            return false;
        }
    }

    [System.Data.ResDescription("DataTableTableNameDescr"), System.Data.ResCategory("DataCategory_Data"), RefreshProperties(RefreshProperties.All), DefaultValue("")]
    public String TableName
    {
        get
        {
            return this.tableName;
        }
        set
        {
            IntPtr ptr;
            Bid.ScopeEnter(out ptr, "<ds.DataTable.set_TableName|API> %d#, value='%ls'\n", this.ObjectID, value);
            try
            {
                if (value == null)
                {
                    value = "";
                }
                CultureInfo locale = this.Locale;
                if (String.Compare(this.tableName, value, true, locale) != 0)
                {
                    if (this.dataSet != null)
                    {
                        if (value.Length == 0)
                        {
                            throw ExceptionBuilder.NoTableName();
                        }
                        if ((String.Compare(value, this.dataSet.DataSetName, true, this.dataSet.Locale) == 0) && !this.fNestedInDataset)
                        {
                            throw ExceptionBuilder.DatasetConflictingName(this.dataSet.DataSetName);
                        }
                        DataRelation[] nestedParentRelations = this.NestedParentRelations;
                        if (nestedParentRelations.Length == 0)
                        {
                            this.dataSet.Tables.RegisterName(value, this.Namespace);
                        }
                        else
                        {
                            for(DataRelation relation2 : nestedParentRelations)
                            {
                                if (!relation2.ParentTable.Columns.CanRegisterName(value))
                                {
                                    throw ExceptionBuilder.CannotAddDuplicate2(value);
                                }
                            }
                            this.dataSet.Tables.RegisterName(value, this.Namespace);
                            for(DataRelation relation : nestedParentRelations)
                            {
                                relation.ParentTable.Columns.RegisterColumnName(value, null);
                                relation.ParentTable.Columns.UnregisterName(this.TableName);
                            }
                        }
                        if (this.tableName.Length != 0)
                        {
                            this.dataSet.Tables.UnregisterName(this.tableName);
                        }
                    }
                    this.RaisePropertyChanging("TableName");
                    this.tableName = value;
                    this.encodedTableName = null;
                }
                else if (String.Compare(this.tableName, value, false, locale) != 0)
                {
                    this.RaisePropertyChanging("TableName");
                    this.tableName = value;
                    this.encodedTableName = null;
                }
            }
            finally
            {
                Bid.ScopeLeave(ref ptr);
            }
        }
    }

    protected XmlQualifiedName TypeName
    {
        get
        {
            if (this.typeName != null)
            {
                return (XmlQualifiedName) this.typeName;
            }
            return XmlQualifiedName.Empty;
        }
        set
        {
            this.typeName = value;
        }
    }

    protected int UKColumnPositionForInference
    {
        get
        {
            return this.ukColumnPositionForInference;
        }
        set
        {
            this.ukColumnPositionForInference = value;
        }
    }

    protected DataColumn XmlText
    {
        get
        {
            return this.xmlText;
        }
        set
        {
            if (this.xmlText != value)
            {
                if (this.xmlText != null)
                {
                    if (value != null)
                    {
                        throw ExceptionBuilder.MultipleTextOnlyColumns();
                    }
                    this.Columns.Remove(this.xmlText);
                }
                else if (value != this.Columns[value.ColumnName])
                {
                    this.Columns.Add(value);
                }
                this.xmlText = value;
            }
        }
    }

    [StructLayout(LayoutKind.Sequential)]
    protected final class DSRowDiffIdUsageSection //This was a struct
    {
        private DataSet _targetDS;
        protected void Prepare(DataSet ds)
        {
            this._targetDS = ds;
            for (int i = 0; i < ds.Tables.Count; i++)
            {
                DataTable table = ds.Tables[i];
                table.rowDiffId = null;
            }
        }

        [Conditional("DEBUG")]
        protected void Cleanup()
        {
            if (this._targetDS != null)
            {
                for (int i = 0; i < this._targetDS.Tables.Count; i++)
                {
                    DataTable table = this._targetDS.Tables[i];
                    table.rowDiffId = null;
                }
            }
        }
    }

    [StructLayout(LayoutKind.Sequential)]
    protected final class RowDiffIdUsageSection //This was a struct
    {
        private DataTable _targetTable;
        protected void Prepare(DataTable table)
        {
            this._targetTable = table;
            table.rowDiffId = null;
        }

        [Conditional("DEBUG")]
        protected void Cleanup()
        {
            if (this._targetTable != null)
            {
                this._targetTable.rowDiffId = null;
            }
        }

        [Conditional("DEBUG")]
        protected static void Assert(String message)
        {
        }
    }
}

