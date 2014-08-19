package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.GroupKind;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.util.events.EventArgs;

/**
 * Created by Jonathan on 13/03/14.
 */
public class LeftPanelListAdapter extends BaseAdapter {

    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    private int visibleList;

    private View.OnClickListener clickListener;

    public void SetVisibleList(int LeftPanel_ListKind) { this.visibleList = LeftPanel_ListKind; this.OnAddItem(this,EventArgs.Empty()); }
    public  int GetVisibleList()                         { return this.visibleList; }

    public void SetOnClickListener (View.OnClickListener listener) { this.clickListener = listener; notifyDataSetChanged(); }

    public void OnAddItem(Object sender, EventArgs args) {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public LeftPanelListAdapter (Context activityContext) {
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

        if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            return Hive.getHiveCount();
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            return Group.getGroupCount();
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
            return 0;
        }

        return 0;
    }

    @Override
    public Object getItem(int position){

        if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            return Hive.getHiveByIndex(position);
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            return Group.getGroupByIndex(position);
        } else if (this.visibleList == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
            return null;
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
        if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_None)) { return null; }
        if (convertView==null) {
            if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
                holder = new HiveViewHolder();
                convertView = this.inflater.inflate(R.layout.left_panel_hives_list_item,parent,false);
                ((HiveViewHolder)holder).hiveItem = (LinearLayout)convertView.findViewById((R.id.left_panel_hives_list_item_top_view));
                ((HiveViewHolder)holder).hiveName = (TextView)convertView.findViewById(R.id.left_panel_hives_list_item_hive_name);
                ((HiveViewHolder)holder).hiveImage = (ImageView)convertView.findViewById(R.id.left_panel_hives_list_item_img);
                ((HiveViewHolder)holder).hiveItem.setOnClickListener(clickListener);
            } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
                holder = new ChatViewHolder();
                convertView = this.inflater.inflate(R.layout.left_panel_chat_list_item,parent,false);

                ((ChatViewHolder)holder).chatItem = (LinearLayout)convertView.findViewById((R.id.left_panel_chat_list_item_top_view));
                ((ChatViewHolder)holder).chatName = (TextView)convertView.findViewById(R.id.left_panel_chat_list_item_chat_name);
                ((ChatViewHolder)holder).chatLastMessage = (TextView)convertView.findViewById(R.id.left_panel_chat_list_item_last_message);
                ((ChatViewHolder)holder).chatImage = (ImageView)convertView.findViewById(R.id.left_panel_chat_list_item_big_img);
                ((ChatViewHolder)holder).chatItem.setOnClickListener(clickListener);
            } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {
                //convertView = this.inflater.inflate(R.layout.main_panel_chat_hive_message_me,parent,false);
                holder = new MateViewHolder();
            }

            if (convertView != null)
                convertView.setTag(R.id.LeftPanel_ListViewHolder,holder);
        } else {
            holder = (ViewHolder)convertView.getTag(R.id.LeftPanel_ListViewHolder);
        }

        Object item = this.getItem(position);

        if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Hives)) {
            ((HiveViewHolder)holder).hiveName.setText(((Hive)item).getName());
            ((HiveViewHolder)holder).hiveItem.setTag(R.id.BO_Hive,item);
        } else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Chats)) {
            String GroupName = "";
            if ((((Group)item).getGroupKind() == GroupKind.PRIVATE_SINGLE) || (((Group)item).getGroupKind() == GroupKind.PUBLIC_SINGLE)) {
                for (User user : ((Group) item).getMembers())
                    if (!user.isMe()) GroupName = user.getShowingName();
            } else if ((((Group)item).getGroupKind() == GroupKind.HIVE) && (((Group)item).getParentHive() != null)) {
                GroupName = ((Group)item).getParentHive().getName();
            } else {
                GroupName = ((Group)item).getName();
            }
            ((ChatViewHolder)holder).chatName.setText(GroupName);
            ((ChatViewHolder)holder).chatLastMessage.setText(((Group)item).getChat().getLastMessage().getMessageContent().getContent());
            ((ChatViewHolder)holder).chatItem.setTag(R.id.BO_Chat,item);
        } /*else if (type == context.getResources().getInteger(R.integer.LeftPanel_ListKind_Mates)) {

        }*/

        return convertView;
    }

    private abstract class ViewHolder{}

    private class HiveViewHolder extends ViewHolder {
        public LinearLayout hiveItem;
        public TextView hiveName;
        public ImageView hiveImage;
    }

    private class ChatViewHolder extends ViewHolder {
        public LinearLayout chatItem;
        public TextView chatName;
        public TextView chatLastMessage;
        public ImageView chatImage;
    }

    private class MateViewHolder extends ViewHolder {

    }
}
