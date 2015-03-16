package com.chattyhive.chattyhive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.Core.BusinessObjects.Image;
import com.chattyhive.Core.BusinessObjects.Users.ProfileLevel;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.ContentProvider.formats.COMMON;
import com.chattyhive.Core.ContentProvider.formats.Format;
import com.chattyhive.Core.ContentProvider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.Core.ContentProvider.formats.USER_PROFILE;
import com.chattyhive.Core.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.Core.Util.Formatters.DateFormatter;
import com.chattyhive.chattyhive.framework.Util.Keyboard;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;
import com.chattyhive.chattyhive.framework.Util.ViewPair;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Jonathan on 20/05/2014.
 */
public class Profile extends Window {
    private static int ProfileHierarchyLevel = 1;

    private transient View profileView;
    private transient View actionBar;

    public enum ProfileType {Private, Public}
    private enum ProfileView {Private, Public, Own, Edit}
    private enum ShowInProfile { None, Private, Public, Both}

    private transient User user;
    private transient User modifiedUser; //This is for profile edition.
    private String actionBarSubstring = "";
    private ProfileType profileType;
    private ProfileView profileViewType;

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
    private String[] region2;
    private String[] city;
    private String[] titles;
    private ArrayList<String[]> regions;
    private ArrayList<String[]> cities;

    private ArrayList<String> selectedLanguages;
    String[] languages;
    boolean[] languagesSelection = null;

    private Profile (Context context) {
        super(context);
        this.setHierarchyLevel(ProfileHierarchyLevel);
        getLanguages();
    }

    public Profile(Context context, User user, ProfileType profileType) {
        this(context, user, profileType, null);
    }

    public Profile(Context context, User user, ProfileType profileType, String actionBarSubstring) {
        this(context);
        this.user = user;
        this.profileType = profileType;
        this.actionBarSubstring = (actionBarSubstring != null)?actionBarSubstring:"";
    }

    @Override
    public void Open() {
        if (!this.hasContext()) return;
        if ((this.user == null) || (this.profileType == null)) return;

        this.profileViewType = (this.user.isMe())?ProfileView.Own:((this.profileType == ProfileType.Private)?ProfileView.Private:ProfileView.Public);

        this.Show();
    }

    @Override
    public void Close() {
        if (!this.hasContext()) return;

        this.Hide();

        this.user = null;
        this.modifiedUser = null;
        this.profileType = null;
        this.profileViewType = null;
    }

    @Override
    public void Show() {
        if (!this.hasContext()) return;
        if ((this.user == null) || (this.profileType == null)) return;

        if (this.profileViewType == null)
            this.profileViewType = (this.user.isMe())?ProfileView.Own:((this.profileType == ProfileType.Private)?ProfileView.Private:ProfileView.Public);

        ViewPair profileViewPair = ((Main)context).ShowLayout(R.layout.main_panel_profile_layout,R.layout.profile_action_bar);
        this.profileView = profileViewPair.getMainView();
        this.actionBar = profileViewPair.getActionBarView();

        adjustView();

        actionBar.findViewById(R.id.profile_action_bar_menu_clickable).setOnClickListener(((Main)context).menuIcon_ClickListener);
        actionBar.findViewById(R.id.profile_action_bar_myPhoto_button).setOnClickListener(((Main)context).appIcon_ClickListener);

        if (((Main)context).floatingPanel.isOpen())
            ((Main)context).floatingPanel.close();

        if (user.isMe()) {
            setData();

            profileView.findViewById(R.id.my_profile_status_public_button).setOnClickListener(change_shown_message);
            profileView.findViewById(R.id.my_profile_status_private_button).setOnClickListener(change_shown_message);
            profileView.findViewById(R.id.my_profile_edit_button).setOnClickListener(edit_button_click);
        } else {
            user.UserLoaded.add(new EventHandler<EventArgs>(this,"OnUserProfileLoaded",EventArgs.class));

            com.chattyhive.Core.BusinessObjects.Users.ProfileType requestProfile = com.chattyhive.Core.BusinessObjects.Users.ProfileType.PUBLIC;
            if (this.profileType == ProfileType.Private)
                requestProfile = com.chattyhive.Core.BusinessObjects.Users.ProfileType.PRIVATE;

            user.loadProfile(requestProfile, ProfileLevel.Complete);
        }
    }

    @Override
    public void Hide() {
        if (!this.hasContext()) return;

        ((Main)context).floatingPanel.resetAllowSwipeToMovePanels();

        this.profileView = null;
        this.actionBar = null;
    }

    public void OnUserProfileLoaded(Object sender, EventArgs eventArgs) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                setData();
            }
        });
    }

    public void setContext(Context context) {
        super.setContext(context);
        if ((this.user != null) && (!this.user.hasController()))
           this.user.setController(((Main)context).controller);

        if ((this.modifiedUser != null) && (!this.modifiedUser.hasController()))
            this.modifiedUser.setController(((Main)context).controller);
    }


    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        // write 'this' to 'out'...
        out.writeObject(this.profileType);
        out.writeObject(this.profileViewType);
        out.writeUTF(this.actionBarSubstring);

        if (this.user != null)
            Log.w("Profile.writeObject()","Saving user.");
        else
            Log.w("Profile.writeObject()","Saving NULL user.");

        if (this.user.isMe())
            out.writeUTF(this.user.toJson(new LOCAL_USER_PROFILE()).toString());
        else
            out.writeUTF(this.user.toJson(new USER_PROFILE()).toString());

        if (this.modifiedUser != null) {
            out.writeBoolean(true);
            if (this.modifiedUser.isMe())
                out.writeUTF(this.modifiedUser.toJson(new LOCAL_USER_PROFILE()).toString());
            else
                out.writeUTF(this.modifiedUser.toJson(new USER_PROFILE()).toString());
        } else
            out.writeBoolean(false);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.profileType = (ProfileType) in.readObject();
        this.profileViewType = (ProfileView) in.readObject();
        this.actionBarSubstring = in.readUTF();

        String userS = in.readUTF();

        if ((userS != null) && (!userS.isEmpty()))
            Log.w("Profile.readObject()","Restoring user.");
        else
            Log.w("Profile.readObject()","Restoring NULL user.");

        if (!userS.isEmpty()) {
            Format[] formats = Format.getFormat(new JsonParser().parse(userS));
            for (Format format : formats)
                if ((format instanceof LOCAL_USER_PROFILE) || (format instanceof USER_PROFILE))
                    this.user = new User(format);
        }

        Boolean hasModifiedUser = in.readBoolean();
        if (hasModifiedUser) {
            String modifiedUserS = in.readUTF();
            if (!modifiedUserS.isEmpty()) {
                Format[] formats = Format.getFormat(new JsonParser().parse(modifiedUserS));
                for (Format format : formats)
                    if ((format instanceof LOCAL_USER_PROFILE) || (format instanceof USER_PROFILE))
                        this.modifiedUser = new User(format);
            }
        }
    }

    public void loadBigPhoto(Object sender, EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image)sender;
        final Profile thisProfile = this;

        ((Activity)this.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = image.getImage(Image.ImageSize.xlarge, 0);
                    if (is != null) {
                        ((ImageView) profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (profileViewType != ProfileView.Edit)
                            ((ImageView) actionBar.findViewById(R.id.profile_action_bar_myPhoto_button)).setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisProfile, "loadBigPhoto", EventArgs.class));
                    //image.freeMemory();
                }
            }
        });

    }
    public void loadSmallPhoto (Object sender, EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image)sender;
        final Profile thisProfile = this;

        ((Activity)this.context).runOnUiThread( new Runnable() {
            @Override
            public void run() {
                try {
                InputStream is = image.getImage(Image.ImageSize.medium,0);
                if (is != null) {
                    ((ImageView) profileView.findViewById(R.id.profile_small_photo_thumbnail)).setImageBitmap(BitmapFactory.decodeStream(is));
                    try {
                        is.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisProfile,"loadSmallPhoto",EventArgs.class));
                    //image.freeMemory();
                }
            }
        });
    }

    private void adjustView() {
        if ((this.actionBarSubstring != null) && (!this.actionBarSubstring.isEmpty())) {
            ((TextView)actionBar.findViewById(R.id.profile_subtitle_text)).setText(this.actionBarSubstring);
            actionBar.findViewById(R.id.profile_subtitle_text).setVisibility(View.VISIBLE);
        }
        else
            actionBar.findViewById(R.id.profile_subtitle_text).setVisibility(View.GONE);

        switch (profileViewType) {
            case Private:
                //Privacy note
                profileView.findViewById(R.id.profile_edit_privacy_notification).setVisibility(View.GONE);
                //Images
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_small_photo_thumbnail).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.drawable.profile_photo_background);
                profileView.findViewById(R.id.profile_small_photo_thumbnail).setBackgroundResource(R.drawable.profile_photo_background);
                ((ImageView)profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.my_profile_private_profile_simple);
                ((ImageView)profileView.findViewById(R.id.profile_small_photo_thumbnail)).setImageResource(R.drawable.my_profile_public_profile_simple);
                //Full name and public name
                profileView.findViewById(R.id.profile_full_name).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_full_name_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
                profileView.findViewById(R.id.profile_edit_full_name).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);
                profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
                ((TextView)profileView.findViewById(R.id.profile_public_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.my_profile_nickname_text_size_private));
                profileView.findViewById(R.id.profile_edit_color).setVisibility(View.GONE);
                //Status
                profileView.findViewById(R.id.profile_status_message_header).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_status_message).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_status_message).setClickable(false);
                profileView.findViewById(R.id.profile_edit_status_message).setClickable(false);
                //Information
                profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_information_location_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_information_location_value).setClickable(false);
                profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_location_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_age_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_information_languages_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_information_languages_value).setClickable(false);
                profileView.findViewById(R.id.my_profile_information_languages_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_languages_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(false);
                //Edit button
                profileView.findViewById(R.id.my_profile_edit_button).setVisibility(View.GONE);
                break;
            case Public:
                //Privacy note
                profileView.findViewById(R.id.profile_edit_privacy_notification).setVisibility(View.GONE);
                //Images
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_small_photo_thumbnail).setVisibility(View.GONE);
                ((ImageView)profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.my_profile_public_profile_simple);
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.drawable.profile_photo_background);
                //Full name and public name
                profileView.findViewById(R.id.profile_full_name).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
                profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);
                profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
                ((TextView)profileView.findViewById(R.id.profile_public_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.my_profile_nickname_text_size_public));
                profileView.findViewById(R.id.profile_edit_color).setVisibility(View.GONE);
                //Status
                profileView.findViewById(R.id.profile_status_message_header).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_status_message).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_status_message).setClickable(false);
                profileView.findViewById(R.id.profile_edit_status_message).setClickable(false);
                //Information
                profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_information_location_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_information_location_value).setClickable(false);
                profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_location_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_age_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_information_languages_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_information_languages_value).setClickable(false);
                profileView.findViewById(R.id.my_profile_information_languages_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_languages_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(false);
                //Edit button
                profileView.findViewById(R.id.my_profile_edit_button).setVisibility(View.GONE);
                break;
            case Own:
                //Privacy note
                profileView.findViewById(R.id.profile_edit_privacy_notification).setVisibility(View.GONE);
                //Images
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_small_photo_thumbnail).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.drawable.profile_photo_background);
                profileView.findViewById(R.id.profile_small_photo_thumbnail).setBackgroundResource(R.drawable.profile_photo_background);
                ((ImageView)profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.my_profile_private_profile_simple);
                ((ImageView)profileView.findViewById(R.id.profile_small_photo_thumbnail)).setImageResource(R.drawable.my_profile_public_profile_simple);
                //Full name and public name
                profileView.findViewById(R.id.profile_full_name).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_full_name_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
                profileView.findViewById(R.id.profile_edit_full_name).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);
                profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
                ((TextView)profileView.findViewById(R.id.profile_public_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.my_profile_nickname_text_size_private));
                profileView.findViewById(R.id.profile_edit_color).setVisibility(View.GONE);
                //Status
                profileView.findViewById(R.id.profile_status_message_header).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_status_message).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_status).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_status_buttons).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_status_message).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_status_message).setClickable(false);
                profileView.findViewById(R.id.profile_edit_status_message).setClickable(false);
                //Information
                profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_information_location_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_information_location_value).setClickable(false);
                profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.VISIBLE);
                ((ImageView)profileView.findViewById(R.id.my_profile_information_location_show)).clearColorFilter();
                profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_location_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.VISIBLE);
                ((ImageView)profileView.findViewById(R.id.my_profile_information_age_show)).clearColorFilter();
                profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_age_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.VISIBLE);
                ((ImageView)profileView.findViewById(R.id.my_profile_information_gender_show)).clearColorFilter();
                profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_information_languages_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_information_languages_value).setClickable(false);
                profileView.findViewById(R.id.my_profile_information_languages_show).setVisibility(View.VISIBLE);
                ((ImageView)profileView.findViewById(R.id.my_profile_information_languages_show)).clearColorFilter();
                profileView.findViewById(R.id.my_profile_information_languages_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(false);
                //Edit button
                profileView.findViewById(R.id.my_profile_edit_button).setVisibility(View.VISIBLE);
                break;
            case Edit:
                float selected_alpha;
                float unselected_alpha;

                TypedValue alpha = new TypedValue();
                this.context.getResources().getValue(R.color.edit_profile_action_bar_type_button_selected_alpha,alpha,true);
                selected_alpha = alpha.getFloat();
                this.context.getResources().getValue(R.color.edit_profile_action_bar_type_button_unselected_alpha,alpha,true);
                unselected_alpha = alpha.getFloat();

                switch (profileType) {
                    case Private:
                        //Action Bar
                        actionBar.findViewById(R.id.private_profile_tab_button).setBackgroundResource(R.drawable.edit_profile_action_bar_type_button_selected_background);
                        actionBar.findViewById(R.id.public_profile_tab_button).setBackgroundResource(R.drawable.edit_profile_action_bar_type_button_unselected_background);
                        StaticMethods.SetAlpha(actionBar.findViewById(R.id.private_profile_tab_button),selected_alpha);
                        StaticMethods.SetAlpha(actionBar.findViewById(R.id.public_profile_tab_button),unselected_alpha);
                        //Privacy note
                        profileView.findViewById(R.id.profile_edit_privacy_notification).setVisibility(View.VISIBLE);
                        ((ImageView) profileView.findViewById(R.id.profile_edit_privacy_image)).setImageResource(R.drawable.registro_private);
                        ((TextView) profileView.findViewById(R.id.profile_edit_privacy_note)).setText(R.string.edit_profile_privacy_note_private);
                        //Images
                        profileView.findViewById(R.id.profile_big_photo_thumbnail).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_small_photo_thumbnail).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.my_profile_private_profile_simple);
                        ((ImageView)profileView.findViewById(R.id.profile_small_photo_thumbnail)).setImageResource(R.drawable.my_profile_public_profile_simple);
                        profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.drawable.edit_profile_photo_background);
                        profileView.findViewById(R.id.profile_small_photo_thumbnail).setBackgroundResource(R.drawable.edit_profile_photo_background);
                        //Full name and public name
                        profileView.findViewById(R.id.profile_full_name).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_full_name_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(edit_name_click_listener);
                        profileView.findViewById(R.id.profile_edit_full_name).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(edit_name_click_listener);
                        profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
                        ((TextView)profileView.findViewById(R.id.profile_public_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.my_profile_nickname_text_size_private));
                        profileView.findViewById(R.id.profile_edit_color).setVisibility(View.GONE);
                        //Status
                        profileView.findViewById(R.id.profile_status_message_header).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_status_message).setVisibility(View.VISIBLE);
                        ((TextView) profileView.findViewById(R.id.profile_edit_status_message_text)).setText(R.string.edit_profile_change_status_message_private);
                        profileView.findViewById(R.id.profile_status).setVisibility(View.GONE);
                        profileView.findViewById(R.id.my_profile_status_buttons).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_status_message).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_status_message).setOnClickListener(edit_status_click_listener);
                        profileView.findViewById(R.id.profile_edit_status_message).setOnClickListener(edit_status_click_listener);
                        //Information
                        profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_location_value).setClickable(true);
                        profileView.findViewById(R.id.profile_information_location_value).setOnClickListener(location);
                        profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.VISIBLE);
                        //TODO: apply color filter when server accepts changing this visibility.
                        //((ImageView)profileView.findViewById(R.id.my_profile_information_location_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_location_show)).clearColorFilter();
                        profileView.findViewById(R.id.my_profile_information_location_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_age_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_age_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_gender_show)).clearColorFilter();
                        profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_languages_value).setClickable(true);
                        profileView.findViewById(R.id.profile_information_languages_value).setOnClickListener(language);
                        profileView.findViewById(R.id.my_profile_information_languages_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_languages_show)).clearColorFilter();
                        profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_languages_show).setOnClickListener(edit_visibility_click_listener);
                        //Edit button
                        profileView.findViewById(R.id.my_profile_edit_button).setVisibility(View.GONE);
                        break;
                    case Public:
                        //Action Bar
                        actionBar.findViewById(R.id.private_profile_tab_button).setBackgroundResource(R.drawable.edit_profile_action_bar_type_button_unselected_background);
                        actionBar.findViewById(R.id.public_profile_tab_button).setBackgroundResource(R.drawable.edit_profile_action_bar_type_button_selected_background);
                        StaticMethods.SetAlpha(actionBar.findViewById(R.id.private_profile_tab_button),unselected_alpha);
                        StaticMethods.SetAlpha(actionBar.findViewById(R.id.public_profile_tab_button),selected_alpha);
                        //Privacy note
                        profileView.findViewById(R.id.profile_edit_privacy_notification).setVisibility(View.VISIBLE);
                        ((ImageView) profileView.findViewById(R.id.profile_edit_privacy_image)).setImageResource(R.drawable.registro_public);
                        ((TextView) profileView.findViewById(R.id.profile_edit_privacy_note)).setText(R.string.edit_profile_privacy_note_public);
                        //Images
                        profileView.findViewById(R.id.profile_big_photo_thumbnail).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_small_photo_thumbnail).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.drawable.edit_profile_photo_background);
                        ((ImageView)profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.my_profile_public_profile_simple);
                        //Full name and public name
                        profileView.findViewById(R.id.profile_full_name).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(null);
                        profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
                        profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(null);
                        profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);
                        profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
                        ((TextView)profileView.findViewById(R.id.profile_public_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.my_profile_nickname_text_size_public));
                        profileView.findViewById(R.id.profile_edit_color).setVisibility(View.VISIBLE);
                        //Status
                        profileView.findViewById(R.id.profile_status_message_header).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_status_message).setVisibility(View.VISIBLE);
                        ((TextView) profileView.findViewById(R.id.profile_edit_status_message_text)).setText(R.string.edit_profile_change_status_message_public);
                        profileView.findViewById(R.id.profile_status).setVisibility(View.GONE);
                        profileView.findViewById(R.id.my_profile_status_buttons).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_status_message).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_status_message).setOnClickListener(edit_status_click_listener);
                        profileView.findViewById(R.id.profile_edit_status_message).setOnClickListener(edit_status_click_listener);
                        //Information
                        profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_location_value).setClickable(true);
                        profileView.findViewById(R.id.profile_information_location_value).setOnClickListener(location);
                        profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_location_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_location_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_age_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_age_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_gender_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_languages_value).setClickable(true);
                        profileView.findViewById(R.id.profile_information_languages_value).setOnClickListener(language);
                        profileView.findViewById(R.id.my_profile_information_languages_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_languages_show)).clearColorFilter();
                        profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_languages_show).setOnClickListener(edit_visibility_click_listener);
                        //Edit button
                        profileView.findViewById(R.id.my_profile_edit_button).setVisibility(View.GONE);
                        break;
                }
                break;
        }
    }

    private View.OnClickListener language = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("LANG");
            //getLanguages();
            Dialog dialogN = onCreateDialog(LANGUAGE_DIALOG_ID);
            dialogN.show();
        }
    };

    private View.OnClickListener location = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            locationStep = 0;
            System.out.println("LOCATION");
            locationData();
            Dialog dialog = onCreateDialog(LOCATION_DIALOG0_ID);
            dialog.show();
        }
    };

    protected Dialog onCreateDialog(int id) {
        switch (id) {
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
        languagesSelection = new boolean[4];
        selectedLanguages = new ArrayList();  // Where we track the selected items
    }

    public Dialog languagesDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select your languages")
                .setMultiChoiceItems(languages, languagesSelection,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedLanguages.add(languages[which]);
                                    languagesSelection[which] = isChecked;
                                    System.out.println(isChecked);
                                } else if (selectedLanguages.contains(languages[which])) {
                                    selectedLanguages.remove(languages[which]);
                                    languagesSelection[which] = isChecked;
                                    for (int i = 0; i <selectedLanguages.size() ; i++) {
                                        System.out.println(selectedLanguages.get(i));
                                    }
                                    System.out.println(languages[which]);
                                    System.out.println(isChecked);
                                }
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (selectedLanguages.size() != 0) {
                            String languagesString = selectedLanguages.get(0);
                            for (int i = 1; i < selectedLanguages.size(); i++) {
                                languagesString = languagesString +"; "+ selectedLanguages.get(i);
                            }
                            modifiedUser.getUserPublicProfile().setLanguages(selectedLanguages);
                            modifiedUser.getUserPrivateProfile().setLanguages(selectedLanguages);
                            ((TextView) ((Activity)context).findViewById(R.id.profile_information_languages_value)).setText(languagesString);
                        } else if (selectedLanguages.size() == 0){
                            modifiedUser.getUserPublicProfile().setLanguages(selectedLanguages);
                            modifiedUser.getUserPrivateProfile().setLanguages(selectedLanguages);
                            ((TextView) ((Activity)context).findViewById(R.id.profile_information_languages_value)).setText("");
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

        region2 = new String[4];
        region2[0] = "And";
        region2[1] = "Cat";
        region2[2] = "Glz";
        region2[3] = "PV";

        regions = new ArrayList<String[]>();
        regions.add(0, null);
        regions.add(1, null);
        regions.add(2, region);
        regions.add(3, region2);
        cities = new ArrayList<String[]>();
        cities.add(0, null);
        cities.add(1, null);
        cities.add(2, city);
        cities.add(3, null);
    }

    protected Dialog locationDialog0(){
        System.out.println("bnmlñ");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (context==null)
            System.out.println("null");
        else if (context!=null)
            System.out.println("not null");
        builder.setTitle(titles[locationStep]).setItems(countries, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("CLICK!");
                locationString = countries[which];
                ((TextView) ((Activity)context).findViewById(R.id.profile_information_location_value)).setText(locationString);
                modifiedUser.getUserPrivateProfile().setLocation(locationString);
                modifiedUser.getUserPublicProfile().setLocation(locationString);
                locationStep++;
                locationIndex = which;
                if (regions.get(which) != null) {
                    Dialog dialog2 = onCreateDialog(LOCATION_DIALOG1_ID);
                    dialog2.show();
                }
            }
        });
        return builder.create();
    }

    protected Dialog locationDialog1(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titles[locationStep]).setItems(regions.get(locationIndex), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                locationString = locationString +", "+ regions.get(locationIndex)[which];
                ((TextView) ((Activity)context).findViewById(R.id.profile_information_location_value)).setText(locationString);
                modifiedUser.getUserPrivateProfile().setLocation(locationString);
                modifiedUser.getUserPublicProfile().setLocation(locationString);
                locationStep++;
                locationIndex = which;
                if (cities.get(which) != null) {
                    Dialog dialog3 = onCreateDialog(LOCATION_DIALOG2_ID);
                    dialog3.show();
                }
            }
        });
        return builder.create();
    }
    protected Dialog locationDialog2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)context));
        builder.setTitle(titles[locationStep]).setItems(cities.get(locationIndex), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                locationString = locationString +", "+ cities.get(locationIndex)[which];
                ((TextView) ((Activity)context).findViewById(R.id.profile_information_location_value)).setText(locationString);
                modifiedUser.getUserPrivateProfile().setLocation(locationString);
                modifiedUser.getUserPublicProfile().setLocation(locationString);
            }
        });
        return builder.create();
    }

    private void setData() {
        if (((profileViewType != ProfileView.Edit) && (user == null)) || ((profileViewType == ProfileView.Edit) && (modifiedUser == null))) return;

        ShowInProfile showInProfile;

        switch (profileViewType) {
            case Private:
                ((TextView) actionBar.findViewById(R.id.profile_title_text)).setText(String.format("%s %s", user.getUserPrivateProfile().getFirstName(), user.getUserPrivateProfile().getLastName()));

                ((TextView) profileView.findViewById(R.id.profile_full_name_value)).setText(String.format("%s %s", user.getUserPrivateProfile().getFirstName(), user.getUserPrivateProfile().getLastName()));
                ((TextView) profileView.findViewById(R.id.profile_public_name)).setText(String.format("%s%s", this.context.getResources().getString(R.string.public_username_identifier_character), user.getUserPublicProfile().getPublicName()));
                if (user.getUserPublicProfile().getColor() != null)
                    ((TextView) profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(user.getUserPublicProfile().getColor()));

                if (user.getUserPrivateProfile().getImageURL() != null) {
                    user.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"loadBigPhoto",EventArgs.class));
                    user.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.xlarge,0);
                }

                if (user.getUserPublicProfile().getImageURL() != null) {
                    user.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"loadSmallPhoto",EventArgs.class));
                    user.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.medium,0);
                }

                if ((user.getUserPrivateProfile().getLocation() == null) || (user.getUserPrivateProfile().getLocation().isEmpty()))
                    profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText(user.getUserPrivateProfile().getLocation());
                }

                if ((user.getUserPrivateProfile().getBirthdate() == null) || (!user.getUserPrivateProfile().getShowAge()))
                    profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                    String age = String.format("%s %s", DateFormatter.getUserAge(user.getUserPrivateProfile().getBirthdate()), context.getString(R.string.profile_information_age_append_value_after));
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText(age);
                }

                if ((user.getUserPrivateProfile().getSex() == null) || (user.getUserPrivateProfile().getSex().isEmpty()))
                    profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                    if (user.getUserPrivateProfile().getSex().equalsIgnoreCase("male")) {
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_male_value);
                        if (user.getUserPrivateProfile().getProfileImage() == null)
                            ((ImageView) profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.default_profile_image_male);
                    } else {
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_female_value);
                        if (user.getUserPrivateProfile().getProfileImage() == null)
                            ((ImageView) profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.default_profile_image_female);
                    }
                }

                if ((user.getUserPrivateProfile().getLanguages() == null) || (user.getUserPrivateProfile().getLanguages().isEmpty()))
                    profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                    String Language = "";
                    ArrayList<String> Languages = user.getUserPrivateProfile().getLanguages();
                    for (String lang : Languages)
                        Language += ((Language.isEmpty()) ? "" : "; ") + lang;
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText(Language);
                }

                break;
            case Public:
                ((TextView) actionBar.findViewById(R.id.profile_title_text)).setText(String.format("%s%s", this.context.getResources().getString(R.string.public_username_identifier_character), user.getUserPublicProfile().getPublicName()));

                ((TextView) profileView.findViewById(R.id.profile_public_name)).setText(String.format("%s%s", this.context.getResources().getString(R.string.public_username_identifier_character), user.getUserPublicProfile().getPublicName()));
                if (user.getUserPublicProfile().getColor() != null)
                    ((TextView) profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(user.getUserPublicProfile().getColor()));

                if (user.getUserPublicProfile().getImageURL() != null) {
                    user.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"loadBigPhoto",EventArgs.class));
                    user.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.xlarge,0);
                }

                if ((user.getUserPublicProfile().getLocation() == null) || (user.getUserPublicProfile().getLocation().isEmpty()) || (!user.getUserPublicProfile().getShowLocation()))
                    profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText(user.getUserPublicProfile().getLocation());
                }

                if ((user.getUserPublicProfile().getBirthdate() == null) || (!user.getUserPublicProfile().getShowAge()))
                    profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                    String age = String.format("%s %s", DateFormatter.getUserAge(user.getUserPublicProfile().getBirthdate()), context.getString(R.string.profile_information_age_append_value_after));
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText(age);
                }

                if ((user.getUserPublicProfile().getSex() == null) || (user.getUserPublicProfile().getSex().isEmpty()) || (!user.getUserPublicProfile().getShowSex()))
                    profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                    if (user.getUserPublicProfile().getSex().equalsIgnoreCase("male"))
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_male_value);
                    else
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_female_value);
                }

                if ((user.getUserPublicProfile().getLanguages() == null) || (user.getUserPublicProfile().getLanguages().isEmpty()))
                    profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                    String Language = "";
                    ArrayList<String> Languages = user.getUserPublicProfile().getLanguages();
                    for (String lang : Languages)
                        Language += ((Language.isEmpty()) ? "" : "; ") + lang;
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText(Language);
                }

                break;
            case Own:
                ((TextView) actionBar.findViewById(R.id.profile_title_text)).setText(R.string.profile_title);

                ((TextView) profileView.findViewById(R.id.profile_full_name_value)).setText(String.format("%s %s", user.getUserPrivateProfile().getFirstName(), user.getUserPrivateProfile().getLastName()));
                ((TextView) profileView.findViewById(R.id.profile_public_name)).setText(String.format("%s%s", this.context.getResources().getString(R.string.public_username_identifier_character), user.getUserPublicProfile().getPublicName()));
                if (user.getUserPublicProfile().getColor() != null)
                    ((TextView) profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(user.getUserPublicProfile().getColor()));

                if (user.getUserPrivateProfile().getImageURL() != null) {
                    user.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"loadBigPhoto",EventArgs.class));
                    user.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.xlarge,0);
                }

                if (user.getUserPublicProfile().getImageURL() != null) {
                    user.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"loadSmallPhoto",EventArgs.class));
                    user.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.medium,0);
                }

                if ((user.getUserPrivateProfile().getLocation() == null) || (user.getUserPrivateProfile().getLocation().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText(user.getUserPrivateProfile().getLocation());
                }

                if (user.getUserPrivateProfile().getBirthdate() == null) {
                    profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                    String age = String.format("%s %s", DateFormatter.getUserAge(user.getUserPrivateProfile().getBirthdate()), context.getString(R.string.profile_information_age_append_value_after));
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText(age);
                }

                if ((user.getUserPrivateProfile().getSex() == null) || (user.getUserPrivateProfile().getSex().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                    if (user.getUserPrivateProfile().getSex().equalsIgnoreCase("male")) {
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_male_value);
                        if (user.getUserPrivateProfile().getProfileImage() == null)
                            ((ImageView) profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.default_profile_image_male);
                    } else {
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_female_value);
                        if (user.getUserPrivateProfile().getProfileImage() == null)
                            ((ImageView) profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.default_profile_image_female);
                    }
                }

                if ((user.getUserPrivateProfile().getLanguages() == null) || (user.getUserPrivateProfile().getLanguages().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                    String Language = "";
                    ArrayList<String> Languages = user.getUserPrivateProfile().getLanguages();
                    for (String lang : Languages)
                        Language += ((Language.isEmpty()) ? "" : "; ") + lang;
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText(Language);
                }

                showInProfile = ShowInProfile.Both;
                if (!user.getUserPublicProfile().getShowLocation())
                    showInProfile = ShowInProfile.Private;
                ((ImageView) profileView.findViewById(R.id.my_profile_information_location_show)).setImageResource(getImageResourceToShowInProfile(showInProfile));

                showInProfile = ShowInProfile.None;
                if (user.getUserPrivateProfile().getShowAge())
                    showInProfile = ShowInProfile.Private;
                if (user.getUserPublicProfile().getShowAge()) {
                    if (showInProfile == ShowInProfile.Private) showInProfile = ShowInProfile.Both;
                    else showInProfile = ShowInProfile.Public;
                }
                ((ImageView) profileView.findViewById(R.id.my_profile_information_age_show)).setImageResource(getImageResourceToShowInProfile(showInProfile));

                ((ImageView) profileView.findViewById(R.id.my_profile_information_gender_show)).setImageResource(getImageResourceToShowInProfile(ShowInProfile.Both));

                ((ImageView) profileView.findViewById(R.id.my_profile_information_languages_show)).setImageResource(getImageResourceToShowInProfile(ShowInProfile.Both));

                break;
            case Edit:
                if (profileType == ProfileType.Private)
                    ((TextView) profileView.findViewById(R.id.profile_full_name_value)).setText(String.format("%s %s", modifiedUser.getUserPrivateProfile().getFirstName(), modifiedUser.getUserPrivateProfile().getLastName()));
                ((TextView) profileView.findViewById(R.id.profile_public_name)).setText(String.format("%s%s", this.context.getResources().getString(R.string.public_username_identifier_character), modifiedUser.getUserPublicProfile().getPublicName()));
                if (modifiedUser.getUserPublicProfile().getColor() != null)
                    ((TextView) profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(modifiedUser.getUserPublicProfile().getColor()));

                if ((profileType == ProfileType.Private) && (user.getUserPrivateProfile().getImageURL() != null)) {
                    user.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"loadBigPhoto",EventArgs.class));
                    user.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.xlarge,0);
                }

                if (user.getUserPublicProfile().getImageURL() != null) {
                    if (profileType == ProfileType.Private) {
                        user.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "loadSmallPhoto", EventArgs.class));
                        user.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.medium, 0);
                    } else {
                        user.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "loadBigPhoto", EventArgs.class));
                        user.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.xlarge, 0);
                    }
                }

                if ((modifiedUser.getUserPrivateProfile().getLocation() == null) || (modifiedUser.getUserPrivateProfile().getLocation().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                   ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_block_location).setVisibility(View.VISIBLE);
                   ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText(modifiedUser.getUserPrivateProfile().getLocation());
                }

                if (modifiedUser.getUserPrivateProfile().getBirthdate() == null) {
                    profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                   ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_block_age).setVisibility(View.VISIBLE);
                    String age = String.format("%s %s", DateFormatter.getUserAge(modifiedUser.getUserPrivateProfile().getBirthdate()), context.getString(R.string.profile_information_age_append_value_after));
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText(age);
                }

                if ((modifiedUser.getUserPrivateProfile().getSex() == null) || (modifiedUser.getUserPrivateProfile().getSex().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_block_gender).setVisibility(View.VISIBLE);
                    if (modifiedUser.getUserPrivateProfile().getSex().equalsIgnoreCase("male")) {
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_male_value);
                        if (modifiedUser.getUserPrivateProfile().getProfileImage() == null)
                            ((ImageView) profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.default_profile_image_male);
                    } else {
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_female_value);
                        if (modifiedUser.getUserPrivateProfile().getProfileImage() == null)
                            ((ImageView) profileView.findViewById(R.id.profile_big_photo_thumbnail)).setImageResource(R.drawable.default_profile_image_female);
                    }
                }

                if ((modifiedUser.getUserPrivateProfile().getLanguages() == null) || (modifiedUser.getUserPrivateProfile().getLanguages().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_block_languages).setVisibility(View.VISIBLE);
                    String Language = "";
                    ArrayList<String> Languages = modifiedUser.getUserPrivateProfile().getLanguages();
                    for (String lang : Languages)
                        Language += ((Language.isEmpty()) ? "" : "; ") + lang;
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText(Language);
                }

                float visible_alpha;
                float hidden_alpha;

                TypedValue alpha = new TypedValue();
                this.context.getResources().getValue(R.color.edit_profile_information_item_visible_alpha, alpha, true);
                visible_alpha = alpha.getFloat();
                this.context.getResources().getValue(R.color.edit_profile_information_item_hidden_alpha, alpha, true);
                hidden_alpha = alpha.getFloat();

                showInProfile = ShowInProfile.Both;
                if (!modifiedUser.getUserPublicProfile().getShowLocation())
                    showInProfile = ShowInProfile.Private;
                ((ImageView) profileView.findViewById(R.id.my_profile_information_location_show)).setImageResource(getImageResourceToShowInProfile(showInProfile));

                if ((profileType == ProfileType.Private) || (showInProfile == ShowInProfile.Both)) {
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_location), visible_alpha);
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_location_value), visible_alpha);
                } else {
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_location), hidden_alpha);
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_location_value), hidden_alpha);
                }

                showInProfile = ShowInProfile.None;
                if (modifiedUser.getUserPrivateProfile().getShowAge())
                    showInProfile = ShowInProfile.Private;
                if (modifiedUser.getUserPublicProfile().getShowAge()) {
                    if (showInProfile == ShowInProfile.Private) showInProfile = ShowInProfile.Both;
                    else showInProfile = ShowInProfile.Public;
                }
                ((ImageView) profileView.findViewById(R.id.my_profile_information_age_show)).setImageResource(getImageResourceToShowInProfile(showInProfile));

                if (((profileType == ProfileType.Private) && (showInProfile == ShowInProfile.Private)) || ((profileType == ProfileType.Public) && (showInProfile == ShowInProfile.Public)) || (showInProfile == ShowInProfile.Both)) {
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_age), visible_alpha);
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_age_value), visible_alpha);
                } else {
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_age), hidden_alpha);
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_age_value), hidden_alpha);
                }

                showInProfile = ShowInProfile.Both;
                if (!modifiedUser.getUserPublicProfile().getShowSex())
                    showInProfile = ShowInProfile.Private;
                ((ImageView)profileView.findViewById(R.id.my_profile_information_gender_show)).setImageResource(getImageResourceToShowInProfile(showInProfile));

                if ((profileType == ProfileType.Private) || (showInProfile == ShowInProfile.Both)) {
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_gender),visible_alpha);
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_gender_value),visible_alpha);
                } else {
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_gender),hidden_alpha);
                    StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_gender_value),hidden_alpha);
                }

                ((ImageView)profileView.findViewById(R.id.my_profile_information_languages_show)).setImageResource(getImageResourceToShowInProfile(ShowInProfile.Both));
                StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_languages),visible_alpha);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.profile_information_languages_value),visible_alpha);

                break;
        }

        showStatusMessage();
    }

    private int getImageResourceToShowInProfile(ShowInProfile showInProfile) {
        switch (showInProfile) {
            case Both:
                return R.drawable.user_profile_show_profile_element_both;
            case Private:
                return R.drawable.user_profile_show_profile_element_only_private;
            case Public:
                return R.drawable.user_profile_show_profile_element_only_public;
            default:
                return R.drawable.user_profile_show_profile_element_both_off;
        }
    }

    private void showStatusMessage() {
        String statusMessage;

        float active_alpha;
        float inactive_alpha;

        TypedValue alpha = new TypedValue();
        this.context.getResources().getValue(R.color.my_profile_status_active_alpha,alpha,true);
        active_alpha = alpha.getFloat();
        this.context.getResources().getValue(R.color.my_profile_status_inactive_alpha,alpha,true);
        inactive_alpha = alpha.getFloat();

        switch (profileType) {
            case Private:
                if (profileViewType != ProfileView.Edit) {
                    if ((user.getUserPrivateProfile().getStatusMessage() == null) || (user.getUserPrivateProfile().getStatusMessage().isEmpty()))
                        statusMessage = this.context.getResources().getString(R.string.profile_default_private_status_message);
                    else
                        statusMessage = user.getUserPrivateProfile().getStatusMessage();
                } else {
                    if ((modifiedUser.getUserPrivateProfile().getStatusMessage() == null) || (modifiedUser.getUserPrivateProfile().getStatusMessage().isEmpty()))
                        statusMessage = this.context.getResources().getString(R.string.profile_default_private_status_message);
                    else
                        statusMessage = modifiedUser.getUserPrivateProfile().getStatusMessage();
                }
                ((TextView)profileView.findViewById(R.id.profile_status_message)).setText(statusMessage);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.my_profile_status_private_button),active_alpha);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.my_profile_status_public_button),inactive_alpha);
                break;
            case Public:
                if (profileViewType != ProfileView.Edit) {
                    if ((user.getUserPublicProfile().getStatusMessage() == null) || (user.getUserPublicProfile().getStatusMessage().isEmpty()))
                        statusMessage = this.context.getResources().getString(R.string.profile_default_public_status_message);
                    else
                        statusMessage = user.getUserPublicProfile().getStatusMessage();
                } else {
                    if ((modifiedUser.getUserPublicProfile().getStatusMessage() == null) || (modifiedUser.getUserPublicProfile().getStatusMessage().isEmpty()))
                        statusMessage = this.context.getResources().getString(R.string.profile_default_public_status_message);
                    else
                        statusMessage = modifiedUser.getUserPublicProfile().getStatusMessage();
                }
                ((TextView)profileView.findViewById(R.id.profile_status_message)).setText(statusMessage);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.my_profile_status_public_button),active_alpha);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.my_profile_status_private_button),inactive_alpha);
                break;
        }
    }

    private void enterEditMode() {
        this.modifiedUser = new User(user.toFormat(new LOCAL_USER_PROFILE()));
        this.profileType = ProfileType.Private;
        this.profileViewType = ProfileView.Edit;

        this.actionBar = ((Main)this.context).ChangeActionBar(R.layout.profile_edit_action_bar);

        this.actionBar.findViewById(R.id.private_profile_tab_button).setOnClickListener(change_edit_type);
        this.actionBar.findViewById(R.id.public_profile_tab_button).setOnClickListener(change_edit_type);
        this.actionBar.findViewById(R.id.save_button).setOnClickListener(accept_profile_changes);

        this.adjustView();
        this.setData();

        ((Main)context).floatingPanel.setAllowSwipeToMovePanels(false);
    }

    protected View.OnClickListener edit_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enterEditMode();
        }
    };

    protected View.OnClickListener accept_profile_changes = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveProfile();
        }
    };

    protected void saveProfile() {

        user.EditProfile(new EventHandler<CommandCallbackEventArgs>(this,"onUpdatedProfile",CommandCallbackEventArgs.class),modifiedUser);
        //Exit edit mode
        modifiedUser = null;
        profileType = ProfileType.Private;
        profileViewType = ProfileView.Own;

        actionBar = ((Main)context).ChangeActionBar(R.layout.profile_action_bar);
        actionBar.findViewById(R.id.profile_action_bar_menu_clickable).setOnClickListener(((Main)context).menuIcon_ClickListener);
        actionBar.findViewById(R.id.profile_action_bar_myPhoto_button).setOnClickListener(((Main)context).appIcon_ClickListener);

        adjustView();
        setData();

        ((Main)context).floatingPanel.resetAllowSwipeToMovePanels();
    }

    public void onUpdatedProfile(Object sender,CommandCallbackEventArgs eventArgs) {
        COMMON common = null;
        for (Format format : eventArgs.getReceivedFormats())
            if (format instanceof COMMON)
                common = (COMMON)format;

        if ((common != null) && (common.STATUS != null) && (common.STATUS.equalsIgnoreCase("OK"))) {
            ((Activity)this.context).runOnUiThread(new Runnable(){
                public void run() {
                    if ((profileView != null) && (actionBar != null))
                        setData();
                }
            });
        }
    }

    protected View.OnClickListener change_edit_type = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.private_profile_tab_button) {
                profileType = ProfileType.Private;
            } else {
                profileType = ProfileType.Public;
            }
            adjustView();
            setData();
        }
    };

    protected View.OnClickListener change_shown_message = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.my_profile_status_private_button)
                profileType = ProfileType.Private;
            else
                profileType = ProfileType.Public;

            showStatusMessage();
        }
    };


    protected View.OnClickListener edit_visibility_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (profileViewType != ProfileView.Edit) return;
            switch (profileType) {
                case Private:
                    if (v.getId() == R.id.my_profile_information_location_show) {
                        Log.w("Edit private profile","TODO: Add private location show to private profile");
                    } else if (v.getId() == R.id.my_profile_information_age_show) {
                        modifiedUser.getUserPrivateProfile().setShowAge(!modifiedUser.getUserPrivateProfile().getShowAge());
                        setData();
                    } else if (v.getId() == R.id.my_profile_information_gender_show) {
                        Log.w("Edit private profile","TODO: Show message error: Not allowed to edit gender visibility");
                    } else if (v.getId() == R.id.my_profile_information_languages_show) {
                        Log.w("Edit private profile","TODO: Show message error: Not allowed to edit language visibility");
                    }
                    break;
                case Public:
                    if (v.getId() == R.id.my_profile_information_location_show) {
                        modifiedUser.getUserPublicProfile().setShowLocation(!modifiedUser.getUserPublicProfile().getShowLocation());
                        setData();
                    } else if (v.getId() == R.id.my_profile_information_age_show) {
                        modifiedUser.getUserPublicProfile().setShowAge(!modifiedUser.getUserPublicProfile().getShowAge());
                        setData();
                    } else if (v.getId() == R.id.my_profile_information_gender_show) {
                        modifiedUser.getUserPublicProfile().setShowSex(!modifiedUser.getUserPublicProfile().getShowSex());
                        setData();
                    } else if (v.getId() == R.id.my_profile_information_languages_show) {
                        Log.w("Edit public profile","TODO: Show message error: Not allowed to edit language visibility");
                    }
                    break;
            }
        }
    };

    protected View.OnClickListener edit_status_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView statusMessageView = (TextView)profileView.findViewById(R.id.profile_status_message);
            EditText statusMessageEdit = (EditText)profileView.findViewById(R.id.edit_profile_status_message);
            statusMessageEdit.setHint(statusMessageView.getText());
            statusMessageEdit.setText("");
            statusMessageView.setVisibility(View.GONE);
            profileView.findViewById(R.id.edit_profile_status).setVisibility(View.VISIBLE);
            ((TextView)profileView.findViewById(R.id.edit_profile_status_length)).setText(String.format("%d / 100",statusMessageView.getText().length()));
            statusMessageEdit.requestFocus();
            statusMessageEdit.addTextChangedListener(edit_status_changed);
            profileView.findViewById(R.id.edit_profile_view_root).setOnClickListener(finishEditStatus);
            actionBar.findViewById(R.id.edit_profile_action_bar_root).setOnClickListener(finishEditStatus);

            //Disable action Bar click listeners
            actionBar.findViewById(R.id.private_profile_tab_button).setClickable(false);
            actionBar.findViewById(R.id.public_profile_tab_button).setClickable(false);
            actionBar.findViewById(R.id.save_button).setClickable(false);

            //Disable profile View click listeners
            profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
            profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);

            profileView.findViewById(R.id.profile_status_message).setClickable(false);
            profileView.findViewById(R.id.profile_edit_status_message).setClickable(false);

            profileView.findViewById(R.id.my_profile_information_location_show).setClickable(false);
            profileView.findViewById(R.id.my_profile_information_age_show).setClickable(false);
            profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(false);
            profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(false);
        }
    };

    protected TextWatcher edit_status_changed = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0)
                ((TextView)profileView.findViewById(R.id.edit_profile_status_length)).setText(String.format("%d / 100",s.length()));
            else
                ((TextView)profileView.findViewById(R.id.edit_profile_status_length)).setText(String.format("%d / 100", ((TextView) profileView.findViewById(R.id.profile_status_message)).getText().length()));
        }
    };

    protected View.OnClickListener finishEditStatus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView statusMessageView = (TextView)profileView.findViewById(R.id.profile_status_message);
            EditText statusMessageEdit = (EditText)profileView.findViewById(R.id.edit_profile_status_message);

            if (statusMessageEdit.getText().length() > 0) {
                statusMessageView.setText(statusMessageEdit.getText());
                switch (profileType) {
                    case Private:
                        modifiedUser.getUserPrivateProfile().setStatusMessage(statusMessageView.getText().toString());
                        break;
                    case Public:
                        modifiedUser.getUserPublicProfile().setStatusMessage(statusMessageView.getText().toString());
                        break;
                }
            }

            Keyboard.HideKeyboard(((Activity)context));

            profileView.findViewById(R.id.edit_profile_status).setVisibility(View.GONE);
            statusMessageView.setVisibility(View.VISIBLE);
            statusMessageEdit.removeTextChangedListener(edit_status_changed);

            profileView.findViewById(R.id.edit_profile_view_root).setOnClickListener(null);
            profileView.findViewById(R.id.edit_profile_view_root).setClickable(false);
            actionBar.findViewById(R.id.edit_profile_action_bar_root).setOnClickListener(null);
            actionBar.findViewById(R.id.edit_profile_action_bar_root).setClickable(false);

            //Enable action Bar click listeners
            actionBar.findViewById(R.id.private_profile_tab_button).setClickable(true);
            actionBar.findViewById(R.id.public_profile_tab_button).setClickable(true);
            actionBar.findViewById(R.id.save_button).setClickable(true);

            //Enable profile View click listeners
            profileView.findViewById(R.id.profile_full_name_value).setClickable(true);
            profileView.findViewById(R.id.profile_edit_full_name).setClickable(true);

            profileView.findViewById(R.id.profile_status_message).setClickable(true);
            profileView.findViewById(R.id.profile_edit_status_message).setClickable(true);

            profileView.findViewById(R.id.my_profile_information_location_show).setClickable(true);
            profileView.findViewById(R.id.my_profile_information_age_show).setClickable(true);
            profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(true);
            profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(true);
        }
    };

    protected View.OnClickListener edit_name_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (modifiedUser.getUserPrivateProfile().getFirstName() != null)
                ((EditText)profileView.findViewById(R.id.edit_profile_first_name)).setHint(modifiedUser.getUserPrivateProfile().getFirstName());
            else
                ((EditText)profileView.findViewById(R.id.edit_profile_first_name)).setHint(context.getResources().getString(R.string.register_first_step_name_hint));
            ((EditText)profileView.findViewById(R.id.edit_profile_first_name)).setText("");

            if (modifiedUser.getUserPrivateProfile().getLastName() != null)
                ((EditText)profileView.findViewById(R.id.edit_profile_last_name)).setHint(modifiedUser.getUserPrivateProfile().getLastName());
            else
                ((EditText)profileView.findViewById(R.id.edit_profile_last_name)).setHint(context.getResources().getString(R.string.register_first_step_surname_hint));
            ((EditText)profileView.findViewById(R.id.edit_profile_last_name)).setText("");

            profileView.findViewById(R.id.profile_full_name).setVisibility(View.GONE);
            profileView.findViewById(R.id.edit_profile_full_name).setVisibility(View.VISIBLE);

            profileView.findViewById(R.id.edit_profile_first_name).requestFocus();

            profileView.findViewById(R.id.edit_profile_view_root).setOnClickListener(finishEditName);
            actionBar.findViewById(R.id.edit_profile_action_bar_root).setOnClickListener(finishEditName);

            //Disable action Bar click listeners
            actionBar.findViewById(R.id.private_profile_tab_button).setClickable(false);
            actionBar.findViewById(R.id.public_profile_tab_button).setClickable(false);
            actionBar.findViewById(R.id.save_button).setClickable(false);

            //Disable profile View click listeners
            profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
            profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);

            profileView.findViewById(R.id.profile_status_message).setClickable(false);
            profileView.findViewById(R.id.profile_edit_status_message).setClickable(false);

            profileView.findViewById(R.id.my_profile_information_location_show).setClickable(false);
            profileView.findViewById(R.id.my_profile_information_age_show).setClickable(false);
            profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(false);
            profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(false);
        }
    };

    protected View.OnClickListener finishEditName = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText firstName = (EditText)profileView.findViewById(R.id.edit_profile_first_name);
            EditText lastName = (EditText)profileView.findViewById(R.id.edit_profile_last_name);

            if (firstName.getText().length() > 0) {
                modifiedUser.getUserPrivateProfile().setFirstName(firstName.getText().toString());
            }

            if (lastName.getText().length() > 0) {
                modifiedUser.getUserPrivateProfile().setLastName(lastName.getText().toString());
            }

            Keyboard.HideKeyboard(((Activity)context));

            ((TextView) profileView.findViewById(R.id.profile_full_name_value)).setText(String.format("%s %s", modifiedUser.getUserPrivateProfile().getFirstName(), modifiedUser.getUserPrivateProfile().getLastName()));

            profileView.findViewById(R.id.edit_profile_full_name).setVisibility(View.GONE);
            profileView.findViewById(R.id.profile_full_name).setVisibility(View.VISIBLE);

            profileView.findViewById(R.id.edit_profile_view_root).setOnClickListener(null);
            profileView.findViewById(R.id.edit_profile_view_root).setClickable(false);
            actionBar.findViewById(R.id.edit_profile_action_bar_root).setOnClickListener(null);
            actionBar.findViewById(R.id.edit_profile_action_bar_root).setClickable(false);

            //Enable action Bar click listeners
            actionBar.findViewById(R.id.private_profile_tab_button).setClickable(true);
            actionBar.findViewById(R.id.public_profile_tab_button).setClickable(true);
            actionBar.findViewById(R.id.save_button).setClickable(true);

            //Enable profile View click listeners
            profileView.findViewById(R.id.profile_full_name_value).setClickable(true);
            profileView.findViewById(R.id.profile_edit_full_name).setClickable(true);

            profileView.findViewById(R.id.profile_status_message).setClickable(true);
            profileView.findViewById(R.id.profile_edit_status_message).setClickable(true);

            profileView.findViewById(R.id.my_profile_information_location_show).setClickable(true);
            profileView.findViewById(R.id.my_profile_information_age_show).setClickable(true);
            profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(true);
            profileView.findViewById(R.id.my_profile_information_languages_show).setClickable(true);
        }
    };
}
