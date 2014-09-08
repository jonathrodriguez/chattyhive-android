package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.chattyhive.framework.OnInflateLayoutListener;
import com.chattyhive.chattyhive.framework.OnRemoveLayoutListener;
import com.chattyhive.chattyhive.framework.OnTransitionListener;
import com.chattyhive.chattyhive.framework.SlidingStepsLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Register extends Activity {

    private SlidingStepsLayout layout;

    private User newUser;

    //TEMP
    String email = "";
    String proposedUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        this.layout = ((SlidingStepsLayout)findViewById(R.id.slidingsteps));
        this.layout.setOnInflateLayoutListener(onInflateLayoutListener);
        this.layout.setOnRemoveLayoutListener(onRemoveLayoutListener);
        this.layout.setOnTransitionListener(onTransitionListener);

        Intent intent = this.getIntent();

        email = intent.getStringExtra("email");
        proposedUsername = intent.getStringExtra("username");

        //this.newUser = new User(); //TODO: Add empty constructor or at least passing only email.
    }

    public View.OnClickListener onEnterButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (StaticParameters.StandAlone) {
                setResult(RESULT_OK);
                finish();
            } else {
                //TODO: Validate data and send to server.
            }
        }
    };

    public View.OnClickListener onCancelButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public OnInflateLayoutListener onInflateLayoutListener = new OnInflateLayoutListener() {
        @Override
        public void OnInflate(View view) {

            //TODO: Restore values
            switch (view.getId()) {
                case R.id.register_first_step_layout:
                    view.findViewById(R.id.register_first_step_back_button).setOnClickListener(onCancelButtonClick);
                    break;
                case R.id.register_second_step_layout:
                    if ((proposedUsername != null) && (!proposedUsername.isEmpty()))
                        ((TextView)view.findViewById(R.id.register_second_step_username)).setText(proposedUsername);
                    break;
                case R.id.register_third_step_layout:
                    view.findViewById(R.id.register_third_step_next_button).setOnClickListener(onEnterButtonClick);
                    if ((email != null) && (!email.isEmpty()))
                        ((TextView)view.findViewById(R.id.register_third_step_email)).setText(email);
                    break;
            }
        }
    };

    public OnRemoveLayoutListener onRemoveLayoutListener = new OnRemoveLayoutListener() {
        @Override
        public void OnRemove(View view) {

        }
    };

    public OnTransitionListener onTransitionListener = new OnTransitionListener() {
        @Override
        public boolean OnBeginTransition(int actualStep, int nextStep) {
            switch (actualStep) {
                case 0:
                    TextView birthdayView = ((TextView)layout.findViewById(R.id.register_first_step_date));
                    String birthday = birthdayView.getText().toString();
                    if (birthday.isEmpty()) {
                        birthdayView.setError("You must enter a birthday.");
                        birthdayView.requestFocus();
                        return false;
                    }
                    else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date birthdayDate = simpleDateFormat.parse(birthday);
                            if (!isAtLeastYearsOld(birthdayDate,14)) {
                                birthdayView.setError("You must be at least 14 years old to sign up.");
                                birthdayView.requestFocus();
                                return false;
                            }
                        } catch (ParseException e) {
                            birthdayView.setError("You must enter a valid date in the format dd/MM/yyyy.");
                            birthdayView.requestFocus();
                            return false;
                        }
                    }
                    break;
                case 1:
                    if (nextStep == 2) {
                        //TODO: Check username
                    }
                    break;
            }
            return true;
        }

        @Override
        public void OnEndTransition(int actualStep, int previousStep) {
            //TODO: Save values.
        }
    };

    public void onRegisterCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        for(Format receivedFormat : eventArgs.getReceivedFormats())
            if ((receivedFormat instanceof COMMON) && (((COMMON) receivedFormat).STATUS.equalsIgnoreCase("OK"))) {
                setResult(RESULT_OK);
                finish();
            }
            else if ((receivedFormat instanceof COMMON) && (!((COMMON) receivedFormat).STATUS.equalsIgnoreCase("OK"))) {
                //TODO: Process errors
            }
    }



    private boolean isAtLeastYearsOld(Date first, int numYears) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(new Date());
        a.add(Calendar.YEAR,numYears);
        return b.after(a);
    }

    private Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);
        return cal;
    }


}
