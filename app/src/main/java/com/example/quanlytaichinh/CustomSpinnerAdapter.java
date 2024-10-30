package com.example.quanlytaichinh;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.quanlytaichinh.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private int selectedPosition = -1;

    public CustomSpinnerAdapter(Context context, String[] values) {
        super(context, R.layout.item_spinner, values);
        this.context = context;
        this.values = values;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Hiển thị item đã chọn trên Spinner (khi đóng dropdown)
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.textViewItem);
        textView.setText(values[position]);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Hiển thị các item trong dropdown của Spinner
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.textViewItem);
        textView.setText(values[position]);

        // Đổi màu nền của item đã chọn trong dropdown
        if (position == selectedPosition) {
            textView.setBackgroundColor(android.graphics.Color.GRAY);
        } else {
            textView.setBackgroundColor(android.graphics.Color.WHITE);
        }
        return view;
    }
}
