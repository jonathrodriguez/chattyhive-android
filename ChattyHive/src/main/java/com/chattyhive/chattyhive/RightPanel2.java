package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;

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
    }

    private void InitializeComponent(){
        crearDatos();
        listView = (ExpandableListView) ((Activity)this.context).findViewById(R.id.right_panel_expandable_list);
        RightPanelExpandableListAdapter adapter = new RightPanelExpandableListAdapter((Activity)this.context, grupos);
        listView.setAdapter(adapter);
        //if(listView.isGroupExpanded(0) == false) {
        //    listView.expandGroup(0);
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
}

