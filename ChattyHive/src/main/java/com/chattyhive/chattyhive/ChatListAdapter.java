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

import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.backend.util.formatters.TimestampFormatter;

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
//        Log.w("ChatListAdapter.new()",String.format("chatKind: %d",chatKind));
        this.chatKind = chatKind;
    }

    public void OnAddItem(Object sender, ChannelEventArgs args) {
//        Log.w("ChatListAdapter.OnAddItem()",String.format("Event triggered. Message count: %d",chatMessages.size()));
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Message m = chatMessages.toArray(new Message[0])[position];
        if ((m.getUser() == null) && (m.getHive() == null)) return context.getResources().getInteger(R.integer.MainPanelChat_ListKind_None);
        boolean mineMessage = ((m.getUser() != null) && (m.getUser().isMe()));
//        Log.w("ChatListAdapter.getItemViewType",String.format("MainPanelChat_ListKind_Hive: %d",R.id.MainPanelChat_ListKind_Hive));
//        Log.w("ChatListAdapter.getItemViewType",String.format("MainPanelChat_ListKind_PrivateGroup: %d",R.id.MainPanelChat_ListKind_PrivateGroup));
//        Log.w("ChatListAdapter.getItemViewType",String.format("MainPanelChat_ListKind_PrivateSingle: %d",R.id.MainPanelChat_ListKind_PrivateSingle));
//        Log.w("ChatListAdapter.getItemViewType",String.format("MainPanelChat_ListKind_PublicGroup: %d",R.id.MainPanelChat_ListKind_PublicGroup));
//        Log.w("ChatListAdapter.getItemViewType",String.format("MainPanelChat_ListKind_PublicSingle: %d",R.id.MainPanelChat_ListKind_PublicSingle));
//        Log.w("ChatListAdapter.getItemViewType",String.format("MainPanelChat_ListKind_None: %d",context.getResources().getInteger(R.integer.MainPanelChat_ListKind_None)));
//        Log.w("ChatListAdapter.getItemViewType",String.format("MainPanelChat_ListKind_Me: %d",context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Me)));
//        Log.w("ChatListAdapter.getItemViewType",String.format("MainPanelChat_ListKind_Other: %d",context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Other)));
//        Log.w("ChatListAdapter.getItemViewType",String.format("Position: %d, Mine: %B, chatKind: %d",position,mineMessage,this.chatKind));

        if ((this.chatKind != R.id.MainPanelChat_ListKind_Hive) &&
            (this.chatKind != R.id.MainPanelChat_ListKind_PrivateGroup) &&
            (this.chatKind != R.id.MainPanelChat_ListKind_PrivateSingle) &&
            (this.chatKind != R.id.MainPanelChat_ListKind_PublicGroup) &&
            (this.chatKind != R.id.MainPanelChat_ListKind_PublicSingle)) {
            //Log.w("ChatListAdapter.getItemViewType","Returned NONE");
            return context.getResources().getInteger(R.integer.MainPanelChat_ListKind_None);
        }

        return ((mineMessage)?context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Me):context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Other));
    }

    @Override
    public int getViewTypeCount() {
        //Log.w("ChatListAdapter.getViewTypeCount()",String.format("MainPanelChat_ListKind_Count: %d",context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Count)));
        return context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Count);
    }

    @Override
    public int getCount() {
        //Log.w("ChatListAdapter.getCount()",String.format("Item count: %d",this.chatMessages.size()));
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
        Boolean separator = (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_None));
        if(convertView==null){
            holder = new ViewHolder();
            switch (chatKind) {
                case R.id.MainPanelChat_ListKind_Hive:
                    if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Me)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_me, parent, false);
                    } else if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Other)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_other, parent, false);
                    } else if (separator) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_day_marker,parent,false);
                    } else {
                        Log.e("ChatListAdapter.getView()","Incompatible type!");
                        return null;
                    }
                    if (!separator) {
                        holder.username = (TextView) convertView.findViewById(R.id.main_panel_chat_username);
                        holder.messageText = (TextView) convertView.findViewById(R.id.main_panel_chat_messageText);
                        holder.avatarThumbnail = (ImageView) convertView.findViewById(R.id.main_panel_chat_avatarThumbnail);
                    }
                    holder.timeStamp = (TextView) convertView.findViewById(R.id.main_panel_chat_timeStamp);
                    break;
                default:
                    Log.e("ChatListAdapter.getView()","Incompatible type!");
                    return null;
            }

            convertView.setTag(R.id.MainPanelChat_ListViewHolder,holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.MainPanelChat_ListViewHolder);
        }

        Message message = chatMessages.toArray(new Message[0])[position];

        convertView.setTag(R.id.BO_Message,message);

        if (!separator) {
            if (message.getUser() != null) {
                holder.username.setText(message.getUser().getPublicName());
                //Log.w("ChatListAdapter - getView","User color: ".concat(message.getUser().color));
                holder.username.setTextColor(Color.parseColor(message.getUser().color));
            }

            holder.messageText.setText(message.getMessageContent().getContent());
            holder.timeStamp.setText(TimestampFormatter.toLocaleString(message.getTimeStamp()));
        } else {
            holder.timeStamp.setText(DateFormatter.toHumanReadableString(message.getTimeStamp()));
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView username;
        public TextView messageText;
        public TextView timeStamp;
        public ImageView avatarThumbnail;
    }
}
