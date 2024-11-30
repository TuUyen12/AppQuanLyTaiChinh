package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.DataBase.DTBase.Financial;
import com.example.quanlytaichinh.InsertAdapter;
import com.example.quanlytaichinh.DataBase.DTBase.User;
import com.example.quanlytaichinh.DataBase.InsertItem;
import com.example.quanlytaichinh.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsertFragment extends Fragment {

    private User authUser;
    GridView gridView;
    TextView tvShowDay, tvfinancial; // Thêm TextView để hiển thị trạng thái tài chính
    ArrayList<DTBase.Category> Category; // Danh sách hiện tại
    ArrayList<DTBase.Category> expense;
    ArrayList<DTBase.Category> income;
    InsertAdapter insertAdapter;
    Button btnSubmit;
    ImageButton btnIncome, btnExpense;
    EditText et_amount, et_note;
    int userId;

    private int selectedPosition = -1; // Biến lưu vị trí item được chọn

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insert_layout, container, false);

        tvShowDay = view.findViewById(R.id.tv_show_day);
        // Hiển thị ngày hiện tại
        setCurrentDate();
        Bundle bundle = getArguments();
        if (bundle != null) {
            authUser = (User) bundle.getSerializable("User");
            if (authUser != null) {
                userId = authUser.getUserID();
            }

            // Kiểm tra và hiển thị ngày được chọn từ CalendarFragment
            int year = bundle.getInt("selectedYear", -1); // Sử dụng -1 nếu không có dữ liệu
            int month = bundle.getInt("selectedMonth", -1); // Sử dụng -1 nếu không có dữ liệu
            int day = bundle.getInt("selectedDay", -1); // Sử dụng -1 nếu không có dữ liệu

            // Nếu dữ liệu ngày hợp lệ (khác -1), cập nhật ngày trên TextView
            if (year != -1 && month != -1 && day != -1) {
                tvShowDay.setText(String.format("%d/%d/%d", day, month, year));
            } else {
                // Nếu không có dữ liệu, hiển thị ngày hiện tại
                setCurrentDate(); // Gọi lại hàm để đảm bảo ngày hiện tại được hiển thị
            }
        } else {
            // Nếu không có bundle, hiển thị ngày hiện tại mặc định
            setCurrentDate(); // Gọi lại hàm để hiển thị ngày hiện tại
            Toast.makeText(getActivity(), "No data passed", Toast.LENGTH_SHORT).show();
        }

        // Tham chiếu đến GridView và TextView
        gridView = view.findViewById(R.id.grid_view);
        tvfinancial = view.findViewById(R.id.tv_financial); // Khởi tạo TextView



        // Khởi tạo danh sách cho income và expense
        loadCategoriesFromPreferences();

        // Mặc định hiển thị danh sách expense
        Category = new ArrayList<>(expense); // Gán danh sách ban đầu là expense
        insertAdapter = new InsertAdapter(getActivity(), Category);
        gridView.setAdapter(insertAdapter);

        // Mặc định hiển thị "Expense"
        tvfinancial.setText("Expense: ");

        // Phần thiết lập nút trong onCreateView
        btnIncome = view.findViewById(R.id.btn_income);
        btnExpense = view.findViewById(R.id.btn_expense);

        // Thiết lập hình ảnh mặc định cho nút Expense và Income
        btnIncome.setImageResource(R.drawable.income1_with_size); // Đặt hình ảnh mặc định cho Income
        btnExpense.setImageResource(R.drawable.expense_with_size); // Đặt hình ảnh mặc định cho Expense

        // Sự kiện bấm nút Income
        btnIncome.setOnClickListener(v -> {
            Category.clear(); // Xóa danh sách hiện tại
            Category.addAll(income); // Thêm danh sách income
            insertAdapter.notifyDataSetChanged(); // Cập nhật GridView
            tvfinancial.setText("Income: "); // Cập nhật TextView hiển thị

            // Cập nhật hình ảnh nút Income và Expense khi Income được chọn
            btnIncome.setImageResource(R.drawable.income_with_size);
            btnExpense.setImageResource(R.drawable.expense1_with_size);

            // Reset selection
            selectedPosition = -1; // Đặt lại vị trí đã chọn
            insertAdapter.setSelectedPosition(selectedPosition); // Cập nhật adapter để không có item nào được chọn
            insertAdapter.notifyDataSetChanged(); // Cập nhật GridView
        });

        // Sự kiện bấm nút Expense
        btnExpense.setOnClickListener(v -> {
            Category.clear(); // Xóa danh sách hiện tại
            Category.addAll(expense); // Thêm danh sách expense
            insertAdapter.notifyDataSetChanged(); // Cập nhật GridView
            tvfinancial.setText("Expense: "); // Cập nhật TextView hiển thị

            // Cập nhật hình ảnh nút Income và Expense khi Expense được chọn
            btnIncome.setImageResource(R.drawable.income1_with_size);
            btnExpense.setImageResource(R.drawable.expense_with_size);

            // Reset selection
            selectedPosition = -1; // Đặt lại vị trí đã chọn
            insertAdapter.setSelectedPosition(selectedPosition); // Cập nhật adapter để không có item nào được chọn
            insertAdapter.notifyDataSetChanged(); // Cập nhật GridView
        });

        // Thiết lập sự kiện khi nhấn vào item
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            selectedPosition = position;
            insertAdapter.setSelectedPosition(selectedPosition);
            insertAdapter.notifyDataSetChanged();
        });

        et_amount = view.findViewById(R.id.et_financial);
        et_note = view.findViewById(R.id.et_note);

        btnSubmit = view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(view1 -> {
            // Lấy dữ liệu từ các trường EditText
            String amountText = et_amount.getText().toString();
            String note = et_note.getText().toString();
            String text_financial = tvfinancial.getText().toString().equals("Expense: ") ? "Expense" : "Income";

            if (!amountText.isEmpty()) {
                try {
                    // Chuyển đổi amountText sang Double
                    Double amount = Double.parseDouble(amountText);

                    // Lấy ngày hiển thị và ID người dùng
                    String selectedDate = tvShowDay.getText().toString(); // Lấy ngày từ TextView

                    // Lấy ID category từ item được chọn trong GridView
                    if (selectedPosition != -1) {
                        DTBase.Category selectedItem = Category.get(selectedPosition); // Lấy item đã chọn
                        int categoryId = selectedItem.getCategoryID(); // ID của item đã chọn

                        // Tạo đối tượng Financial mới với thông tin đã lấy
                        Financial newFinancial = new DTBase.Financial(0, categoryId, note, text_financial, amount, selectedDate, userId);

                        // Khởi tạo đối tượng DTBase và thêm thông tin tài chính vào cơ sở dữ liệu
                        DTBase db = new DTBase();
                        db.addFinancialForUser(newFinancial, userId);

                        // Lấy dữ liệu tài chính từ SharedPreferences
                        SharedPreferences financialSharedPreferences = getActivity().getSharedPreferences("MyFinancials", MODE_PRIVATE);
                        String financialJson = financialSharedPreferences.getString("financialList", "[]"); // Mặc định là mảng rỗng nếu không có dữ liệu

                        // Chuyển đổi chuỗi JSON thành danh sách các mục tài chính
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<DTBase.Financial>>() {
                        }.getType();
                        List<DTBase.Financial> financialList = gson.fromJson(financialJson, type);

                        // Thêm đối tượng tài chính mới vào danh sách
                        financialList.add(newFinancial);

                        // Cập nhật lại SharedPreferences với danh sách mới
                        SharedPreferences.Editor editor = financialSharedPreferences.edit();
                        String updatedJson = gson.toJson(financialList); // Chuyển danh sách thành chuỗi JSON
                        editor.putString("financialList", updatedJson); // Lưu chuỗi JSON vào SharedPreferences
                        editor.apply(); // Áp dụng thay đổi

                        // Hiển thị thông báo thành công
                        Toast.makeText(getActivity(), "Financial data submitted successfully!", Toast.LENGTH_SHORT).show();

                        // Xóa dữ liệu trong EditText sau khi lưu thành công
                        et_amount.setText("");
                        et_note.setText("");
                    } else {
                        // Nếu không có item nào được chọn
                        Toast.makeText(getActivity(), "Please select a category", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    // Nếu người dùng nhập một giá trị không phải là số hợp lệ
                    Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Nếu trường amount trống
                Toast.makeText(getActivity(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

        private void loadCategoriesFromPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyCategory", MODE_PRIVATE);
        String expenseJson = sharedPreferences.getString("expense", "[]");  // Mặc định là mảng rỗng nếu không có giá trị
        String incomeJson = sharedPreferences.getString("income", "[]");    // Mặc định là mảng rỗng nếu không có giá trị

        // Chuyển từ chuỗi JSON thành các mảng
        Gson gson = new Gson();
        Type categoryListType = new TypeToken<ArrayList<DTBase.Category>>() {}.getType();
        expense = gson.fromJson(expenseJson, categoryListType);
        income = gson.fromJson(incomeJson, categoryListType);
    }

    private void setCurrentDate() {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng từ 0-11, cộng thêm 1
        int year = calendar.get(Calendar.YEAR);
        tvShowDay.setText(String.format("%02d/%02d/%04d", day, month, year)); // Hiển thị ngày hiện tại
    }

}
