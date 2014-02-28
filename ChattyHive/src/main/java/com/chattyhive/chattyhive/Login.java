package com.chattyhive.chattyhive;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.LinearLayout;

public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        setTabButtonsBehaviour();

    }

    protected void setTabButtonsBehaviour() {
        final Button signin = (Button)findViewById(R.id.login_activity_signin_tab_button);
        final Button loggin = (Button)findViewById(R.id.login_activity_login_tab_button);
        final LinearLayout master_frame = (LinearLayout)findViewById(R.id.login_activity_master_frame);
        signin.setSelected(true);

        View.OnClickListener tab_button_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signin.isSelected()) {
                    master_frame.destroyDrawingCache();
                    loggin.setSelected(true);
                    signin.setSelected(false);
                } else {
                    master_frame.destroyDrawingCache();
                    signin.setSelected(true);
                    loggin.setSelected(false);
                }
            }
        };

        signin.setOnClickListener(tab_button_listener);
        loggin.setOnClickListener(tab_button_listener);
    }
}
