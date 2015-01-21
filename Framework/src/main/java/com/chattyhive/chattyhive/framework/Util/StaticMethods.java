package com.chattyhive.chattyhive.framework.Util;

import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created by Jonathan on 03/08/2014.
 */
public class StaticMethods {
    private StaticMethods() {}

    public static void SetAlpha(View v,float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            v.setAlpha(alpha);
        } else {
            if (alpha != 1f) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(alpha,alpha);
                alphaAnimation.setDuration(0);
                alphaAnimation.setFillAfter(true);
                v.startAnimation(alphaAnimation);
            } else {
                v.clearAnimation();
            }
        }
    }
}
