package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.chattyhive.ViewHolders.LeftPanelHiveUserListChatViewHolder;
import com.chattyhive.chattyhive.ViewHolders.LeftPanelHiveUserListUserViewHolder;

/**
 * Created by jonathan on 27/06/2015.
 */
public class LeftPanelHiveUserListAdapter extends PaginationList {

    Activity contextActivity;
    LayoutInflater inflater;
    Hive hive;
    Hive.HiveUsersType hiveUsersType;
    int lastPage = 0;
    boolean allPagesLoaded = false;
    int lastItemCount = 0;


    public LeftPanelHiveUserListAdapter(Context context, ViewGroup listView, Hive hive, Hive.HiveUsersType hiveUsersType) {
        super(listView);

        this.hive = hive;
        this.hiveUsersType = hiveUsersType;

        if (context instanceof Activity)
            this.contextActivity = ((Activity) context);
        else
            throw new IllegalStateException("Expected an activity as context.");


            this.inflater = this.contextActivity.getLayoutInflater();

        this.hive.OnSubscribedUsersListUpdated.add(new EventHandler<EventArgs>(this,"onAddItem",EventArgs.class));
        this.hive.requestUsers(0,11,this.hiveUsersType,"");
        // TODO: here show the "loading" animation

/*        this.contextActivity.runOnUiThread(new Runnable() { //This is for preloading the chat card
            @Override
            public void run() {
                LeftPanelHiveUserListAdapter.this.loadNextPage(0);
            }
        });*/
    }

    public void onAddItem(Object sender,EventArgs eventArgs) {
        int actualItemCount = hive.getSubscribedUsers().size();
        if ((actualItemCount == lastItemCount) || (actualItemCount < 11)) {
            allPagesLoaded = true;
            lastPage = (int)Math.ceil(actualItemCount/this.getItemCountPerPage());
            if (this.getPage() > lastPage) {
                this.contextActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: here hide the "loading" animation
                        LeftPanelHiveUserListAdapter.this.loadNextPage(0);
                    }
                });
                return;
            }
        } else {
            lastItemCount = actualItemCount;
        }
        if (this.getPage() == 0) {
            this.contextActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO: here hide the "loading" animation
                    LeftPanelHiveUserListAdapter.this.notifyPageChanged();
                }
            });
        } else if (this.getPage() >= 5) {
            this.contextActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO: here hide the "loading" animation
                    LeftPanelHiveUserListAdapter.this.loadNextPage(LeftPanelHiveUserListAdapter.this.getPage()+1);
                }
            });
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    } // 0 -> Public Chat. 1 -> User. 2 -> Empty hive.

    @Override
    public int getItemViewType(int position) {
        return ((position==0)?0:((hive.getSubscribedUsers().size() > 0)?1:2));
    }

    @Override
    public int getItemCountInThisPage() {
        int res = 0;

        int users = hive.getSubscribedUsers().size();

        if (users == 0)
            users = 2; //The public chat and the no users card.
        else
            users += 1; //Add the public chat.

        if ((this.getPage()+1) > (users/this.getItemCountPerPage()))
            res = (users % this.getItemCountPerPage());
        else
            res = this.getItemCountPerPage();

        return res;
    }

    @Override
    public int getItemCountPerPage() {
        return 4;
    }

    @Override
    public int getItemHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) this.listView.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return (int)Math.ceil((72 + 8.5) * displayMetrics.scaledDensity);
    }

    @Override
    public int getCount() {
        return this.hive.getSubscribedUsers().size();
    }

    @Override
    public Object getItem(int position) {
        if (position == 0)
            return hive.getPublicChat();
        else if (position <= this.getCount())
            return hive.getSubscribedUsers().get(position-1);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            switch (this.getItemViewType(position)) {
                case 0:
                    convertView = inflater.inflate(R.layout.left_panel_hive_user_list_chat_card,parent,false);
                    LeftPanelHiveUserListChatViewHolder leftPanelHiveUserListChatViewHolder = new LeftPanelHiveUserListChatViewHolder(this.contextActivity,this,convertView,hive.getPublicChat());
                    convertView.setTag(leftPanelHiveUserListChatViewHolder);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.left_panel_hive_user_list_user_card,parent,false);
                    LeftPanelHiveUserListUserViewHolder leftPanelHiveUserListUserViewHolder = new LeftPanelHiveUserListUserViewHolder(this.contextActivity,this,convertView,hive.getSubscribedUsers().get(position-1).getUser(),hive);
                    convertView.setTag(leftPanelHiveUserListUserViewHolder);
                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.left_panel_chat_context_no_user,parent,false);
                    break;
            }
        } else {
            switch (this.getItemViewType(position)) {
                case 0:
                    LeftPanelHiveUserListChatViewHolder leftPanelHiveUserListChatViewHolder = (LeftPanelHiveUserListChatViewHolder)convertView.getTag();
                    leftPanelHiveUserListChatViewHolder.setItem(hive.getPublicChat());
                    break;
                case 1:
                    LeftPanelHiveUserListUserViewHolder leftPanelHiveUserListUserViewHolder = (LeftPanelHiveUserListUserViewHolder)convertView.getTag();
                    leftPanelHiveUserListUserViewHolder.setItem(hive.getSubscribedUsers().get(position - 1).getUser());
                    break;
            }
        }
        return convertView;
    }

    public void loadNextPage() {
        int actualPage = this.getPage();
        if ((actualPage == lastPage) && (allPagesLoaded)) {
            this.contextActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LeftPanelHiveUserListAdapter.this.loadNextPage(0);
                }
            });
        } else if (allPagesLoaded) {
            this.contextActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LeftPanelHiveUserListAdapter.this.loadNextPage(LeftPanelHiveUserListAdapter.this.getPage() + 1);
                }
            });
        } else if ((actualPage>=1) && (actualPage<4)) {
            this.hive.requestUsers(this.hive.getSubscribedUsers().size(), this.getItemCountPerPage(), this.hiveUsersType,"");
            this.contextActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LeftPanelHiveUserListAdapter.this.loadNextPage(LeftPanelHiveUserListAdapter.this.getPage() + 1);
                }
            });
        } else if (actualPage>=5) {
            this.hive.requestUsers(this.hive.getSubscribedUsers().size(), this.getItemCountPerPage(), this.hiveUsersType,"");
            // TODO: here show the "loading" animation
        } else {
            this.contextActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LeftPanelHiveUserListAdapter.this.loadNextPage(LeftPanelHiveUserListAdapter.this.getPage() + 1);
                }
            });
        }
    }

    public void dispose() {
        this.free();
        this.hive.OnSubscribedUsersListUpdated.remove(new EventHandler<EventArgs>(this, "onAddItem", EventArgs.class));
    }
}
