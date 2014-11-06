package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Chats.Messages.MessageContent;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Jonathan on 27/03/14.
 */
public class MainChat {
    Context context;
    TextView textInput;
    Group channelGroup;
    Chat channelChat;

    ChatListAdapter chatListAdapter;

    public MainChat (Context context, String channelUnicode) {
        this.context = context;
        this.channelGroup = Group.getGroup(channelUnicode);
        this.channelChat = this.channelGroup.getChat();
        this.textInput = ((TextView)((Activity)context).findViewById(R.id.main_panel_chat_textBox));

        this.channelChat.setChatWindowActive(true);
        this.InicializeChatWindow();
        this.chatListAdapter = new ChatListAdapter(context,this.channelChat);
        ((ListView)((Activity)context).findViewById(R.id.main_panel_chat_message_list)).setAdapter(chatListAdapter);

            this.channelChat.MessageListModifiedEvent.add(new EventHandler<EventArgs>(this.chatListAdapter,"OnAddItem",EventArgs.class));
    }

    private void InicializeChatWindow(){
        //TextView txt = (TextView) channelGroup.getName();
        ((TextView)((Activity)context).findViewById(R.id.main_panel_chat_name)).setText("HOLA");
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
