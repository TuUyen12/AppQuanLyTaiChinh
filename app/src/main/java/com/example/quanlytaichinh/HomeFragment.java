package com.example.quanlytaichinh;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import android.content.SharedPreferences;
import android.content.Context;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    // Biến SharedPreferences để lưu vị trí đã chọn của Spinner
    private static final String PREFS_NAME = "SpinnerPrefs";
    private static final String KEY_SELECTED_POSITION = "SelectedPosition";
    private static final String PREFS_NAME_YEAR = "SpinnerYear";
    private static final String KEY_SELECTED_YEAR = "SelectedYear";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.home_layout, container, false);

        // TextView để hiển thị số dư
        TextView textViewMoney = view.findViewById(R.id.tv_balance);
        double amount = 5000000; // Giả sử số tiền

        // Định dạng tiền tệ Việt Nam
        Locale vietnamLocale = new Locale("vi", "VN");
        String formattedAmount = NumberFormat.getCurrencyInstance(vietnamLocale).format(amount);
        textViewMoney.setText("**** đ"); // Ẩn số tiền mặc định

        ImageButton ib_eye = view.findViewById(R.id.ib_eye);
        final boolean[] isShowingAmount = {false}; // Ẩn ban đầu

        ib_eye.setOnClickListener(v -> {
            if (isShowingAmount[0]) {
                isShowingAmount[0] = false;
                ib_eye.setImageResource(R.drawable.hide_with_size1);
                textViewMoney.setText("**** đ"); // Ẩn số tiền
            } else {
                isShowingAmount[0] = true;
                ib_eye.setImageResource(R.drawable.show_with_size1);
                textViewMoney.setText(formattedAmount); // Hiển thị số tiền
            }
        });
        //Thiết lập spinner chọn khoảng thời gian cho biểu đồ chi phí và thu nhập

        Spinner spinnerDuration = view.findViewById(R.id.spinner_duration);
        String[] durationOptions = {"Today", "This Week", "This Month", "This Year"};
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(requireContext(), durationOptions);
        spinnerDuration.setAdapter(adapter);

        // Lấy vị trí đã lưu trong SharedPreferences và cài đặt cho Spinner
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedPosition = prefs.getInt(KEY_SELECTED_POSITION, 0);
        spinnerDuration.setSelection(savedPosition);
        adapter.setSelectedPosition(savedPosition);

        // Sự kiện khi người dùng chọn một item trong Spinner
        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Cập nhật vị trí item đã chọn trong adapter
                adapter.setSelectedPosition(position);
                // Lưu vị trí đã chọn vào SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(KEY_SELECTED_POSITION, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không có item nào được chọn
            }
        });

        // Thiết lập spinner year:
        Spinner spinnerYear = view.findViewById(R.id.spinner_year);
        String[] yearOptions = {"2024","2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010"};
        CustomSpinnerAdapter adapterYear = new CustomSpinnerAdapter(requireContext(), yearOptions);
        spinnerYear.setAdapter(adapterYear);
        adapterYear.setSelectedPosition(0);

        // Lấy vị trí đã lưu trong SharedPreferences và cài đặt cho Spinner
        SharedPreferences yearPrefs = requireContext().getSharedPreferences(PREFS_NAME_YEAR, Context.MODE_PRIVATE);
        int savedYearPosition = yearPrefs.getInt(KEY_SELECTED_YEAR, 0);
        spinnerYear.setSelection(savedYearPosition);
        adapterYear.setSelectedPosition(savedYearPosition);

        // Sự kiện khi người dùng chọn một item trong Spinner year
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Cập nhật vị trí item đã chọn trong adapter
                adapterYear.setSelectedPosition(position);
                // Lưu vị trí đã chọn vào SharedPreferences
                SharedPreferences.Editor editor = yearPrefs.edit();
                editor.putInt(KEY_SELECTED_YEAR, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không có item nào được chọn
            }
        });


        // Thiết lập PieChart
        PieChart pieChart = view.findViewById(R.id.pieChart);
        setupPieChart(pieChart, getUserPieData());


        // Thiết lập BarChart chi phí và thu nhập
        BarChart barChart = view.findViewById(R.id.barChart);
        setupBarChartExpenseIncome(barChart);

        // Thiết lập BarChart tài chính với dữ liệu tùy chỉnh
        BarChart barChartFinancial = view.findViewById(R.id.barChart_financial);
        ArrayList<BarEntry> userFinancialData = getUserFinancialData(); // Giả sử lấy dữ liệu từ người dùng
        setupBarChartFinancial(barChartFinancial, userFinancialData);

        return view;
    }

    // Hàm thiết lập PieChart
    private void setupPieChart(PieChart pieChart, ArrayList<PieEntry> userPieEntries) {
        PieDataSet pieDataSet = new PieDataSet(userPieEntries, "");

        // Màu cho các phần của PieChart
        ArrayList<Integer> pieColors = new ArrayList<>();
        pieColors.add(getResources().getColor(R.color.color1));
        pieColors.add(getResources().getColor(R.color.color2));
        pieColors.add(getResources().getColor(R.color.color3));

        pieDataSet.setColors(pieColors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueFormatter(new PercentFormatter());
        pieDataSet.setSliceSpace(3f);

        // Tạo dữ liệu cho PieChart
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        // Ẩn giá trị và nhãn hiển thị trên biểu đồ
        pieDataSet.setValueTextColor(Color.TRANSPARENT); // Ẩn số liệu
        pieDataSet.setValueTextSize(0f); // Không hiển thị kích thước văn bản
        pieDataSet.setSliceSpace(3f);

        // Ẩn nhãn tên loại chi phí trong các phần của PieChart
        pieChart.setDrawEntryLabels(false);

        // Thiết lập chú thích (Legend)
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false); // Chú thích nằm bên ngoài biểu đồ
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(14f);

        // Ẩn tiêu đề của PieChart
        pieChart.getDescription().setEnabled(false);

        pieChart.invalidate(); // Làm mới PieChart
    }

    // Giả lập hàm lấy dữ liệu người dùng cho PieChart
    private ArrayList<PieEntry> getUserPieData() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(25f, "Bill"));       // Hóa đơn
        pieEntries.add(new PieEntry(15f, "Education"));  // Giáo dục
        pieEntries.add(new PieEntry(30f, "Food"));       // Ăn uống
        // Dữ liệu sẽ thay đổi tùy thuộc vào nguồn người dùng
        return pieEntries;
    }


    // Hàm thiết lập BarChart với hai cột: chi phí và thu nhập
    private void setupBarChartExpenseIncome(BarChart barChart) {
        // Giả sử lấy dữ liệu chi phí và thu nhập từ người dùng
        float userExpense = getUserExpense(); // Hàm giả định để lấy dữ liệu chi phí
        float userIncome = getUserIncome();   // Hàm giả định để lấy dữ liệu thu nhập

        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        barEntries1.add(new BarEntry(0, userExpense)); // Cột 1 (Chi phí)

        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        barEntries2.add(new BarEntry(1, userIncome)); // Cột 2 (Thu nhập)

        BarDataSet barDataSet1 = new BarDataSet(barEntries1, "Expense");
        barDataSet1.setColor(getResources().getColor(R.color.color5));

        BarDataSet barDataSet2 = new BarDataSet(barEntries2, "Income");
        barDataSet2.setColor(getResources().getColor(R.color.color6));

        BarData barData = new BarData(barDataSet1, barDataSet2);
        barData.setBarWidth(0.4f);

        barChart.setData(barData);
        customizeBarChart(barChart);
        barChart.invalidate();
    }

    // Hàm giả định để lấy dữ liệu chi phí từ người dùng
    private float getUserExpense() {

        return 10f; // Ví dụ dữ liệu chi phí
    }

    // Hàm giả định để lấy dữ liệu thu nhập từ người dùng
    private float getUserIncome() {

        return 20f; // Ví dụ dữ liệu thu nhập
    }

    // Hàm thiết lập BarChart tài chính với dữ liệu từ người dùng
    private void setupBarChartFinancial(BarChart barChart, ArrayList<BarEntry> barEntries) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "Expense");
        barDataSet.setColor(getResources().getColor(R.color.color1));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.6f);
        // Ẩn giá trị và nhãn hiển thị trên biểu đồ
        barDataSet.setValueTextColor(Color.TRANSPARENT); // Ẩn số liệu
        barDataSet.setValueTextSize(0f); // Không hiển thị kích thước văn bản

        barChart.setData(barData);
        customizeBarChartFinancial(barChart);
        barChart.invalidate();
    }

    // Hàm chung để tùy chỉnh BarChart cho Expense và Income
    private void customizeBarChart(BarChart barChart) {
        barChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        barChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        barChart.getLegend().setDrawInside(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false); // Ẩn các nhãn trên trục X

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setEnabled(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        barChart.getDescription().setEnabled(false);
    }

    private void customizeBarChartFinancial(BarChart barChart) {
        barChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        barChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        barChart.getLegend().setDrawInside(false);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true); // Hiển thị đường kẻ trục
        xAxis.setLabelCount(12); // Hiển thị 12 nhãn cho 12 tháng


        // Cài đặt bộ định dạng cho trục X để hiển thị tháng
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
        }));


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(true); // Hiển thị đường kẻ trục Y
        leftAxis.setEnabled(true); // Bật trục Y

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        barChart.getDescription().setEnabled(false);
    }



    // Hàm giả định để lấy dữ liệu từ người dùng cho BarChart tài chính
    private ArrayList<BarEntry> getUserFinancialData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        // Giả sử các giá trị này tương ứng với từng tháng
        barEntries.add(new BarEntry(0, 10));  // Tháng 1
        barEntries.add(new BarEntry(1, 20));  // Tháng 2
        barEntries.add(new BarEntry(2, 30));  // Tháng 3
        barEntries.add(new BarEntry(3, 25));  // Tháng 4
        barEntries.add(new BarEntry(4, 15));  // Tháng 5
        barEntries.add(new BarEntry(5, 0));  // Tháng 6
        barEntries.add(new BarEntry(6, 35));  // Tháng 7
        barEntries.add(new BarEntry(7, 50));  // Tháng 8
        barEntries.add(new BarEntry(8, 0));  // Tháng 9
        barEntries.add(new BarEntry(9, 45));  // Tháng 10
        barEntries.add(new BarEntry(10, 55)); // Tháng 11
        barEntries.add(new BarEntry(11, 70)); // Tháng 12
        return barEntries;
    }

}