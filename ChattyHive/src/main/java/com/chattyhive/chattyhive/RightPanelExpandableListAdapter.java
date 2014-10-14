package com.chattyhive.chattyhive;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by J.Guzmán on 24/09/2014.
 */

public class RightPanelExpandableListAdapter extends BaseExpandableListAdapter {
    private final SparseArray<RightPanelListItem> group;
    public LayoutInflater inflater;
    public Activity activity;
    private boolean flag;

    public RightPanelExpandableListAdapter(Activity act, SparseArray<RightPanelListItem> group) {
    //public RightPanelExpandableListAdapter(Activity act) {
        activity = act;
        this.group = group;
        inflater = act.getLayoutInflater();
        flag = true;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {
            activity.findViewById(R.id.menu_notexpanded_explora_img).setVisibility(View.INVISIBLE);
        }
    };

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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.right_panel_items_layout, null);
        }

        if(isExpanded){
            convertView.findViewById(R.id.menu_notexpanded_explora_img).setVisibility(View.INVISIBLE);
            convertView.findViewById(R.id.menu_notexpanded_home_img).setVisibility(View.INVISIBLE);
            ImageView imgv = (ImageView) convertView.findViewById(R.id.menu_flecha_imagen);
            imgv.setImageResource(R.drawable.ic_action_next_item_down);
        }else{
            convertView.findViewById(R.id.menu_notexpanded_explora_img).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.menu_notexpanded_home_img).setVisibility(View.VISIBLE);
            ImageView imgv = (ImageView) convertView.findViewById(R.id.menu_flecha_imagen);
            imgv.setImageResource(R.drawable.ic_action_next_item);
        }
        /*RightPanelListItem grupo = (RightPanelListItem) getGroup(groupPosition);
        ((CheckedTextView)convertView).setText(grupo.string);
        ((CheckedTextView)convertView).setChecked(isExpanded);*/
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.right_panel_subitems_layout, null);
        }
        /*text = (TextView) convertView.findViewById(R.id.right_panel_subitem_layout_text);
        text.setText(children);
        switch (groupPosition){
            case 0:
                switch (childPosition) {
                    case 0:
                        text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.menu_explore_blanco , 0, 0, 0);
                        break;
                    case 1:
                        text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.menu_explore_blanco, 0, 0, 0); //naranja????
                        break;
                    case 2:
                        text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.menu_chats_blanco , 0, 0, 0);
                        break;
                    case 3:
                        text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.menu_peticiones_blanco , 0, 0, 0);
                        break;
                }
                break;
        }*/

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                ImageView img1, img2;
                //Toast.makeText(activity, children, Toast.LENGTH_SHORT).show();
                if (flag == true){
                    flag = false;
                    //volver a redireccionar????
                }
                else if (flag == false) {
                    img1 = (ImageView) v.findViewById(R.id.menu_notexpanded_explora_img); // ??????
                    img2 = (ImageView) v.findViewById(R.id.menu_notexpanded_explora_img); // ??????
                    img1.setVisibility(View.GONE);
                    img2 = null;
                    flag = true;
                }*/
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}


