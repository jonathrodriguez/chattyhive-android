package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.*;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Explore;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.util.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jonathan on 11/04/2014.
 */

public class ExploreListAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private Context context;

    private Controller controller;
    private String exploreListHeader;
    private Explore.SortType sortType;
    private String categoryCode;

    private Explore explore;
    private ArrayList<Hive> hives_list_data;

    private LayoutInflater inflater;

    private ListView listView;
    public void setListView (ListView listView) {
        if (this.listView == listView) return;
        this.listView = listView;
        if (this.listView == null) return;

        ViewGroup header = (ViewGroup) this.inflater.inflate(R.layout.explore_list_header, this.listView, false);
        ((TextView)header.findViewById(R.id.explore_title)).setText(this.exploreListHeader);
        listView.addHeaderView(header);

        ViewGroup footer = (ViewGroup) this.inflater.inflate(R.layout.explore_list_footer, this.listView, false);
        listView.addFooterView(footer);

        if (explore.HasMore()) {
            listView.findViewById(R.id.explore_list_overscroll_footer_loading_panel).setVisibility(View.VISIBLE);
        } else {
            listView.findViewById(R.id.explore_list_overscroll_footer_loading_panel).setVisibility(View.GONE);
        }

        this.listView.setOnScrollListener(this);
        this.listView.setAdapter(this);
        if (this.scroll_position != -1)
            this.listView.smoothScrollToPosition(this.scroll_position);
    }

    private int expanded_hive;
    private int scroll_position;
    private HashMap<String,Boolean> joined_hives;

    private View.OnClickListener expandedHiveDescriptionButtonClickListener;
    public void SetPublicChatClickListener(View.OnClickListener listener) {
        this.expandedHiveDescriptionButtonClickListener = listener;
        syncNotifyDataSetChanged();
    }

    private Boolean active;
    public void setActive(Boolean active) {
        if ((active) && (!this.active) && (explore.HasMore()) && (explore.getResults().size() == 0)) {
            explore.More();
        }
        this.active = active;
    }

    public void OnAddItem(Object sender, EventArgs args) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                hives_list_data = new ArrayList<Hive>(explore.getResults());
                if (explore.HasMore()) {
                    listView.findViewById(R.id.explore_list_overscroll_footer_loading_panel).setVisibility(View.VISIBLE);
                } else {
                    listView.findViewById(R.id.explore_list_overscroll_footer_loading_panel).setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            }
        });
    }

    public void syncNotifyDataSetChanged() {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public ExploreListAdapter (Context context, Explore.SortType sortType, String categoryCode, String exploreListHeader, HashMap<String,Boolean> joined_hives, View.OnClickListener expandedHiveDescriptionButtonClickListener) {
        this.expanded_hive = -1;
        this.scroll_position = -1;
        this.active = false;
        this.categoryCode = categoryCode;

        this.context = context;
        this.sortType = sortType;
        this.joined_hives = joined_hives;
        this.exploreListHeader = exploreListHeader;
        this.expandedHiveDescriptionButtonClickListener = expandedHiveDescriptionButtonClickListener;

        this.inflater = ((Activity)this.context).getLayoutInflater();
        this.controller = ((com.chattyhive.chattyhive.Explore)this.context).controller;
        this.explore = new Explore(this.controller,this.sortType,this.categoryCode);

        this.explore.onMoreResults.add(new EventHandler<EventArgs>(this,"OnAddItem",EventArgs.class));
        this.hives_list_data = new ArrayList<Hive>(this.explore.getResults());
        this.setListView(null);
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
            holder.expanded_hive_chatLanguages = (TextView)convertView.findViewById(R.id.explore_list_item_expanded_hive_chat_languages);

            convertView.setTag(R.id.Explore_ListViewHolder, holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.Explore_ListViewHolder);
        }

        View.OnClickListener expand_hive = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder h = (ViewHolder)v.getTag(R.id.Explore_ListViewHolder);

                if (expanded_hive == position){//SI SE SELECCIONA EL HIVE YA EXPANDIDO SE PONE A -1
                    expanded_hive = -1;
                }
                else {
                    expanded_hive = position;
                }

                notifyDataSetChanged();
            }
        };
        convertView.setOnClickListener(expand_hive);//setOnClickListener to expand/collapse hive cards

        if(expanded_hive == position) {//EXPANDIR
            convertView.findViewById(R.id.explore_list_item_short).setVisibility(View.GONE);
            convertView.findViewById(R.id.explore_hive_card).setVisibility(View.VISIBLE);
            ((ListView)parent).smoothScrollToPosition(position);
        }
        else {//CONTRAER
            convertView.findViewById(R.id.explore_hive_card).setVisibility(View.GONE);
            convertView.findViewById(R.id.explore_list_item_short).setVisibility(View.VISIBLE);
        }

        Hive hive = this.hives_list_data.get(position);
        holder.hive = hive;
        convertView.findViewById(R.id.explore_join_button).setTag(R.id.BO_Hive, hive);//cambiado del converview al boton de join para recuperar info de hive subscrito!!!

        holder.collapsed_hive_name.setText(hive.getName());
        holder.collapsed_hive_description.setText(hive.getDescription());
        Category.setCategory(hive.getCategory(),holder.collapsed_categoryImage,holder.collapsed_categoryText);
        holder.collapsed_usersText.setText(String.valueOf(hive.getSubscribedUsers()));

        holder.expanded_hive_name.setText(hive.getName());
        holder.expanded_hive_description.setText(hive.getDescription());
        Category.setCategory(hive.getCategory(),holder.expanded_categoryImage,holder.expanded_categoryText);
        holder.expanded_usersText.setText(context.getString(R.string.explore_hive_card_expanded_n_mates,hive.getSubscribedUsers()));

        String chatLanguages = "";
        if (hive.getChatLanguages() != null)
            for (String language : hive.getChatLanguages())
                chatLanguages = chatLanguages.concat((chatLanguages.isEmpty())?"":", ").concat(language);

       // Log.w("ExploreListAdapter.", String.format("Postion: %d. Langs: %s",position,chatLanguages));

        if (!chatLanguages.isEmpty()) {
            holder.expanded_hive_chatLanguages.invalidate();
            holder.expanded_hive_chatLanguages.setText(chatLanguages);
            holder.expanded_hive_chatLanguages.requestLayout();
        }

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
                //System.out.println("join!!!!");
                //String hiveNameURL =((String) ((TextView)v.findViewById(R.id.explore_list_item_name)).getTag());
                //controller.JoinHive(hiveNameURL);

                ViewHolder holder = (ViewHolder)v.getTag(R.id.Explore_ListViewHolder);

                //joined_hives.add(position);

                ((View)v.getParent()).findViewById(R.id.explore_chat_button2).setVisibility(View.VISIBLE);
                v.findViewById(R.id.explore_join_button).setVisibility(View.GONE);
                controller.JoinHive(holder.hive);
            }
        };

        if (!joined_hives.containsKey(hive.getNameUrl())) {
            convertView.findViewById(R.id.explore_join_button).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.explore_joining_frame).setVisibility(View.GONE);
            convertView.findViewById(R.id.explore_chat_button2).setVisibility(View.GONE);
        } else if (joined_hives.get(hive.getNameUrl())) {
            convertView.findViewById(R.id.explore_join_button).setVisibility(View.GONE);
            convertView.findViewById(R.id.explore_joining_frame).setVisibility(View.GONE);
            convertView.findViewById(R.id.explore_chat_button2).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.explore_join_button).setVisibility(View.GONE);
            convertView.findViewById(R.id.explore_joining_frame).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.explore_chat_button2).setVisibility(View.GONE);
        }

        convertView.findViewById(R.id.explore_join_button).setTag(R.id.BO_Hive,hive);
        convertView.findViewById(R.id.explore_joining_frame).setTag(R.id.BO_Hive,hive);
        convertView.findViewById(R.id.explore_chat_button2).setTag(R.id.BO_Hive,hive);

        convertView.findViewById(R.id.explore_join_button).setOnClickListener(expandedHiveDescriptionButtonClickListener);
        convertView.findViewById(R.id.explore_joining_frame).setOnClickListener(expandedHiveDescriptionButtonClickListener);
        convertView.findViewById(R.id.explore_chat_button2).setOnClickListener(expandedHiveDescriptionButtonClickListener);

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int scroll_position = firstVisibleItem + visibleItemCount;
        if (scroll_position != this.scroll_position)
            this.scroll_position = scroll_position;

        if ((active) && (this.explore.HasMore()) && ((this.hives_list_data == null) || (scroll_position >= (this.getCount() - 10))))
            this.explore.More();
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
        public TextView expanded_hive_chatLanguages;

        public Hive hive;

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
