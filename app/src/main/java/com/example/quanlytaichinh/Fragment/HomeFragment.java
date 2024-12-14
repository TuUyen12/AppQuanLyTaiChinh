package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import com.example.quanlytaichinh.CustomSpinnerAdapter;
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import android.content.SharedPreferences;
import android.content.Context;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    // Biến SharedPreferences để lưu vị trí đã chọn của Spinner
    private static final String PREFS_NAME = "SpinnerPrefs";
    private static final String KEY_SELECTED_POSITION = "SelectedPosition";
    private static final String PREFS_NAME_YEAR = "SpinnerYear";
    private static final String KEY_SELECTED_YEAR = "SelectedYear";

    private boolean isPersonal;
    private String categoryJson;
    private String financialJson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.home_layout, container, false);

        // Khởi tạo SharedPreferences để lấy dữ liệu isPersonal
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isPersonal = sharedPreferences.getBoolean("isPersonal", false);

        // Khởi tạo SharedPreferences để lấy dữ liệu category
        SharedPreferences categorySharedPreferences = getActivity().getSharedPreferences("MyCategory", MODE_PRIVATE);
        categoryJson = categorySharedPreferences.getString("category", "[]"); // Mặc định là mảng rỗng nếu không có dữ liệu ([])

        // Khởi tạo SharedPreferences để lấy dữ liệu financial
        SharedPreferences financialSharedPreferences = getActivity().getSharedPreferences("MyFinancials", MODE_PRIVATE);
        financialJson = financialSharedPreferences.getString("financialList", "[]"); // Mặc định là mảng rỗng nếu không có dữ liệu ([])


        // TextView để hiển thị số dư
        TextView textViewMoney = view.findViewById(R.id.tv_balance);

        boolean[] isNegative = new boolean[1];  // Dùng mảng để truyền giá trị
        double amount = (double) getTotalBalance(isNegative);

        // Kiểm tra trạng thái âm
        if (isNegative[0]) {
            // Số dư là âm
            Log.d("BalanceStatus", "The balance is negative.");
        } else {
            // Số dư là dương
            Log.d("BalanceStatus", "The balance is positive.");
        }

        // Định dạng tiền tệ Việt Nam
        Locale vietnamLocale = new Locale("vi", "VN");
        String formattedAmount = NumberFormat.getCurrencyInstance(vietnamLocale).format(amount);

        // Nếu số dư là âm, thêm dấu "-" trước số tiền
        if (isNegative[0]) {
            formattedAmount = "-" + formattedAmount.replace("₫", "").trim(); // Xóa dấu "₫" và thêm dấu "-"
        }

        // Ẩn số tiền mặc định
        textViewMoney.setText("**** đ"); // Ẩn số tiền mặc định

        // Chức năng hiển thị/ẩn số tiền khi nhấn vào biểu tượng mắt
        ImageButton ib_eye = view.findViewById(R.id.ib_eye);
        final boolean[] isShowingAmount = {false}; // Ẩn ban đầu

        String finalFormattedAmount = formattedAmount;
        ib_eye.setOnClickListener(v -> {
            if (isShowingAmount[0]) {
                isShowingAmount[0] = false;
                ib_eye.setImageResource(R.drawable.hide_with_size1);
                textViewMoney.setText("**** đ"); // Ẩn số tiền
            } else {
                isShowingAmount[0] = true;
                ib_eye.setImageResource(R.drawable.show_with_size1);
                textViewMoney.setText(finalFormattedAmount); // Hiển thị số tiền
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

        // Thiết lập BarChart chi phí và thu nhập
        BarChart barChart = view.findViewById(R.id.barChart);

        // Đặt sự kiện khi người dùng chọn item trong Spinner
        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Lấy giá trị item đã chọn
                String selectedDuration = durationOptions[position];
                // Cập nhật vị trí item đã chọn trong adapter
                adapter.setSelectedPosition(position);
                // Lưu vị trí đã chọn vào SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(KEY_SELECTED_POSITION, position);
                editor.apply();
                // Gọi hàm xử lý sự kiện với item đã chọn
                onDurationSelected(selectedDuration, barChart);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không làm gì khi không có lựa chọn nào được chọn
            }
        });



        // Thiết lập spinner year:
        Spinner spinnerYear = view.findViewById(R.id.spinner_year);
        String[] yearOptions = generateYearOptions(2024, 2010);

        CustomSpinnerAdapter adapterYear = new CustomSpinnerAdapter(requireContext(), yearOptions);
        spinnerYear.setAdapter(adapterYear);
        adapterYear.setSelectedPosition(0);

        // Lấy vị trí đã lưu trong SharedPreferences và cài đặt cho Spinner
        SharedPreferences yearPrefs = requireContext().getSharedPreferences(PREFS_NAME_YEAR, Context.MODE_PRIVATE);
        int savedYearPosition = yearPrefs.getInt(KEY_SELECTED_YEAR, 0);
        spinnerYear.setSelection(savedYearPosition);
        adapterYear.setSelectedPosition(savedYearPosition);

        // Thiết lập BarChart tài chính với dữ liệu tùy chỉnh
        BarChart barChartFinancial = view.findViewById(R.id.barChart_financial);
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
                // Lấy giá trị item đã chọn
                int selectedYear = Integer.parseInt(yearOptions[position]);
                ArrayList<BarEntry> userFinancialData = getUserFinancialData(selectedYear);
                setupBarChartFinancial(barChartFinancial, userFinancialData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không có item nào được chọn
            }
        });

        // Thiết lập PieChart
        PieChart pieChart = view.findViewById(R.id.pieChart);
        setupPieChart(pieChart, getUserPieData());

        return view;
    }

    // Hàm lấy dữ liệu tổng số tiền hiện có
    private double getTotalBalance(boolean[] isNegative) {
        Gson gson = new Gson();
        double expense = 0.0;
        double income = 0.0;
        double amount;

        if (financialJson != null) {
            // Chuyển đổi financialJson thành danh sách các đối tượng Financial
            Type listType = new TypeToken<List<DTBase.Financial>>() {}.getType();
            List<DTBase.Financial> financialList = gson.fromJson(financialJson, listType);

            // Duyệt qua từng phần tử Financial
            for (DTBase.Financial financial : financialList) {
                if ("expense".equalsIgnoreCase(financial.getFinancialType())) {
                    if (isPersonal && financial.getCategoryID() < 201) {
                        expense += financial.getFinancialAmount();
                    } else if (!isPersonal && financial.getCategoryID() >= 201) {
                        expense += financial.getFinancialAmount();
                    }
                }
                else if ("income".equalsIgnoreCase(financial.getFinancialType())) {
                    if (isPersonal && financial.getCategoryID() < 201) {
                        income += financial.getFinancialAmount();
                    } else if (!isPersonal && financial.getCategoryID() >= 201) {
                        income += financial.getFinancialAmount();
                    }
                }
            }

            Log.d("TotalAmount", "Total Expense: " + expense + ", Total Income: " + income);

        }
        Log.d("TotalAmount", "Total Expense: " + expense + ", Total Income: " + income);


        // Kiểm tra nếu tổng số dư âm
        if (expense > income) {
            amount = expense - income;
            isNegative[0] = true; // Đánh dấu là số âm
            Log.d("TotalAmount", " - " + amount);
        } else {
            amount = income - expense;
            isNegative[0] = false; // Đánh dấu là số dương
            Log.d("TotalAmount", "" + amount);
        }
        Log.d("CalculatedAmount", "Calculated Amount: " + amount);
        return amount;  // Trả về tổng số dư dưới dạng double
    }

    // Hàm xử lý sự kiện khi chọn item trong Spinner
    private void onDurationSelected(String selectedDuration, BarChart barChart) {

        switch (selectedDuration) {
            case "Today":
                ArrayList<BarEntry> userBarEntries1 = getUserBarExpenseIncome(1);
                setupBarChartExpenseIncome(barChart, userBarEntries1);
                break;
            case "This Week":
                ArrayList<BarEntry> userBarEntries2 = getUserBarExpenseIncome(2);
                setupBarChartExpenseIncome(barChart, userBarEntries2);
                break;
            case "This Month":
                ArrayList<BarEntry> userBarEntries3 = getUserBarExpenseIncome(3);
                setupBarChartExpenseIncome(barChart, userBarEntries3);
                break;
            case "This Year":
                ArrayList<BarEntry> userBarEntries4 = getUserBarExpenseIncome(4);
                setupBarChartExpenseIncome(barChart, userBarEntries4);
                break;
            default:
                Log.d("Spinner", "No selection");
                break;
        }
    }


    //Thêm dữ liệu cho BarChart
    private ArrayList<BarEntry> getUserBarExpenseIncome(int type){

        // Danh sách BarEntry để trả về 2 cột expense và income
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        Gson gson = new Gson();
        if (financialJson != null) {
            // Sử dụng Gson để chuyển JSON thành danh sách các đối tượng Financial
            Type financialType = new TypeToken<List<DTBase.Financial>>() {}.getType();
            List<DTBase.Financial> financialList = gson.fromJson(financialJson, financialType);

            // Biến để lưu tổng expense và income
            double totalExpense = 0.0;
            double totalIncome = 0.0;

            // Duyệt qua từng phần tử Financial
            for (DTBase.Financial financial : financialList) {

                String financialDateString = financial.getFinancialDate(); // Chuỗi ngày tháng, ví dụ: "yyyy-MM-dd"
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date financialDate = null;
                try {
                    financialDate = dateFormat.parse(financialDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar currentDate = Calendar.getInstance();

                switch(type){
                    case 1:
                        if(financialDate != null){
                            //Kiểm tra nếu type=1 -> lấy dữ liệu financial có financialDate = currentDate
                            if (currentDate.get(Calendar.YEAR) == financialDate.getYear() + 1900 &&
                                    currentDate.get(Calendar.MONTH) == financialDate.getMonth() &&
                                    currentDate.get(Calendar.DAY_OF_MONTH) == financialDate.getDate()) {
                                totalExpense += getExpense(financial, isPersonal);
                                totalIncome += getIncome(financial, isPersonal);
                            }

                        }
                        break;
                    case 2:
                        // Lấy ngày đầu tiên của tuần (Chủ nhật)
                        Calendar startOfWeek = (Calendar) currentDate.clone();
                        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

                        // Lấy ngày cuối cùng của tuần (Thứ Bảy)
                        Calendar endOfWeek = (Calendar) currentDate.clone();
                        endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

                        if(financialDate != null){
                            // Kiểm tra xem financialDate có nằm trong tuần của currentDate không
                            if (financialDate.compareTo(startOfWeek.getTime()) >= 0 && financialDate.compareTo(endOfWeek.getTime()) <= 0) {
                                totalExpense += getExpense(financial, isPersonal);
                                totalIncome += getIncome(financial, isPersonal);
                            }
                        }
                        break;
                    case 3:
                        //Kiểm tra nếu type=3 -> lấy dữ liệu financial có financialDate trong tháng hiện tại

                        if(financialDate != null){
                            if (currentDate.get(Calendar.YEAR) == financialDate.getYear() + 1900 &&
                                    currentDate.get(Calendar.MONTH) == financialDate.getMonth()) {
                                totalExpense += getExpense(financial, isPersonal);
                                totalIncome += getIncome(financial, isPersonal);
                            }
                        }

                        break;
                    case 4:
                        //Kiểm tra nếu type=4 -> lấy dữ liệu financial có financialDate trong năm hiện tại
                        if(financialDate != null){
                            if (currentDate.get(Calendar.YEAR) == financialDate.getYear() + 1900) {
                                totalExpense += getExpense(financial, isPersonal);
                                totalIncome += getIncome(financial, isPersonal);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            // Thêm các giá trị vào BarEntry (cột 0 là expense, cột 1 là income)
            barEntries.add(new BarEntry(0, (float)totalExpense)); // Expense
            barEntries.add(new BarEntry(1, (float)totalIncome)); // Income
        }

        // Trả về danh sách BarEntry
        return barEntries;
    }

    private double getExpense(DTBase.Financial financial, boolean isPersonal) {
        double totalExpense = 0.0;
        if ("expense".equalsIgnoreCase(financial.getFinancialType())) {
            // Kiểm tra điều kiện là cá nhân hoặc không phải cá nhân
            if (isPersonal) {
                // Nếu là cá nhân (isPersonal = true), chỉ thêm các financial có categoryID < 201
                if (financial.getCategoryID() < 201) {
                    totalExpense += financial.getFinancialAmount();
                }
            } else {
                // Nếu không phải cá nhân, chỉ thêm các financial có categoryID >= 201
                if (financial.getCategoryID() >= 201) {
                    totalExpense += financial.getFinancialAmount();
                }
            }
        }
        return totalExpense;

    }
    private double getIncome(DTBase.Financial financial, boolean isPersonal) {
        double totalIncome = 0.0;
        if ("income".equalsIgnoreCase(financial.getFinancialType())) {
            // Tương tự cho income
            if (isPersonal) {
                if (financial.getCategoryID() < 201) {
                    totalIncome += financial.getFinancialAmount();
                }
            } else {
                if (financial.getCategoryID() >= 201) {
                    totalIncome += financial.getFinancialAmount();
                }
            }
        }
        return totalIncome;
    }

    // Hàm thiết lập BarChart với hai cột: chi phí và thu nhập
    private void setupBarChartExpenseIncome(BarChart barChart, ArrayList<BarEntry> userBarEntries) {

        // Tạo BarDataSet từ userBarEntries
        BarDataSet barDataSet = new BarDataSet(userBarEntries, "Financial Overview");
        barDataSet.setColors(
                getResources().getColor(R.color.color5), // Expense
                getResources().getColor(R.color.color6)  // Income
        );
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTextColor(getResources().getColor(R.color.black));

        // Thiết lập BarData
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.8f); // Độ rộng của cột

        // Cấu hình BarChart
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false); // Tắt mô tả
        barChart.getLegend().setEnabled(true); // Hiển thị chú thích
        barChart.getXAxis().setGranularity(1f); // Đảm bảo các giá trị x được hiển thị cách đều
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Trục X ở dưới cùng

        // Gắn nhãn trục X (expense và income)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Expense", "Income"}));

        YAxis leftAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();

        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f); // Hoặc giá trị khác tùy theo độ lớn của dữ liệu

        leftAxis.setEnabled(false); // Tắt trục Y bên trái
        rightAxis.setEnabled(false); // Tắt trục Y bên phải

        barData.setBarWidth(0.4f);

        customizeBarChart(barChart);
        barChart.setFitBars(true); // Tự động căn chỉnh các cột
        barChart.animateY(1000); // Hiệu ứng khi hiển thị biểu đồ
        barChart.invalidate(); // Cập nhật biểu đồ
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
    private String[] generateYearOptions(int startYear, int endYear) {
        ArrayList<String> years = new ArrayList<>();
        for (int i = startYear; i >= endYear; i--) {
            years.add(String.valueOf(i));
        }
        return years.toArray(new String[0]);
    }

    //Thêm dữ liệu cho PieChart
    private ArrayList<PieEntry> getUserPieData() {

        // Chuyển đổi categoryJson thành danh sách các đối tượng Category
        Gson gson = new Gson();
        Type categoryType = new TypeToken<List<DTBase.Category>>() {}.getType();
        List<DTBase.Category> categoryList = gson.fromJson(categoryJson, categoryType);

        // Danh sách PieEntry để trả về
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        if (financialJson != null) {
            // Sử dụng Gson để chuyển JSON thành danh sách các đối tượng Financial
            Type financialType = new TypeToken<List<DTBase.Financial>>() {}.getType();
            List<DTBase.Financial> financialList = gson.fromJson(financialJson, financialType);

            // Khởi tạo một HashMap để gộp các giá trị theo categoryID
            HashMap<Integer, Float> categoryAmountMap = new HashMap<>();

            // Duyệt qua từng phần tử Financial
            for (DTBase.Financial financial : financialList) {
                // Kiểm tra loại financial: chỉ lấy các mục có `financialType` là "expense"
                if ("expense".equalsIgnoreCase(financial.getFinancialType())) {
                    if (isPersonal) {
                        // Nếu là cá nhân (isPersonal = true), chỉ thêm các financial có categoryID < 20
                        if (financial.getCategoryID() < 201) {
                            // Cộng dồn financialAmount vào categoryID tương ứng
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                        }
                    } else {
                        // Nếu không phải cá nhân, chỉ thêm các financial có categoryID >= 20
                        if (financial.getCategoryID() >= 201) {
                            // Cộng dồn financialAmount vào categoryID tương ứng
                            categoryAmountMap.put(
                                    financial.getCategoryID(),
                                    categoryAmountMap.getOrDefault(financial.getCategoryID(), 0f) + (float) financial.getFinancialAmount()
                            );
                        }
                    }
                }
            }

            // Tạo PieEntry từ categoryAmountMap và categoryList
            for (Map.Entry<Integer, Float> entry : categoryAmountMap.entrySet()) {
                int categoryID = entry.getKey();
                float amount = entry.getValue();

                // Lấy tên category từ categoryList dựa trên categoryID
                String categoryName = "Unknown"; // Mặc định là "Unknown" nếu không tìm thấy
                for (DTBase.Category category : categoryList) {
                    Log.d("DEBUG", "Comparing categoryID: " + categoryID + " with " + category.getCategoryID());
                    if (category.getCategoryID() == categoryID) {
                        categoryName = category.getCategoryName();
                        Log.d("DEBUG", "Matched categoryID: " + categoryID + ", Name: " + categoryName);
                        break;
                    }
                }

                // Kiểm tra nếu không tìm thấy tên category
                if ("Unknown".equals(categoryName)) {
                    Log.e("ERROR", "Category ID not found: " + categoryID);
                }

                // Thêm PieEntry vào danh sách
                pieEntries.add(new PieEntry(amount, categoryName));
            }

        }

        // Trả về danh sách PieEntry
        return pieEntries;
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

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setEnabled(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        barChart.getDescription().setEnabled(false);
    }


    //Thêm dữ liệu cho barchart tài chính
    private ArrayList<BarEntry> getUserFinancialData(int year) {
        //Danh sách BarEntry để trả về 2 cột expense và income
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        Gson gson = new Gson();
        if (financialJson != null) {
            // Sử dụng Gson để chuyển JSON thành danh sách các đối tượng Financial
            Type financialType = new TypeToken<List<DTBase.Financial>>() {
            }.getType();
            List<DTBase.Financial> financialList = gson.fromJson(financialJson, financialType);
            // Duyệt qua từng phần tử Financial

            double[] a = new double[13];
            for (int i = 1; i < 13; i++) {
                a[i] = 0.0;
                for (DTBase.Financial financial : financialList) {

                    String financialDateString = financial.getFinancialDate(); // Chuỗi ngày tháng, ví dụ: "yyyy-MM-dd"
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date financialDate = null;
                    try {
                        financialDate = dateFormat.parse(financialDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (financialDate.getYear() + 1900 == year && financialDate.getMonth() + 1 == i) {
                        a[i] += getExpense(financial, isPersonal);
                    }

                }
                // Thêm các giá trị vào BarEntry (cột 0 là expense, cột 1 là income)
                barEntries.add(new BarEntry(i-1, (float) a[i])); // Expense theo tháng
            }
        }
        return barEntries;
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


}