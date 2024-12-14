package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

import com.example.quanlytaichinh.ChartAdapter;
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
import java.util.ArrayList;
import java.util.Calendar;
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
    private DTBase.User authUser;
    private int userId;
    private List<DTBase.Financial> userFinancialList = new ArrayList<>();

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

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            authUser = (DTBase.User) bundle.getSerializable("User");
            if (authUser != null) {
                userId = authUser.getUserID();
                Log.d("ChartFragment", "User ID: " + userId);
            } else {
                // Gán giá trị mặc định nếu authUser là null
                userId = -1; // Hoặc giá trị hợp lệ tùy theo yêu cầu của bạn
            }
        }
        updateView();

        // Month
        ibMonth.setOnClickListener(v -> {
            isMonthlyView = true;
            Log.d("ChartFragment", "Monthly view selected");
            Toast.makeText(getContext(), "Changed to Monthly view", Toast.LENGTH_SHORT).show();
            updateView();
            int time = isMonthlyView ? Calendar.getInstance().get(Calendar.MONTH) + 1 : Calendar.getInstance().get(Calendar.YEAR);
            ArrayList<PieEntry> pieEntries = getUserPieData(time, isMonthlyView);
            setupPieChart(pieChart, pieEntries);
        });
        ibYear.setOnClickListener(v -> {
            isMonthlyView = false;
            Log.d("ChartFragment", "Yearly view selected");
            Toast.makeText(getContext(), "Changed to Yearly view", Toast.LENGTH_SHORT).show();
            updateView();
            int time = isMonthlyView ? Calendar.getInstance().get(Calendar.MONTH) + 1 : Calendar.getInstance().get(Calendar.YEAR);
            ArrayList<PieEntry> pieEntries = getUserPieData(time, isMonthlyView);
            setupPieChart(pieChart, pieEntries);
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

                // Lấy đối tượng Financial tại vị trí "position"
                DTBase.Financial financialItem = (DTBase.Financial) adapterView.getItemAtPosition(i);

                // Lấy financialID từ đối tượng Financial
                int financialID = financialItem.getFinancialID();
                int userID = financialItem.getUserID();
                // Gọi hàm xử lý Edit/Delete với financialID

                ShowDelete(userID, financialID);

                return true; // Trả về true để sự kiện được xử lý và không tiếp tục với các hành động khác
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
        ArrayList<DTBase.Financial> chartItems = new ArrayList<>();
        DTBase dtBase = new DTBase();

        if (financialJson != null) {
            Type financialType = new TypeToken<List<DTBase.Financial>>() {}.getType();
            List<DTBase.Financial> financialList = gson.fromJson(financialJson, financialType);

            HashMap<Integer, Float> categoryAmountMap = new HashMap<>();

            // Duyệt qua danh sách chi tiêu hoặc thu nhập
            for (DTBase.Financial financial : financialList) {
                if (currentType.equals("Expense")) { // Loại chi tiêu
                    if (isMonthlyView) {
                        if (isPersonal && dtBase.getFinancialMonth(financial) == time && financial.getCategoryID() < 101) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            chartItems.add(financial);
                        } else if (!isPersonal && dtBase.getFinancialMonth(financial) == time && financial.getCategoryID() >= 201) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            chartItems.add(financial);
                        }
                    } else { // Chế độ xem theo năm
                        if (isPersonal && dtBase.getFinancialYear(financial) == time && financial.getCategoryID() < 101) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            chartItems.add(financial);
                        } else if (!isPersonal && dtBase.getFinancialYear(financial) == time && financial.getCategoryID() >= 201) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            chartItems.add(financial);
                        }
                    }
                } else if (currentType.equals("Income")) { // Loại thu nhập
                    if (isMonthlyView) {
                        if (isPersonal && dtBase.getFinancialMonth(financial) == time && financial.getCategoryID() >= 101 && financial.getCategoryID() < 201) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            chartItems.add(financial);
                        } else if (!isPersonal && dtBase.getFinancialMonth(financial) == time && financial.getCategoryID() >= 301) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            chartItems.add(financial);
                        }
                    } else {
                        if (isPersonal && dtBase.getFinancialYear(financial) == time && financial.getCategoryID() >= 101 && financial.getCategoryID() < 2011) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                            chartItems.add(financial);
                        } else if (!isPersonal && dtBase.getFinancialYear(financial) == time && financial.getCategoryID() >= 301) {
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );chartItems.add(financial);

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
        ChartAdapter adapter = new ChartAdapter(getContext(), chartItems);
        // Cập nhật lại ListView
        if (chartItems != null && !chartItems.isEmpty()) {
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

    // Hàm xử lý sự kiện chọn Delete
    private void ShowDelete(int userID, int financialID) {
        if (userID <= 0 || financialID <= 0) {
            Toast.makeText(getActivity(), "Invalid data. Cannot delete." + userID + " " + financialID, Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "Do you want to delete?";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Tạo đối tượng DTBase để xóa dữ liệu
                    DTBase database = new DTBase();
                    database.deleteFinancial(userID, financialID);

                    // Cập nhật lại ListView
                    updateListView(); // Gọi hàm cập nhật lại ListView

                    // Cập nhật SharedPreferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyFinancials", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Thông báo sau khi xóa thành công
                    Toast.makeText(getActivity(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Đóng dialog khi người dùng chọn "No"
                    dialog.dismiss();
                });

        builder.create().show();
    }
    private void updateListView() {
        DTBase db = new DTBase();
        // Lấy dữ liệu tài chính từ Firebase
        db.fetchFinancialData(userId, new DTBase.FinancialCallback() {
            @Override
            public void onFinancialDataFetched(List<DTBase.Financial> financialList) {
                if (financialList != null) {
                    userFinancialList.addAll(financialList);

                    // Khi dữ liệu tài chính đã tải xong, lưu vào SharedPreferences
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyFinancials", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(userFinancialList);
                    editor.putString("financialList", json);
                    editor.apply();
                    int time = isMonthlyView ? Calendar.getInstance().get(Calendar.MONTH) + 1 : Calendar.getInstance().get(Calendar.YEAR);
                    ArrayList<PieEntry> pieEntries = getUserPieData(time, isMonthlyView);
                    setupPieChart(pieChart, pieEntries);
                } else {
                    Toast.makeText(getActivity(), "Error loading financial data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
