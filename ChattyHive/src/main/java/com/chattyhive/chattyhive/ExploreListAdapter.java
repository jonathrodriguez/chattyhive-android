package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.chattyhive.backend.businessobjects.Hive;
import com.chattyhive.backend.businessobjects.Mate;
import com.chattyhive.backend.util.events.EventArgs;

import java.util.ArrayList;

/**
 * Created by Jonathan on 11/04/2014.
 */
public class ExploreListAdapter extends BaseAdapter {
    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    private ArrayList<Hive> hives_list_data;

    private View.OnClickListener clickListener;
    public void SetOnClickListener (View.OnClickListener listener) { this.clickListener = listener; notifyDataSetChanged(); }

    public void OnAddItem(Object sender, EventArgs args) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public ExploreListAdapter (Context activityContext,ArrayList<Hive> hivesList, ListView listView) {
        this.hives_list_data = hivesList;

        this.context = activityContext;
        this.inflater = ((Activity)this.context).getLayoutInflater();

        this.listView = listView;
    }

    @Override
    public int getCount() {
        return this.hives_list_data.size();
    }

    @Override
    public Object getItem(int position) {
        return this.hives_list_data.get(position);
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
