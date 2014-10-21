package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

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
        this.InitializeComponent(); //inicializa todas las componentes del panel
        ((Activity)this.context).findViewById(R.id.right_panel_action_bar).setOnClickListener((new Profile(this.context)).open_profile);
        String name = ((Main) this.context).controller.getMe().getUserPrivateProfile().getShowingName().toString();
        ((TextView)((Activity)this.context).findViewById(R.id.menu_private_profile_name)).setText(name);
        name = ((Main) this.context).controller.getMe().getUserPublicProfile().getShowingName().toString();
        ((TextView)((Activity)this.context).findViewById(R.id.menu_public_profile_name)).setText(name);
    }

    private void InitializeComponent(){
        crearDatos();
        listView = (ExpandableListView) ((Activity)this.context).findViewById(R.id.right_panel_expandable_list);
        RightPanelExpandableListAdapter adapter = new RightPanelExpandableListAdapter((Activity)this.context, grupos);
        listView.setAdapter(adapter);

        View footer = ((Activity)this.context).findViewById(R.id.footer);
        listView.addFooterView(footer);

        /*if(listView.isGroupExpanded(0) == true) {
            listView.collapseGroup(1);
            listView.collapseGroup(2);
        }
        if(listView.isGroupExpanded(1) == true) {
            listView.collapseGroup(0);
            listView.collapseGroup(2);
        }
        if(listView.isGroupExpanded(2) == true) {
            listView.collapseGroup(0);
            listView.collapseGroup(1);
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

