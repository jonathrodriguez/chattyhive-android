package com.chattyhive.chattyhive.Authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Jonathan on 19/07/2015.
 */
public class ChattyhiveAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        ChattyhiveAuthenticator authenticator = new ChattyhiveAuthenticator(this);
        return authenticator.getIBinder();
    }
}