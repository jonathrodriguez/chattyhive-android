package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;
import com.chattyhive.chattyhive.framework.Util.ViewPair;

import java.util.ArrayList;

/**
 * Created by Jonathan on 20/05/2014.
 */
public class Profile {
    Context context;
    View profileView;
    View actionBar;

    private enum ProfileType {Private, Public}
    private enum ProfileView {Private, Public, Own, Edit}
    private enum ShowInProfile { None, Private, Public, Both}

    private User user;
    private User modifiedUser; //This is for profile edition.
    private ProfileType profileType;
    private ProfileView profileViewType;

    public Profile(Context context) {
        this.context = context;
    }

    protected View.OnClickListener open_profile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OpenProfile(((Main)context).controller.getMe(),ProfileType.Private);
        }
    };

    protected void OpenProfile(User user, ProfileType profileType) {
        ViewPair profileViewPair = ((Main)context).ShowLayout(R.layout.main_panel_profile_layout,R.layout.profile_action_bar);

        profileView = profileViewPair.getMainView();
        actionBar = profileViewPair.getActionBarView();

        this.user = user;
        this.profileType = profileType;

        this.profileViewType = (user.isMe())?ProfileView.Own:((profileType == ProfileType.Private)?ProfileView.Private:ProfileView.Public);

        adjustView();
        setData();

        actionBar.findViewById(R.id.profile_action_bar_menu_clickable).setOnClickListener(((Main)context).menuIcon_ClickListener);
        actionBar.findViewById(R.id.profile_action_bar_myPhoto_button).setOnClickListener(((Main)context).appIcon_ClickListener);
        ((Main)context).menuIcon_ClickListener.onClick(actionBar.findViewById(R.id.profile_action_bar_menu_clickable));

        if (user.isMe()) {
            profileView.findViewById(R.id.my_profile_status_public_button).setOnClickListener(change_shown_message);
            profileView.findViewById(R.id.my_profile_status_private_button).setOnClickListener(change_shown_message);
            profileView.findViewById(R.id.my_profile_edit_button).setOnClickListener(edit_button_click);
        }
    }

    private void adjustView() {
        switch (profileViewType) {
            case Private:
                //Privacy note
                profileView.findViewById(R.id.profile_edit_privacy_notification).setVisibility(View.GONE);
                //Images
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_small_photo_thumbnail).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.color.profile_photo_background_color);
                profileView.findViewById(R.id.profile_small_photo_thumbnail).setBackgroundResource(R.color.profile_photo_background_color);
                //Full name and public name
                profileView.findViewById(R.id.profile_full_name).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_full_name_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
                profileView.findViewById(R.id.profile_edit_full_name).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);
                profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_color).setVisibility(View.GONE);
                //Status
                profileView.findViewById(R.id.profile_status_message_header).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_status_message).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_status_message).setClickable(false);
                profileView.findViewById(R.id.profile_edit_status_message).setClickable(false);
                //Information
                profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_location_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_age_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
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
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.color.profile_photo_background_color);
                //Full name and public name
                profileView.findViewById(R.id.profile_full_name).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
                profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);
                profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_color).setVisibility(View.GONE);
                //Status
                profileView.findViewById(R.id.profile_status_message_header).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_status_message).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_status_message).setOnClickListener(null);
                profileView.findViewById(R.id.profile_status_message).setClickable(false);
                profileView.findViewById(R.id.profile_edit_status_message).setClickable(false);
                //Information
                profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_location_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_age_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.GONE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
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
                profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.color.profile_photo_background_color);
                profileView.findViewById(R.id.profile_small_photo_thumbnail).setBackgroundResource(R.color.profile_photo_background_color);
                //Full name and public name
                profileView.findViewById(R.id.profile_full_name).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_full_name_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(null);
                profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
                profileView.findViewById(R.id.profile_edit_full_name).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(null);
                profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);
                profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
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
                profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.VISIBLE);
                ((ImageView)profileView.findViewById(R.id.my_profile_information_location_show)).clearColorFilter();
                profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_location_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.VISIBLE);
                ((ImageView)profileView.findViewById(R.id.my_profile_information_age_show)).clearColorFilter();
                profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_age_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.VISIBLE);
                ((ImageView)profileView.findViewById(R.id.my_profile_information_gender_show)).clearColorFilter();
                profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(null);
                profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(false);

                profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.GONE);
                profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
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
                        profileView.findViewById(R.id.profile_big_photo_thumbnail).setBackgroundResource(R.drawable.edit_profile_photo_background);
                        profileView.findViewById(R.id.profile_small_photo_thumbnail).setBackgroundResource(R.drawable.edit_profile_photo_background);
                        //Full name and public name
                        profileView.findViewById(R.id.profile_full_name).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_full_name_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(edit_name_click_listener);
                        profileView.findViewById(R.id.profile_edit_full_name).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(edit_name_click_listener);
                        profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
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
                        profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_location_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_location_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_age_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_age_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_gender_show)).clearColorFilter();
                        profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
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
                        //Full name and public name
                        profileView.findViewById(R.id.profile_full_name).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_full_name_value).setOnClickListener(null);
                        profileView.findViewById(R.id.profile_full_name_value).setClickable(false);
                        profileView.findViewById(R.id.profile_edit_full_name).setOnClickListener(null);
                        profileView.findViewById(R.id.profile_edit_full_name).setClickable(false);
                        profileView.findViewById(R.id.profile_public_name).setVisibility(View.VISIBLE);
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
                        profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_location).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_location_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_location_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_location_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_location_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_location_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_age).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_information_age_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_age_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_age_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_age_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_age_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_gender).setVisibility(View.GONE);
                        profileView.findViewById(R.id.profile_information_gender_value).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.my_profile_information_gender_show).setVisibility(View.VISIBLE);
                        ((ImageView)profileView.findViewById(R.id.my_profile_information_gender_show)).setColorFilter(this.context.getResources().getColor(R.color.edit_profile_edit_images_tint_color));
                        profileView.findViewById(R.id.my_profile_information_gender_show).setClickable(true);
                        profileView.findViewById(R.id.my_profile_information_gender_show).setOnClickListener(edit_visibility_click_listener);

                        profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_edit_information_languages).setVisibility(View.VISIBLE);
                        profileView.findViewById(R.id.profile_information_languages_value).setVisibility(View.VISIBLE);
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

    private void setData() {
        if (((profileViewType != ProfileView.Edit) && (user == null)) || ((profileViewType == ProfileView.Edit) && (modifiedUser == null))) return;

        ShowInProfile showInProfile;

        switch (profileViewType) {
            case Private:
                ((TextView) profileView.findViewById(R.id.profile_full_name_value)).setText(String.format("%s %s", user.getUserPrivateProfile().getFirstName(), user.getUserPrivateProfile().getLastName()));
                ((TextView) profileView.findViewById(R.id.profile_public_name)).setText(String.format("%s%s", this.context.getResources().getString(R.string.public_username_identifier_character), user.getUserPublicProfile().getPublicName()));
                if (user.getUserPublicProfile().getColor() != null)
                    ((TextView) profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(user.getUserPublicProfile().getColor()));

                if ((user.getUserPrivateProfile().getLocation() == null) || (user.getUserPrivateProfile().getLocation().isEmpty()))
                    profileView.findViewById(R.id.profile_information_location).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText(user.getUserPrivateProfile().getLocation());
                }

                if ((user.getUserPrivateProfile().getBirthdate() == null) || (!user.getUserPrivateProfile().getShowAge()))
                    profileView.findViewById(R.id.profile_information_age).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                    String age = String.format("%s %s", DateFormatter.getUserAge(user.getUserPrivateProfile().getBirthdate()), context.getString(R.string.profile_information_age_append_value_after));
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText(age);
                }

                if ((user.getUserPrivateProfile().getSex() == null) || (user.getUserPrivateProfile().getSex().isEmpty()))
                    profileView.findViewById(R.id.profile_information_gender).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                    if (user.getUserPrivateProfile().getSex().equalsIgnoreCase("male"))
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_male_value);
                    else
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_female_value);
                }

                if ((user.getUserPrivateProfile().getLanguages() == null) || (user.getUserPrivateProfile().getLanguages().isEmpty()))
                    profileView.findViewById(R.id.profile_information_languages).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                    String Language = "";
                    ArrayList<String> Languages = user.getUserPrivateProfile().getLanguages();
                    for (String lang : Languages)
                        Language += ((Language.isEmpty()) ? "" : "; ") + lang;
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText(Language);
                }

                break;
            case Public:
                ((TextView) profileView.findViewById(R.id.profile_public_name)).setText(String.format("%s%s", this.context.getResources().getString(R.string.public_username_identifier_character), user.getUserPublicProfile().getPublicName()));
                if (user.getUserPublicProfile().getColor() != null)
                    ((TextView) profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(user.getUserPublicProfile().getColor()));

                if ((user.getUserPublicProfile().getLocation() == null) || (user.getUserPublicProfile().getLocation().isEmpty()) || (!user.getUserPublicProfile().getShowLocation()))
                    profileView.findViewById(R.id.profile_information_location).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText(user.getUserPublicProfile().getLocation());
                }

                if ((user.getUserPublicProfile().getBirthdate() == null) || (!user.getUserPublicProfile().getShowAge()))
                    profileView.findViewById(R.id.profile_information_age).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                    String age = String.format("%s %s", DateFormatter.getUserAge(user.getUserPublicProfile().getBirthdate()), context.getString(R.string.profile_information_age_append_value_after));
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText(age);
                }

                if ((user.getUserPublicProfile().getSex() == null) || (user.getUserPublicProfile().getSex().isEmpty()) || (!user.getUserPublicProfile().getShowSex()))
                    profileView.findViewById(R.id.profile_information_gender).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                    if (user.getUserPublicProfile().getSex().equalsIgnoreCase("male"))
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_male_value);
                    else
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_female_value);
                }

                if ((user.getUserPublicProfile().getLanguages() == null) || (user.getUserPublicProfile().getLanguages().isEmpty()))
                    profileView.findViewById(R.id.profile_information_languages).setVisibility(View.GONE);
                else {
                    profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                    String Language = "";
                    ArrayList<String> Languages = user.getUserPublicProfile().getLanguages();
                    for (String lang : Languages)
                        Language += ((Language.isEmpty()) ? "" : "; ") + lang;
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText(Language);
                }

                break;
            case Own:
                ((TextView) profileView.findViewById(R.id.profile_full_name_value)).setText(String.format("%s %s", user.getUserPrivateProfile().getFirstName(), user.getUserPrivateProfile().getLastName()));
                ((TextView) profileView.findViewById(R.id.profile_public_name)).setText(String.format("%s%s", this.context.getResources().getString(R.string.public_username_identifier_character), user.getUserPublicProfile().getPublicName()));
                if (user.getUserPublicProfile().getColor() != null)
                    ((TextView) profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(user.getUserPublicProfile().getColor()));

                if ((user.getUserPrivateProfile().getLocation() == null) || (user.getUserPrivateProfile().getLocation().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText(user.getUserPrivateProfile().getLocation());
                }

                if (user.getUserPrivateProfile().getBirthdate() == null) {
                    profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                    String age = String.format("%s %s", DateFormatter.getUserAge(user.getUserPrivateProfile().getBirthdate()), context.getString(R.string.profile_information_age_append_value_after));
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText(age);
                }

                if ((user.getUserPrivateProfile().getSex() == null) || (user.getUserPrivateProfile().getSex().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                    if (user.getUserPrivateProfile().getSex().equalsIgnoreCase("male"))
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_male_value);
                    else
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_female_value);
                }

                if ((user.getUserPrivateProfile().getLanguages() == null) || (user.getUserPrivateProfile().getLanguages().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
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

                if ((modifiedUser.getUserPrivateProfile().getLocation() == null) || (modifiedUser.getUserPrivateProfile().getLocation().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                   ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_location).setVisibility(View.VISIBLE);
                   ((TextView) profileView.findViewById(R.id.profile_information_location_value)).setText(modifiedUser.getUserPrivateProfile().getLocation());
                }

                if (modifiedUser.getUserPrivateProfile().getBirthdate() == null) {
                    profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                   ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_age).setVisibility(View.VISIBLE);
                    String age = String.format("%s %s", DateFormatter.getUserAge(modifiedUser.getUserPrivateProfile().getBirthdate()), context.getString(R.string.profile_information_age_append_value_after));
                    ((TextView) profileView.findViewById(R.id.profile_information_age_value)).setText(age);
                }

                if ((modifiedUser.getUserPrivateProfile().getSex() == null) || (modifiedUser.getUserPrivateProfile().getSex().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_gender).setVisibility(View.VISIBLE);
                    if (modifiedUser.getUserPrivateProfile().getSex().equalsIgnoreCase("male"))
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_male_value);
                    else
                        ((TextView) profileView.findViewById(R.id.profile_information_gender_value)).setText(R.string.profile_information_gender_female_value);
                }

                if ((modifiedUser.getUserPrivateProfile().getLanguages() == null) || (modifiedUser.getUserPrivateProfile().getLanguages().isEmpty())) {
                    profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
                    ((TextView) profileView.findViewById(R.id.profile_information_languages_value)).setText("");
                } else {
                    profileView.findViewById(R.id.profile_information_languages).setVisibility(View.VISIBLE);
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
    }

    public void onUpdatedProfile(Object sender,CommandCallbackEventArgs eventArgs) {
        COMMON common = null;
        for (Format format : eventArgs.getReceivedFormats())
            if (format instanceof COMMON)
                common = (COMMON)format;

        if ((common != null) && (common.STATUS != null) && (common.STATUS.equalsIgnoreCase("OK"))) {
            ((Activity)this.context).runOnUiThread(new Runnable(){
                public void run() {
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

            ((TextView)profileView.findViewById(R.id.profile_full_name_value)).setText(String.format("%s %s", modifiedUser.getUserPrivateProfile().getFirstName(), modifiedUser.getUserPrivateProfile().getLastName()));

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
