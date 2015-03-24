package com.chattyhive.Core.Util.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Jonathan on 16/03/2015.
 */
public abstract class InternalDataCollectionBase<T> implements Collection<T>, Iterable<T> {
    // Fields
    private ArrayList<T> list;

    protected InternalDataCollectionBase() {
        this.list = new ArrayList<T>();
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

    public Object SyncRoot() {
        return this;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null)
            throw new NullPointerException("o must not be null");

        return this.list.contains(o);
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

    @Override
    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            try {
                if (!this.contains(o))
                    return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        int iSize = this.List().size();

        for (T dc : c) {
            this.add(dc);
        }

        return iSize != this.List().size();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int iSize = this.List().size();

        for (Object o : c) {
            this.remove(o);
        }

        return iSize != this.List().size();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        int iSize = this.List().size();

        for (T dc : this.List()) {
            if (!c.contains(dc))
                this.remove(dc);
        }

        return iSize != this.List().size();
    }

    protected ArrayList<T> List() {
        return this.list;
    }
}
