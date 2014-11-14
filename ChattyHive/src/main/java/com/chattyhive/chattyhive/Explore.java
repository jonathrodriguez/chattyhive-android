package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnTransitionListener;
import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.SlidingStepsLayout;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;

public class Explore extends Activity {

    Controller controller;
    ExploreListAdapter exploreListAdapter_list0;
    ExploreListAdapter exploreListAdapter_list1;
    ExploreListAdapter exploreListAdapter_list2;
    ExploreListAdapter exploreListAdapter_list3;
    int lastOffset;
    int joined = 0;

    SlidingStepsLayout slidingPanel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);
        this.Initialize();
    }

    private void loadListView(int step) {
        ExploreListAdapter listAdapter;
        switch (step) {
            case 0:
                listAdapter = this.exploreListAdapter_list0;
                break;
            case 1:
                listAdapter = this.exploreListAdapter_list1;
                break;
            case 2:
                listAdapter = this.exploreListAdapter_list2;
                break;
            case 3:
                listAdapter = this.exploreListAdapter_list3;
                break;
            default:
                return;
        }

        ListView listView = (ListView)(this.slidingPanel.getViewByStep(step).findViewById(R.id.explore_list_listView));
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.explore_hive_card, listView, false);
        listView.addHeaderView(header);
        listView.setAdapter(listAdapter);
    }

    private void Initialize(){
        this.controller = Controller.GetRunningController();

        this.controller.exploreHives(0,9, Controller.ExploreType.OUTSTANDING);
        this.controller.exploreHives(0,9, Controller.ExploreType.CREATION_DATE);
        this.controller.exploreHives(0,9, Controller.ExploreType.TRENDING);
        this.controller.exploreHives(0,9, Controller.ExploreType.USERS);

        this.slidingPanel = (SlidingStepsLayout)findViewById(R.id.explore_slidingsteps);
        this.lastOffset = 0;

        this.exploreListAdapter_list0 = new ExploreListAdapter(this,this.controller.getExploreHives(Controller.ExploreType.OUTSTANDING));
        this.exploreListAdapter_list1 = new ExploreListAdapter(this,this.controller.getExploreHives(Controller.ExploreType.CREATION_DATE));
        this.exploreListAdapter_list2 = new ExploreListAdapter(this,this.controller.getExploreHives(Controller.ExploreType.TRENDING));
        this.exploreListAdapter_list3 = new ExploreListAdapter(this,this.controller.getExploreHives(Controller.ExploreType.USERS));

        this.controller.ExploreHivesListChange.add(new EventHandler<EventArgs>(exploreListAdapter_list0, "OnAddItem", EventArgs.class));
        this.controller.ExploreHivesListChange.add(new EventHandler<EventArgs>(exploreListAdapter_list1, "OnAddItem", EventArgs.class));
        this.controller.ExploreHivesListChange.add(new EventHandler<EventArgs>(exploreListAdapter_list2, "OnAddItem", EventArgs.class));
        this.controller.ExploreHivesListChange.add(new EventHandler<EventArgs>(exploreListAdapter_list3, "OnAddItem", EventArgs.class));

        this.controller.HiveJoined.add(new EventHandler<EventArgs>(this,"onHiveJoined", EventArgs.class));

        this.findViewById(R.id.explore_action_bar_goBack_button).setOnClickListener(this.backButton);
        this.findViewById(R.id.explore_button_categories).setOnClickListener(this.categoriesButton);
        this.findViewById(R.id.explore_tab_list_favourites_button).setOnClickListener(this.outstanding);
        this.findViewById(R.id.explore_tab_list_location_button).setOnClickListener(this.time);
        this.findViewById(R.id.explore_tab_list_recent_button).setOnClickListener(this.trending);
        this.findViewById(R.id.explore_tab_list_trending_button).setOnClickListener(this.users);

        StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_favourites_button),1f);
        StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_location_button),0.25f);
        StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_recent_button),0.25f);
        StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_trending_button),0.25f);
        StaticMethods.SetAlpha(findViewById(R.id.explore_button_categories),0.25f);

        findViewById(R.id.explore_tab_list_favourites_button).setBackgroundResource(R.drawable.explore_tab_list_border);

        loadListView(0);
        loadListView(1);
    }

    public void GetMoreHives() {
        this.lastOffset += 9;
        this.controller.exploreHives(this.lastOffset,9, Controller.ExploreType.OUTSTANDING);
        //findViewById(R.id.explore_tab_list_favourites_button).setBackgroundResource(R.drawable.explore_tab_list_border);
    }

    protected OnTransitionListener onTransitionListener = new OnTransitionListener() {
        @Override
        public boolean OnBeginTransition(int actualStep, int nextStep) {
            return true;
        }

        @Override
        public void OnEndTransition(int actualStep, int previousStep) {
            if ((actualStep > previousStep) && (actualStep < 3))
                loadListView(actualStep+1);
            else if ((actualStep < previousStep) && (actualStep > 0))
                loadListView(actualStep-1);
        }
    };

    protected View.OnClickListener backButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (joined > 0)
                setResult(RESULT_OK);

            /*Intent data = new Intent();
            data.putExtra("NameURL",hive.getNameURL());
            setResult(RESULT_OK,data);*/

            finish();
        }
    };

    protected View.OnClickListener outstanding = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 0:
            slidingPanel.openStep(0);

            findViewById(R.id.explore_tab_list_favourites_button).setBackgroundResource(R.drawable.explore_tab_list_border);


            findViewById(R.id.explore_tab_list_location_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_recent_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_trending_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_button_categories).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);

            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_favourites_button),1f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_location_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_recent_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_trending_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_button_categories),0.25f);
        }
    };
    protected View.OnClickListener time = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 1:
            slidingPanel.openStep(1);

            findViewById(R.id.explore_tab_list_location_button).setBackgroundResource(R.drawable.explore_tab_list_border);

            findViewById(R.id.explore_tab_list_favourites_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_recent_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_trending_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_button_categories).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);

            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_favourites_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_location_button),1f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_recent_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_trending_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_button_categories),0.25f);
        }
    };
    protected View.OnClickListener trending = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 2:
            slidingPanel.openStep(2);

            findViewById(R.id.explore_tab_list_recent_button).setBackgroundResource(R.drawable.explore_tab_list_border);

            findViewById(R.id.explore_tab_list_location_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_favourites_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_trending_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_button_categories).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);

            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_favourites_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_location_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_recent_button),1f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_trending_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_button_categories),0.25f);
        }
    };
    protected View.OnClickListener users = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 3:
            slidingPanel.openStep(3);

            findViewById(R.id.explore_tab_list_trending_button).setBackgroundResource(R.drawable.explore_tab_list_border);

            findViewById(R.id.explore_tab_list_location_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_favourites_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_recent_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_button_categories).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);

            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_favourites_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_location_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_recent_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_trending_button),1f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_button_categories),0.25f);
        }
    };

    protected View.OnClickListener categoriesButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 4:
            slidingPanel.openStep(4);

            findViewById(R.id.explore_button_categories).setBackgroundResource(R.drawable.explore_tab_list_border);

            findViewById(R.id.explore_tab_list_location_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_favourites_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_recent_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
            findViewById(R.id.explore_tab_list_trending_button).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);

            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_favourites_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_location_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_recent_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_tab_list_trending_button),0.25f);
            StaticMethods.SetAlpha(findViewById(R.id.explore_button_categories),1f);
        }
    };

    public void onHiveJoined(Object sender,EventArgs eventArgs) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (joined == 0) {
                    findViewById(R.id.explore_action_bar_goBack_button).setBackgroundResource(R.drawable.explore_action_bar_hive_joined_border);
                    findViewById(R.id.explore_action_bar_hive_added).setVisibility(View.VISIBLE);
                    ((ImageView)findViewById(R.id.explore_action_bar_goBack_image)).setImageResource(R.drawable.explore_new_hive_back_with_subscriptions);
                }
                joined++;
                ((TextView)findViewById(R.id.explore_action_bar_number_text)).setText(String.valueOf(joined));

            }
        });
    }

    @Override
    protected void onDestroy(){
        this.controller.ExploreHivesListChange.remove(new EventHandler<EventArgs>(exploreListAdapter_list0, "OnAddItem", EventArgs.class));
        this.controller.ExploreHivesListChange.remove(new EventHandler<EventArgs>(exploreListAdapter_list1, "OnAddItem", EventArgs.class));
        this.controller.ExploreHivesListChange.remove(new EventHandler<EventArgs>(exploreListAdapter_list2, "OnAddItem", EventArgs.class));
        this.controller.ExploreHivesListChange.remove(new EventHandler<EventArgs>(exploreListAdapter_list3, "OnAddItem", EventArgs.class));
        this.controller.HiveJoined.remove(new EventHandler<EventArgs>(this, "onHiveJoined", EventArgs.class));

        exploreListAdapter_list0 = null;
        exploreListAdapter_list1 = null;
        exploreListAdapter_list2 = null;
        exploreListAdapter_list3 = null;
        controller = null;
        super.onDestroy();
    }
}
