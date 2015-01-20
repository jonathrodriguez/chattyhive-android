package com.chattyhive.chattyhive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnInflateLayoutListener;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnRemoveLayoutListener;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnTransitionListener;
import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.SlidingStepsLayout;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private Controller controller;

    private int year = 1988;
    private int month=0;
    private int day=1;

    static final int DATE_DIALOG_ID = 999;
    static final int LOCATION_DIALOG0_ID = 9980;
    static final int LOCATION_DIALOG1_ID = 9981;
    static final int LOCATION_DIALOG2_ID = 9982;
    static final int LANGUAGE_DIALOG_ID = 997;

    TextView birthdayView;
    String birthday;
    int locationStep = 0;
    int locationIndex = -1;
    String locationString;

    private String[] countries;
    private String[] region;
    private String[] city;
    private String[] titles;
    private ArrayList<String[]> regions;
    private ArrayList<String[]> cities;

    private ArrayList<String> mSelectedItems;
    String[] languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        this.thisActivity = this;

        this.controller = Controller.GetRunningController();

        this.layout = ((SlidingStepsLayout)findViewById(R.id.slidingsteps));
        this.layout.setOnInflateLayoutListener(onInflateLayoutListener);
        this.layout.setOnRemoveLayoutListener(onRemoveLayoutListener);
        this.layout.setOnTransitionListener(onTransitionListener);

        Intent intent = this.getIntent();

        String email = intent.getStringExtra("email");
        String proposedUsername = intent.getStringExtra("username");

        this.newUser = new User(email, this.controller);
        this.newUser.getUserPublicProfile().setPublicName(proposedUsername);
        this.newUser.getUserPrivateProfile().setSex("female"); //Load default value.
    }

    public View.OnClickListener onEnterButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                TextView emailView = (TextView)findViewById(R.id.register_third_step_email);
                TextView passwordView = (TextView)findViewById(R.id.register_third_step_password);
                TextView repeatPasswordView = (TextView)findViewById(R.id.register_third_step_repeat_password);

                if (!emailView.getText().toString().equalsIgnoreCase(newUser.getEmail())) {
                    controller.CheckEmail(emailView.getText().toString(), new EventHandler<CommandCallbackEventArgs>(this, "onEmailCheckedCallback", CommandCallbackEventArgs.class));
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
                    birthdayView = ((TextView)layout.findViewById(R.id.register_first_step_date));
                    birthdayView.setOnClickListener(date);
                    view.findViewById(R.id.register_first_step_location_textView).setOnClickListener(location);
                    view.findViewById(R.id.register_first_step_languages_textView).setOnClickListener(language);
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

                    //((TextView)view.findViewById(R.id.register_first_step_location_textView)).setText(newUser.getUserPrivateProfile().getLocation());

                    String langs = "";
                    if ((newUser.getUserPrivateProfile().getLanguages() != null) && (newUser.getUserPrivateProfile().getLanguages().size() > 0))
                        for (String lang : newUser.getUserPrivateProfile().getLanguages())
                            langs += ((langs.isEmpty())?"":", ") + lang;

                    if (!langs.isEmpty())
                        ((TextView)view.findViewById(R.id.register_first_step_languages_textView)).setText(langs);

                    if ((newUser.getUserPrivateProfile().getImageURL() != null) && (!newUser.getUserPrivateProfile().getImageURL().isEmpty())) {
                        Image image = null;
                        if ((newUser != null) && (newUser.getUserPrivateProfile() != null))
                            image = newUser.getUserPrivateProfile().getProfileImage();

                        if (image != null) {
                            image.OnImageLoaded.add(new EventHandler<EventArgs>(thisActivity,"onPrivateImageLoaded",EventArgs.class));
                            image.loadImage(Image.ImageSize.xlarge,0);
                        }
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

                    if ((newUser.getUserPublicProfile().getImageURL() != null) && (!newUser.getUserPublicProfile().getImageURL().isEmpty())) {
                        Image image = null;
                        if ((newUser != null) && (newUser.getUserPublicProfile() != null))
                            image = newUser.getUserPublicProfile().getProfileImage();

                        if (image != null) {
                            image.OnImageLoaded.add(new EventHandler<EventArgs>(thisActivity,"onPublicImageLoaded",EventArgs.class));
                            image.loadImage(Image.ImageSize.xlarge,0);
                        }
                    }

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

    public void onPrivateImageLoaded(Object sender,EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image)sender;

        final ImageView imageView = (ImageView)findViewById(R.id.register_first_step_profile_photo);

        runOnUiThread( new Runnable() {
            @Override
            public void run() {
            InputStream is = image.getImage(Image.ImageSize.xlarge,0);
            if ((is != null) && (imageView != null)) {
                imageView.setImageBitmap(BitmapFactory.decodeStream(is));
                try {
                    is.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (is != null)
                image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisActivity,"onPrivateImageLoaded",EventArgs.class));
            //image.freeMemory();
            }
        });
    }

    public void onPublicImageLoaded(Object sender,EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image)sender;

        final ImageView imageView = (ImageView)findViewById(R.id.register_second_step_avatar_image);

        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                InputStream is = image.getImage(Image.ImageSize.xlarge,0);
                if ((is != null) && (imageView != null)) {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(is));
                    try {
                        is.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (is != null)
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisActivity,"onPublicImageLoaded",EventArgs.class));
                //image.freeMemory();
            }
        });
    }

    public OnRemoveLayoutListener onRemoveLayoutListener = new OnRemoveLayoutListener() {
        @Override
        public void OnRemove(View view) {

        }
    };

    private View.OnClickListener date = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialog(DATE_DIALOG_ID);
        }
    };
    private View.OnClickListener language = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialog(LANGUAGE_DIALOG_ID);
        }
    };

    private View.OnClickListener location = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            locationStep = 0;
            locationData();
            showDialog(LOCATION_DIALOG0_ID);
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month, day);
            case LOCATION_DIALOG0_ID:
                return locationDialog0();
            case LOCATION_DIALOG1_ID:
                return locationDialog1();
            case LOCATION_DIALOG2_ID:
                return locationDialog2();
            case LANGUAGE_DIALOG_ID:
                return languagesDialog();
        }
        return null;
    }

    private void getLanguages(){
        languages = new String[4];
        languages[0] = "English";
        languages[1] = "French";
        languages[2] = "Spanish";
        languages[3] = "Turkish";
    }

    public Dialog languagesDialog(){
        mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        getLanguages();
        builder.setTitle("Select your languages")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(languages, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(languages[which]);
                                } else if (mSelectedItems.contains(languages[which])) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(languages[which]);
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        if (mSelectedItems.size() != 0) {
                            String languagesString = mSelectedItems.get(0);
                            for (int i = 1; i < mSelectedItems.size(); i++) {
                                languagesString = languagesString +", "+mSelectedItems.get(i);
                            }
                            ((TextView) findViewById(R.id.register_first_step_languages_textView)).setText(languagesString);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private void locationData(){
        countries = new String[4];
        countries[0] = "Albania";
        countries[1] = "EEUU";
        countries[2] = "Spain";
        countries[3] = "Yemen";
        region = new String[4];
        region[0] = "Andalucía";
        region[1] = "Cataluña";
        region[2] = "Galicia";
        region[3] = "Pais Vasco";
        city = new String[4];
        city[0] = "A Coruña";
        city[1] = "Lugo";
        city[2] = "Ourense";
        city[3] = "Pontevedra";
        titles = new String[3];
        titles[0] = "Choose your country";
        titles[1] = "Choose your region";
        titles[2] = "Choose your city";

        regions = new ArrayList<String[]>();
        regions.add(0, null);
        regions.add(1, null);
        regions.add(2, region);
        regions.add(3, null);
        cities = new ArrayList<String[]>();
        cities.add(0, null);
        cities.add(1, null);
        cities.add(2, city);
        cities.add(3, null);
    }

    protected Dialog locationDialog0(){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)this);
            builder.setTitle(titles[locationStep]).setItems(countries, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    locationString = countries[which];
                    newUser.getUserPrivateProfile().setLocation(locationString);
                    ((TextView) findViewById(R.id.register_first_step_location_textView)).setText(locationString);
                    locationStep++;
                    locationIndex = which;
                    if (regions.get(which) != null)
                        showDialog(LOCATION_DIALOG1_ID);
                }
            });
            return builder.create();
    }

    protected Dialog locationDialog1(){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)this);
                builder.setTitle(titles[locationStep]).setItems(regions.get(locationIndex), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        locationString = locationString +", "+ regions.get(locationIndex)[which];
                        newUser.getUserPrivateProfile().setLocation(locationString);
                        ((TextView) findViewById(R.id.register_first_step_location_textView)).setText(locationString);
                        locationStep++;
                        locationIndex = which;
                        if (cities.get(which) != null)
                            showDialog(LOCATION_DIALOG2_ID);
                    }
                });
                return builder.create();
    }
    protected Dialog locationDialog2(){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)this);
                builder.setTitle(titles[locationStep]).setItems(cities.get(locationIndex), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        locationString = locationString +", "+ cities.get(locationIndex)[which];
                        newUser.getUserPrivateProfile().setLocation(locationString);
                        ((TextView) findViewById(R.id.register_first_step_location_textView)).setText(locationString);
                    }
                });
                return builder.create();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            birthdayView.setText(new StringBuilder().append(day)
                    .append("/").append(month + 1).append("/").append(year)
                    .append(" "));
        }
    };

    public OnTransitionListener onTransitionListener = new OnTransitionListener() {
        @Override
        public boolean OnBeginTransition(int actualStep, int nextStep) {
            switch (actualStep) {
                case 0:
                    birthday = birthdayView.getText().toString();
                    if (birthday.isEmpty()) {
                        birthdayView.setError("You must enter a birthday.");
                        birthdayView.requestFocus();
                        return false;
                    }
                    else {
                        if (birthday.charAt(1) == '/'){
                            birthday = '0' + birthday;
                        }
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
                        controller.CheckUsername(((TextView)findViewById(R.id.register_second_step_username)).getText().toString(),new EventHandler<CommandCallbackEventArgs>(thisActivity,"onUsernameCheckedCallback",CommandCallbackEventArgs.class));
                    }
                    break;
            }
            return true;
        }

        @Override
        public void OnDuringTransition(int[] visibleSteps, float[] visibilityAmount) { }

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
                this.controller.setMe(this.newUser);
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
