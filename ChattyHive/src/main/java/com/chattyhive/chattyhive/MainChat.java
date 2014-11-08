package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Chats.Messages.MessageContent;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.framework.Util.ViewPair;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by Jonathan on 27/03/14.
 */
public class MainChat {
    Context context;
    TextView textInput;
    Group channelGroup;
    Chat channelChat;

    View mainChat;
    View actionBar;

    ChatListAdapter chatListAdapter;

    public MainChat (Context context, Group channelGroup) {
        this.context = context;
        this.channelGroup = channelGroup;
        this.channelChat = this.channelGroup.getChat();

        ViewPair viewPair = ((Main)context).ShowLayout(R.layout.main_panel_chat_layout,R.layout.chat_action_bar);
        this.actionBar = viewPair.getActionBarView();
        this.mainChat = viewPair.getMainView();

        this.loadActionBarData();

        this.mainChat.findViewById(R.id.main_panel_chat_send_icon).setOnClickListener(this.send_button_click);

        ((Main)context).appIcon_ClickListener.onClick(this.actionBar.findViewById(R.id.main_panel_chat_icon));

        this.textInput = ((TextView)mainChat.findViewById(R.id.main_panel_chat_textBox));

        this.channelChat.setChatWindowActive(true);

        this.chatListAdapter = new ChatListAdapter(context,this.channelChat);
        ((ListView)mainChat.findViewById(R.id.main_panel_chat_message_list)).setAdapter(chatListAdapter);

        this.channelChat.MessageListModifiedEvent.add(new EventHandler<EventArgs>(this.chatListAdapter,"OnAddItem",EventArgs.class));
    }

    protected void loadActionBarData() {
        this.actionBar.findViewById(R.id.main_panel_chat_name).setTag(this.channelGroup.getChannelUnicode());
        this.actionBar.findViewById(R.id.main_panel_chat_menu_icon).setOnClickListener(((Main)context).menuIcon_ClickListener);
        this.actionBar.findViewById(R.id.main_panel_chat_icon).setOnClickListener(((Main)context).appIcon_ClickListener);

        String mainName = "";
        String infoText = "";

        String userPublicNameIdentifier = context.getResources().getString(R.string.public_username_identifier_character);
        String hiveNameIdentifier = context.getResources().getString(R.string.hivename_identifier_character);

        ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setImageResource(R.drawable.chats_users_online);
        ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setColorFilter(Color.parseColor("#ffffff"));

        User otherUser = null;

        switch (channelGroup.getGroupKind()) {
            case HIVE:
                ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setImageResource(R.drawable.pestanha_chats_public_chat);

                if ((this.channelGroup.getParentHive() != null) && (this.channelGroup.getParentHive().getImageURL() != null) && (!this.channelGroup.getParentHive().getImageURL().isEmpty())) {
                    this.channelGroup.getParentHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"onImageLoaded",EventArgs.class));
                    this.channelGroup.getParentHive().getHiveImage().loadImage(Image.ImageSize.small,0);
                }

                if ((this.channelGroup.getName() != null) && (!this.channelGroup.getName().isEmpty()))
                    mainName = this.channelGroup.getName();
                else if ((this.channelGroup.getParentHive() != null) && (this.channelGroup.getParentHive().getName() != null))
                    mainName = hiveNameIdentifier.concat(this.channelGroup.getParentHive().getName());
                break;
            case PUBLIC_SINGLE:
                for (User member : this.channelGroup.getMembers())
                    if (!member.isMe())
                        otherUser = member;

                if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getImageURL() != null) && (!otherUser.getUserPublicProfile().getImageURL().isEmpty())) {
                    otherUser.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"onImageLoaded",EventArgs.class));
                    otherUser.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.small,0);
                }

                if ((this.channelGroup.getName() != null) && (!this.channelGroup.getName().isEmpty()))
                    mainName = this.channelGroup.getName();
                else if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getPublicName() != null))
                    mainName = userPublicNameIdentifier.concat(otherUser.getUserPublicProfile().getPublicName());

                if ((this.channelGroup.getParentHive() != null) && (this.channelGroup.getParentHive().getName() != null))
                    infoText = hiveNameIdentifier.concat(this.channelGroup.getParentHive().getName());


                break;
            case PUBLIC_GROUP:
                if ((this.channelGroup.getName() != null) && (!this.channelGroup.getName().isEmpty()))
                    mainName = this.channelGroup.getName();
                else
                    for (User member : this.channelGroup.getMembers())
                        if (!member.isMe())
                            if ((member.getUserPublicProfile() != null) && (member.getUserPublicProfile().getPublicName() != null))
                                mainName = ((mainName.isEmpty())?"":", ").concat(userPublicNameIdentifier.concat(member.getUserPublicProfile().getPublicName()));
                if ((this.channelGroup.getParentHive() != null) && (this.channelGroup.getParentHive().getName() != null))
                    infoText = hiveNameIdentifier.concat(this.channelGroup.getParentHive().getName());
                break;
            case PRIVATE_SINGLE:
                for (User member : this.channelGroup.getMembers())
                    if (!member.isMe())
                        otherUser = member;

                if ((otherUser != null) && (otherUser.getUserPrivateProfile() != null) && (otherUser.getUserPrivateProfile().getImageURL() != null) && (!otherUser.getUserPrivateProfile().getImageURL().isEmpty())) {
                    otherUser.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"onImageLoaded",EventArgs.class));
                    otherUser.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.small,0);
                }

                if ((this.channelGroup.getName() != null) && (!this.channelGroup.getName().isEmpty()))
                    mainName = this.channelGroup.getName();
                else if ((otherUser != null) && (otherUser.getUserPrivateProfile() != null) && (otherUser.getUserPrivateProfile().getShowingName() != null))
                    mainName = otherUser.getUserPrivateProfile().getShowingName();

                break;
            case PRIVATE_GROUP:
                if ((this.channelGroup.getName() != null) && (!this.channelGroup.getName().isEmpty()))
                    mainName = this.channelGroup.getName();
                else
                    for (User member : this.channelGroup.getMembers())
                        if (!member.isMe())
                            if ((member.getUserPrivateProfile() != null) && (member.getUserPrivateProfile().getShowingName() != null))
                                mainName = ((mainName.isEmpty())?"":", ").concat(member.getUserPrivateProfile().getShowingName());
                break;
        }

        if ((mainName != null) && (!mainName.isEmpty())) {
            ((TextView) actionBar.findViewById(R.id.main_panel_chat_name)).setText(mainName);
            actionBar.findViewById(R.id.main_panel_chat_name).setVisibility(View.VISIBLE);
        } else {
            actionBar.findViewById(R.id.main_panel_chat_name).setVisibility(View.GONE);
        }

        if ((infoText != null) && (!infoText.isEmpty())) {
            ((TextView) actionBar.findViewById(R.id.main_panel_chat_info)).setText(infoText);
            actionBar.findViewById(R.id.main_panel_chat_info).setVisibility(View.VISIBLE);
        } else {
            actionBar.findViewById(R.id.main_panel_chat_info).setVisibility(View.GONE);
        }
    }

    public void onImageLoaded(Object sender, EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image)sender;
        final MainChat thisMainChat = this;

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputStream is = image.getImage(Image.ImageSize.small,0);
                if (is != null) {
                    ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setImageBitmap(BitmapFactory.decodeStream(is));
                    ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).clearColorFilter();
                    try {
                        is.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisMainChat, "onImageLoaded", EventArgs.class));
                }
            }
        });
    }


    protected View.OnClickListener send_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text_to_send = textInput.getText().toString();
            if ((text_to_send == null) || (text_to_send.isEmpty())) return;

            try {
                new Message(Controller.GetRunningController().getMe(),channelChat,new MessageContent("TEXT",text_to_send),new Date()).SendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                textInput.setText("");
            }
        }
    };

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        this.context = null;
        this.channelChat.setChatWindowActive(false);
        this.channelChat = null;
        this.channelGroup = null;
        this.textInput = null;
        this.chatListAdapter = null;
    }
}
