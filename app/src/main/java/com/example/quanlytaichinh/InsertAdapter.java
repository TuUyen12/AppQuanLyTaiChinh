package com.example.quanlytaichinh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.DataBase.InsertItem;

import java.util.ArrayList;

public class InsertAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DTBase.Category> gridItems;
    private int selectedPosition = -1;

    public InsertAdapter(Context context, ArrayList<DTBase.Category> gridItems) {
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

        DTBase.Category gridItem = gridItems.get(position);
        imageView.setImageResource(gridItem.getCategoryIcon());
        textView.setText(gridItem.getCategoryName());

        // Áp dụng background dựa trên vị trí đã chọn
        if (position == selectedPosition) {
            convertView.setBackgroundResource(R.drawable.gridview_item_style_color); // Màu nền đã chọn
        } else {
            convertView.setBackgroundResource(R.drawable.gridview_item_style); // Màu nền mặc định
        }

        return convertView;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged(); // Cập nhật lại GridView khi thay đổi vị trí đã chọn
    }

}
