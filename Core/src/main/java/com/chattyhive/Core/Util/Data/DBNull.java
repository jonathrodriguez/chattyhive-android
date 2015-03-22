package com.chattyhive.Core.Util.Data;

import java.io.Serializable;

/**
 * Created by Jonathan on 21/03/2015.
 */
public final class DBNull implements Serializable /*ISerializable, IConvertible*/ {

    public static final DBNull Value = new DBNull();

    private DBNull() { }

    @Override
    public String toString() {
        return "";
    }
}
