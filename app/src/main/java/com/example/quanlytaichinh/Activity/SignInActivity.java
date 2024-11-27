package com.example.quanlytaichinh.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);

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

    private void signIn(String username, String password) {
        DTBase database = new DTBase();
        database.CheckSignIn(username, password, (success, MyUser, financialData) -> {
            if (success) {
                // Đăng nhập thành công
                Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                authUser = MyUser;
                userId = MyUser.getUserID();
                // Lưu thông tin tài chính cho người dùng (danh sách Financial)
                userFinancialList.clear(); // Đảm bảo danh sách trước đó bị xóa

                if (financialData != null) {
                    for (Financial financial : financialData) {
                        userFinancialList.add(financial); // Thêm vào danh sách tài chính của người dùng
                    }
                }
                // Hiển thị dialog chọn loại tài khoản hoặc tiếp tục điều hướng tới màn hình chính
                showAccountTypeDialog();
            } else {
                Toast.makeText(SignInActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
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


                // Chuyển `authUser` sang `GeneralActivity` sau khi đã lưu loại tài khoản
                Intent intent = new Intent(SignInActivity.this, GeneralActivity.class);
                intent.putExtra("User", authUser);
                startActivity(intent);
                Intent feedbackIntent = new Intent(SignInActivity.this, FeedbackActivity.class);
                feedbackIntent.putExtra("User", authUser);
                finish();

            }
        });
        builder.create().show();
    }
    private void initCategory(int userId, boolean isPersonal) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("CATEGORIES").child(String.valueOf(userId));
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Duyệt qua các danh mục và phân loại chúng vào 2 mảng expense và income
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    DTBase.Category category = categorySnapshot.getValue(DTBase.Category.class);
                    if (category != null) {
                        Category.add(category);
                        if(isPersonal){
                            // Kiểm tra loại category là "expense" hay "income"
                            if ("expense".equals(category.getCategoryType())) {
                                if(category.getCategoryID() < 20) expense.add(category);
                            } else if ("income".equals(category.getCategoryType())) {
                                if(category.getCategoryID() < 20) income.add(category);
                            }
                        }else{
                            if ("expense".equals(category.getCategoryType())) {
                                if(category.getCategoryID() >= 20) expense.add(category);
                            }
                            else if ("income".equals(category.getCategoryType())) {
                                if(category.getCategoryID() >= 20) income.add(category);
                            }
                        }
                    }
                }

                // Chuyển mảng expense và income thành chuỗi JSON
                Gson gson = new Gson();
                String expenseJson = gson.toJson(expense);
                String incomeJson = gson.toJson(income);
                String categoryJson = gson.toJson(Category);

                // Lưu các chuỗi JSON vào SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyCategory", MODE_PRIVATE);
                SharedPreferences.Editor categoryEditor = sharedPreferences.edit();
                categoryEditor.putString("expense", expenseJson);  // Lưu mảng expense
                categoryEditor.putString("income", incomeJson);    // Lưu mảng income
                categoryEditor.putString("category", categoryJson);    // Lưu mảng category
                categoryEditor.apply();  // Lưu thay đổi



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi khi truy cập Firebase
                System.out.println("Error fetching categories: " + error.getMessage());
            }
        });
    }


}
