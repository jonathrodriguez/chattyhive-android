package com.chattyhive.chattyhive.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * Created by jonathan on 21/06/2015.
 */
public abstract class ViewHolder<T> {
    protected View containerView;
    protected Context context;
    protected BaseAdapter baseAdapter;
    protected T item;

    public ViewHolder(Context context, BaseAdapter baseAdapter, View containerView) {
        this.context = context;
        this.baseAdapter = baseAdapter;
        this.setContainerView(containerView);
    }
    public ViewHolder(Context context, BaseAdapter baseAdapter, View containerView, T item) {
        this.context = context;
        this.baseAdapter = baseAdapter;
        this.setContainerView(containerView);
        this.setItem(item);
    }

    public void setContainerView(View containerView) {
        this.containerView = containerView;
        if ((this.containerView != null) && (this.item != null))
            this.updateView();
    }

    public void setItem(T item) {
        this.item = item;
        if ((this.containerView != null) && (this.item != null))
            this.updateView();
    }

    protected abstract void updateView();
}
