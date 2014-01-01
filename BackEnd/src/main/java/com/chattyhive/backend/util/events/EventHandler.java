package com.chattyhive.backend.util.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jonathan on 15/12/13.
 * This class provides reflection for method invocation on events. An event handler is a method from
 * any object to process the event. It acts like a method pointer.
 */
public class EventHandler<T extends EventArgs> {

    private Method _method;
    private Object _subscriber;

    /**
     * Public constructor.
     * @param Subscriber The object on which the method is to be invoked.
     * @param methodName The name of the method to invoke. It HAS TO have the correct parameters and be public.
     * @param eventArgsClass The class of the event arguments.
     * @throws NoSuchMethodException If the method is not found in the subscriber.
     */
    //TODO: Find a better way to get the class of the Type Parameter T.
    public EventHandler (Object Subscriber,String methodName, Class<T> eventArgsClass) throws NoSuchMethodException {
            this._subscriber = Subscriber;
            this._method = Subscriber.getClass().getMethod(methodName,Object.class,eventArgsClass);
    }

    /**
     * Invokes the method to which this event handler points to. It passes to the method the sender
     * of the invocation and the event arguments.
     * @param sender An Object which represents the object that fired the event.
     * @param eventArgs The event arguments. They HAVE TO extend EventArgs.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void Invoke(Object sender, T eventArgs) throws InvocationTargetException, IllegalAccessException {
        this._method.invoke(this._subscriber,sender,eventArgs);
    }
}
