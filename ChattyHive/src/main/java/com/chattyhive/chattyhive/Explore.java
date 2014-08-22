package com.chattyhive.chattyhive;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.MessageLocalStorage;

public class Explore extends Activity {

    Controller controller;
    ExploreListAdapter exploreListAdapter;
    int lastOffset;
    int joined = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);

        this.Initialize();
    }

    private void Initialize() {
        this.controller = Controller.GetRunningController();
        this.lastOffset = 0;

        this.exploreListAdapter = new ExploreListAdapter(this,this.controller.getExploreHives(),(ListView)this.findViewById(R.id.explore_list_listView));

        try {
            this.controller.ExploreHivesListChange.add(new EventHandler<EventArgs>(exploreListAdapter, "OnAddItem", EventArgs.class));
            this.controller.HiveJoined.add(new EventHandler<EventArgs>(this,"onHiveJoined",EventArgs.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void GetMoreHives() {
        this.lastOffset += 9;
        this.controller.exploreHives(this.lastOffset,9);
    }

    protected View.OnClickListener join_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String hiveNameURL =((String) ((TextView)v.findViewById(R.id.explore_list_item_name)).getTag());
            controller.JoinHive(hiveNameURL);
        }
    };

    public void onHiveJoined(Object sender,EventArgs eventArgs) {
        if (joined == 0)
            ((ImageButton)findViewById(R.id.explore_action_bar_goBack_button)).setBackgroundColor(Color.GREEN);
        joined++;
        ((TextView)findViewById(R.id.explore_action_bar_number_text)).setText(String.valueOf(joined));
    }

    @Override
    protected void onDestroy(){
        try {
            this.controller.ExploreHivesListChange.remove(new EventHandler<EventArgs>(exploreListAdapter, "OnAddItem", EventArgs.class));
            this.controller.HiveJoined.remove(new EventHandler<EventArgs>(this,"onHiveJoined",EventArgs.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        exploreListAdapter = null;
        controller = null;
        super.onDestroy();
    }
}
