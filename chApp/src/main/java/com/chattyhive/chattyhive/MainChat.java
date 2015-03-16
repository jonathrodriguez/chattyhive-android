package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Chats.Conversation;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.MessageContent;
import com.chattyhive.Core.BusinessObjects.Image;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.chattyhive.framework.Util.ViewPair;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by Jonathan on 27/03/14.
 */
public class MainChat extends Window {
    private static int ChatHierarchyLevel = 1;

    private transient TextView textInput;

    private String channelChatID;
    private transient Chat channelChat;
    private transient Conversation channelConversation;

    private transient View mainChat;
    private transient View actionBar;

    private transient ChatListAdapter chatListAdapter;

    private MainChat (Context context) {
        super(context);
        this.setHierarchyLevel(ChatHierarchyLevel);
    }
    public MainChat (Context context, Chat channelChat) {
        this(context);

        this.channelChatID = channelChat.getChannelUnicode();
        this.channelChat = channelChat;
    }
    public MainChat (Context context, String channelUnicode) {
        this(context);

        this.channelChatID = channelUnicode;
    }

    protected void loadActionBarData() {
        this.actionBar.findViewById(R.id.main_panel_chat_name).setTag(this.channelChat.getChannelUnicode());
        this.actionBar.findViewById(R.id.main_panel_chat_menu_icon).setOnClickListener(((Main)context).menuIcon_ClickListener);
        this.actionBar.findViewById(R.id.main_panel_chat_icon).setOnClickListener(((Main)context).appIcon_ClickListener);

        String mainName = "";
        String infoText = "";

        String userPublicNameIdentifier = context.getResources().getString(R.string.public_username_identifier_character);
        String hiveNameIdentifier = context.getResources().getString(R.string.hivename_identifier_character);

        ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setImageResource(R.drawable.default_profile_image_male);
        //((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setColorFilter(Color.parseColor("#ffffff"));

        User otherUser = null;

        switch (channelChat.getChatKind()) {
            case HIVE:
                ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setImageResource(R.drawable.default_hive_image);

                if ((this.channelChat.getParentHive() != null) && (this.channelChat.getParentHive().getImageURL() != null) && (!this.channelChat.getParentHive().getImageURL().isEmpty())) {
                    this.channelChat.getParentHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"onImageLoaded",EventArgs.class));
                    this.channelChat.getParentHive().getHiveImage().loadImage(Image.ImageSize.small,0);
                }

                if ((this.channelChat.getName() != null) && (!this.channelChat.getName().isEmpty()))
                    mainName = this.channelChat.getName();
                else if ((this.channelChat.getParentHive() != null) && (this.channelChat.getParentHive().getName() != null))
                    mainName = hiveNameIdentifier.concat(this.channelChat.getParentHive().getName());
                break;
            case PUBLIC_SINGLE:
                for (User member : this.channelChat.getMembers())
                    if (!member.isMe())
                        otherUser = member;

                if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getImageURL() != null) && (!otherUser.getUserPublicProfile().getImageURL().isEmpty())) {
                    otherUser.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"onImageLoaded",EventArgs.class));
                    otherUser.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.small,0);
                } else if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getSex() != null) && (otherUser.getUserPublicProfile().getSex().equalsIgnoreCase("female")))
                    ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setImageResource(R.drawable.default_profile_image_female);


                if ((this.channelChat.getName() != null) && (!this.channelChat.getName().isEmpty()))
                    mainName = this.channelChat.getName();
                else if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getPublicName() != null))
                    mainName = userPublicNameIdentifier.concat(otherUser.getUserPublicProfile().getPublicName());

                if ((this.channelChat.getParentHive() != null) && (this.channelChat.getParentHive().getName() != null))
                    infoText = hiveNameIdentifier.concat(this.channelChat.getParentHive().getName());


                break;
            case PUBLIC_GROUP:
                if ((this.channelChat.getName() != null) && (!this.channelChat.getName().isEmpty()))
                    mainName = this.channelChat.getName();
                else
                    for (User member : this.channelChat.getMembers())
                        if (!member.isMe())
                            if ((member.getUserPublicProfile() != null) && (member.getUserPublicProfile().getPublicName() != null))
                                mainName = ((mainName.isEmpty())?"":", ").concat(userPublicNameIdentifier.concat(member.getUserPublicProfile().getPublicName()));
                if ((this.channelChat.getParentHive() != null) && (this.channelChat.getParentHive().getName() != null))
                    infoText = hiveNameIdentifier.concat(this.channelChat.getParentHive().getName());
                break;
            case PRIVATE_SINGLE:
                for (User member : this.channelChat.getMembers())
                    if (!member.isMe())
                        otherUser = member;


                if ((otherUser != null) && (otherUser.getUserPrivateProfile() != null) && (otherUser.getUserPrivateProfile().getImageURL() != null) && (!otherUser.getUserPrivateProfile().getImageURL().isEmpty())) {
                    otherUser.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"onImageLoaded",EventArgs.class));
                    otherUser.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.small,0);
                } else if ((otherUser != null) && (otherUser.getUserPrivateProfile() != null) && (otherUser.getUserPrivateProfile().getSex() != null) && (otherUser.getUserPrivateProfile().getSex().equalsIgnoreCase("female")))
                    ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setImageResource(R.drawable.default_profile_image_female);

                if ((this.channelChat.getName() != null) && (!this.channelChat.getName().isEmpty()))
                    mainName = this.channelChat.getName();
                else if ((otherUser != null) && (otherUser.getUserPrivateProfile() != null) && (otherUser.getUserPrivateProfile().getShowingName() != null))
                    mainName = otherUser.getUserPrivateProfile().getShowingName();
                if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getPublicName() != null))
                    infoText = userPublicNameIdentifier.concat(otherUser.getUserPublicProfile().getPublicName());
                break;
            case PRIVATE_GROUP:
                if ((this.channelChat.getName() != null) && (!this.channelChat.getName().isEmpty()))
                    mainName = this.channelChat.getName();
                else
                    for (User member : this.channelChat.getMembers())
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
                try {
                    InputStream is = image.getImage(Image.ImageSize.small,0);
                    if (is != null) {
                        ((ImageView) actionBar.findViewById(R.id.main_panel_chat_icon)).setImageBitmap(BitmapFactory.decodeStream(is));
                        ((ImageView) actionBar.findViewById(R.id.main_panel_chat_icon)).clearColorFilter();
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisMainChat, "onImageLoaded", EventArgs.class));
                }
            }
        });
    }


    protected transient View.OnClickListener send_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text_to_send = textInput.getText().toString();
            if ((text_to_send == null) || (text_to_send.isEmpty())) return;

            try {
                new Message(((Main)context).controller.getMe(), channelConversation,new MessageContent("TEXT",text_to_send),new Date()).SendMessage();
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

        try {
            this.channelConversation.setChatWindowActive(false);
            ((Main) this.context).controller.Leave(this.channelChat.getChannelUnicode());
            this.channelConversation.MessageListModifiedEvent.remove(new EventHandler<EventArgs>(this.chatListAdapter, "OnAddItem", EventArgs.class));
        } catch (Exception e) {

        } finally {
            this.context = null;
            this.channelConversation = null;
            this.channelChat = null;
            this.textInput = null;
            this.chatListAdapter = null;
            this.channelChatID = null;
        }
    }

    @Override
    public void Open() {
        //Log.w("MainChat", "Open().Start");
        if (!this.hasContext()) return;

        //Log.w("MainChat", "Show window");
        this.Show();
        //Log.w("MainChat", "Open().End");
    }

    @Override
    public void Close() {
        if (!this.hasContext()) return;

        this.Hide();

        this.channelChat = null;
        this.channelConversation = null;
    }

    @Override
    public void Show() {
        //Log.w("MainChat", "Show().Start");
        if (!this.hasContext()) return;

        //Log.w("MainChat", "Set channel chat if needed.");
        if (this.channelChat == null) {
            this.channelChat = Chat.getChat(this.channelChatID);
            this.channelConversation = this.channelChat.getConversation();
        }

        //Log.w("MainChat", "Set conversation if needed.");
        if (this.channelConversation == null)
            this.channelConversation = this.channelChat.getConversation();

        //Log.w("MainChat", "Set chatListAdapter if needed.");
        if (this.chatListAdapter == null)
            this.chatListAdapter = new ChatListAdapter(context, this.channelConversation);

        //Log.w("MainChat", "Show the view and the actionBar.");
        ViewPair viewPair = ((Main)context).ShowLayout(R.layout.main_panel_chat_layout,R.layout.chat_action_bar);
        this.actionBar = viewPair.getActionBarView();
        this.mainChat = viewPair.getMainView();

        //Log.w("MainChat", "Remember the textInput and set click listener for send button.");
        this.textInput = ((TextView)mainChat.findViewById(R.id.main_panel_chat_textBox));
        this.mainChat.findViewById(R.id.main_panel_chat_send_icon).setOnClickListener(this.send_button_click);

        //Log.w("MainChat", "Load action bar data.");
        this.loadActionBarData();

        //Log.w("MainChat", "Close lateral panels if open.");
        if (((Main)context).floatingPanel.isOpen())
            ((Main)context).floatingPanel.close();

        //Log.w("MainChat", "Establish list adapter.");
        ((ListView)mainChat.findViewById(R.id.main_panel_chat_message_list)).setAdapter(chatListAdapter);

        //Log.w("MainChat", "Subscribe to conversation changes.");
        this.channelConversation.MessageListModifiedEvent.add(new EventHandler<EventArgs>(this.chatListAdapter,"OnAddItem",EventArgs.class));

        //Log.w("MainChat", "Set bottom bar left icon.");
        if(((TextView)mainChat.findViewById(R.id.main_panel_chat_textBox)).didTouchFocusSelect()){////????????????????????????????????????
            ((ImageView)mainChat.findViewById(R.id.main_panel_chat_smyles_icon)).setBackgroundResource(R.drawable.launcher_launcher_a);
        }else{
            ((ImageView)mainChat.findViewById(R.id.main_panel_chat_smyles_icon)).setBackgroundResource(R.drawable.chats_isotipo_puro_recto_01);
        }

        //Log.w("MainChat", "Notify core that channel conversation window is active.");
        this.channelConversation.setChatWindowActive(true);
        //Log.w("MainChat", "Show().End");
    }

    @Override
    public void Hide() {
        if (!this.hasContext()) return;

        if (this.channelConversation != null)
            this.channelConversation.setChatWindowActive(false);

        if (this.channelChat != null)
            ((Main)this.context).controller.Leave(this.channelChat.getChannelUnicode());

        if (this.channelConversation != null)
            this.channelConversation.MessageListModifiedEvent.remove(new EventHandler<EventArgs>(this.chatListAdapter,"OnAddItem",EventArgs.class));

        this.chatListAdapter = null;
        if ((mainChat != null) && (mainChat.findViewById(R.id.main_panel_chat_message_list) != null))
            ((ListView)mainChat.findViewById(R.id.main_panel_chat_message_list)).setAdapter(null);
        this.mainChat = null;
        this.textInput = null;
        this.actionBar = null;
    }
}
