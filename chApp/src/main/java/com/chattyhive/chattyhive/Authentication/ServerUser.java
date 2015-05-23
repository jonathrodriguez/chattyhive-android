package com.chattyhive.chattyhive.Authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

import com.chattyhive.Core.ContentProvider.Server.IServerUser;
import com.chattyhive.Core.ContentProvider.Server.SessionStatus;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandDefinition;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jonathan on 11/02/2015.
 */
public class ServerUser implements IServerUser {

    private Account account;
    private AccountManager accountManager;

    public ServerUser (Account account, Context context) {
        this(account, AccountManager.get(context));
    }

    public ServerUser (Account account, AccountManager accountManager) {
        this.account = account;
        this.accountManager = accountManager;
    }

    @Override
    public String getLogin() {
        return account.name;
    }

    @Override
    public String getPassword() {
        return this.accountManager.getPassword(this.account);
    }

    @Override
    public String getAuthToken(String name) {
        if (name.equalsIgnoreCase(CommandDefinition.SessionCookie))
            return accountManager.peekAuthToken(this.account, "FULL_ACCESS");

        return null;
    }

    @Override
    public String getAuthTokens() {
        String sessionCookie = this.getAuthToken(CommandDefinition.SessionCookie);
            if (sessionCookie != null)
                return CommandDefinition.SessionCookie.concat("=").concat(sessionCookie);

        return null;
    }

    @Override
    public void updateAuthToken(String name, String value) {
        if (name.equalsIgnoreCase(CommandDefinition.SessionCookie))
            accountManager.setAuthToken(this.account,"FULL_ACCESS",value);
    }

    @Override
    public SessionStatus getStatus() {
        if (this.getAuthToken(CommandDefinition.SessionCookie) != null)
            return SessionStatus.CONNECTED;

        return SessionStatus.EXPIRED;
    }

    @Override
    public void invalidateAuthToken(String name) {
        this.accountManager.invalidateAuthToken(account.type,this.getAuthToken(name));
    }

    @Override
    public void invalidateAuthTokens() {
        this.invalidateAuthToken(CommandDefinition.SessionCookie);
    }

    @Override
    public Boolean removeUser() {
        final AccountManagerFuture<Boolean> amf = this.accountManager.removeAccount(this.account,null,null);

        final AtomicBoolean result = new AtomicBoolean();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result.set(amf.getResult());
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                    result.set(false);
                } catch (IOException e) {
                    e.printStackTrace();
                    result.set(false);
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                    result.set(false);
                }
            }
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            result.set(false);
        }

        return result.get();
    }

    @Override
    public void setPassword(String newPassword) {
        this.accountManager.setPassword(this.account,newPassword);
    }

    @Override
    public int hashCode() {
        return this.getLogin().hashCode();
    }
}
