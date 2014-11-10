package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.GroupKind;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;
import com.chattyhive.chattyhive.util.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jonathan on 13/03/14.
 */
public class LeftPanelListAdapter extends BaseAdapter {

    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    private int visibleList;
    private View.OnClickListener clickListener;
    public Event<EventArgs> ListSizeChanged;

    public void SetVisibleList(int LeftPanel_ListKind) {
        this.visibleList = LeftPanel_ListKind;
        this.OnAddItem(this,EventArgs.Empty());
    }
    public  int GetVisibleList() {
        return this.visibleList;
    }

    public void SetOnClickListener (View.OnClickListener listener) {
        this.clickListener = listener;
        notifyDataSetChanged();
    }

    public void OnAddItem(Object sender, EventArgs args) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
                if ((ListSizeChanged != null) && (ListSizeChanged.count() > 0))
                    ListSizeChanged.fire(this, EventArgs.Empty());
            }
        });
    }

    public LeftPanelListAdapter (Context activityContext) {
        super();
        this.context = activityContext;
        this.ListSizeChanged = new Event<EventArgs>();
        this.inflater = ((Activity)this.context).getLayoutInflater();
        this.listView = ((ListView)((Activity)this.context).findViewById(R.id.left_panel_element_list));
        //this.listView.setAdapter(this);

    }

    @Override
    public int getItemViewType(int position) {
        return visibleList;
    }

    @Override
    public int getViewTypeCount() {
        return this.context.getResources().getInteger(R.integer.LeftPanel_ListKind_Count);
    }

    @Override
    public int getCount() {
        int result = 0;
        if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            result = Hive.getHiveCount();
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            result = Group.getGroupCount();
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
            result = 0;
        }
        return result;
    }

    @Override
    public Object getItem(int position){
        if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            return Hive.getHiveByIndex(position);
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            return Group.getGroupByIndex(position);
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
            return null;
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        int type = visibleList;

        if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_None)) { return null; }
        if (convertView==null) {
            TypedValue alpha = new TypedValue();
            if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
                holder = new HiveViewHolder();
                convertView = this.inflater.inflate(R.layout.left_panel_hives_list_item,parent,false);
                ((HiveViewHolder)holder).hiveItem = (LinearLayout)convertView.findViewById((R.id.left_panel_hives_list_item_top_view));
                ((HiveViewHolder)holder).hiveName = (TextView)convertView.findViewById(R.id.left_panel_hives_list_item_hive_name);
                ((HiveViewHolder)holder).hiveImage = (ImageView)convertView.findViewById(R.id.left_panel_hives_list_item_img);
                ((HiveViewHolder)holder).hiveCategoryImage = (ImageView)convertView.findViewById(R.id.left_panel_hives_list_item_hive_categroy_img);
                ((HiveViewHolder)holder).hiveCategoryName = (TextView)convertView.findViewById(R.id.left_panel_hives_list_item_hive_category);
                ((HiveViewHolder)holder).hiveDescription = (TextView)convertView.findViewById(R.id.left_panel_hives_list_item_hive_description);
                ((HiveViewHolder)holder).hiveSubscribedUsers = (TextView)convertView.findViewById(R.id.left_panel_hives_list_item_hive_subscribed_users);
                ((HiveViewHolder)holder).hiveItem.setOnClickListener(clickListener);

                //Set the alpha values
                convertView.getContext().getResources().getValue(R.color.left_panel_hive_list_item_hive_subscribed_users_img_alpha, alpha, true);
                StaticMethods.SetAlpha((ImageView) convertView.findViewById(R.id.left_panel_hives_list_item_hive_subscribed_users_img), alpha.getFloat());
                convertView.getContext().getResources().getValue(R.color.left_panel_hive_list_item_hive_category_img_alpha, alpha, true);
                StaticMethods.SetAlpha(((HiveViewHolder)holder).hiveCategoryImage,alpha.getFloat());
            } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
                holder = new ChatViewHolder();
                convertView = this.inflater.inflate(R.layout.left_panel_chat_list_item,parent,false);
                ((ChatViewHolder)holder).chatItem = (RelativeLayout)convertView.findViewById((R.id.left_panel_chat_list_item_top_view));
                ((ChatViewHolder)holder).chatName = (TextView)convertView.findViewById(R.id.left_panel_chat_list_item_chat_name);
                ((ChatViewHolder)holder).chatLastMessage = (TextView)convertView.findViewById(R.id.left_panel_chat_list_item_last_message);
                ((ChatViewHolder)holder).chatImage = (ImageView)convertView.findViewById(R.id.left_panel_chat_list_item_big_img);
                ((ChatViewHolder)holder).chatHiveImage = (ImageView)convertView.findViewById(R.id.left_panel_chat_list_item_little_img);
                ((ChatViewHolder)holder).chatLastMessageTimestamp = (TextView)convertView.findViewById(R.id.left_panel_chat_list_item_timestamp);
                ((ChatViewHolder)holder).chatPendingMessagesNumber = (TextView)convertView.findViewById(R.id.left_panel_chat_list_item_number_messages);
                ((ChatViewHolder)holder).chatTypeImage = (ImageView)convertView.findViewById(R.id.left_panel_chat_list_item_item_type_img);
                ((ChatViewHolder)holder).chatItem.setOnClickListener(clickListener);

                //set the alpha values
                convertView.getContext().getResources().getValue(R.color.left_panel_chat_list_item_item_type_img_alpha, alpha, true);
                StaticMethods.SetAlpha(((ChatViewHolder)holder).chatTypeImage,alpha.getFloat());
            } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
                //convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_me,parent,false);
                holder = new FriendViewHolder();
            }

            if (convertView != null)
                convertView.setTag(R.id.LeftPanel_ListViewHolder,holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.LeftPanel_ListViewHolder);
        }

        Object item = this.getItem(position);

        if (item == null) {
            Log.w("LeftPanelListAdapter - getView", "item is NULL");
            return null;
        }

        if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            ((HiveViewHolder)holder).hiveName.setText(context.getResources().getString(R.string.hivename_identifier_character).concat(((Hive)item).getName()));
            ((HiveViewHolder)holder).hiveDescription.setText(((Hive)item).getDescription());
            Category.setCategory(((Hive) item).getCategory(),((HiveViewHolder)holder).hiveCategoryImage,((HiveViewHolder)holder).hiveCategoryName);
            ((HiveViewHolder)holder).hiveSubscribedUsers.setText("Unknown");
            ((HiveViewHolder)holder).hiveItem.setTag(R.id.BO_Hive,item);
            ((HiveViewHolder)holder).hiveImage.setImageResource(R.drawable.pestanha_chats_public_chat);
            try {
                Log.w("Showing Hive item",String.format("Image URL: %s",((Hive)item).getImageURL()));
                ((Hive) item).getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadHiveImage", EventArgs.class));
                ((Hive) item).getHiveImage().loadImage(Image.ImageSize.medium, 0);
            } catch (Exception e) { }
        } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            String GroupName = "";
            SpannableString LastMessage = new SpannableString("");
            Message lastMessage = null;
            String LastMessageTimestamp = "";

            try {
                lastMessage = ((Group)item).getChat().getLastMessage();
                Date timeStamp = lastMessage.getOrdinationTimeStamp();
                Date fiveMinutesAgo = new Date((new Date()).getTime() - 5*60*1000);
                Date today = DateFormatter.toDate(DateFormatter.toString(timeStamp));
                Calendar yesterday = Calendar.getInstance();
                yesterday.setTime(today);
                yesterday.roll(Calendar.DAY_OF_MONTH, false);
                if (timeStamp.after( fiveMinutesAgo ))
                    LastMessageTimestamp = this.context.getString(R.string.left_panel_imprecise_time_now);
                else if (timeStamp.after(today))
                    LastMessageTimestamp = TimestampFormatter.toLocaleString(timeStamp);
                else if (timeStamp.after(yesterday.getTime()))
                    LastMessageTimestamp = this.context.getString(R.string.left_panel_imprecise_time_yesterday);
                else
                    LastMessageTimestamp = DateFormatter.toHumanReadableString(timeStamp);
            } catch (Exception e) {
                //Log.w("ChatItem","Unable to recover last message: "+e.getMessage());
            }
            if (((Group)item).getGroupKind() == null) return null;

            switch (((Group)item).getGroupKind()) {
                case PUBLIC_SINGLE:
                    ((ChatViewHolder)holder).chatHiveImage.setVisibility(View.VISIBLE);
                    ((ChatViewHolder)holder).chatTypeImage.setImageResource(R.drawable.pestanha_chats_arroba);
                    ((ChatViewHolder)holder).chatImage.setImageResource(R.drawable.chats_users_online);
                    try {
                        ((Group) item).getParentHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadHiveImage", EventArgs.class));
                        ((Group) item).getParentHive().getHiveImage().loadImage(Image.ImageSize.small, 0);
                    } catch (Exception e) { }
                    for (User user : ((Group) item).getMembers())
                        if (!user.isMe()) {
                            if ((user.getUserPublicProfile() != null) && (user.getUserPublicProfile().getShowingName() != null)) {
                                GroupName = context.getResources().getString(R.string.public_username_identifier_character).concat(user.getUserPublicProfile().getShowingName());
                                try {
                                    user.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder,"loadChatImage",EventArgs.class));
                                    user.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.medium,0);
                                } catch (Exception e) { e.printStackTrace(); }
                            } else
                                user.UserLoaded.add(new EventHandler<EventArgs>(this, "OnAddItem", EventArgs.class));
                        }

                    if (lastMessage != null) {
                        LastMessage = new SpannableString(" ".concat(lastMessage.getMessageContent().getContent()));
                        Drawable img = null;
                        if (lastMessage.getUser().isMe()) {
                            img = this.context.getResources().getDrawable(R.drawable.ic_action_next_item);

                        } else {
                            img = this.context.getResources().getDrawable(R.drawable.ic_action_previous_item);
                        }
                        img.setBounds(0,0,((ChatViewHolder) holder).chatLastMessage.getLineHeight(),((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                        LastMessage.setSpan(new ImageSpan(img,ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    ((ChatViewHolder)holder).chatLastMessageTimestamp.setVisibility(View.VISIBLE);
                    ((ChatViewHolder)holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);
                    break;
                case PUBLIC_GROUP:
                    ((ChatViewHolder)holder).chatHiveImage.setVisibility(View.VISIBLE);
                    ((ChatViewHolder)holder).chatTypeImage.setImageResource(R.drawable.pestanha_hives_show_more_users);
                    ((ChatViewHolder)holder).chatImage.setImageResource(R.drawable.chats_users_online);
                    try {
                    ((Group)item).getParentHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder,"loadHiveImage",EventArgs.class));
                    ((Group)item).getParentHive().getHiveImage().loadImage(Image.ImageSize.small,0);
                    } catch (Exception e) { }
                    if ((((Group)item).getName() != null) && (!((Group)item).getName().isEmpty()))
                        GroupName = ((Group)item).getName();
                    else
                        for (User user : ((Group) item).getMembers())
                            if (!user.isMe()) {
                                if ((user.getUserPublicProfile() != null) && (user.getUserPublicProfile().getShowingName() != null))
                                    GroupName += ((GroupName.isEmpty())?"":", ").concat(context.getResources().getString(R.string.public_username_identifier_character).concat(user.getUserPublicProfile().getShowingName()));
                                else
                                    user.UserLoaded.add(new EventHandler<EventArgs>(this,"OnAddItem",EventArgs.class));
                            }

                    if (lastMessage != null) {
                        LastMessage = new SpannableString(" ".concat(lastMessage.getMessageContent().getContent()));
                        Drawable img = null;
                        if (lastMessage.getUser().isMe()) {
                            img = this.context.getResources().getDrawable(R.drawable.ic_action_next_item);

                        } else {
                            img = this.context.getResources().getDrawable(R.drawable.ic_action_previous_item);
                        }
                        img.setBounds(0,0,((ChatViewHolder) holder).chatLastMessage.getLineHeight(),((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                        LastMessage.setSpan(new ImageSpan(img,ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    ((ChatViewHolder)holder).chatLastMessageTimestamp.setVisibility(View.VISIBLE);
                    ((ChatViewHolder)holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);
                    break;
                case HIVE:
                    ((ChatViewHolder)holder).chatHiveImage.setVisibility(View.GONE);
                    ((ChatViewHolder)holder).chatTypeImage.setImageResource(R.drawable.pestanha_chats_public_chat);
                    ((ChatViewHolder)holder).chatImage.setImageResource(R.drawable.pestanha_chats_public_chat);
                    try {
                        ((Group)item).getParentHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder,"loadChatImage",EventArgs.class));
                        ((Group)item).getParentHive().getHiveImage().loadImage(Image.ImageSize.medium,0);
                    } catch (Exception e) { }
                    if (((Group) item).getParentHive() != null)
                        GroupName = context.getResources().getString(R.string.hivename_identifier_character).concat(((Group) item).getParentHive().getName());
                    if ((lastMessage != null) && (lastMessage.getUser() != null) && (lastMessage.getUser().getUserPublicProfile() != null) && (lastMessage.getUser().getUserPublicProfile().getShowingName() != null) && (lastMessage.getMessageContent() != null) && (lastMessage.getMessageContent().getContent() != null)) {
                        LastMessage = new SpannableString(context.getResources().getString(R.string.public_username_identifier_character).concat(lastMessage.getUser().getUserPublicProfile().getShowingName()).concat(": ").concat(lastMessage.getMessageContent().getContent()));
                    }
                    ((ChatViewHolder)holder).chatLastMessageTimestamp.setVisibility(View.INVISIBLE);
                    ((ChatViewHolder)holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);
                    ((ChatViewHolder)holder).chatImage.setAdjustViewBounds(true);
                    ((ChatViewHolder)holder).chatImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
                case PRIVATE_SINGLE:
                    ((ChatViewHolder)holder).chatHiveImage.setVisibility(View.GONE);
                    ((ChatViewHolder)holder).chatTypeImage.setImageResource(R.drawable.pestanha_chats_user);
                    ((ChatViewHolder)holder).chatImage.setImageResource(R.drawable.chats_users_online);
                    for (User user : ((Group) item).getMembers())
                        if (!user.isMe()) {
                            if ((user.getUserPrivateProfile() != null) && (user.getUserPrivateProfile().getShowingName() != null)) {
                                GroupName = user.getUserPrivateProfile().getShowingName();
                                try {
                                    user.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder,"loadChatImage",EventArgs.class));
                                    user.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.medium,0);
                                } catch (Exception e) { e.printStackTrace(); }
                            } else
                                user.UserLoaded.add(new EventHandler<EventArgs>(this, "OnAddItem", EventArgs.class));
                        }
                    if (lastMessage != null) {
                        LastMessage = new SpannableString(" ".concat(lastMessage.getMessageContent().getContent()));
                        Drawable img = null;
                        if (lastMessage.getUser().isMe()) {
                            img = this.context.getResources().getDrawable(R.drawable.ic_action_next_item);

                        } else {
                            img = this.context.getResources().getDrawable(R.drawable.ic_action_previous_item);
                        }
                        img.setBounds(0,0,((ChatViewHolder) holder).chatLastMessage.getLineHeight(),((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                        LastMessage.setSpan(new ImageSpan(img,ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    ((ChatViewHolder)holder).chatLastMessageTimestamp.setVisibility(View.VISIBLE);
                    ((ChatViewHolder)holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);
                    break;
                case PRIVATE_GROUP:
                    ((ChatViewHolder)holder).chatHiveImage.setVisibility(View.GONE);
                    ((ChatViewHolder)holder).chatTypeImage.setImageResource(R.drawable.pestanha_chats_group);
                    ((ChatViewHolder)holder).chatImage.setImageResource(R.drawable.chats_users_online);
                    if (((Group) item).getName() != null)
                        GroupName = ((Group)item).getName();
                    else
                        for (User user : ((Group) item).getMembers())
                            if (!user.isMe()) {
                                if ((user.getUserPrivateProfile() != null) && (user.getUserPrivateProfile().getShowingName() != null))
                                    GroupName += ((GroupName.isEmpty())?"":", ") + user.getUserPrivateProfile().getFirstName();
                                else
                                    user.UserLoaded.add(new EventHandler<EventArgs>(this,"OnAddItem",EventArgs.class));
                            }
                    if (lastMessage != null) {
                        LastMessage = new SpannableString(" ".concat(lastMessage.getMessageContent().getContent()));
                        Drawable img = null;
                        if (lastMessage.getUser().isMe()) {
                            img = this.context.getResources().getDrawable(R.drawable.ic_action_next_item);

                        } else {
                            img = this.context.getResources().getDrawable(R.drawable.ic_action_previous_item);
                        }
                        img.setBounds(0,0,((ChatViewHolder) holder).chatLastMessage.getLineHeight(),((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                        LastMessage.setSpan(new ImageSpan(img,ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    ((ChatViewHolder)holder).chatLastMessageTimestamp.setVisibility(View.VISIBLE);
                    ((ChatViewHolder)holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);
                    break;
                default:
                    return null;
            }

            ((ChatViewHolder)holder).chatLastMessageTimestamp.setText(LastMessageTimestamp);
            ((ChatViewHolder)holder).chatName.setText(((GroupName == null) || (GroupName.isEmpty()))?"No chat name":GroupName);
            ((ChatViewHolder) holder).chatLastMessage.setText(LastMessage);

            ((ChatViewHolder)holder).chatItem.setTag(R.id.BO_Chat,item);
        } /*else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {

        }*/

        return convertView;
    }

    private abstract class ViewHolder{}

    private class HiveViewHolder extends ViewHolder {
        public LinearLayout hiveItem;
        public TextView hiveName;
        public ImageView hiveImage;
        public TextView hiveDescription;
        public TextView hiveCategoryName;
        public ImageView hiveCategoryImage;
        public TextView hiveSubscribedUsers;

        public void loadHiveImage(Object sender,EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final HiveViewHolder thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.medium,0);
                    if (is != null) {
                        hiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"loadHiveImage",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }
    }

    private class ChatViewHolder extends ViewHolder {
        public RelativeLayout chatItem;
        public TextView chatName;
        public TextView chatLastMessage;
        public ImageView chatImage;
        public ImageView chatHiveImage;
        public ImageView chatTypeImage;
        public TextView chatLastMessageTimestamp;
        public TextView chatPendingMessagesNumber;

        public void loadHiveImage(Object sender,EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final ChatViewHolder thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.small,0);
                    if (is != null) {
                        chatHiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"loadHiveImage",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }

        public void loadChatImage(Object sender,EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final ChatViewHolder thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.medium,0);
                    if (is != null) {
                        chatImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"loadChatImage",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }
    }

    private class FriendViewHolder extends ViewHolder {

    }
}
