package com.chattyhive.chattyhive.framework.Util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jonathan on 25/05/2014.
 */
public class ApplicationContextProvider extends Application {
    /**
     * Keeps a reference of the application context
     */
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() { return context; }
}
