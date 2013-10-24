package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Jonathan on 24/10/13.
 */
public class Multichat_ArrayAdapter extends ArrayAdapter<Multichat_MessageClass> {
    Context _mContext;
    int _layoutResourceId;
    Multichat_MessageClass _data[] = null;

    public Multichat_ArrayAdapter(Context mContext, int layoutResourceId, Multichat_MessageClass[] data) {
        super(mContext, layoutResourceId, data);

        this._layoutResourceId = layoutResourceId;
        this._mContext = mContext;
        this._data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) _mContext).getLayoutInflater();
            convertView = inflater.inflate(_layoutResourceId, parent, false);
        }

        // object item based on the position
        Multichat_MessageClass multichat_message = _data[position];

        // get the TextView and then set the text (item name) and tag (item ID) values
        /*TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
        textViewItem.setText(objectItem.itemName);
        textViewItem.setTag(objectItem.itemId);*/

        return convertView;
    }
}
