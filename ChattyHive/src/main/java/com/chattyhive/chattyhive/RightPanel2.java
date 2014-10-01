package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.widget.ExpandableListView;

/**
 * Created by J.Guzmán on 24/09/2014.
 */

public class RightPanel2  {
    Context context;
    SparseArray<RightPanelListItem> grupos = new  SparseArray<RightPanelListItem>();
    ExpandableListView listView;

    public RightPanel2(Context activity) {
        this.context = activity ;
        this.InitializeComponent(); //inicializa todas las componentes del panel
    }

    private void InitializeComponent(){
        crearDatos();
        listView = (ExpandableListView) ((Activity)this.context).findViewById(R.id.right_panel_expandable_list);
        RightPanelExpandableListAdapter adapter = new RightPanelExpandableListAdapter((Activity)this.context, grupos); //cast modificado
        listView.setAdapter(adapter);
        listView.expandGroup(0);
    }

    /*    @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.right_panel_layout);
            crearDatos();
            ExpandableListView listView = (ExpandableListView) findViewById(R.id.right_panel_expandable_list);
            RightPanelExpandableListAdapter adapter = new RightPanelExpandableListAdapter((Activity)this.context, grupos); //cast modificado
            listView.setAdapter(adapter);
        }*/

    public void crearDatos()  {
        RightPanelListItem grupo0 = new RightPanelListItem("Menú");
        grupo0.children.add("Inicio");
        grupo0.children.add("Explora");
        grupo0.children.add("Chats");
        grupo0.children.add("Peticiones");
        grupos.append(0, grupo0);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

}

