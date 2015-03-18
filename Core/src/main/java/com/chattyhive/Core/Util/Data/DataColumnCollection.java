package com.chattyhive.Core.Util.Data;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by Jonathan on 18/03/2015.
 */
    public final class DataColumnCollection extends InternalDataCollectionBase<DataColumn> {
		
        private final ArrayList<DataColumn> _list = new ArrayList<DataColumn>();
        private final TreeMap<String, DataColumn> columnFromName;

        private int defaultNameIndex = 1;
        private DataColumn[] delayedAddRangeColumns;
        private boolean fInClear;
        private final DataTable table;

        protected DataColumnCollection(DataTable table) {
            this.table = table;
            this.columnFromName = new TreeMap<String, DataColumn>();
        }

        public DataColumn Add() {
            DataColumn column = new DataColumn();
            this.Add(column);
            return column;
        }

        public void Add(DataColumn column) {
            this.AddAt(-1, column);
        }

        public DataColumn Add(String columnName) {
            DataColumn column = new DataColumn(columnName);
            this.Add(column);
            return column;
        }

        public DataColumn Add(String columnName, Type type) {
            DataColumn column = new DataColumn(columnName, type);
            this.Add(column);
            return column;
        }

        void AddAt(int index, DataColumn column) {
            if ((column != null) && (column.ColumnMapping == MappingType.SimpleContent)) {
                if (this.table.ElementColumnCount > 0) {
                    throw new UnsupportedOperationException("Can not add more columns to table");
                }
                this.BaseAdd(column);
                if (index != -1) {
                    this.ArrayAdd(index, column);
                } else {
                    this.ArrayAdd(column);
                }
            } else {
                this.BaseAdd(column);
                if (index != -1) {
                    this.ArrayAdd(index, column);
                } else {
                    this.ArrayAdd(column);
                }
                if (column.ColumnMapping == MappingType.Element) {
                    this.table.ElementColumnCount++;
                }
            }
        }

        public void AddRange(DataColumn[] columns) {
            if (this.table.fInitInProgress) {
                this.delayedAddRangeColumns = columns;
            } else if (columns != null) {
                for (DataColumn column : columns) {
                    if (column != null) {
                        this.Add(column);
                    }
                }
            }
        }

        private void ArrayAdd(DataColumn column) {
            this._list.add(column);
            column.SetOrdinalInternal(this._list.size() - 1);
        }

        private void ArrayAdd(int index, DataColumn column) {
            this._list.add(index, column);
        }

        private void ArrayRemove(DataColumn column) {
            column.SetOrdinalInternal(-1);
            this._list.remove(column);
            int count = this._list.size();
            for (int i = 0; i < count; i++) {
                this._list.get(i).SetOrdinalInternal(i);
            }
        }
		
        String AssignName() {
            String key = this.MakeName(this.defaultNameIndex++);
            while (this.columnFromName.containsKey(key)) {
                key = this.MakeName(this.defaultNameIndex++);
            }
            return key;
        }

        private void BaseAdd(DataColumn column) {
            if (column == null) {
                throw new IllegalArgumentException("Column can not be null");
            }
            if (column.table == this.table) {
                throw new IllegalArgumentException("Column is already in table");
            }
            if (column.table != null) {
                throw new IllegalArgumentException("Column belongs to another table");
            }
            if (column.ColumnName.length() == 0) {
                column.ColumnName = this.AssignName();
            }
            this.RegisterColumnName(column.ColumnName, column);
            try {
                column.SetTable(this.table);

                if (0 < this.table.RecordCapacity) {
                    column.SetCapacity(this.table.RecordCapacity);
                }
                for (int i = 0; i < this.table.RecordCapacity; i++) {
                    column.InitializeRecord(i);
                }
            } catch (Exception exception) {
				this.UnregisterName(column.ColumnName);
                //throw exception;
            }
        }

        private void BaseGroupSwitch(DataColumn[] oldArray, int oldLength, DataColumn[] newArray, int newLength) {
            int num4 = 0;
            for (int i = 0; i < oldLength; i++) {
                boolean flag = false;
                for (int k = num4; k < newLength; k++) {
                    if (oldArray[i] == newArray[k]) {
                        if (num4 == k) {
                            num4++;
                        }
                        flag = true;
                        break;
                    }
                }
                if (!flag && (oldArray[i].Table == this.table)) {
                    this.BaseRemove(oldArray[i]);
                    this._list.remove(oldArray[i]);
                    oldArray[i].SetOrdinalInternal(-1);
                }
            }
            for (int j = 0; j < newLength; j++) {
                if (newArray[j].Table != this.table) {
                    this.BaseAdd(newArray[j]);
                    this._list.add(newArray[j]);
                }
                newArray[j].SetOrdinalInternal(j);
            }
        }

        private void BaseRemove(DataColumn column) {
            if (this.CanRemove(column, true)) {
                if (column.errors > 0) {
                    for (int i = 0; i < this.table.Rows().size(); i++) {
                        this.table.Rows(i).ClearError(column);
                    }
                }
                this.UnregisterName(column.ColumnName);
                column.SetTable(null);
            }
        }

        boolean CanRegisterName(String name) {
            return !this.columnFromName.containsKey(name);
        }

        public boolean CanRemove(DataColumn column) {
            return this.CanRemove(column, false);
        }

        boolean CanRemove(DataColumn column, boolean fThrowException) {
            if (column == null) {
                if (fThrowException) {
                    throw new IllegalArgumentException("Column can not be null");
                }
                return false;
            }
            if (column.table != this.table) {
                if (fThrowException) {
                    throw new IllegalArgumentException("Column does not belong to table");
                }
                return false;
            }
            
            return true;
        }


        public void Clear() {
            int count = this._list.size();
            DataColumn[] array = new DataColumn[this._list.size()];
            array = this._list.toArray(array);
            
            if (this.table.fInitInProgress && (this.delayedAddRangeColumns != null)) {
                this.delayedAddRangeColumns = null;
            }
            try {
                this.fInClear = true;
                this.BaseGroupSwitch(array, count, null, 0);
                this.fInClear = false;
            }
            catch (Exception exception) { }
            this._list.clear();
            this.table.ElementColumnCount = 0;
        }

        public boolean Contains(String name) {
            DataColumn column = null;
			if (this.columnFromName.containsKey(name))
				column = this.columnFromName.get(name);
            return ((column != null) || (this.IndexOfCaseInsensitive(name) >= 0));
        }

        boolean Contains(String name, boolean caseSensitive) {
            DataColumn column = null;
			if (this.columnFromName.containsKey(name))
				column = this.columnFromName.get(name);
            if (column != null) {
                return true;
            }
            if (caseSensitive) {
                return false;
            }
            return (this.IndexOfCaseInsensitive(name) >= 0);
        }

        public void CopyTo(DataColumn[] array, int index) {
            if (array == null) {
                throw new IllegalArgumentException("Array can not be null");
            }
            if (index < 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            if ((array.length - index) < this._list.size()) {
                throw new IllegalArgumentException("Array is too small to contain list at specified index");
            }
            for (int i = 0; i < this._list.size(); i++) {
                array[index + i] = (DataColumn) this._list.get(i);
            }
        }

         void EnsureAdditionalCapacity(int capacity) {
             this._list.ensureCapacity(capacity+this._list.size());
        }

        void FinishInitCollection() {
            if (this.delayedAddRangeColumns != null) {
                for (DataColumn column : this.delayedAddRangeColumns) {
                    if (column != null) {
                        this.Add(column);
						column.FinishInitInProgress();
                    }
                }
                this.delayedAddRangeColumns = null;
            }
        }

        public int IndexOf(DataColumn column) {
            int count = this._list.size();
            for (int i = 0; i < count; i++) {
                if (column == ((DataColumn) this._list.get(i))) {
                    return i;
                }
            }
            return -1;
        }

        public int IndexOf(String columnName) {
            if ((columnName != null) && (0 < columnName.length())) {
                DataColumn column = null;
				if (this.columnFromName.containsKey(columnName))
					column = this.columnFromName.get(columnName);
                int count = this.size();
                if (column == null) {
                    int num = this.IndexOfCaseInsensitive(columnName);
                    if (num >= 0) {
                        return num;
                    }
                    return -1;
                }
                for (int i = 0; i < count; i++) {
                    if (column == this._list.get(i)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        int IndexOfCaseInsensitive(String name) {
            int specialHashCode = this.table.GetSpecialHashCode(name);
            int num2 = -1;
            DataColumn column = null;
            for (int i = 0; i < this.size(); i++) {
                column = (DataColumn) this._list.get(i);
                if ((((specialHashCode == 0) || (column._hashCode == 0)) || (column._hashCode == specialHashCode)) && (super.NamesEqual(column.ColumnName, name, false) != 0)) {
                    if (num2 != -1) {
                        return -2;
                    }
                    num2 = i;
                }
            }
            return num2;
        }

        private String MakeName(int index) {
			return String.format("Column%d",index);
        }

        void MoveTo(DataColumn column, int newPosition) {
            if ((0 > newPosition) || (newPosition > (this.size() - 1))) {
                throw new ArrayIndexOutOfBoundsException(newPosition);
            }
            this._list.remove(column);
            this._list.add(newPosition, column);
            int count = this._list.size();
            for (int i = 0; i < count; i++) {
                this._list.get(i).SetOrdinalInternal(i);
            }
        }

        private void OnCollectionChanged() {
            this.table.UpdatePropertyDescriptorCollectionCache();
        }

        void OnColumnPropertyChanged() {
            this.table.UpdatePropertyDescriptorCollectionCache();
        }

        void RegisterColumnName(String name, DataColumn column) {
            try {
                this.columnFromName.put(name, column);
                if (column != null) {
                    column._hashCode = this.table.GetSpecialHashCode(name);
                }
            } catch (ArgumentException e) {
                if (this.columnFromName.get(name) == null) {
                    throw new IllegalArgumentException(String.format("Table already has a column named: %s",name));
                }
                if (column != null) {
                    throw ExceptionBuilder.CannotAddDuplicate(name);
                }
                throw ExceptionBuilder.CannotAddDuplicate3(name);
            }
            if ((column == null) && (super.NamesEqual(name, this.MakeName(this.defaultNameIndex), true) != 0)) {
                do {
                    this.defaultNameIndex++;
                } while (this.Contains(this.MakeName(this.defaultNameIndex)));
            }
        }

        public void Remove(DataColumn column) {
            this.BaseRemove(column);
            this.ArrayRemove(column);
            this.OnCollectionChanged();
            if (column.ColumnMapping == MappingType.Element) {
                this.table.ElementColumnCount--;
            }
        }

        public void Remove(String name) {
            DataColumn column = this.get(name);
            if (column == null) {
                throw new IllegalArgumentException(String.format("Table has not column named: %s",name));
            }
            this.Remove(column);
        }

        public void RemoveAt(int index) {
            DataColumn column = this.get(index);
            if (column == null) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            this.Remove(column);
        }

        void UnregisterName(String name) {
            this.columnFromName.remove(name);
            if (super.NamesEqual(name, this.MakeName(this.defaultNameIndex - 1), true) != 0) {
                do {
                    this.defaultNameIndex--;
                } while ((this.defaultNameIndex > 1) && !this.Contains(this.MakeName(this.defaultNameIndex - 1)));
            }
        }

        public DataColumn get(int index) {
            DataColumn column;
            column = this._list.get(index);
            return column;
        }

        public DataColumn get(String name) {
			DataColumn column = null;
			if (name == null) {
				throw new IllegalArgumentException("Argument name can not be null");
			}
			
			if (this.columnFromName.containsKey(name))
				column = this.columnFromName.get(name);
            
			if (column == null) {
				int num = this.IndexOfCaseInsensitive(name);
				if (0 <= num) {
					column = this._list.get(num);
				} else if (-2 == num) {
                    throw new IllegalStateException(String.format("Column %s is duplicated.",name));
				}
			}
			return column;
        }

		@Override
        protected ArrayList<DataColumn> List() {
			return this._list;
        }
    }