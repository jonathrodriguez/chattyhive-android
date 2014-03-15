package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Hive;
import com.chattyhive.backend.businessobjects.Mate;
import com.chattyhive.backend.util.events.EventArgs;

import java.util.ArrayList;

/**
 * Created by Jonathan on 13/03/14.
 */
public class LeftPanelListAdapter extends BaseAdapter {
    public static final int LEFT_PANEL_LIST_KIND_NONE  = 0;
    public static final int LEFT_PANEL_LIST_KIND_CHATS = 1;
    public static final int LEFT_PANEL_LIST_KIND_HIVES = 2;
    public static final int LEFT_PANEL_LIST_KIND_MATES = 3;
    private static final int LEFT_PANEL_LIST_KIND_COUNT = 4;

    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    private ArrayList<Hive> hives_list_data;
    private ArrayList chats_list_data;
    private ArrayList<Mate> mates_list_data;
    private int visibleList;

    public void SetVisibleList(int LEFT_PANEL_LIST_KIND) { this.visibleList = LEFT_PANEL_LIST_KIND; }
    public  int GetVisibleList()                         { return this.visibleList; }

    public void OnAddItem(Object sender, EventArgs args) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public LeftPanelListAdapter (Context activityContext,ArrayList<Hive> hivesList, ArrayList chatsList, ArrayList<Mate> matesList) {
        this.hives_list_data = hivesList;
        this.chats_list_data = chatsList;
        this.mates_list_data = matesList;

        this.context = activityContext;
        this.inflater = ((Activity)this.context).getLayoutInflater();

        this.listView = ((ListView)((Activity)this.context).findViewById(R.id.left_panel_element_list));
    }

    @Override
    public int getItemViewType(int position) {
        return visibleList;
    }

    @Override
    public int getViewTypeCount() {
        return LEFT_PANEL_LIST_KIND_COUNT;
    }

    @Override
    public int getCount() {
        switch (this.visibleList) {
            case LEFT_PANEL_LIST_KIND_HIVES:
                return this.hives_list_data.size();
            case LEFT_PANEL_LIST_KIND_CHATS:
                return this.chats_list_data.size();
            case LEFT_PANEL_LIST_KIND_MATES:
                return this.mates_list_data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position){
        switch (this.visibleList) {
            case LEFT_PANEL_LIST_KIND_HIVES:
                return this.hives_list_data.get(position);
            case LEFT_PANEL_LIST_KIND_CHATS:
                return this.chats_list_data.get(position);
            case LEFT_PANEL_LIST_KIND_MATES:
                return this.mates_list_data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = visibleList;
        if (type == LEFT_PANEL_LIST_KIND_NONE) { return null; }
        if (convertView==null) {
            switch (type) {
                case LEFT_PANEL_LIST_KIND_HIVES:
                    holder = new HiveViewHolder();
                    convertView = this.inflater.inflate(R.layout.left_panel_hives_list_item,parent,false);
                    ((HiveViewHolder)holder).hiveName = (TextView)convertView.findViewById(R.id.left_panel_hives_list_item_hive_name);
                    ((HiveViewHolder)holder).hiveImage = (ImageView)convertView.findViewById(R.id.left_panel_hives_list_item_img);
                    break;
                case LEFT_PANEL_LIST_KIND_CHATS:
                    //convertView = this.inflater.inflate(R.layout.multichat_message_me,parent,false);
                    holder = new ChatViewHolder();
                    break;
                case LEFT_PANEL_LIST_KIND_MATES:
                    //convertView = this.inflater.inflate(R.layout.multichat_message_me,parent,false);
                    holder = new MateViewHolder();
                    break;
            }
            if (convertView != null)
                convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Object item = this.getItem(position);

        switch (type) {
            case LEFT_PANEL_LIST_KIND_HIVES:
                ((HiveViewHolder)holder).hiveName.setText(((Hive)item).get_name());
                //((HiveViewHolder)holder).hiveImage = (ImageView)convertView.findViewById(R.id.left_panel_hives_list_item_img);
                break;
            case LEFT_PANEL_LIST_KIND_CHATS:
                break;
            case LEFT_PANEL_LIST_KIND_MATES:
                break;
        }

        return convertView;
    }

    private abstract class ViewHolder{}

    private class HiveViewHolder extends ViewHolder {
        public TextView hiveName;
        public ImageView hiveImage;
    }

    private class ChatViewHolder extends ViewHolder {

    }

    private class MateViewHolder extends ViewHolder {

    }
}
