package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.util.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Jonathan on 11/04/2014.
 */

public class ExploreListAdapter extends BaseAdapter {
    Controller controller;
    private Boolean moreItems;
    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    private ArrayList<Hive> hives_list_data;
    private View.OnClickListener clickListener;
    private Hive hive;

    public void SetOnClickListener (View.OnClickListener listener) { this.clickListener = listener; notifyDataSetChanged(); }

    public void OnAddItem(Object sender, EventArgs args) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public ExploreListAdapter (Context activityContext, ArrayList<Hive> hivesList, ListView listView) {
        this.controller = Controller.GetRunningController();
        this.hives_list_data = hivesList;
        this.moreItems = false;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder = new ViewHolder();

            convertView = this.inflater.inflate(R.layout.explore_list_item,parent,false);
//HOLDERS COLLAPSED HIVE CARD
            holder.collapsed_hive_name = (TextView)convertView.findViewById(R.id.explore_list_item_collapsed_hive_name);
            holder.collapsed_hive_description = (TextView)convertView.findViewById(R.id.explore_list_item_collapsed_hive_description);
            holder.collapsed_hiveImage = (ImageView)convertView.findViewById(R.id.explore_list_item_collapsed_hive_image);
            holder.collapsed_categoryText = (TextView)convertView.findViewById(R.id.explore_list_item_collapsed_category_text);
            holder.collapsed_categoryImage = (ImageView)convertView.findViewById(R.id.explore_list_item_collapsed_category_image);
            holder.collapsed_usersText = (TextView)convertView.findViewById(R.id.explore_list_item_collapsed_users_number);
//HOLDERS EXPANDED HIVE CARD
            holder.expanded_hive_name = (TextView)convertView.findViewById(R.id.explore_list_item_expanded_hive_name);
            holder.expanded_hive_description = (TextView)convertView.findViewById(R.id.explore_list_item_expanded_hive_description);
            holder.expanded_hiveImage = (ImageView)convertView.findViewById(R.id.explore_list_item_expanded_hive_image);
            holder.expanded_categoryText = (TextView)convertView.findViewById(R.id.explore_list_item_expanded_category_text);
            holder.expanded_categoryImage = (ImageView)convertView.findViewById(R.id.explore_list_item_expanded_category_image);
            holder.expanded_usersText = (TextView)convertView.findViewById(R.id.explore_list_item_expanded_users_number);
            holder.card_number = -1;

            convertView.setTag(R.id.Explore_ListViewHolder, holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.Explore_ListViewHolder);
        }

        View.OnClickListener expand_hive = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder h = (ViewHolder)v.getTag(R.id.Explore_ListViewHolder);
                int hive_num = h.card_number;

                /*if (hive_num == -1){
                    h.card_number = position;
                }*/

                if (position == hive_num){//SI SE SELECCIONA EL HIVE YA EXPANDIDO SE PONE A -1
                    h.card_number = -1;
                }
                else {
                    h.card_number = position;
                }
                v.setTag(R.id.Explore_ListViewHolder, h);
                notifyDataSetChanged();

            }
        };
        convertView.setOnClickListener(expand_hive);//setOnClickListener to expand/collapse hive cards

        System.out.println("POSITION: "+position);
        System.out.println("CARD_NUMBER: "+holder.card_number);
        if(holder.card_number == position) {//EXPANDIR
            convertView.findViewById(R.id.explore_list_item_short).setVisibility(View.GONE);
            convertView.findViewById(R.id.explore_hive_card).setVisibility(View.VISIBLE);
        }
        else {//CONTRAER
            convertView.findViewById(R.id.explore_hive_card).setVisibility(View.GONE);
            convertView.findViewById(R.id.explore_list_item_short).setVisibility(View.VISIBLE);
        }

        this.hive = this.hives_list_data.get(position);
        convertView.findViewById(R.id.explore_join_button).setTag(R.id.BO_Hive, hive);//cambiado del converview al boton de join para recuperar info de hive subscrito!!!

        holder.collapsed_hive_name.setText(hive.getName());
        holder.collapsed_hive_description.setText(hive.getDescription());
        Category.setCategory(hive.getCategory(),holder.collapsed_categoryImage,holder.collapsed_categoryText);
        //holder.collapsed_usersText.setText("");

        holder.expanded_hive_name.setText(hive.getName());
        holder.expanded_hive_description.setText(hive.getDescription());
        Category.setCategory(hive.getCategory(),holder.expanded_categoryImage,holder.expanded_categoryText);
        //holder.collapsed_usersText.setText("");

        holder.expanded_hiveImage.setImageResource(R.drawable.pestanha_chats_public_chat);
        holder.collapsed_hiveImage.setImageResource(R.drawable.pestanha_chats_public_chat);
        try {
            hive.getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadCollapsedHiveImage", EventArgs.class));
            hive.getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(holder, "loadExpandedHiveImage", EventArgs.class));
            hive.getHiveImage().loadImage(Image.ImageSize.medium, 0);
            hive.getHiveImage().loadImage(Image.ImageSize.large, 0);
        } catch (Exception e) { }

       /*if ((position == (this.getCount()-1)) && (this.moreItems)) {
            ((Explore)this.context).GetMoreHives();
        }*/

        View.OnClickListener join_button_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("join!!!!");
                //String hiveNameURL =((String) ((TextView)v.findViewById(R.id.explore_list_item_name)).getTag());
                //controller.JoinHive(hiveNameURL);

                if(((View)v.getParent()).findViewById(R.id.explore_chat_button2).getVisibility() == View.GONE){
                    ((View)v.getParent()).findViewById(R.id.explore_chat_button2).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.explore_join_button).setVisibility(View.GONE);
                    //((TextView)((View)v.getParent()).findViewById(R.id.explore_list_item_collapsed_hive_name)).setTextColor(R.color.explore_hive_joined);   FIND PARENT
                    //((TextView)((View)v.getParent()).findViewById(R.id.explore_list_item_expanded_hive_name)).setTextColor(R.color.explore_hive_joined);
                    controller.JoinHive(hive.getNameUrl());
                }
            }
        };
        convertView.findViewById(R.id.explore_join_button).setOnClickListener(join_button_click);
        return convertView;
    }

    private class ViewHolder {
        public TextView collapsed_hive_name;
        public TextView collapsed_hive_description;
        public ImageView collapsed_categoryImage;
        public TextView collapsed_categoryText;
        public TextView collapsed_usersText;
        public ImageView collapsed_hiveImage;
        public TextView expanded_hive_name;
        public TextView expanded_hive_description;
        public ImageView expanded_categoryImage;
        public TextView expanded_categoryText;
        public TextView expanded_usersText;
        public ImageView expanded_hiveImage;
        public int card_number;

        public void loadCollapsedHiveImage(Object sender,EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final ViewHolder thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.medium,0);
                    if (is != null) {
                        collapsed_hiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"loadCollapsedHiveImage",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }

        public void loadExpandedHiveImage(Object sender,EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final ViewHolder thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.large,0);
                    if (is != null) {
                        expanded_hiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"loadExpandedHiveImage",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }
    }
}
