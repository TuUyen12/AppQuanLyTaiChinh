package com.example.quanlytaichinh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SettingAdapter extends BaseAdapter {
    private Context context;
    private List<SettingItem> settingItems;

    public SettingAdapter(Context context, List<SettingItem> settingItems) {
        this.context = context;
        this.settingItems = settingItems;
    }

    @Override
    public int getCount() {
        return settingItems.size();
    }

    @Override
    public Object getItem(int position) {
        return settingItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview, parent, false);
        }

        ImageView ivItem = convertView.findViewById(R.id.iv_item);
        TextView tvItem = convertView.findViewById(R.id.tv_item);

        SettingItem item = settingItems.get(position);

        ivItem.setImageResource(item.getImageResId());
        tvItem.setText(item.getName());

        return convertView;
    }
}