package com.example.quanlytaichinh.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etEmail, etOTP;
    private Button btnSendOTP, btnChangePassword;
    private String sentOTP;
    private DTBase.User authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        authUser = (DTBase.User) getIntent().getSerializableExtra("User");

        etEmail = findViewById(R.id.et_email);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etOTP = findViewById(R.id.et_otp);
        btnSendOTP = findViewById(R.id.btn_send_otp);
        btnChangePassword = findViewById(R.id.btn_change_password);

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
