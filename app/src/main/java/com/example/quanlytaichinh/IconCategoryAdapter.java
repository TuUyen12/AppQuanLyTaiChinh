package com.example.quanlytaichinh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class IconCategoryAdapter extends BaseAdapter {
    private Context context;
    private List<IconCategoryItem> iconCategoryItems;
    public IconCategoryAdapter(Context context, List<IconCategoryItem> iconCategoryItems) {
        this.context = context;
        this.iconCategoryItems = iconCategoryItems;

    }
    @Override
    public int getCount() {
        return iconCategoryItems.size();
    }
    @Override
    public Object getItem(int position) {
        return iconCategoryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.icon_category, parent, false);
        }
        ImageView ivItem = convertView.findViewById(R.id.iv_item);
        IconCategoryItem item = iconCategoryItems.get(position);
        ivItem.setImageResource(item.getImageResId());

        return convertView;
    }
}