package com.chattyhive.chattyhive;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by Jonathan on 05/01/2015.
 */
public abstract class Window implements Serializable {
    protected final Context context;
    protected int hierarchyLevel;

    public int getHierarchyLevel() {
        return this.hierarchyLevel;
    }
    protected void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public Window(Context context) {
        if (!(context instanceof Main))
            throw new IllegalArgumentException("Context must be an instance of com.chattyhive.chattyhive.Main");

        this.context = context;
        this.hierarchyLevel = 0;
    }

    public abstract void Open();
    public abstract void Close();
}
