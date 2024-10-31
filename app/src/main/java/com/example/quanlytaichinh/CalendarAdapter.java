package com.example.quanlytaichinh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private List<CalendarItem> itemList;

    public CalendarAdapter(Context context, List<CalendarItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_calendar, parent, false);
        }

        CalendarItem currentItem = itemList.get(position);

        ImageView ivItem = convertView.findViewById(R.id.iv_item);
        TextView tvItem = convertView.findViewById(R.id.tv_item); // categoryType
        TextView tvNote = convertView.findViewById(R.id.tv_note); // title
        TextView tvMoney = convertView.findViewById(R.id.tv_money);

        ivItem.setImageResource(currentItem.getImageResId());

        // Hiển thị categoryType trong tv_item
        tvItem.setText(currentItem.getCategoryType());

        // Hiển thị title trong tv_note
        tvNote.setText(currentItem.getTitle());

        // Định dạng tiền tệ Việt Nam
        Locale vietnamLocale = new Locale("vi", "VN");
        String formattedAmount = NumberFormat.getCurrencyInstance(vietnamLocale).format(currentItem.getMoney());
        tvMoney.setText(formattedAmount);

        return convertView;
    }


}
