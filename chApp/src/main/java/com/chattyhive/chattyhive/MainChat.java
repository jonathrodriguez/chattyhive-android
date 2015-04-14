package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Context.ContextElement;
import com.chattyhive.backend.businessobjects.Chats.Context.IContextualizable;
import com.chattyhive.backend.businessobjects.Chats.Conversation;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Chats.Messages.MessageContent;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.framework.Util.ViewPair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jonathan on 27/03/14.
 */
public class MainChat extends Window {
    private static int ChatHierarchyLevel = 1;

    private transient TextView textInput;

    private String channelChatID;
    private String userID;
    private Boolean newChat;
    private String hiveID;

    private transient Hive hive;
    private transient User user;

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

        this.newChat = false;

        this.channelChatID = channelChat.getChannelUnicode();
        this.channelChat = channelChat;

        this.hiveID = "";
        this.userID = "";
    }
    public MainChat (Context context, String channelUnicode) {
        this(context);

        this.newChat = false;

        this.channelChatID = channelUnicode;

        this.hiveID = "";
        this.userID = "";
    }
    public MainChat (Context context, Hive hive, User user) {
        this(context);

        this.newChat = true;

        if (this.hive != null) {
            this.hiveID = hive.getNameUrl();
            this.hive = hive;
        } else {
            this.hiveID = "";
        }

        this.userID = user.getUserID();
        this.user = user;

        this.channelChatID = "";
        this.channelChat = Chat.CreateChat(this.user,this.hive,new EventHandler<CommandCallbackEventArgs>(this,"onChatCreated",CommandCallbackEventArgs.class));
    }

    protected void loadActionBarData() {
        //this.actionBar.findViewById(R.id.main_panel_chat_name).setTag(this.channelChat.getChannelUnicode());
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
                if (!this.newChat) {
                    for (User member : this.channelChat.getMembers())
                        if (!member.isMe())
                            otherUser = member;
                } else
                    otherUser = this.user;

                    if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getImageURL() != null) && (!otherUser.getUserPublicProfile().getImageURL().isEmpty())) {
                        otherUser.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "onImageLoaded", EventArgs.class));
                        otherUser.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.small, 0);
                    } else if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getSex() != null) && (otherUser.getUserPublicProfile().getSex().equalsIgnoreCase("female")))
                        ((ImageView) actionBar.findViewById(R.id.main_panel_chat_icon)).setImageResource(R.drawable.default_profile_image_female);


                    if ((!this.newChat) && (this.channelChat.getName() != null) && (!this.channelChat.getName().isEmpty()))
                        mainName = this.channelChat.getName();
                    else if ((otherUser != null) && (otherUser.getUserPublicProfile() != null) && (otherUser.getUserPublicProfile().getPublicName() != null))
                        mainName = userPublicNameIdentifier.concat(otherUser.getUserPublicProfile().getPublicName());

                    if ((!this.newChat) && (this.channelChat.getParentHive() != null) && (this.channelChat.getParentHive().getName() != null))
                        infoText = hiveNameIdentifier.concat(this.channelChat.getParentHive().getName());
                    else if ((this.newChat) && (this.hive != null) && (this.hive.getName() != null))
                        infoText = hiveNameIdentifier.concat(this.hive.getName());

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
                if (!this.newChat) {
                    for (User member : this.channelChat.getMembers())
                        if (!member.isMe())
                            otherUser = member;
                } else
                    otherUser = this.user;

                if ((otherUser != null) && (otherUser.getUserPrivateProfile() != null) && (otherUser.getUserPrivateProfile().getImageURL() != null) && (!otherUser.getUserPrivateProfile().getImageURL().isEmpty())) {
                    otherUser.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "onImageLoaded", EventArgs.class));
                    otherUser.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.small, 0);
                } else if ((otherUser != null) && (otherUser.getUserPrivateProfile() != null) && (otherUser.getUserPrivateProfile().getSex() != null) && (otherUser.getUserPrivateProfile().getSex().equalsIgnoreCase("female")))
                    ((ImageView)actionBar.findViewById(R.id.main_panel_chat_icon)).setImageResource(R.drawable.default_profile_image_female);

                if ((!this.newChat) && (this.channelChat.getName() != null) && (!this.channelChat.getName().isEmpty()))
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

    public void onChatCreated(Object sender, CommandCallbackEventArgs eventArgs) {
        this.UIThreadOnChatCreated(sender,eventArgs);
    }

    private void UIThreadOnChatCreated(final Object sender, final CommandCallbackEventArgs eventArgs) {
        final MainChat thisMainChat = this;
        ((Activity)this.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Boolean createdOK = false;
                if (eventArgs.countReceivedFormats() > 0)
                    for (Format format : eventArgs.getReceivedFormats())
                        if ((format instanceof COMMON) && (((COMMON) format).STATUS.equalsIgnoreCase("OK")))
                            createdOK = true;

                if (createdOK) {
                    thisMainChat.channelChat = Chat.getChat(((Chat)sender).getChannelUnicode());
                    thisMainChat.channelChatID = thisMainChat.channelChat.getChannelUnicode();
                    thisMainChat.channelConversation = thisMainChat.channelChat.getConversation();
                    thisMainChat.newChat = false;
                    Toast.makeText(thisMainChat.context,"Chat created!",Toast.LENGTH_LONG).show();
                    thisMainChat.Show(false);
                } else {
                    Toast.makeText(thisMainChat.context,"Some error happened while creating chat!",Toast.LENGTH_LONG).show();
                    thisMainChat.Close();
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
        this.Show(true);
    }

    private void Show(Boolean complete) {
        //Log.w("MainChat", "Show().Start");
        if (!this.hasContext()) return;

        if ((complete) || (this.actionBar == null) || (this.mainChat == null) || (this.textInput == null)) {
            //Log.w("MainChat", "Show the view and the actionBar.");
            ViewPair viewPair = ((Main) context).ShowLayout(R.layout.main_panel_chat_layout, R.layout.chat_action_bar);
            this.actionBar = viewPair.getActionBarView();
            this.mainChat = viewPair.getMainView();

            //Log.w("MainChat", "Remember the textInput.");
            this.textInput = ((TextView) mainChat.findViewById(R.id.main_panel_chat_textBox));
        }

        if (!this.newChat) {
            //Log.w("MainChat", "Set channel chat if needed.");
            if (this.channelChat == null) {
                this.channelChat = Chat.getChat(this.channelChatID);
                this.channelConversation = this.channelChat.getConversation();
            }

            //Log.w("MainChat", "Set conversation if needed.");
            if (this.channelConversation == null)
                this.channelConversation = this.channelChat.getConversation();

            //TODO: This is for testing. Remove after tested.
            ((IContextualizable)this.channelChat).getOnContextLoaded().add(new EventHandler<EventArgs>(this,"OnContextLoaded",EventArgs.class));
            ((IContextualizable)this.channelChat).loadContext(4,4,4);

            //Log.w("MainChat", "Notify core that channel conversation window is active.");
            this.channelConversation.setChatWindowActive(true);
            //Log.w("MainChat", "Show().End");


            //Log.w("MainChat", "Set chatListAdapter if needed.");
            if (this.chatListAdapter == null)
                this.chatListAdapter = new ChatListAdapter(context, this.channelConversation);

            //Log.w("MainChat", "Establish list adapter.");
            ((ListView)mainChat.findViewById(R.id.main_panel_chat_message_list)).setAdapter(chatListAdapter);

            //Log.w("MainChat", "Subscribe to conversation changes.");
            this.channelConversation.MessageListModifiedEvent.add(new EventHandler<EventArgs>(this.chatListAdapter,"OnAddItem",EventArgs.class));

            //Log.w("MainChat", "Set click listener for send button.");
            this.mainChat.findViewById(R.id.main_panel_chat_send_icon).setOnClickListener(this.send_button_click);

            //TODO: Activate textInput
        } else {
            if ((this.hive == null) && (!this.hiveID.isEmpty()))
                this.hive = Hive.getHive(this.hiveID);

            if ((this.user == null) && (!this.userID.isEmpty())) {
                PROFILE_ID profile_id = new PROFILE_ID();
                profile_id.USER_ID = this.userID;
                profile_id.PROFILE_TYPE = "BASIC_".concat((this.hive != null)?"PUBLIC":"PRIVATE");
                this.user = ((Main) context).controller.getUser(profile_id);
            }

            //TODO: Deactivate textInput
        }

        //Log.w("MainChat", "Load action bar data.");
        this.loadActionBarData();

        //Log.w("MainChat", "Close lateral panels if open.");
        if (((Main) context).floatingPanel.isOpen())
            ((Main) context).floatingPanel.close();

        //Log.w("MainChat", "Set bottom bar left icon.");
        if(((TextView)mainChat.findViewById(R.id.main_panel_chat_textBox)).didTouchFocusSelect()){////????????????????????????????????????
            ((ImageView)mainChat.findViewById(R.id.main_panel_chat_smyles_icon)).setBackgroundResource(R.drawable.launcher_launcher_a);
        }else{
            ((ImageView)mainChat.findViewById(R.id.main_panel_chat_smyles_icon)).setBackgroundResource(R.drawable.chats_isotipo_puro_recto_01);
        }
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

    //TODO: This is for testing. Remove after tested.
    public void OnContextLoaded(Object sender,EventArgs eventArgs) {
        ContextElement parentContext = ((IContextualizable)this.channelChat).getParentContext();
        ContextElement baseContext = ((IContextualizable)this.channelChat).getBaseContext();
        ContextElement communityContext = ((IContextualizable)this.channelChat).getCommunityContext();
        List<Message> sharedImages = ((IContextualizable)this.channelChat).getSharedImages();
        List<Message> topBuzzes = ((IContextualizable)this.channelChat).getTrendingBuzzes();
        List<User> newUsers = ((IContextualizable)this.channelChat).getNewUsers();
        List<User> users = ((IContextualizable)this.channelChat).getUsers();
        List<ContextElement> otherChats = ((IContextualizable)this.channelChat).getOtherChats();
        List<ContextElement> publicChats = ((IContextualizable)this.channelChat).getPublicChats();

        Log.w("Context",String.format("COMMUNITY CONTEXT\nImage: %s\tCommunity name: %s",(((communityContext != null) && (communityContext.getImage() != null))?"Ok!":"NULL"),(((communityContext != null) && (communityContext.getName() != null))?"\"".concat(communityContext.getName()).concat("\""):"NULL")));
        Log.w("Context",String.format("BASE CONTEXT\nImage: %s\tChat name: %s",(((baseContext != null) && (baseContext.getImage() != null))?"Ok!":"NULL"),(((baseContext != null) && (baseContext.getName() != null))?"\"".concat(((baseContext.getElementType() == ContextElement.ElementType.Hive)?this.context.getString(R.string.hivename_identifier_character):((baseContext.getElementType() == ContextElement.ElementType.PublicUser)?this.context.getString(R.string.public_username_identifier_character):""))).concat(baseContext.getName()).concat("\""):"NULL")));
        Log.w("Context",String.format("PARENT CONTEXT\nImage: %s\tHive name: %s",(((parentContext != null) && (parentContext.getImage() != null))?"Ok!":"NULL"),(((parentContext != null) && (parentContext.getName() != null))?"\"".concat(this.context.getString(R.string.hivename_identifier_character)).concat(parentContext.getName()).concat("\""):"NULL")));

        String elementList = "";
        if ((publicChats != null) && (!publicChats.isEmpty())) {
            for (ContextElement publicChat : publicChats) {
                if (!elementList.isEmpty())
                    elementList = elementList.concat("\n");
                elementList = elementList.concat(String.format("\tImage: %s\tChat name: %s",(((publicChat != null) && (publicChat.getImage() != null))?"Ok!":"NULL"),(((publicChat != null) && (publicChat.getName() != null))?"\"".concat(publicChat.getName()).concat("\""):"NULL")));
            }
        } else {
            elementList = "\tEmpty!";
        }
        Log.w("Context",String.format("PUBLIC CHATS\n%s",elementList));

        elementList = "";
        if ((sharedImages != null) && (!sharedImages.isEmpty())) {
            for (Message message : sharedImages) {
                if (!elementList.isEmpty())
                    elementList = elementList.concat("\n");
                elementList = elementList.concat(String.format("\tTimestamp: %s\tImage URL: %s",message.getOrdinationTimeStamp().toString(),message.getMessageContent().getContent()));
            }
        } else {
            elementList = "\tEmpty!";
        }
        Log.w("Context",String.format("SHARED IMAGES\n%s",elementList));

        elementList = "";
        if ((newUsers != null) && (!newUsers.isEmpty())) {
            for (User user : newUsers) {
                if (!elementList.isEmpty())
                    elementList = elementList.concat("\n");
                elementList = elementList.concat(String.format("\tUsername: %s",this.context.getString(R.string.public_username_identifier_character).concat(user.getUserPublicProfile().getShowingName())));
            }
        } else {
            elementList = "\tEmpty!";
        }
        Log.w("Context",String.format("NEW USERS\n%s",elementList));

        elementList = "";
        if ((users != null) && (!users.isEmpty())) {
            for (User user : users) {
                if (!elementList.isEmpty())
                    elementList = elementList.concat("\n");
                elementList = elementList.concat(String.format("\tUser: %s",(baseContext.getElementType() == ContextElement.ElementType.PublicUser)?this.context.getString(R.string.public_username_identifier_character).concat(user.getUserPublicProfile().getShowingName()):user.getUserPrivateProfile().getShowingName()));
            }
        } else {
            elementList = "\tEmpty!";
        }
        Log.w("Context",String.format("USERS\n%s",elementList));

        elementList = "";
        if ((topBuzzes != null) && (!topBuzzes.isEmpty())) {
            for (Message message : topBuzzes) {
                if (!elementList.isEmpty())
                    elementList = elementList.concat("\n");
                elementList = elementList.concat(String.format("\tTimestamp: %s\tMessage Content: %s",message.getOrdinationTimeStamp().toString(),message.getMessageContent().getContent()));
            }
        } else {
            elementList = "\tEmpty!";
        }
        Log.w("Context",String.format("TRENDING BUZZES\n%s",elementList));

        elementList = "";
        if ((otherChats != null) && (!otherChats.isEmpty())) {
            for (ContextElement otherChat : otherChats) {
                if (!elementList.isEmpty())
                    elementList = elementList.concat("\n");
                elementList = elementList.concat(String.format("\tImage: %s\tChat name: %s",(((otherChat != null) && (otherChat.getImage() != null))?"Ok!":"NULL"),(((otherChat != null) && (otherChat.getName() != null))?"\"".concat(otherChat.getName()).concat("\""):"NULL")));
            }
        } else {
            elementList = "\tEmpty!";
        }
        Log.w("Context",String.format("OTHER CHATS\n%s",elementList));
    }
}
