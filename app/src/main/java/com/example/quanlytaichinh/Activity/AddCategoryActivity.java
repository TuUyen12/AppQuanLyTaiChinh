package com.example.quanlytaichinh.Activity;

import com.example.quanlytaichinh.CategoryHelper;
import com.example.quanlytaichinh.DataBase.DTBase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.CustomSpinnerAdapter;
import com.example.quanlytaichinh.IconCategoryAdapter;
import com.example.quanlytaichinh.IconCategoryItem;
import com.example.quanlytaichinh.R;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddCategoryActivity extends AppCompatActivity {
    Spinner spinner;
    GridView gridview;
    TextView tvCancel;
    ImageView ivCheck;
    EditText etCategoryName;
    private int selectedPosition = -1; // Mặc định chưa chọn item nào
    int userId;
    boolean isPersonal;
    private DTBase.User authUser;
    private ArrayList<IconCategoryItem> iconCategoryItems;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        initVariable();
        initCategory();

        // Spinner chọn expense hay income
        String[] typeOptions = {"Expense", "Income"};
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, typeOptions);
        spinner.setSelection(0);
        spinner.setAdapter(adapter);
        // Click vào text Cancel -> quay về setting fragment
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = etCategoryName.getText().toString().trim();

                // Kiểm tra ô nhập tên danh mục
                if (categoryName.isEmpty()) {
                    Toast.makeText(view.getContext(), "Please enter category name", Toast.LENGTH_SHORT).show();
                }
                // Kiểm tra nếu người dùng không chọn icon trong GridView
                else if (selectedPosition == -1) {
                    Toast.makeText(view.getContext(), "Please choose an icon for category", Toast.LENGTH_SHORT).show();
                }
                else {
                    // financialType là string được chọn trong spinner và không viết hoa
                    String financialType = spinner.getSelectedItem().toString().toLowerCase();

                    // Thực hiện logic lấy categoryId từ Firebase
                    DTBase dtBase = new DTBase();

                    getCategoryIdFromFirebase(userId, isPersonal, financialType, new FirebaseCallback() {
                        @Override
                        public void onDataFetched(int value) {
                            // Đảm bảo categoryId được gán sau khi lấy dữ liệu từ Firebase
                            int categoryId = value;

                            // Lấy categoryIcon từ GridView, tại vị trí selectedPosition
                            IconCategoryItem selectedItem = iconCategoryItems.get(selectedPosition);
                            int categoryIcon = selectedItem.getImageResId(); // Lấy ID tài nguyên hình ảnh

                            // Tạo đối tượng category sau khi có categoryId và categoryIcon
                            DTBase.Category category = new DTBase.Category(categoryId, categoryIcon , categoryName, financialType, userId);

                            // Thêm danh mục vào Firebase
                            dtBase.addNewCategory(category, userId, categoryId);

                            // Lưu danh mục vào SharedPreferences sau khi đã lưu vào Firebase
                            SharedPreferences sharedPreferences = getSharedPreferences("MyCategory", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // Lấy danh sách danh mục hiện tại từ SharedPreferences
                            String expenseJson = sharedPreferences.getString("expense", "[]"); // Mặc định là mảng rỗng
                            String incomeJson = sharedPreferences.getString("income", "[]"); // Mặc định là mảng rỗng
                            String categoryJson = sharedPreferences.getString("category", "[]");
                            // Chuyển từ chuỗi JSON thành các mảng Category
                            Gson gson = new Gson();
                            Type categoryListType = new TypeToken<ArrayList<DTBase.Category>>() {}.getType();
                            ArrayList<DTBase.Category> expense = gson.fromJson(expenseJson, categoryListType);
                            ArrayList<DTBase.Category> income = gson.fromJson(incomeJson, categoryListType);
                            ArrayList<DTBase.Category> newcategory = gson.fromJson(categoryJson, categoryListType);

                            // Kiểm tra nếu là danh mục chi tiêu hoặc thu nhập và thêm vào danh sách tương ứng
                            if (financialType.equals("expense")) {
                                expense.add(category);
                            } else {
                                income.add(category);
                            }
                            newcategory.add(category);
                            // Chuyển lại thành chuỗi JSON và lưu vào SharedPreferences
                            String updatedExpenseJson = gson.toJson(expense);
                            String updatedIncomeJson = gson.toJson(income);
                            String updatedCategoryJson = gson.toJson(newcategory);
                            editor.putString("expense", updatedExpenseJson);
                            editor.putString("income", updatedIncomeJson);
                            editor.putString("category",updatedCategoryJson);

                            editor.apply(); // Lưu thay đổi

                            // Hiển thị thông báo thành công và quay lại màn hình trước
                            Toast.makeText(view.getContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Quay về màn hình trước đó
                        }
                    });
                }
            }
        });

        // Lắng nghe sự kiện chọn item trong GridView
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position; // Cập nhật item được chọn
            }
        });

    }
    private void initVariable() {
        spinner = findViewById(R.id.spinner);
        gridview = findViewById(R.id.grid_view);
        tvCancel = findViewById(R.id.tv_cancel);
        ivCheck = findViewById(R.id.iv_check);
        etCategoryName = findViewById(R.id.et_category_name);

        // Nhận thông tin người dùng từ Intent
        authUser = (DTBase.User) getIntent().getSerializableExtra("User");
        userId = authUser.getUserID();

        // Lấy giá trị 'isPersonal' từ SharedPreferences thay vì dùng 'which'
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isPersonal = sharedPreferences.getBoolean("isPersonal", false);
    }


    private void getCategoryIdFromFirebase(int userId, boolean isPersonal, String financialType, FirebaseCallback callback) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String newCategoryId = "NewCategoryID"; // Gốc của node trong Firebase
        String userIdString = String.valueOf(userId);

        // Xác định node cha dựa trên giá trị isPersonal
        String parentNode = isPersonal ? "Personal" : "Business";

        // Tham chiếu đến node cụ thể
        DatabaseReference userRef = mDatabase.child(newCategoryId)
                .child(userIdString)
                .child(parentNode)
                .child(financialType);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long value = snapshot.getValue(Long.class); // Lấy giá trị từ Firebase
                    if (value != null) {
                        int newCategoryIdValue = value.intValue() + 1; // Cộng thêm 1 vào giá trị ID
                        saveDataToFirebase(mDatabase, newCategoryId, userIdString, parentNode, financialType, newCategoryIdValue);
                        callback.onDataFetched(newCategoryIdValue); // Trả về kết quả qua callback
                    }
                } else {
                    Log.d("FirebaseData", "No data found for this path.");
                    int defaultValue = 1; // Nếu không có dữ liệu, gán ID là 1
                    saveDataToFirebase(mDatabase, newCategoryId, userIdString, parentNode, financialType, defaultValue);
                    callback.onDataFetched(defaultValue); // Trả về giá trị mặc định nếu không có dữ liệu
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Error fetching data: " + error.getMessage());
                callback.onDataFetched(0); // Trả về giá trị mặc định khi có lỗi
            }
        });
    }


    private interface FirebaseCallback {
        void onDataFetched(int value);
    }
    private void saveDataToFirebase(DatabaseReference mDatabase, String categoryId, String userId, String type, String subType, int value) {
        mDatabase.child(categoryId)
                .child(userId)
                .child(type)
                .child(subType)
                .setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initCategory(){
        iconCategoryItems = new ArrayList<>(); // Khởi tạo danh sách
        // Lấy danh sách các biểu tượng từ CategoryHelper
        iconCategoryItems = CategoryHelper.getIconCategoryItems(AddCategoryActivity.this); // Sử dụng biến đã khai báo
        // Tạo adapter và thiết lập cho GridView
        IconCategoryAdapter iconAdapter = new IconCategoryAdapter(this, iconCategoryItems);
        gridview.setAdapter(iconAdapter);
    }
}