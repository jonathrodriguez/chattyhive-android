package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.Core.BusinessObjects.Chats.ChatType;
import com.chattyhive.Core.BusinessObjects.Chats.Conversation;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Image;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.Core.Util.Formatters.DateFormatter;
import com.chattyhive.Core.Util.Formatters.TimestampFormatter;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Jonathan on 25/03/14.
 */
public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private ListView listView;
    private LayoutInflater inflater;

    private ChatType chatType;
    private Conversation channelConversation;

    private ArrayList<Message> messages;

    public ChatListAdapter (Context activityContext,Conversation channelConversation) {
        this.channelConversation = channelConversation;

        this.context = activityContext;
        this.inflater = ((Activity)this.context).getLayoutInflater();

        this.listView = ((ListView)((Activity)this.context).findViewById(R.id.left_panel_element_list));
        this.chatType = this.channelConversation.getParent().getChatType();

        this.messages = new ArrayList<Message>(this.channelConversation.getMessages());
        this.notifyDataSetChanged();
    }

    public void OnAddItem(Object sender, EventArgs args) { //TODO: This is only a patch. Message collection must be updated on UIThread.
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                messages = null;
                while (messages == null)
                    try { messages = new ArrayList<Message>(channelConversation.getMessages()); } catch (Exception e) { messages = null; }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Message m = this.messages.get(position);

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
        return this.messages.size();
    }

    @Override
    public Object getItem(int position){
        return this.messages.get(position);
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

        if (convertView==null) {
            holder = new ViewHolder();
            switch (chatType) {
                case PUBLIC:
                case PRIVATE_GROUP_HIVEMATE:
                case PRIVATE_GROUP_FRIEND:
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
                        holder.messageImage = (ImageView) convertView.findViewById(R.id.main_panel_chat_image);
                        holder.avatarThumbnail = (ImageView) convertView.findViewById(R.id.main_panel_chat_avatarThumbnail);
                        holder.timeStamp = (TextView) convertView.findViewById(R.id.main_panel_chat_timeStamp);
                        holder.chatItem = convertView.findViewById(R.id.main_panel_chat_item);
                    }
                    break;
                case PRIVATE_HIVEMATE:
                case PRIVATE_FRIEND:
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
                        holder.messageImage = (ImageView) convertView.findViewById(R.id.main_panel_chat_single_message_image);
                        holder.timeStamp = (TextView) convertView.findViewById(R.id.main_panel_chat_single_message_timeStamp);
                        holder.chatItem = convertView.findViewById(R.id.main_panel_chat_item);
                        holder.tickImage = (ImageView)convertView.findViewById(R.id.main_panel_chat_single_message_confirm_icon);
                    }
                    break;
                default:
                    Log.e("ChatListAdapter.getView()","Unknown chat type!");
                    return null;
            }

            convertView.setTag(R.id.MainPanelChat_ListViewHolder,holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.MainPanelChat_ListViewHolder);
            isMessage = ((type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Me)) || (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_Other)));
            isHoleMarker = (type == context.getResources().getInteger(R.integer.MainPanelChat_ListKind_HoleSeparator));
            if ((type != context.getResources().getInteger(R.integer.MainPanelChat_ListKind_DateSeparator)) && (!isMessage) && (!isHoleMarker)) {
                Log.e("ChatListAdapter.getView()","Incompatible type!");
                return null;
            }
        }

        Message message = this.messages.get(position);

        convertView.setTag(R.id.BO_Message,message);

        if (isMessage) {
            if ((message.getUser() != null) && (holder.username != null)) {
                if ((this.chatType == ChatType.PRIVATE_FRIEND) || (this.chatType == ChatType.PRIVATE_GROUP_FRIEND)) {
                    holder.username.setText(message.getUser().getUserPrivateProfile().getShowingName());
                } else {
                    holder.username.setText(context.getResources().getString(R.string.public_username_identifier_character).concat(message.getUser().getUserPublicProfile().getShowingName()));
                    holder.username.setTextColor(Color.parseColor(message.getUser().getUserPublicProfile().getColor()));
                }
            }

            holder.messageImage.setVisibility(View.GONE);
            holder.messageText.setVisibility(View.VISIBLE);
            holder.messageText.setText(message.getMessageContent().getContent());

            if ((message.getMessageContent().getContentType() != null) && (message.getMessageContent().getContentType().equalsIgnoreCase("IMAGE")))
            {
                Image image = null;
                try { image = message.getMessageContent().getImage(); } catch (Exception e) {}
                if (image != null) {
                    image.OnImageLoaded.add(new EventHandler<EventArgs>(holder, "onMessageImageLoaded", EventArgs.class));
                    image.loadImage(Image.ImageSize.xlarge, 0);
                }
            }

            /*if (message.getServerTimeStamp() != null)
                holder.timeStamp.setText(TimestampFormatter.toLocaleString(message.getServerTimeStamp()));
            else if (message.getTimeStamp() != null)
                holder.timeStamp.setText(TimestampFormatter.toLocaleString(message.getTimeStamp()));
            else*/
            if (message.getOrdinationTimeStamp() != null)
                holder.timeStamp.setText(TimestampFormatter.toLocaleString(message.getOrdinationTimeStamp()));
            else
                holder.timeStamp.setText(TimestampFormatter.toLocaleString(new Date()));

            if (message.getUser().isMe()) {
                if (((this.chatType == ChatType.PRIVATE_FRIEND) || (this.chatType == ChatType.PRIVATE_HIVEMATE)) && (message.getConfirmed())) {
                    StaticMethods.SetAlpha(holder.chatItem,1f);
                    if (holder.tickImage != null)
                        holder.tickImage.setVisibility(View.VISIBLE);
                } else if (message.getId() != null) {
                    StaticMethods.SetAlpha(holder.chatItem,1f);
                    if (holder.tickImage != null)
                        holder.tickImage.setVisibility(View.GONE);
                } else {
                    StaticMethods.SetAlpha(holder.chatItem,0.5f);
                    if (holder.tickImage != null)
                        holder.tickImage.setVisibility(View.GONE);
                }
            } else {
                StaticMethods.SetAlpha(holder.chatItem,1f);
                if (holder.tickImage != null)
                    holder.tickImage.setVisibility(View.GONE);
            }

            //Load image
            if (holder.avatarThumbnail != null) {
                Image image = null;
                if ((chatType == ChatType.PUBLIC) || (chatType == ChatType.PRIVATE_GROUP_HIVEMATE)) {
                    if ((message != null) && (message.getUser() != null) && (message.getUser().getUserPublicProfile() != null))
                        image = message.getUser().getUserPublicProfile().getProfileImage();
                }
                else if (chatType == ChatType.PRIVATE_GROUP_FRIEND) {
                    if ((message != null) && (message.getUser() != null) && (message.getUser().getUserPrivateProfile() != null))
                        image = message.getUser().getUserPrivateProfile().getProfileImage();
                }
                if (image != null) {
                    image.OnImageLoaded.add(new EventHandler<EventArgs>(holder,"onImageLoaded",EventArgs.class));
                    image.loadImage(Image.ImageSize.small,0);
                }
            }
        } else if (!isHoleMarker) {
            if (message.getTimeStamp() != null)
                holder.timeStamp.setText(DateFormatter.toHumanReadableString(message.getTimeStamp()));
            else
                holder.timeStamp.setText(DateFormatter.toHumanReadableString(new Date()));
        } else {
            holder.messageText.setText(String.format("Loading %s messages...",message.getMessageContent().getContent()));
            message.FillMessageHole(this.channelConversation.getMessageByIndex(position+1).getId());
        }
        return convertView;
    }

    private class ViewHolder {
        public View chatItem;
        public TextView username;
        public TextView messageText;
        public ImageView messageImage;
        public TextView timeStamp;
        public ImageView avatarThumbnail;
        public ImageView tickImage;

        public void onMessageImageLoaded(Object sender,EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final ViewHolder thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.xlarge,0);
                    if ((is != null) && (messageImage != null)) {
                        messageImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        messageImage.setVisibility(View.VISIBLE);
                        messageText.setVisibility(View.GONE);
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (is != null)
                        image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"onMessageImageLoaded",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }

        public void onImageLoaded(Object sender,EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final ViewHolder thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.small,0);
                    if ((is != null) && (avatarThumbnail != null)) {
                        avatarThumbnail.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (is != null)
                        image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"onImageLoaded",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }
    }
}
