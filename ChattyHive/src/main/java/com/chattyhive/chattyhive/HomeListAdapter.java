package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Home.HomeCard;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;

import java.util.ArrayList;

/**
 * Created by Jonathan on 07/10/2014.
 */
public class HomeListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private Controller controller;
    private ArrayList<HomeCard> homeCards;

    public HomeListAdapter(Context context) {
        this.context = context;
        this.inflater = ((Activity)context).getLayoutInflater();
        this.controller = ((Main)this.context).controller;

        this.controller.HomeReceived.add(new EventHandler<EventArgs>(this,"onHomeChanged",EventArgs.class));
        ((ListView)((Activity)this.context).findViewById(R.id.left_panel_element_list)).setAdapter(this);
    }

    public void onHomeChanged(Object sender, EventArgs eventArgs) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                homeCards = controller.getHomeCards();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        if (this.homeCards == null)
            return 0;
        else
            return this.homeCards.size();
    }

    @Override
    public Object getItem(int position) {
        if ((this.homeCards == null) || (this.homeCards.size() <= position))
            return null;
        else
            return this.homeCards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }


}
