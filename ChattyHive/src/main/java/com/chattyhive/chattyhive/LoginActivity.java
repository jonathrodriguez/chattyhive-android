package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Intent;
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
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.EventHandler;

public class LoginActivity extends Activity {
    static final int OP_CODE_REGISTER = 3;

    private Boolean connecting;
    private DataProvider dataProvider;
    private Controller controller;

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

        findViewById(R.id.login_activity_signup_go_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmail();
            }
        });

        connecting = false;

        try {
            dataProvider = DataProvider.GetDataProvider();
            controller = Controller.GetRunningController();
            dataProvider.ServerConnectionStateChanged.add(new EventHandler<ConnectionEventArgs>(this,"onServerConnectionStateChanged",ConnectionEventArgs.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    protected void setTabButtonsBehaviour() {
        final Button signup = (Button)findViewById(R.id.login_activity_signup_tab_button);
        final Button loggin = (Button)findViewById(R.id.login_activity_login_tab_button);
        //final LinearLayout master_frame = (LinearLayout)findViewById(R.id.login_activity_master_frame);
        final ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        signup.setSelected(true);
        signup.setTextColor(getResources().getColor(R.color.login_tab_panel_selected_button_text_color));

        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.login_activity_sign_up, viewSwitcher);
        inflater.inflate(R.layout.login_activity_login, viewSwitcher);


        // TODO: Try this changing background instead of using selection.
        View.OnClickListener tab_button_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (signup.isSelected()) {
                if ((v.getId() == R.id.login_activity_login_tab_button) && (signup.isSelected())) {
                    loggin.setSelected(true);
                    signup.setSelected(false);
                    loggin.setTextColor(getResources().getColor(R.color.login_tab_panel_selected_button_text_color));
                    signup.setTextColor(getResources().getColor(R.color.login_tab_panel_not_selected_button_text_color));
                    viewSwitcher.showNext();

                } else if ((v.getId() == R.id.login_activity_signup_tab_button) && (loggin.isSelected())) {
                    signup.setSelected(true);
                    loggin.setSelected(false);
                    signup.setTextColor(getResources().getColor(R.color.login_tab_panel_selected_button_text_color));
                    loggin.setTextColor(getResources().getColor(R.color.login_tab_panel_not_selected_button_text_color));
                    viewSwitcher.showPrevious();
                }
            }
        };

        signup.setOnClickListener(tab_button_listener);
        loggin.setOnClickListener(tab_button_listener);
    }

    public void checkEmail() {
        if (connecting) return;

        //Get the view
        TextView emailView = ((TextView)findViewById(R.id.login_activity_signup_email));

        //Store the value
        String email = emailView.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailView.setError("This field can not be blank.");
            emailView.requestFocus();
        } else {
            if (StaticParameters.StandAlone) {
                simulateWait(false);
            } else {
                try {
                    this.controller.checkEmail(email,new EventHandler<CommandCallbackEventArgs>(this,"onEmailCheckedCallback",CommandCallbackEventArgs.class));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void simulateWait(boolean fromLogin) {
        Thread t = new Thread() {
            @Override
            public void run(){
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fromLogin) {
                setResult(RESULT_OK);
                finish();
            } else {
                openRegister();
            }
        }
    }

    public void attemptLogin() {
        if (connecting) return;

        //Get the views
        TextView usernameView = ((TextView)findViewById(R.id.login_activity_login_username));
        TextView passwordView = ((TextView)findViewById(R.id.login_activity_login_password));

        // Store values at the time of the login attempt.
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

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
            dataProvider.setUser(new ServerUser(username, password));
            dataProvider.Connect();

            if (StaticParameters.StandAlone) {
                simulateWait(true);
            }
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

    public void onEmailCheckedCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        for(Format receivedFormat : eventArgs.getReceivedFormats())
            if ((receivedFormat instanceof COMMON) && (((COMMON) receivedFormat).STATUS.equalsIgnoreCase("OK")))
                openRegister();
            else if ((receivedFormat instanceof COMMON) && (!((COMMON) receivedFormat).STATUS.equalsIgnoreCase("OK"))) {
                TextView emailView = (TextView) findViewById(R.id.login_activity_signup_email);
                emailView.setError("Email is already registered");
                emailView.requestFocus();
            }
    }

    private void openRegister() { //TODO: Add parameter. Proposed username
        Intent intent = new Intent(this, Register.class);
        String email = ((TextView) findViewById(R.id.login_activity_signup_email)).getText().toString();
        intent.putExtra("email",email);
        try {
            intent.putExtra("username", email.split("@")[0]);
        } catch (Exception e) {}
        startActivityForResult(intent, OP_CODE_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case OP_CODE_REGISTER:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }
}
