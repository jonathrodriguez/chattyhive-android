package com.chattyhive.chattyhive.framework.Util;

import android.view.View;

/**
 * Created by Jonathan on 18/08/2014.
 */
public class ViewPair {
    private View mainView;
    private View actionBarView;

    public View getMainView() { return this.mainView; }
    public View getActionBarView() { return this.actionBarView; }

    public ViewPair(View mainView,View actionBarView) {
        this.mainView = mainView;
        this.actionBarView = actionBarView;
    }
}
