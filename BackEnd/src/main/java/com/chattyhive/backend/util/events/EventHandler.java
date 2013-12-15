package com.chattyhive.backend.util.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jonathan on 15/12/13.
 */
public class EventHandler<T extends EventArgs> {

    private Method _method;
    private Object _o;

    public EventHandler (Object O,String methodName) throws NoSuchMethodException {
        try {
            Class tClass = Class.forName((getClass().getTypeParameters()[0]).getName());
            this._o = 0;
            this._method = O.getClass().getMethod(methodName,Object.class,tClass);
        } catch (ClassNotFoundException e) { }
    }

    public void Invoke(Object sender, T eventArgs) throws InvocationTargetException, IllegalAccessException {
        this._method.invoke(this._o,sender,eventArgs);
    }
}
