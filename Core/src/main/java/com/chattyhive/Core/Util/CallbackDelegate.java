package com.chattyhive.Core.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jonathan on 09/03/2015.
 */
public class CallbackDelegate {
    private Object subscriber;
    private Method method;

    /**
     * Public constructor.
     * @param Subscriber The object on which the method is to be invoked.
     * @param methodName The name of the method to invoke. It HAS TO have the correct parameters and be public.
     * @param argsClasses The classes of the method arguments.
     * @throws NoSuchMethodException If the method is not found in the subscriber.
     */
    public CallbackDelegate (Object Subscriber,String methodName, Class<?>... argsClasses) {
        this.subscriber = Subscriber;
        try {
            this.method = Subscriber.getClass().getMethod(methodName,argsClasses);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public void Run(final Object... args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(subscriber, args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
