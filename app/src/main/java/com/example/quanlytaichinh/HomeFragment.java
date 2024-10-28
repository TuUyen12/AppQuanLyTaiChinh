package com.example.quanlytaichinh;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_layout, container, false);

        // Tìm TextView trong layout đã inflate
        TextView textViewMoney = view.findViewById(R.id.tv_balance);

        // Giả sử có một số tiền
        double amount = 5000000;

        // Định dạng tiền tệ Việt Nam
        Locale vietnamLocale = new Locale("vi", "VN");
        String formattedAmount = NumberFormat.getCurrencyInstance(vietnamLocale).format(amount);

        // Khởi tạo giá trị cho textViewMoney
        textViewMoney.setText("**** đ"); // Mặc định là ẩn số tiền

        ImageButton ib_eye = view.findViewById(R.id.ib_eye);
        final boolean[] isShowingAmount = {false}; // Initially hidden

        ib_eye.setOnClickListener(v -> {
            if (isShowingAmount[0]) {
                isShowingAmount[0] = false;
                ib_eye.setImageResource(R.drawable.hide_with_size1);
                textViewMoney.setText("**** đ"); // Ẩn số tiền
            } else {
                isShowingAmount[0] = true;
                ib_eye.setImageResource(R.drawable.show_with_size1);
                textViewMoney.setText(formattedAmount); // Hiện số tiền
            }
        });

        //Các chart
        //pieChart
        // Khởi tạo PieChart
        PieChart pieChart = view.findViewById(R.id.pieChart);
        loadPieChart(pieChart);

        //collumChart

        BarChart barChart = view.findViewById(R.id.barChart);
        loadBarChart(barChart);

        //collumChart_financial
        BarChart barChart_financial = view.findViewById(R.id.barChart_financial);
        loadBarChart(barChart_financial);

        return view; // Trả về view sau khi đã thực hiện các thao tác
    }
    private void loadPieChart(PieChart pieChart ){

        // Tạo dữ liệu cho biểu đồ
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(10f, "Bill"));
        entries.add(new PieEntry(20f, "Education"));
        entries.add(new PieEntry(30f, "Food"));

        PieDataSet dataSet = new PieDataSet(entries, "Data Set");

        // Thiết lập màu sắc cho từng phần của biểu đồ
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.color1)); // Màu từ tài nguyên
        colors.add(getResources().getColor(R.color.color2));
        colors.add(getResources().getColor(R.color.color3));

        dataSet.setColors(colors); // Áp dụng màu sắc cho PieDataSet
        dataSet.setValueTextColor(Color.BLACK); // Màu chữ hiển thị giá trị thành đen
        dataSet.setValueTextSize(16f); // Kích thước chữ hiển thị giá trị
        dataSet.setValueFormatter(new PercentFormatter()); // Định dạng hiển thị giá trị dưới dạng phần trăm
        dataSet.setSliceSpace(3f); // Khoảng cách giữa các phần của biểu đồ

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Đặt màu cho nhãn (labels) hiển thị tên danh mục
        pieChart.setEntryLabelColor(Color.BLACK); // Đặt màu cho nhãn bên ngoài thành đen

        pieChart.invalidate(); // Refresh biểu đồ
    }
    private void loadBarChart(BarChart barChart) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        // Thêm dữ liệu vào biểu đồ
        entries.add(new BarEntry(0, 10)); // Cột 1
        entries.add(new BarEntry(1, 20)); // Cột 2
        entries.add(new BarEntry(2, 30)); // Cột 3
        entries.add(new BarEntry(3, 25)); // Cột 4

        BarDataSet barDataSet = new BarDataSet(entries, "Dữ liệu cột");
        BarData barData = new BarData(barDataSet);

        // Tùy chỉnh màu sắc
        barDataSet.setColor(getResources().getColor(R.color.color1));
        barChart.setData(barData);
        barChart.invalidate(); // Cập nhật biểu đồ
    }

}