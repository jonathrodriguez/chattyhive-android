package com.chattyhive.chattyhive.Authentication;

import android.accounts.Account;
import android.content.Context;

import com.chattyhive.Core.ContentProvider.Formats.LOGIN;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandDefinition;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandQueue;
import com.chattyhive.Core.Controller;
import com.chattyhive.Core.Util.CallbackDelegate;
import com.chattyhive.backend.contentprovider.server.Server;
import com.chattyhive.chattyhive.framework.OSStorageProvider.LocalSettings;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jonathan on 01/08/2015.
 */
public class ChattyhiveAuthenticatorServer {
    private Context context;

    public ChattyhiveAuthenticatorServer(Context context) {
        this.context = context;

    }

    public String userSignIn(Account account) {
        ServerUser user = new ServerUser(account,this.context);
        Subprocess subprocess = new Subprocess();
        subprocess.doWork(user);
        synchronized (subprocess.isRunning) {
            while (subprocess.isRunning.get())
                try {
                    subprocess.isRunning.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        return user.getAuthToken(CommandDefinition.SessionCookie);
    }

    public class Subprocess {
        private Controller controller;
        protected Subprocess() {
            this.controller = Controller.GetRunningController();
            if (this.controller == null) {
                this.controller = Controller.InitializeController(LocalSettings.getLocalSettings());
            }
        }

        protected final AtomicBoolean isRunning = new AtomicBoolean(false);

        protected void doWork(ServerUser user) {
            synchronized (this.isRunning) {
                this.isRunning.set(true);
            }
            LOGIN loginFormat = new LOGIN();
            loginFormat.USER = user.getLogin();
            loginFormat.PASS = user.getPassword();
            Command loginCommand = new Command(user, AvailableCommands.Login,loginFormat);
            loginCommand.addCallbackDelegate(new CallbackDelegate(this,"WorkCallback",null));
            this.controller.getDataProvider().runCommand(loginCommand, CommandQueue.Priority.RealTime);
        }

        public void WorkCallback() {
            synchronized (this.isRunning) {
                this.isRunning.set(false);
                this.isRunning.notify();
            }
        }
    }
}
