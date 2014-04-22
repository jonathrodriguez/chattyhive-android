package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.util.events.EventArgs;

import java.util.ArrayList;

/**
 * Created by Jonathan on 11/04/2014.
 */
public class ExploreListAdapter extends BaseAdapter {
    private Boolean moreItems;
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

    public ExploreListAdapter (Context activityContext,ArrayList<Hive> hivesList, Boolean moreItems, ListView listView) {
        this.hives_list_data = hivesList;
        this.moreItems = moreItems;

        this.context = activityContext;
        this.inflater = ((Activity)this.context).getLayoutInflater();

        this.listView = listView;
        this.listView.setAdapter(this);
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
        ViewHolder holder = null;
        //int type = getItemViewType(position);
        if(convertView==null){
            holder = new ViewHolder();
            /*switch (type) {
                case R.id.MainPanelChat_ListKind_Hive_Other:
                    convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_other,parent,false);
                    holder.username = (TextView)convertView.findViewById(R.id.main_panel_chat_username);
                    holder.messageText = (TextView)convertView.findViewById(R.id.main_panel_chat_messageText);
                    holder.timeStamp = (TextView)convertView.findViewById(R.id.main_panel_chat_timeStamp);
                    holder.avatarThumbnail = (ImageView)convertView.findViewById(R.id.main_panel_chat_avatarThumbnail);
                    break;
                case R.id.MainPanelChat_ListKind_Hive_Me:
                    convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_me,parent,false);
                    Log.w("ChatListAdapter", "Is me...");
                    holder.username = (TextView)convertView.findViewById(R.id.main_panel_chat_username);
                    holder.messageText = (TextView)convertView.findViewById(R.id.main_panel_chat_messageText);
                    holder.timeStamp = (TextView)convertView.findViewById(R.id.main_panel_chat_timeStamp);
                    holder.avatarThumbnail = (ImageView)convertView.findViewById(R.id.main_panel_chat_avatarThumbnail);
                    Log.w("ChatListAdapter", "What is wrong?");
                    break;
            }*/

            convertView = this.inflater.inflate(R.layout.explore_list_item,parent,false);
            holder.scoreAndImage = (TextView)convertView.findViewById(R.id.explore_list_item_image_and_score_textview);
            holder.mainTitle = (TextView)convertView.findViewById(R.id.explore_list_item_name);
            holder.mainText = (TextView)convertView.findViewById(R.id.explore_list_item_text);
            holder.categoryText = (TextView)convertView.findViewById(R.id.explore_list_item_category_textview);
            holder.categoryImage = (ImageView)convertView.findViewById(R.id.explore_list_item_category_imageview);
            holder.usersText = (TextView)convertView.findViewById(R.id.explore_list_item_users_textview);
            holder.usersImage = (ImageView)convertView.findViewById(R.id.explore_list_item_users_image_view);

            convertView.setTag(R.id.Explore_ListViewHolder,holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.Explore_ListViewHolder);
        }

        Hive hive = this.hives_list_data.get(position);

        convertView.setTag(R.id.BO_Hive,hive);

        if (hive.getName() != null) {
            holder.mainTitle.setText(hive.getName());
            //holder.username.setTextColor(Color.parseColor(message.getUser()._color));
        }
       /* else {
            holder.username.setText("noName");
            holder.username.setTextColor(Color.parseColor("#111111"));
        }*/



        holder.mainText.setText(hive.getDescription());
        String category = hive.getCategory();
        holder.categoryText.setText(category);
        holder.usersText.setText("0");
        holder.scoreAndImage.setText(String.valueOf(position).concat("/100"));
        if ((position % 2) == 0) {
            holder.scoreAndImage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.launcher_launcher_a, 0, 0);
        } else {
            holder.scoreAndImage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.pestanha_chats_mas_opciones, 0, 0);
        }

        if (category.equalsIgnoreCase("sports")) {
            holder.categoryImage.setImageResource(R.drawable.menu_news_negro);
        } else if (category.equalsIgnoreCase("science")) {
            holder.categoryImage.setImageResource(R.drawable.pestanha_hives_recommended_users);
        } else if (category.equalsIgnoreCase("free time")) {
            holder.categoryImage.setImageResource(R.drawable.pestanha_hives_location);
        }

        if ((position == (this.getCount()-1)) && (this.moreItems)) {
            this.moreItems = ((Explore)this.context).GetMoreHives();
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView scoreAndImage;
        public TextView mainTitle;
        public TextView mainText;
        public ImageView categoryImage;
        public TextView categoryText;
        public ImageView usersImage;
        public TextView usersText;
    }
}
