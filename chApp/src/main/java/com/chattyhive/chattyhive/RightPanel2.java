package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.Core.BusinessObjects.Image;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.Core.BusinessObjects.Chats.Context.IContextualizable;

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
    private int lastExpandedPosition = -1;

    private View.OnClickListener open_profile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           /* if (((Main)context).controller.getMe() != null) {
                ((Main)context).OpenWindow(new Profile(context,((Main)context).controller.getMe(), Profile.ProfileType.Private));
            }*/
        }
    };

    public RightPanel2(Context activity){
        this.context = activity ;
        this.InitializeComponent();
        ((Activity)this.context).findViewById(R.id.right_panel_action_bar).setOnClickListener(this.open_profile);
        //((Main)this.context).controller.LocalUserReceived.add(new EventHandler<EventArgs>(this, "onLocalUserLoaded", EventArgs.class));
        /*if (((Main)this.context).controller.getMe() != null)
            this.onLocalUserLoaded(((Main)this.context).controller.getMe(),EventArgs.Empty());*/

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    listView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

    }

    public void onLocalUserLoaded(Object sender, EventArgs args){
        final RightPanel2 thisPanel = this;
        ((Activity)context).runOnUiThread( new Runnable() {
            @Override
            public void run() {
                /*String name = ((Main) context).controller.getMe().getUserPrivateProfile().getShowingName().toString();
                ((TextView)((Activity)context).findViewById(R.id.menu_private_profile_name)).setText(name);
                name = context.getResources().getString(R.string.public_username_identifier_character).concat(((Main) context).controller.getMe().getUserPublicProfile().getShowingName().toString());
                ((TextView)((Activity)context).findViewById(R.id.menu_public_profile_name)).setText(name);

                if (((Main) context).controller.getMe().getUserPrivateProfile().getSex().equalsIgnoreCase("male"))
                    ((ImageView)((Activity)context).findViewById(R.id.menu_profile_photo_image)).setImageResource(R.drawable.default_profile_image_male);
                else
                    ((ImageView)((Activity)context).findViewById(R.id.menu_profile_photo_image)).setImageResource(R.drawable.default_profile_image_female);

                ((ImageView)((Activity)context).findViewById(R.id.menu_profile_photo_image)).setColorFilter(Color.parseColor("#ffffff"));
                if (((Main) context).controller.getMe().getUserPrivateProfile().getProfileImage() != null) {
                    ((Main) context).controller.getMe().getUserPrivateProfile().getProfileImage().OnImageLoaded.add(new EventHandler<EventArgs>(thisPanel, "loadImage", EventArgs.class));
                    ((Main) context).controller.getMe().getUserPrivateProfile().getProfileImage().loadImage(Image.ImageSize.medium, 0);
                }*/

                //((Main)context).controller.LocalUserReceived.remove(new EventHandler<EventArgs>(thisPanel, "onLocalUserLoaded", EventArgs.class));
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

                //image.OnImageLoaded.remove(new EventHandler<EventArgs>(thisPanel,"loadImage",EventArgs.class));
                //image.freeMemory();
            }
        });
    }

    private void InitializeComponent(){
        crearDatos();

        listView = (ExpandableListView) ((Activity)this.context).findViewById(R.id.right_panel_expandable_list);
        LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.right_panel_action_bar, listView, false);
        listView.addHeaderView(header, null, false);
        RightPanelExpandableListAdapter adapter = new RightPanelExpandableListAdapter((Activity)this.context, grupos);
        listView.setAdapter(adapter);

        View footer = ((Activity)this.context).findViewById(R.id.footer);

        if (listView.isGroupExpanded(0)==false) {
            listView.expandGroup(0);
//        }
            if (listView.isGroupExpanded(1) == true) {
                listView.collapseGroup(1);
            }
            if (listView.isGroupExpanded(2) == true) {
                listView.collapseGroup(2);
            }
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
        }*/

        /*if(listView.isGroupExpanded(2) == true) {
            if(listView.isGroupExpanded(0) == true) {
                listView.collapseGroup(0);
            }
            if(listView.isGroupExpanded(1) == true) {
                listView.collapseGroup(1);
            }*/
        //}
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

    public void setCommunicationContext(IContextualizable communicationContext) {
        if (communicationContext == null) {
            //TODO: hide communication context.
        } else {
            //TODO: show communication context.
            //Nota: si necesitas el tipo de contexto (chat con amigo, chat privado en hive, chat publico de hive, etc) Sería conveniente en
            //IContextualizable definir un tipo enumerado con los posibles valores, y como método de la interfaz una funcion "getContextType()" que
            //devuelva el tipo.
        }
    }
}

