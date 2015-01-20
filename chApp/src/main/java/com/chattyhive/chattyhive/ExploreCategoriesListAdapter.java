package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattyhive.chattyhive.util.Category;

import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Jonathan on 19/12/2014.
 */
public class ExploreCategoriesListAdapter extends BaseAdapter {

    private Context context;
    public Context getContext() {
        return this.context;
    }
    private LayoutInflater layoutInflater;

    private GridView gridView;
    public void setGridView(GridView gridView) {
        if (this.gridView == gridView) return;
        this.gridView = gridView;
        if (this.gridView == null) return;

        this.gridView.setAdapter(this);
    }

    private View.OnClickListener categoriesItemClickListener;

    private TreeMap<Category,TreeSet<Category>> categories;

    public ExploreCategoriesListAdapter (Context context,View.OnClickListener categoriesItemClickListener) {
        this.context = context;
        this.categoriesItemClickListener = categoriesItemClickListener;
        this.layoutInflater = ((Activity)this.context).getLayoutInflater();

        this.categories = new TreeMap<Category, TreeSet<Category>>(Category.listCategories());
       // this.categories.put(new Category(R.string.communities,R.drawable.category_19_00),null); //Add communities to list
    }

    public void Clear() {
        this.categories.clear();
        this.syncNotifyDataSetInvalidated();

        if (this.gridView != null)
            this.gridView.setAdapter(null);
        this.gridView = null;
    }

    public void syncNotifyDataSetInvalidated() {
        ((Activity)this.context).runOnUiThread(new Runnable(){
            public void run() {
                notifyDataSetInvalidated();
            }
        });
    }

    @Override
    public int getCount() {
        return this.categories.size();
    }

    @Override
    public Object getItem(int position) {
        return this.categories.keySet().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = (Category)this.getItem(position);
        if(convertView==null){
            convertView = this.layoutInflater.inflate(R.layout.explore_categories_item,parent,false);
            convertView.setOnClickListener(this.categoriesItemClickListener);
            new CategoryViewHolder(convertView,category);
        } else {
            CategoryViewHolder holder = (CategoryViewHolder)convertView.getTag(R.id.Explore_ListViewHolder);
            holder.setCategory(category);
        }

        return convertView;
    }

    private class CategoryViewHolder {
        Category category;
        public void setCategory(Category category) {
            this.category = category;
            if (this.itemView != null)
                this.itemView.setTag(R.id.BO_Category,this.category);
            this.loadData();
        }

        ImageView categoryImage;
        public void setCategoryImage(ImageView imageView) {
            this.categoryImage = imageView;
            this.loadData();
        }

        TextView categoryText;
        public void setCategoryText(TextView textView) {
            this.categoryText = textView;
            this.loadData();
        }

        View itemView;
        public void setItemView(View itemView) {
            this.itemView = itemView;
            if (this.category != null)
                this.itemView.setTag(R.id.BO_Category,this.category);
        }

        private void loadData() {
            if ((this.category != null) && (this.categoryImage != null) && (this.categoryText != null)) {
                this.category.setCategory(this.categoryImage,this.categoryText);
            }
        }

        public CategoryViewHolder (View itemView) {
            this.itemView = itemView;
            this.itemView.setTag(R.id.Explore_ListViewHolder,this);
            this.categoryImage = (ImageView)itemView.findViewById(R.id.explore_categories_item_image);
            this.categoryText = (TextView)itemView.findViewById(R.id.explore_categories_item_name);
            this.loadData();
        }

        public CategoryViewHolder(View itemView, Category category) {
            this(itemView);
            this.setCategory(category);
        }
    }
}
