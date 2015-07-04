package com.chattyhive.chattyhive.ViewHolders;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.businessobjects.Users.PublicProfile;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.Main;
import com.chattyhive.chattyhive.MainChat;
import com.chattyhive.chattyhive.Profile;
import com.chattyhive.chattyhive.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jonathan on 27/06/2015.
 */
public class LeftPanelHiveUserListUserViewHolder extends ViewHolder<User> {

    ImageView leftPanelHiveUserListUserImage;
    private static Image.ImageSize MateImageSize = Image.ImageSize.medium;

    TextView leftPanelHiveUserListUserPublicName;
    TextView leftPanelHiveUserListUserStatusMessage;

    Hive hive;

    public LeftPanelHiveUserListUserViewHolder(Context context, BaseAdapter baseAdapter,View containerView) {
        super(context, baseAdapter, containerView);
    }
    public LeftPanelHiveUserListUserViewHolder(Context context, BaseAdapter baseAdapter,View containerView, User item, Hive hive) {
        super(context,baseAdapter,containerView, item);
        this.hive = hive;
        this.updateView();
    }

    @Override
    public void setContainerView(View containerView) {
        leftPanelHiveUserListUserImage = (ImageView)containerView.findViewById(R.id.left_panel_hive_user_list_user_img);
        leftPanelHiveUserListUserPublicName = (TextView)containerView.findViewById(R.id.left_panel_hive_user_list_user_public_name);
        leftPanelHiveUserListUserStatusMessage = (TextView)containerView.findViewById(R.id.left_panel_hive_user_list_user_status_message);

        super.setContainerView(containerView);
    }

    @Override
    protected void updateView() {
        if ((containerView == null) || (item == null))
            return;

        if (this.item.getUserPublicProfile() != null) {
            leftPanelHiveUserListUserPublicName.setText(this.item.getUserPublicProfile().getPublicName());
            leftPanelHiveUserListUserStatusMessage.setText(this.item.getUserPublicProfile().getStatusMessage());
        } else {
            leftPanelHiveUserListUserPublicName.setText("");
            leftPanelHiveUserListUserStatusMessage.setText("");
        }



        leftPanelHiveUserListUserImage.setImageResource(R.drawable.chats_users_online);
        try {
            item.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "loadMateImage", EventArgs.class));
            item.getUserPublicProfile().getProfileImage().loadImage(LeftPanelHiveUserListUserViewHolder.MateImageSize, 0);
        } catch (Exception e) { }

            this.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Main) context).OpenWindow(new MainChat(context, LeftPanelHiveUserListUserViewHolder.this.hive, LeftPanelHiveUserListUserViewHolder.this.item));
                }
            });
    }

    public void loadMateImage(Object sender, EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image) sender;

        if (!(containerView.getContext() instanceof Activity)) {
            Log.e("loadImage","Context is not activity.");
        }

        ((Activity) containerView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputStream is = image.getImage(LeftPanelHiveUserListUserViewHolder.MateImageSize, 0);
                if (is != null) {
                    leftPanelHiveUserListUserImage.setImageBitmap(BitmapFactory.decodeStream(is));
                    try {
                        is.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(LeftPanelHiveUserListUserViewHolder.this, "loadMateImage", EventArgs.class));
                }
            }
        });
    }
}
