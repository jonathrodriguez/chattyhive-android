package com.chattyhive.backend.ContentProvider.OSStorageProvider;

import com.chattyhive.backend.Util.Data.DataTable;

/**
 * Created by Jonathan on 14/03/2015.
 */
public interface LocalDataBaseInterface {

    //Commands
    public void executeSQL(String sql);
    public DataTable tableQuerySQL(String sql);
    public Object simpleQuerySQL(String sql);

    //Transactions
    public enum TransactionMode { IMMEDIATE, EXCLUSIVE };
    public void beginTransaction(TransactionMode transactionMode);
    public void commitTransaction();
    public void finishTransaction();
}
