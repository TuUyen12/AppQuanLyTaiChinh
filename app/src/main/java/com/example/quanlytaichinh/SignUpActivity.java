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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        EditText et_username = findViewById(R.id.et_username);
        EditText et_email = findViewById(R.id.et_email);
        EditText et_password = findViewById(R.id.et_password);
        EditText et_confirm_password = findViewById(R.id.et_confirm_password);
        Button btn_sign_up = findViewById(R.id.btn_sign_up);

        // Giao diện
        ImageButton ib_eye = findViewById(R.id.ib_eye);
        final boolean[] isPasswordVisible = {false};

        ib_eye.setOnClickListener(v -> {
            isPasswordVisible[0] = !isPasswordVisible[0];
            ib_eye.setImageResource(isPasswordVisible[0] ? R.drawable.show_with_size : R.drawable.hide_with_size);
            et_password.setInputType(isPasswordVisible[0] ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et_password.setSelection(et_password.getText().length());
        });

        ImageButton ib_eye1 = findViewById(R.id.ib_eye1);
        final boolean[] isConfirmPasswordVisible = {false};
        ib_eye1.setOnClickListener(v -> {
            isConfirmPasswordVisible[0] = !isConfirmPasswordVisible[0];
            ib_eye1.setImageResource(isConfirmPasswordVisible[0] ? R.drawable.show_with_size : R.drawable.hide_with_size);
            et_confirm_password.setInputType(isConfirmPasswordVisible[0] ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et_confirm_password.setSelection(et_confirm_password.getText().length());
        });

        auth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        btn_sign_up.setOnClickListener(view -> {
            String username = et_username.getText().toString().trim();
            String email = et_email.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String confirm_password = et_confirm_password.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm_password) || !password.equals(confirm_password)) {
                Toast.makeText(SignUpActivity.this, "Vui lòng nhập đầy đủ thông tin và kiểm tra lại mật khẩu!", Toast.LENGTH_SHORT).show();
            } else {
                RegisterUser(email, password, username);
            }
        });
    }

    private void RegisterUser(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        SaveUserData(username, email);
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
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

        // Tạo một đối tượng UserData mới
        UserData user = new UserData(username, email);

        // Tạo ID cho người dùng
        String customUserId = user.getId(); // Lấy ID đã được tạo trong UserData

        // Lưu dữ liệu người dùng vào Firebase
        databasereference.child(customUserId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "Dữ liệu người dùng đã được lưu thành công với ID: " + customUserId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpActivity.this, "Lưu dữ liệu người dùng thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
