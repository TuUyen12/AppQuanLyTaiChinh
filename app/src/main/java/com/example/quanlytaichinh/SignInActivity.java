package com.example.quanlytaichinh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button loginButton;
    private UserData userData; // Đối tượng UserData để lưu thông tin người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views
        EditText et_email = findViewById(R.id.et_email);
        EditText et_password = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_sign_in);

        // Set up the login button event
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    signIn(email, password);
                } else {
                    Toast.makeText(SignInActivity.this, "Vui lòng điền email và password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserData(user);
                            }

                            Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // Chuyển `UserData` sang `GeneralActivity`
                            Intent intent = new Intent(SignInActivity.this, GeneralActivity.class);
                            intent.putExtra("username", userData.getUsername());
                            intent.putExtra("email", userData.getEmail());
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                            Toast.makeText(SignInActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    // Hàm lưu thông tin người dùng từ Firebase xuống UserData
    private void saveUserData(FirebaseUser firebaseUser) {
        // Lấy email và username từ FirebaseUser
        String email = firebaseUser.getEmail();
        String username = firebaseUser.getDisplayName();  // Firebase có thể không cung cấp username, cần tùy chỉnh nếu không có

        // Nếu username null, gán giá trị mặc định
        if (username == null || username.isEmpty()) {
            username = email != null ? email.split("@")[0] : "Unknown User";
        }

        // Khởi tạo đối tượng UserData và lưu thông tin
        userData = new UserData(username, "password_placeholder", email); // Password có thể không lưu tại đây

        // Debug thông tin
        Toast.makeText(this, "UserData saved: " + username + ", " + email, Toast.LENGTH_SHORT).show();
    }
}