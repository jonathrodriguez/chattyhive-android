package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;

import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.chattyhive.OSStorageProvider.CookieStore;
import com.chattyhive.chattyhive.OSStorageProvider.GroupLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.HiveLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.MessageLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.UserLocalStorage;

import com.chattyhive.chattyhive.backgroundservice.CHService;

import com.chattyhive.chattyhive.framework.FloatingPanel;


public class Main extends Activity {
    static final int OP_CODE_LOGIN = 1;
    static final int OP_CODE_EXPLORE = 2;


    FloatingPanel floatingPanel;

    Controller controller;

    int ActiveLayoutID;

    //TODO: Add main panel view stack

    protected View ShowLayout (int layoutID, int actionBarID) {
        FrameLayout mainPanel = ((FrameLayout)findViewById(R.id.mainCenter));
        FrameLayout mainActionBar = ((FrameLayout)findViewById(R.id.actionCenter));
        mainPanel.removeAllViews();
        mainActionBar.removeAllViews();
        ActiveLayoutID = layoutID;
        LayoutInflater.from(this).inflate(actionBarID,mainActionBar,true);
        return LayoutInflater.from(this).inflate(layoutID, mainPanel, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveLayoutID = R.layout.home;
        setContentView(R.layout.main);

        findViewById(R.id.temp_explore_button).setOnClickListener(this.explore_button_click);
        findViewById(R.id.temp_profile_button).setOnClickListener((new Profile(this)).open_profile);
        findViewById(R.id.temp_chat_sync_button).setOnClickListener(this.chat_sync_button_click);
        findViewById(R.id.temp_logout_button).setOnClickListener(this.logout_button_click);
        findViewById(R.id.temp_clear_chats_button).setOnClickListener(this.clear_chats_button_click);

        setPanelBehaviour();

        //Log.w("Main","onCreate..."); //DEBUG
        Object[] LocalStorage = {LoginLocalStorage.getLoginLocalStorage(), GroupLocalStorage.getGroupLocalStorage(), HiveLocalStorage.getHiveLocalStorage(), MessageLocalStorage.getMessageLocalStorage(), UserLocalStorage.getUserLocalStorage()};
        Controller.Initialize(new CookieStore(),LocalStorage);

        this.controller = Controller.GetRunningController(true);
        Controller.bindApp();

        LeftPanel lp = new LeftPanel(this);

        this.ConnectService();

        this.checkLogin();

    }

    private void checkLogin() {
        if ((this.controller == null) || (LoginLocalStorage.getLoginLocalStorage().RecoverLoginPassword() == null)) {
            this.hasToLogin();
        } else {
            this.Logged();
        }
    }

    private void hasToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, OP_CODE_LOGIN);
    }

    private void Logged () {
/*        try {
            this.controller.SubscribeChannelEventHandler(new EventHandler<ChannelEventArgs>(this,"onChannelEvent",ChannelEventArgs.class));
            this.controller.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionStateChange",PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }*/

        if (!this.controller.isServerConnected()) {
            this.controller.Connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case OP_CODE_LOGIN:
                    if (resultCode == RESULT_OK) {
                        this.Logged();
                    } else {
                        Controller.DisposeRunningController();
                        this.finish();
                    }
                break;
            case OP_CODE_EXPLORE:
                    if (resultCode == RESULT_OK) {
                        Log.w("ExploreActionResult","Has to show hives...");
                    } else {
                        Log.w("ExploreActionResult","Don't move from here...");
                    }
                break;
        }
    }

    private void ConnectService() {
        if (StaticParameters.BackgroundService) {
            Context context = this.getApplicationContext();
            context.startService(new Intent(context, CHService.class)); //If not, then start it.}
        }
    }

    @Override
    public void onDestroy() {
        Controller.unbindApp();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected View.OnClickListener appIcon_ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (floatingPanel.isOpen())
                floatingPanel.close();
            else
                floatingPanel.openLeft();
    } };

    protected View.OnClickListener menuIcon_ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (floatingPanel.isOpen())
                floatingPanel.close();
            else
                floatingPanel.openRight();
        }
    };

    public void setPanelBehaviour() {
        floatingPanel = ((FloatingPanel)findViewById(R.id.FloatingPanel));

        ImageButton appIcon = (ImageButton)findViewById(R.id.appIcon);
        appIcon.setOnClickListener(this.appIcon_ClickListener);

        ImageButton menuIcon = (ImageButton)findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(this.menuIcon_ClickListener);
    }

    protected View.OnClickListener explore_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplicationContext(),Explore.class);
            startActivityForResult(intent, OP_CODE_EXPLORE);
        }
    };

    protected View.OnClickListener logout_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            controller.clearUserData();
            checkLogin();
        }
    };

    protected View.OnClickListener clear_chats_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            controller.clearAllChats();
        }
    };

    protected View.OnClickListener chat_sync_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataProvider dataProvider = DataProvider.GetDataProvider();
            dataProvider.InvokeServerCommand(ServerCommand.AvailableCommands.ChatList, null);
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                if ((ActiveLayoutID != R.layout.home)) { // Tell the framework to start tracking this event.
                    findViewById(R.id.mainCenter).getKeyDispatcherState().startTracking(event, this);
                    return true;
                }
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                findViewById(R.id.mainCenter).getKeyDispatcherState().handleUpEvent(event);
                if (event.isTracking() && !event.isCanceled()) {
                    if (ActiveLayoutID == R.layout.main_panel_chat_layout) {
                        this.controller.Leave((String) findViewById(R.id.main_panel_chat_name).getTag());
                    }
                    if (ActiveLayoutID != R.layout.home) {
                        ShowLayout(R.layout.home,R.layout.action_bar_layout);
                        this.setPanelBehaviour();
                        return true;
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }




}
