package com.example.quanlytaichinh.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etEmail;
    private Button btnChangePassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        mAuth = FirebaseAuth.getInstance();

        etCurrentPassword = findViewById(R.id.et_current_password);
        etEmail = findViewById(R.id.et_email);
        btnChangePassword = findViewById(R.id.btn_change_password);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(email)) {
            Toast.makeText(ChangePasswordActivity.this, "Please enter current password and email.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy người dùng hiện tại
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getEmail().equals(email)) {
            // Đăng nhập lại với email và mật khẩu hiện tại
            mAuth.signInWithEmailAndPassword(email, currentPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Gửi email thay đổi mật khẩu
                    sendPasswordResetEmail(email);
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Current password is incorrect.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ChangePasswordActivity.this, "Email does not match the current user.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ChangePasswordActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChangePasswordActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
