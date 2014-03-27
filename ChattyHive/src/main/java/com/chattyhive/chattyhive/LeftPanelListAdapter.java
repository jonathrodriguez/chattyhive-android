package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Hive;
import com.chattyhive.backend.businessobjects.Mate;
import com.chattyhive.backend.util.events.EventArgs;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Jonathan on 13/03/14.
 */
public class LeftPanelListAdapter extends BaseAdapter {

    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    private ArrayList<Hive> hives_list_data;
    private ArrayList chats_list_data;
    private ArrayList<Mate> mates_list_data;
    private int visibleList;

    private View.OnClickListener clickListener;

    public void SetVisibleList(int LeftPanel_ListKind) { this.visibleList = LeftPanel_ListKind; }
    public  int GetVisibleList()                         { return this.visibleList; }

    public void SetOnClickListener (View.OnClickListener listener) { this.clickListener = listener; notifyDataSetChanged(); }

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
        return this.context.getResources().getInteger(R.integer.LeftPanel_ListKind_Count);
    }

    @Override
    public int getCount() {
        switch (this.visibleList) {
            case R.id.LeftPanel_ListKind_Hives:
                return this.hives_list_data.size();
            case R.id.LeftPanel_ListKind_Chats:
                return this.chats_list_data.size();
            case R.id.LeftPanel_ListKind_Mates:
                return this.mates_list_data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position){
        switch (this.visibleList) {
            case R.id.LeftPanel_ListKind_Hives:
                return this.hives_list_data.get(position);
            case R.id.LeftPanel_ListKind_Chats:
                return this.chats_list_data.get(position);
            case R.id.LeftPanel_ListKind_Mates:
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
        if (type == R.id.LeftPanel_ListKind_None) { return null; }
        if (convertView==null) {
            switch (type) {
                case R.id.LeftPanel_ListKind_Hives:
                    holder = new HiveViewHolder();
                    convertView = this.inflater.inflate(R.layout.left_panel_hives_list_item,parent,false);
                    ((HiveViewHolder)holder).hiveItem = (LinearLayout)convertView.findViewById((R.id.left_panel_hives_list_item_top_view));
                    ((HiveViewHolder)holder).hiveName = (TextView)convertView.findViewById(R.id.left_panel_hives_list_item_hive_name);
                    ((HiveViewHolder)holder).hiveImage = (ImageView)convertView.findViewById(R.id.left_panel_hives_list_item_img);
                    ((HiveViewHolder)holder).hiveItem.setOnClickListener(clickListener);
                    break;
                case R.id.LeftPanel_ListKind_Chats:
                    //convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_me,parent,false);
                    holder = new ChatViewHolder();
                    break;
                case R.id.LeftPanel_ListKind_Mates:
                    //convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_me,parent,false);
                    holder = new MateViewHolder();
                    break;
            }
            if (convertView != null)
                convertView.setTag(R.id.LeftPanel_ListViewHolder,holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.LeftPanel_ListViewHolder);
        }

        Object item = this.getItem(position);

        switch (type) {
            case R.id.LeftPanel_ListKind_Hives:
                ((HiveViewHolder)holder).hiveName.setText(((Hive)item).get_name());
                ((HiveViewHolder)holder).hiveItem.setTag(R.id.BO_Hive,item);
                //((HiveViewHolder)holder).hiveImage = (ImageView)convertView.findViewById(R.id.left_panel_hives_list_item_img);
                break;
            case R.id.LeftPanel_ListKind_Chats:
                break;
            case R.id.LeftPanel_ListKind_Mates:
                break;
        }

        return convertView;
    }

    private abstract class ViewHolder{}

    private class HiveViewHolder extends ViewHolder {
        public LinearLayout hiveItem;
        public TextView hiveName;
        public ImageView hiveImage;
    }

    private class ChatViewHolder extends ViewHolder {

    }

    private class MateViewHolder extends ViewHolder {

    }
}
