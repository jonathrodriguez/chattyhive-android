package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    }

    private void InitializeComponent(){
        crearDatos();
        listView = (ExpandableListView) ((Activity)this.context).findViewById(R.id.right_panel_expandable_list);
        RightPanelExpandableListAdapter adapter = new RightPanelExpandableListAdapter((Activity)this.context, grupos);
        listView.setAdapter(adapter);
        if(listView.isGroupExpanded(0) == false) { //igual no permite cerrar. vigilar
            listView.expandGroup(0);
        }
        /*img =  ((Activity)this.context).findViewById(R.id.menu_notexpanded_explora_img);
        LinearLayout lay = (LinearLayout) ((Activity)this.context).findViewById(R.id.right_panel_items_layout);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            public boolean onChildClick(ExpandableListView parent, View v, int
                    groupPosition, int childPosition, long id) {
                ((Activity)context).findViewById(R.id.menu_notexpanded_explora_img).setVisibility(View.INVISIBLE);
                //Toast.makeText(context, "hijo pulsado de grupo y posicion " +
                        //groupPosition + childPosition,Toast.LENGTH_LONG).show();
                return true;
            }
        });*/

        /*if (lay!=null)
            lay.setOnClickListener(listener);
        else
            Log.w("Inizalice componet", "lay is null");*/
    }

        /*@Override
        protected void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ImageView img1 = (ImageView) findViewById(R.id.menu_notexpanded_explora_img);
            LinearLayout lay = (LinearLayout) findViewById(R.id.menu_layout_ajustes);
            lay.setOnClickListener(listener);
        }*/

    View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {
            ((Activity)context).findViewById(R.id.menu_notexpanded_explora_img).setVisibility(View.INVISIBLE);
        }
    };

    public void crearDatos()  {
        RightPanelListItem grupo0 = new RightPanelListItem(" ");
        grupo0.children.add(" ");
        grupos.append(0, grupo0);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

}

