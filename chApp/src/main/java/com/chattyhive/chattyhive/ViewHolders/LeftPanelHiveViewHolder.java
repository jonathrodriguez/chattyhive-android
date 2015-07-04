package com.chattyhive.chattyhive.ViewHolders;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.chattyhive.chattyhive.LeftPanelHiveUserListAdapter;
import com.chattyhive.chattyhive.LeftPanelHivesListAdapter;
import com.chattyhive.chattyhive.Main;
import com.chattyhive.chattyhive.MainChat;
import com.chattyhive.chattyhive.R;
import com.chattyhive.chattyhive.WrapLayout;
import com.chattyhive.chattyhive.util.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by jonathan on 21/06/2015.
 */
public class LeftPanelHiveViewHolder extends ViewHolder<Hive> {
    private enum HiveItemStatus {Collapsed, Description, List}

    private HiveItemStatus itemStatus = HiveItemStatus.Collapsed;
    private int position;

    /*   HIVE COLLAPSED */
    private View leftPanelCollapsedCard;
    private ImageView leftPanelHivesListItemImage;
    private static Image.ImageSize CollapsedImageSize = Image.ImageSize.medium;
    private TextView leftPanelHivesListItemHiveName;
    private TextView leftPanelHivesListItemHiveDescription;
    private ImageView leftPanelHivesListItemHiveCategroyImg;
    private TextView leftPanelHivesListItemHiveCategory;
    //private ImageView leftPanelHivesListItemHiveSubscribedUsersImg; // This is not needed.
    private TextView leftPanelHivesListItemHiveSubscribedUsers;

    /*   HIVE HEADER EXPANDED */
    private View leftPanelHeaderCard;
    private ImageView leftPanelTitleImage;
    private static Image.ImageSize HeaderImageSize = Image.ImageSize.small;
    private TextView leftPanelTitleTextView;

    /*   HIVE DESCRIPTION EXPANDED */
    private View leftPanelDescriptionCard;
    private TextView contextListItemExpandedHiveName;
    private ImageView contextListItemExpandedHiveImage;
    private static Image.ImageSize DescriptionImageSize = Image.ImageSize.large;
    private ImageView contextListItemExpandedCategoryImage;
    private TextView contextListItemExpandedCategoryText;
    private TextView contextListItemExpandedUsersNumber;
    private WrapLayout contextListItemExpandedHiveChatLanguages;
    private TextView contextListItemExpandedHiveDescription;
    private WrapLayout contextWrapLayoutTags;
    private TextView contextStatsCreationDate;
    private TextView contextStatsLastActivityDate;
    private View contextChatButton2;
    private View contextExitButton;


    /*   HIVE USER LIST EXPANDED */
    private View leftPanelHiveUsersListCard;
    private View leftPanelContextUsersContainer;
    private LeftPanelHiveUserListAdapter leftPanelHiveUserListAdapter;
    private Hive.HiveUsersType hiveUsersType = Hive.HiveUsersType.OUTSTANDING;

    private View leftPanelTrendingButton;
    private View leftPanelLocationButton;
    private View leftPanelRecentlyButton;
    private View leftPanelContextMoreUsers;

    public LeftPanelHiveViewHolder(Context context, BaseAdapter baseAdapter,View containerView) {
        super(context,baseAdapter,containerView);
    }
    public LeftPanelHiveViewHolder(Context context, BaseAdapter baseAdapter,View containerView, Hive item, int position) {
        super(context,baseAdapter,containerView, item);
        this.position = position;
        this.setButtons();
    }

    @Override
    public void setContainerView(View containerView) {
        //Initialize collapsed card views
        this.leftPanelCollapsedCard = containerView.findViewById(R.id.left_panel_item_card);
        this.leftPanelHivesListItemImage = (ImageView)containerView.findViewById(R.id.left_panel_hives_list_item_img);
        this.leftPanelHivesListItemHiveName = (TextView)containerView.findViewById(R.id.left_panel_hives_list_item_hive_name);
        this.leftPanelHivesListItemHiveDescription = (TextView)containerView.findViewById(R.id.left_panel_hives_list_item_hive_description);
        this.leftPanelHivesListItemHiveCategroyImg = (ImageView)containerView.findViewById(R.id.left_panel_hives_list_item_hive_categroy_img);
        this.leftPanelHivesListItemHiveCategory = (TextView)containerView.findViewById(R.id.left_panel_hives_list_item_hive_category);
        this.leftPanelHivesListItemHiveSubscribedUsers = (TextView)containerView.findViewById(R.id.left_panel_hives_list_item_hive_subscribed_users);

         // Initialize header expandend views
        this.leftPanelHeaderCard = containerView.findViewById(R.id.left_panel_header);
        this.leftPanelTitleImage = (ImageView)containerView.findViewById(R.id.left_panel_title_img);
        this.leftPanelTitleTextView = (TextView)containerView.findViewById(R.id.left_panel_title_text_view);

        // Initialize hive description expandend views
        this.leftPanelDescriptionCard = containerView.findViewById(R.id.left_panel_card);
        this.contextListItemExpandedHiveName = (TextView)containerView.findViewById(R.id.context_list_item_expanded_hive_name);
        this.contextListItemExpandedHiveImage = (ImageView)containerView.findViewById(R.id.context_list_item_expanded_hive_image);
        this.contextListItemExpandedCategoryImage = (ImageView)containerView.findViewById(R.id.context_list_item_expanded_category_image);
        this.contextListItemExpandedCategoryText = (TextView)containerView.findViewById(R.id.context_list_item_expanded_category_text);
        this.contextListItemExpandedUsersNumber = (TextView)containerView.findViewById(R.id.context_list_item_expanded_users_number); // ATENTION!
        this.contextListItemExpandedHiveChatLanguages = (WrapLayout)containerView.findViewById(R.id.context_list_item_expanded_hive_chat_languages);
        this.contextListItemExpandedHiveDescription = (TextView)containerView.findViewById(R.id.context_list_item_expanded_hive_description);
        this.contextWrapLayoutTags = (WrapLayout)containerView.findViewById(R.id.context_wrap_layout_tags);
        this.contextStatsCreationDate = (TextView)containerView.findViewById(R.id.context_stats_creation_date); // ATENTION!
        this.contextStatsLastActivityDate = (TextView)containerView.findViewById(R.id.context_stats_last_activity_date); // ATENTION!
        this.contextChatButton2 = containerView.findViewById(R.id.context_chat_button2);
        this.contextExitButton = containerView.findViewById(R.id.conext_exit_button);

        // Initialize hive user list expandend views
        this.leftPanelHiveUsersListCard = containerView.findViewById(R.id.left_panel_chat_users);

        this.leftPanelContextUsersContainer = containerView.findViewById(R.id.left_panel_context_users_container);

        this.leftPanelTrendingButton = containerView.findViewById(R.id.left_panel_trending_button);
        this.leftPanelLocationButton = containerView.findViewById(R.id.left_panel_location_button);
        this.leftPanelRecentlyButton = containerView.findViewById(R.id.left_panel_recently_button);
        this.leftPanelContextMoreUsers = containerView.findViewById(R.id.left_panel_context_more_users);

        super.setContainerView(containerView);
    }

    @Override
    protected void updateView() {
        String showingHiveName = context.getResources().getString(R.string.hivename_identifier_character).concat(this.item.getName());

        if (this.itemStatus == null)
            this.itemStatus = HiveItemStatus.Collapsed;
        //Show only correct cards
        this.leftPanelCollapsedCard.setVisibility((this.itemStatus == HiveItemStatus.Collapsed)?View.VISIBLE:View.GONE);
        this.leftPanelHeaderCard.setVisibility((this.itemStatus == HiveItemStatus.Collapsed)?View.GONE:View.VISIBLE);
        this.leftPanelDescriptionCard.setVisibility((this.itemStatus == HiveItemStatus.Description)?View.VISIBLE:View.GONE);
        this.leftPanelHiveUsersListCard.setVisibility((this.itemStatus == HiveItemStatus.List)?View.VISIBLE:View.GONE);

        //Fill only needed views
        switch (this.itemStatus) {
            case Collapsed:
                if (this.item.getHiveImage() == null) {
                    this.leftPanelHivesListItemImage.setImageResource(R.drawable.default_hive_image);
                } else {
                    this.item.getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "onLoadCollapsedImage", EventArgs.class));
                    this.item.getHiveImage().loadImage(LeftPanelHiveViewHolder.CollapsedImageSize, 0);
                }

                this.leftPanelHivesListItemHiveName.setText(showingHiveName);
                this.leftPanelHivesListItemHiveDescription.setText(this.item.getDescription());

                Category.setCategory(this.item.getCategory(), this.leftPanelHivesListItemHiveCategroyImg, this.leftPanelHivesListItemHiveCategory);

                this.leftPanelHivesListItemHiveSubscribedUsers.setText(String.valueOf(this.item.getSubscribedUsersCount()));

                break;
            case Description:
                if (this.item.getHiveImage() == null) {
                    this.contextListItemExpandedHiveImage.setImageResource(R.drawable.default_hive_image);
                } else {
                    this.item.getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "onLoadDescriptionImage", EventArgs.class));
                    this.item.getHiveImage().loadImage(LeftPanelHiveViewHolder.DescriptionImageSize, 0);
                }

                this.contextListItemExpandedHiveName.setText(showingHiveName);

                Category.setCategory(this.item.getCategory(), this.contextListItemExpandedCategoryImage, this.contextListItemExpandedCategoryText);
                this.contextListItemExpandedUsersNumber.setText(context.getResources().getString(R.string.explore_hive_card_expanded_n_mates, this.item.getSubscribedUsersCount()));

                String[] languages_list = this.item.getChatLanguages();
                if ((languages_list != null) && (languages_list.length > 0)) {
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    this.contextListItemExpandedHiveChatLanguages.removeAllViews();
                    this.contextListItemExpandedHiveChatLanguages.invalidate();
                    TextView tv = new TextView(context);
                    tv.setLayoutParams(params2);
                    tv.setText(context.getResources().getString(R.string.explore_hive_card_expanded_hive_chat_langs));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    tv.setTextColor(Color.parseColor("#808080"));
                    this.contextListItemExpandedHiveChatLanguages.addView(tv);
                    for (int i = 0; i < languages_list.length; i++) {
                        tv = new TextView(context);
                        tv.setLayoutParams(params2);
                        tv.setText(languages_list[i].concat((i < (languages_list.length - 1))?", ":""));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                        tv.setTextColor(Color.BLACK);
                        this.contextListItemExpandedHiveChatLanguages.addView(tv);
                    }
                    this.contextListItemExpandedHiveChatLanguages.requestLayout();
                }

                this.contextListItemExpandedHiveDescription.setText("\"".concat(this.item.getDescription()).concat("\""));

                String[] tagsArray = this.item.getTags();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(3, 3, 3, 3);
                if ((tagsArray != null) && (tagsArray.length > 0)) {
                    this.containerView.findViewById(R.id.context_list_item_expanded_tags_layout).setVisibility(View.VISIBLE);
                    this.contextWrapLayoutTags.removeAllViews();
                    this.contextWrapLayoutTags.invalidate();
                    for (int i = 0; i < tagsArray.length; i++) {
                        LinearLayout textContainer = new LinearLayout(context);
                        textContainer.setLayoutParams(params);
                        TextView tv = new TextView(context);
                        tv.setLayoutParams(params);
                        tv.setBackgroundResource(R.drawable.explore_tags_border);
                        tv.setText(tagsArray[i]);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                        tv.setTextColor(Color.BLACK);
                        textContainer.addView(tv);
                        this.contextWrapLayoutTags.addView(textContainer);
                    }
                    this.contextWrapLayoutTags.requestLayout();
                } else {
                    this.containerView.findViewById(R.id.context_list_item_expanded_tags_layout).setVisibility(View.GONE);
                }

                if (this.item.getCreationDate() != null) {
                    this.contextStatsCreationDate.setText(context.getResources().getString(R.string.context_stats_creation_date, DateFormatter.toShortHumanReadableString(this.item.getCreationDate())));
                } else {
                    this.contextStatsCreationDate.setText(context.getResources().getString(R.string.context_stats_creation_date, ""));
                }

                if ((this.item.getPublicChat() != null) && (this.item.getPublicChat().getConversation() != null) && (this.item.getPublicChat().getConversation().getLastMessage() != null) && (this.item.getPublicChat().getConversation().getLastMessage().getOrdinationTimeStamp() != null)) {
                    this.contextStatsLastActivityDate.setText(context.getResources().getString(R.string.context_stats_last_activity_date, updateTimeStamp(this.item.getPublicChat().getConversation().getLastMessage().getOrdinationTimeStamp())));
                } else {
                    this.contextStatsLastActivityDate.setText(context.getResources().getString(R.string.context_stats_last_activity_date, ""));
                }

                break;
            case List:
                if (this.leftPanelHiveUserListAdapter != null)
                    this.leftPanelHiveUserListAdapter.dispose();

                this.hiveUsersType = Hive.HiveUsersType.OUTSTANDING;
                this.setTint();

                this.leftPanelHiveUserListAdapter = new LeftPanelHiveUserListAdapter(this.context,(ViewGroup)this.leftPanelContextUsersContainer,this.item,this.hiveUsersType);
                break;
        }

        //The header
        if (this.itemStatus != HiveItemStatus.Collapsed) {
            if (this.item.getHiveImage() == null) {
                this.leftPanelTitleImage.setImageResource(R.drawable.default_hive_image);
            } else {
                this.item.getHiveImage().OnImageLoaded.add(new EventHandler<EventArgs>(this, "onLoadHeaderImage", EventArgs.class));
                this.item.getHiveImage().loadImage(LeftPanelHiveViewHolder.HeaderImageSize, 0);
            }

            this.leftPanelTitleTextView.setText(showingHiveName);
        }

        this.setButtons();
    }

    private void setButtons() {
        // Header buttons
        this.leftPanelTitleImage.setOnClickListener(descriptionClickListener);
        this.leftPanelHeaderCard.setOnClickListener(userListClickListener);

        // Collapsed buttons
        this.leftPanelHivesListItemImage.setOnClickListener(descriptionClickListener);
        this.leftPanelCollapsedCard.setOnClickListener(userListClickListener);

        // Description buttons
        this.contextChatButton2.setOnClickListener(publicChatClickListener);
        //this.contextExitButton; // TODO: Asign click listener on action defined.

        // List buttons
        this.leftPanelTrendingButton.setOnClickListener(outstandingButtonClickListener);
        this.leftPanelLocationButton.setOnClickListener(locationButtonClickListener);
        this.leftPanelRecentlyButton.setOnClickListener(recentlyOnlineButtonClickListener);
        this.leftPanelContextMoreUsers.setOnClickListener(moreButtonClickListener);
    }

    public void collapseCard() {
        if (this.leftPanelHiveUserListAdapter != null) {
            this.leftPanelHiveUserListAdapter.dispose();
        }
        if (this.itemStatus != HiveItemStatus.Collapsed) {
            this.itemStatus = HiveItemStatus.Collapsed;
            this.updateView();
        }
    }

    private View.OnClickListener descriptionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LeftPanelHiveViewHolder.this.itemStatus != HiveItemStatus.Description) {
                LeftPanelHiveViewHolder.this.itemStatus = HiveItemStatus.Description;
                ((LeftPanelHivesListAdapter)LeftPanelHiveViewHolder.this.baseAdapter).setExpandedItem(LeftPanelHiveViewHolder.this.position);
            } else {
                LeftPanelHiveViewHolder.this.itemStatus = HiveItemStatus.Collapsed;
                ((LeftPanelHivesListAdapter)LeftPanelHiveViewHolder.this.baseAdapter).setExpandedItem(-1);
            }
            if (LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter != null) {
                LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter.dispose();
            }
            LeftPanelHiveViewHolder.this.updateView();
        }
    };

    private View.OnClickListener userListClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LeftPanelHiveViewHolder.this.itemStatus != HiveItemStatus.List) {
                LeftPanelHiveViewHolder.this.itemStatus = HiveItemStatus.List;
                ((LeftPanelHivesListAdapter)LeftPanelHiveViewHolder.this.baseAdapter).setExpandedItem(LeftPanelHiveViewHolder.this.position);
            } else {
                LeftPanelHiveViewHolder.this.itemStatus = HiveItemStatus.Collapsed;
                LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter.dispose();
                ((LeftPanelHivesListAdapter)LeftPanelHiveViewHolder.this.baseAdapter).setExpandedItem(-1);
            }
            LeftPanelHiveViewHolder.this.updateView();
        }
    };

    private View.OnClickListener moreButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter.loadNextPage();
        }
    };

    private View.OnClickListener outstandingButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LeftPanelHiveViewHolder.this.hiveUsersType != Hive.HiveUsersType.OUTSTANDING) {
                LeftPanelHiveViewHolder.this.hiveUsersType = Hive.HiveUsersType.OUTSTANDING;
                LeftPanelHiveViewHolder.this.setTint();

                LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter.dispose();

                LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter = new LeftPanelHiveUserListAdapter(LeftPanelHiveViewHolder.this.context,((ViewGroup)LeftPanelHiveViewHolder.this.leftPanelContextUsersContainer),LeftPanelHiveViewHolder.this.item,hiveUsersType);
            }
        }
    };

    private View.OnClickListener locationButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LeftPanelHiveViewHolder.this.hiveUsersType != Hive.HiveUsersType.LOCATION) {
                LeftPanelHiveViewHolder.this.hiveUsersType = Hive.HiveUsersType.LOCATION;
                LeftPanelHiveViewHolder.this.setTint();

                LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter.dispose();

                LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter = new LeftPanelHiveUserListAdapter(LeftPanelHiveViewHolder.this.context,((ViewGroup)LeftPanelHiveViewHolder.this.leftPanelContextUsersContainer),LeftPanelHiveViewHolder.this.item,hiveUsersType);
            }
        }
    };

    private View.OnClickListener recentlyOnlineButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LeftPanelHiveViewHolder.this.hiveUsersType != Hive.HiveUsersType.RECENTLY_ONLINE) {
                LeftPanelHiveViewHolder.this.hiveUsersType = Hive.HiveUsersType.RECENTLY_ONLINE;
                LeftPanelHiveViewHolder.this.setTint();

                LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter.dispose();

                LeftPanelHiveViewHolder.this.leftPanelHiveUserListAdapter = new LeftPanelHiveUserListAdapter(LeftPanelHiveViewHolder.this.context,((ViewGroup)LeftPanelHiveViewHolder.this.leftPanelContextUsersContainer),LeftPanelHiveViewHolder.this.item,hiveUsersType);
            }
        }
    };

    private void setTint() {
        if (this.hiveUsersType != Hive.HiveUsersType.RECENTLY_ONLINE)
            ((ImageView)this.leftPanelRecentlyButton).clearColorFilter();
        else
            ((ImageView)this.leftPanelRecentlyButton).setColorFilter(Color.parseColor("#ffb615"));

        if (this.hiveUsersType != Hive.HiveUsersType.LOCATION)
            ((ImageView)this.leftPanelLocationButton).clearColorFilter();
        else
            ((ImageView)this.leftPanelLocationButton).setColorFilter(Color.parseColor("#ffb615"));

        if (this.hiveUsersType != Hive.HiveUsersType.OUTSTANDING)
            ((ImageView)this.leftPanelTrendingButton).clearColorFilter();
        else
            ((ImageView)this.leftPanelTrendingButton).setColorFilter(Color.parseColor("#ffb615"));
    }

    private View.OnClickListener publicChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ((LeftPanelHiveViewHolder.this.item != null) && (LeftPanelHiveViewHolder.this.item.getPublicChat() != null)) {
                ((Main) context).OpenWindow(new MainChat(context, LeftPanelHiveViewHolder.this.item.getPublicChat()));

                LeftPanelHiveViewHolder.this.itemStatus = HiveItemStatus.Collapsed;
                ((LeftPanelHivesListAdapter)LeftPanelHiveViewHolder.this.baseAdapter).setExpandedItem(-1);
                LeftPanelHiveViewHolder.this.updateView();
            }
        }
    };

    public void onLoadCollapsedImage(Object sender, EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image) sender;

        ((Activity) this.containerView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputStream is = image.getImage(LeftPanelHiveViewHolder.CollapsedImageSize, 0);
                if (is != null) {
                    LeftPanelHiveViewHolder.this.leftPanelHivesListItemImage.setImageBitmap(BitmapFactory.decodeStream(is));
                    try {
                        is.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(LeftPanelHiveViewHolder.this, "onLoadCollapsedImage", EventArgs.class));
                }
            }
        });
    }

    public void onLoadHeaderImage(Object sender, EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image) sender;

        ((Activity) this.containerView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputStream is = image.getImage(LeftPanelHiveViewHolder.HeaderImageSize, 0);
                if (is != null) {
                    LeftPanelHiveViewHolder.this.leftPanelTitleImage.setImageBitmap(BitmapFactory.decodeStream(is));
                    try {
                        is.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(LeftPanelHiveViewHolder.this, "onLoadHeaderImage", EventArgs.class));
                }
            }
        });
    }

    public void onLoadDescriptionImage(Object sender, EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final Image image = (Image) sender;

        ((Activity) this.containerView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputStream is = image.getImage(LeftPanelHiveViewHolder.DescriptionImageSize, 0);
                if (is != null) {
                    LeftPanelHiveViewHolder.this.contextListItemExpandedHiveImage.setImageBitmap(BitmapFactory.decodeStream(is));
                    try {
                        is.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.OnImageLoaded.remove(new EventHandler<EventArgs>(LeftPanelHiveViewHolder.this, "onLoadDescriptionImage", EventArgs.class));
                }
            }
        });
    }

    private String updateTimeStamp(Date timeStamp) {
        String LastMessageTimestamp = "";
        Date fiveMinutesAgo = new Date((new Date()).getTime() - 5 * 60 * 1000);
        Date today = DateFormatter.toDate(DateFormatter.toString(new Date()));
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(today);
        yesterday.roll(Calendar.DAY_OF_MONTH, false);
        if (timeStamp.after(fiveMinutesAgo))
            LastMessageTimestamp = context.getString(R.string.left_panel_context_imprecise_time_now);
        else if (timeStamp.after(today))
            LastMessageTimestamp = context.getString(R.string.left_panel_context_imprecise_time_today).concat(" ").concat(TimestampFormatter.toLocaleString(timeStamp));
        else if (timeStamp.after(yesterday.getTime()))
            LastMessageTimestamp = context.getString(R.string.left_panel_context_imprecise_time_yesterday).concat(" ").concat(TimestampFormatter.toLocaleString(timeStamp));
        else
            LastMessageTimestamp = DateFormatter.toShortHumanReadableString(timeStamp);

        return LastMessageTimestamp;
    }
}
