package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by J.Guzmán on 24/09/2014.
 */

public class RightPanel2{
    Context context;
    SparseArray<RightPanelListItem> grupos = new  SparseArray<RightPanelListItem>();
    ExpandableListView listView;
    View img;

    public RightPanel2(Context activity){
        this.context = activity ;
        this.InitializeComponent();
        ((Activity)this.context).findViewById(R.id.right_panel_action_bar).setOnClickListener((new Profile(this.context)).open_profile);
        ((Main)this.context).controller.LocalUserReceived.add(new EventHandler<EventArgs>(this, "onLocalUserLoaded", EventArgs.class));
    }

    public void onLocalUserLoaded(Object sender, EventArgs args){
        final RightPanel2 thisPanel = this;
        ((Activity)context).runOnUiThread( new Runnable() {
            @Override
            public void run() {
                String name = ((Main) context).controller.getMe().getUserPrivateProfile().getShowingName().toString();
                ((TextView)((Activity)context).findViewById(R.id.menu_private_profile_name)).setText(name);
                name = context.getResources().getString(R.string.public_username_identifier_character).concat(((Main) context).controller.getMe().getUserPublicProfile().getShowingName().toString());
                ((TextView)((Activity)context).findViewById(R.id.menu_public_profile_name)).setText(name);
                ((ImageView)((Activity)context).findViewById(R.id.menu_profile_photo_image)).setImageResource(R.drawable.pestanha_chats_user);
                ((ImageView)((Activity)context).findViewById(R.id.menu_profile_photo_image)).setColorFilter(Color.parseColor("#ffffff"));
                ((Main) context).controller.getMe().getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(thisPanel,"loadImage",EventArgs.class));
                ((Main) context).controller.getMe().getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.medium,0);
            }
        });
    }

    public void loadImage(Object sender, EventArgs eventArgs) {
        if (!(sender instanceof Image)) return;

        final ImageView imageView = ((ImageView)((Activity)this.context).findViewById(R.id.menu_profile_photo_image));
        final Image image = (Image)sender;
        final RightPanel2 thisPanel = this;

        ((Activity)context).runOnUiThread( new Runnable() {
            @Override
            public void run() {
                InputStream is = image.getImage(Image.ImageSize.medium,0);
                if (is != null) {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(is));
                    imageView.clearColorFilter();
                    try {
                        is.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisPanel,"loadImage",EventArgs.class));
                image.freeMemory();
            }
        });
    }

    private void InitializeComponent(){
        crearDatos();
        listView = (ExpandableListView) ((Activity)this.context).findViewById(R.id.right_panel_expandable_list);
        RightPanelExpandableListAdapter adapter = new RightPanelExpandableListAdapter((Activity)this.context, grupos);
        listView.setAdapter(adapter);

        View footer = ((Activity)this.context).findViewById(R.id.footer);

        if (listView.isGroupExpanded(0)==false){
            listView.expandGroup(0);
        }
        if(listView.isGroupExpanded(1)== true){
            listView.collapseGroup(1);
        }
        if(listView.isGroupExpanded(2)== true){
            listView.collapseGroup(2);
        }

        //listView.addFooterView(footer);
        /*if(listView.isGroupExpanded(0) == true) {
            if(listView.isGroupExpanded(1) == true) {
                listView.collapseGroup(1);
            }
            if(listView.isGroupExpanded(2) == true) {
                listView.collapseGroup(2);
            }
        }
        if(listView.isGroupExpanded(1) == true) {
            if(listView.isGroupExpanded(0) == true) {
                listView.collapseGroup(0);
            }
            if(listView.isGroupExpanded(2) == true) {
                listView.collapseGroup(2);
            }
        }

        if(listView.isGroupExpanded(2) == true) {
            if(listView.isGroupExpanded(0) == true) {
                listView.collapseGroup(0);
            }
            if(listView.isGroupExpanded(1) == true) {
                listView.collapseGroup(1);
            }
        }*/
    }

    public void crearDatos()  {
        RightPanelListItem grupo0 = new RightPanelListItem(" ");
        grupo0.children.add(" ");
        RightPanelListItem grupo1 = new RightPanelListItem(" ");
        grupo1.children.add(" ");
        RightPanelListItem grupo2 = new RightPanelListItem(" ");
        grupo2.children.add(" ");
        grupos.append(0, grupo0);
        grupos.append(1, grupo1);
        grupos.append(2, grupo2);
    }
}

