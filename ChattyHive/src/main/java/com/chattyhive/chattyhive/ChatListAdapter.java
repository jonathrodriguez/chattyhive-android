package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.bussinesobjects.Message;

import java.util.ArrayList;

/**
 * Created by Jonathan on 24/10/13.
 */
public class ChatListAdapter extends BaseAdapter {
    private Context _mContext;
    private int _layoutResourceId;
    private LayoutInflater _mInflater;
    private ArrayList<Message> _data = new ArrayList<Message>();
    private String _myName = "";
    private Boolean _multichat = true;

    private static final int TYPE_MSG_MULTICHAT_OTHER = 0;
    private static final int TYPE_MSG_MULTICHAT_ME = 1;
    private static final int TYPE_MSG_SINGLECHAT_OTHER = 2;
    private static final int TYPE_MSG_SINGLECHAT_ME = 3;
    private static final int TYPE_MSG_COUNT = 4;

    public ChatListAdapter(Context mContext, String myName, Boolean Multichat, Message[] data) {
        this(mContext, myName, Multichat);

        for (Message message : data) {
            this._data.add(message);
        }
    }

    public ChatListAdapter(Context mContext, String myName, Message[] data) {
        this(mContext,myName,true,data);
    }

    public ChatListAdapter(Context mContext, String myName, Boolean Multichat) {

        this._mContext = mContext;
        this._mInflater = ((Activity) _mContext).getLayoutInflater();
        this._myName = myName;
        this._multichat = Multichat;
    }

    public ChatListAdapter(Context mContext, String myName) {
        this(mContext,myName,true);
    }

    @Override
    public int getItemViewType(int position) {
        int type = (this._multichat)?TYPE_MSG_MULTICHAT_OTHER:TYPE_MSG_SINGLECHAT_OTHER;
        if (_data.get(position).getUser().getUsername().equalsIgnoreCase(this._myName)) {
            type = (this._multichat)?TYPE_MSG_MULTICHAT_ME:TYPE_MSG_SINGLECHAT_ME;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MSG_COUNT;
    }

    @Override
    public int getCount() {
        return this._data.size();
    }

    public void addItem(Message message) {
        this._data.add(message);
        this.notifyDataSetChanged();
    }

    @Override
    public Message getItem(int position){
        return this._data.get(position);
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

        Message message = this._data.get(position);

        holder.username.setText(message.getUser().getUsername());
        holder.messageText.setText(message.getMessage().getContent());
        holder.timeStamp.setText(message.getTimeStamp().toString());

        return convertView;
    }

    private static class ViewHolder {
        public TextView username;
        public TextView messageText;
        public TextView timeStamp;
        public ImageView avatarThumbnail;
    }
}
