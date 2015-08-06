package com.chattyhive.chattyhive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.chattyhive.util.Category;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by J.Guzmán on 12/12/2014.
 */
public class NewHive extends Activity{

    private final NewHive thisNewHive = this;

    private static final int CATEGORY_ID = 100;
    private static final int SUB_CATEGORY_ID = 101;
    private static final int HIVE_LAGUAGES = 102;
    private static final int HIVE_LOCATION = 103;
    private static final int HIVE_LOCATION1 = 104;
    private static final int HIVE_LOCATION2 = 105;

    //Required fields
    private boolean allRequiredFieldsOk = false;

    //Categories
    private String[] catList = null;
    private String[] catListCode = null;
    private ArrayList<String[]> listaSubcats = null;
    private ArrayList<String[]> listaSubcatsCode = null;
    private String categoryCode = null;
    private int subcatIndex = -1;

    //Languages
    private String[] languages = null;
    private ArrayList<String> selectedLanguages = null;

    //Locations
    private String[] countries = null;
    private String[] region = null;
    private String[] city = null;
    private ArrayList<String[]> regions = null;
    private ArrayList<String[]> cities = null;
    private String[] titles = null;
    private String locationString = null;
    private int locationStep = 0;
    private int locationIndex = -1;

    //Tags
    private ArrayList<String> tags = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newhive);
        this.init();
    }

    protected void init(){
        this.findViewById(R.id.new_hive_back_button).setOnClickListener(this.backButton);
        this.findViewById(R.id.new_hive_make_button).setOnClickListener(this.make_new_hive);
        this.findViewById(R.id.new_hive_category).setOnClickListener(this.categories);
        this.findViewById(R.id.new_hive_languages).setOnClickListener(this.languagesListener);
        this.findViewById(R.id.new_hive_location_layout).setOnClickListener(this.locationListener);

        this.findViewById(R.id.new_hive_make_button).setSelected(allRequiredFieldsOk);

        ((EditText)this.findViewById(R.id.new_hive_name)).addTextChangedListener(validator);
        ((EditText)this.findViewById(R.id.new_hive_description)).addTextChangedListener(validator);
    }

    private TextWatcher validator = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            verify();
        }
    };

    private void verify() {
        boolean hasName = (((TextView)this.findViewById(R.id.new_hive_name)).getText().length() > 0);
        boolean hasDescription = (((TextView)this.findViewById(R.id.new_hive_description)).getText().length() > 0);
        boolean hasLanguages = ((selectedLanguages != null) && (!selectedLanguages.isEmpty()));
        boolean hasCategory = ((categoryCode != null) && (!categoryCode.isEmpty()) && (categoryCode.matches("^[0-9]{2}\\.[0-9]{2}$")));

        allRequiredFieldsOk = (hasName && hasCategory && hasDescription && hasLanguages);
        ((LinearLayout)findViewById(R.id.new_hive_make_button)).setSelected(allRequiredFieldsOk);
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

            if (!allRequiredFieldsOk) {
                Toast toast = Toast.makeText(thisNewHive,"Deben cubrirse todos los campos no opcionales.",Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            Hive newHive = new Hive(((TextView)findViewById(R.id.new_hive_name)).getText().toString());

            newHive.setDescription(((TextView) findViewById(R.id.new_hive_description)).getText().toString());

            if ((categoryCode != null) && (!categoryCode.isEmpty()) && (categoryCode.matches("^[0-9]{2}\\.[0-9]{2}$")))
                newHive.setCategory(categoryCode);
            else if ((categoryCode != null) && (!categoryCode.isEmpty()))
                Log.w("NewHive", String.format("CategoryCode: %s", categoryCode));

            //SELECCIÓN DE TAGS
            //TODO: Sustituir esto por una lista de tags a asociar al hive.

            ArrayList<String> tags = new ArrayList<String>();
            String[] tags_tmp;
            String tags_string = ((TextView)findViewById(R.id.new_hive_tags)).getText().toString();
            tags_tmp = tags_string.split("[, ]+");
            if (tags_tmp.length > 0) {
                for (String tag : tags_tmp)
                    if ((tag != null) && (!tag.isEmpty()) && (!tags.contains(tag)))
                        tags.add(tag);
            }
            if (tags.size() > 0)
                newHive.setTags(tags.toArray(new String[tags.size()]));


            //SELECCIÓN DE IDIOMAS
            if ((selectedLanguages != null) && (selectedLanguages.size() > 0))
                newHive.setChatLanguages(selectedLanguages.toArray(new String[selectedLanguages.size()]));

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
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout)).setOrientation(LinearLayout.VERTICAL);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_sup)).setPadding(0, 0, 0, 0);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_inf)).setPadding(0,0,0,0);
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//horizontal
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout)).setOrientation(LinearLayout.HORIZONTAL);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_sup)).setPadding(0,0,5,0);
            ((LinearLayout)this.findViewById(R.id.new_hive_variable_layout_inf)).setPadding(5,0,0,0);
        }

    }

    private View.OnClickListener categories = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getCategories();
            showDialog(CATEGORY_ID);
        }
    };

    private View.OnClickListener languagesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getLanguages();
            showDialog(HIVE_LAGUAGES);
        }
    };

    private View.OnClickListener locationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            locationData();
            showDialog(HIVE_LOCATION);
        }
    };

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case CATEGORY_ID:
                break;
            case SUB_CATEGORY_ID:
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        removeDialog(SUB_CATEGORY_ID);
                    }
                });
                break;
            }
        }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CATEGORY_ID:
                return categoriesDialog();
            case SUB_CATEGORY_ID:
                return categoriesDialog1();
            case HIVE_LAGUAGES:
                return languagesDialog();
            case HIVE_LOCATION:
                return locationDialog0();
            case HIVE_LOCATION1:
                return locationDialog1();
            case HIVE_LOCATION2:
                return locationDialog2();
        }
        return null;
    }

    private void getCategories(){
        TreeMap<Category,TreeSet<Category>> categoriesTreeMap = new TreeMap<Category, TreeSet<Category>>(Category.listCategories());
        Object[] objList = categoriesTreeMap.keySet().toArray();
        catList = new String[objList.length];
        catListCode = new String[objList.length];
        for (int i = 0; i < catList.length; i++) {
            int res = ((Category) objList[i]).getCategoryNameResID();
            catList[i] = getResources().getString(res);
            catListCode[i] = ((Category) objList[i]).getCompleteCode();
        }
        listaSubcats = new ArrayList<String[]>();
        listaSubcatsCode = new ArrayList<String[]>();
        TreeSet<Category> subCats;
        Object[] subcatobj;
        String[] stringsubcat;
        String[] stringsubcatCode;
        for (int i = 0; i <objList.length ; i++) {
            subCats = categoriesTreeMap.get(objList[i]);
            subcatobj = subCats.toArray();
            stringsubcat = new String[subcatobj.length];
            stringsubcatCode = new String[subcatobj.length];
            for (int j = 0; j < stringsubcat.length; j++) {
                int res = ((Category) subcatobj[j]).getCategoryNameResID();
                stringsubcat[j] = getResources().getString(res);
                stringsubcatCode[j] = ((Category) subcatobj[j]).getCompleteCode();
            }
            listaSubcats.add(stringsubcat);
            listaSubcatsCode.add(stringsubcatCode);
        }
    }

    protected synchronized Dialog categoriesDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Categorías").setItems(catList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                subcatIndex = which;
                if (listaSubcats.get(which) == null || listaSubcats.get(which).length < 1 ){
                    System.out.println("SUBCATEGORIES NULL");
                }
                else if (listaSubcats.get(which).length == 1){
                    ((TextView)findViewById(R.id.new_hive_category)).setText(catList[which]);
                    categoryCode = catListCode[which];
                    verify();
                }
                else {
                    showDialog(SUB_CATEGORY_ID);
                }
            }
        });
        return builder.create();
    }

    protected synchronized Dialog categoriesDialog1(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Subcategorías").setItems(listaSubcats.get(subcatIndex), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((TextView)findViewById(R.id.new_hive_category)).setText(listaSubcats.get(subcatIndex)[which]);
                categoryCode = listaSubcatsCode.get(subcatIndex)[which];
                verify();
            }
        });
        return builder.create();
    }

    private void getLanguages(){
        languages = new String[4];
        languages[0] = "English";
        languages[1] = "French";
        languages[2] = "Spanish";
        languages[3] = "Turkish";
    }

    public Dialog languagesDialog(){
        selectedLanguages = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        getLanguages();
        builder.setTitle("Select your languages")
                .setMultiChoiceItems(languages, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedLanguages.add(languages[which]);
                                } else if (selectedLanguages.contains(languages[which])) {
                                    selectedLanguages.remove(languages[which]);
                                }
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (selectedLanguages.size() != 0) {
                                    ((TextView)findViewById(R.id.new_hive_languages_text)).setVisibility(View.GONE);
                                    ((LinearLayout)findViewById(R.id.new_hive_languages_layout)).setVisibility(View.VISIBLE);
                                    WrapLayout expanded_hive_tagsLayout = (WrapLayout) findViewById(R.id.explore_wrap_layout_tags);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(3, 3, 3, 3);
                                    expanded_hive_tagsLayout.removeAllViews();
                                    expanded_hive_tagsLayout.invalidate();
                                    for (int i = 0; i < selectedLanguages.size(); i++) {
                                        LinearLayout textContainer = new LinearLayout(getApplicationContext());
                                        textContainer.setLayoutParams(params);
                                        textContainer.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                        TextView tv = new TextView(getApplicationContext());
                                        tv.setLayoutParams(params);
                                        tv.setBackgroundResource(R.drawable.explore_tags_border);
                                        tv.setText(selectedLanguages.get(i));
                                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                        tv.setTextColor(Color.BLACK);
                                        textContainer.addView(tv);
                                        expanded_hive_tagsLayout.addView(textContainer);
                                    }
                                    expanded_hive_tagsLayout.requestLayout();
                                }else if (selectedLanguages.size() == 0){
                                    WrapLayout expanded_hive_tagsLayout = (WrapLayout) findViewById(R.id.explore_wrap_layout_tags);
                                    ((LinearLayout)findViewById(R.id.new_hive_languages_layout)).setVisibility(View.GONE);
                                    expanded_hive_tagsLayout.removeAllViews();
                                    expanded_hive_tagsLayout.invalidate();
                                    ((TextView)findViewById(R.id.new_hive_languages_text)).setVisibility(View.VISIBLE);
                                }
                                verify();
                            }
                        }

                )
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    }

                            );

                    return builder.create();
                }

        private void locationData(){
        countries = new String[5];
        countries[0] = "Visible en todo el mundo";
        countries[1] = "EEUU";
        countries[2] = "Spain";
        countries[3] = "Yemen";
        countries[4] = "Albania";
        region = new String[4];
        region[0] = "Andalucía";
        region[1] = "Cataluña";
        region[2] = "Galicia";
        region[3] = "Pais Vasco";
        city = new String[4];
        city[0] = "A Coruña";
        city[1] = "Lugo";
        city[2] = "Ourense";
        city[3] = "Pontevedra";
        titles = new String[3];
        titles[0] = "Choose your country";
        titles[1] = "Choose your region";
        titles[2] = "Choose your city";

        regions = new ArrayList<String[]>();
        regions.add(0, null);
        regions.add(1, null);
        regions.add(2, region);
        regions.add(3, null);
        regions.add(4, null);
        cities = new ArrayList<String[]>();
        cities.add(0, null);
        cities.add(1, null);
        cities.add(2, city);
        cities.add(3, null);
    }

    protected Dialog locationDialog0(){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)this);
        builder.setTitle(titles[locationStep]).setItems(countries, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                locationString = countries[which];
                //newUser.getUserPrivateProfile().setLocation(locationString);
                ((TextView) findViewById(R.id.new_hive_location_text)).setText(locationString);
                locationStep++;
                locationIndex = which;
                if (regions.get(which) != null)
                    showDialog(HIVE_LOCATION1);
            }
        });
        return builder.create();
    }

    protected Dialog locationDialog1(){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)this);
        builder.setTitle(titles[locationStep]).setItems(regions.get(locationIndex), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                locationString = locationString +", "+ regions.get(locationIndex)[which];
                //newUser.getUserPrivateProfile().setLocation(locationString);
                ((TextView) findViewById(R.id.new_hive_location_text)).setText(locationString);
                locationStep++;
                locationIndex = which;
                if (cities.get(which) != null)
                    showDialog(HIVE_LOCATION2);
            }
        });
        return builder.create();
    }
    protected Dialog locationDialog2(){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)this);
        builder.setTitle(titles[locationStep]).setItems(cities.get(locationIndex), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                locationString = locationString +", "+ cities.get(locationIndex)[which];
                //newUser.getUserPrivateProfile().setLocation(locationString);
                ((TextView) findViewById(R.id.new_hive_location_text)).setText(locationString);
            }
        });
        return builder.create();
    }
}