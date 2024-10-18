package com.example.quanlytaichinh;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;

public class InsertFragment extends Fragment {

    GridView gridView;
    TextView tvShowDay, tvfinancial; // Thêm TextView để hiển thị trạng thái tài chính
    ArrayList<InsertItem> insertItems; // Danh sách hiện tại
    ArrayList<InsertItem> incomeItems; // Danh sách cho income
    ArrayList<InsertItem> expenseItems; // Danh sách cho expense
    InsertAdapter insertAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insert_layout, container, false);

        // Tham chiếu đến GridView và TextView
        gridView = view.findViewById(R.id.grid_view);
        tvShowDay = view.findViewById(R.id.tv_show_day);
        tvfinancial = view.findViewById(R.id.tv_financial); // Khởi tạo TextView

        // Hiển thị ngày hiện tại
        setCurrentDate();

        // Lấy Bundle từ CalendarFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            int year = bundle.getInt("selectedYear");
            int month = bundle.getInt("selectedMonth");
            int day = bundle.getInt("selectedDay");
            tvShowDay.setText(String.format("Date: %02d/%02d/%04d", day, month, year));
        }

        // Khởi tạo danh sách cho income và expense
        initializeItems();

        // Mặc định hiển thị danh sách expense
        insertItems = new ArrayList<>(expenseItems);
        insertAdapter = new InsertAdapter(getActivity(), insertItems);
        gridView.setAdapter(insertAdapter);

        // Mặc định hiển thị "Expense"
        tvfinancial.setText("Expense: ");

        // Thiết lập các nút chuyển đổi
        Button btnIncome = view.findViewById(R.id.btn_income);
        Button btnExpense = view.findViewById(R.id.btn_expense);

        btnIncome.setBackgroundColor(Color.CYAN);
        btnIncome.setOnClickListener(v -> {
            insertItems.clear(); // Xóa danh sách hiện tại
            insertItems.addAll(incomeItems); // Thêm danh sách income
            insertAdapter.notifyDataSetChanged(); // Cập nhật GridView
            tvfinancial.setText("Income: "); // Cập nhật TextView hiển thị
        });

        btnExpense.setOnClickListener(v -> {
            insertItems.clear(); // Xóa danh sách hiện tại
            insertItems.addAll(expenseItems); // Thêm danh sách expense
            insertAdapter.notifyDataSetChanged(); // Cập nhật GridView
            tvfinancial.setText("Expense: "); // Cập nhật TextView hiển thị
        });

        return view;
    }

    private void initializeItems() {
        // Khởi tạo danh sách cho expense
        expenseItems = new ArrayList<>();
        expenseItems.add(new InsertItem(R.drawable.ic_transport, "Transport"));
        expenseItems.add(new InsertItem(R.drawable.ic_food, "Food"));
        expenseItems.add(new InsertItem(R.drawable.ic_edu, "Education"));
        expenseItems.add(new InsertItem(R.drawable.ic_cloths, "Cloths"));
        expenseItems.add(new InsertItem(R.drawable.ic_medical, "Medical"));
        expenseItems.add(new InsertItem(R.drawable.ic_contact_fee, "Contact Fee"));
        expenseItems.add(new InsertItem(R.drawable.ic_cosmetic, "Cosmetic"));
        expenseItems.add(new InsertItem(R.drawable.ic_housing_expenses, "Housing expense"));
        expenseItems.add(new InsertItem(R.drawable.ic_add, "Add"));

        // Khởi tạo danh sách cho income
        incomeItems = new ArrayList<>();
        incomeItems.add(new InsertItem(R.drawable.ic_salary, "Salary"));
        incomeItems.add(new InsertItem(R.drawable.ic_bonus, "Bonus"));
        incomeItems.add(new InsertItem(R.drawable.ic_investment, "Investment"));
        incomeItems.add(new InsertItem(R.drawable.ic_add, "Add"));

        // Thêm các mục khác nếu cần...
    }

    private void setCurrentDate() {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng từ 0-11 nên cần cộng 1
        int year = calendar.get(Calendar.YEAR);

        // Hiển thị ngày hiện tại lên TextView
        tvShowDay.setText(String.format("Date: %02d/%02d/%04d", day, month, year));
    }
}
