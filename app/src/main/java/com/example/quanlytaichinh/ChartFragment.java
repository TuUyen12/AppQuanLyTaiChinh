package com.example.quanlytaichinh;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

public class ChartFragment extends Fragment {

    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chart_layout, container, false);

        // Khởi tạo PieChart
        pieChart = view.findViewById(R.id.pieChart);

        // Tạo dữ liệu cho biểu đồ
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(10f, "Category 1"));
        entries.add(new PieEntry(20f, "Category 2"));
        entries.add(new PieEntry(30f, "Category 3"));
        entries.add(new PieEntry(40f, "Category 4"));

        PieDataSet dataSet = new PieDataSet(entries, "Data Set");

        // Thiết lập màu sắc cho từng phần của biểu đồ
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.color1)); // Màu từ tài nguyên
        colors.add(getResources().getColor(R.color.color2));
        colors.add(getResources().getColor(R.color.color3));
        colors.add(getResources().getColor(R.color.color4));

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

        return view;
    }
}