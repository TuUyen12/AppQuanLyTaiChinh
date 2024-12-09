package com.example.quanlytaichinh.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.Fragment.AccountFragment;
import com.example.quanlytaichinh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import android.util.Patterns; // Thêm thư viện Patterns

public class InfoAccountActivity extends AppCompatActivity {

    private DTBase.User authUser;
    private DatabaseReference databaseReference; // Biến databaseReference

    private EditText etUsername;
    private TextView tvEmail;
    private EditText etBirthday;
    private EditText etAddress;
    private RadioGroup rgGender;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_account_layout);

        // Khởi tạo các view
        initVariable();

        // Khởi tạo Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference(); // Khởi tạo đúng đối tượng

        // Nhận từ Intent
        authUser = getUserFromSharedPreferences();

        // Hiển thị thông tin người dùng lên các EditText
        if (authUser != null) {
            etUsername.setText(authUser.getUserName());
            tvEmail.setText(authUser.getUserMail());
            etBirthday.setText(authUser.getUserBirthday());
            etAddress.setText(authUser.getUserAddress());


            String gender = authUser.getUserGender();
            // Thiết lập giới tính trong RadioGroup
            if (gender != null) {
                if (gender.equals("Male")) {
                    rgGender.check(R.id.radioButton);
                } else if (gender.equals("Female")) {
                    rgGender.check(R.id.radioButton2);
                }
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String email = tvEmail.getText().toString();
                String birthday = etBirthday.getText().toString();
                String address = etAddress.getText().toString();
                int Avatar = 0; //chưa dùng đến
                String gender = "";
                int selectedRadioButtonId = rgGender.getCheckedRadioButtonId();
                if (selectedRadioButtonId == R.id.radioButton) {
                    gender = "Male";
                } else if (selectedRadioButtonId == R.id.radioButton2) {
                    gender = "Female";
                }

                // Kiểm tra định dạng email
                if (!isValidEmail(email)) {
                    Toast.makeText(InfoAccountActivity.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveUserInfo(username, email, birthday, address, gender);
                // Lưu vào sharedPreferences MyUser
                updateUserSharedPreferences(authUser);

            }
        });
    }

    private void initVariable() {
        etUsername = findViewById(R.id.et_username);
        tvEmail = findViewById(R.id.tv_email1);
        etBirthday = findViewById(R.id.et_birthday);
        etAddress = findViewById(R.id.et_address);
        rgGender = findViewById(R.id.radioGroup);
        btnSave = findViewById(R.id.btn_save);
    }

    private void saveUserInfo(String username, String email, String birthday, String address, String gender) {
        // Thực hiện lưu thông tin người dùng vào Firebase Database
        authUser.setUserName(username);
        authUser.setUserMail(email);
        authUser.setUserBirthday(birthday);
        authUser.setUserAddress(address);
        authUser.setUserGender(gender);

        // Lưu thông tin người dùng vào Firebase, sử dụng child() cho "USERS" và ID của người dùng
        databaseReference.child("USERS").child(String.valueOf(authUser.getUserID())).setValue(authUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Hiển thị Toast thông báo thành công
                        Toast.makeText(InfoAccountActivity.this, "Saved information successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Hiển thị Toast thông báo thất bại
                        Toast.makeText(InfoAccountActivity.this, "Save information failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    finish(); // Đóng Activity sau khi lưu thành công
                });

    }

    // Kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    // Lấy User từ SharedPreferences
    private DTBase.User getUserFromSharedPreferences() {
        SharedPreferences userSharedPreferences = getSharedPreferences("MyUser", MODE_PRIVATE);
        String userJson = userSharedPreferences.getString("userJson", null);

        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, DTBase.User.class);
        }
        return null;
    }

    // Cập nhật thông tin User trong SharedPreferences
    private void updateUserSharedPreferences(DTBase.User updatedUser) {
        Gson gson = new Gson();
        String userJson = gson.toJson(updatedUser);

        SharedPreferences userSharedPreferences = getSharedPreferences("MyUser", MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userSharedPreferences.edit();
        userEditor.putString("userJson", userJson);
        userEditor.apply();
    }
}
