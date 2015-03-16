package com.chattyhive.Core.Util.Events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jonathan on 15/12/13.
 * This class provides reflection for method invocation on events. An event handler is a method from
 * any object to process the event. It acts like a method pointer.
 */
public class EventHandler<T extends EventArgs> {

    private Method method;
    private Object subscriber;


    //TODO: Find a better way to get the class of the Type Parameter T.
    //TODO: Find a way to get compile time errors if method does not exist. Maybe with Annotations or LINT.

    /**
     * Public constructor.
     * @param Subscriber The object on which the method is to be invoked.
     * @param methodName The name of the method to invoke. It HAS TO have the correct parameters and be public.
     * @param eventArgsClass The class of the event arguments.
     * @throws NoSuchMethodException If the method is not found in the subscriber.
     */
    public EventHandler (Object Subscriber,String methodName, Class<T> eventArgsClass) {
            this.subscriber = Subscriber;
            try {
                this.method = Subscriber.getClass().getMethod(methodName, Object.class, eventArgsClass);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
    }

    /**
     * Public constructor.
     * @param SubscriberClass The class which defines the static method to be invoked.
     * @param methodName The name of the method to invoke. It HAS TO have the correct parameters and be public.
     * @param eventArgsClass The class of the event arguments.
     * @throws NoSuchMethodException If the method is not found in the subscriber.
     */
    public EventHandler (Class<?> SubscriberClass,String methodName, Class<T> eventArgsClass) {
        this.subscriber = null;
        try {
            this.method = SubscriberClass.getMethod(methodName,Object.class,eventArgsClass);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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
        this.method.invoke(this.subscriber, sender, eventArgs);
    }

    public void Run(Object sender, T eventArgs) {
        try {
            this.method.invoke(this.subscriber, sender, eventArgs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof EventHandler) && (this.subscriber == (((EventHandler) o).subscriber)) && (this.method.getName().equalsIgnoreCase(((EventHandler) o).method.getName())));
    }

    @Override
    public int hashCode() {
        int res = 0;
        if (this.subscriber != null)
            res += this.subscriber.hashCode();
        if (this.method != null)
            res += this.method.hashCode();

        return res;
    }
}
