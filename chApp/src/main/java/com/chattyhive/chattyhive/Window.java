package com.chattyhive.chattyhive;

import android.content.Context;
import java.io.Serializable;

/**
 * Created by Jonathan on 05/01/2015.
 */
public abstract class Window implements Serializable {
    protected transient Context context;
    protected int hierarchyLevel;

    void setContext(Context context) {
        if (!(context instanceof Main))
            throw new IllegalArgumentException("Context must be an instance of com.chattyhive.chattyhive.Main");
        this.context = context;
    }

    public Boolean hasContext() {
        return (this.context != null);
    }

    public int getHierarchyLevel() {
        return this.hierarchyLevel;
    }
    protected void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public Window(Context context) {
        this.setContext(context);
        this.hierarchyLevel = 0;
    }

    public abstract void Open();
    public abstract void Close();
    public abstract void Show();
    public abstract void Hide();
}
