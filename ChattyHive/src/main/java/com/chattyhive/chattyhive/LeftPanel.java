package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.Button;
import android.widget.ViewSwitcher;

/**
 * Created by Jonathan on 7/03/14.
 */
public class LeftPanel {
    Context context;

    Button chats;
    Button hives;
    Button mates;

    ViewSwitcher view_switcher;

    public LeftPanel(Context activity) {
        this.context = activity;

        this.InitializeComponent();
    }

    private void InitializeComponent() {
        // Here we set the components to their respective elements.
        chats = (Button)((Activity)this.context).findViewById(R.id.left_panel_chats_button);
        hives = (Button)((Activity)this.context).findViewById(R.id.left_panel_hives_button);
        mates = (Button)((Activity)this.context).findViewById(R.id.left_panel_mates_button);

        view_switcher = (ViewSwitcher)((Activity)this.context).findViewById(R.id.left_panel_empty_list_view_switcher);


        SetButtonSelected(chats, true, this.context.getResources().getString(R.string.left_panel_chats_button), R.drawable.pestanhas_panel_izquierdo_chats_blanco);
        SetButtonSelected(hives,false,this.context.getResources().getString(R.string.left_panel_hives_button),R.drawable.pestanhas_panel_izquierdo_hives_blanco);
        SetButtonSelected(mates,false,this.context.getResources().getString(R.string.left_panel_mates_button),R.drawable.pestanhas_panel_izquierdo_users_blanco);
  }

    private void SetButtonSelected(Button button, Boolean selected, String text, int drawable) {
        Spannable buttonLabel = new SpannableString(" ");
        if (selected) { buttonLabel = new SpannableString(text); }
        buttonLabel.setSpan(new ImageSpan(this.context.getApplicationContext(),drawable,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        button.setText(buttonLabel);
        //button.setTextColor(Color.parseColor("#3a3a3a"));
    }
}
