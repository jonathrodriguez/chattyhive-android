package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.businessobjects.MessageContent;
import com.chattyhive.backend.util.events.ChannelEventArgs;

import java.util.Date;

/**
 * Created by Jonathan on 27/03/14.
 */
public class MainChat {
    Context context;

   public MainChat (Context context) {
       this.context = context;
   }


    protected View.OnClickListener send_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String pusher_channel = ((String)((Activity)context).findViewById(R.id.main_panel_chat_name).getTag());
            String text_to_send = ((TextView)((Activity)context).findViewById(R.id.main_panel_chat_textBox)).getText().toString();

            Message message = new Message(new MessageContent(text_to_send),new Date());

            if (((Main)context)._controller.sendMessage(message,pusher_channel)) {
                ((TextView) ((Activity) context).findViewById(R.id.main_panel_chat_textBox)).setText("");
                ((ChatListAdapter)((ListView)((Activity)context).findViewById(R.id.main_panel_chat_message_list)).getAdapter()).OnAddItem(this, new ChannelEventArgs());
            }
        }
    };
}
