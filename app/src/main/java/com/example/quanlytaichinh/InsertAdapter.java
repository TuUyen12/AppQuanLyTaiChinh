package com.example.quanlytaichinh;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class InsertAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<InsertItem> gridItems;
    private int selectedPosition = -1;

    public InsertAdapter(Context context, ArrayList<InsertItem> gridItems) {
        this.context = context;
        this.gridItems = gridItems;
    }

    @Override
    public int getCount() {
        return gridItems.size();
    }

    @Override
    public Object getItem(int position) {
        return gridItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.iv_item);
        TextView textView = convertView.findViewById(R.id.tv_item);

        InsertItem gridItem = gridItems.get(position);
        imageView.setImageResource(gridItem.getImageResId());
        textView.setText(gridItem.getTitle());
        // Thay đổi màu nền của item dựa trên vị trí đã chọn
        if (position == selectedPosition) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.color11)); // Màu nền đã chọn
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT); // Màu nền mặc định
        }

        return convertView;
    }
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
}
