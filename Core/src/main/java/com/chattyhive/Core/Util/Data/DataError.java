package com.chattyhive.Core.Util.Data;

/**
 * Created by Jonathan on 21/03/2015.
 */
final class DataError {
    private int count;
    private ColumnError[] errorList;
    final int initialCapacity = 1;
    private String rowError;

    DataError() {
        this.rowError = "";
    }

    DataError(String rowError) {
        this.rowError = "";
        this.SetText(rowError);
    }

    void Clear() {
        for (int i = 0; i < this.count; i++) {
            this.errorList[i].column.errors--;
        }
        this.count = 0;
        this.rowError = "";
    }

    void Clear(DataColumn column) {
        if (this.count != 0) {
            for (int i = 0; i < this.count; i++) {
                if (this.errorList[i].column == column) {
                    Array.Copy(this.errorList, i + 1, this.errorList, i, (this.count - i) - 1);
                    this.count--;
                    column.errors--;
                }
            }
        }
    }

    String GetColumnError(DataColumn column) {
        for (int i = 0; i < this.count; i++) {
            if (this.errorList[i].column == column) {
                return this.errorList[i].error;
            }
        }
        return "";
    }

    DataColumn[] GetColumnsInError() {
        DataColumn[] columnArray = new DataColumn[this.count];
        for (int i = 0; i < this.count; i++) {
            columnArray[i] = this.errorList[i].column;
        }
        return columnArray;
    }

    int IndexOf(DataColumn column) {
        for (int i = 0; i < this.count; i++) {
            if (this.errorList[i].column == column) {
                return i;
            }
        }
        if (this.count >= this.errorList.length) {
            ColumnError[] destinationArray = new ColumnError[Math.min(this.count * 2, column.Table().Columns().size())];
            Array.Copy(this.errorList, 0, destinationArray, 0, this.count);
            this.errorList = destinationArray;
        }
        return this.count;
    }

    void SetColumnError(DataColumn column, String error) {
        if ((error == null) || (error.length() == 0)) {
            this.Clear(column);
        } else {
            if (this.errorList == null) {
                this.errorList = new ColumnError[1];
            }
            int index = this.IndexOf(column);
            this.errorList[index].column = column;
            this.errorList[index].error = error;
            column.errors++;
            if (index == this.count) {
                this.count++;
            }
        }
    }

    private void SetText(String errorText) {
        if (errorText == null) {
            errorText = "";
        }
        this.rowError = errorText;
    }

    boolean HasErrors()
    {
            if (this.rowError.length() == 0) {
                return (this.count != 0);
            }
            return true;
    }

        String Text() {
            return this.rowError;
        }
        void Text(String value) {
            this.SetText(value);
        }


    class ColumnError {
        DataColumn column;
        String error;
    }
}