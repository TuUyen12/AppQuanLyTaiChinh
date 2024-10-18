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
    private Button loginButton, resetPasswordButton; // Thêm nút reset password
    private UserData userData; // Đối tượng UserData để lưu thông tin người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);

        mAuth = FirebaseAuth.getInstance();

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
}
