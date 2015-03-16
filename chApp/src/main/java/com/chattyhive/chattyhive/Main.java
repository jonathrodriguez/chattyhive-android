package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.chattyhive.Core.Controller;
import com.chattyhive.Core.StaticParameters;

import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Chats.Hive;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.DataProvider;
import com.chattyhive.chattyhive.framework.OSStorageProvider.ChatLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.CookieStore;
import com.chattyhive.chattyhive.framework.OSStorageProvider.HiveLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.MessageLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.UserLocalStorage;

import com.chattyhive.chattyhive.backgroundservice.CHService;

import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.FloatingPanel;
import com.chattyhive.chattyhive.framework.Util.ViewPair;
import com.chattyhive.chattyhive.framework.Util.Keyboard;

import java.util.HashMap;
import java.util.Map;


public class Main extends Activity {
    static final int OP_CODE_LOGIN = 1;
    static final int OP_CODE_EXPLORE = 2;
    static final int OP_CODE_NEW_HIVE = 3;

    FloatingPanel floatingPanel;

    Controller controller;

    Home home;

    LeftPanel leftPanel;
    RightPanel2 rightPanel;

    HashMap <Integer, Window> viewStack;
    int lastOpenHierarchyLevel;

    void OpenWindow(Window window) {
        //Log.w("Main", "OpenWindow(Window).Start");
        OpenWindow(window,window.getHierarchyLevel());
        //Log.w("Main", "OpenWindow(Window).End");
    }
    void OpenWindow(Window window,Integer hierarchyLevel) {
        //Log.w("Main", "OpenWindow(Window,Integer).Start");
        if (hierarchyLevel > (this.lastOpenHierarchyLevel+1))
            throw new IllegalArgumentException("Expected at most one level over the last open hierarchy level");
        //Log.w("Main", "HideKeyboard");
        Keyboard.HideKeyboard(this);
        //Log.w("Main", "Close/Hide other windows.");
        if (this.lastOpenHierarchyLevel > -1) {
            if (hierarchyLevel < this.lastOpenHierarchyLevel) {
                for (int i = this.lastOpenHierarchyLevel; i > hierarchyLevel; i--) {
                    this.viewStack.get(i).Close();
                    this.viewStack.remove(i);
                }
            } else if (hierarchyLevel == this.lastOpenHierarchyLevel) {
                this.viewStack.get(this.lastOpenHierarchyLevel).Close();
            } else if (hierarchyLevel > this.lastOpenHierarchyLevel) {
                this.viewStack.get(this.lastOpenHierarchyLevel).Hide();
            }
        }
        //Log.w("Main", "Adjust hierarchy level.");
        if (hierarchyLevel != window.getHierarchyLevel())
            window.setHierarchyLevel(hierarchyLevel);
        //Log.w("Main", "Put window in viewStack.");
        this.viewStack.put(hierarchyLevel,window);
        //Log.w("Main", "Remember hierarchy level.");
        this.lastOpenHierarchyLevel = hierarchyLevel;
        //Log.w("Main", "Set context if needed.");
        if ((!window.hasContext()) || (window.context != this))
            window.setContext(this);
        //Log.w("Main", "Open window.");
        window.Open();
        //Log.w("Main", "OpenWindow(Window,Integer).End");
    }

    void Close() {
        Keyboard.HideKeyboard(this);

        if (this.lastOpenHierarchyLevel >= 0) {
            this.viewStack.get(this.lastOpenHierarchyLevel).Close();
            this.viewStack.remove(this.lastOpenHierarchyLevel);
        }

        this.lastOpenHierarchyLevel--;

        if (this.lastOpenHierarchyLevel >= 0)
            this.viewStack.get(this.lastOpenHierarchyLevel).Show();

    }

    protected ViewPair ShowLayout (int layoutID, int actionBarID) {
        //Log.w("Main", "ShowLayout(Integer,Integer).Start");
        FrameLayout mainPanel = ((FrameLayout)findViewById(R.id.mainCenter));
        FrameLayout mainActionBar = ((FrameLayout)findViewById(R.id.actionCenter));
        //Log.w("Main", String.format("mainPanel captured: %b. mainActionBar captured: %b.",(mainPanel!=null),(mainActionBar!=null)));
        View actionBar = null;
        View mainView = null;
        //Log.w("Main", "process main panel if available");
        if (mainPanel != null) {
            //Log.w("Main", "remove main panel views");
            mainPanel.removeAllViews();
            //Log.w("Main", "inflate main layout");
            mainView = LayoutInflater.from(this).inflate(layoutID, mainPanel, true);
        }
        //Log.w("Main", "process action bar if available");
        if (mainActionBar != null) {
            //Log.w("Main", "remove action bar views");
            mainActionBar.removeAllViews();
            //Log.w("Main", "inflate action bar layout");
            actionBar = LayoutInflater.from(this).inflate(actionBarID,mainActionBar,true);
        }
        //Log.w("Main", "prepare result.");
        ViewPair actualView = new ViewPair(mainView,actionBar);
        //Log.w("Main", "ShowLayout(Integer,Integer).End");
        return actualView;
    }
    protected View ChangeActionBar (int actionBarID) {
        FrameLayout mainActionBar = ((FrameLayout)findViewById(R.id.actionCenter));
        mainActionBar.removeAllViews();
        View actionBar = LayoutInflater.from(this).inflate(actionBarID,mainActionBar,true);

        return actionBar;
    }

    protected void ShowHome() {
        if (this.home == null)
            this.home = new Home(this);
        else if (!this.home.hasContext())
            this.home.setContext(this);

        OpenWindow(this.home);
    }

    protected void ShowChats() {
        this.leftPanel.OpenChats();
        if (floatingPanel.isOpen())
            floatingPanel.openLeft(0);
        else
            floatingPanel.openLeft();
    }

    protected void ShowHives() {
        this.leftPanel.OpenHives();
        floatingPanel.openLeft(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Log.w("Main","onCreate..."); //DEBUG
        Object[] LocalStorage = {LoginLocalStorage.getLoginLocalStorage(), ChatLocalStorage.getGroupLocalStorage(), HiveLocalStorage.getHiveLocalStorage(), MessageLocalStorage.getMessageLocalStorage(), UserLocalStorage.getUserLocalStorage()};
        Controller.Initialize(new CookieStore(),LocalStorage);

        this.controller = Controller.GetRunningController(com.chattyhive.chattyhive.framework.OSStorageProvider.LocalStorage.getLocalStorage());

        this.viewStack = new HashMap<Integer, Window>();
        this.lastOpenHierarchyLevel = -1;

        this.leftPanel = new LeftPanel(this);

        if (savedInstanceState == null)
            this.ShowHome();

        this.rightPanel = new RightPanel2(this);

        try {
            Controller.bindApp(this.getClass().getMethod("hasToLogin"),this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        this.ConnectService();

        if (savedInstanceState != null)
            Restore(savedInstanceState);
    }

    private void Restore(Bundle savedInstanceState) {
        int lastOpenHierarchyLevel = savedInstanceState.getInt("lastOpenHierarchyLevel");

        if (lastOpenHierarchyLevel >= 0) {
            this.home = ((Home)savedInstanceState.getSerializable("viewStackEntry_0"));
            OpenWindow(this.home,0);
        }

        for (int i = 1; i <= lastOpenHierarchyLevel; i++)
            OpenWindow((Window)savedInstanceState.getSerializable(String.format("viewStackEntry_%d",i)),i);
    }

    public void hasToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, OP_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case OP_CODE_LOGIN:
                if (floatingPanel.isOpen())
                    floatingPanel.close(0);
                if (resultCode != RESULT_OK) {
                    Controller.DisposeRunningController();
                    this.finish();
                }
                break;
            case OP_CODE_EXPLORE:
                if (floatingPanel.isOpen())
                    floatingPanel.close(0);
                if (resultCode == RESULT_OK) {
                    String nameURL = null;
                    if ((data != null) && (data.hasExtra("NameURL")))
                        nameURL = data.getStringExtra("NameURL");

                    if ((nameURL != null) && (!nameURL.isEmpty())) {
                        Hive h = Hive.getHive(nameURL);
                        Chat c = null;
                        if (h != null)
                            c = h.getPublicChat();

                        if (c != null) {
                            this.OpenWindow(new MainChat(this, c));
                        }
                        else
                            this.ShowHives();
                    } else
                        this.ShowHives();
                }
                break;
            case OP_CODE_NEW_HIVE:
                if (floatingPanel.isOpen())
                    floatingPanel.close(0);
                if(resultCode == RESULT_OK){
                        this.ShowHives();
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

            /*if (floatingPanel.isOpen())
                floatingPanel.close(0);*/
        }
    };

    protected View.OnClickListener new_hive_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),NewHive.class);
            startActivityForResult(intent,OP_CODE_NEW_HIVE);

            /*if (floatingPanel.isOpen())
                floatingPanel.close(0);*/
        }
    };

    protected View.OnClickListener logout_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            controller.clearUserData();
            controller.clearAllChats();

            hasToLogin();
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
            dataProvider.InvokeServerCommand(AvailableCommands.ChatList, null);
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((this.lastOpenHierarchyLevel > 0) && (!floatingPanel.isOpen())) { // Tell the framework to start tracking this event.
                    findViewById(R.id.mainCenter).getKeyDispatcherState().startTracking(event, this);
                    return true;
                }
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                findViewById(R.id.mainCenter).getKeyDispatcherState().handleUpEvent(event);
                if (event.isTracking() && !event.isCanceled() && (!floatingPanel.isOpen()) && (this.lastOpenHierarchyLevel > 0)) {
                    this.Close();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putSerializable("Home",home);
        outState.putInt("lastOpenHierarchyLevel",lastOpenHierarchyLevel);
        for (Map.Entry<Integer,Window> viewStackEntry : viewStack.entrySet())
            outState.putSerializable(String.format("viewStackEntry_%d",viewStackEntry.getKey()),viewStackEntry.getValue());
    }


}
