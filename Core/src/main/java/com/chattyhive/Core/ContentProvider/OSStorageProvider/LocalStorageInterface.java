package com.chattyhive.Core.ContentProvider.OSStorageProvider;

/**
 * Created by Jonathan on 22/05/2015.
 */
public interface LocalStorageInterface {
    enum StorageType { GlobalSettings, AccountSettings };

    String getData(StorageType storageType, String name);
    void setData(StorageType storageType, String name, String value);
}
