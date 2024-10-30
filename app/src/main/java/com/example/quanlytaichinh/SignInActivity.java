package com.example.quanlytaichinh;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button loginButton, resetPasswordButton; // Thêm nút reset password
    private UserData userData; // Đối tượng UserData để lưu thông tin người dùng


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
        loginButton = findViewById(R.id.btn_sign_in);

        loginButton.setOnClickListener(v -> {
            String email = et_email.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                signIn(email, password);
            } else {
                Toast.makeText(SignInActivity.this, "Vui lòng điền email và password", Toast.LENGTH_SHORT).show();
            }
        });
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


    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user);
                        }

                        Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Bật dialog_account_type để chọn loại tài khoản và lưu vào SharedPreferences
                        showAccountTypeDialog();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                        Toast.makeText(SignInActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserData(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail();
        String username = firebaseUser.getDisplayName();

        if (username == null || username.isEmpty()) {
            username = email != null ? email.split("@")[0] : "Unknown User";
        }

        userData = new UserData(username, email);

        Toast.makeText(this, "UserData saved: " + username + ", " + email, Toast.LENGTH_SHORT).show();
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

                // Lưu giá trị boolean vào SharedPreferences
                if (which == 0) {
                    editor.putBoolean("isPersonnal", true); // Personal
                } else {
                    editor.putBoolean("isPersonnal", false); // Business
                }
                editor.apply();

                dialog.dismiss();

                // Chuyển `UserData` sang `GeneralActivity` sau khi đã lưu loại tài khoản
                Intent intent = new Intent(SignInActivity.this, GeneralActivity.class);
                intent.putExtra("username", userData.getUsername());
                intent.putExtra("email", userData.getEmail());
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }
}
