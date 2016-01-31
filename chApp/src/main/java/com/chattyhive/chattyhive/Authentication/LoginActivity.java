package com.chattyhive.chattyhive.Authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.chattyhive.Core.ContentProvider.Formats.LOGIN;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandQueue;
import com.chattyhive.Core.Controller;
import com.chattyhive.Core.ContentProvider.DataProvider;
import com.chattyhive.Core.ContentProvider.Formats.COMMON;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.Util.CallbackDelegate;
import com.chattyhive.Core.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.chattyhive.R;

public class LoginActivity extends AccountAuthenticatorActivity {
    static final int OP_CODE_REGISTER = 3;

    static final String PARAM_USER_PASS = "USER_PASS";

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


        this.controller = Controller.GetRunningController();
        this.dataProvider = this.controller.getDataProvider();
    }


    protected void setTabButtonsBehaviour() {
        final Button signup = (Button)findViewById(R.id.login_activity_signup_tab_button);
        final Button loggin = (Button)findViewById(R.id.login_activity_login_tab_button);

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
            this.controller.CheckEmail(email, new CallbackDelegate(this, "onEmailCheckedCallback", CommandCallbackEventArgs.class));
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
            LOGIN login = new LOGIN();
            login.USER = username;
            login.PASS = password;

            Command command = new Command(AvailableCommands.Login,login);
            command.addCallbackDelegate(new CallbackDelegate(this,"onLogin",CommandCallbackEventArgs.class));
            dataProvider.runCommand(command, CommandQueue.Priority.RealTime);
        }
    }

    public void onLogin(CommandCallbackEventArgs eventArgs) {
        connecting = false;
        //TODO: Hide animation
        //Then check if connection was OK
        if (eventArgs.countReceivedFormats() > 0) {
            boolean connected = false;
            for (Format receivedFormat : eventArgs.getReceivedFormats()) {
                if ((receivedFormat instanceof COMMON) && (((COMMON) receivedFormat).STATUS.equals("OK"))) {
                    connected = true;
                    break;
                }
            }
            if (connected) { //We are in

                final Intent intent = new Intent();

                for (Format sentFormat : eventArgs.getSentFormats()) {
                    if (sentFormat instanceof LOGIN) {
                        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, ((LOGIN) sentFormat).USER);
                        intent.putExtra(PARAM_USER_PASS, ((LOGIN) sentFormat).PASS);
                        break;
                    }
                }

                //TODO: Complete this data
                //intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                //intent.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);

                finishLogin(intent);
            } else { //Connection failed
                TextView usernameView = ((TextView)findViewById(R.id.login_activity_login_username));
                usernameView.setError("Unknown error while connecting to server.");
                usernameView.requestFocus();
            }
        } else { //Something went very wrong.
            TextView usernameView = ((TextView)findViewById(R.id.login_activity_login_username));
            usernameView.setError("Unknown error while connecting to server.");
            usernameView.requestFocus();
        }
    }

    public void onEmailCheckedCallback(CommandCallbackEventArgs eventArgs) {
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
        Intent intent = new Intent(this, RegisterActivity.class);
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
                //TODO: On returning from register save the account to the account manager
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        Account account = new Account(accountName, accountType);

        boolean accountExists = false;
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(accountType);
        for (Account acc : accounts) {
            if ((acc.name.equalsIgnoreCase(accountName)) && (acc.type.equalsIgnoreCase(accountType))) {
                account = acc;
                accountExists = true;
                break;
            }
        }

        if (!accountExists) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = "";
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, accountPassword, null);
            accountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            accountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
