package com.chattyhive.Core.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jonathan on 09/03/2015.
 */
public class CallbackDelegate<T> {

    Callback<T> callback;
    /**
     * Public constructor.
     * @param callback Callback interface to run.
     */
    public CallbackDelegate (Callback<T> callback) {
        this.callback = callback;
    }

    /**
     * Callback invocation
     * @param arg Argument to the callback function
     */
    public void Run(final T arg) {
        new Thread(() -> { callback.run(arg); }).start();
    }

    /**
     * Callback interface.
     * @param <T> Type of callback argument.
     */
    public interface Callback<T> {
        void run(T arg);
    }
}
