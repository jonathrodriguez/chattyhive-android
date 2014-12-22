package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.SlidingStepsLayout;

/**
 * Created by J.Guzmán on 12/12/2014.
 */
public class NewHive extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newhive);
        this.init();
    }

    protected void init(){
        this.findViewById(R.id.new_hive_back_button).setOnClickListener(this.backButton);
        this.findViewById(R.id.new_hive_make_button).setOnClickListener(this.make_new_hive);
    }

    protected View.OnClickListener backButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    protected View.OnClickListener make_new_hive =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_OK);
            finish();
        }
    };

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){//vertical
            System.out.println("PORTRAIT!!!!");
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout)).setOrientation(LinearLayout.VERTICAL);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_sup)).setPadding(0,0,0,0);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_inf)).setPadding(0,0,0,0);
            //Toast.makeText(this, "In portrait mode", Toast.LENGTH_SHORT).show();
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//horizontal
            System.out.println("LANDSCAPE!!!");
            //LinearLayout myLayout = ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout));
            //myLayout.setLayoutParams(new SlidingStepsLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //myLayout.setOrientation(LinearLayout.HORIZONTAL);
            //((TextView) this.findViewById(R.id.test)).setText("aasdasdasdasd");
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout)).setOrientation(LinearLayout.HORIZONTAL);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_sup)).setPadding(0,0,5,0);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_inf)).setPadding(5,0,0,0);
            //Toast.makeText(this, "In landscape mode", Toast.LENGTH_SHORT).show();
            //((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_sup)).setWeightSum(0.5);
        }

    }
}
