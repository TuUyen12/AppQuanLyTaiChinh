package com.example.quanlytaichinh.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnChangePassword;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        // Khởi tạo FirebaseAuth và lấy người dùng hiện tại
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Nếu không có người dùng nào đăng nhập, thông báo và đóng Activity
            Toast.makeText(this, "No user is logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các thành phần giao diện
        initUI();

        // Xử lý sự kiện click cho nút thay đổi mật khẩu
        btnChangePassword.setOnClickListener(view -> changePassword());
    }

    private void initUI() {
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // Kiểm tra các trường nhập liệu
        if (validateInputs(currentPassword, newPassword, confirmNewPassword)) {
            // Cập nhật mật khẩu mới
            updateUserPassword(newPassword);
        }
    }

    private boolean validateInputs(String currentPassword, String newPassword, String confirmNewPassword) {
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Please enter your current password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "New password and confirmation do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateUserPassword(String newPassword) {
        currentUser.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
