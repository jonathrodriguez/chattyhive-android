package com.chattyhive.chattyhive.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.chattyhive.Main;
import com.chattyhive.chattyhive.MainChat;
import com.chattyhive.chattyhive.R;

/**
 * Created by jonathan on 28/06/2015.
 */
public class LeftPanelHiveUserListChatViewHolder extends ViewHolder<Chat> {
    TextView leftPanelHiveUserListChatLastMessage;

    public LeftPanelHiveUserListChatViewHolder(Context context, BaseAdapter baseAdapter,View containerView) {
        super(context,baseAdapter,containerView);
    }
    public LeftPanelHiveUserListChatViewHolder(Context context, BaseAdapter baseAdapter,View containerView, Chat item) {
        super(context,baseAdapter,containerView, item);
    }

    @Override
    public void setContainerView(View containerView) {
        leftPanelHiveUserListChatLastMessage = (TextView)containerView.findViewById(R.id.left_panel_hive_user_list_chat_last_message);

        super.setContainerView(containerView);
    }

    @Override
    protected void updateView() {
        if ((containerView == null) || (item == null))
            return;

        leftPanelHiveUserListChatLastMessage.setText(item.getConversation().getLastMessage().getMessageContent().getContent());

        this.containerView.setOnClickListener(publicChatClickListener);
    }

    private View.OnClickListener publicChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LeftPanelHiveUserListChatViewHolder.this.item != null) {
                ((Main)context).OpenWindow(new MainChat(context, LeftPanelHiveUserListChatViewHolder.this.item));
            }
        }
    };
}
