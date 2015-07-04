package com.chattyhive.chattyhive;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jonathan on 21/06/2015.
 */
public abstract class PaginationList extends BaseAdapter {

    ViewGroup listView;
    private ArrayDeque<View>[] convertViewStack;
    private int page;
    private HashMap<View,Integer> viewTypes;

    private final Object drawingPage = new Object();
    private int latDrawPage;


    public PaginationList(ViewGroup listView) {
        this.listView = listView;
        this.latDrawPage = -1;

        this.convertViewStack = (ArrayDeque<View>[])new ArrayDeque[this.getViewTypeCount()];
        for (int i = 0; i < this.convertViewStack.length; i++)
            this.convertViewStack[i] = new ArrayDeque<View>();

        this.viewTypes = new HashMap<>();

        int minHeight = this.getItemCountPerPage()*this.getItemHeight() + listView.getPaddingBottom() + listView.getPaddingTop();
        this.listView.setMinimumHeight(minHeight);
    }

    public abstract int getViewTypeCount();

    public abstract int getItemViewType(int position);

    public abstract int getItemCountInThisPage();

    public abstract int getItemCountPerPage();

    public abstract int getItemHeight();

    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void loadNextPage(int nextPage) {
        synchronized (drawingPage) {
            this.page = nextPage;
            this.notifyPageChangedInternal();
        }
    }

    public int getPage() {
        return this.page;
    }

    public void notifyPageChanged() {
        synchronized (drawingPage) {
            this.notifyPageChangedInternal();
        }
    }

    private void notifyPageChangedInternal() {

            //if (this.latDrawPage != this.page)
                this.latDrawPage = this.page;
            //else
            //    return;

            for (View v : this.viewTypes.keySet()) { //First remove all views
                listView.removeView(v);
                int viewType = this.viewTypes.get(v);
                this.convertViewStack[viewType].add(v);
            }

            this.viewTypes.clear();

            int start = this.page * this.getItemCountPerPage();
            int end = start + this.getItemCountInThisPage();

            for (int i = start; i < end; i++) { //Then add new views
                int viewType = this.getItemViewType(i);
                View convertView = convertViewStack[viewType].poll();
                View resultView = this.getView(i,convertView,this.listView);
                this.listView.addView(resultView);
                this.viewTypes.put(resultView,viewType);
            }

            this.listView.invalidate();
            this.listView.requestLayout();
    }

    public void free() {
        for (View v : this.viewTypes.keySet()) { //First remove all views
            listView.removeView(v);
        }

        this.viewTypes.clear();

        for (int i = 0; i < this.convertViewStack.length; i++)
            this.convertViewStack[i].clear();

        this.page = 0;
    }

}
