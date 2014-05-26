package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.User;
import com.chattyhive.backend.util.events.ChannelEventArgs;

/**
 * Created by Jonathan on 20/05/2014.
 */
public class Profile {
    Context context;

    public Profile(Context context) {
        this.context = context;
    }

    protected View.OnClickListener open_profile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (((Main)context).ActiveLayoutID == R.layout.main_panel_chat_layout) {
                ((Main)context)._controller.Leave((String)((Activity)context).findViewById(R.id.main_panel_chat_name).getTag());
            }

            User me = User.getMe();
            View profileView = ((Main)context).ShowLayout(R.layout.main_panel_profile_layout);
            ((TextView)profileView.findViewById(R.id.profile_first_name)).setText(me.getFirstName());
            ((TextView)profileView.findViewById(R.id.profile_last_name)).setText(me.getLastName());

            ((TextView)profileView.findViewById(R.id.profile_public_name)).setText(me.getPublicName());
            ((TextView)profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(me.color));

            ((TextView)profileView.findViewById(R.id.profile_sex)).setText(me.getSex());
            ((TextView)profileView.findViewById(R.id.profile_email)).setText(me.getEmail());

            ((TextView)profileView.findViewById(R.id.profile_location)).setText(me.getLocation());
            ((TextView)profileView.findViewById(R.id.profile_language)).setText(me.getLanguage());

            ((CheckBox)profileView.findViewById(R.id.profile_private_show_age)).setChecked(me.getPrivateShowAge());
            ((CheckBox)profileView.findViewById(R.id.profile_public_show_age)).setChecked(me.getPublicShowAge());
            ((CheckBox)profileView.findViewById(R.id.profile_show_location)).setChecked(me.getShowLocation());

            profileView.findViewById(R.id.profile_action_bar_menu_icon).setOnClickListener(((Main)context).menuIcon_ClickListener);
            profileView.findViewById(R.id.profile_action_bar_myPhoto_button).setOnClickListener(((Main)context).appIcon_ClickListener);
            ((Main)context).menuIcon_ClickListener.onClick(profileView.findViewById(R.id.profile_action_bar_menu_icon));
        }
    };
}
