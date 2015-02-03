package com.chattyhive.chattyhive.framework.Util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Jonathan on 02/02/2015.
 */
public class Keyboard {
    public static void HideKeyboard(Activity activity) {
        if (activity == null) return;

        Keyboard.HideKeyboard(activity.getCurrentFocus());
        //View view = activity.getCurrentFocus();
        //if (view != null) {
            //InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            //inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //}
    }
    public static void HideKeyboard(View view) {
        if (view == null) return;

        view.clearFocus();

        //InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public static void ShowKeyboard(View view) {
        if (view == null) return;

        view.requestFocusFromTouch();

        //if (view.requestFocus()) {
        //    InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //    imm.showSoftInput(view, 0);
        //}
    }
}
