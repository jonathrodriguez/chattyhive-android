package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.businessobjects.Users.ProfileLevel;
import com.chattyhive.backend.businessobjects.Users.ProfileType;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

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
    public ArrayList<Hive> hiveList;
    public ArrayList<Chat> chatList;
    public ArrayList<User> friendList;
    private int expandedCard = -1;
    private int expandedList = -1;
    private int usersPage = 0;
    private int usersFilter = 1;
    private boolean refresh = true;

    public void SetVisibleList(int LeftPanel_ListKind) {
        this.visibleList = LeftPanel_ListKind;
        this.OnAddItem(this, EventArgs.Empty());
    }

    public int GetVisibleList() {
        return this.visibleList;
    }

    public void SetOnClickListener(View.OnClickListener listener) {
        this.clickListener = listener;
        notifyDataSetChanged();
    }

    public void OnAddItem(Object sender, EventArgs args) {  //TODO: This is only a patch. Hive and Chat collections must be updated on UIThread.
        ((Activity) this.context).runOnUiThread(new Runnable() {
            public void run() {
                hiveList = null;
                chatList = null;
                friendList = null;
                if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
                    while (hiveList == null)
                        try {
                            CaptureHives();
                        } catch (Exception e) {
                            // e.printStackTrace();
                            hiveList = null;
                        }
                } else if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
                    while (chatList == null)
                        try {
                            CaptureChats();
                        } catch (Exception e) {
                            // e.printStackTrace();
                            chatList = null;
                        }
                } else if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
                    while (friendList == null)
                        try {
                            CaptureFriends();
                        } catch (Exception e) {
                            //  e.printStackTrace();
                            friendList = null;
                        }
                }

                notifyDataSetChanged();
                if ((ListSizeChanged != null) && (ListSizeChanged.count() > 0))
                    ListSizeChanged.fire(this, EventArgs.Empty());
            }
        });
    }

    private void CaptureChats() {
        TreeSet<Chat> list = new TreeSet<Chat>(new Comparator<Chat>() {
            @Override
            public int compare(Chat lhs, Chat rhs) { // lhs < rhs => return < 0 | lhs = rhs => return = 0 | lhs > rhs => return > 0
                int res = 0;
                if ((lhs == null) && (rhs != null))
                    res = 1;
                else if ((lhs != null) && (rhs == null))
                    res = -1;
                else if (lhs == null) //&& (rhs == null)) <- Which is always true
                    res = 0;
                else {
                    Date lhsDate = null;
                    Date rhsDate = null;

                    if ((lhs.getConversation() != null) && (lhs.getConversation().getLastMessage() != null))
                        lhsDate = lhs.getConversation().getLastMessage().getOrdinationTimeStamp();
                    else
                        lhsDate = lhs.getCreationDate();

                    if ((rhs.getConversation() != null) && (rhs.getConversation().getLastMessage() != null))
                        rhsDate = rhs.getConversation().getLastMessage().getOrdinationTimeStamp();
                    else
                        rhsDate = rhs.getCreationDate();

                    if ((lhsDate == null) && (rhsDate != null))
                        res = 1;
                    else if ((lhsDate != null) && (rhsDate == null))
                        res = -1;
                    else if (lhsDate != null) //&& (rhsDate != null)) <- Which is always true
                        res = rhsDate.compareTo(lhsDate);
                    else {
                        lhsDate = lhs.getCreationDate();
                        rhsDate = rhs.getCreationDate();

                        if ((lhsDate == null) && (rhsDate != null))
                            res = 1;
                        else if ((lhsDate != null) && (rhsDate == null))
                            res = -1;
                        else if (lhsDate != null) //&& (rhsDate != null)) <- Which is always true
                            res = rhsDate.compareTo(lhsDate);
                        else {
                            res = 0;
                        }
                    }
                }

                return res;
            }
        });
        list.addAll(Chat.getChats());
        chatList = new ArrayList<Chat>(list);
    }

    private void CaptureHives() {
        TreeSet<Hive> list = new TreeSet<Hive>(new Comparator<Hive>() {
            @Override
            public int compare(Hive lhs, Hive rhs) { // lhs < rhs => return < 0 | lhs = rhs => return = 0 | lhs > rhs => return > 0
                int res = 0;
                if ((lhs == null) && (rhs != null))
                    res = 1;
                else if ((lhs != null) && (rhs == null))
                    res = -1;
                else if (lhs == null) //&& (rhs == null)) <- Which is always true
                    res = 0;
                else {
                    Date lhsDate = null;
                    Date rhsDate = null;

                    //TODO: Change comparison method. Instead of creation date use lastLocalUserActivityDate. Find a way to determine this value.

                    /*if ((lhs.getPublicChat().getConversation() != null) && (lhs.getPublicChat().getConversation().getCount() > 0) && (lhs.getPublicChat().getConversation().getLastMessage() != null))
                        lhsDate = lhs.getPublicChat().getConversation().getLastMessage().getOrdinationTimeStamp();
                    else if (lhs.getCreationDate() != null)*/
                    lhsDate = lhs.getCreationDate();

                   /* if ((rhs.getPublicChat().getConversation() != null) && (rhs.getPublicChat().getConversation().getCount() > 0) && (rhs.getPublicChat().getConversation().getLastMessage() != null))
                        rhsDate = rhs.getPublicChat().getConversation().getLastMessage().getOrdinationTimeStamp();
                    else if (rhs.getCreationDate() != null)*/
                    rhsDate = rhs.getCreationDate();

                    if ((lhsDate == null) && (rhsDate != null))
                        res = 1;
                    else if ((lhsDate != null) && (rhsDate == null))
                        res = -1;
                    else if (lhsDate != null) //&& (rhsDate != null)) <- Which is always true
                        res = rhsDate.compareTo(lhsDate);
                    else {
                        lhsDate = lhs.getCreationDate();
                        rhsDate = rhs.getCreationDate();

                        if ((lhsDate == null) && (rhsDate != null))
                            res = 1;
                        else if ((lhsDate != null) && (rhsDate == null))
                            res = -1;
                        else if ((lhsDate != null) && (rhsDate != null))
                            res = rhsDate.compareTo(lhsDate);
                        else {
                            res = 0;
                        }
                    }
                }

                return res;
            }
        });
        list.addAll(Hive.getHives());
        hiveList = new ArrayList<Hive>(list);
    }

    private void CaptureFriends() {
        TreeSet<User> list = new TreeSet<User>(new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) { // lhs < rhs => return < 0 | lhs = rhs => return = 0 | lhs > rhs => return > 0
                int res = 0;
                if ((lhs == null) && (rhs != null))
                    res = 1;
                else if ((lhs != null) && (rhs == null))
                    res = -1;
                else if (lhs == null) //&& (rhs == null)) <- Which is always true
                    res = 0;
                else {
                    String lhsName = null;
                    String rhsName = null;


                    if (lhs.getUserPrivateProfile() != null)
                        lhsName = lhs.getUserPrivateProfile().getShowingName();

                    if (rhs.getUserPrivateProfile() != null)
                        rhsName = rhs.getUserPrivateProfile().getShowingName();

                    if ((lhsName == null) && (rhsName != null))
                        res = 1;
                    else if ((lhsName != null) && (rhsName == null))
                        res = -1;
                    else if (lhsName != null) //&& (rhsName != null)) <- Which is always true
                        res = lhsName.compareToIgnoreCase(rhsName);
                    else {
                        if (lhs.getUserPublicProfile() != null)
                            lhsName = lhs.getUserPublicProfile().getShowingName();
                        if (rhs.getUserPublicProfile() != null)
                            rhsName = rhs.getUserPublicProfile().getShowingName();

                        if ((lhsName == null) && (rhsName != null))
                            res = 1;
                        else if ((lhsName != null) && (rhsName == null))
                            res = -1;
                        else if (lhsName != null) //&& (rhsName != null)) <- Which is always true
                            res = lhsName.compareTo(rhsName);
                        else {
                            res = 0;
                        }
                    }
                }

                return res;
            }
        });
        User me = ((Main)context).controller.getMe();
        if (me != null)
            list.addAll(me.getFriends());

        friendList = new ArrayList<User>(list);
    }

    public LeftPanelListAdapter (Context activityContext) {
        super();
        this.context = activityContext;
        this.ListSizeChanged = new Event<EventArgs>();
        this.inflater = ((Activity) this.context).getLayoutInflater();
        this.listView = ((ListView) ((Activity) this.context).findViewById(R.id.left_panel_element_list));
        //this.listView.setAdapter(this);

        if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            chatList = null;
            friendList = null;
            CaptureHives();
        } else if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            hiveList = null;
            friendList = null;
            CaptureChats();
        } else if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
            hiveList = null;
            chatList = null;
            CaptureFriends();
        }

        notifyDataSetChanged();
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
            result = hiveList.size();
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            result = chatList.size();
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
            result = friendList.size();
        }
        return result;
    }

    @Override
    public Object getItem(int position) {
        if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            return hiveList.get(position);
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            return chatList.get(position);
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
            return friendList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        int type = visibleList;

        if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_None)) {
            return null;
        }
        if (convertView == null) {
            TypedValue alpha = new TypedValue();
            if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
                holder = new HiveViewHolder();
                convertView = this.inflater.inflate(R.layout.left_panel_hives_list_item, parent, false);
                ((HiveViewHolder) holder).hiveItem = (LinearLayout) convertView.findViewById((R.id.left_panel_hives_list_item_top_view));
                ((HiveViewHolder) holder).hiveName = (TextView) convertView.findViewById(R.id.left_panel_hives_list_item_hive_name);
                ((HiveViewHolder) holder).hiveImage = (ImageView) convertView.findViewById(R.id.left_panel_hives_list_item_img);
                ((HiveViewHolder) holder).hiveCategoryImage = (ImageView) convertView.findViewById(R.id.left_panel_hives_list_item_hive_categroy_img);
                ((HiveViewHolder) holder).hiveCategoryName = (TextView) convertView.findViewById(R.id.left_panel_hives_list_item_hive_category);
                ((HiveViewHolder) holder).hiveDescription = (TextView) convertView.findViewById(R.id.left_panel_hives_list_item_hive_description);
                ((HiveViewHolder) holder).hiveSubscribedUsers = (TextView) convertView.findViewById(R.id.left_panel_hives_list_item_hive_subscribed_users);
                ((HiveViewHolder) holder).hiveTags = (WrapLayout) convertView.findViewById(R.id.context_wrap_layout_tags);
                ((HiveViewHolder) holder).hiveImageSmall = (ImageView) convertView.findViewById(R.id.left_panel_title_img);
                ((HiveViewHolder) holder).headerTextView = (TextView) convertView.findViewById(R.id.left_panel_title_text_view);
                ((HiveViewHolder) holder).contextTextView = (TextView) convertView.findViewById(R.id.context_list_item_expanded_hive_name);
                ((HiveViewHolder) holder).contextCategoryImage = (ImageView) convertView.findViewById(R.id.context_list_item_expanded_category_image);
                ((HiveViewHolder) holder).contextCategoryName = (TextView) convertView.findViewById(R.id.context_list_item_expanded_category_text);
                ((HiveViewHolder) holder).contextSubscribedUsers = (TextView) convertView.findViewById(R.id.context_list_item_expanded_users_number);
                ((HiveViewHolder) holder).contextLanguages = (WrapLayout) convertView.findViewById(R.id.context_list_item_expanded_hive_chat_languages);
                ((HiveViewHolder) holder).contextDescription = (TextView) convertView.findViewById(R.id.context_list_item_expanded_hive_description);
                ((HiveViewHolder) holder).contextStatsCreationDate = (TextView) convertView.findViewById(R.id.context_stats_creation_date);
                ((HiveViewHolder) holder).contextStatsLastActivityDate = (TextView) convertView.findViewById(R.id.context_stats_last_activity_date);
                ((HiveViewHolder) holder).contextHiveImage = (ImageView) convertView.findViewById(R.id.context_list_item_expanded_hive_image);
                ((HiveViewHolder) holder).contextUsersListView = (LinearLayout) convertView.findViewById(R.id.left_panel_chat_users);
                ((HiveViewHolder) holder).contextChatButton = (LinearLayout) convertView.findViewById(R.id.context_chat_button2);
                //((HiveViewHolder) holder).hiveItem.setOnClickListener(clickListener);
                ((HiveViewHolder) holder).contextUsersContainer = (LinearLayout) convertView.findViewById(R.id.left_panel_context_users_container);
                ((HiveViewHolder) holder).user1 = (LinearLayout) convertView.findViewById(R.id.left_panel_chat_context_user01);
                ((HiveViewHolder) holder).user2 = (LinearLayout) convertView.findViewById(R.id.left_panel_chat_context_user02);
                ((HiveViewHolder) holder).user3 = (LinearLayout) convertView.findViewById(R.id.left_panel_chat_context_user03);
                ((HiveViewHolder) holder).user4 = (LinearLayout) convertView.findViewById(R.id.left_panel_chat_context_user04);
                ((HiveViewHolder) holder).contextChat = (LinearLayout) convertView.findViewById(R.id.left_panel_chat);
                ((HiveViewHolder) holder).trendingButton = (LinearLayout) convertView.findViewById(R.id.left_panel_trending_button);
                ((HiveViewHolder) holder).locationButton = (LinearLayout) convertView.findViewById(R.id.left_panel_location_button);
                ((HiveViewHolder) holder).recentlyButton = (LinearLayout) convertView.findViewById(R.id.left_panel_recently_button);
                ((HiveViewHolder) holder).moreButton = (LinearLayout) convertView.findViewById(R.id.left_panel_context_more_users);

                ((HiveViewHolder) holder).moreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        usersPage++;
                        notifyDataSetChanged();
                    }
                });

                ((HiveViewHolder) holder).trendingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (usersFilter != 1) {
                            usersFilter = 1;
                            usersPage = 0;
                            notifyDataSetChanged();
                        }
                    }
                });

                ((HiveViewHolder) holder).locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (usersFilter != 2) {
                            usersFilter = 2;
                            usersPage = 0;
                            notifyDataSetChanged();
                        }
                    }
                });

                ((HiveViewHolder) holder).recentlyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (usersFilter != 3) {
                            usersFilter = 3;
                            usersPage = 0;
                            notifyDataSetChanged();
                        }
                    }
                });
                //((HiveViewHolder) holder).contextUserCard = (LinearLayout) convertView.findViewById(R.id.left_panel_chat_context_user);
                ((HiveViewHolder) holder).contextNoUsers = (LinearLayout) convertView.findViewById(R.id.left_panel_chat_context_no_user);

                ((HiveViewHolder) holder).leftPanelHeader = (LinearLayout) convertView.findViewById(R.id.left_panel_header);
                ((HiveViewHolder) holder).leftPanelCard = (LinearLayout) convertView.findViewById(R.id.left_panel_card);


                ((HiveViewHolder) holder).hiveImage.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (expandedCard == position) {//SI SE SELECCIONA EL HIVE YA EXPANDIDO SE PONE A -1
                            expandedCard = -1;
                        } else {
                            expandedCard = position;
                            expandedList = -1;
                        }

                        notifyDataSetChanged();
                    }
                });

                ((HiveViewHolder) holder).hiveImageSmall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (expandedCard == position) {//SI SE SELECCIONA EL HIVE YA EXPANDIDO SE PONE A -1
                            expandedCard = -1;
                        } else {
                            expandedCard = position;
                            expandedList = -1;
                        }
                        notifyDataSetChanged();
                    }
                });

                ((HiveViewHolder) holder).hiveItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (expandedList == position) {//SI SE SELECCIONA EL HIVE YA EXPANDIDO SE PONE A -1
                            expandedList = -1;
                        } else {
                            expandedList = position;
                            expandedCard = -1;
                        }

                        notifyDataSetChanged();
                    }
                });

                ((HiveViewHolder) holder).leftPanelHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (expandedList == position) {//SI SE SELECCIONA EL HIVE YA EXPANDIDO SE PONE A -1
                            expandedList = -1;
                        } else {
                            expandedList = position;
                            expandedCard = -1;
                        }

                        notifyDataSetChanged();
                    }
                });

                //Set the alpha values
                convertView.getContext().getResources().getValue(R.color.left_panel_hive_list_item_hive_subscribed_users_img_alpha, alpha, true);
                StaticMethods.SetAlpha((ImageView) convertView.findViewById(R.id.left_panel_hives_list_item_hive_subscribed_users_img), alpha.getFloat());
                convertView.getContext().getResources().getValue(R.color.left_panel_hive_list_item_hive_category_img_alpha, alpha, true);
                StaticMethods.SetAlpha(((HiveViewHolder) holder).hiveCategoryImage, alpha.getFloat());
            } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
                holder = new ChatViewHolder();
                convertView = this.inflater.inflate(R.layout.left_panel_chat_list_item, parent, false);
                ((ChatViewHolder) holder).chatItem = (RelativeLayout) convertView.findViewById((R.id.left_panel_chat_list_item_top_view));
                ((ChatViewHolder) holder).chatName = (TextView) convertView.findViewById(R.id.left_panel_chat_list_item_chat_name);
                ((ChatViewHolder) holder).chatLastMessage = (TextView) convertView.findViewById(R.id.left_panel_chat_list_item_last_message);
                ((ChatViewHolder) holder).chatImage = (ImageView) convertView.findViewById(R.id.left_panel_chat_list_item_big_img);
                ((ChatViewHolder) holder).chatHiveImage = (ImageView) convertView.findViewById(R.id.left_panel_chat_list_item_little_img);
                ((ChatViewHolder) holder).chatLastMessageTimestamp = (TextView) convertView.findViewById(R.id.left_panel_chat_list_item_timestamp);
                ((ChatViewHolder) holder).chatPendingMessagesNumber = (TextView) convertView.findViewById(R.id.left_panel_chat_list_item_number_messages);
                ((ChatViewHolder) holder).chatTypeImage = (ImageView) convertView.findViewById(R.id.left_panel_chat_list_item_item_type_img);
                ((ChatViewHolder) holder).chatItem.setOnClickListener(clickListener);

                //set the alpha values
                convertView.getContext().getResources().getValue(R.color.left_panel_chat_list_item_item_type_img_alpha, alpha, true);
                StaticMethods.SetAlpha(((ChatViewHolder) holder).chatTypeImage, alpha.getFloat());
            } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
                convertView = this.inflater.inflate(R.layout.left_panel_friend_list_item,parent,false);
                holder = new FriendViewHolder(convertView);
            }

            if (convertView != null)
                convertView.setTag(R.id.LeftPanel_ListViewHolder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.LeftPanel_ListViewHolder);
        }

        Object item = this.getItem(position);

        if (item == null) {
            Log.w("LeftPanelListAdapter - getView", "item is NULL");
            return null;
        }

        if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            if ((((Hive) item).getName() != null) && (!((Hive) item).getName().isEmpty()))
                ((HiveViewHolder) holder).hiveName.setText(context.getResources().getString(R.string.hivename_identifier_character).concat(((Hive) item).getName()));
            else
                ((HiveViewHolder) holder).hiveName.setText(context.getResources().getString(R.string.hivename_identifier_character).concat("<empty_hive_name>"));
            ((HiveViewHolder) holder).hiveDescription.setText(((Hive) item).getDescription());
            if ((((Hive) item).getCategory() != null) && (!((Hive) item).getCategory().isEmpty())) {
                Category.setCategory(((Hive) item).getCategory(), ((HiveViewHolder) holder).hiveCategoryImage, ((HiveViewHolder) holder).hiveCategoryName);
                Category.setCategory(((Hive) item).getCategory(), ((HiveViewHolder) holder).contextCategoryImage, ((HiveViewHolder) holder).contextCategoryName);
            } else {
                ((HiveViewHolder) holder).hiveCategoryImage.setImageResource(R.drawable.registro_important_note_orange);
                ((HiveViewHolder) holder).hiveCategoryName.setText("Unknown category");
            }
            ((HiveViewHolder) holder).hiveSubscribedUsers.setText(String.valueOf(((Hive) item).getSubscribedUsersCount()));
            ((HiveViewHolder) holder).contextSubscribedUsers.setText(context.getResources().getString(R.string.explore_hive_card_expanded_n_mates, Integer.valueOf(((Hive) item).getSubscribedUsersCount())));
            ((HiveViewHolder) holder).hiveItem.setTag(R.id.BO_Hive, item);
            if (((Hive) item).getHiveImage() == null) {
                ((HiveViewHolder) holder).hiveImage.setImageResource(R.drawable.default_hive_image);
                ((HiveViewHolder) holder).hiveImageSmall.setImageResource(R.drawable.default_hive_image);
                ((HiveViewHolder) holder).contextHiveImage.setImageResource(R.drawable.default_hive_image);
            } else {
                ((Hive) item).getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadHiveImage", EventArgs.class));
                ((Hive) item).getHiveImage().loadImage(Image.ImageSize.medium, 0);
                ((Hive) item).getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadHiveImageSmall", EventArgs.class));
                ((Hive) item).getHiveImage().loadImage(Image.ImageSize.small, 0);
            }

            String[] tagsArray = ((Hive) item).getTags();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(3, 3, 3, 3);
            if (tagsArray != null || tagsArray.length > 0) {
                convertView.findViewById(R.id.context_list_item_expanded_tags_layout).setVisibility(View.VISIBLE);
                ((HiveViewHolder) holder).hiveTags.removeAllViews();
                ((HiveViewHolder) holder).hiveTags.invalidate();
                for (int i = 0; i < tagsArray.length; i++) {
                    LinearLayout textContainer = new LinearLayout(context);
                    textContainer.setLayoutParams(params);
                    TextView tv = new TextView(context);
                    tv.setLayoutParams(params);
                    tv.setBackgroundResource(R.drawable.explore_tags_border);
                    tv.setText(tagsArray[i]);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    tv.setTextColor(Color.BLACK);
                    textContainer.addView(tv);
                    ((HiveViewHolder) holder).hiveTags.addView(textContainer);
                }
                ((HiveViewHolder) holder).hiveTags.requestLayout();
            }
            if (tagsArray.length == 0) {
                convertView.findViewById(R.id.context_list_item_expanded_tags_layout).setVisibility(View.GONE);
            }

            ((HiveViewHolder) holder).headerTextView.setText(context.getResources().getString(R.string.hivename_identifier_character).concat(((Hive) item).getName()));
            ((HiveViewHolder) holder).contextTextView.setText(context.getResources().getString(R.string.hivename_identifier_character).concat(((Hive) item).getName()));

            String[] languages_list = ((Hive) item).getChatLanguages();
            if (languages_list.length > 0) {
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ((HiveViewHolder) holder).contextLanguages.removeAllViews();
                ((HiveViewHolder) holder).contextLanguages.invalidate();
                TextView tv = new TextView(context);
                tv.setLayoutParams(params2);
                tv.setText(context.getResources().getString(R.string.explore_hive_card_expanded_hive_chat_langs));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                tv.setTextColor(Color.parseColor("#808080"));
                ((HiveViewHolder) holder).contextLanguages.addView(tv);
                for (int i = 0; i < languages_list.length - 1; i++) {
                    tv = new TextView(context);
                    tv.setLayoutParams(params2);
                    tv.setText(languages_list[i].concat(", "));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    tv.setTextColor(Color.BLACK);
                    ((HiveViewHolder) holder).contextLanguages.addView(tv);
                }
                tv = new TextView(context);
                tv.setLayoutParams(params2);
                tv.setText(languages_list[languages_list.length - 1]);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                tv.setTextColor(Color.BLACK);
                ((HiveViewHolder) holder).contextLanguages.addView(tv);
                ((HiveViewHolder) holder).contextLanguages.requestLayout();
            }

            ((HiveViewHolder) holder).contextDescription.setText("\"".concat(((Hive) item).getDescription()).concat("\""));

            if (((Hive) item).getCreationDate() != null) {
                DateFormatter dateFormatter = new DateFormatter();
                ((HiveViewHolder) holder).contextStatsCreationDate.setText(context.getResources().getString(R.string.context_stats_creation_date, dateFormatter.toShortHumanReadableString(((Hive) item).getCreationDate())));
            } else
                ((HiveViewHolder) holder).contextStatsCreationDate.setText(context.getResources().getString(R.string.context_stats_creation_date, ""));

            if (((Hive) item).getPublicChat() != null && ((Hive) item).getPublicChat().getConversation() != null && ((Hive) item).getPublicChat().getConversation().getLastMessage() != null && ((Hive) item).getPublicChat().getConversation().getLastMessage().getOrdinationTimeStamp() != null) {
                Date ladate = ((Hive) item).getPublicChat().getConversation().getLastMessage().getOrdinationTimeStamp();
                ((HiveViewHolder) holder).contextStatsLastActivityDate.setText(context.getResources().getString(R.string.context_stats_last_activity_date, updateTimeStamp(ladate)));
            } else
                ((HiveViewHolder) holder).contextStatsLastActivityDate.setText(context.getResources().getString(R.string.context_stats_last_activity_date, ""));

            if (expandedCard == position) {
                ((HiveViewHolder) holder).hiveItem.setVisibility(View.GONE);
                ((HiveViewHolder) holder).leftPanelHeader.setVisibility(View.VISIBLE);//visible header
                ((HiveViewHolder) holder).leftPanelCard.setVisibility(View.VISIBLE);//visible tarjeta del hive
                ((HiveViewHolder) holder).contextUsersListView.setVisibility(View.GONE);
            } else if (expandedList == position) {
                ((HiveViewHolder) holder).hiveItem.setVisibility(View.GONE);
                ((HiveViewHolder) holder).leftPanelHeader.setVisibility(View.VISIBLE);//visible header
                ((HiveViewHolder) holder).leftPanelCard.setVisibility(View.GONE);
                ((HiveViewHolder) holder).contextUsersListView.setVisibility(View.VISIBLE);//solo visible la lista de usuarios del chat

                ((Hive) item).OnSubscribedUsersListUpdated.add(new EventHandler<EventArgs>(holder, "loadSubscribedUsersList", EventArgs.class));

                //if (usersPage == 0) {
                    if (usersFilter == 1) {
                        ((Hive) item).requestUsers(0, 11, Hive.HiveUsersType.OUTSTANDING);
                    } else if (usersFilter == 2) {
                        ((Hive) item).requestUsers(0, 11, Hive.HiveUsersType.LOCATION);
                    } else if (usersFilter == 3) {
                        ((Hive) item).requestUsers(0, 11, Hive.HiveUsersType.RECENTLY_ONLINE);
                    }
                //}
                //fire(((Hive) item), EventArgs.Empty());
/*                if (((Hive) item).getSubscribedUsers()!=null && ((Hive) item).getSubscribedUsers().size()>0) {
                    ((HiveViewHolder) holder).subscribedUsers = ((Hive) item).getSubscribedUsers();
                    LinearLayout userView = ((HiveViewHolder) holder).contextUserCard;
                    for (int i = 0; i < ((HiveViewHolder) holder).subscribedUsers.size(); i++) {
                        System.out.println("no null: " + i);
                        ((TextView)userView.findViewById(R.id.left_panel_chat_context_user_public_name)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(((HiveViewHolder) holder).subscribedUsers.get(i).getUserPublicProfile().getShowingName()));
                        ((TextView)userView.findViewById(R.id.left_panel_chat_context_user_state)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(((HiveViewHolder) holder).subscribedUsers.get(i).getUserPublicProfile().getStatusMessage()));
                        ((HiveViewHolder) holder).contextUsersContainer.addView(userView);
                    }
                }
                else if (((Hive) item).getSubscribedUsers()==null || ((Hive) item).getSubscribedUsers().size()==0) {
                        System.out.println("null");
                    if (((HiveViewHolder) holder).contextNoUsers==null)
                        System.out.println("layout null");
                    ((HiveViewHolder) holder).contextUsersContainer.inflate(context,R.id.left_panel_chat_context_no_user,null);
                }*/

            } else {
                ((HiveViewHolder) holder).hiveItem.setVisibility(View.VISIBLE);//solo visible item
                ((HiveViewHolder) holder).leftPanelHeader.setVisibility(View.GONE);
                ((HiveViewHolder) holder).leftPanelCard.setVisibility(View.GONE);
                ((HiveViewHolder) holder).contextUsersListView.setVisibility(View.GONE);
            }

        } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            String GroupName = "";
            SpannableString LastMessage = new SpannableString("");
            Message lastMessage = null;
            String LastMessageTimestamp = "";

            try {
                lastMessage = ((Chat) item).getConversation().getLastMessage();
                Date timeStamp = lastMessage.getOrdinationTimeStamp();
                Date fiveMinutesAgo = new Date((new Date()).getTime() - 5 * 60 * 1000);
                Date today = DateFormatter.toDate(DateFormatter.toString(new Date()));
                Calendar yesterday = Calendar.getInstance();
                yesterday.setTime(today);
                yesterday.roll(Calendar.DATE, false);
                if (timeStamp.after(fiveMinutesAgo))
                    LastMessageTimestamp = this.context.getString(R.string.left_panel_imprecise_time_now);
                else if (timeStamp.after(today))
                    LastMessageTimestamp = TimestampFormatter.toLocaleString(timeStamp);
                else if (timeStamp.after(yesterday.getTime()))
                    LastMessageTimestamp = this.context.getString(R.string.left_panel_imprecise_time_yesterday);
                else
                    LastMessageTimestamp = DateFormatter.toShortHumanReadableString(timeStamp);
            } catch (Exception e) {
                //Log.w("ChatItem","Unable to recover last message: "+e.getMessage());
            }
            if (((Chat) item).getChatKind() == null) return null;

            ((ChatViewHolder) holder).chatHiveImage.setImageResource(R.drawable.default_hive_image);

            switch (((Chat) item).getChatKind()) {
                case PUBLIC_SINGLE:
                    ((ChatViewHolder) holder).chatHiveImage.setVisibility(View.VISIBLE);
                    ((ChatViewHolder) holder).chatTypeImage.setImageResource(R.drawable.pestanha_chats_arroba);
                    ((ChatViewHolder) holder).chatImage.setImageResource(R.drawable.chats_users_online);
                    try {
                        ((Chat) item).getParentHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadHiveImage", EventArgs.class));
                        ((Chat) item).getParentHive().getHiveImage().loadImage(Image.ImageSize.small, 0);
                        ((ChatViewHolder) holder).hiveName = context.getResources().getString(R.string.hivename_identifier_character).concat(((Chat) item).getParentHive().getName());
                    } catch (Exception e) {
                    }
                    for (User user : ((Chat) item).getMembers())
                        if (!user.isMe()) {
                            ((ChatViewHolder) holder).user = user;
                            if ((user.getUserPublicProfile() != null) && (user.getUserPublicProfile().getShowingName() != null)) {
                                GroupName = context.getResources().getString(R.string.public_username_identifier_character).concat(user.getUserPublicProfile().getShowingName());
                                try {
                                    user.getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadChatImage", EventArgs.class));
                                    user.getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.medium, 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else
                                user.UserLoaded.add(new EventHandler<EventArgs>(this, "OnAddItem", EventArgs.class));
                        }

                    if (lastMessage != null) {
                        String lastMessageString = "";
                        Drawable typeIcon = null;

                        if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase(this.context.getString(R.string.default_left_panel_image_content_type))) {
                            lastMessageString = "   ".concat(this.context.getString(R.string.default_left_panel_image_text));
                            typeIcon = this.context.getResources().getDrawable(R.drawable.default_left_panel_image_icon);
                        } else { //if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase("TEXT")) {
                            lastMessageString = " ".concat(lastMessage.getMessageContent().getContent());
                        }
                        
                        Drawable directionImg = this.context.getResources().getDrawable( (lastMessage.getUser().isMe()) ? R.drawable.default_left_panel_last_message_outgoing_icon : R.drawable.default_left_panel_last_message_incoming_icon );
                        directionImg.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                        if (typeIcon != null) {
                            typeIcon.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                            typeIcon.setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
                        }

                        LastMessage = new SpannableString(lastMessageString);

                        if (typeIcon != null) {
                            LastMessage.setSpan(new ImageSpan(typeIcon,ImageSpan.ALIGN_BOTTOM),1,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }

                        LastMessage.setSpan(new ImageSpan(directionImg,ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    ((ChatViewHolder) holder).chatLastMessageTimestamp.setVisibility(View.VISIBLE);
                    ((ChatViewHolder) holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);


                    ((ChatViewHolder) holder).profileType = Profile.ProfileType.Public;
                    ((ChatViewHolder) holder).chatImage.setOnClickListener(((ChatViewHolder) holder).thumbnailClickListener);
                    ((ChatViewHolder) holder).chatImage.setClickable(true);

                    break;
                case PUBLIC_GROUP:
                    ((ChatViewHolder) holder).chatHiveImage.setVisibility(View.VISIBLE);
                    ((ChatViewHolder) holder).chatTypeImage.setImageResource(R.drawable.pestanha_hives_show_more_users);
                    ((ChatViewHolder) holder).chatImage.setImageResource(R.drawable.chats_users_online);
                    try {
                        ((Chat) item).getParentHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadHiveImage", EventArgs.class));
                        ((Chat) item).getParentHive().getHiveImage().loadImage(Image.ImageSize.small, 0);
                    } catch (Exception e) {
                    }
                    if ((((Chat) item).getName() != null) && (!((Chat) item).getName().isEmpty()))
                        GroupName = ((Chat) item).getName();
                    else
                        for (User user : ((Chat) item).getMembers())
                            if (!user.isMe()) {
                                if ((user.getUserPublicProfile() != null) && (user.getUserPublicProfile().getShowingName() != null))
                                    GroupName += ((GroupName.isEmpty()) ? "" : ", ").concat(context.getResources().getString(R.string.public_username_identifier_character).concat(user.getUserPublicProfile().getShowingName()));
                                else
                                    user.UserLoaded.add(new EventHandler<EventArgs>(this, "OnAddItem", EventArgs.class));
                            }

                    if (lastMessage != null) {
                        String lastMessageString = "";
                        Drawable typeIcon = null;

                        if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase(this.context.getString(R.string.default_left_panel_image_content_type))) {
                            lastMessageString = "   ".concat(this.context.getString(R.string.default_left_panel_image_text));
                            typeIcon = this.context.getResources().getDrawable(R.drawable.default_left_panel_image_icon);
                        } else { //if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase("TEXT")) {
                            lastMessageString = " ".concat(lastMessage.getMessageContent().getContent());
                        }

                        Drawable directionImg = this.context.getResources().getDrawable( (lastMessage.getUser().isMe()) ? R.drawable.default_left_panel_last_message_outgoing_icon : R.drawable.default_left_panel_last_message_incoming_icon );
                        directionImg.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                        if (typeIcon != null) {
                            typeIcon.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                            typeIcon.setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
                        }

                        LastMessage = new SpannableString(lastMessageString);

                        if (typeIcon != null) {
                            LastMessage.setSpan(new ImageSpan(typeIcon,ImageSpan.ALIGN_BOTTOM),1,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }

                        LastMessage.setSpan(new ImageSpan(directionImg,ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    ((ChatViewHolder) holder).chatLastMessageTimestamp.setVisibility(View.VISIBLE);
                    ((ChatViewHolder) holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);

                    ((ChatViewHolder) holder).chatImage.setOnClickListener(null);
                    ((ChatViewHolder) holder).chatImage.setClickable(false);
                    break;
                case HIVE:
                    ((ChatViewHolder) holder).chatHiveImage.setVisibility(View.GONE);
                    ((ChatViewHolder) holder).chatTypeImage.setImageResource(R.drawable.pestanha_chats_public_chat);
                    ((ChatViewHolder) holder).chatImage.setImageResource(R.drawable.default_hive_image);
                    try {
                        ((Chat) item).getParentHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadChatImage", EventArgs.class));
                        ((Chat) item).getParentHive().getHiveImage().loadImage(Image.ImageSize.medium, 0);
                    } catch (Exception e) {
                    }
                    if (((Chat) item).getParentHive() != null)
                        GroupName = context.getResources().getString(R.string.hivename_identifier_character).concat(((Chat) item).getParentHive().getName());

                    if (lastMessage != null) {
                        String userName = "";
                        String lastMessageString = "";
                        Drawable typeIcon = null;

                        if ((lastMessage.getUser() != null) && (lastMessage.getUser().getUserPublicProfile() != null) && (lastMessage.getUser().getUserPublicProfile().getShowingName() != null)) {
                            userName = context.getResources().getString(R.string.public_username_identifier_character).concat(lastMessage.getUser().getUserPublicProfile().getShowingName()).concat(":");
                        }

                        if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase(this.context.getString(R.string.default_left_panel_image_content_type))) {
                            lastMessageString = "   ".concat(this.context.getString(R.string.default_left_panel_image_text));
                            typeIcon = this.context.getResources().getDrawable(R.drawable.default_left_panel_image_icon);
                        } else { //if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase("TEXT")) {
                            lastMessageString = " ".concat(lastMessage.getMessageContent().getContent());
                        }

                        if (typeIcon != null) {
                            typeIcon.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                            typeIcon.setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
                        }

                        LastMessage = new SpannableString(lastMessageString);

                        if (typeIcon != null) {
                            LastMessage.setSpan(new ImageSpan(typeIcon,ImageSpan.ALIGN_BOTTOM),1,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }

                        LastMessage = new SpannableString(TextUtils.concat(new SpannableString(userName),LastMessage));

                        //LastMessage.setSpan(new SpannableString(userName), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }

                    /*if ((lastMessage != null) && (lastMessage.getUser() != null) && (lastMessage.getUser().getUserPublicProfile() != null) && (lastMessage.getUser().getUserPublicProfile().getShowingName() != null) && (lastMessage.getMessageContent() != null) && (lastMessage.getMessageContent().getContent() != null)) {
                        LastMessage = new SpannableString(context.getResources().getString(R.string.public_username_identifier_character).concat(lastMessage.getUser().getUserPublicProfile().getShowingName()).concat(": ").concat(lastMessage.getMessageContent().getContent()));
                    }*/
                    ((ChatViewHolder)holder).chatLastMessageTimestamp.setVisibility(View.GONE);
                    ((ChatViewHolder)holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);
                    ((ChatViewHolder)holder).chatImage.setAdjustViewBounds(true);
                    ((ChatViewHolder)holder).chatImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    ((ChatViewHolder) holder).chatImage.setOnClickListener(null);
                    ((ChatViewHolder) holder).chatImage.setClickable(false);
                    break;
                case PRIVATE_SINGLE:
                    ((ChatViewHolder) holder).chatHiveImage.setVisibility(View.GONE);
                    ((ChatViewHolder) holder).chatTypeImage.setImageResource(R.drawable.pestanha_chats_user);
                    ((ChatViewHolder) holder).chatImage.setImageResource(R.drawable.default_profile_image_male);

                    for (User user : ((Chat) item).getMembers())
                        if (!user.isMe()) {
                            ((ChatViewHolder) holder).user = user;
                            if ((user.getUserPrivateProfile() != null) && (user.getUserPrivateProfile().getShowingName() != null)) {
                                GroupName = user.getUserPrivateProfile().getShowingName();
                                if (user.getUserPrivateProfile().getProfileImage() == null) {
                                    if ((user.getUserPrivateProfile().getSex() != null) && (user.getUserPrivateProfile().getSex().equalsIgnoreCase("female")))
                                        ((ChatViewHolder) holder).chatImage.setImageResource(R.drawable.default_profile_image_female);
                                } else {
                                    user.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadChatImage", EventArgs.class));
                                    user.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.medium, 0);
                                }
                            } else
                                user.UserLoaded.add(new EventHandler<EventArgs>(this, "OnAddItem", EventArgs.class));
                        }
                    if (lastMessage != null) {
                        String lastMessageString = "";
                        Drawable typeIcon = null;

                        if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase(this.context.getString(R.string.default_left_panel_image_content_type))) {
                            lastMessageString = "   ".concat(this.context.getString(R.string.default_left_panel_image_text));
                            typeIcon = this.context.getResources().getDrawable(R.drawable.default_left_panel_image_icon);
                        } else { //if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase("TEXT")) {
                            lastMessageString = " ".concat(lastMessage.getMessageContent().getContent());
                        }

                        Drawable directionImg = this.context.getResources().getDrawable( (lastMessage.getUser().isMe()) ? R.drawable.default_left_panel_last_message_outgoing_icon : R.drawable.default_left_panel_last_message_incoming_icon );
                        directionImg.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                        if (typeIcon != null) {
                            typeIcon.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                            typeIcon.setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
                        }

                        LastMessage = new SpannableString(lastMessageString);

                        if (typeIcon != null) {
                            LastMessage.setSpan(new ImageSpan(typeIcon,ImageSpan.ALIGN_BOTTOM),1,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }

                        LastMessage.setSpan(new ImageSpan(directionImg,ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    ((ChatViewHolder) holder).chatLastMessageTimestamp.setVisibility(View.VISIBLE);
                    ((ChatViewHolder) holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);

                    ((ChatViewHolder) holder).profileType = Profile.ProfileType.Private;
                    ((ChatViewHolder) holder).hiveName = null;

                    ((ChatViewHolder) holder).chatImage.setOnClickListener(((ChatViewHolder) holder).thumbnailClickListener);
                    ((ChatViewHolder) holder).chatImage.setClickable(true);
                    break;
                case PRIVATE_GROUP:
                    ((ChatViewHolder) holder).chatHiveImage.setVisibility(View.GONE);
                    ((ChatViewHolder) holder).chatTypeImage.setImageResource(R.drawable.pestanha_chats_group);
                    ((ChatViewHolder) holder).chatImage.setImageResource(R.drawable.chats_users_online);
                    if (((Chat) item).getName() != null)
                        GroupName = ((Chat) item).getName();
                    else
                        for (User user : ((Chat) item).getMembers())
                            if (!user.isMe()) {
                                if ((user.getUserPrivateProfile() != null) && (user.getUserPrivateProfile().getShowingName() != null))
                                    GroupName += ((GroupName.isEmpty()) ? "" : ", ") + user.getUserPrivateProfile().getFirstName();
                                else
                                    user.UserLoaded.add(new EventHandler<EventArgs>(this, "OnAddItem", EventArgs.class));
                            }
                    if (lastMessage != null) {
                        String lastMessageString = "";
                        Drawable typeIcon = null;

                        if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase(this.context.getString(R.string.default_left_panel_image_content_type))) {
                            lastMessageString = "   ".concat(this.context.getString(R.string.default_left_panel_image_text));
                            typeIcon = this.context.getResources().getDrawable(R.drawable.default_left_panel_image_icon);
                        } else { //if (lastMessage.getMessageContent().getContentType().equalsIgnoreCase("TEXT")) {
                            lastMessageString = " ".concat(lastMessage.getMessageContent().getContent());
                        }

                        Drawable directionImg = this.context.getResources().getDrawable( (lastMessage.getUser().isMe()) ? R.drawable.default_left_panel_last_message_outgoing_icon : R.drawable.default_left_panel_last_message_incoming_icon );
                        directionImg.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                        if (typeIcon != null) {
                            typeIcon.setBounds(0, 0, ((ChatViewHolder) holder).chatLastMessage.getLineHeight(), ((ChatViewHolder) holder).chatLastMessage.getLineHeight());
                            typeIcon.setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
                        }

                        LastMessage = new SpannableString(lastMessageString);

                        if (typeIcon != null) {
                            LastMessage.setSpan(new ImageSpan(typeIcon,ImageSpan.ALIGN_BOTTOM),1,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }

                        LastMessage.setSpan(new ImageSpan(directionImg,ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    ((ChatViewHolder) holder).chatLastMessageTimestamp.setVisibility(View.VISIBLE);
                    ((ChatViewHolder) holder).chatPendingMessagesNumber.setVisibility(View.INVISIBLE);

                    ((ChatViewHolder) holder).chatImage.setOnClickListener(null);
                    ((ChatViewHolder) holder).chatImage.setClickable(false);
                    break;
                default:
                    return null;
            }

            ((ChatViewHolder) holder).chatLastMessageTimestamp.setText(LastMessageTimestamp);
            ((ChatViewHolder) holder).chatName.setText(((GroupName == null) || (GroupName.isEmpty())) ? "No chat name" : GroupName);
            ((ChatViewHolder) holder).chatLastMessage.setText(LastMessage);

            ((ChatViewHolder)holder).chatItem.setTag(R.id.BO_Chat,item);
        } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
            ((FriendViewHolder)holder).setFriend((User)item);
        }

        return convertView;
    }

    private void openProfile(User user, Profile.ProfileType profileType, String hiveName) {
        if (user != null)
            ((Main) context).OpenWindow(new Profile(context, user, profileType, hiveName));
    }

    private abstract class ViewHolder {
    }

    private class HiveViewHolder extends ViewHolder {
        public LinearLayout hiveItem;
        public TextView hiveName;
        public ImageView hiveImage;
        public TextView hiveDescription;
        public TextView hiveCategoryName;
        public ImageView hiveCategoryImage;
        public TextView hiveSubscribedUsers;
        public WrapLayout hiveTags;
        public ImageView hiveImageSmall;
        public ImageView contextHiveImage;
        public TextView headerTextView;
        public TextView contextTextView;
        public ImageView contextCategoryImage;
        public TextView contextCategoryName;
        public TextView contextSubscribedUsers;
        public WrapLayout contextLanguages;
        public TextView contextDescription;
        public TextView contextStatsCreationDate;
        public TextView contextStatsLastActivityDate;
        public LinearLayout leftPanelHeader;
        public LinearLayout leftPanelCard;
        public LinearLayout contextUsersListView;
        public LinearLayout contextChatButton;
        public List<User> subscribedUsers;
        public LinearLayout contextUsersContainer;
        public LinearLayout contextUserCard;
        public LinearLayout contextNoUsers;
        //public int usersPage = 0;
        //public int usersFilter = 1;
        public LinearLayout user1;
        public LinearLayout user2;
        public LinearLayout user3;
        public LinearLayout user4;
        public LinearLayout contextChat;
        public LinearLayout trendingButton;
        public LinearLayout locationButton;
        public LinearLayout recentlyButton;
        public LinearLayout moreButton;

        public void loadHiveImage(Object sender, EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image) sender;
            final HiveViewHolder thisViewHolder = this;

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.medium, 0);
                    if (is != null) {
                        hiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                            contextHiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    InputStream is2 = image.getImage(Image.ImageSize.small, 0);
                    if (is2 != null) {
                        hiveImageSmall.setImageBitmap(BitmapFactory.decodeStream(is2));
                        try {
                            is2.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder, "loadHiveImage", EventArgs.class));
                    //image.freeMemory();
                }
            });
        }

        public void loadHiveImageSmall(Object sender, EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image) sender;
            final HiveViewHolder thisViewHolder = this;

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.small, 0);
                    if (is != null) {
                        hiveImageSmall.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder, "loadHiveImageSmall", EventArgs.class));
                    //image.freeMemory();
                }
            });
        }

        public void loadSubscribedUsersList(Object sender, EventArgs eventArgs) {

            final HiveViewHolder thisViewHolder = this;
            final Hive hive = (Hive) sender;

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (hive.getSubscribedUsers() != null && hive.getSubscribedUsers().size() > 0) {
                        thisViewHolder.subscribedUsers = hive.getSubscribedUsers();
                        System.out.println("Subscribed users array size: " + thisViewHolder.subscribedUsers.size());
                        contextNoUsers.setVisibility(View.GONE);

                        if (usersPage > 0) {
                            contextChat.setVisibility(View.GONE);

                            if (thisViewHolder.subscribedUsers.get(usersPage * 4 - 1) != null) {
                                if (thisViewHolder.subscribedUsers.get(usersPage * 4 - 1).getUserPublicProfile() != null)
                                    ((TextView) user1.findViewById(R.id.left_panel_chat_context_user_public_name01)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(thisViewHolder.subscribedUsers.get(usersPage * 4 - 1).getUserPublicProfile().getShowingName()));
                                if (thisViewHolder.subscribedUsers.get(usersPage * 4 - 1).getUserPublicProfile().getStatusMessage() != null)
                                    ((TextView) user1.findViewById(R.id.left_panel_chat_context_user_state01)).setText(thisViewHolder.subscribedUsers.get(usersPage * 4 - 1).getUserPublicProfile().getStatusMessage());
                                user1.setVisibility(View.VISIBLE);
                            } else {
                                user2.setVisibility(View.INVISIBLE);
                            }
                            if (thisViewHolder.subscribedUsers.get(usersPage * 4) != null) {
                                if (thisViewHolder.subscribedUsers.get(usersPage * 4).getUserPublicProfile() != null)
                                    ((TextView) user2.findViewById(R.id.left_panel_chat_context_user_public_name02)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(thisViewHolder.subscribedUsers.get(usersPage * 4).getUserPublicProfile().getShowingName()));
                                if (thisViewHolder.subscribedUsers.get(usersPage * 4).getUserPublicProfile().getStatusMessage() != null)
                                    ((TextView) user2.findViewById(R.id.left_panel_chat_context_user_state02)).setText(thisViewHolder.subscribedUsers.get(usersPage * 4).getUserPublicProfile().getStatusMessage());
                                user2.setVisibility(View.VISIBLE);
                            } else {
                                user2.setVisibility(View.INVISIBLE);
                            }

                            if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 1) != null) {
                                if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 1).getUserPublicProfile() != null)
                                    ((TextView) user3.findViewById(R.id.left_panel_chat_context_user_public_name03)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(thisViewHolder.subscribedUsers.get((usersPage * 4) + 1).getUserPublicProfile().getShowingName()));
                                if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 1).getUserPublicProfile().getStatusMessage() != null)
                                    ((TextView) user3.findViewById(R.id.left_panel_chat_context_user_state03)).setText(thisViewHolder.subscribedUsers.get((usersPage * 4) + 1).getUserPublicProfile().getStatusMessage());
                                user3.setVisibility(View.VISIBLE);
                            } else {
                                user3.setVisibility(View.GONE);
                            }
                            if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 2) != null) {
                                if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 2).getUserPublicProfile() != null)
                                    ((TextView) user4.findViewById(R.id.left_panel_chat_context_user_public_name04)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(thisViewHolder.subscribedUsers.get((usersPage * 4) + 2).getUserPublicProfile().getShowingName()));
                                if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 2).getUserPublicProfile().getStatusMessage() != null)
                                    ((TextView) user4.findViewById(R.id.left_panel_chat_context_user_state04)).setText(thisViewHolder.subscribedUsers.get((usersPage * 4) + 2).getUserPublicProfile().getStatusMessage());
                                user4.setVisibility(View.VISIBLE);
                            } else {
                                user4.setVisibility(View.GONE);
                            }
                        } else if (usersPage == 0) {
                            contextChat.setVisibility(View.VISIBLE);
                            user1.setVisibility(View.GONE);
                            if (thisViewHolder.subscribedUsers.get(usersPage * 4) != null) {
                                if (thisViewHolder.subscribedUsers.get(usersPage * 4).getUserPublicProfile() != null)
                                    ((TextView) user2.findViewById(R.id.left_panel_chat_context_user_public_name02)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(thisViewHolder.subscribedUsers.get(usersPage * 4).getUserPublicProfile().getShowingName()));
                                if (thisViewHolder.subscribedUsers.get(usersPage * 4).getUserPublicProfile().getStatusMessage() != null)
                                    ((TextView) user2.findViewById(R.id.left_panel_chat_context_user_state02)).setText(thisViewHolder.subscribedUsers.get(usersPage * 4).getUserPublicProfile().getStatusMessage());
                                user2.setVisibility(View.VISIBLE);
                            } else {
                                user2.setVisibility(View.INVISIBLE);
                            }

                            if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 1) != null) {
                                if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 1).getUserPublicProfile() != null)
                                    ((TextView) user3.findViewById(R.id.left_panel_chat_context_user_public_name03)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(thisViewHolder.subscribedUsers.get((usersPage * 4) + 1).getUserPublicProfile().getShowingName()));
                                if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 1).getUserPublicProfile().getStatusMessage() != null)
                                    ((TextView) user3.findViewById(R.id.left_panel_chat_context_user_state03)).setText(thisViewHolder.subscribedUsers.get((usersPage * 4) + 1).getUserPublicProfile().getStatusMessage());
                                user3.setVisibility(View.VISIBLE);
                            } else {
                                user3.setVisibility(View.GONE);
                            }
                            if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 2) != null) {
                                if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 2).getUserPublicProfile() != null)
                                    ((TextView) user4.findViewById(R.id.left_panel_chat_context_user_public_name04)).setText(context.getResources().getString(R.string.public_username_identifier_character).concat(thisViewHolder.subscribedUsers.get((usersPage * 4) + 2).getUserPublicProfile().getShowingName()));
                                if (thisViewHolder.subscribedUsers.get((usersPage * 4) + 2).getUserPublicProfile().getStatusMessage() != null)
                                    ((TextView) user4.findViewById(R.id.left_panel_chat_context_user_state04)).setText(thisViewHolder.subscribedUsers.get((usersPage * 4) + 2).getUserPublicProfile().getStatusMessage());
                                user4.setVisibility(View.VISIBLE);
                            } else {
                                user4.setVisibility(View.GONE);
                            }
                        }
                    } else if (hive.getSubscribedUsers() == null || hive.getSubscribedUsers().size() == 0) {
                        contextNoUsers.setVisibility(View.GONE);
                    }
                    //((Hive)sender).OnSubscribedUsersListUpdated.remove(new EventHandler<EventArgs>(thisViewHolder, "loadSubscribedUsersList", EventArgs.class));
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

        public User user;
        public String hiveName;
        public Profile.ProfileType profileType;

        public View.OnClickListener thumbnailClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((user != null) && (profileType != null))
                    openProfile(user, profileType, hiveName);
            }
        };

        public void loadHiveImage(Object sender, EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image) sender;
            final ChatViewHolder thisViewHolder = this;

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.small, 0);
                    if (is != null) {
                        chatHiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder, "loadHiveImage", EventArgs.class));
                    //image.freeMemory();
                }
            });
        }

        public void loadChatImage(Object sender, EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image) sender;
            final ChatViewHolder thisViewHolder = this;

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.medium, 0);
                    if (is != null) {
                        chatImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder, "loadChatImage", EventArgs.class));
                    //image.freeMemory();
                }
            });
        }
    }

    private class FriendViewHolder extends ViewHolder {
        private View cardView;
        private User cardFriend;

        private ImageView friendImage;

        private TextView friendFullName;
        private TextView friendNickname;
        private TextView friendStatusMsg;

        public void setCardView(View cardView) {
            if (this.cardView != cardView) {
                this.cardView = cardView;

                if (this.cardView != null) {
                    this.friendImage = ((ImageView) cardView.findViewById(R.id.left_panel_friend_list_item_image));
                    this.friendFullName = ((TextView) cardView.findViewById(R.id.left_panel_friend_list_item_full_name));
                    this.friendNickname = ((TextView) cardView.findViewById(R.id.left_panel_friend_list_item_nickname));
                    this.friendStatusMsg = ((TextView) cardView.findViewById(R.id.left_panel_friend_list_item_status));

                    if (this.cardFriend != null)
                        this.updateData();
                } else {
                    this.friendImage = null;
                    this.friendFullName = null;
                    this.friendNickname = null;
                    this.friendStatusMsg = null;
                }
            }
        }

        public void setFriend(User friend) {
            if (this.cardFriend != friend) {
                this.cardFriend = friend;

                if ((this.cardFriend != null) && (this.cardView != null))
                    this.updateData();
            }
        }

        public FriendViewHolder() {
            this(null, null);
        }

        public FriendViewHolder(View cardView) {
            this(cardView, null);
        }

        public FriendViewHolder(User friend) {
            this(null, friend);
        }

        public FriendViewHolder(View cardView, User friend) {
            this.setCardView(cardView);
            this.setFriend(friend);
        }

        private View.OnClickListener onCardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onImageClickListener.onClick(v);
                ((Main) context).OpenWindow(new MainChat(context, null, cardFriend));
            }
        };

        private View.OnClickListener onImageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(cardFriend, Profile.ProfileType.Private, null);
            }
        };

        private void updateData() {
            if ((this.cardFriend.getUserPrivateProfile() == null) || (this.cardFriend.getUserPrivateProfile().getLoadedProfileLevel().ordinal() < ProfileLevel.Basic.ordinal())) {
                this.cardFriend.UserLoaded.add(new EventHandler<EventArgs>(this, "onUserLoaded", EventArgs.class));
                this.cardFriend.loadProfile(ProfileType.PRIVATE, ProfileLevel.Basic);
                return;
            }
            if ((this.cardFriend.getUserPublicProfile() == null) || (this.cardFriend.getUserPublicProfile().getLoadedProfileLevel().ordinal() < ProfileLevel.Basic.ordinal())) {
                this.cardFriend.UserLoaded.add(new EventHandler<EventArgs>(this, "onUserLoaded", EventArgs.class));
                this.cardFriend.loadProfile(ProfileType.PUBLIC, ProfileLevel.Basic);
                return;
            }
            this.friendFullName.setText(this.cardFriend.getUserPrivateProfile().getShowingName());
            this.friendNickname.setText(context.getText(R.string.public_username_identifier_character).toString().concat(this.cardFriend.getUserPublicProfile().getPublicName()));

            String statusMessage = null;

            if (this.cardFriend.getUserPrivateProfile().getStatusMessage() != null)
                statusMessage = this.cardFriend.getUserPrivateProfile().getStatusMessage();

            if ((statusMessage == null) || (statusMessage.isEmpty())) {
                statusMessage = context.getString(R.string.profile_default_private_status_message);
            }
            this.friendStatusMsg.setText("\"".concat(statusMessage).concat("\""));

            if ((this.cardFriend.getUserPrivateProfile().getSex() != null) && (this.cardFriend.getUserPrivateProfile().getSex().equalsIgnoreCase("female")))
                this.friendImage.setImageResource(R.drawable.default_profile_image_female);
            else
                this.friendImage.setImageResource(R.drawable.default_profile_image_male);


            if (this.cardFriend.getUserPrivateProfile().getProfileImage() != null) {
                this.cardFriend.getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "onImageLoaded", EventArgs.class));
                this.cardFriend.getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.medium, 0);
            }

            this.friendImage.setOnClickListener(onImageClickListener);
            this.cardView.setOnClickListener(onCardClickListener);
        }

        public void onUserLoaded(Object sender, EventArgs eventArgs) {
            this.cardFriend.UserLoaded.remove(new EventHandler<EventArgs>(this, "onUserLoaded", EventArgs.class));
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateData();
                }
            });
        }

        public void onImageLoaded(Object sender, EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image) sender;
            final FriendViewHolder thisViewHolder = this;

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.medium, 0);
                    if (is != null) {
                        friendImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder, "onImageLoaded", EventArgs.class));
                    //image.freeMemory();
                }
            });
        }
    }

    private String updateTimeStamp(Date timeStamp) {

        String LastMessageTimestamp = "";
        Date fiveMinutesAgo = new Date((new Date()).getTime() - 5 * 60 * 1000);
        Date today = DateFormatter.toDate(DateFormatter.toString(new Date()));
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(today);
        yesterday.roll(Calendar.DAY_OF_MONTH, false);
        if (timeStamp.after(fiveMinutesAgo))
            LastMessageTimestamp = context.getString(R.string.left_panel_context_imprecise_time_now);
        else if (timeStamp.after(today))
            LastMessageTimestamp = context.getString(R.string.left_panel_context_imprecise_time_yesterday).concat(" ").concat(TimestampFormatter.toLocaleString(timeStamp));
        else if (timeStamp.after(yesterday.getTime()))
            LastMessageTimestamp = context.getString(R.string.left_panel_context_imprecise_time_yesterday).concat(" ").concat(TimestampFormatter.toLocaleString(timeStamp));
        else
            LastMessageTimestamp = DateFormatter.toShortHumanReadableString(timeStamp);

        return LastMessageTimestamp;
    }

    private Integer[] getRandomUsers(int amount, int length) {
        if (amount == 0)
            return null;
        else if (amount <= length) {
            Integer[] randomArray = (Integer[]) (makeRandom(amount, amount)).toArray();
            return randomArray;
        } else {
            Integer[] randomArray = (Integer[]) (makeRandom(amount, length)).toArray();
            return randomArray;
        }
    }

    private Set makeRandom(final int NUMBER_RANGE, final int SET_SIZE_REQUIRED) {
        Set set = new HashSet<Integer>(SET_SIZE_REQUIRED);
        while (set.size() < SET_SIZE_REQUIRED) {
            Random random = new Random();
            while (set.add(random.nextInt(NUMBER_RANGE)) != true)
                ;
        }
        assert set.size() == SET_SIZE_REQUIRED;
        return set;
    }
}
