package com.example.quanlytaichinh;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChartAdapter extends BaseAdapter {
    private Context context;
    private List<DTBase.Financial> itemList;
    private ArrayList<DTBase.Category> category;
    // Constructor
    public ChartAdapter(Context context, List<DTBase.Financial> itemList) {
        this.context = context;
        this.itemList = itemList;
        // Lấy danh sách Category từ SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyCategory", MODE_PRIVATE);
        String categoryJson = sharedPreferences.getString("category", "[]"); // Mặc định là mảng rỗng nếu không có dữ liệu
        Gson gson = new Gson();
        Type categoryListType = new TypeToken<ArrayList<DTBase.Category>>() {}.getType();
        this.category = gson.fromJson(categoryJson, categoryListType);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_chartfragment, parent, false);
        }

        DTBase.Financial currentItem = itemList.get(position);

        ImageView ivItem = convertView.findViewById(R.id.iv_item);
        TextView tvItem = convertView.findViewById(R.id.tv_item);
        TextView tvCategoryName = convertView.findViewById(R.id.tv_category_name);
        TextView tvNote = convertView.findViewById(R.id.tv_financial_name);
        TextView tvMoney = convertView.findViewById(R.id.tv_money);

        // Hiển thị categoryType trong tv_item
        tvItem.setText(currentItem.getFinancialType());

        // Hiển thị title trong tv_note
        tvNote.setText(currentItem.getFinancialName());

        // Định dạng tiền tệ Việt Nam
        Locale vietnamLocale = new Locale("vi", "VN");
        String formattedAmount = NumberFormat.getCurrencyInstance(vietnamLocale).format(currentItem.getFinancialAmount());
        tvMoney.setText(formattedAmount);

        // Tìm kiếm Category tương ứng với categoryId của Financial
        for (DTBase.Category cat : category) {
            if (cat.getCategoryID() == currentItem.getCategoryID()) {
                ivItem.setImageResource(cat.getCategoryIcon()); // Gán categoryIcon vào ImageView
                tvCategoryName.setText(cat.getCategoryName());

                break;
            }
        }

        return convertView;
    }
    public void setData(List<DTBase.Financial> newItemList) {
        if (itemList != null) {
            itemList.clear();  // Xóa tất cả các phần tử trong danh sách
            itemList.addAll(newItemList);  // Thêm dữ liệu mới vào itemList
            notifyDataSetChanged();  // Cập nhật giao diện
        }
    }

    public void clear() {
        if (itemList != null) {
            itemList.clear();  // Xóa tất cả các phần tử trong danh sách
            notifyDataSetChanged(); // Cập nhật giao diện
        }
    }
}
