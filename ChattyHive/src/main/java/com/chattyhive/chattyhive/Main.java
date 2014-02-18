package com.chattyhive.chattyhive;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        FrameLayout main_block = (FrameLayout)findViewById(R.id.main_block);
        FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();

        int left_margin = (int)Math.ceil(-1 * getResources().getDimension(R.dimen.chat_list_width));

        main_block_params.setMargins(left_margin, main_block_params.topMargin,main_block_params.rightMargin,main_block_params.bottomMargin);
        main_block.setLayoutParams(main_block_params);

        ImageButton appIcon = (ImageButton)findViewById(R.id.appIcon);
        appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FrameLayout main_block = (FrameLayout)findViewById(R.id.main_block);
                final FrameLayout.LayoutParams main_block_params = (FrameLayout.LayoutParams) main_block.getLayoutParams();
                final int translate, new_margin_left, new_margin_right;

                int main_block_left_margin = main_block_params.leftMargin;
                int main_block_right_margin = main_block_params.rightMargin;

                if (main_block_left_margin == 0) {
                    translate = main_block_right_margin;
                    new_margin_left = main_block_right_margin;
                    new_margin_right = 0;
                } else {
                    translate = -1*main_block_left_margin;
                    new_margin_left = 0;
                    new_margin_right = main_block_left_margin;
                }

                Animation animation = new TranslateAnimation(0, translate,0, 0);
                animation.setDuration(600);
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
        });
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
}
