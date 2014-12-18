package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnTransitionListener;
import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.SlidingStepsLayout;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;

import java.util.HashMap;

public class Explore extends Activity {

    Controller controller;

    int[] exploreListHeaders;
    int[] tabButtonIDs;
    com.chattyhive.backend.businessobjects.Explore.SortType[] sortTypes;

    int activeList;
    int joined;

    SlidingStepsLayout slidingPanel;
    HashMap<Integer,ExploreListAdapter> exploreListAdapter;

    HashMap<String,Boolean> joined_hives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);
        this.Initialize();
    }

    private void loadListView(int step) {
        if (!exploreListAdapter.containsKey(step))
            exploreListAdapter.put(step,new ExploreListAdapter(this,this.sortTypes[step],getString(exploreListHeaders[step]),joined_hives,expandedHiveDescriptionButtonClickListener));

        ExploreListAdapter listAdapter = exploreListAdapter.get(step);
        ListView listView = (ListView)(this.slidingPanel.getViewByStep(step).findViewById(R.id.explore_list_listView));
        listAdapter.setListView(listView);
    }

    private void Initialize(){
        this.exploreListAdapter = new HashMap<Integer,ExploreListAdapter>();
        this.joined_hives = new HashMap<String,Boolean>();
        this.joined = 0;
        this.activeList = 0;

        this.exploreListHeaders = new int[] { R.string.explore_outstanding_hives, R.string.explore_hives_by_date, R.string.explore_trending_hives, R.string.explore_hives_by_users };
        this.tabButtonIDs = new int[] {R.id.explore_tab_list_favourites_button,R.id.explore_tab_list_location_button,R.id.explore_tab_list_recent_button,R.id.explore_tab_list_trending_button,R.id.explore_button_categories };
        this.sortTypes = new com.chattyhive.backend.businessobjects.Explore.SortType[] {com.chattyhive.backend.businessobjects.Explore.SortType.OUTSTANDING, com.chattyhive.backend.businessobjects.Explore.SortType.CREATION_DATE, com.chattyhive.backend.businessobjects.Explore.SortType.TRENDING, com.chattyhive.backend.businessobjects.Explore.SortType.USERS };
        this.controller = Controller.GetRunningController();

        this.slidingPanel = (SlidingStepsLayout)findViewById(R.id.explore_slidingsteps);
        this.slidingPanel.setOnTransitionListener(onTransitionListener);

        this.controller.HiveJoined.add(new EventHandler<EventArgs>(this,"onHiveJoined", EventArgs.class));

        this.findViewById(R.id.explore_action_bar_goBack_button).setOnClickListener(this.backButton);
        this.findViewById(R.id.explore_button_categories).setOnClickListener(this.categoriesButton);
        this.findViewById(R.id.explore_tab_list_favourites_button).setOnClickListener(this.outstanding);
        this.findViewById(R.id.explore_tab_list_location_button).setOnClickListener(this.time);
        this.findViewById(R.id.explore_tab_list_recent_button).setOnClickListener(this.trending);
        this.findViewById(R.id.explore_tab_list_trending_button).setOnClickListener(this.users);

        setTabStatus(0);

        loadListView(0);
        loadListView(1);

        exploreListAdapter.get(0).setActive(true);
    }

    protected OnTransitionListener onTransitionListener = new OnTransitionListener() {
        @Override
        public boolean OnBeginTransition(int actualStep, int nextStep) {
            if (nextStep < 4)
                loadListView(nextStep);

            return true;
        }

        @Override
        public void OnDuringTransition(int[] visibleSteps, float[] visibilityAmount) {
            //setTabStatus(visibleSteps,visibilityAmount);
        }

        @Override
        public void OnEndTransition(int actualStep, int previousStep) {
            setTabStatus(actualStep);

            if (activeList < 4)
                exploreListAdapter.get(activeList).setActive(false);

            activeList = actualStep;

            if (activeList < 4)
                exploreListAdapter.get(activeList).setActive(true);

           /* if ((actualStep > previousStep) && (actualStep < 3))
                loadListView(actualStep+1);
            else if ((actualStep < previousStep) && (actualStep > 0))
                loadListView(actualStep-1);*/
        }
    };

    protected void setTabStatus (int[] visibleSteps, float[] visibilityAmount) {
        for (int i = 0; i < this.tabButtonIDs.length; i++) {
            int index = IndexOfInt(i,visibleSteps);
            if (index > -1) {
                findViewById(this.tabButtonIDs[i]).setBackgroundResource(R.drawable.explore_tab_list_border);
                float alpha = ((1f - 0.25f)*visibilityAmount[index]) + 0.25f;
                StaticMethods.SetAlpha(findViewById(this.tabButtonIDs[i]),alpha);
            } else {
                findViewById(this.tabButtonIDs[i]).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
                StaticMethods.SetAlpha(findViewById(this.tabButtonIDs[i]),0.25f);
            }
        }
    }
    protected int IndexOfInt(int value, int[] array) {
        for (int i = 0; i < array.length; i++)
            if (value == array[i]) return i;
        return -1;
    }
    protected void setTabStatus (int step) {
        for (int i = 0; i < this.tabButtonIDs.length; i++) {
            if (i == step) {
                findViewById(this.tabButtonIDs[i]).setBackgroundResource(R.drawable.explore_tab_list_border);
                StaticMethods.SetAlpha(findViewById(this.tabButtonIDs[i]),1f);
            } else {
                findViewById(this.tabButtonIDs[i]).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
                StaticMethods.SetAlpha(findViewById(this.tabButtonIDs[i]),0.25f);
            }
        }
    }

    protected View.OnClickListener backButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (joined > 0)
                setResult(RESULT_OK);
            finish();
        }
    };

    protected View.OnClickListener expandedHiveDescriptionButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Hive hive = (Hive)v.getTag(R.id.BO_Hive);

            if (!joined_hives.containsKey(hive.getNameUrl())) {
                controller.JoinHive(hive);
                joined_hives.put(hive.getNameUrl(),false);
                exploreListAdapter.get(activeList).notifyDataSetChanged();
            } else if (joined_hives.get(hive.getNameUrl())) {
                Intent data = new Intent();
                data.putExtra("NameURL", hive.getNameUrl());
                setResult(RESULT_OK, data);
                finish();
            }
        }
    };

    protected View.OnClickListener outstanding = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 0:
            slidingPanel.openStep(0);
        }
    };
    protected View.OnClickListener time = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 1:
            slidingPanel.openStep(1);
        }
    };
    protected View.OnClickListener trending = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 2:
            slidingPanel.openStep(2);
        }
    };
    protected View.OnClickListener users = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 3:
            slidingPanel.openStep(3);
        }
    };

    protected View.OnClickListener categoriesButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GOTO STEP 4:
            slidingPanel.openStep(4);
        }
    };

    public void onHiveJoined(final Object sender,EventArgs eventArgs) {
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

                if (sender instanceof Hive) {
                    joined_hives.put(((Hive) sender).getNameUrl(),true);
                    exploreListAdapter.get(activeList).syncNotifyDataSetChanged();
                }

            }
        });
    }

    @Override
    protected void onDestroy(){
        this.controller.HiveJoined.remove(new EventHandler<EventArgs>(this, "onHiveJoined", EventArgs.class));

        exploreListAdapter.clear();
        controller = null;
        super.onDestroy();
    }

    @Override
    public void onLowMemory () {

    }

    @Override
    public void onTrimMemory (int level) {
        if (level >= TRIM_MEMORY_COMPLETE) {
            this.onLowMemory();
        } else {
            if (level >= TRIM_MEMORY_UI_HIDDEN) {

            } else if (level >= TRIM_MEMORY_BACKGROUND) {

            } else if (level >= TRIM_MEMORY_MODERATE) {

            }
        }
    }
}
