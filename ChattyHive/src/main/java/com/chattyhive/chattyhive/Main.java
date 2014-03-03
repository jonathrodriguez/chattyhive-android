package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.contentprovider.server.ServerStatus;


public class Main extends Activity implements GestureDetector.OnGestureListener {
    static final int OP_CODE_LOGIN = 1;

    GestureDetector _detector;
    MotionEvent _lastOnScrollMotionEvent = null;
    MotionEvent _firstOnScrollMotionEvent = null;
    Boolean _performScroll = false;

    Controller _controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        setPanelBehaviour();

        this._controller = Controller.getRunningController();
        Controller.bindApp();

        if ((this._controller == null) || (this._controller.getServerUser() == null) ||
                (this._controller.getServerUser().getLogin().isEmpty())) {
            this.hasToLogin();
        } else {
            this.Logged();
        }

    }

    private void hasToLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivityForResult(intent, OP_CODE_LOGIN);
    }

    private void Logged () {
/*        try {
            this._controller.SubscribeChannelEventHandler(new EventHandler<ChannelEventArgs>(this,"onChannelEvent",ChannelEventArgs.class));
            this._controller.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionStateChange",PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }*/

        if (this._controller.getServerUser().getStatus() != ServerStatus.LOGGED) {
            this._controller.Connect();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OP_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                this.Logged();
            } /*else {
                Controller.disposeRunningController();
                this.finish();
            }*/
        }
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


    public void setPanelBehaviour() {
        this._detector = new GestureDetector(this,this);

        LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
        FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();

        int left_margin = (int)Math.ceil(-1 * getResources().getDimension(R.dimen.chat_list_width));

        main_block_params.setMargins(left_margin, main_block_params.topMargin,main_block_params.rightMargin,main_block_params.bottomMargin);
        main_block.setLayoutParams(main_block_params);

        ImageButton appIcon = (ImageButton)findViewById(R.id.appIcon);
        appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
                final FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();
                final int translate, new_left_margin, new_right_margin;

                int main_block_left_margin = main_block_params.leftMargin;
                int main_block_right_margin = main_block_params.rightMargin;

                int distance = (int)getResources().getDimension(R.dimen.chat_list_width);

                if (main_block_right_margin < 0) {
                    translate = main_block_right_margin;
                    new_left_margin = -1 * distance;
                    new_right_margin = 0;
                } else {
                    translate = -1*main_block_left_margin;
                    new_left_margin = 0;
                    new_right_margin = -1 * distance;
                }



                movePanel(main_block,main_block_params,translate,new_left_margin,new_right_margin,250);
            }
        });

        ImageButton menuIcon = (ImageButton)findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
                final FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();
                final int translate, new_left_margin, new_right_margin;

                int main_block_left_margin = main_block_params.leftMargin;
                int main_block_right_margin = main_block_params.rightMargin;

                int distance = (int)getResources().getDimension(R.dimen.right_menu_width);
                int zero_left = (int)getResources().getDimension(R.dimen.chat_list_width);
                if (main_block_right_margin > 0) {
                    translate = main_block_right_margin;
                    new_left_margin = -1 * zero_left;
                    new_right_margin = 0;
                } else {
                    translate = -1 * (distance - main_block_right_margin);
                    new_left_margin = -1*zero_left - distance;
                    new_right_margin = distance;
                }



                movePanel(main_block,main_block_params,translate,new_left_margin,new_right_margin,250);
            }
        });
    }

    public void movePanel(final LinearLayout main_block, final FrameLayout.LayoutParams main_block_params, final int translate, final int new_margin_left, final int new_margin_right,long duration) {

        Animation animation = new TranslateAnimation(0, translate,0, 0);
        animation.setDuration(duration);
        animation.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                main_block_params.setMargins(new_margin_left,main_block_params.topMargin,new_margin_right,main_block_params.bottomMargin);
                main_block.destroyDrawingCache();
                main_block.setLayoutParams(main_block_params);
                main_block.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        main_block.startAnimation(animation);
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

        LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
        FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();

        if (!_performScroll) {
            int actual_margin_right = main_block_params.rightMargin;

            if (actual_margin_right < 0) { // left panel is visible
                if (e1.getX() < -1*actual_margin_right) return false;
            } else if (actual_margin_right > 0) { // right panel is visible
                DisplayMetrics metrics = new DisplayMetrics();
                this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int screen_width = metrics.widthPixels;
                Log.w("onScroll","Screen width: ".concat(String.valueOf(screen_width)));
                if (e1.getX() > (screen_width-actual_margin_right)) return false;
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

        int max_scroll_right = (int)getResources().getDimension(R.dimen.chat_list_width);
        int max_scroll_left = (int)getResources().getDimension(R.dimen.right_menu_width);

        int margin_left = Math.round(main_block_params.leftMargin - distanceX);
        int margin_right = Math.round(main_block_params.rightMargin + distanceX);

        if (margin_right > max_scroll_left) {
            margin_right = max_scroll_left;
            margin_left = -1 * (max_scroll_left+max_scroll_right);
            //Log.w("onScroll", "Movement locked at left side.");
        } else if (margin_left > 0) {
            margin_right = -1*max_scroll_right;
            margin_left = 0;
            //Log.w("onScroll", "Movement locked at right side.");
        }

        //Log.w("onScroll","Previous left:  ".concat(String.valueOf(main_block_params.leftMargin)).concat(" New left:  ").concat(String.valueOf(margin_left)));
        //Log.w("onScroll","Previous right: ".concat(String.valueOf(main_block_params.rightMargin)).concat(" New right: ").concat(String.valueOf(margin_right)));
        //Log.w("onScroll","Distance X: ".concat(String.valueOf(distanceX)));

        main_block_params.setMargins(margin_left, main_block_params.topMargin, margin_right, main_block_params.bottomMargin);
        main_block.setLayoutParams(main_block_params);

        if (e2.getAction() == MotionEvent.ACTION_UP) {
            _performScroll = false;
            _lastOnScrollMotionEvent = null;
            _firstOnScrollMotionEvent = null;

            int chat_list_width = (int)getResources().getDimension(R.dimen.chat_list_width);
            int right_menu_width = (int)getResources().getDimension(R.dimen.right_menu_width);
            int translate, new_margin_right, new_margin_left;

            if ((margin_right >= (-0.5*chat_list_width)) && (margin_right <= (0.5*right_menu_width))) { // Show center panel (main window)
                new_margin_right = 0;
            } else if (margin_right > (0.5*right_menu_width)) { // Show right panel (menu)
                new_margin_right = right_menu_width;
            } else { // Show left panel (chat list)
                new_margin_right = -1*chat_list_width;
            }
            translate = margin_right - new_margin_right;
            new_margin_left = margin_left + translate;

            movePanel(main_block,main_block_params,translate,new_margin_left,new_margin_right,250);
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

        Log.w("onFling","onFlingDetected!");

        float finger_distance = e1.getX() - e2.getX();

        LinearLayout main_block = (LinearLayout)findViewById(R.id.main_block);
        FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();

        int margin_right = main_block_params.rightMargin;
        int margin_left = main_block_params.leftMargin;

        int origin_right = Math.round(margin_right - finger_distance);

        if (origin_right < 0) { // left panel is visible
            if (e1.getX() < -1*origin_right) return false;
        } else if (origin_right > 0) { // right panel is visible
            DisplayMetrics metrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int screen_width = metrics.widthPixels;
            if (e1.getX() > (screen_width-origin_right)) return false;
        }

        int chat_list_width = (int)getResources().getDimension(R.dimen.chat_list_width);
        int right_menu_width = (int)getResources().getDimension(R.dimen.right_menu_width);

        int translate, new_margin_right, new_margin_left;


        if ((origin_right >= (-0.5*chat_list_width)) && (origin_right <= (0.5*right_menu_width))) {
            if ((velocityX > 0) && (margin_right < 0)) { // Show left panel (chat list)
                new_margin_right = -1*chat_list_width;
            } else if ((velocityX < 0) && (margin_right > 0)) { // Show right panel (menu)
                new_margin_right = right_menu_width;
            } else { // Ensure that main panel is shown
                new_margin_right = 0;
            }
        } else if ((origin_right > (0.5*right_menu_width)) && (velocityX > 0)) { // Show center panel (main)
            new_margin_right = 0;
        } else if ((origin_right < (-0.5*chat_list_width)) && (velocityX < 0)) { // Show center panel (main)
            new_margin_right = 0;
        } else return false;

        translate = margin_right - new_margin_right;
        new_margin_left = margin_left + translate;

        if (_performScroll) {
            _performScroll = false;
            _lastOnScrollMotionEvent = null;
            _firstOnScrollMotionEvent = null;
        }

        movePanel(main_block,main_block_params,translate,new_margin_left,new_margin_right,100);

        return true;
    }
}
