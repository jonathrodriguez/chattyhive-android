package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Jonathan on 10/10/2014.
 */
public class Home {
    Context context;
    HomeListAdapter homeListAdapter;

    public Home (final Context context) {
        this.context = context;
        this.homeListAdapter = new HomeListAdapter(context);
        this.Reload();
    }

    public void setButtons() {
        ((Activity)this.context).findViewById(R.id.home_chat_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Main)context).ShowChats();
            }
        });

        ((Activity)this.context).findViewById(R.id.home_explore_button).setOnClickListener(((Main)this.context).explore_button_click);

        //((Activity)this.context).findViewById(R.id.home_hive_button).setOnClickListener(); //TODO: Define action.
    }

    public void Reload() {
        ((ListView)((Activity)this.context).findViewById(R.id.home_listView)).setAdapter(this.homeListAdapter);

        ((Main)this.context).controller.RequestHome();
        this.setButtons();
    }
}
