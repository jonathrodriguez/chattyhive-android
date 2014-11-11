package com.chattyhive.chattyhive.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.chattyhive.R;
import com.chattyhive.chattyhive.framework.Util.ApplicationContextProvider;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Jonathan on 09/11/2014.
 */
public class Category {
    private static Context context;

    static {
        context = ApplicationContextProvider.getContext();
    }
    public static void setCategory(String category, ImageView categoryImageView, TextView categoryTextView) {
        Category cat = new Category(category);
        if (cat.getCategoryNameResID() != 0)
            categoryTextView.setText(cat.getCategoryNameResID());
        else
            categoryTextView.setText("Category");

        if (cat.getCategoryImageResID() != 0)
            categoryImageView.setImageResource(cat.categoryImageResID);
        else
            categoryImageView.setImageResource(R.drawable.registro_important_note_orange);
    }
    public static Category getCategory(String category) {
        return new Category(category);
    }
    public static TreeMap<Category,TreeSet<Category>> listCategories() {
        TreeMap<Category,TreeSet<Category>> response = null;

        TreeSet<String> categoryCodes = new TreeSet<String>();

        Field[] ID_Fields = R.drawable.class.getFields();
        for (Field field : ID_Fields)
            if (field.getName().matches("category(_[0-9]{2}){2}"))
                categoryCodes.add(field.getName().substring("category_".length()).replace('_','.'));

        if (categoryCodes.size() > 0) {
            TreeMap<String,Category> groups = new TreeMap<String, Category>();
            response = new TreeMap<Category,TreeSet<Category>>(new Comparator<Category>() {
                @Override
                public int compare(Category lhs, Category rhs) {
                    String lhsName = context.getResources().getString(lhs.getCategoryNameResID());
                    String rhsName = context.getResources().getString(rhs.getCategoryNameResID());
                    return lhsName.compareTo(rhsName);
                }
            });
            for (String category : categoryCodes) {
                String groupCode = category.split(".")[0];
                Category group = null;
                if (!groups.containsKey(groupCode)) {
                    group = getCategory(groupCode.concat(".00"));
                    groups.put(groupCode,group);
                    response.put(group,new TreeSet<Category>(new Comparator<Category>() {
                        @Override
                        public int compare(Category lhs, Category rhs) {
                            if ((lhs.getCategoryCode() == "01") || (rhs.getCategoryCode() != "01"))
                                return -1;
                            else if ((lhs.getCategoryCode() != "01") || (rhs.getCategoryCode() == "01"))
                                return 1;
                            else {
                                String lhsName = context.getResources().getString(lhs.getCategoryNameResID());
                                String rhsName = context.getResources().getString(rhs.getCategoryNameResID());
                                return lhsName.compareTo(rhsName);
                            }
                        }
                    }));
                } else {
                    group = groups.get(groupCode);
                }
                response.get(group).add(new Category(category));
            }
        }

        return response;
    }

    /**************************************/
    /*           Category                 */
    /**************************************/

    private String categoryCode;
    private int categoryNameResID;
    private int categoryImageResID;

    public String getCategoryCode() {
        return this.categoryCode.split(".")[1];
    }
    public String getGroupCode() {
        return this.categoryCode.split(".")[0];
    }
    public String getCompleteCode() {
        return this.categoryCode;
    }

    public int getCategoryNameResID() {
        return this.categoryNameResID;
    }
    public int getGroupNameResID() {
        String groupResource = "category_".concat(this.getGroupCode()).concat("_00");
        return context.getResources().getIdentifier(groupResource,"string","com.chattyhive.chattyhive");
    }

    public int getCategoryImageResID() {
        return this.categoryImageResID;
    }
    public int getGroupImageResID() {
        String groupResource = "category_".concat(this.getGroupCode()).concat("_00");
        return context.getResources().getIdentifier(groupResource,"drawable","com.chattyhive.chattyhive");
    }

    private Category(String categoryCode) {
        this.categoryCode = categoryCode;
        String categoryResource = "category_".concat(this.categoryCode.replace('.','_'));
        int nameResID = context.getResources().getIdentifier(categoryResource,"string","com.chattyhive.chattyhive");
        int imageResID = context.getResources().getIdentifier(categoryResource,"drawable","com.chattyhive.chattyhive");

        if ((nameResID == 0) && (this.categoryCode.contains("."))) {
            categoryResource = "category_".concat(this.categoryCode.split("\\.")[0]).concat("_00");
            nameResID = context.getResources().getIdentifier(categoryResource,"string","com.chattyhive.chattyhive");
        }

        if ((imageResID == 0) && (this.categoryCode.contains("."))) {
            categoryResource = "category_".concat(this.categoryCode.split("\\.")[0]).concat("_00");
            imageResID = context.getResources().getIdentifier(categoryResource,"drawable","com.chattyhive.chattyhive");
        }

        this.categoryNameResID = nameResID;
        this.categoryImageResID = imageResID;
    }

    public Category() {}
}
