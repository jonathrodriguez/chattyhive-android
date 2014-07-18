package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.contentprovider.server.ServerStatus;
import com.chattyhive.chattyhive.OSStorageProvider.CookieStore;
import com.chattyhive.chattyhive.OSStorageProvider.GroupLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.HiveLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.MessageLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.UserLocalStorage;
import com.chattyhive.chattyhive.backgroundservice.CHService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Main extends Activity implements GestureDetector.OnGestureListener {
    static final int OP_CODE_LOGIN = 1;
    static final int OP_CODE_EXPLORE = 2;

    GestureDetector _detector;
    MotionEvent _lastOnScrollMotionEvent = null;
    MotionEvent _firstOnScrollMotionEvent = null;
    Boolean _performScroll = false;

    Controller _controller;

    int mainPanelOffset;
    int leftPanelWidth;
    int rightPanelWidth;

    int ActiveLayoutID;

    Method updatePositionMethod = null;

    protected void retrieveParameters() {
        mainPanelOffset = 0;
        leftPanelWidth = Math.round(getResources().getDimension(R.dimen.chat_list_width));
        rightPanelWidth = Math.round(getResources().getDimension(R.dimen.right_menu_width));
        //Log.w("Main.retrieveParameters()","Parameters retrieved.");
    }

    public void updatePositionMargin() {
        LinearLayout mainBlock = (LinearLayout)findViewById(R.id.main_block);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mainBlock.getLayoutParams();
        params.setMargins(mainPanelOffset-leftPanelWidth,params.topMargin,-1*mainPanelOffset,params.bottomMargin);
        mainBlock.destroyDrawingCache();
        mainBlock.setLayoutParams(params);
//        Log.w("Main.updatePosition","Using updatePositionMargin()");
    }

    public void updatePositionPadding() {
        LinearLayout mainBlock = (LinearLayout)findViewById(R.id.main_block);
        mainBlock.destroyDrawingCache();
        mainBlock.setPadding(mainPanelOffset,mainBlock.getPaddingTop(),-1*mainPanelOffset,mainBlock.getPaddingBottom());
//        Log.w("Main.updatePosition","Using updatePositionPadding()");
    }

    protected void updatePosition(int offset) {
        //findViewById(R.id.main_block).offsetLeftAndRight(offset);
        /*LinearLayout mainBlock = (LinearLayout)findViewById(R.id.main_block);*/
        mainPanelOffset += offset;
        /*FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mainBlock.getLayoutParams();
        params.setMargins(mainPanelOffset-leftPanelWidth,params.topMargin,-1*mainPanelOffset,params.bottomMargin);
        //findViewById(R.id.main_block).setLayoutParams(params);
        FrameLayout mainBlockParent = ((FrameLayout)mainBlock.getParent());
        mainBlockParent.removeView(mainBlock);
        mainBlockParent.addView(mainBlock,params);*/
        try {
            if (updatePositionMethod == null) {
                LinearLayout mainBlock = (LinearLayout) findViewById(R.id.main_block);

                //Log.w("Main.updatePosition",String.format("[BEFORE] mainBlock..leftMargin = %d",((ViewGroup.MarginLayoutParams)mainBlock.getLayoutParams()).leftMargin));
                updatePositionMargin();
                int margin = ((ViewGroup.MarginLayoutParams)mainBlock.getLayoutParams()).leftMargin;
                //Log.w("Main.updatePosition",String.format("[AFTER] mainBlock.leftMargin = %d",margin));

                if (margin != (mainPanelOffset - leftPanelWidth)) {
                    updatePositionPadding();
                    this.updatePositionMethod = this.getClass().getMethod("updatePositionPadding");
                } else {
                    this.updatePositionMethod = this.getClass().getMethod("updatePositionMargin");
                }
            } else {
                updatePositionMethod.invoke(this);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected View ShowLayout (int layoutID) {
        FrameLayout mainPanel = ((FrameLayout)findViewById(R.id.main_panel));
        mainPanel.removeAllViews();
        ActiveLayoutID = layoutID;
        return LayoutInflater.from(this).inflate(layoutID, mainPanel, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveLayoutID = R.layout.main;
        setContentView(R.layout.main);


        findViewById(R.id.temp_explore_button).setOnClickListener(this.explore_button_click);
        findViewById(R.id.temp_profile_button).setOnClickListener((new Profile(this)).open_profile);
        findViewById(R.id.temp_logout_button).setOnClickListener(this.logout_button_click);
        findViewById(R.id.temp_clear_chats_button).setOnClickListener(this.clear_chats_button_click);
        retrieveParameters();
        setPanelBehaviour();

        //Log.w("Main","onCreate..."); //DEBUG
        Object[] LocalStorage = {LoginLocalStorage.getLoginLocalStorage(), GroupLocalStorage.getGroupLocalStorage(), HiveLocalStorage.getHiveLocalStorage(), MessageLocalStorage.getMessageLocalStorage(), UserLocalStorage.getUserLocalStorage()};
        Controller.Initialize(new CookieStore(),LocalStorage);

        this._controller = Controller.GetRunningController(true);
        Controller.bindApp();

        LeftPanel lp = new LeftPanel(this);

        this.ConnectService();

        this.checkLogin();

    }

    private void checkLogin() {
        if ((this._controller == null) || (LoginLocalStorage.getLoginLocalStorage().RecoverLoginPassword() == null)) {
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
            this._controller.SubscribeChannelEventHandler(new EventHandler<ChannelEventArgs>(this,"onChannelEvent",ChannelEventArgs.class));
            this._controller.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionStateChange",PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }*/

        if (!this._controller.isServerConnected()) {
            this._controller.Connect();
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
    public boolean dispatchTouchEvent(MotionEvent me){
        if (!this._detector.onTouchEvent(me)) {
            if ((_performScroll) && (me.getAction() == MotionEvent.ACTION_UP)) {
                float distanceX, distanceY;
                distanceX = _lastOnScrollMotionEvent.getX() - me.getX();
                distanceY = _lastOnScrollMotionEvent.getY() - me.getY();
                return this.onScroll(_firstOnScrollMotionEvent,me,distanceX,distanceY);
            } else if ((_lastOnScrollMotionEvent != null) && (me.getAction() == MotionEvent.ACTION_UP)) {
                _lastOnScrollMotionEvent = null;
                return super.dispatchTouchEvent(me);
            }
            else {
                return super.dispatchTouchEvent(me);
            }
        }
        return true;
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

            //Log.w("Main.appIcon_ClickListener()","AppIcon clicked");

            final LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
            final int translate;

            int distance = leftPanelWidth;

            if (mainPanelOffset > 0) { //If leftPanel is visible then hide it.
                translate = -1*mainPanelOffset;
            } else { //Ensure leftPanel will be visible.
                translate = distance-mainPanelOffset;
            }

            movePanel(main_block,translate,250);
    } };

    protected View.OnClickListener menuIcon_ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Log.w("Main.menuIcon_ClickListener()","MenuIcon clicked");

            final LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
            final int translate;

            int distance = rightPanelWidth;

            if (mainPanelOffset < 0) { //If rightPanel is visible then hide it.
                translate = -1*mainPanelOffset;
            } else { //Ensure rightPanel will be visible.
                translate = -1*(distance+mainPanelOffset);
            }

            movePanel(main_block,translate,250);
        }
    };

    public void setPanelBehaviour() {
        this._detector = new GestureDetector(this,this);

        LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
        //FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();

        int left_padding = 0;//(int)Math.ceil(-1 * getResources().getDimension(R.dimen.chat_list_width));

        //main_block_params.setMargins(left_padding, main_block_params.topMargin,main_block_params.rightMargin,main_block_params.bottomMargin);
        //main_block.setLayoutParams(main_block_params);
        //main_block.destroyDrawingCache();
        //main_block.setPadding(left_padding,0,0,0);
        //main_block.requestLayout();
        updatePosition(-1* mainPanelOffset);

        ImageButton appIcon = (ImageButton)findViewById(R.id.appIcon);
        appIcon.setOnClickListener(this.appIcon_ClickListener);

        ImageButton menuIcon = (ImageButton)findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(this.menuIcon_ClickListener);

        //Log.w("Main.setPanelBehaviour()","Panel behaviour set.");
    }



    public void movePanel(final LinearLayout main_block, final int translate,long duration) {

        Animation animation = new TranslateAnimation(0, translate,0, 0);
        animation.setDuration(duration);
        animation.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //int new_left_padding = new_padding_left-main_block_params.leftMargin;
                //int new_right_padding = new_padding_right;
                //main_block_params.setMargins(new_padding_left, main_block_params.topMargin, new_padding_right, main_block_params.bottomMargin);

                //main_block.destroyDrawingCache();
                //main_block.setLayoutParams(main_block_params);
                //main_block.setPadding(new_padding_left, 0, new_padding_right, 0);
                //main_block.requestLayout();

                updatePosition(translate);

                main_block.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        main_block.startAnimation(animation);
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
            _controller.clearUserData();
            checkLogin();
        }
    };

    protected View.OnClickListener clear_chats_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            _controller.clearAllChats();
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                if ((ActiveLayoutID != R.layout.main) || (mainPanelOffset != 0)) { // Tell the framework to start tracking this event.
                    findViewById(R.id.main_block).getKeyDispatcherState().startTracking(event, this);
                    return true;
                }
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                findViewById(R.id.main_block).getKeyDispatcherState().handleUpEvent(event);
                if (event.isTracking() && !event.isCanceled()) {
                    if (ActiveLayoutID == R.layout.main_panel_chat_layout) {
                        this._controller.Leave((String)findViewById(R.id.main_panel_chat_name).getTag());
                    }
                    if (ActiveLayoutID != R.layout.main) {
                        ShowLayout(R.layout.main);
                        this.setPanelBehaviour();
                        return true;
                    } else if (mainPanelOffset != 0) {
                        movePanel((LinearLayout)findViewById(R.id.main_block),-1*mainPanelOffset,250);
                        return true;
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }



    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        //LinearLayout mainBlock = (LinearLayout)findViewById(R.id.main_block);

        if (!_performScroll) {
            if (mainPanelOffset > 0) { // leftPanel is visible
                if (e1.getX() < leftPanelWidth) return false;
            } else if (mainPanelOffset < 0) { // rightPanel is visible
                DisplayMetrics metrics = new DisplayMetrics();
                this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int screen_width = metrics.widthPixels;
                if (e1.getX() > (screen_width- rightPanelWidth)) return false;
            }
        }

        if (_lastOnScrollMotionEvent == null) {
            _lastOnScrollMotionEvent = e2;
            if (Math.abs(distanceY) < Math.abs(distanceX)) {
                _performScroll = true;
                _firstOnScrollMotionEvent = e1;
            }
        }

        if (!_performScroll) {
            return false;
        }

        _lastOnScrollMotionEvent = e2;

        int max_scroll_right = leftPanelWidth;
        int max_scroll_left = -1* rightPanelWidth;

        int offset = Math.round(-1*distanceX);

        if (mainPanelOffset+offset < max_scroll_left) {
            offset = max_scroll_left-mainPanelOffset;
        } else if (mainPanelOffset+offset > max_scroll_right) {
            offset = max_scroll_right-mainPanelOffset;
        }

        //movePanel((LinearLayout)findViewById(R.id.main_block),offset,0);
        updatePosition(offset);

        if (e2.getAction() == MotionEvent.ACTION_UP) {
            _performScroll = false;
            _lastOnScrollMotionEvent = null;
            _firstOnScrollMotionEvent = null;

            int chat_list_width =leftPanelWidth;
            int right_menu_width = rightPanelWidth;
            int translate;

            if ((mainPanelOffset <= (0.5*chat_list_width)) && (mainPanelOffset >= (-0.5*right_menu_width))) { // Show center panel (main window)
                translate = -1*mainPanelOffset;
            } else if (mainPanelOffset < (-0.5*right_menu_width)) { // Show right panel (menu)
                translate = (-1*right_menu_width)-mainPanelOffset;
            } else { // Show left panel (chat list)
                translate = chat_list_width-mainPanelOffset;
            }

            movePanel((LinearLayout)findViewById(R.id.main_block),translate,250);
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if ((e2.getAction() != MotionEvent.ACTION_UP) || (Math.abs(velocityX) < Math.abs(velocityY))) return false;

        if (Math.abs(velocityX) < 200) return false;

        //Log.w("onFling","onFlingDetected!");

        float finger_distance = e1.getX() - e2.getX();

        //LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
        //FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();

        int origin = Math.round(mainPanelOffset + finger_distance);


        if (origin > (0.5*leftPanelWidth)) { // leftPanel is visible
            if (e1.getX() < leftPanelWidth) return false;
        } else if (origin < (-0.5* rightPanelWidth)) { // rightPanel is visible
            DisplayMetrics metrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int screen_width = metrics.widthPixels;
            if (e1.getX() > (screen_width- rightPanelWidth)) return false;
        }

        int chat_list_width = leftPanelWidth;
        int right_menu_width = rightPanelWidth;

        int translate;

        if ((origin <= (0.5*chat_list_width)) && (origin >= (-0.5*right_menu_width))) { // centerPanel is visible
            if ((velocityX > 0) && (mainPanelOffset > 0)) { //Show leftPanel
                translate = chat_list_width - mainPanelOffset;
            } else if ((velocityX < 0) && (mainPanelOffset < 0)) { //Show rightPanel
                translate = (-1*right_menu_width)-mainPanelOffset;
            } else { //Ensure main panel is shown
                translate = -1*mainPanelOffset;
            }
        } else if (((origin < (-0.5*right_menu_width)) && (velocityX > 0)) ||
                   ((origin > ( 0.5* chat_list_width)) && (velocityX < 0))) { // Show mainPanel
            translate = -1*mainPanelOffset;
        } else return false;

        if (_performScroll) {
            _performScroll = false;
            _lastOnScrollMotionEvent = null;
            _firstOnScrollMotionEvent = null;
        }

       movePanel((LinearLayout)findViewById(R.id.main_block),translate,100);

        return true;
    }
}
