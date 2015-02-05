package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;

import com.chattyhive.chattyhive.framework.Util.StaticMethods;
import com.chattyhive.chattyhive.framework.Util.ViewPair;


/**
 * Created by Jonathan on 10/10/2014.
 */

public class Home extends Window {
    private static int HomeHierarchyLevel = 0;
    private transient HomeListAdapter homeListAdapter;

    void setContext(Context context) {
        super.setContext(context);
        this.homeListAdapter = null;
    }

    public Home(Context context) {
        super(context);
        this.setHierarchyLevel(HomeHierarchyLevel);
    }

    @Override
    public void Open() {
        if (!this.hasContext()) return;

        this.Show();
    }

    @Override
    public void Close() {
        if (!this.hasContext()) return;

        this.homeListAdapter = null;
        ((ListView) ((Activity) this.context).findViewById(R.id.home_listView)).setAdapter(null);
    }

    @Override
    public void Show() {
        if (!this.hasContext()) return;

        ViewPair pair = ((Main)this.context).ShowLayout(R.layout.home,R.layout.home_action_bar);
        ((Main)this.context).setPanelBehaviour();

        TypedValue alpha = new TypedValue();

        this.context.getResources().getValue(R.color.home_action_bar_app_icon_alpha,alpha,true);
        StaticMethods.SetAlpha(pair.getActionBarView().findViewById(R.id.appIcon), alpha.getFloat());

        this.context.getResources().getValue(R.color.home_action_bar_menu_icon_alpha,alpha,true);
        StaticMethods.SetAlpha(pair.getActionBarView().findViewById(R.id.menuIcon),alpha.getFloat());

        this.context.getResources().getValue(R.color.home_top_bar_image_alpha,alpha,true);
        StaticMethods.SetAlpha(pair.getMainView().findViewById(R.id.home_chat_button_image),alpha.getFloat());
        StaticMethods.SetAlpha(pair.getMainView().findViewById(R.id.home_explore_button_image),alpha.getFloat());
        StaticMethods.SetAlpha(pair.getMainView().findViewById(R.id.home_hive_button_image),alpha.getFloat());

        this.Reload();

        if (((Main)this.context).floatingPanel.isOpen())
            ((Main)this.context).floatingPanel.close();

        ((Main)context).rightPanel.setCommunicationContext(null);
    }

    @Override
    public void Hide() {
        if (!this.hasContext()) return;
    }

    private void Reload() {
        if (this.homeListAdapter == null)
            this.homeListAdapter = new HomeListAdapter(context);

        ((ListView) ((Activity) this.context).findViewById(R.id.home_listView)).setAdapter(this.homeListAdapter);

        ((Main) this.context).controller.RequestHome();
        this.setButtons();
    }

    private void setButtons() {
        ((Activity) this.context).findViewById(R.id.home_chat_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Main) context).ShowChats();
            }
        });

        ((Activity) this.context).findViewById(R.id.home_explore_button).setOnClickListener(((Main) this.context).explore_button_click);

        ((Activity) this.context).findViewById(R.id.home_hive_button).setOnClickListener(((Main) this.context).new_hive_button_click);
    }
}
