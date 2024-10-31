package com.example.quanlytaichinh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TimeFormatAdapter extends ArrayAdapter<String> {
    public TimeFormatAdapter(Context context, List<String> formats) {
        super(context, 0, formats);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Kiểm tra nếu view đã được tái sử dụng, nếu không thì tạo mới
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Lấy định dạng thời gian
        String format = getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(format);

        return convertView;
    }
}
