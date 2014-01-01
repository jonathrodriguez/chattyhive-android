package com.chattyhive.chattyhive;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.businessobjects.MessageContent;
import com.chattyhive.backend.contentprovider.server.Server;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.chattyhive.backgroundservice.CHService;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.util.Date;

/**
 * Created by Jonathan on 17/10/2013
 */

public class Home extends Activity {
    static final int OP_CODE_LOGIN = 1;
    //PubSub publishSubscriptionService;
    String mUsername = "";//Jonathan
    String mChannel_name = "public_test";

    ChatListAdapter _chatListAdapter;
    //ConnectionState targetState;

    TextView status;
    ToggleButton switchButton;

    Server server;
    Controller _controller;
    ServerUser _serverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        switchButton =((ToggleButton)findViewById(R.id.toggleButton));
        status = ((TextView)findViewById(R.id.textView));

        switchButton.setOnClickListener(onClick_ToggleButton);

        ((Button)findViewById(R.id.button)).setOnClickListener(onClick_SendButton);


        if ((mUsername==null) || (mUsername.isEmpty())) {
           this.hasToLogin();
        } else {
            this.ConnectToServer("");
            this.Logged();
        }
    }

    private void ConnectToServer(String AppName) {
        Intent intent = new Intent(this, CHService.class);
        intent.putExtra(LoginActivity.EXTRA_EMAIL,mUsername);
        intent.putExtra(LoginActivity.EXTRA_SERVER,AppName);
        this.startService(intent);

        this._serverUser = new ServerUser(mUsername,"");
        if ((AppName == null) || (AppName.isEmpty())) {
            this._controller = new Controller(this._serverUser);
        } else {
            this._controller = new Controller(this._serverUser,AppName);
        }

        try {
            this._controller.SubscribeChannelEventHandler(new EventHandler<ChannelEventArgs>(this,"onChannelEvent",ChannelEventArgs.class));
            this._controller.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionStateChange",PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }

        switchButton.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OP_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                mUsername = data.getStringExtra(LoginActivity.EXTRA_EMAIL);
                String serverAppName = data.getStringExtra(LoginActivity.EXTRA_SERVER);
                if ((mUsername!=null)&&(!mUsername.isEmpty())) {
                    this.ConnectToServer(serverAppName);
                    this.Logged();
                } else {
                    this.hasToLogin();
                }
            } else {
                this.finish();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void hasToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_EMAIL, mUsername);
        intent.putExtra(LoginActivity.EXTRA_SERVER, StaticParameters.DefaultServerAppName);
        startActivityForResult(intent, OP_CODE_LOGIN);
    }

    private void Logged () {
        this._chatListAdapter = new ChatListAdapter(this, this.mUsername,true);
        ((ListView)findViewById(R.id.listView)).setAdapter(this._chatListAdapter);
    }

    public void onConnectionStateChange(Object sender, final PubSubConnectionEventArgs args) {
        runOnUiThread(new Runnable(){
            public void run() {
                status.setText(args.getChange().getCurrentState().toString());
            }
        });
    }

    public void onChannelEvent(Object sender, final ChannelEventArgs args) {
        int event_type = 0;
        if (args.getEventName().equalsIgnoreCase("msg")) {
            event_type = 1;
        } else if (args.getEventName().equalsIgnoreCase("SubscriptionSucceeded")) {
            event_type = 2;
        }
        switch (event_type) {
            case 1:
                if (args.getChannelName().equalsIgnoreCase(mChannel_name)) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            _chatListAdapter.addItem(args.getMessage());
                            _chatListAdapter.notifyDataSetChanged();
                        }
                    });
                }
                break;
            case 2:
                if (args.getChannelName().equalsIgnoreCase(mChannel_name)) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            status.setText("CONNECTED & JOINED");
                        }
                    });
                }
                break;
        }
    }

    public View.OnClickListener onClick_SendButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText input = (EditText)findViewById(R.id.editText);
            String msg = input.getText().toString();

            Message message = new Message(new MessageContent(msg),new Date());

            _controller.sendMessage(message);
        }
    };

    public View.OnClickListener onClick_ToggleButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (((ToggleButton)v).isChecked()) {
                _controller.Connect();
            } else {
                _controller.Disconnect();
            }
        }
    };
}