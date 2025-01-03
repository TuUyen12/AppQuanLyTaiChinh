package com.example.quanlytaichinh.Activity;

import static android.app.PendingIntent.getActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.DataBase.DTBase.Financial;
import com.example.quanlytaichinh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    ArrayList<DTBase.Category> Category; // Danh sách hiện tại
    ArrayList<DTBase.Category> expense;
    ArrayList<DTBase.Category> income;
    private Button loginButton, resetPasswordButton; // Thêm nút reset password
    private DTBase.User authUser; // Đối tượng User để lưu thông tin người dùng
    private TextView tv_ForgotPassword;
    private List<Financial> userFinancialList = new ArrayList<>();
    int userId;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo SharedPreferences và Editor để lưu lựa chọn cá nhân hay doanh nghiệp
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        EditText et_email = findViewById(R.id.et_email);
        EditText et_password = findViewById(R.id.et_password);

        ImageButton ib_eye = findViewById(R.id.ib_eye);
        setPasswordVisibilityToggle(ib_eye, et_password);
        //Sự kiện khi ấn forgot password
        tv_ForgotPassword = findViewById(R.id.tv_forgot_password);
        tv_ForgotPassword.setOnClickListener(v -> {
            // Chuyển đến ForgotPassActivity khi nhấn vào TextView
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        loginButton = findViewById(R.id.btn_sign_in);

        loginButton.setOnClickListener(v -> {
            String email = et_email.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                signIn(email, password);
            } else {
                Toast.makeText(SignInActivity.this, "Please fill in all information", Toast.LENGTH_SHORT).show();
            }
        });


    }
    // Phương thức hiển thị mật khẩu
    private void setPasswordVisibilityToggle(ImageButton ib_eye, EditText editText) {
        final boolean[] isPasswordVisible = {false};
        ib_eye.setOnClickListener(v -> {
            isPasswordVisible[0] = !isPasswordVisible[0];
            ib_eye.setImageResource(isPasswordVisible[0] ? R.drawable.show_with_size : R.drawable.hide_with_size);
            editText.setInputType(isPasswordVisible[0] ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setSelection(editText.getText().length());
        });
    }

    private void signIn(String useremail, String password) {
        DTBase database = new DTBase();
        mAuth.signInWithEmailAndPassword(useremail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            database.CheckSignIn(useremail, (success, MyUser, financialData) -> {
                                if (success) {
                                    authUser = MyUser;
                                    userId = MyUser.getUserID();
                                    // Lưu thông tin người dùng vào SharedPreferences
                                    saveUserToSharedPreferences(authUser);

                                    // Xóa danh sách tài chính trước đó
                                    userFinancialList.clear();

                                    // Lấy dữ liệu tài chính từ Firebase
                                    database.fetchFinancialData(userId, new DTBase.FinancialCallback() {
                                        @Override
                                        public void onFinancialDataFetched(List<Financial> financialList) {
                                            if (financialList != null) {
                                                userFinancialList.addAll(financialList);

                                                // Khi dữ liệu tài chính đã tải xong, lưu vào SharedPreferences
                                                SharedPreferences sharedPreferences = getSharedPreferences("MyFinancials", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                Gson gson = new Gson();
                                                String json = gson.toJson(userFinancialList);
                                                editor.putString("financialList", json);
                                                editor.apply();
                                            }
                                        }

                                        @Override
                                        public void onError(String errorMessage) {
                                            // Xử lý lỗi khi không thể lấy dữ liệu tài chính
                                            Toast.makeText(SignInActivity.this, "Error fetching financial data: " + errorMessage, Toast.LENGTH_SHORT).show();
                                            Log.e("SignInActivity", "Error: " + errorMessage);
                                        }
                                    });

                                }
                            });
                            // Hiển thị dialog chọn loại tài khoản hoặc điều hướng
                            showAccountTypeDialog();

                        }
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Hàm hiển thị dialog cho người dùng chọn loại tài khoản và lưu vào SharedPreferences
    private void showAccountTypeDialog() {
        // Tạo danh sách các tùy chọn loại tài khoản
        String[] accountTypes = {"Personal", "Business"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Account Type");
        builder.setSingleChoiceItems(accountTypes, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                boolean isPersonal;
                // Lưu giá trị boolean vào SharedPreferences
                if (which == 0) {
                    editor.putBoolean("isPersonal", true); // Personal
                    isPersonal = true;
                } else {
                    editor.putBoolean("isPersonal", false); // Business
                    isPersonal = false;
                }
                editor.apply();

                dialog.dismiss();
                // Khởi tạo danh sách cho expense và income
                expense = new ArrayList<>();
                income = new ArrayList<>();
                Category = new ArrayList<>();
                // Gọi phương thức initCategory để lấy dữ liệu từ Firebase
                initCategory(userId, isPersonal);

                // Đợi dữ liệu tải xong trước khi chuyển sang GeneralActivity
                DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("CATEGORIES").child(String.valueOf(userId));
                categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Khi tải dữ liệu xong
                        Intent intent = new Intent(SignInActivity.this, GeneralActivity.class);
                        intent.putExtra("User", authUser);
                        startActivity(intent);
                        Intent feedbackIntent = new Intent(SignInActivity.this, FeedbackActivity.class);
                        feedbackIntent.putExtra("User", authUser);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignInActivity.this, "Error loading categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        builder.create().show();
    }
    private void initCategory(int userId, boolean isPersonal) {
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
                            // Thêm vào danh sách chung
                            Category.add(category);

                            // Phân loại category dựa trên loại tài khoản
                            boolean isExpense = "expense".equals(category.getCategoryType());
                            boolean isIncome = "income".equals(category.getCategoryType());
                            int categoryId = category.getCategoryID();

                            if (isPersonal) {
                                if (isExpense && categoryId < 201) expense.add(category);
                                if (isIncome && categoryId < 201) income.add(category);
                            } else {
                                if (isExpense && categoryId >= 201) expense.add(category);
                                if (isIncome && categoryId >= 201) income.add(category);
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
                    categoryEditor.apply(); // Lưu thay đổi
                } else {
                    Log.e("initCategory", "No categories found for userId: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log lỗi nếu có vấn đề với Firebase
                Log.e("initCategory", "Error fetching categories: " + error.getMessage());
            }
        });
    }

    private void saveUserToSharedPreferences(DTBase.User authUser) {
        Gson gson = new Gson();
        String userJson = gson.toJson(authUser);

        SharedPreferences userSharedPreferences = getSharedPreferences("MyUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = userSharedPreferences.edit();
        editor.putString("userJson", userJson);
        editor.apply();

    }


}