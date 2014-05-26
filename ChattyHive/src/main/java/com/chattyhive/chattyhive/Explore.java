package com.chattyhive.chattyhive;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.businessobjects.MessageContent;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.OSStorageProvider.MessageLocalStorage;

import java.util.Date;

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
        this.controller = Controller.getRunningController(LoginLocalStorage.getLoginLocalStorage());
        this.controller.setMessageLocalStorage(MessageLocalStorage.getMessageLocalStorage());
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

    protected View.OnClickListener join_button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String hiveNameURL =((String) ((TextView)v.findViewById(R.id.explore_list_item_name)).getTag());
            if (controller.JoinHive(hiveNameURL)) {
                if (joined == 0)
                    ((ImageButton)findViewById(R.id.explore_action_bar_goBack_button)).setBackgroundColor(Color.GREEN);
                joined++;
                ((TextView)findViewById(R.id.explore_action_bar_number_text)).setText(String.valueOf(joined));
            }
        }
    };
}
