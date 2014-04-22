package com.chattyhive.chattyhive;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;

public class Explore extends Activity {

    Controller controller;
    ExploreListAdapter exploreListAdapter;
    int lastOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);

        this.Initialize();
    }

    private void Initialize() {
        this.controller = Controller.getRunningController();
        boolean moreItems = this.controller.exploreHives(0,9);
        this.lastOffset = 9;
        this.exploreListAdapter = new ExploreListAdapter(this,this.controller.getExploreHives(),moreItems,(ListView)this.findViewById(R.id.explore_list_listView));

        try {
            this.controller.SubscribeToExploreHivesListChange(new EventHandler<EventArgs>(exploreListAdapter, "OnAddItem", EventArgs.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        };
    }

    public boolean GetMoreHives() {
        this.lastOffset += 9;
        return this.controller.exploreHives(this.lastOffset,9);
    }
}
