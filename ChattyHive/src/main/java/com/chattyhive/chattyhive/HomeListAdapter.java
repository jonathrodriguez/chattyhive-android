package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Home.Cards.HiveMessageCard;
import com.chattyhive.backend.businessobjects.Home.HomeCard;
import com.chattyhive.backend.businessobjects.Home.HomeCardType;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.backend.util.formatters.TimestampFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jonathan on 07/10/2014.
 */
public class HomeListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private Controller controller;
    private ArrayList<HomeCard> homeCards;

    public HomeListAdapter(Context context) {
        this.context = context;
        this.inflater = ((Activity)context).getLayoutInflater();
        this.controller = ((Main)this.context).controller;

        this.controller.HomeReceived.add(new EventHandler<EventArgs>(this,"onHomeChanged",EventArgs.class));

        this.homeCards = new ArrayList<HomeCard>(this.controller.getHomeCards());
        this.notifyDataSetChanged();
    }

    public void onHomeChanged(Object sender, EventArgs eventArgs) { //TODO: this is only a patch. HomeCard collection in controller must be updated on UIThread.
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                homeCards = null;
                while (homeCards == null)
                    try { homeCards = new ArrayList<HomeCard>(controller.getHomeCards()); } catch (Exception e) { homeCards = null; }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if ((this.homeCards == null) || (this.homeCards.size() <= position)) throw new ArrayIndexOutOfBoundsException("Requested item outside bounds of the array.");
        HomeCard hc = this.homeCards.get(position);
        return hc.getCardType().ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return HomeCardType.values().length;
    }

    @Override
    public int getCount() {
        if (this.homeCards == null)
            return 0;
        else
            return this.homeCards.size();
    }

    @Override
    public Object getItem(int position) {
        if ((this.homeCards == null) || (this.homeCards.size() <= position))
            return null;
        else
            return this.homeCards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        HomeCardType type = HomeCardType.values()[getItemViewType(position)];

        if(convertView==null){
            switch (type) {
                case HiveMessage:
                    convertView = this.inflater.inflate(R.layout.home_card_message_hive,parent,false);
                    holder = new ViewHolder_HiveMessage((HiveMessageCard)this.homeCards.get(position));
                    ((ViewHolder_HiveMessage)holder).setHiveName((TextView) convertView.findViewById(R.id.home_card_message_hive_name_hive));
                    ((ViewHolder_HiveMessage)holder).setUserName((TextView) convertView.findViewById(R.id.home_card_message_hive_name_user));
                    ((ViewHolder_HiveMessage)holder).setTimeStamp((TextView) convertView.findViewById(R.id.home_card_message_hive_timestamp));
                    ((ViewHolder_HiveMessage)holder).setMessage((TextView) convertView.findViewById(R.id.home_card_message_hive_message));
                    //TODO: Set ImageViews.
                    ((ViewHolder_HiveMessage)holder).setHiveImage((ImageView)convertView.findViewById(R.id.home_card_message_hive_image_hive));
                    ((ViewHolder_HiveMessage)holder).setUserImage((ImageView)convertView.findViewById(R.id.home_card_message_hive_image_user));
                    break;
                default:
                    return null;
            }
            convertView.setOnClickListener(holder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
            holder.setCard(this.homeCards.get(position));
        }

        int marginTop = (int)context.getResources().getDimension(R.dimen.home_card_margin_top);
        int marginLeft = (int)context.getResources().getDimension(R.dimen.home_card_margin_left);
        int marginRight = (int)context.getResources().getDimension(R.dimen.home_card_margin_right);
        int marginBottom =  0;

        if (position == (this.homeCards.size()-1))
            marginBottom = (int)context.getResources().getDimension(R.dimen.home_list_padding_bottom);

        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            convertView.postInvalidate();
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(marginLeft, marginTop, marginRight, marginBottom);
            convertView.setLayoutParams(layoutParams);
        }

        return convertView;
    }

    private static abstract class ViewHolder implements View.OnClickListener {
        HomeCard card;
        public HomeCard getCard() {
            return card;
        }
        public abstract void setCard(HomeCard card);
    }

    private class ViewHolder_HiveMessage extends ViewHolder {

        private TextView HiveName;
        private ImageView HiveImage;
        private TextView UserName;
        private ImageView UserImage;
        private TextView TimeStamp;
        private TextView Message;

        public void setHiveName(TextView hiveName) {
            this.HiveName = hiveName;
            this.updateHiveName((HiveMessageCard)this.card);
        }
        public void setUserName(TextView userName) {
            this.UserName = userName;
            this.updateUserName((HiveMessageCard) this.card);
        }
        public void setTimeStamp(TextView timeStamp) {
            this.TimeStamp = timeStamp;
            this.updateTimeStamp((HiveMessageCard) this.card);
        }
        public void setMessage(TextView message) {
            this.Message = message;
            this.updateMessage((HiveMessageCard) this.card);
        }

        public void setHiveImage(ImageView hiveImage) {
            this.HiveImage = hiveImage;
            this.updateHiveImage((HiveMessageCard) this.card);
        }
        public void setUserImage(ImageView userImage) {
            this.UserImage = userImage;
            this.updateUserImage((HiveMessageCard)this.card);
        }

        public ViewHolder_HiveMessage(HiveMessageCard card) {
            this.setCard(card);
        }

        @Override
        public void setCard(HomeCard card) {
            if (card.getCardType() != HomeCardType.HiveMessage) throw new IllegalArgumentException("Expected HiveMessageCard.");
            this.card = card;
            this.updateFields();
        }

        @Override
        public void onClick(View v) {

        }

        private void updateFields() {
            if (this.card == null) return;

            HiveMessageCard hc = (HiveMessageCard)this.card;

            this.updateHiveName(hc);
            this.updateUserName(hc);
            this.updateTimeStamp(hc);
            this.updateMessage(hc);
            this.updateHiveImage(hc);
            this.updateUserImage(hc);
        }

        private void updateHiveName(HiveMessageCard hc) {
            if (hc == null) return;
            if (this.HiveName != null)
                this.HiveName.setText(context.getResources().getString(R.string.hivename_identifier_character).concat(hc.getHive().getName()));
        }

        private void updateUserName(HiveMessageCard hc) {
            if (hc == null) return;
            if (this.UserName != null) {
                try {
                    this.UserName.setText(context.getResources().getString(R.string.public_username_identifier_character).concat(hc.getMessage().getUser().getUserPublicProfile().getShowingName()));
                    this.UserName.setTextColor(Color.parseColor(hc.getMessage().getUser().getUserPublicProfile().getColor()));
                } catch (Exception e) {

                }
            }
        }

        private void updateTimeStamp(HiveMessageCard hc) {
            if (hc == null) return;
            if (this.TimeStamp != null) {
                String LastMessageTimestamp = "";
                Date timeStamp = hc.getMessage().getOrdinationTimeStamp();
                Date fiveMinutesAgo = new Date((new Date()).getTime() - 5*60*1000);
                Date today = DateFormatter.toDate(DateFormatter.toString(timeStamp));
                Calendar yesterday = Calendar.getInstance();
                yesterday.setTime(today);
                yesterday.roll(Calendar.DAY_OF_MONTH, false);
                if (timeStamp.after( fiveMinutesAgo ))
                    LastMessageTimestamp = context.getString(R.string.left_panel_imprecise_time_now);
                else if (timeStamp.after(today))
                    LastMessageTimestamp = TimestampFormatter.toLocaleString(timeStamp);
                else if (timeStamp.after(yesterday.getTime()))
                    LastMessageTimestamp = context.getString(R.string.left_panel_imprecise_time_yesterday);
                else
                    LastMessageTimestamp = DateFormatter.toHumanReadableString(timeStamp);

                this.TimeStamp.setText(LastMessageTimestamp);
            }
        }

        private void updateMessage(HiveMessageCard hc) {
            if (hc == null) return;
            if (this.Message != null)
                this.Message.setText((hc.getMessage().getMessageContent().getContentType().equalsIgnoreCase("TEXT"))?hc.getMessage().getMessageContent().getContent():hc.getMessage().getMessageContent().getContentType());
        }

        private void updateHiveImage(HiveMessageCard hc) {
            if (hc == null) return;
            if ((this.HiveImage != null) && (hc.getHive().getHiveImage() != null)) {
                hc.getHive().getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"onHiveImageLoaded",EventArgs.class));
                hc.getHive().getHiveImage().loadImage(Image.ImageSize.small,0);
            }
        }

        public void onHiveImageLoaded(Object sender, EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final ViewHolder_HiveMessage thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.small,0);
                    if (is != null) {
                        HiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"onHiveImageLoaded",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }

        private void updateUserImage(HiveMessageCard hc) {
            if (hc == null) return;
            if ((this.HiveImage != null) && (hc.getMessage().getUser().getUserPublicProfile().getProfileImage() != null)) {
                hc.getMessage().getUser().getUserPublicProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(this,"onUserImageLoaded",EventArgs.class));
                hc.getMessage().getUser().getUserPublicProfile().getProfileImage().loadImage(Image.ImageSize.small,0);
            }
        }

        public void onUserImageLoaded(Object sender, EventArgs eventArgs) {
            if (!(sender instanceof Image)) return;

            final Image image = (Image)sender;
            final ViewHolder_HiveMessage thisViewHolder = this;

            ((Activity)context).runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    InputStream is = image.getImage(Image.ImageSize.small,0);
                    if (is != null) {
                        UserImage.setImageBitmap(BitmapFactory.decodeStream(is));
                        try {
                            is.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisViewHolder,"onUserImageLoaded",EventArgs.class));
                    //image.freeMemory();
                }
            });
        }
    }
}
