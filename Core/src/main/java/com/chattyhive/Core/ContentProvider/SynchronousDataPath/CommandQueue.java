package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by Jonathan on 08/03/2015.
 */
public class CommandQueue {
    public enum Priority { RealTime, High, Medium, Low  }

    private TreeSet<Request> requests;
    private HashMap<Command,Request> crMap;
    private final Object _syncLock = new Object();

    public CommandQueue() {
        synchronized (_syncLock) {
            requests = new TreeSet<Request>();
            crMap = new HashMap<Command, Request>();
            _syncLock.notify();
        }
    }

    public void put(Command command, Priority priority) throws InterruptedException {
        synchronized (_syncLock) {
            while ((requests == null) || (crMap == null))
                _syncLock.wait();

            if (crMap.containsKey(command)) {
                Request request = crMap.get(command);
                if (!request.getPriority().equals(priority))
                    request.setPriority(priority);
            } else {
                Request request = new Request(priority, command);
                crMap.put(command,request);
                requests.add(request);
            }
            _syncLock.notify();
        }
    }

    public Command poll() throws InterruptedException {
        Request request = null;
        synchronized (_syncLock) {
            while ((requests == null) || (crMap == null) || (requests.isEmpty()))
                _syncLock.wait();

            request = requests.pollFirst();
            crMap.remove(request.command);
            _syncLock.notify();
        }
        //if (request != null)
            return request.command;

        //return null;
    }

    public AbstractMap.Entry<Priority,Command> pollRequest() throws InterruptedException {
        Request request = null;
        synchronized (_syncLock) {
            while ((requests == null) || (crMap == null) || (requests.isEmpty()))
                _syncLock.wait();

            request = requests.pollFirst();
            crMap.remove(request.command);
            _syncLock.notify();
        }
        //if (request != null)
            return new AbstractMap.SimpleEntry<Priority, Command>(request.priority,request.command);

        //return null;
    }

    private class Request implements Comparable<Request> {
        private Priority priority;
        private Date timeStamp;
        private Command command;

        public Priority getPriority() {
            return priority;
        }
        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public Command getCommand() {
            return command;
        }

        public Request(Priority priority, Command command) {
            this.priority = priority;
            this.command = command;
            this.timeStamp = new Date();
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         */
        @Override
        public int compareTo(Request o) {
            int result = this.priority.compareTo(o.priority);
            if (result == 0)
                result = this.timeStamp.compareTo(o.timeStamp);

            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Request request = (Request) o;

            if (!command.equals(request.command)) return false;
            if (priority != request.priority) return false;
            if (!timeStamp.equals(request.timeStamp)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = priority.hashCode();
            result = 31 * result + timeStamp.hashCode();
            result = 31 * result + command.hashCode();
            return result;
        }
    }
}
