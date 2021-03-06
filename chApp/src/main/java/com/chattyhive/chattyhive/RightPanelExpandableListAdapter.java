package com.chattyhive.chattyhive;

import android.app.Activity;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by J.Guzmán on 24/09/2014.
 */

public class RightPanelExpandableListAdapter extends BaseExpandableListAdapter {
    private final SparseArray<RightPanelListItem> group;
    public LayoutInflater inflater;
    public Activity activity;
    private short expanded;
    private boolean friends = true;
    private boolean hivemates = true;
    private boolean notifications = true;

    public RightPanelExpandableListAdapter(Activity act, SparseArray<RightPanelListItem> group) {
        activity = act;
        this.group = group;
        inflater = act.getLayoutInflater();
    }

    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return group.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //LinearLayout ly= (LinearLayout) activity.findViewById(R.id.right_panel_items_layout);
        return group.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //LinearLayout ly= (LinearLayout) activity.findViewById(R.id.menu_subitems_layout);
        return group.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);

    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (groupPosition == 0){
                convertView = inflater.inflate(R.layout.right_panel_items_layout, null);
            if(isExpanded){
                convertView.findViewById(R.id.menu_notexpanded_explora_img).setVisibility(View.INVISIBLE);
                convertView.findViewById(R.id.menu_notexpanded_home_img).setVisibility(View.INVISIBLE);
                ImageView imgv = (ImageView) convertView.findViewById(R.id.menu_flecha_imagen);
                imgv.setImageResource(R.drawable.ic_action_next_item_down);
                convertView.findViewById(R.id.right_panel_items_layout).setBackgroundResource(R.drawable.borde2px);
            }else{
                convertView.findViewById(R.id.menu_notexpanded_explora_img).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.menu_notexpanded_home_img).setVisibility(View.VISIBLE);
                ImageView imgv = (ImageView) convertView.findViewById(R.id.menu_flecha_imagen);
                imgv.setImageResource(R.drawable.ic_action_next_item);
                convertView.findViewById(R.id.right_panel_items_layout).setBackgroundResource(R.drawable.borde2pxhide);
                convertView.findViewById(R.id.menu_notexpanded_home_img).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ((Main) activity).ShowHome();
                    }
                });
                convertView.findViewById(R.id.menu_notexpanded_explora_img).setOnClickListener(((Main) activity).explore_button_click);
            }
        }

        if (groupPosition == 1){
                convertView = inflater.inflate(R.layout.right_panel_options, null);
                if (isExpanded){
                    ImageView imgv = (ImageView) convertView.findViewById(R.id.menu_flecha_imagen2);
                    imgv.setImageResource(R.drawable.ic_action_next_item_down);
                    convertView.findViewById(R.id.right_panel_options).setBackgroundResource(R.drawable.borde2px);
                }else{
                    ImageView imgv = (ImageView) convertView.findViewById(R.id.menu_flecha_imagen2);
                    imgv.setImageResource(R.drawable.ic_action_next_item);
                    convertView.findViewById(R.id.right_panel_options).setBackgroundResource(R.drawable.borde2pxhide);
                }

        }
        if (groupPosition == 2){
            convertView = inflater.inflate(R.layout.right_panel_hive, null);
            if (isExpanded){
                ImageView imgv = (ImageView) convertView.findViewById(R.id.menu_flecha_imagen3);
                imgv.setImageResource(R.drawable.ic_action_next_item_down);
                //convertView.findViewById(R.id.right_panel_hive).setBackgroundResource(R.drawable.borde2px);
            }else{
                ImageView imgv = (ImageView) convertView.findViewById(R.id.menu_flecha_imagen3);
                imgv.setImageResource(R.drawable.ic_action_next_item);
                //convertView.findViewById(R.id.right_panel_hive).setBackgroundResource(R.drawable.borde2pxhide);
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        if(groupPosition==0) {
                convertView = inflater.inflate(R.layout.right_panel_subitems_layout, null);

            convertView.findViewById(R.id.menu_layout_inicio).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ((Main) activity).ShowHome();
                }
            });

            convertView.findViewById(R.id.menu_layout_chats).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ((Main) activity).ShowChats();
                }
            });
            convertView.findViewById(R.id.menu_new_hive).setOnClickListener(((Main)activity).new_hive_button_click);
            convertView.findViewById(R.id.menu_layout_explora).setOnClickListener(((Main) activity).explore_button_click);

            convertView.findViewById(R.id.menu_layout_logout).setOnClickListener(((Main) activity).logout_button_click);

            convertView.findViewById(R.id.menu_bell).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (notifications == true) {
                        ((ImageView) v.findViewById(R.id.menu_bell_img)).setImageResource(R.drawable.menu_notificaciones_off_blanco);
                        (((View)v.getParent().getParent()).findViewById(R.id.menu_check_buttons)).setVisibility(View.GONE);
                        (((View)v.getParent().getParent()).findViewById(R.id.menu_notificaciones_off)).setVisibility(View.VISIBLE);
                        notifications = false;
                    }
                    else if (notifications == false) {
                        ((ImageView) v.findViewById(R.id.menu_bell_img)).setImageResource(R.drawable.menu_notificaciones_on_blanco);
                        (((View)v.getParent().getParent()).findViewById(R.id.menu_check_buttons)).setVisibility(View.VISIBLE);
                        (((View)v.getParent().getParent()).findViewById(R.id.menu_notificaciones_off)).setVisibility(View.GONE);
                        notifications = true;
                    }
                }
            });

            convertView.findViewById(R.id.menu_friends).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (friends == true) {
                        ((ImageView) v.findViewById(R.id.menu_friends_checkbox)).setImageResource(R.drawable.menu_white_tick_deactivated_grey);
                        friends = false;
                    }
                    else if (friends == false) {
                        ((ImageView) v.findViewById(R.id.menu_friends_checkbox)).setImageResource(R.drawable.menu_white_tick_activated_grey);
                        friends = true;
                    }
                }
            });
            convertView.findViewById(R.id.menu_hivemates).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (hivemates == true) {
                        ((ImageView) v.findViewById(R.id.menu_hivemates_checkbox)).setImageResource(R.drawable.menu_white_tick_deactivated_grey);
                        hivemates = false;
                    }
                    else if (hivemates == false) {
                        ((ImageView) v.findViewById(R.id.menu_hivemates_checkbox)).setImageResource(R.drawable.menu_white_tick_activated_grey);
                        hivemates = true;
                    }
                }
            });
        }
        if (groupPosition == 1){
                convertView = inflater.inflate(R.layout.right_panel_subitems_options, null);
        }
        if (groupPosition == 2){
                convertView = inflater.inflate(R.layout.right_panel_subitems_hive, null);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}


