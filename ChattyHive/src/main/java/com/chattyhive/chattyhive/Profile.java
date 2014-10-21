package com.chattyhive.chattyhive;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Users.User;
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

    private enum ProfileType {Private, Public};
    private enum ShowInProfile { None, Private, Public, Both};

    public Profile(Context context) {
        this.context = context;
    }

    protected View.OnClickListener open_profile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*if (((Main)context).ActiveLayoutID == R.layout.main_panel_chat_layout) {
                ((Main)context)._controller.Leave((String)((Activity)context).findViewById(R.id.main_panel_chat_name).getTag());
            }*/

            ViewPair profileViewPair = ((Main)context).ShowLayout(R.layout.main_panel_my_profile_layout,R.layout.profile_action_bar);

            profileView = profileViewPair.getMainView();
            actionBar = profileViewPair.getActionBarView();

/*            ImageView editImage = (ImageView)profileView.findViewById(R.id.my_profile_edit_button_image);
            editImage.setMaxWidth(editImage.getHeight());
            profileView.invalidate();
            profileView.requestLayout();*/

            setData();

            showStatusMessage(ProfileType.Private);

            actionBar.findViewById(R.id.profile_action_bar_menu_clickable).setOnClickListener(((Main)context).menuIcon_ClickListener);
            actionBar.findViewById(R.id.profile_action_bar_myPhoto_button).setOnClickListener(((Main)context).appIcon_ClickListener);
            ((Main)context).menuIcon_ClickListener.onClick(actionBar.findViewById(R.id.profile_action_bar_menu_clickable));

            profileView.findViewById(R.id.my_profile_status_public_button).setOnClickListener(change_shown_message);
            profileView.findViewById(R.id.my_profile_status_private_button).setOnClickListener(change_shown_message);
        }
    };

    private void setData() {
        User me = Controller.GetRunningController().getMe();

        if (me != null) {
            ((TextView) profileView.findViewById(R.id.my_profile_full_name)).setText(String.format("%s %s",me.getUserPrivateProfile().getFirstName(),me.getUserPrivateProfile().getLastName()));

            ((TextView) profileView.findViewById(R.id.my_profile_public_name)).setText(String.format("@%s",me.getUserPublicProfile().getPublicName()));
            if (me.getUserPublicProfile().getColor() != null)
                ((TextView) profileView.findViewById(R.id.my_profile_public_name)).setTextColor(Color.parseColor(me.getUserPublicProfile().getColor()));

            if (me.getUserPrivateProfile().getSex().equalsIgnoreCase("male"))
                ((TextView) profileView.findViewById(R.id.my_profile_information_gender)).setText(R.string.profile_information_gender_male_value);
            else
                ((TextView) profileView.findViewById(R.id.my_profile_information_gender)).setText(R.string.profile_information_gender_female_value);

            ((ImageView)profileView.findViewById(R.id.my_profile_information_gender_show)).setImageResource(getImageResourceToShowInProfile(ShowInProfile.Both));

            ((TextView) profileView.findViewById(R.id.my_profile_information_location)).setText(me.getUserPrivateProfile().getLocation());
            ShowInProfile showInProfile = ShowInProfile.Both;
            if (!me.getUserPublicProfile().getShowLocation())
                showInProfile = ShowInProfile.Private;
            ((ImageView)profileView.findViewById(R.id.my_profile_information_location_show)).setImageResource(getImageResourceToShowInProfile(showInProfile));

            String Language = "";
            ArrayList<String> Languages = me.getUserPrivateProfile().getLanguages();
            if (Languages != null)
                for (String lang : Languages)
                    Language += ((Language.isEmpty())?"":"; ") + lang;

            ((TextView) profileView.findViewById(R.id.my_profile_information_languages)).setText(Language);
            ((ImageView)profileView.findViewById(R.id.my_profile_information_languages_show)).setImageResource(getImageResourceToShowInProfile(ShowInProfile.Both));


            String age = String.format("%s %s",DateFormatter.getUserAge(me.getUserPrivateProfile().getBirthdate()),context.getString(R.string.profile_information_age_append_value_after));
            ((TextView)profileView.findViewById(R.id.my_profile_information_age)).setText(age);

            showInProfile = ShowInProfile.None;
            if (me.getUserPrivateProfile().getShowAge())
                showInProfile = ShowInProfile.Private;

            if (me.getUserPublicProfile().getShowAge()) {
                if (showInProfile == ShowInProfile.Private) showInProfile = ShowInProfile.Both;
                else showInProfile = ShowInProfile.Public;
            }
            ((ImageView)profileView.findViewById(R.id.my_profile_information_age_show)).setImageResource(getImageResourceToShowInProfile(showInProfile));
        }
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

    private void showStatusMessage(ProfileType profileType) {
        switch (profileType) {
            case Private:
                ((TextView)profileView.findViewById(R.id.my_profile_status_message)).setText(R.string.profile_default_private_status_message);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.my_profile_status_private_button),1f);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.my_profile_status_public_button),0.5f);
                break;
            case Public:
                ((TextView)profileView.findViewById(R.id.my_profile_status_message)).setText(R.string.profile_default_public_status_message);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.my_profile_status_public_button),1f);
                StaticMethods.SetAlpha(profileView.findViewById(R.id.my_profile_status_private_button),0.5f);
                break;
        }
    }

    protected View.OnClickListener change_shown_message = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.my_profile_status_private_button)
                showStatusMessage(ProfileType.Private);
            else
                showStatusMessage(ProfileType.Public);
        }
    };
}
