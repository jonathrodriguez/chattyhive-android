package com.chattyhive.Core.Util.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Jonathan on 16/03/2015.
 */
public abstract class InternalDataCollectionBase<T> implements Collection<T>, Iterable<T> {
    // Fields


    // Methods
    public void CopyTo(Array ar, int index) {
        System.arraycopy(this.List().toArray(),0,ar,index,this.size());
    }

    @Override
    public Iterator iterator() {
        return this.List().iterator();
    }

    int NamesEqual(String s1, String s2, boolean fCaseSensitive) {
        if (fCaseSensitive) {
            if (s1.compareTo(s2) == 0) {
                return 1;
            }
            return 0;
        }
        if (s1.compareToIgnoreCase(s2) != 0) {
            return 0;
        }
        if (s1.compareToIgnoreCase(s2) == 0) {
            return 1;
        }
        return -1;
    }

    // Properties

    @Override
    public int size() {
        return this.List().size();
    }

    public boolean IsReadOnly() {
        return false;
    }

    public boolean IsSynchronized() {
        return false;
    }

    protected ArrayList<T> List() {
        return null;
    }

    public Object SyncRoot() {
        return this;
    }

    @Override
    public Object[] toArray() {
        return this.List().toArray();
    }

    @Override
    public T[] toArray(Object[] a) {
        return this.List().toArray((T[])a);
    }

    @Override
    public void clear() {
        this.List().clear();
    }

    @Override
    public boolean isEmpty() {
        return this.List().isEmpty();
    }
}
