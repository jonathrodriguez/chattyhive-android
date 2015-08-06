package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
//import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnRemoveLayoutListener;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnTransitionListener;
import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.SlidingStepsLayout;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;

/**
 * Created by Jonathan on 7/03/14.
 */
public class LeftPanel {
    Context context;

    int activeStep;

    SlidingStepsLayout leftPanelSlidingSteps;
    LeftPanelListAdapter[] leftPanelListAdapter;
    TextView active_emptyMessage;

    LinearLayout chats;
    LinearLayout hives;
    LinearLayout friends;

    ViewSwitcher active_view_switcher;

    Boolean[] showingEmpty;

    public LeftPanel(Context activity) {
        this.context = activity;
        this.InitializeComponent();
    }

    private OnTransitionListener transitionListener = new OnTransitionListener() {
        @Override
        public boolean OnBeginTransition(int actualStep, int nextStep) {

            ((ListView)leftPanelSlidingSteps.getViewByStep(nextStep).findViewById(R.id.left_panel_element_list)).setAdapter(leftPanelListAdapter[nextStep]);
            ((TextView)leftPanelSlidingSteps.getViewByStep(nextStep).findViewById(R.id.left_panel_empty_list_message)).setText(getEmptyMessage(nextStep));

            int count = leftPanelListAdapter[nextStep].getCount();
            if ((showingEmpty[nextStep]) && (count > 0)) {
                showingEmpty[nextStep] = false;
                ((ViewSwitcher)leftPanelSlidingSteps.getViewByStep(nextStep).findViewById(R.id.left_panel_empty_list_view_switcher)).showPrevious();
            } else if ((!showingEmpty[nextStep]) && (count == 0)) {
                showingEmpty[nextStep] = true;
                ((ViewSwitcher)leftPanelSlidingSteps.getViewByStep(nextStep).findViewById(R.id.left_panel_empty_list_view_switcher)).showNext();
            }

            if ((nextStep == 0) && (!showingEmpty[nextStep]))
                leftPanelSlidingSteps.getViewByStep(nextStep).findViewById(R.id.left_panel_list_filter).setVisibility(View.VISIBLE);
            else
                leftPanelSlidingSteps.getViewByStep(nextStep).findViewById(R.id.left_panel_list_filter).setVisibility(View.GONE);

            if (!showingEmpty[nextStep]) {
                leftPanelSlidingSteps.getViewByStep(nextStep).findViewById(R.id.left_panel_element_list).setVisibility(View.VISIBLE);
            }

            return true;
        }

        @Override
        public void OnDuringTransition(int[] visibleSteps, float[] visibilityAmount) {

        }

        @Override
        public void OnEndTransition(int actualStep, int previousStep) {
            activeStep = actualStep;
            switch (activeStep) {
                case 0:
                    SetButtonSelected(chats, true, (TextView) chats.findViewById(R.id.left_panel_action_bar_tab_text_chats), (ImageView) chats.findViewById(R.id.left_panel_action_bar_tab_img_chats), R.drawable.pestanhas_panel_izquierdo_chats);
                    SetButtonSelected(hives,false, (TextView)hives.findViewById(R.id.left_panel_action_bar_tab_text_hives), (ImageView)hives.findViewById(R.id.left_panel_action_bar_tab_img_hives),R.drawable.pestanhas_panel_izquierdo_hives_blanco);
                    SetButtonSelected(friends,false, (TextView)friends.findViewById(R.id.left_panel_action_bar_tab_text_friends), (ImageView)friends.findViewById(R.id.left_panel_action_bar_tab_img_friends),R.drawable.pestanhas_panel_izquierdo_users_blanco);
                    break;
                case 1:
                    SetButtonSelected(chats,false, (TextView)chats.findViewById(R.id.left_panel_action_bar_tab_text_chats), (ImageView)chats.findViewById(R.id.left_panel_action_bar_tab_img_chats),R.drawable.pestanhas_panel_izquierdo_chats_blanco);
                    SetButtonSelected(hives,true, (TextView)hives.findViewById(R.id.left_panel_action_bar_tab_text_hives), (ImageView)hives.findViewById(R.id.left_panel_action_bar_tab_img_hives),R.drawable.pestanhas_panel_izquierdo_hives);
                    SetButtonSelected(friends,false, (TextView)friends.findViewById(R.id.left_panel_action_bar_tab_text_friends), (ImageView)friends.findViewById(R.id.left_panel_action_bar_tab_img_friends),R.drawable.pestanhas_panel_izquierdo_users_blanco);
                    break;
                case 2:
                    SetButtonSelected(chats,false, (TextView)chats.findViewById(R.id.left_panel_action_bar_tab_text_chats), (ImageView)chats.findViewById(R.id.left_panel_action_bar_tab_img_chats),R.drawable.pestanhas_panel_izquierdo_chats_blanco);
                    SetButtonSelected(hives,false, (TextView)hives.findViewById(R.id.left_panel_action_bar_tab_text_hives), (ImageView)hives.findViewById(R.id.left_panel_action_bar_tab_img_hives),R.drawable.pestanhas_panel_izquierdo_hives_blanco);
                    SetButtonSelected(friends,true, (TextView)friends.findViewById(R.id.left_panel_action_bar_tab_text_friends), (ImageView)friends.findViewById(R.id.left_panel_action_bar_tab_img_friends),R.drawable.pestanhas_panel_izquierdo_users);
                    break;
            }
        }
    };

    private OnRemoveLayoutListener removeLayoutListener = new OnRemoveLayoutListener() {
        @Override
        public void OnRemove(View view, int stepNumber) {
            if ((stepNumber < showingEmpty.length) && (stepNumber >= 0))
            showingEmpty[stepNumber] = false;
        }
    };

    private void InitializeComponent() {
        // Here we set the components to their respective elements.
        this.leftPanelSlidingSteps = (SlidingStepsLayout)((Activity)this.context).findViewById(R.id.left_panel_sliding_steps);
        chats = (LinearLayout)((Activity)this.context).findViewById(R.id.left_panel_action_bar_tab_chats);
        hives = (LinearLayout)((Activity)this.context).findViewById(R.id.left_panel_action_bar_tab_hives);
        friends = (LinearLayout)((Activity)this.context).findViewById(R.id.left_panel_action_bar_tab_friends);

        activeStep = 0;
        showingEmpty = new Boolean[] {false,false,false};

        active_view_switcher = (ViewSwitcher) this.leftPanelSlidingSteps.getViewByStep(this.activeStep).findViewById(R.id.left_panel_empty_list_view_switcher);
        active_emptyMessage = (TextView)this.leftPanelSlidingSteps.getViewByStep(this.activeStep).findViewById(R.id.left_panel_empty_list_message);

        this.leftPanelSlidingSteps.setOnTransitionListener(this.transitionListener);
        this.leftPanelSlidingSteps.setOnRemoveLayoutListener(this.removeLayoutListener);

        SetButtonSelected(chats,true, (TextView)chats.findViewById(R.id.left_panel_action_bar_tab_text_chats), (ImageView)chats.findViewById(R.id.left_panel_action_bar_tab_img_chats),R.drawable.pestanhas_panel_izquierdo_chats);
        SetButtonSelected(hives,false, (TextView)hives.findViewById(R.id.left_panel_action_bar_tab_text_hives), (ImageView)hives.findViewById(R.id.left_panel_action_bar_tab_img_hives),R.drawable.pestanhas_panel_izquierdo_hives_blanco);
        SetButtonSelected(friends, false, (TextView) friends.findViewById(R.id.left_panel_action_bar_tab_text_friends), (ImageView) friends.findViewById(R.id.left_panel_action_bar_tab_img_friends), R.drawable.pestanhas_panel_izquierdo_users_blanco);

        chats.setOnClickListener(left_panel_tab_button_click);
        hives.setOnClickListener(left_panel_tab_button_click);
        friends.setOnClickListener(left_panel_tab_button_click);

        TypedValue alpha = new TypedValue();
        this.context.getResources().getValue(R.color.left_panel_list_filter_image_alpha, alpha, true);
        StaticMethods.SetAlpha(this.leftPanelSlidingSteps.getViewByStep(this.activeStep).findViewById(R.id.left_panel_list_filter_help), alpha.getFloat());
        this.context.getResources().getValue(R.color.left_panel_action_bar_search_button_alpha, alpha, true);
        StaticMethods.SetAlpha(((Activity) this.context).findViewById(R.id.left_panel_search_button), alpha.getFloat());

        this.leftPanelListAdapter = new LeftPanelListAdapter[3];
        this.leftPanelListAdapter[0] = new LeftPanelListAdapter(this.context);
        this.leftPanelListAdapter[1] = new LeftPanelListAdapter(this.context);
        this.leftPanelListAdapter[2] = new LeftPanelListAdapter(this.context);

        this.leftPanelListAdapter[0].SetVisibleList(context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats));
        this.leftPanelListAdapter[1].SetVisibleList(context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives));
        this.leftPanelListAdapter[2].SetVisibleList(context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates));

        ((ListView)this.leftPanelSlidingSteps.getViewByStep(this.activeStep).findViewById(R.id.left_panel_element_list)).setAdapter(this.leftPanelListAdapter[this.activeStep]);

        Hive.HiveListChanged.add(new EventHandler<EventArgs>(leftPanelListAdapter[1], "OnAddItem", EventArgs.class));
        Chat.ChatListChanged.add(new EventHandler<EventArgs>(leftPanelListAdapter[0], "OnAddItem", EventArgs.class));

        this.leftPanelListAdapter[0].ListSizeChanged.add(new EventHandler<EventArgs>(this, "OnListSizeChanged", EventArgs.class));
        this.leftPanelListAdapter[1].ListSizeChanged.add(new EventHandler<EventArgs>(this, "OnListSizeChanged", EventArgs.class));
        this.leftPanelListAdapter[2].ListSizeChanged.add(new EventHandler<EventArgs>(this, "OnListSizeChanged", EventArgs.class));

        this.leftPanelListAdapter[0].SetOnClickListener(chatClick);
        this.leftPanelListAdapter[1].SetOnClickListener(hiveClick);

        active_emptyMessage.setText(getEmptyMessage(this.activeStep));
  }

    protected int getEmptyMessage(int list) {
        if (list == 0)
            return R.string.left_panel_chats_empty_list;
        else if (list == 1)
            return R.string.left_panel_hives_empty_list;
        else if (list == 2)
            return R.string.left_panel_friends_empty_list;
        else
            return 0;
    }
    public void OnListSizeChanged(Object sender, EventArgs eventArgs) {
        int count = leftPanelListAdapter[this.activeStep].getCount();
        if ((showingEmpty[this.activeStep]) && (count > 0)) {
            showingEmpty[this.activeStep] = false;
            active_view_switcher.showPrevious();
        } else if ((!showingEmpty[this.activeStep]) && (count == 0)) {
            showingEmpty[this.activeStep] = true;
            active_view_switcher.showNext();
        }
        if ((this.activeStep == 0) && (!showingEmpty[this.activeStep]))
            leftPanelSlidingSteps.getViewByStep(this.activeStep).findViewById(R.id.left_panel_list_filter).setVisibility(View.VISIBLE);
        else
            leftPanelSlidingSteps.getViewByStep(this.activeStep).findViewById(R.id.left_panel_list_filter).setVisibility(View.GONE);

        if (!showingEmpty[this.activeStep]) {
            leftPanelSlidingSteps.getViewByStep(this.activeStep).findViewById(R.id.left_panel_element_list).setVisibility(View.VISIBLE);
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

        switch (v.getId()) {
            case R.id.left_panel_action_bar_tab_chats:
                leftPanelSlidingSteps.openStep(0);
                break;
            case R.id.left_panel_action_bar_tab_hives:
                leftPanelSlidingSteps.openStep(1);
                break;
            case R.id.left_panel_action_bar_tab_friends:
                leftPanelSlidingSteps.openStep(2);
                break;
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

    protected View.OnClickListener chatClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Chat chatChat = ((Chat)v.getTag(R.id.BO_Chat));

            if (chatChat != null) {
                ((Main)context).OpenWindow(new MainChat(context, chatChat));
            }
        }
    };

    protected View.OnClickListener hiveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Chat chatChat = ((Hive)v.getTag(R.id.BO_Hive)).getPublicChat();

            if (chatChat != null) {
                ((Main)context).OpenWindow(new MainChat(context, chatChat));
            }
        }
    };

}
