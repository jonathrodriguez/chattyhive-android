package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Hive;
import com.chattyhive.backend.businessobjects.Mate;
import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.formatters.TimestampFormatter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jonathan on 25/03/14.
 */
public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    private Collection<Message> chatMessages;
    private int chatKind;

    public ChatListAdapter (Context activityContext,Collection<Message> chatMessages, int chatKind) {
        this.chatMessages = chatMessages;

        this.context = activityContext;
        this.inflater = ((Activity)this.context).getLayoutInflater();

        this.listView = ((ListView)((Activity)this.context).findViewById(R.id.left_panel_element_list));
        this.chatKind = chatKind;
    }

    public void OnAddItem(Object sender, ChannelEventArgs args) {
        Log.w("ChatListAdapter.OnAddItem()",String.format("Event triggered. Message count: %d",chatMessages.size()));
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Message m = chatMessages.toArray(new Message[0])[position];
        boolean mineMessage = ((m.getUser() != null) && (m.getUser().isMe()));
        switch (this.chatKind) {
            case R.id.MainPanelChat_ListKind_Hive:
                return ((mineMessage)?R.id.MainPanelChat_ListKind_Hive_Me:R.id.MainPanelChat_ListKind_Hive_Other);
            case R.id.MainPanelChat_ListKind_PrivateGroup:
                return ((mineMessage)?R.id.MainPanelChat_ListKind_PrivateGroup_Me:R.id.MainPanelChat_ListKind_PrivateGroup_Other);
            case R.id.MainPanelChat_ListKind_PrivateSingle:
                return ((mineMessage)?R.id.MainPanelChat_ListKind_PrivateSingle_Me:R.id.MainPanelChat_ListKind_PrivateSingle_Other);
            case R.id.MainPanelChat_ListKind_PublicGroup:
                return ((mineMessage)?R.id.MainPanelChat_ListKind_PublicGroup_Me:R.id.MainPanelChat_ListKind_PublicGroup_Other);
            case R.id.MainPanelChat_ListKind_PublicSingle:
                return ((mineMessage)?R.id.MainPanelChat_ListKind_PublicSingle_Me:R.id.MainPanelChat_ListKind_PublicSingle_Other);
        }
        return R.id.MainPanelChat_ListKind_None;
    }

    @Override
    public int getViewTypeCount() {
        return this.context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Count);
    }

    @Override
    public int getCount() {
        return this.chatMessages.size();
    }

    @Override
    public Object getItem(int position){
        return chatMessages.toArray(new Message[0])[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if(convertView==null){
            holder = new ViewHolder();
            switch (type) {
                case R.id.MainPanelChat_ListKind_Hive_Other:
                    convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_other,parent,false);
                    holder.username = (TextView)convertView.findViewById(R.id.main_panel_chat_username);
                    holder.messageText = (TextView)convertView.findViewById(R.id.main_panel_chat_messageText);
                    holder.timeStamp = (TextView)convertView.findViewById(R.id.main_panel_chat_timeStamp);
                    holder.avatarThumbnail = (ImageView)convertView.findViewById(R.id.main_panel_chat_avatarThumbnail);
                    break;
                case R.id.MainPanelChat_ListKind_Hive_Me:
                    convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_me,parent,false);
                    holder.username = (TextView)convertView.findViewById(R.id.main_panel_chat_username);
                    holder.messageText = (TextView)convertView.findViewById(R.id.main_panel_chat_messageText);
                    holder.timeStamp = (TextView)convertView.findViewById(R.id.main_panel_chat_timeStamp);
                    holder.avatarThumbnail = (ImageView)convertView.findViewById(R.id.main_panel_chat_avatarThumbnail);
                    break;
            }

            convertView.setTag(R.id.MainPanelChat_ListViewHolder,holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.MainPanelChat_ListViewHolder);
        }

        Message message = chatMessages.toArray(new Message[0])[position];

        convertView.setTag(R.id.BO_Message,message);

        if (message.getUser() != null) {
            holder.username.setText(message.getUser().getPublicName());
            //Log.w("ChatListAdapter - getView","User color: ".concat(message.getUser().color));
            holder.username.setTextColor(Color.parseColor(message.getUser().color));
        }

        holder.messageText.setText(message.getMessage().getContent());
        holder.timeStamp.setText(TimestampFormatter.toLocaleString(message.getTimeStamp()));

        return convertView;
    }

    private static class ViewHolder {
        public TextView username;
        public TextView messageText;
        public TextView timeStamp;
        public ImageView avatarThumbnail;
    }
}
