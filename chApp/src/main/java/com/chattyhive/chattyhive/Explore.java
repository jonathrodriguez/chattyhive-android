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

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

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

    TreeMap<Date,Integer> LRU_date;
    TreeMap<Integer,Date> LRU_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);
        this.Initialize();
        if (savedInstanceState != null)
            this.Restore(savedInstanceState);
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
        Log.w("Explore","Initialize()");

        this.LRU_date = new TreeMap<Date, Integer>();
        this.LRU_step = new TreeMap<Integer, Date>();

        this.exploreListAdapter = new HashMap<Integer,ExploreListAdapter>();
        this.joined_hives = new HashMap<String,Boolean>();
        this.joined = 0;
        this.activeList = 0;

        this.LRU_step.put(this.activeList,new Date());
        this.LRU_date.put(this.LRU_step.get(this.activeList),this.activeList);

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
        this.findViewById(R.id.explore_new_hive_button).setOnClickListener(this.new_hive_button_click);

        setTabStatus(0);

        loadListView(0);
        loadListView(1);

        exploreListAdapter.get(0).setActive(true);
    }

    private void Restore(Bundle savedInstance) {
        if ((savedInstance.containsKey("joined_hives_keys")) && (savedInstance.containsKey("joined_hives_values"))) {
            String[] keys = savedInstance.getStringArray("joined_hives_keys");
            boolean[] values = savedInstance.getBooleanArray("joined_hives_values");
            for (int i = 0; i < keys.length; i++) {
                joined_hives.put(keys[i], values[i]);
                if (values[i])
                    joined++;
            }

            if (joined > 0) {
                findViewById(R.id.explore_action_bar_goBack_button).setBackgroundResource(R.drawable.explore_action_bar_hive_joined_border);
                findViewById(R.id.explore_action_bar_hive_added).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.explore_action_bar_goBack_image)).setImageResource(R.drawable.explore_new_hive_back_with_subscriptions);
                ((TextView) findViewById(R.id.explore_action_bar_number_text)).setText(String.valueOf(joined));

                if (activeList < 4)
                    exploreListAdapter.get(activeList).syncNotifyDataSetChanged();
                else if (activeList == 4)
                    exploreFilteredListAdapter.syncNotifyDataSetChanged();
            }
        }
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

            if (LRU_step.containsKey(actualStep))
                LRU_date.remove(LRU_step.get(actualStep));

            LRU_step.put(actualStep,new Date());
            LRU_date.put(LRU_step.get(actualStep),actualStep);

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

            if (((Hive.getHiveCount() > 0) && (Hive.isHiveJoined(hive.getNameUrl()))) || ((joined_hives.containsKey(hive.getNameUrl())) && (joined_hives.get(hive.getNameUrl())))) {
                Intent data = new Intent();
                data.putExtra("NameURL", hive.getNameUrl());
                setResult(RESULT_OK, data);
                finish();
            }
            else if (!joined_hives.containsKey(hive.getNameUrl())) {
                controller.JoinHive(hive);
                joined_hives.put(hive.getNameUrl(),false);
                if (activeList < 4)
                    exploreListAdapter.get(activeList).notifyDataSetChanged();
                else if (activeList == 4)
                    exploreFilteredListAdapter.notifyDataSetChanged();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Main.OP_CODE_NEW_HIVE:
                if (resultCode == RESULT_OK){
                    this.setResult(RESULT_OK);
                    this.finish();
                }
                break;
        }
    }

    protected View.OnClickListener new_hive_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),NewHive.class);
            startActivityForResult(intent, Main.OP_CODE_NEW_HIVE);
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

    protected void unloadList(int list) {
        if (list < 4) {
            if (exploreListAdapter.containsKey(list))
                exploreListAdapter.get(list).Clear();
            exploreListAdapter.remove(list);
        } else if (list == 4) {
            if (exploreCategoriesListAdapter != null) {
                exploreCategoriesListAdapter.Clear();
                exploreCategoriesListAdapter = null;
            }
            if (exploreFilteredListAdapter != null) {
                exploreFilteredListAdapter.Clear();
                exploreFilteredListAdapter = null;
            }
        }

        this.LRU_date.remove(this.LRU_step.get(list));
        this.LRU_step.remove(list);
    }

    @Override
    public void onLowMemory () {
        Log.w("Explore - Trim Memory","TRIM_MEMORY_COMPLETE");
        for (Integer list : this.LRU_date.values())
            unloadList(list);
    }

    @Override
    public void onTrimMemory (int level) {
        if (level >= TRIM_MEMORY_COMPLETE) {
            this.onLowMemory();
        } else {
            /*if (level >= TRIM_MEMORY_UI_HIDDEN) {

            } else*/ if (level >= TRIM_MEMORY_BACKGROUND) {
                Log.w("Explore - Trim Memory","TRIM_MEMORY_BACKGROUND");
                while (this.LRU_date.firstEntry().getValue() != this.activeList)
                    unloadList(this.LRU_date.firstEntry().getValue());
            } else if (level >= TRIM_MEMORY_MODERATE) {
                Log.w("Explore - Trim Memory","TRIM_MEMORY_MODERATE");
                while (this.LRU_date.size() > 2)
                    unloadList(this.LRU_date.firstEntry().getValue());
            }
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        if (joined_hives.size() > 0) {
            String[] keys = joined_hives.keySet().toArray(new String[joined_hives.size()]);
            outState.putStringArray("joined_hives_keys", keys);
            boolean[] values = new boolean[joined_hives.size()];
            for (int i = 0; i < keys.length; i++)
                values[i] = joined_hives.get(keys[i]);
            outState.putBooleanArray("joined_hives_values", values);
        }
    }
}
