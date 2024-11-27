package com.example.quanlytaichinh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_layout);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo các thành phần giao diện
        etEmail = findViewById(R.id.et_email_forgot_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        // Xử lý sự kiện click cho nút đặt lại mật khẩu
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi yêu cầu đặt lại mật khẩu
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng ForgotPassActivity
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Gọi super để đảm bảo hành vi mặc định
        super.onBackPressed();
        // Quay lại màn hình đăng nhập
        startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
        finish();
    }
}
