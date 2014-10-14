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

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.GroupKind;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_INTERVAL;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.backend.util.formatters.TimestampFormatter;

import java.util.Date;


/**
 * Created by Jonathan on 25/03/14.
 */
public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private ListView listView;
    private LayoutInflater inflater;

    private GroupKind chatKind;
    private Chat channelChat;

    public ChatListAdapter (Context activityContext,Chat channelChat) {
        this.channelChat = channelChat;

        this.context = activityContext;
        this.inflater = ((Activity)this.context).getLayoutInflater();

        this.listView = ((ListView)((Activity)this.context).findViewById(R.id.left_panel_element_list));
        this.chatKind = this.channelChat.getParent().getGroupKind();
    }

    public void OnAddItem(Object sender, EventArgs args) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Message m = this.channelChat.getMessageByIndex(position);
        if (m.getUser() == null) {
            if (m.getMessageContent().getContentType().equalsIgnoreCase("DATE_SEPARATOR"))
                return context.getResources().getInteger(R.integer.MainPanelChat_ListKind_DateSeparator);
            else if (m.getMessageContent().getContentType().equalsIgnoreCase("HOLE_SEPARATOR"))
                return context.getResources().getInteger(R.integer.MainPanelChat_ListKind_HoleSeparator);
            else
                return context.getResources().getInteger(R.integer.MainPanelChat_ListKind_None);
        }
        return (((m.getUser() != null) && (m.getUser().isMe()))?context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Me):context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Other));
    }

    @Override
    public int getViewTypeCount() {
        return context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Count);
    }

    @Override
    public int getCount() {
        return this.channelChat.getCount();
    }

    @Override
    public Object getItem(int position){
        return this.channelChat.getMessageByIndex(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);

        Boolean isMessage = false;
        Boolean isHoleMarker = false;

        if(convertView==null){
            holder = new ViewHolder();
            switch (chatKind) {
                case HIVE:
                    if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Me)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_me, parent, false);
                        isMessage = true;
                    } else if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Other)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_other, parent, false);
                        isMessage = true;
                    } else if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_DateSeparator)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_day_marker, parent, false);
                        holder.timeStamp = (TextView) convertView.findViewById(R.id.main_panel_chat_timeStamp);
                    } else if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_HoleSeparator)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_message_hole, parent, false);
                        holder.messageText = (TextView) convertView.findViewById(R.id.main_panel_chat_messageText);
                        isHoleMarker = true;
                    } else {
                        Log.e("ChatListAdapter.getView()","Incompatible type!");
                        return null;
                    }
                    if (isMessage) {
                        holder.username = (TextView) convertView.findViewById(R.id.main_panel_chat_username);
                        holder.messageText = (TextView) convertView.findViewById(R.id.main_panel_chat_messageText);
                        holder.avatarThumbnail = (ImageView) convertView.findViewById(R.id.main_panel_chat_avatarThumbnail);
                        holder.timeStamp = (TextView) convertView.findViewById(R.id.main_panel_chat_timeStamp);
                    }
                    break;
                case PUBLIC_SINGLE:
                case PRIVATE_SINGLE:
                    if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Me)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_single_message_me, parent, false);
                        isMessage = true;
                    } else if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Other)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_single_message_other, parent, false);
                        isMessage = true;
                    } else if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_DateSeparator)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_day_marker, parent, false);
                        holder.timeStamp = (TextView) convertView.findViewById(R.id.main_panel_chat_timeStamp);
                    } else if (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_HoleSeparator)) {
                        convertView = this.inflater.inflate(R.layout.main_panel_chat_message_hole, parent, false);
                        holder.messageText = (TextView) convertView.findViewById(R.id.main_panel_chat_messageText);
                        isHoleMarker = true;
                    } else {
                        Log.e("ChatListAdapter.getView()","Incompatible type!");
                        return null;
                    }
                    if (isMessage) {
                        holder.messageText = (TextView) convertView.findViewById(R.id.main_panel_chat_single_message_messageText);
                        holder.timeStamp = (TextView) convertView.findViewById(R.id.main_panel_chat_single_message_timeStamp);
                    }
                    break;
                default:
                    Log.e("ChatListAdapter.getView()","Unknown chat type!");
                    return null;
            }

            convertView.setTag(R.id.MainPanelChat_ListViewHolder,holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.MainPanelChat_ListViewHolder);
        }

        Message message = this.channelChat.getMessageByIndex(position);

        convertView.setTag(R.id.BO_Message,message);

        if (isMessage) {
            if ((message.getUser() != null) && (holder.username != null)) {
                if ((this.chatKind == GroupKind.PRIVATE_SINGLE) || (this.chatKind == GroupKind.PRIVATE_GROUP)) {
                    holder.username.setText(message.getUser().getUserPrivateProfile().getShowingName());
                } else {
                    holder.username.setText(message.getUser().getUserPublicProfile().getShowingName());
                    holder.username.setTextColor(Color.parseColor(message.getUser().getUserPublicProfile().getColor()));
                }
            }

            holder.messageText.setText(message.getMessageContent().getContent());
            /*if (message.getServerTimeStamp() != null)
                holder.timeStamp.setText(TimestampFormatter.toLocaleString(message.getServerTimeStamp()));
            else if (message.getTimeStamp() != null)
                holder.timeStamp.setText(TimestampFormatter.toLocaleString(message.getTimeStamp()));
            else*/
                holder.timeStamp.setText(TimestampFormatter.toLocaleString(new Date()));
            if (message.getUser().isMe()) {
                if (((this.chatKind == GroupKind.PRIVATE_SINGLE) || (this.chatKind == GroupKind.PUBLIC_SINGLE)) && (message.getConfirmed())) {
                    holder.timeStamp.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.abc_ic_cab_done_holo_light,0);
                } else if (message.getId() != null) {
                    //holder.timeStamp.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                } else {
                    holder.timeStamp.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.pestanha_hives_historial,0);
                }
            }
        } else if (!isHoleMarker) {
            if (message.getTimeStamp() != null)
                holder.timeStamp.setText(DateFormatter.toHumanReadableString(message.getTimeStamp()));
            else
                holder.timeStamp.setText(DateFormatter.toHumanReadableString(new Date()));
        } else {
            holder.messageText.setText(String.format("Loading %s messages...",message.getMessageContent().getContent()));
            holder.messageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.menu_new_hive_blanco,0,0,0);
            message.FillMessageHole(this.channelChat.getMessageByIndex(position+1).getId());
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
