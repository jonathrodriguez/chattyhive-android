package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Users.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

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
            /*if (((Main)context).ActiveLayoutID == R.layout.main_panel_chat_layout) {
                ((Main)context)._controller.Leave((String)((Activity)context).findViewById(R.id.main_panel_chat_name).getTag());
            }*/

            User me = User.getMe();

            View profileView = ((Main)context).ShowLayout(R.layout.main_panel_profile_layout,R.layout.profile_action_bar);

            ImageView editImage = (ImageView)profileView.findViewById(R.id.my_profile_edit_button_image);
            editImage.setMaxWidth(editImage.getHeight());
            profileView.invalidate();
            profileView.requestLayout();

            if (me != null) {
                ((TextView) profileView.findViewById(R.id.profile_first_name)).setText(me.getUserPrivateProfile().getFirstName());
                ((TextView) profileView.findViewById(R.id.profile_last_name)).setText(me.getUserPrivateProfile().getLastName());

                ((TextView) profileView.findViewById(R.id.profile_public_name)).setText(me.getUserPublicProfile().getPublicName());
                ((TextView) profileView.findViewById(R.id.profile_public_name)).setTextColor(Color.parseColor(me.getColor()));

                ((TextView) profileView.findViewById(R.id.profile_sex)).setText(me.getUserPrivateProfile().getSex());
                ((TextView) profileView.findViewById(R.id.profile_email)).setText(me.getEmail());

                ((TextView) profileView.findViewById(R.id.profile_location)).setText(me.getUserPrivateProfile().getLocation());
                String Language = "";
                ArrayList<String> Languages = me.getUserPrivateProfile().getLanguages();
                Iterator<String> iterator = Languages.iterator();
                if (iterator.hasNext())
                    Language = iterator.next();
                while (iterator.hasNext())
                    Language = Language.concat("; ").concat(iterator.next());
                ((TextView) profileView.findViewById(R.id.profile_language)).setText(Language);

                ((CheckBox) profileView.findViewById(R.id.profile_private_show_age)).setChecked(me.getUserPrivateProfile().getShowAge());
                ((CheckBox) profileView.findViewById(R.id.profile_public_show_age)).setChecked(me.getUserPublicProfile().getShowAge());
                ((CheckBox) profileView.findViewById(R.id.profile_show_location)).setChecked(me.getUserPublicProfile().getShowLocation());
            }

            ((Main) context).findViewById(R.id.profile_action_bar_menu_clickable).setOnClickListener(((Main)context).menuIcon_ClickListener);
            ((Main) context).findViewById(R.id.profile_action_bar_myPhoto_button).setOnClickListener(((Main)context).appIcon_ClickListener);
            ((Main)context).menuIcon_ClickListener.onClick(((Main) context).findViewById(R.id.profile_action_bar_menu_clickable));
        }
    };
}
