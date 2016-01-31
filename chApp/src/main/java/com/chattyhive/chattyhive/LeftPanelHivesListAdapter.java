package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.chattyhive.ViewHolders.LeftPanelHiveViewHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

/**
 * Created by jonathan on 21/06/2015.
 */
public class LeftPanelHivesListAdapter extends BaseAdapter {
    private Context context;
    private ListView listView;
    private LayoutInflater inflater;
    public Event<EventArgs> ListSizeChanged;
    public ArrayList<Hive> hiveList;
    private int expandedItem = -1;

    public LeftPanelHivesListAdapter(Context context) {
        super();
        this.context = context;
        this.ListSizeChanged = new Event<EventArgs>();
        this.inflater = ((Activity) this.context).getLayoutInflater();
        //this.listView = ((ListView) ((Activity) this.context).findViewById(R.id.left_panel_element_list));
        //this.listView.setAdapter(this);

        CaptureHives();

        notifyDataSetChanged();
    }

    private void CaptureHives() {
        TreeSet<Hive> list = new TreeSet<Hive>(new Comparator<Hive>() {
            @Override
            public int compare(Hive lhs, Hive rhs) { // lhs < rhs => return < 0 | lhs = rhs => return = 0 | lhs > rhs => return > 0
                int res = 0;
                if ((lhs == null) && (rhs != null))
                    res = 1;
                else if ((lhs != null) && (rhs == null))
                    res = -1;
                else if (lhs == null) //&& (rhs == null)) <- Which is always true
                    res = 0;
                else {
                    Date lhsDate = null;
                    Date rhsDate = null;

                    //TODO: Change comparison method. Instead of creation date use lastLocalUserActivityDate. Find a way to determine this value.

                    /*if ((lhs.getPublicChat().getConversation() != null) && (lhs.getPublicChat().getConversation().getCount() > 0) && (lhs.getPublicChat().getConversation().getLastMessage() != null))
                        lhsDate = lhs.getPublicChat().getConversation().getLastMessage().getOrdinationTimeStamp();
                    else if (lhs.getCreationDate() != null)*/
                    lhsDate = lhs.getCreationDate();

                   /* if ((rhs.getPublicChat().getConversation() != null) && (rhs.getPublicChat().getConversation().getCount() > 0) && (rhs.getPublicChat().getConversation().getLastMessage() != null))
                        rhsDate = rhs.getPublicChat().getConversation().getLastMessage().getOrdinationTimeStamp();
                    else if (rhs.getCreationDate() != null)*/
                    rhsDate = rhs.getCreationDate();

                    if ((lhsDate == null) && (rhsDate != null))
                        res = 1;
                    else if ((lhsDate != null) && (rhsDate == null))
                        res = -1;
                    else if (lhsDate != null) //&& (rhsDate != null)) <- Which is always true
                        res = rhsDate.compareTo(lhsDate);
                    else {
                        lhsDate = lhs.getCreationDate();
                        rhsDate = rhs.getCreationDate();

                        if ((lhsDate == null) && (rhsDate != null))
                            res = 1;
                        else if ((lhsDate != null) && (rhsDate == null))
                            res = -1;
                        else if ((lhsDate != null) && (rhsDate != null))
                            res = rhsDate.compareTo(lhsDate);
                        else {
                            res = 0;
                        }
                    }
                }

                return res;
            }
        });
        list.addAll(Hive.getHives());
        hiveList = new ArrayList<Hive>(list);
    }

    public void OnAddItem(Object sender, EventArgs args) {  //TODO: This is only a patch. Hive and Chat collections must be updated on UIThread.
        ((Activity) this.context).runOnUiThread(new Runnable() {
            public void run() {
                hiveList = null;

                while (hiveList == null)
                    try {
                        CaptureHives();
                    } catch (Exception e) {
                        hiveList = null;
                    }


                notifyDataSetChanged();
                if ((ListSizeChanged != null) && (ListSizeChanged.count() > 0))
                    ListSizeChanged.fire(this, EventArgs.Empty());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return (hiveList != null)?hiveList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return ((hiveList != null) && (!hiveList.isEmpty()))?hiveList.get(position):null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.left_panel_hives_list_item,parent,false);
            LeftPanelHiveViewHolder leftPanelHiveViewHolder = new LeftPanelHiveViewHolder(this.context,this,convertView,((Hive)this.getItem(position)),position);
            convertView.setTag(leftPanelHiveViewHolder);
        } else {
            LeftPanelHiveViewHolder leftPanelHiveViewHolder = (LeftPanelHiveViewHolder)convertView.getTag();

            if (position != this.expandedItem)
                leftPanelHiveViewHolder.collapseCard();

            leftPanelHiveViewHolder.setItem((Hive)this.getItem(position));
        }
        return convertView;
    }

    public void setExpandedItem(int expandedItem) {
        if (this.expandedItem == expandedItem) return;
        this.expandedItem = expandedItem;
        ((Activity) this.context).runOnUiThread(new Runnable() {
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
