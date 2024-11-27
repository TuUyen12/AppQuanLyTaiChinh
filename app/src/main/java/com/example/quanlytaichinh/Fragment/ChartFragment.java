package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.quanlytaichinh.CalendarAdapter;
import com.example.quanlytaichinh.CalendarItem;
import com.example.quanlytaichinh.CustomSpinnerAdapter;
import com.example.quanlytaichinh.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartFragment extends Fragment {
    private PieChart pieChart;
    private LinearLayout linearLayout;
    private TextView selectedYearTextView;
    private String currentType = "expense"; // Default type
    private boolean isMonthlyView = false; // View state
    private ImageButton ibMonth;
    private ImageButton ibYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_layout, container, false);
        Spinner spinner = view.findViewById(R.id.spinner_type);

        String[] typeOptions = {"Expense", "Income"};
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(requireContext(), typeOptions);
        spinner.setAdapter(adapter);
        spinner.setSelection(0); // Default selection

        ibMonth = view.findViewById(R.id.ib_month);
        ibYear = view.findViewById(R.id.ib_year);
        ibMonth.setImageResource(R.drawable.month1_with_size);
        ibYear.setImageResource(R.drawable.year_with_size);

        linearLayout = view.findViewById(R.id.linearLayout);
        pieChart = view.findViewById(R.id.pieChart);
        ListView lv_chart = view.findViewById(R.id.lv_chart);

        // Set up the year selection
        setupYearSelection();

        // Month
        ibMonth.setOnClickListener(v -> {
            isMonthlyView = true;
            Log.d("ChartFragment", "Monthly view selected");
            Toast.makeText(getContext(), "Changed to Monthly view", Toast.LENGTH_SHORT).show();
            updateView();
        });


        ibYear.setOnClickListener(v -> {
            isMonthlyView = false;
            Log.d("ChartFragment", "Yearly view selected");
            Toast.makeText(getContext(), "Changed to Yearly view", Toast.LENGTH_SHORT).show();
            updateView();
        });

        // Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPosition(position);
                currentType = typeOptions[position];
                updateListView(lv_chart); // Cập nhật ListView khi chọn loại mới
                showData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Khởi tạo SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        // Lấy giá trị của `isPersonal`, với giá trị mặc định là `false` nếu biến chưa được lưu
        boolean isPersonal = sharedPreferences.getBoolean("isPersonnal", false);
        List<CalendarItem> calendarItems = new ArrayList<>();

        updateListView(lv_chart);

        lv_chart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                ShowChooseEdit_Delete();
                return false;
            }
        });

        return view;
    }

    private void setupYearSelection() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = 2000; year <= 2024; year++) {
            TextView textView = createYearTextView(year);
            if (year == currentYear) {
                selectedYearTextView = textView;
                selectedYearTextView.setBackgroundColor(Color.LTGRAY);
                showData(); // Show current year's data
            }
            linearLayout.addView(textView);
        }
    }

    private TextView createYearTextView(int year) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(String.valueOf(year));
        textView.setPadding(20, 20, 20, 20);
        textView.setTextSize(18);
        textView.setOnClickListener(v -> {
            if (selectedYearTextView != null) {
                selectedYearTextView.setBackgroundColor(Color.TRANSPARENT);
            }
            selectedYearTextView = textView;
            selectedYearTextView.setBackgroundColor(Color.LTGRAY);
            showData(); // Update data for selected year
        });
        return textView;
    }

    private void updateView() {
        linearLayout.removeAllViews(); // Clear previous views before adding new ones
        if (isMonthlyView) {
            ibMonth.setImageResource(R.drawable.month_with_size);
            ibYear.setImageResource(R.drawable.year1_with_size);
            for (int month = 1; month <= 12; month++) {
                TextView monthTextView = createMonthTextView(month);
                linearLayout.addView(monthTextView);
            }
        } else {
            ibMonth.setImageResource(R.drawable.month1_with_size);
            ibYear.setImageResource(R.drawable.year_with_size);
            setupYearSelection();
        }
        showData(); // Refresh data after changing view
    }


    private TextView createMonthTextView(int month) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(String.valueOf(month));
        textView.setPadding(20, 20, 20, 20);
        textView.setTextSize(18);
        textView.setOnClickListener(v -> {
            // Handle month selection if needed
        });
        return textView;
    }

    private void showData() {
        if (selectedYearTextView != null) {
            String year = selectedYearTextView.getText().toString();
            showYearData(year);
        }
    }

    private void showYearData(String year) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean isPersonal = sharedPreferences.getBoolean("isPersonnal", false);

        if (currentType.equals("Expense")) {
            if (isPersonal) {
                pieEntries.add(new PieEntry(25f, "Bill"));
                pieEntries.add(new PieEntry(15f, "Education"));
                pieEntries.add(new PieEntry(30f, "Food"));
                pieEntries.add(new PieEntry(30f, "Other"));
            } else {
                pieEntries.add(new PieEntry(15f, "Marketing"));
                pieEntries.add(new PieEntry(15f, "Maintenance"));
                pieEntries.add(new PieEntry(30f, "Project Costs"));
                pieEntries.add(new PieEntry(20f, "Personnel Costs"));
            }
        } else if (currentType.equals("Income")) {
            pieEntries.add(new PieEntry(40f, "Salary"));
            pieEntries.add(new PieEntry(30f, "Investments"));
            pieEntries.add(new PieEntry(20f, "Freelancing"));
            pieEntries.add(new PieEntry(10f, "Other"));
        }

        updateChartDataBasedOnType(currentType, pieEntries, year);
    }


    private void updateChartDataBasedOnType(String type, ArrayList<PieEntry> pieEntries, String year) {
        PieDataSet dataSet = new PieDataSet(pieEntries, isMonthlyView ? "Data for " + " (Month)" : "Data for " +" (Year)");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.color1));
        colors.add(getResources().getColor(R.color.color2));
        colors.add(getResources().getColor(R.color.color3));
        colors.add(getResources().getColor(R.color.color4));

        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.TRANSPARENT);
        dataSet.setValueTextSize(0f);
        dataSet.setSliceSpace(3f);

        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(12f);

        pieChart.setData(new PieData(dataSet));
        pieChart.animateY(1400);
        pieChart.invalidate(); // Refresh the chart
    }
    // Thêm phương thức updateListView() để cập nhật ListView
    private void updateListView(ListView lv_chart) {
        List<CalendarItem> calendarItems = new ArrayList<>();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isPersonal = sharedPreferences.getBoolean("isPersonal", false);

    }

    private void ShowChooseEdit_Delete(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit or Delete");
        // Xử lý sự kiện khi người dùng chọn Edit

        builder.setPositiveButton("Edit", (dialog, which) -> {
            //Sự kiện sửa

        });
        // Xử lý sự kiện khi người dùng chọn Delete
        builder.setNegativeButton("Delete", (dialog, which) -> {

        });
        // Hiển thị dialog
        builder.create().show();
    }

}
