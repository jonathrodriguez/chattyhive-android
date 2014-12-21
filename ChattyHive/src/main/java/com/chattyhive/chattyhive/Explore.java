package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.framework.CustomViews.Listener.OnTransitionListener;
import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.SlidingStepsLayout;
import com.chattyhive.chattyhive.framework.Util.StaticMethods;
import com.chattyhive.chattyhive.util.Category;

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

    ExploreCategoriesListAdapter exploreCategoriesListAdapter;
    ExploreListAdapter exploreFilteredListAdapter;

    HashMap<String,Boolean> joined_hives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);
        this.Initialize();
    }

    private void loadListView(int step) {
        if (step < 4) {
            if (!exploreListAdapter.containsKey(step))
                exploreListAdapter.put(step, new ExploreListAdapter(this, this.sortTypes[step],null, getString(exploreListHeaders[step]), joined_hives, expandedHiveDescriptionButtonClickListener));

            ExploreListAdapter listAdapter = exploreListAdapter.get(step);
            ListView listView = (ListView) (this.slidingPanel.getViewByStep(step).findViewById(R.id.explore_list_listView));
            listAdapter.setListView(listView);
        } else if (step == 4) {
            if (exploreCategoriesListAdapter == null)
                exploreCategoriesListAdapter = new ExploreCategoriesListAdapter(this,categoryClickListener);

            GridView gridView = (GridView)(this.slidingPanel.getViewByStep(step).findViewById(R.id.explore_categories_list_gridView));
            exploreCategoriesListAdapter.setGridView(gridView);
        }
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
            if (nextStep < 5)
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

            if ((previousStep == 4) && (actualStep != 4))
                headerBackButton.onClick(null);

            if (activeList < 4)
                exploreListAdapter.get(activeList).setActive(false);

            activeList = actualStep;

            if (activeList < 4)
                exploreListAdapter.get(activeList).setActive(true);
        }
    };

    protected void setTabStatus (int[] visibleSteps, float[] visibilityAmount) {
        for (int i = 0; i < this.tabButtonIDs.length; i++) {
            int index = IndexOfInt(i,visibleSteps);
            if (index > -1) {
                findViewById(this.tabButtonIDs[i]).setBackgroundResource(R.drawable.explore_tab_list_border);
                float alpha = ((1f - 0.75f)*visibilityAmount[index]) + 0.75f;
                StaticMethods.SetAlpha(findViewById(this.tabButtonIDs[i]),alpha);
            } else {
                findViewById(this.tabButtonIDs[i]).setBackgroundResource(R.drawable.explore_tab_list_no_selected_border);
                StaticMethods.SetAlpha(findViewById(this.tabButtonIDs[i]),0.75f);
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
                StaticMethods.SetAlpha(findViewById(this.tabButtonIDs[i]),0.75f);
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
                if (activeList < 4)
                    exploreListAdapter.get(activeList).notifyDataSetChanged();
                else if (activeList == 4)
                    exploreFilteredListAdapter.notifyDataSetChanged();
            } else if (joined_hives.get(hive.getNameUrl())) {
                Intent data = new Intent();
                data.putExtra("NameURL", hive.getNameUrl());
                setResult(RESULT_OK, data);
                finish();
            }
        }
    };

    protected View.OnClickListener categoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Category category = (Category)v.getTag(R.id.BO_Category);

            if (category != null) {
                exploreFilteredListAdapter = new ExploreListAdapter(exploreCategoriesListAdapter.getContext(), sortTypes[1],category.getGroupCode(), getString(category.getCategoryNameResID()), joined_hives, expandedHiveDescriptionButtonClickListener);
                ListView listView = (ListView) (slidingPanel.getViewByStep(4).findViewById(R.id.explore_list_listView));
                exploreFilteredListAdapter.setListView(listView);
                exploreFilteredListAdapter.setHeaderBackButtonClickListener(headerBackButton);
                exploreFilteredListAdapter.setActive(true);
                ((ViewSwitcher)slidingPanel.getViewByStep(4).findViewById(R.id.explore_categories_view_switcher)).showNext();
            }
        }
    };

    protected View.OnClickListener headerBackButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (exploreFilteredListAdapter != null) {
                exploreFilteredListAdapter = null;
                ((ViewSwitcher)slidingPanel.getViewByStep(4).findViewById(R.id.explore_categories_view_switcher)).showPrevious();
                ((ListView)slidingPanel.getViewByStep(4).findViewById(R.id.explore_list_listView)).setAdapter(null);
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
            headerBackButton.onClick(v);
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getRepeatCount() == 0)) {
                if ((activeList == 4) && (exploreFilteredListAdapter != null)) { // Tell the framework to start tracking this event.
                    return true;
                }
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                if (!event.isCanceled() && (exploreFilteredListAdapter != null)) {
                    headerBackButton.onClick(null);
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

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
                    if (activeList < 4)
                        exploreListAdapter.get(activeList).syncNotifyDataSetChanged();
                    else if (activeList == 4)
                        exploreFilteredListAdapter.syncNotifyDataSetChanged();
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
