package com.example.quanlytaichinh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.quanlytaichinh.R;

import java.util.ArrayList;


public class SignUpActivity extends AppCompatActivity {


    ArrayList<DTBase.Category> Category; // Danh sách cho cả income và expense
    ArrayList<DTBase.Financial> Financial;

    EditText et_username, et_email, et_password, et_confirm_password;
    Button btn_sign_up;
    ImageButton ib_eye, ib_eye1;

    private FirebaseAuth mAuth; // Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        mAuth = FirebaseAuth.getInstance();

        //Khởi tạo các thành phần giao diện
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        ib_eye = findViewById(R.id.ib_eye);
        ib_eye1 = findViewById(R.id.ib_eye1);

        setPasswordVisibilityToggle(ib_eye, et_password);
        setPasswordVisibilityToggle(ib_eye1, et_confirm_password);
        evenClickSignUp(btn_sign_up, et_username, et_email, et_password, et_confirm_password);


    }

    public void RegisterUser(String email, String password, String username, String confirm_password) {
        if (email.isEmpty() || password.isEmpty() || confirm_password.isEmpty() || username.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please fill in all information", Toast.LENGTH_LONG).show();
        } else if (!password.equals(confirm_password)) {
            Toast.makeText(SignUpActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
        } else {
            try {
                DTBase dtBase = new DTBase();

                // Kiểm tra username trước
                dtBase.isUserNameExists(username, new DTBase.FirebaseCallback<Boolean>() {
                    @Override
                    public void onCallback(Boolean usernameExists) {
                        if (usernameExists) {
                            Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                        } else {
                            // Nếu username không tồn tại, kiểm tra email
                            dtBase.isUserEmailExists(email, new DTBase.FirebaseCallback<Boolean>() {
                                @Override
                                public void onCallback(Boolean emailExists) {
                                    if (emailExists) {
                                        Toast.makeText(SignUpActivity.this, "Email already exists", Toast.LENGTH_LONG).show();
                                    } else {
                                        // Lấy NewUserID từ Firebase
                                        dtBase.getNewUserID(new DTBase.FirebaseCallback<Integer>() {
                                            @Override
                                            public void onCallback(Integer newUserId) {
                                                if (newUserId != null) {
                                                    // Gọi hàm thêm mới với newUserId kiểu Integer
                                                    mAuth.createUserWithEmailAndPassword(email, password)
                                                            .addOnCompleteListener(SignUpActivity.this, task -> {
                                                                if (task.isSuccessful()) {
                                                                    // Đăng ký thành công
                                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                                    dtBase.addNewUser(newUserId, username, email);
                                                                    Toast.makeText(SignUpActivity.this, "Register successfully!", Toast.LENGTH_LONG).show();
                                                                    initCategory(newUserId);
                                                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    // Đăng ký thất bại
                                                                    Toast.makeText(SignUpActivity.this, "Failed to Sign Up", Toast.LENGTH_LONG).show();
                                                                }
                                                            });

                                                } else {
                                                    Toast.makeText(SignUpActivity.this, "Failed to generate user ID", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onError(String error) {
                                                Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(SignUpActivity.this, "Error connecting to the database", Toast.LENGTH_LONG).show();
            }
        }
    }
    // Phương thức khởi tạo các category
    private void initCategory(int userId) {
        Category = new ArrayList<>();
        Category.add(new DTBase.Category(1, R.drawable.ic_transport, "Transport", "expense", userId));
        Category.add(new DTBase.Category(2, R.drawable.ic_food, "Food", "expense", userId));
        Category.add(new DTBase.Category(3, R.drawable.ic_edu, "Education", "expense", userId));
        Category.add(new DTBase.Category(4, R.drawable.ic_cloths, "Cloths", "expense", userId));
        Category.add(new DTBase.Category(5, R.drawable.ic_medical, "Medical", "expense", userId));
        Category.add(new DTBase.Category(6, R.drawable.ic_contact_fee, "Contact Fee", "expense", userId));
        Category.add(new DTBase.Category(7, R.drawable.ic_cosmetic, "Cosmetic", "expense", userId));
        Category.add(new DTBase.Category(8, R.drawable.ic_housing_expenses, "Housing expense", "expense", userId));

        Category.add(new DTBase.Category(9, R.drawable.ic_salary, "Salary", "income", userId));
        Category.add(new DTBase.Category(10, R.drawable.ic_bonus, "Bonus", "income", userId));
        Category.add(new DTBase.Category(11, R.drawable.ic_investment, "Investment", "income", userId));

        Category.add(new DTBase.Category(20,R.drawable.ic_office_supplies, "Office Supplies", "expense", userId));
        Category.add(new DTBase.Category(21,R.drawable.ic_travel, "Travel", "expense", userId));
        Category.add(new DTBase.Category(22,R.drawable.ic_utilities, "Utilities", "expense", userId));
        Category.add(new DTBase.Category(23,R.drawable.ic_marketing, "Marketing", "expense", userId));
        Category.add(new DTBase.Category(24,R.drawable.ic_personnel, "Salary", "expense", userId));
        Category.add(new DTBase.Category(25,R.drawable.ic_maintenance, "Maintenance", "expense", userId));
        Category.add(new DTBase.Category(26,R.drawable.ic_project, "Project Costs", "expense", userId));

        Category.add(new DTBase.Category(27,R.drawable.ic_project_payment, "Project Payment", "income", userId));
        Category.add(new DTBase.Category(28,R.drawable.ic_investment, "Investment", "income", userId));
        Category.add(new DTBase.Category(29,R.drawable.ic_sales, "Sales Revenue", "income", userId));

        DTBase dtBase = new DTBase();
        dtBase.addListCategorytoFirebase(Category, userId);

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
    // Sự kiện click đăng ký
    private void evenClickSignUp(Button btn_sign_up, EditText et_username, EditText et_email, EditText et_password, EditText et_confirm_password) {
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String confirm_password = et_confirm_password.getText().toString().trim();
                RegisterUser (email, password, username, confirm_password);
            }
        });
    }

}
