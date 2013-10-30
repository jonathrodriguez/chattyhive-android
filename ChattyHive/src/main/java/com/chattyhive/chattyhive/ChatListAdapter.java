package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jonathan on 24/10/13.
 */
public class ChatListAdapter extends BaseAdapter {
    private Context _mContext;
    private int _layoutResourceId;
    private LayoutInflater _mInflater;
    private ChatMessage _data[] = null;
    private String _myName = "";

    private static final int TYPE_MSG_MULTICHAT_OTHER = 0;
    private static final int TYPE_MSG_MULTICHAT_ME = 1;
    private static final int TYPE_MSG_SINGLECHAT_OTHER = 2;
    private static final int TYPE_MSG_SINGLECHAT_ME = 3;
    private static final int TYPE_MSG_COUNT = 4;

    public ChatListAdapter(Context mContext, String myName, ChatMessage[] data) {

        this._mContext = mContext;
        this._mInflater = ((Activity) _mContext).getLayoutInflater();
        this._data = data;
        this._myName = myName;
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_MSG_MULTICHAT_OTHER;
        if (_data[position].user.equalsIgnoreCase(this._myName)) {
            type = TYPE_MSG_MULTICHAT_ME;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MSG_COUNT;
    }

    @Override
    public int getCount() {
        return this._data.length;
    }

    @Override
    public ChatMessage getItem(int position){
        return this._data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if(convertView==null){
            holder = new ViewHolder();
            switch (type) {
                case TYPE_MSG_MULTICHAT_OTHER:
                    convertView = this._mInflater.inflate(R.layout.multichat_message_other,parent,false);
                    holder.username = (TextView)convertView.findViewById(R.id.username);
                    holder.messageText = (TextView)convertView.findViewById(R.id.messageText);
                    holder.timeStamp = (TextView)convertView.findViewById(R.id.timeStamp);
                    holder.avatarThumbnail = (ImageView)convertView.findViewById(R.id.avatarThumbnail);
                    break;
                case TYPE_MSG_MULTICHAT_ME:
                    convertView = this._mInflater.inflate(R.layout.multichat_message_me,parent,false);
                    holder.username = (TextView)convertView.findViewById(R.id.username);
                    holder.messageText = (TextView)convertView.findViewById(R.id.messageText);
                    holder.timeStamp = (TextView)convertView.findViewById(R.id.timeStamp);
                    holder.avatarThumbnail = (ImageView)convertView.findViewById(R.id.avatarThumbnail);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        ChatMessage chatMessage = _data[position];

        holder.username.setText(chatMessage.user);
        holder.messageText.setText(chatMessage.message);
        holder.timeStamp.setText(chatMessage.timeStamp.toString());

        return convertView;
    }

    private static class ViewHolder {
        public TextView username;
        public TextView messageText;
        public TextView timeStamp;
        public ImageView avatarThumbnail;
    }
}
