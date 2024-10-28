package com.example.quanlytaichinh;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private long currentUserId = 22520000; // ID bắt đầu là 22520000

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        EditText et_username = findViewById(R.id.et_username);
        EditText et_email = findViewById(R.id.et_email);
        EditText et_password = findViewById(R.id.et_password);
        EditText et_confirm_password = findViewById(R.id.et_confirm_password);
        Button btn_sign_up = findViewById(R.id.btn_sign_up);

        //Giao diện
        ImageButton ib_eye = findViewById(R.id.ib_eye);
        final boolean[] isPasswordVisible = {false}; // Sử dụng mảng để có thể thay đổi giá trị

        ib_eye.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                isPasswordVisible[0] = false;
                ib_eye.setImageResource(R.drawable.hide_with_size);
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                isPasswordVisible[0] = true;
                ib_eye.setImageResource(R.drawable.show_with_size);
                et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }

            // Đặt con trỏ ở cuối văn bản
            et_password.setSelection(et_password.getText().length());
        });

        ImageButton ib_eye1 = findViewById(R.id.ib_eye1);
        final boolean[] isConfirmPasswordVisible = {false}; // Sử dụng mảng để có thể thay đổi giá trị
        ib_eye1.setOnClickListener(v -> {
            if (isConfirmPasswordVisible[0]) {
                isConfirmPasswordVisible[0] = false;
                ib_eye1.setImageResource(R.drawable.hide_with_size);
                et_confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                isConfirmPasswordVisible[0] = true;
                ib_eye1.setImageResource(R.drawable.show_with_size);
                et_confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }

            et_confirm_password.setSelection(et_confirm_password.getText().length());
        });


        // Khởi tạo Firebase Auth và Firebase Analytics
        auth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        btn_sign_up.setOnClickListener(view -> {
            String username = et_username.getText().toString().trim();
            String email = et_email.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String confirm_password = et_confirm_password.getText().toString().trim();

            // Kiểm tra các trường thông tin
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(SignUpActivity.this, "Vui lòng nhập username!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignUpActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(SignUpActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(confirm_password)) {
                Toast.makeText(SignUpActivity.this, "Vui lòng nhập lại mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm_password)) {
                Toast.makeText(SignUpActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng ký người dùng
            RegisterUser(email, password, username);
        });

    }

    private void RegisterUser(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công
                        FirebaseUser user = auth.getCurrentUser();
                        SaveUserData(username, email);
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(SignUpActivity.this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void SaveUserData(String username, String email) {
        FirebaseDatabase firebasedatabase = FirebaseDatabase.getInstance();
        DatabaseReference databasereference = firebasedatabase.getReference("users");

        // Kiểm tra ID người dùng hiện tại
        databasereference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while (snapshot.hasChild(String.valueOf(currentUserId))) {
                    currentUserId++; // Tăng ID cho đến khi tìm được ID chưa sử dụng
                }

                // Tạo ID người dùng mới
                String customUserId = generateCustomUserId();

                // Khởi tạo đối tượng UserData với username và email
                UserData user = new UserData(username, email);

                // Lưu dữ liệu người dùng vào Firebase với ID tự tạo
                databasereference.child(customUserId).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Dữ liệu người dùng đã được lưu thành công với ID: " + customUserId, Toast.LENGTH_SHORT).show();
                        // Tăng ID để chuẩn bị cho người dùng tiếp theo
                        currentUserId++;
                    } else {
                        Toast.makeText(SignUpActivity.this, "Lưu dữ liệu người dùng thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignUpActivity.this, "Lỗi khi kiểm tra ID người dùng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm tạo ID người dùng tùy chỉnh (8 chữ số, bắt đầu bằng 2252)
    private String generateCustomUserId() {
        return String.valueOf(currentUserId);
    }

}
