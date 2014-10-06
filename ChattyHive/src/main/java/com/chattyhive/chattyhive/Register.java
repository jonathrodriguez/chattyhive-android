package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnInflateLayoutListener;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnRemoveLayoutListener;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnTransitionListener;
import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.SlidingStepsLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Register extends Activity {

    private SlidingStepsLayout layout;
    private User newUser;
    private String password;
    private String repeatPassword;

    private Boolean usernameValidated = false;

    private Register thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        this.thisActivity = this;

        this.layout = ((SlidingStepsLayout)findViewById(R.id.slidingsteps));
        this.layout.setOnInflateLayoutListener(onInflateLayoutListener);
        this.layout.setOnRemoveLayoutListener(onRemoveLayoutListener);
        this.layout.setOnTransitionListener(onTransitionListener);

        Intent intent = this.getIntent();

        String email = intent.getStringExtra("email");
        String proposedUsername = intent.getStringExtra("username");

        this.newUser = new User(email);
        this.newUser.getUserPublicProfile().setPublicName(proposedUsername);
        this.newUser.getUserPrivateProfile().setSex("female"); //Load default value.
    }

    public View.OnClickListener onEnterButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (StaticParameters.StandAlone) {
                setResult(RESULT_OK);
                finish();
            } else {
                TextView emailView = (TextView)findViewById(R.id.register_third_step_email);
                TextView passwordView = (TextView)findViewById(R.id.register_third_step_password);
                TextView repeatPasswordView = (TextView)findViewById(R.id.register_third_step_repeat_password);

                if (!emailView.getText().toString().equalsIgnoreCase(newUser.getEmail())) {
                    Controller.GetRunningController().CheckEmail(emailView.getText().toString(), new EventHandler<CommandCallbackEventArgs>(this, "onEmailCheckedCallback", CommandCallbackEventArgs.class));
                }

                if (passwordIsValid(passwordView)) {
                    if (passwordView.getText().toString().equals(repeatPasswordView.getText().toString())) {
                        newUser.Register(passwordView.getText().toString(),new EventHandler<CommandCallbackEventArgs>(thisActivity,"onRegisterCallback",CommandCallbackEventArgs.class));
                    } else {
                        repeatPasswordView.setError("Passwords must match.");
                        repeatPasswordView.requestFocus();
                    }
                }
            }
        }
    };

    private boolean passwordIsValid(TextView passwordView) {
        boolean isValid = false;

        if (passwordView.getText().length() < 8) {
            passwordView.setError("Password must be at least 8 characters long.");
            passwordView.requestFocus();
        } else {
            isValid = true;
        }

        return isValid;
    }

    public View.OnClickListener onCancelButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public OnInflateLayoutListener onInflateLayoutListener = new OnInflateLayoutListener() {
        @Override
        public void OnInflate(View view) {

            switch (view.getId()) {
                case R.id.register_first_step_layout:
                    view.findViewById(R.id.register_first_step_back_button).setOnClickListener(onCancelButtonClick);
                    view.findViewById(R.id.register_first_step_show_age_private).setOnClickListener(onCheckBoxClickListener);
                    view.findViewById(R.id.register_first_step_male_redio_button).setOnClickListener(onGenderRadioButtonClick);
                    view.findViewById(R.id.register_first_step_female_radio_button).setOnClickListener(onGenderRadioButtonClick);
                    ((ImageView)view.findViewById(R.id.register_first_step_show_age_private_checkbox_image)).setImageResource((newUser.getUserPrivateProfile().getShowAge()) ? R.drawable.registro_white_tick_activated_grey : R.drawable.registro_white_tick_deactivated_grey);
                    view.findViewById(R.id.register_first_step_show_age_private).setTag(newUser.getUserPrivateProfile().getShowAge());
                    ((TextView)view.findViewById(R.id.register_first_step_name)).setText(newUser.getUserPrivateProfile().getFirstName());
                    ((TextView)view.findViewById(R.id.register_first_step_surname)).setText(newUser.getUserPrivateProfile().getLastName());
                    if (newUser.getUserPrivateProfile().getBirthdate() != null)
                        ((TextView)view.findViewById(R.id.register_first_step_date)).setText(DateFormatter.toShortHumanReadableString(newUser.getUserPrivateProfile().getBirthdate()));
                    String sex = newUser.getUserPrivateProfile().getSex();
                    if (sex.equalsIgnoreCase("male")) {
                        ((ImageView)findViewById(R.id.register_first_step_male_radio_image)).setImageResource(R.drawable.registro_selector);
                        ((ImageView)findViewById(R.id.register_first_step_female_radio_image)).setImageResource(R.drawable.registro_selector_deactivated);
                    } else {
                        ((ImageView)findViewById(R.id.register_first_step_male_radio_image)).setImageResource(R.drawable.registro_selector_deactivated);
                        ((ImageView)findViewById(R.id.register_first_step_female_radio_image)).setImageResource(R.drawable.registro_selector);
                    }
                    break;
                case R.id.register_second_step_layout:
                    ((TextView)view.findViewById(R.id.register_second_step_username)).setText(newUser.getUserPublicProfile().getPublicName());
                    //Show age
                    view.findViewById(R.id.register_second_step_show_age_public_button).setOnClickListener(onCheckBoxClickListener);
                    ((ImageView)view.findViewById(R.id.register_second_step_show_age_public_checkbox_image)).setImageResource((newUser.getUserPublicProfile().getShowAge())?R.drawable.registro_white_tick_activated_grey:R.drawable.registro_white_tick_deactivated_grey);
                    view.findViewById(R.id.register_second_step_show_age_public_button).setTag(newUser.getUserPublicProfile().getShowAge());
                    //Show location
                    view.findViewById(R.id.register_second_step_show_location_public_button).setOnClickListener(onCheckBoxClickListener);
                    ((ImageView)view.findViewById(R.id.register_second_step_show_location_public_checkbox_image)).setImageResource((newUser.getUserPublicProfile().getShowLocation())?R.drawable.registro_white_tick_activated_grey:R.drawable.registro_white_tick_deactivated_grey);
                    view.findViewById(R.id.register_second_step_show_location_public_button).setTag(newUser.getUserPublicProfile().getShowLocation());
                    //Show gender
                    view.findViewById(R.id.register_second_step_show_gender_public_button).setOnClickListener(onCheckBoxClickListener);
                    ((ImageView)view.findViewById(R.id.register_second_step_show_gender_public_checkbox_image)).setImageResource((newUser.getUserPublicProfile().getShowSex())?R.drawable.registro_white_tick_activated_grey:R.drawable.registro_white_tick_deactivated_grey);
                    view.findViewById(R.id.register_second_step_show_gender_public_button).setTag(newUser.getUserPublicProfile().getShowSex());
                    break;
                case R.id.register_third_step_layout:
                    view.findViewById(R.id.register_third_step_next_button).setOnClickListener(onEnterButtonClick);
                    ((TextView)view.findViewById(R.id.register_third_step_email)).setText(newUser.getEmail());
                    ((TextView)view.findViewById(R.id.register_third_step_password)).setText(password);
                    ((TextView)view.findViewById(R.id.register_third_step_repeat_password)).setText(repeatPassword);
                    break;
            }
        }
    };

    public View.OnClickListener onGenderRadioButtonClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String sex = newUser.getUserPrivateProfile().getSex();
            if ((v.getId() == R.id.register_first_step_male_redio_button) && (sex.equalsIgnoreCase("female"))) {
                newUser.getUserPrivateProfile().setSex("MALE");
                ((ImageView)findViewById(R.id.register_first_step_male_radio_image)).setImageResource(R.drawable.registro_selector);
                ((ImageView)findViewById(R.id.register_first_step_female_radio_image)).setImageResource(R.drawable.registro_selector_deactivated);
            } else if ((v.getId() == R.id.register_first_step_female_radio_button) && (sex.equalsIgnoreCase("male"))) {
                newUser.getUserPrivateProfile().setSex("FEMALE");
                ((ImageView)findViewById(R.id.register_first_step_male_radio_image)).setImageResource(R.drawable.registro_selector_deactivated);
                ((ImageView)findViewById(R.id.register_first_step_female_radio_image)).setImageResource(R.drawable.registro_selector);
            }
        }
    };

    public View.OnClickListener onCheckBoxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int childCount = ((ViewGroup)v).getChildCount();
            Boolean state = true;
            if (v.getTag() != null)
                state = !((Boolean)v.getTag());
            v.setTag(state);

            for (int i=0; i<childCount;i++) {
                View child = ((ViewGroup)v).getChildAt(i);
                if (child instanceof ImageView) {
                    if (state)
                        ((ImageView)child).setImageResource(R.drawable.registro_white_tick_activated_grey);
                    else
                        ((ImageView)child).setImageResource(R.drawable.registro_white_tick_deactivated_grey);
                }
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
                    if ((nextStep == 2) && (!usernameValidated) && (!newUser.getUserPublicProfile().getPublicName().equals(((TextView)findViewById(R.id.register_second_step_username)).getText().toString()))) {
                        Controller.GetRunningController().CheckUsername(((TextView)findViewById(R.id.register_second_step_username)).getText().toString(),new EventHandler<CommandCallbackEventArgs>(thisActivity,"onUsernameCheckedCallback",CommandCallbackEventArgs.class));
                    }
                    break;
            }
            return true;
        }

        @Override
        public void OnEndTransition(int actualStep, int previousStep) {
            switch (previousStep) {
                case 0:
                    newUser.getUserPrivateProfile().setFirstName(((TextView)findViewById(R.id.register_first_step_name)).getText().toString());
                    newUser.getUserPrivateProfile().setLastName(((TextView)findViewById(R.id.register_first_step_surname)).getText().toString());
                    newUser.getUserPrivateProfile().setShowAge((Boolean)findViewById(R.id.register_first_step_show_age_private).getTag());
                    newUser.getUserPrivateProfile().setBirthdate(DateFormatter.fromShortHumanReadableString(((TextView)findViewById(R.id.register_first_step_date)).getText().toString()));
                    //Gender is saved when value changes
                    break;
                case 1:
                    if (actualStep == 2) {
                        newUser.getUserPublicProfile().setPublicName(((TextView) findViewById(R.id.register_second_step_username)).getText().toString());
                        usernameValidated = false;
                    }
                    newUser.getUserPublicProfile().setShowAge((Boolean)findViewById(R.id.register_second_step_show_age_public_button).getTag());
                    newUser.getUserPublicProfile().setShowLocation((Boolean)findViewById(R.id.register_second_step_show_location_public_button).getTag());
                    newUser.getUserPublicProfile().setShowSex((Boolean) findViewById(R.id.register_second_step_show_gender_public_button).getTag());
                    break;
                case 2:
                    newUser.setEmail(((TextView)findViewById(R.id.register_third_step_email)).getText().toString());
                    password = ((TextView)findViewById(R.id.register_third_step_password)).getText().toString();
                    repeatPassword = ((TextView)findViewById(R.id.register_third_step_repeat_password)).getText().toString();
                    break;
            }
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

    public void onEmailCheckedCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        for(Format receivedFormat : eventArgs.getReceivedFormats())
            if ((receivedFormat instanceof COMMON) && (((COMMON) receivedFormat).STATUS.equalsIgnoreCase("OK"))) {
                this.newUser.setEmail(((TextView) findViewById(R.id.register_third_step_email)).getText().toString());
                this.findViewById(R.id.register_third_step_next_button).performClick();
            }
            else if ((receivedFormat instanceof COMMON) && (!((COMMON) receivedFormat).STATUS.equalsIgnoreCase("OK"))) {
                TextView emailView = (TextView) findViewById(R.id.register_third_step_email);
                emailView.setError("Email is already registered");
                emailView.requestFocus();
            }
    }

    public void onUsernameCheckedCallback(Object sender, CommandCallbackEventArgs eventArgs) {
        for (Format receivedFormat : eventArgs.getReceivedFormats())
            if ((receivedFormat instanceof COMMON) && (((COMMON) receivedFormat).STATUS.equalsIgnoreCase("OK"))) {
                usernameValidated = true;
                onTransitionListener.OnEndTransition(1,2);
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
