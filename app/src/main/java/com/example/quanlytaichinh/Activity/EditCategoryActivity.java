package com.example.quanlytaichinh.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlytaichinh.CategoryAdapter;
import com.example.quanlytaichinh.CategoryHelper;
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.IconCategoryAdapter;
import com.example.quanlytaichinh.IconCategoryItem;
import com.example.quanlytaichinh.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class EditCategoryActivity extends AppCompatActivity {
    CategoryAdapter adapter;
    TextView tvCategoryType;
    ListView listView;
    TextView tvCancel;
    ImageView ivCheck, ivCategory;
    EditText etCategoryName;

    ArrayList<DTBase.Category> Category; // Danh sách hiện tại
    ArrayList<DTBase.Category> expense;
    ArrayList<DTBase.Category> income;
    private int selectedPosition = -1; // Mặc định chưa chọn item nào
    private int selectedCategoryIcon = -1;  // Biến lưu icon đã chọn
    int userId;
    boolean isPersonal;
    private DTBase.User authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        initVariable();
        initCategories(userId, isPersonal);

        // Click vào text Cancel -> quay về setting fragment
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Lắng nghe sự kiện chọn item trong ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position; // Cập nhật item được chọn
                DTBase.Category selectedCategory = Category.get(position);

                etCategoryName.setText(selectedCategory.getCategoryName());

                // Cập nhật TextView dựa trên categoryType
                String categoryType = selectedCategory.getCategoryType(); // expense hoặc income
                if ("income".equalsIgnoreCase(categoryType)) {
                    tvCategoryType.setText("Income"); // Cập nhật TextView thành "Income"
                } else if ("expense".equalsIgnoreCase(categoryType)) {
                    tvCategoryType.setText("Expense"); // Cập nhật TextView thành "Expense"
                }
            }
        });
        ivCategory.setOnClickListener(view -> {
            // Tạo một dialog mới
            Dialog dialog = new Dialog(EditCategoryActivity.this);
            dialog.setContentView(R.layout.dialog_category_icons);

            // Lấy danh sách các biểu tượng từ CategoryHelper
            ArrayList<IconCategoryItem> iconCategoryItems = CategoryHelper.getIconCategoryItems(EditCategoryActivity.this);

            // Khởi tạo và thiết lập adapter
            IconCategoryAdapter iconAdapter = new IconCategoryAdapter(EditCategoryActivity.this, iconCategoryItems);
            GridView gridViewIcons = dialog.findViewById(R.id.gridview_icons);
            gridViewIcons.setAdapter(iconAdapter);

            // Lắng nghe sự kiện click vào item trong GridView
            gridViewIcons.setOnItemClickListener((parent, view1, position, id) -> {
                // Lấy biểu tượng đã chọn
                IconCategoryItem selectedItem = iconCategoryItems.get(position);

                // Thiết lập biểu tượng đã chọn vào ivCategory ImageView
                selectedCategoryIcon = selectedItem.getImageResId();  // Cập nhật icon đã chọn

                // Đóng dialog
                dialog.dismiss();
            });

            // Hiển thị dialog
            dialog.show();
        });

        ivCheck.setOnClickListener(view -> {
            String categoryName = etCategoryName.getText().toString().trim();

            // Kiểm tra ô nhập tên danh mục
            if (categoryName.isEmpty()) {
                Toast.makeText(view.getContext(), "Please enter category name", Toast.LENGTH_SHORT).show();
            } else if (selectedCategoryIcon == -1) {  // Kiểm tra nếu chưa chọn biểu tượng
                Toast.makeText(view.getContext(), "Please choose an icon for category", Toast.LENGTH_SHORT).show();
            } else {
                // Lấy category hiện tại đang được chọn
                DTBase.Category selectedCategory = Category.get(selectedPosition);

                selectedCategory.setCategoryName(categoryName);

                // Kiểm tra nếu selectedCategoryIcon có giá trị hợp lệ trước khi cập nhật
                if (selectedCategoryIcon != -1) {
                    selectedCategory.setCategoryIcon(selectedCategoryIcon);  // Cập nhật icon cho category
                } else {
                }

                String financialType = tvCategoryType.getText().toString().toLowerCase();
                selectedCategory.setCategoryType(financialType);

                // Tham chiếu đến node Firebase cho category này
                DatabaseReference categoryRef = FirebaseDatabase.getInstance()
                        .getReference("CATEGORIES")
                        .child(String.valueOf(userId))
                        .child(String.valueOf(selectedCategory.getCategoryID()));

                // Cập nhật dữ liệu lên Firebase
                categoryRef.setValue(selectedCategory)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditCategoryActivity.this, "Category updated successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditCategoryActivity.this, "Failed to update category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                // Khởi tạo danh sách cho expense và income
                expense = new ArrayList<>();
                income = new ArrayList<>();
                Category = new ArrayList<>();
                // Gọi phương thức initCategory để lấy dữ liệu từ Firebase
                initCategories(userId, isPersonal);
            }
        });
    }

        private void initVariable() {
        tvCategoryType = findViewById(R.id.tv_category_type);
        listView = findViewById(R.id.list_view);
        tvCancel = findViewById(R.id.tv_cancel);
        ivCheck = findViewById(R.id.iv_check);
        ivCategory = findViewById(R.id.iv_category);
        etCategoryName = findViewById(R.id.et_category_name);

        // Nhận thông tin người dùng từ Intent
        authUser = (DTBase.User) getIntent().getSerializableExtra("User");
        userId = authUser.getUserID();

        // Lấy giá trị 'isPersonal' từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isPersonal = sharedPreferences.getBoolean("isPersonal", false);

        Category = new ArrayList<>();
        expense = new ArrayList<>();
        income = new ArrayList<>();
    }

    private void initCategories(int userId, boolean isPersonal) {
        adapter = new CategoryAdapter(EditCategoryActivity.this, Category);
        // Tham chiếu đến node CATEGORIES trên Firebase
        DatabaseReference categoryRef = FirebaseDatabase.getInstance()
                .getReference("CATEGORIES")
                .child(String.valueOf(userId));

        // Lấy dữ liệu với lắng nghe chỉ một lần
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Duyệt qua các danh mục
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        DTBase.Category category = categorySnapshot.getValue(DTBase.Category.class);
                        if (category != null) {
                            // Phân loại category dựa trên loại tài khoản
                            boolean isExpense = "expense".equals(category.getCategoryType());
                            boolean isIncome = "income".equals(category.getCategoryType());
                            int categoryId = category.getCategoryID();
                            Category.add(category);

                            // Nếu là tài khoản cá nhân, chọn category có id nhỏ hơn 201
                            if (isPersonal) {
                                if (isExpense && categoryId < 201) {
                                    expense.add(category);  // Thêm vào danh sách expense cho tài khoản cá nhân
                                }
                                if (isIncome && categoryId < 201) {
                                    income.add(category);   // Thêm vào danh sách income cho tài khoản cá nhân
                                }
                            } else {
                                // Nếu không phải tài khoản cá nhân, chọn category có id >= 201
                                if (isExpense && categoryId >= 201) {
                                    expense.add(category);  // Thêm vào danh sách expense cho tài khoản doanh nghiệp
                                }
                                if (isIncome && categoryId >= 201) {
                                    income.add(category);   // Thêm vào danh sách income cho tài khoản doanh nghiệp
                                }
                            }
                        }
                    }

                    // Chuyển đổi dữ liệu sang JSON
                    Gson gson = new Gson();
                    String expenseJson = gson.toJson(expense);
                    String incomeJson = gson.toJson(income);
                    String categoryJson = gson.toJson(Category);

                    // Lưu JSON vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyCategory", MODE_PRIVATE);
                    SharedPreferences.Editor categoryEditor = sharedPreferences.edit();
                    categoryEditor.clear();
                    categoryEditor.putString("expense", expenseJson);
                    categoryEditor.putString("income", incomeJson);
                    categoryEditor.putString("category", categoryJson);
                    categoryEditor.apply();

                    // Kết hợp cả danh sách expense và income vào một danh sách duy nhất
                    ArrayList<DTBase.Category> categoriesToDisplay = new ArrayList<>();
                    categoriesToDisplay.addAll(expense); // Thêm danh sách expense
                    categoriesToDisplay.addAll(income);  // Thêm danh sách income

                    // Thêm category vào ListView
                    adapter = new CategoryAdapter(EditCategoryActivity.this, categoriesToDisplay);
                    listView.setAdapter(adapter);


                    // Hiển thị thông tin từ item đầu tiên (nếu có)
                    if (!categoriesToDisplay.isEmpty()) {
                        selectedPosition = 0; // Mặc định chọn item đầu tiên
                        DTBase.Category firstCategory = categoriesToDisplay.get(0);
                        etCategoryName.setText(firstCategory.getCategoryName());

                        // Cập nhật TextView dựa trên categoryType
                        if ("income".equalsIgnoreCase(firstCategory.getCategoryType())) {
                            tvCategoryType.setText("Income"); // Cập nhật TextView thành "Income"
                        } else if ("expense".equalsIgnoreCase(firstCategory.getCategoryType())) {
                            tvCategoryType.setText("Expense"); // Cập nhật TextView thành "Expense"
                        }
                    }
                } else {
                    Log.e("initCategory", "No categories found for userId: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("initCategory", "Error fetching categories: " + error.getMessage());
            }
        });
    }
}
