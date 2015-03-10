package com.chattyhive.backend.ContentProvider.server;
import com.chattyhive.backend.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.backend.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.backend.Util.Events.EventHandler;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by Jonathan on 26/01/2015.
 */
public class ServerQueue {
    public enum Priority { RealTime, High, Medium, Low  }


    private Server server;

    private final TreeSet<Request> requests;
    private final HashMap<Command,Request> crMap;

    public void setServer(Server server) {
        this.server = server;

        if (this.server != null) {
            if (processor.isAlive()) {
                synchronized (requests) {
                    if (!requests.isEmpty())
                       requests.notify();
                }
            } else {
                this.processor.start();
            }
        }
    }
    public Server getServer() {
        return this.server;
    }

    private Thread processor = new Thread(new Runnable() {
        @Override
        public void run() {
            Request request = null;
            while (true) {
                synchronized (requests) {
                    while ((requests.isEmpty()) || (server == null)) {
                        try {
                            requests.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    request = requests.pollFirst();
                }
                //TODO: Process request.

                //TODO: Invoke callbacks.


            }
        }
    });

    public ServerQueue() {
        requests = new TreeSet<Request>();
        crMap = new HashMap<Command, Request>();
        startProcessor();
    }

    public void addRequest() {
        Command command = null;
        Request request = null;
        Priority priority = Priority.RealTime;
        EventHandler<CommandCallbackEventArgs>[] callbacks = null;

        synchronized (crMap) {
            if ((!crMap.isEmpty()) && (crMap.containsKey(command)))
                request = crMap.get(command);
        }

        if (request != null) {
            synchronized (requests) {
                if ((requests.contains(request)) && (request.priority.ordinal() < priority.ordinal()))
                    request.priority = priority;
            }
            synchronized (request) {
                request.commandCallbacks.addAll(Arrays.asList(callbacks));
            }
        } else {
            request = new Request();
            //TODO: Fill request
            synchronized (requests) {
                requests.add(request);
                synchronized (crMap) {
                    crMap.put(request.command,request);
                }
                if ((!requests.isEmpty()) && (server != null))
                    requests.notify();
            }
        }

    }

    private void startProcessor() {
        if (!this.processor.isAlive())
            this.processor.start();
    }



    protected class Request {
        private Priority priority;
        private Date timestamp;
        private Command command;
        private TreeSet<EventHandler<CommandCallbackEventArgs>> commandCallbacks;

    }
}
