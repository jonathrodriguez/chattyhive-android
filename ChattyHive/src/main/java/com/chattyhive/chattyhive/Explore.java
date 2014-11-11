package com.chattyhive.chattyhive;

import android.app.Activity;
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

public class Explore extends Activity {

    Controller controller;
    ExploreListAdapter exploreListAdapter;
    int lastOffset;
    int joined = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);
        ListView listView = (ListView) findViewById(R.id.explore_list_listView);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.explore_hive_card, listView, false);
        listView.addHeaderView(header);
        this.Initialize();
    }

    private void Initialize(){
        this.controller = Controller.GetRunningController();
        this.lastOffset = 0;

        this.exploreListAdapter = new ExploreListAdapter(this,this.controller.getExploreHives(), (ListView)this.findViewById(R.id.explore_list_listView));
        ((ListView) this.findViewById(R.id.explore_list_listView)).setAdapter(this.exploreListAdapter);

        this.controller.ExploreHivesListChange.add(new EventHandler<EventArgs>(exploreListAdapter, "OnAddItem", EventArgs.class));
        this.controller.HiveJoined.add(new EventHandler<EventArgs>(this,"onHiveJoined", EventArgs.class));

        this.findViewById(R.id.explore_action_bar_goBack_button).setOnClickListener(this.backButton);
        this.findViewById(R.id.explore_button_categories).setOnClickListener(this.categoriesButton);
        this.controller.exploreHives(0,9, Controller.ExploreType.OUTSTANDING);
    }

    public void GetMoreHives() {
        this.lastOffset += 9;
        this.controller.exploreHives(this.lastOffset,9, Controller.ExploreType.OUTSTANDING);
    }

    protected View.OnClickListener backButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (joined > 0)
                setResult(RESULT_OK);
            finish();
        }
    };

    protected View.OnClickListener categoriesButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(findViewById(R.id.explore_list_categories).getVisibility()==View.GONE){
                findViewById(R.id.explore_list_categories).setVisibility(View.VISIBLE);
                findViewById(R.id.explore_list_frame).setVisibility(View.GONE);
            }
        }
    };

    protected View.OnClickListener expand_hive = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //System.out.println("expanded!!!!");
            if(v.findViewById(R.id.explore_hive_card).getVisibility() == View.GONE) {
                v.findViewById(R.id.explore_list_item_short).setVisibility(View.GONE);
                v.findViewById(R.id.explore_hive_card).setVisibility(View.VISIBLE);
            }
            else if (findViewById(R.id.explore_list_item_short).getVisibility() == View.GONE) {
                v.findViewById(R.id.explore_hive_card).setVisibility(View.GONE);
                v.findViewById(R.id.explore_list_item_short).setVisibility(View.VISIBLE);
            }
        }
    };

    public void onHiveJoined(Object sender,EventArgs eventArgs) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (joined == 0)
                    findViewById(R.id.explore_action_bar_goBack_button).setBackgroundColor(Color.GREEN);
                joined++;
                ((TextView)findViewById(R.id.explore_action_bar_number_text)).setText(String.valueOf(joined));
            }
        });
    }

    @Override
    protected void onDestroy(){
        this.controller.ExploreHivesListChange.remove(new EventHandler<EventArgs>(exploreListAdapter, "OnAddItem", EventArgs.class));
        this.controller.HiveJoined.remove(new EventHandler<EventArgs>(this, "onHiveJoined", EventArgs.class));

        exploreListAdapter = null;
        controller = null;
        super.onDestroy();
    }
}
