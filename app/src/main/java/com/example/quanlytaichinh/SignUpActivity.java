package com.example.quanlytaichinh;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAnalytics mFirebaseAnalytics; // Thêm FirebaseAnalytics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        EditText et_username = findViewById(R.id.et_username);
        EditText et_email = findViewById(R.id.et_email);
        EditText et_password = findViewById(R.id.et_password);
        EditText et_confirm_password = findViewById(R.id.et_cf_password);
        Button btn_sign_up = findViewById(R.id.btn_sign_up);

        // Khởi tạo Firebase Auth và Firebase Analytics
        auth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this); // Khởi tạo Firebase Analytics

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    private void RegisterUser(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                SaveUserData(user.getUid(), username, email, password);
                Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                // Ghi lại sự kiện đăng ký thành công
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "email_password");
                mFirebaseAnalytics.logEvent("user_registration_success", bundle); // Ghi lại sự kiện

                finish();
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(SignUpActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SaveUserData(String userId, String username, String email, String password) {
        FirebaseDatabase firebasedatabase = FirebaseDatabase.getInstance();
        DatabaseReference databasereference = firebasedatabase.getReference("users");

        UserData user = new UserData(username, password, email); // Đảm bảo rằng username được sử dụng ở đây

        databasereference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "Dữ liệu người dùng đã được lưu thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpActivity.this, "Lưu dữ liệu người dùng thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
