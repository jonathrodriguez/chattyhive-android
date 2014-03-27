package com.chattyhive.chattyhive;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.contentprovider.server.ServerUser;

public class LoginActivity extends Activity {

    private String username;
    private String password;
    private AsyncTask mAuthTask;

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
        if (mAuthTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
        username = ((TextView)findViewById(R.id.login_activity_login_username)).getText().toString();
        password = ((TextView)findViewById(R.id.login_activity_login_password)).getText().toString();

        //mServer = servers.get(mServerView.getSelectedItemPosition());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
       /* if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }*/

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            //mEmailView.setError("This field can not be blank.");
            focusView = findViewById(R.id.login_activity_login_username);
            cancel = true;
        }/* else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/
        if (TextUtils.isEmpty(password)) {
            //mEmailView.setError("This field can not be blank.");
            focusView = findViewById(R.id.login_activity_login_password);
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
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Log.w("UserLoginTask","Getting the controller");
            Controller controller = Controller.getRunningController();
            Log.w("UserLoginTask","I have the controller");
            controller.setServerUser(new ServerUser(username,password));
            Log.w("UserLoginTask", "I have set the user and pass");
            //controller.setServerApp("chtest2");
            return controller.Connect();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
           // showProgress(false);



            if (success) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {

               /* mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();*/
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
           // showProgress(false);
        }
    }
}
