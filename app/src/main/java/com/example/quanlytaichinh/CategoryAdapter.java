package com.example.quanlytaichinh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quanlytaichinh.DataBase.DTBase;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DTBase.Category> listviewItems;
    private int selectedPosition = -1;

    public CategoryAdapter(Context context, ArrayList<DTBase.Category> listviewItems) {
        this.context = context;
        this.listviewItems = listviewItems;
    }
    @Override
    public int getCount() {
        return listviewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listviewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_edit_category, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.iv_item);
        TextView tvCategoryType = convertView.findViewById(R.id.tv_category_type);
        TextView tvCategoryName = convertView.findViewById(R.id.tv_category_name);

        DTBase.Category listviewItem = listviewItems.get(position);
        imageView.setImageResource(listviewItem.getCategoryIcon());
        tvCategoryName.setText(listviewItem.getCategoryName());
        tvCategoryType.setText(listviewItem.getCategoryType());

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
