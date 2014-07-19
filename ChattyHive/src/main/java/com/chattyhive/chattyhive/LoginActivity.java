package com.chattyhive.chattyhive;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.MessageLocalStorage;

public class LoginActivity extends Activity {

    private String username;
    private String password;
    private Boolean connecting;
    private DataProvider dataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        setTabButtonsBehaviour();

        findViewById(R.id.login_activity_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        connecting = false;

        try {
            dataProvider = DataProvider.GetDataProvider();
            dataProvider.ServerConnectionStateChanged.add(new EventHandler<ConnectionEventArgs>(this,"onServerConnectionStateChanged",ConnectionEventArgs.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    protected void setTabButtonsBehaviour() {
        final Button signin = (Button)findViewById(R.id.login_activity_signup_tab_button);
        final Button loggin = (Button)findViewById(R.id.login_activity_login_tab_button);
        //final LinearLayout master_frame = (LinearLayout)findViewById(R.id.login_activity_master_frame);
        final ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        signin.setSelected(true);

        LayoutInflater inflater = getLayoutInflater();
        viewSwitcher.addView(inflater.inflate(R.layout.login_activity_sign_up,null));
        viewSwitcher.addView(inflater.inflate(R.layout.login_activity_login,null));


        // TODO: Try this changing background instead of using selection.
        View.OnClickListener tab_button_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signin.isSelected()) {
                    loggin.setSelected(true);
                    signin.setSelected(false);
                    viewSwitcher.showNext();

                } else {
                    signin.setSelected(true);
                    loggin.setSelected(false);
                    viewSwitcher.showPrevious();
                }
            }
        };

        signin.setOnClickListener(tab_button_listener);
        loggin.setOnClickListener(tab_button_listener);
    }



    public void attemptLogin() {
        if (connecting) return;

        //Get the views
        TextView usernameView = ((TextView)findViewById(R.id.login_activity_login_username));
        TextView passwordView = ((TextView)findViewById(R.id.login_activity_login_password));

        // Store values at the time of the login attempt.
        username = usernameView.getText().toString();
        password = passwordView.getText().toString();

        //mServer = servers.get(mServerView.getSelectedItemPosition());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError("This field can not be blank.");
            focusView = passwordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameView.setError("This field can not be blank.");
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            //showProgress(true);
            dataProvider.setUser(new ServerUser(username,password));
            dataProvider.Connect();
        }
    }

    public void onServerConnectionStateChanged(Object sender,ConnectionEventArgs eventArgs) {
        connecting = false;
        //First hide animation
        if (eventArgs.getConnected()) {
            setResult(RESULT_OK);
            finish();
        } else {
            //Show some kind of error
            TextView usernameView = ((TextView)findViewById(R.id.login_activity_login_username));
            usernameView.setError("Unknown error while connecting to server.");
            usernameView.requestFocus();
        }
    }
}
