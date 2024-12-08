package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
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
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartFragment extends Fragment {
    private PieChart pieChart;
    private LinearLayout linearLayout;
    private TextView selectedYearTextView,
            selectedMonthTextView;
    private String currentType = "Expense"; // mặc định
    private boolean isMonthlyView = true; // Mặc định khi mở lên hiển thị tháng
    private ImageButton ibMonth;
    private ImageButton ibYear;
    private ListView lv_chart;
    private Spinner spinner;
    private HorizontalScrollView horizontalScrollView;

    private boolean isPersonal;
    private String categoryJson;
    private String financialJson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_layout, container, false);
        setupSharedPreferences();
        initVariable(view);

        // Spinner chọn expense hay income
        String[] typeOptions = {"Expense", "Income"};
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(requireContext(), typeOptions);
        spinner.setAdapter(adapter);
        spinner.setSelection(0); // mặc định là expense

        updateView();

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

                currentType = position == 0 ? "Expense" : "Income"; // 0 : Expense, 1 : Income

                int time = isMonthlyView ? Calendar.getInstance().get(Calendar.MONTH) + 1 : Calendar.getInstance().get(Calendar.YEAR);
                ArrayList<PieEntry> pieEntries = getUserPieData(time, isMonthlyView);
                setupPieChart(pieChart, pieEntries);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lv_chart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });

        return view;
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isPersonal = sharedPreferences.getBoolean("isPersonal", false);
        SharedPreferences categorySharedPreferences = getActivity().getSharedPreferences("MyCategory", MODE_PRIVATE);
        categoryJson = categorySharedPreferences.getString("category", "[]");
        SharedPreferences financialSharedPreferences = getActivity().getSharedPreferences("MyFinancials", MODE_PRIVATE);
        financialJson = financialSharedPreferences.getString("financialList", "[]");
    }

    private void setupYearSelection() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= 2004; year--) {
            TextView textView = createTextView(year);
            if (year == currentYear) {
                selectedYearTextView = textView;
                selectedYearTextView.setBackgroundColor(Color.LTGRAY);
            }
            linearLayout.addView(textView);
        }
    }
    private void setupMonthSelection() {
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        for (int month = 0; month < 12; month++) {
            TextView textView = createTextView(month + 1);
            if (month == currentMonth) {
                selectedMonthTextView = textView;
                selectedMonthTextView.setBackgroundColor(Color.LTGRAY);
            }
            linearLayout.addView(textView);
        }
    }

    private void initVariable(View view){


        ibMonth = view.findViewById(R.id.ib_month);
        ibYear = view.findViewById(R.id.ib_year);
        ibMonth.setImageResource(R.drawable.month_with_size);
        ibYear.setImageResource(R.drawable.year1_with_size);
        linearLayout = view.findViewById(R.id.linearLayout);
        pieChart = view.findViewById(R.id.pieChart);
        lv_chart = view.findViewById(R.id.lv_chart);
        spinner = view.findViewById(R.id.spinner_type);
        horizontalScrollView = view.findViewById(R.id.horizontalScrollView);
    }

    private TextView createTextView(int time) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(String.valueOf(time));
        textView.setPadding(20, 20, 20, 20);
        textView.setTextSize(18);
        textView.setOnClickListener(v -> {
            if (isMonthlyView) {
                if (selectedMonthTextView != null) {
                    selectedMonthTextView.setBackgroundColor(Color.TRANSPARENT);
                }
                selectedMonthTextView = textView;
            } else {
                if (selectedYearTextView != null) {
                    selectedYearTextView.setBackgroundColor(Color.TRANSPARENT);
                }
                selectedYearTextView = textView;
            }
            textView.setBackgroundColor(Color.LTGRAY);

            // Cập nhật lại PieChart và ListView
            getUserPieData(time, isMonthlyView);  // Lấy dữ liệu mới
            setupPieChart(pieChart, getUserPieData(time, isMonthlyView));  // Cập nhật PieChart
        });
        return textView;
    }



    //Thêm dữ liệu cho PieChart
    private ArrayList<PieEntry> getUserPieData(int time, boolean isMonthlyView) {
        Gson gson = new Gson();
        Type categoryType = new TypeToken<List<DTBase.Category>>() {}.getType();
        List<DTBase.Category> categoryList = gson.fromJson(categoryJson, categoryType);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<DTBase.Financial> calendarItems = new ArrayList<>();
        DTBase dtBase = new DTBase();


        if (financialJson != null) {
            Type financialType = new TypeToken<List<DTBase.Financial>>() {}.getType();
            List<DTBase.Financial> financialList = gson.fromJson(financialJson, financialType);

            HashMap<Integer, Float> categoryAmountMap = new HashMap<>();

            // Duyệt qua danh sách chi tiêu hoặc thu nhập
            for (DTBase.Financial financial : financialList) {
                if (currentType.equals("Expense")) { // Loại chi tiêu
                    if (isMonthlyView) {
                        if (isPersonal && dtBase.getFinancialMonth(financial) == time && financial.getCategoryID() < 9) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            calendarItems.add(financial);
                        } else if (!isPersonal && dtBase.getFinancialMonth(financial) == time && financial.getCategoryID() >= 20) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            calendarItems.add(financial);
                        }
                    } else { // Chế độ xem theo năm
                        if (isPersonal && dtBase.getFinancialYear(financial) == time && financial.getCategoryID() < 9) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            calendarItems.add(financial);
                        } else if (!isPersonal && dtBase.getFinancialYear(financial) == time && financial.getCategoryID() >= 20) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            calendarItems.add(financial);
                        }
                    }
                } else if (currentType.equals("Income")) { // Loại thu nhập
                    if (isMonthlyView) {
                        if (isPersonal && dtBase.getFinancialMonth(financial) == time && financial.getCategoryID() > 8 && financial.getCategoryID() < 20) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            calendarItems.add(financial);
                        } else if (!isPersonal && dtBase.getFinancialMonth(financial) == time && financial.getCategoryID() > 26) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            calendarItems.add(financial);
                        }
                    } else {
                        if (isPersonal && dtBase.getFinancialYear(financial) == time && financial.getCategoryID() > 8 && financial.getCategoryID() < 20) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            calendarItems.add(financial);
                        } else if (!isPersonal && dtBase.getFinancialYear(financial) == time && financial.getCategoryID() > 26) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );calendarItems.add(financial);

                        }
                    }
                }
            }

            // Chuyển đổi dữ liệu thành PieEntry
            for (Map.Entry<Integer, Float> entry : categoryAmountMap.entrySet()) {
                int categoryID = entry.getKey();
                float amount = entry.getValue();

                for (DTBase.Category category : categoryList) {
                    if (category.getCategoryID() == categoryID) {
                        pieEntries.add(new PieEntry(amount, category.getCategoryName()));
                    }
                }
            }
        }

        lv_chart.setAdapter(null);
        CalendarAdapter adapter = new CalendarAdapter(getContext(), calendarItems);
        // Cập nhật lại ListView
        if (calendarItems != null && !calendarItems.isEmpty()) {
            lv_chart.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "No financial data available for the selected date", Toast.LENGTH_SHORT).show();
        }

        return pieEntries;
    }

    private void updateView() {
        linearLayout.removeAllViews();
        if (isMonthlyView) {
            ibMonth.setImageResource(R.drawable.month_with_size);
            ibYear.setImageResource(R.drawable.year1_with_size);
            setupMonthSelection();
        } else {
            ibMonth.setImageResource(R.drawable.month1_with_size);
            ibYear.setImageResource(R.drawable.year_with_size);
            setupYearSelection();
        }

    }

    // Hàm thiết lập màu sắc PieChart
    private void setupPieChart(PieChart pieChart, ArrayList<PieEntry> userPieEntries) {
        PieDataSet pieDataSet = new PieDataSet(userPieEntries, "");

        // Màu cho các phần của PieChart
        ArrayList<Integer> pieColors = new ArrayList<>();
        pieColors.add(getResources().getColor(R.color.color1));
        pieColors.add(getResources().getColor(R.color.color2));
        pieColors.add(getResources().getColor(R.color.color3));
        pieColors.add(getResources().getColor(R.color.color4));
        pieColors.add(getResources().getColor(R.color.color5));
        pieColors.add(getResources().getColor(R.color.color6));
        pieColors.add(getResources().getColor(R.color.color7));
        pieColors.add(getResources().getColor(R.color.color8));
        pieColors.add(getResources().getColor(R.color.color9));
        pieColors.add(getResources().getColor(R.color.color10));

        // Thiết lập màu sắc cho các phần
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
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false); // Chú thích nằm bên ngoài biểu đồ
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(12f);

        // Điều chỉnh khoảng cách giữa legend và pie chart
        legend.setYOffset(30f);

        // Ẩn tiêu đề của PieChart
        pieChart.getDescription().setEnabled(false);

        pieChart.invalidate(); // Làm mới PieChart
    }

}
