package com.chattyhive.chattyhive;

import com.chattyhive.backend.PubSub;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.security.Timestamp;
import java.util.Date;

public class Home extends Activity implements PubSub.PubSubChannelEventListener, PubSub.PubSubConnectionEventListener {
    static final int OP_CODE_LOGIN = 1;
    PubSub publishSubscriptionService;
    String mUsername = "Jonathan";
    String mChannel_name = "public_test";

    ChatListAdapter _chatListAdapter;
    ConnectionState targetState;

    TextView status;
    ToggleButton swich;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        swich =((ToggleButton)findViewById(R.id.toggleButton));
        status = ((TextView)findViewById(R.id.textView));

        swich.setOnClickListener(onClick_ToggleButton);

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
        ((ListView)findViewById(R.id.listView)).setAdapter(this._chatListAdapter);
        publishSubscriptionService = new PubSub(this.mUsername,this);
        publishSubscriptionService.setConnectionEventListener(this);
        swich.performClick();
    }

    @Override
    public void onConnectionStateChange(final ConnectionStateChange change) {
        runOnUiThread(new Runnable(){
            public void run() {
                status.setText(change.getCurrentState().toString());
            }
        });

        if (change.getCurrentState() == targetState) {
            if (targetState == ConnectionState.CONNECTED) {
                publishSubscriptionService.Join(mChannel_name);
            }
        } else if ((targetState == ConnectionState.CONNECTED) && (change.getCurrentState() == ConnectionState.DISCONNECTED)) {
            publishSubscriptionService.Connect();
        } else if ((targetState == ConnectionState.DISCONNECTED) && (change.getCurrentState() == ConnectionState.CONNECTED)) {
            publishSubscriptionService.Disconnect();
        }
    }

    @Override
    public void onChannelEvent(String channel_name, String event_name, final String message) {
        int event_type = 0;
        if (event_name.equalsIgnoreCase("msg")) {
            event_type = 1;
        } else if (event_name.equalsIgnoreCase("SubscriptionSucceeded")) {
            event_type = 2;
        }
        switch (event_type) {
            case 1:
                if (channel_name.equalsIgnoreCase(mChannel_name)) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            _chatListAdapter.addItem(new ChatMessage("User",message, new Date()));
                        }
                    });
                }
                break;
            case 2:
                if (channel_name.equalsIgnoreCase(mChannel_name)) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            status.setText(publishSubscriptionService.GetConnectionState().toString().concat(" & JOINED"));
                        }
                    });
                }
                break;
        }
    }

    public View.OnClickListener onClick_ToggleButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            targetState = (!((ToggleButton)v).isChecked())?ConnectionState.DISCONNECTED:ConnectionState.CONNECTED;
            if (targetState == ConnectionState.DISCONNECTED) {
                publishSubscriptionService.Disconnect();
            } else {
                publishSubscriptionService.Connect();
            }
        }
    };
}