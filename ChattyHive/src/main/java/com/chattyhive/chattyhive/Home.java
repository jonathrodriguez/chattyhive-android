package com.chattyhive.chattyhive;

import com.chattyhive.backend.Controler;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.bussinesobjects.Message;
import com.chattyhive.backend.bussinesobjects.MessageContent;
import com.chattyhive.backend.bussinesobjects.User;
import com.chattyhive.backend.contentprovider.pubsubservice.PubSub;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionState;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionStateChange;
import com.chattyhive.backend.contentprovider.server.Server;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Home extends Activity implements PubSub.PubSubChannelEventListener, PubSub.PubSubConnectionEventListener {
    static final int OP_CODE_LOGIN = 1;
    //PubSub publishSubscriptionService;
    String mUsername = "";//Jonathan
    String mChannel_name = "public_test";

    ChatListAdapter _chatListAdapter;
    //ConnectionState targetState;

    TextView status;
    ToggleButton switchButton;

    Server server;
    Controler _controler;
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
            this.ConectToServer("");
            this.Logged();
        }
    }

    private void ConectToServer(String AppName) {
        this._serverUser = new ServerUser(mUsername,"");
        if ((AppName == null) || (AppName.isEmpty())) {
            this._controler = new Controler(this._serverUser,this);
        } else {
            this._controler = new Controler(this._serverUser,AppName,this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OP_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                mUsername = data.getStringExtra(LoginActivity.EXTRA_EMAIL);
                String serverAppName = data.getStringExtra(LoginActivity.EXTRA_SERVER);
                if ((mUsername!=null)&&(!mUsername.isEmpty())) {
                    this.ConectToServer(serverAppName);
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
        Intent inte = new Intent(this, LoginActivity.class);
        inte.putExtra(LoginActivity.EXTRA_EMAIL, mUsername);
        inte.putExtra(LoginActivity.EXTRA_SERVER,StaticParameters.DefaultServerAppName);
        startActivityForResult(inte, OP_CODE_LOGIN);
    }

    private void Logged () {
        this._chatListAdapter = new ChatListAdapter(this, this.mUsername,true);
        ((ListView)findViewById(R.id.listView)).setAdapter(this._chatListAdapter);
        //publishSubscriptionService = new PubSub(this.mUsername,this);
        //publishSubscriptionService.setConnectionEventListener(this);
        //switchButton.performClick();
    }

    @Override
    public void onConnectionStateChange(final ConnectionStateChange change) {
        runOnUiThread(new Runnable(){
            public void run() {
                status.setText(change.getCurrentState().toString());
            }
        });

        /*if ((targetState == ConnectionState.CONNECTED) && (change.getCurrentState() == ConnectionState.DISCONNECTED)) {
            publishSubscriptionService.Connect();
        } else if ((targetState == ConnectionState.DISCONNECTED) && (change.getCurrentState() == ConnectionState.CONNECTED)) {
            publishSubscriptionService.Disconnect();
        }*/
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
                            JsonParser jsonParser = new JsonParser();
                            JsonElement jsonElement = jsonParser.parse(message);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            String msg_uname = jsonObject.get("username").getAsString();
                            String msg_msg = jsonObject.get("message").getAsString();
                            Date ts = TimestampFormatter.toDate(jsonObject.get("timestamp").getAsString());
                            _chatListAdapter.addItem(new Message(new User(msg_uname),new MessageContent(msg_msg), ts));
                            _chatListAdapter.notifyDataSetChanged();
                        }
                    });
                }
                break;
            case 2:
                if (channel_name.equalsIgnoreCase(mChannel_name)) {
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

            //String ts = TimestampFormatter.toString(new Date());
            //msg = "message=".concat(msg.replace("+","%2B").replace(" ", "+")).concat("&timestamp=").concat(ts.replace(":","%3A").replace("+","%2B").replace(" ","+"));

            _controler.sendMessage(message);
           // server.SendMessage(msg);

        }
    };

    public View.OnClickListener onClick_ToggleButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           /* targetState = (!((ToggleButton)v).isChecked())?ConnectionState.DISCONNECTED:ConnectionState.CONNECTED;
            if (targetState == ConnectionState.DISCONNECTED) {
                publishSubscriptionService.Disconnect();
            } else {
                publishSubscriptionService.Join(mChannel_name);
                publishSubscriptionService.Connect();
            }*/
        }
    };
}