package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.chattyhive.framework.CustomViews.ViewGroup.SlidingStepsLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Created by J.Guzmán on 12/12/2014.
 */
public class NewHive extends Activity{

    private final NewHive thisNewHive = this;

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
            Hive newHive = new Hive(((TextView)findViewById(R.id.new_hive_name)).getText().toString());

            newHive.setDescription(((TextView) findViewById(R.id.new_hive_description)).getText().toString());
            newHive.setCategory(((TextView) findViewById(R.id.new_hive_category)).getText().toString());


            TreeSet<String> tags = new TreeSet<String>();
            String[] tags_tmp;
            String tags_string = ((TextView)findViewById(R.id.new_hive_tags)).getText().toString();
            tags_tmp = tags_string.split("[, ]+");
            if (tags_tmp.length > 0) {
                for (String tag : tags_tmp)
                    if ((tag != null) && (!tag.isEmpty()))
                        tags.add(tag);
            }
            if (tags.size() > 0)
                newHive.setTags(tags.toArray(new String[tags.size()]));

            TreeSet<String> languages = new TreeSet<String>();
            String[] languages_tmp;
            String languages_string = ((TextView)findViewById(R.id.new_hive_tags)).getText().toString();
            languages_tmp = languages_string.split("[, ]+");
            if (languages_tmp.length > 0) {
                for (String language : languages_tmp)
                    if ((language != null) && (!language.isEmpty()))
                        languages.add(language);
            }
            if (languages.size() > 0)
                newHive.setChatLanguages(languages.toArray(new String[languages.size()]));

            newHive.createHive(new EventHandler<CommandCallbackEventArgs>(thisNewHive,"onHiveCreatedCallback",CommandCallbackEventArgs.class));
        }
    };

    public void onHiveCreatedCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){//vertical
            //System.out.println("PORTRAIT!!!!");
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout)).setOrientation(LinearLayout.VERTICAL);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_sup)).setPadding(0,0,0,0);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_inf)).setPadding(0,0,0,0);
            //Toast.makeText(this, "In portrait mode", Toast.LENGTH_SHORT).show();
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//horizontal
            //System.out.println("LANDSCAPE!!!");
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
