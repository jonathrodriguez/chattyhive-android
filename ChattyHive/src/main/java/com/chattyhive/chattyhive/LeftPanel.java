package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
//import android.util.Log;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;
import com.chattyhive.chattyhive.framework.Util.ViewPair;

/**
 * Created by Jonathan on 7/03/14.
 */
public class LeftPanel {
    Context context;

    LeftPanelListAdapter leftPanelListAdapter;
    TextView emptyMessage;

    LinearLayout chats;
    LinearLayout hives;
    LinearLayout friends;

    ViewSwitcher view_switcher;

    Boolean showingEmpty;

    public LeftPanel(Context activity) {
        this.context = activity;

        this.InitializeComponent();
    }

    private void InitializeComponent() {
        // Here we set the components to their respective elements.
        chats = (LinearLayout)((Activity)this.context).findViewById(R.id.left_panel_action_bar_tab_chats);
        hives = (LinearLayout)((Activity)this.context).findViewById(R.id.left_panel_action_bar_tab_hives);
        friends = (LinearLayout)((Activity)this.context).findViewById(R.id.left_panel_action_bar_tab_friends);

        view_switcher = (ViewSwitcher)((Activity)this.context).findViewById(R.id.left_panel_empty_list_view_switcher);

        showingEmpty = false;
        emptyMessage = (TextView)((Activity)this.context).findViewById(R.id.left_panel_empty_list_message);

        SetButtonSelected(chats,true, (TextView)chats.findViewById(R.id.left_panel_action_bar_tab_text_chats), (ImageView)chats.findViewById(R.id.left_panel_action_bar_tab_img_chats),R.drawable.pestanhas_panel_izquierdo_chats);
        SetButtonSelected(hives,false, (TextView)hives.findViewById(R.id.left_panel_action_bar_tab_text_hives), (ImageView)hives.findViewById(R.id.left_panel_action_bar_tab_img_hives),R.drawable.pestanhas_panel_izquierdo_hives_blanco);
        SetButtonSelected(friends,false, (TextView)friends.findViewById(R.id.left_panel_action_bar_tab_text_friends), (ImageView)friends.findViewById(R.id.left_panel_action_bar_tab_img_friends),R.drawable.pestanhas_panel_izquierdo_users_blanco);

        chats.setOnClickListener(left_panel_tab_button_click);
        hives.setOnClickListener(left_panel_tab_button_click);
        friends.setOnClickListener(left_panel_tab_button_click);

        this.leftPanelListAdapter = new LeftPanelListAdapter(this.context);
        ((ListView)((Activity)this.context).findViewById(R.id.left_panel_element_list)).setAdapter(this.leftPanelListAdapter);

        Hive.HiveListChanged.add(new EventHandler<EventArgs>(leftPanelListAdapter, "OnAddItem", EventArgs.class));
        Group.GroupListChanged.add(new EventHandler<EventArgs>(leftPanelListAdapter, "OnAddItem", EventArgs.class));

        this.leftPanelListAdapter.ListSizeChanged.add(new EventHandler<EventArgs>(this,"OnListSizeChanged",EventArgs.class));

        this.leftPanelListAdapter.SetOnClickListener(OpenChat);

        this.leftPanelListAdapter.SetVisibleList(context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats));
        emptyMessage.setText(R.string.left_panel_chats_empty_list);
/*        if (this.leftPanelListAdapter.getCount() == 0) {
            view_switcher.showNext();
            showingEmpty = true;
        }*/
  }

    public void OnListSizeChanged(Object sender, EventArgs eventArgs) {
        int count = leftPanelListAdapter.getCount();
        if ((showingEmpty) && (count > 0)) {
            showingEmpty = false;
            view_switcher.showPrevious();
        } else if ((!showingEmpty) && (count == 0)) {
            showingEmpty = true;
            view_switcher.showNext();
        }
        if ((leftPanelListAdapter.GetVisibleList() == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats))&& (!showingEmpty))
            ((Activity)context).findViewById(R.id.left_panel_list_filter).setVisibility(View.VISIBLE);
        else
            ((Activity)context).findViewById(R.id.left_panel_list_filter).setVisibility(View.GONE);

        if (!showingEmpty) {
            ((Activity)context).findViewById(R.id.left_panel_element_list).setVisibility(View.VISIBLE);
        }
    }

    protected void OpenChats() {
        chats.performClick();
    }

    protected void OpenHives() {
        hives.performClick();
    }

    protected View.OnClickListener left_panel_tab_button_click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int count = 0;
            switch (v.getId()) {
                case R.id.left_panel_action_bar_tab_chats:
                    //Log.w("LeftPanel_TabClicked","Opening chats.");
                    SetButtonSelected(chats, true, (TextView) chats.findViewById(R.id.left_panel_action_bar_tab_text_chats), (ImageView) chats.findViewById(R.id.left_panel_action_bar_tab_img_chats), R.drawable.pestanhas_panel_izquierdo_chats);
                    SetButtonSelected(hives,false, (TextView)hives.findViewById(R.id.left_panel_action_bar_tab_text_hives), (ImageView)hives.findViewById(R.id.left_panel_action_bar_tab_img_hives),R.drawable.pestanhas_panel_izquierdo_hives_blanco);
                    SetButtonSelected(friends,false, (TextView)friends.findViewById(R.id.left_panel_action_bar_tab_text_friends), (ImageView)friends.findViewById(R.id.left_panel_action_bar_tab_img_friends),R.drawable.pestanhas_panel_izquierdo_users_blanco);
                    leftPanelListAdapter.SetVisibleList(context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats));
                    emptyMessage.setText(R.string.left_panel_chats_empty_list);
                   // Log.w("LeftPanel_TabClicked",String.format("Opening chats. %d items in list.",leftPanelListAdapter.getCount()));
                    break;
                case R.id.left_panel_action_bar_tab_hives:
                    //Log.w("LeftPanel_TabClicked","Opening hives.");
                    SetButtonSelected(chats,false, (TextView)chats.findViewById(R.id.left_panel_action_bar_tab_text_chats), (ImageView)chats.findViewById(R.id.left_panel_action_bar_tab_img_chats),R.drawable.pestanhas_panel_izquierdo_chats_blanco);
                    SetButtonSelected(hives,true, (TextView)hives.findViewById(R.id.left_panel_action_bar_tab_text_hives), (ImageView)hives.findViewById(R.id.left_panel_action_bar_tab_img_hives),R.drawable.pestanhas_panel_izquierdo_hives);
                    SetButtonSelected(friends,false, (TextView)friends.findViewById(R.id.left_panel_action_bar_tab_text_friends), (ImageView)friends.findViewById(R.id.left_panel_action_bar_tab_img_friends),R.drawable.pestanhas_panel_izquierdo_users_blanco);
                    leftPanelListAdapter.SetVisibleList(context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives));
                    emptyMessage.setText(R.string.left_panel_hives_empty_list);
                    break;
                case R.id.left_panel_action_bar_tab_friends:
                    //Log.w("LeftPanel_TabClicked","Opening friends.");
                    SetButtonSelected(chats,false, (TextView)chats.findViewById(R.id.left_panel_action_bar_tab_text_chats), (ImageView)chats.findViewById(R.id.left_panel_action_bar_tab_img_chats),R.drawable.pestanhas_panel_izquierdo_chats_blanco);
                    SetButtonSelected(hives,false, (TextView)hives.findViewById(R.id.left_panel_action_bar_tab_text_hives), (ImageView)hives.findViewById(R.id.left_panel_action_bar_tab_img_hives),R.drawable.pestanhas_panel_izquierdo_hives_blanco);
                    SetButtonSelected(friends,true, (TextView)friends.findViewById(R.id.left_panel_action_bar_tab_text_friends), (ImageView)friends.findViewById(R.id.left_panel_action_bar_tab_img_friends),R.drawable.pestanhas_panel_izquierdo_users);
                    leftPanelListAdapter.SetVisibleList(context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates));
                    emptyMessage.setText(R.string.left_panel_friends_empty_list);
                    break;
            }
            count = leftPanelListAdapter.getCount();
            if ((showingEmpty) && (count > 0)) {
                showingEmpty = false;
                view_switcher.showPrevious();
            } else if ((!showingEmpty) && (count == 0)) {
                showingEmpty = true;
                view_switcher.showNext();
            }

            if ((leftPanelListAdapter.GetVisibleList() == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats))&& (!showingEmpty))
                ((Activity)context).findViewById(R.id.left_panel_list_filter).setVisibility(View.VISIBLE);
            else
                ((Activity)context).findViewById(R.id.left_panel_list_filter).setVisibility(View.GONE);

            if (!showingEmpty) {
                ((Activity)context).findViewById(R.id.left_panel_element_list).setVisibility(View.VISIBLE);
            }
        }
    };

    private void SetButtonSelected(LinearLayout layout, Boolean selected,TextView textView, ImageView image, int drawable) {
        layout.setSelected(selected);
        TypedValue alpha = new TypedValue();
        if (selected) {
            textView.setTextColor(layout.getContext().getResources().getColor(R.color.left_panel_action_bar_selected_button_text));
            textView.setVisibility(View.VISIBLE);
            layout.getContext().getResources().getValue(R.color.left_panel_action_bar_selected_button_alpha, alpha, true);
        } else {
            textView.setTextColor(layout.getContext().getResources().getColor(R.color.left_panel_action_bar_unselected_button_text));
            textView.setVisibility(View.GONE);
            layout.getContext().getResources().getValue(R.color.left_panel_action_bar_unselected_button_alpha, alpha, true);
        }

        image.setImageResource(drawable);
        StaticMethods.SetAlpha(image, alpha.getFloat());
    }

    protected View.OnClickListener OpenChat = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View chatView;
            View actionBarView;
            MainChat mainChat;

            ViewPair viewPair = ((Main)context).ShowLayout(R.layout.main_panel_chat_layout,R.layout.chat_action_bar);

            actionBarView = viewPair.getActionBarView();
            chatView = viewPair.getMainView();
            int visibleList = leftPanelListAdapter.GetVisibleList();

            if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {                                                                                                                 //SI ES UN HIVE
                /*if (((Main)context).ActiveLayoutID == R.layout.main_panel_chat_layout) {
                        ((Main)context).controller.Leave((String)((Activity)context).findViewById(R.id.main_panel_chat_name).getTag());
                    }*/

                Hive h = ((Hive)v.getTag(R.id.BO_Hive));     //investigar

                ((TextView)actionBarView.findViewById(R.id.main_panel_chat_name)).setText(h.getName());//nombre del chat
                actionBarView.findViewById(R.id.main_panel_chat_name).setTag(h.getPublicChat().getChannelUnicode());

                actionBarView.findViewById(R.id.main_panel_chat_menu_icon).setOnClickListener(((Main)context).menuIcon_ClickListener);//imagen
                actionBarView.findViewById(R.id.main_panel_chat_icon).setOnClickListener(((Main)context).appIcon_ClickListener);
                ((Main)context).appIcon_ClickListener.onClick(actionBarView.findViewById(R.id.main_panel_chat_icon));

                mainChat = new MainChat(context,h.getPublicChat().getChannelUnicode());//crea el chat
                chatView.findViewById(R.id.main_panel_chat_send_icon).setOnClickListener(mainChat.send_button_click);//setListener enviar mensaje
            } else if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {                                                                                                          //SI ES UN CHAT
                Group g = ((Group)v.getTag(R.id.BO_Chat));

                ((TextView)actionBarView.findViewById(R.id.main_panel_chat_name)).setText(g.getName());
                actionBarView.findViewById(R.id.main_panel_chat_name).setTag(g.getChannelUnicode());

                actionBarView.findViewById(R.id.main_panel_chat_menu_icon).setOnClickListener(((Main)context).menuIcon_ClickListener);
                actionBarView.findViewById(R.id.main_panel_chat_icon).setOnClickListener(((Main)context).appIcon_ClickListener);
                ((Main)context).appIcon_ClickListener.onClick(actionBarView.findViewById(R.id.main_panel_chat_icon));

                mainChat = new MainChat(context,g.getChannelUnicode());
                chatView.findViewById(R.id.main_panel_chat_send_icon).setOnClickListener(mainChat.send_button_click);
            } /*else if (visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {

            }*/
        }
    };
}
