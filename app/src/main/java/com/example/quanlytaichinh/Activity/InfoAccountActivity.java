package com.example.quanlytaichinh.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoAccountActivity extends AppCompatActivity {

    private DTBase.User authUser;
    private DatabaseReference databaseReference; // Thêm biến databaseReference

    private EditText etUsername;
    private EditText etEmail;
    private EditText etBirthday;
    private EditText etAddress;
    private RadioGroup rgGender;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_account_layout);

        // Khởi tạo các view
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etBirthday = findViewById(R.id.et_birthday);
        etAddress = findViewById(R.id.et_address);
        rgGender = findViewById(R.id.radioGroup);
        btnSave = findViewById(R.id.btn_save);

        // Nhận từ Intent
        authUser = (DTBase.User) getIntent().getSerializableExtra("User");

        // Khởi tạo Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Hiển thị thông tin người dùng lên các EditText
        if (authUser != null) {
            etUsername.setText(authUser.getUserName());
            etEmail.setText(authUser.getUserMail());
            etBirthday.setText(authUser.getUserBirthday());
            etAddress.setText(authUser.getUserAddress());

            int Avatar = authUser.getUserAvatar(); //chưa dùng đến

            String gender = authUser.getUserGender();
            // Thiết lập giới tính trong RadioGroup
            if (gender != null) {
                if (gender.equals("Male")) {
                    rgGender.check(R.id.radioButton);
                } else if (gender.equals("Nữ")) {
                    rgGender.check(R.id.radioButton2);
                }
            }
        }

    }




}
