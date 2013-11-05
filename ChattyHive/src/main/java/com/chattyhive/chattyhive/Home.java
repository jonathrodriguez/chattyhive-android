package com.chattyhive.chattyhive;

import com.chattyhive.backend.PubSub;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.security.Timestamp;
import java.util.Date;

public class Home extends Activity {
    static final int OP_CODE_LOGIN = 1;
    String mUsername = "Jonathan";
    String mChannel_name = "public_test";

    ChatListAdapter _chatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if ((mUsername==null) || (mUsername.isEmpty())) {
           this.hasToLoggin();
        } else {
            this.Logged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OP_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                mUsername = data.getStringExtra(LoginActivity.EXTRA_EMAIL);
                this.Logged();
            } else {
                this.hasToLoggin();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void hasToLoggin() {
        Intent inte = new Intent(this, LoginActivity.class);
        inte.putExtra(LoginActivity.EXTRA_EMAIL,mUsername);
        startActivityForResult(inte,OP_CODE_LOGIN);
    }

    private void Logged () {
       this._chatListAdapter = new ChatListAdapter(this, this.mUsername,true);
       PubSub publishSubscriptionService = new PubSub(this.mUsername,new PubSub.PubSubChannelEventListener() {
           @Override
           public void onChannelEvent(String channel_name, String event_name, String message) {
               int event_type = 0;
               if (event_name.equalsIgnoreCase("msg")) {
                   event_type = 1;
               }
               switch (event_type) {
                   case 1:
                    if (channel_name.equalsIgnoreCase(mChannel_name)) {
                        _chatListAdapter.addItem(new ChatMessage("Usuario",message, new Date()));
                    }
                    break;
               }
           }
       });
        publishSubscriptionService.Join(mChannel_name);
    }
}