package com.chattyhive.chattyhive;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Chats.Messages.MessageContent;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionState;
import com.chattyhive.backend.contentprovider.server.ServerStatus;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.chattyhive.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.backgroundservice.CHService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.util.Date;

/**
 * Created by Jonathan on 17/10/2013
 */

public class Home_old extends Activity implements ServiceConnection {
    static final int OP_CODE_LOGIN = 1;

    String mChannel_name = "public_test";

    TextView status;
    ToggleButton switchButton;

    Controller _controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_old);

        switchButton =((ToggleButton)findViewById(R.id.toggleButton));
        status = ((TextView)findViewById(R.id.textView));

        switchButton.setOnClickListener(onClick_ToggleButton);

        ((Button)findViewById(R.id.button)).setOnClickListener(onClick_SendButton);

        this._controller = Controller.getRunningController(LoginLocalStorage.getLoginLocalStorage());
        Controller.bindApp();

        this.ConnectService();

        if ((this._controller == null) || (this._controller.getServerUser() == null) ||
                (this._controller.getServerUser().getLogin().isEmpty())) {
            this.hasToLogin();
        } else {
            this.Logged();
        };

        ListView lv = ((ListView)findViewById(R.id.listView));
        lv.smoothScrollToPosition(lv.getCount());

        //((EditText)findViewById(R.id.editText)).setText(String.valueOf(lv.getMaxScrollAmount()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OP_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                this.Logged();
            } else {
                Controller.disposeRunningController();
                this.finish();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_old, menu);
        return true;
    }

    private void hasToLogin() {
        Intent intent = new Intent(this, LoginActivity_old.class);
        startActivityForResult(intent, OP_CODE_LOGIN);
    }

    private void Logged () {
        try {
            this._controller.SubscribeChannelEventHandler(new EventHandler<ChannelEventArgs>(this,"onChannelEvent",ChannelEventArgs.class));
            this._controller.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionStateChange",PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }

        if (this._controller.getServerUser().getStatus() != ServerStatus.LOGGED) {
            this._controller.Connect();
        } else {
            runOnUiThread(new Runnable(){
                public void run() {
                    switchButton.setChecked(true);
                    status.setText("CONNECTED & JOINED");
                }
            });
        }
    }

    public void onConnectionStateChange(Object sender, final PubSubConnectionEventArgs args) {
        runOnUiThread(new Runnable(){
            public void run() {
                status.setText(args.getChange().getCurrentState().toString());
                Boolean checked = (args.getChange().getCurrentState() == ConnectionState.CONNECTED) ||
                                  (args.getChange().getCurrentState() == ConnectionState.CONNECTING);
                switchButton.setChecked(checked);
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

            if (_controller.sendMessage(message,""))
                input.setText("");
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

    private void ConnectService() {
        Context context = this.getApplicationContext();
        context.startService(new Intent(context, CHService.class)); //If not, then start it.}
    }

    @Override
         public void onDestroy() {
        Controller.unbindApp();
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}