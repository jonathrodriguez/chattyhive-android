package com.chattyhive.Core.Util.Data;

/**
 * Created by Jonathan on 16/03/2015.
 */
class DataRowBuilder {
    // Fields
    int _record;
    final DataTable _table;

    // Methods
    DataRowBuilder(DataTable table, int record) {
        this._table = table;
        this._record = record;
    }
}
