package com.example.quanlytaichinh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoAccountActivity extends AppCompatActivity {

    private UserData user;
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

        // Nhận đối tượng UserData từ Intent
        Intent intent = getIntent();
        user = (UserData) intent.getSerializableExtra("userData"); // Lấy UserData từ Intent

        // Khởi tạo Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Hiển thị thông tin người dùng lên các EditText
        if (user != null) {
            etUsername.setText(user.getUsername());
            etEmail.setText(user.getEmail());
            etBirthday.setText(user.getBirthday());
            etAddress.setText(user.getAddress());

            // Thiết lập giới tính trong RadioGroup
            if (user.getGender() != null) {
                if (user.getGender().equals("Nam")) {
                    rgGender.check(R.id.radioButton);
                } else if (user.getGender().equals("Nữ")) {
                    rgGender.check(R.id.radioButton2);
                }
            }
        }

        btnSave.setOnClickListener(view -> saveUserInfo());
    }

    private void saveUserInfo() {
        // Lưu thông tin từ EditText vào các biến
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Lưu gender nếu cần thiết
        int selectedId = rgGender.getCheckedRadioButtonId();
        String gender = "";
        if (selectedId == R.id.radioButton) {
            gender = "Nam";
        } else if (selectedId == R.id.radioButton2) {
            gender = "Nữ";
        }

        // Chỉ cập nhật username nếu người dùng đã nhập một giá trị mới
        if (!username.isEmpty()) {
            user.setUsername(username);
        }

        // Cập nhật các trường khác
        user.setEmail(email);
        user.setBirthday(birthday);
        user.setAddress(address);
        user.setGender(gender); // Lưu giới tính vào đối tượng

        // Lưu UserData vào Firebase
        saveUserDataToFirebase(user);
    }

    private void saveUserDataToFirebase(UserData user) {
        // Sử dụng ID của người dùng làm khóa trong Firebase
        if (user.getId() != null) { // Kiểm tra ID không null
            databaseReference.child(user.getId()).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(InfoAccountActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình trước
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(InfoAccountActivity.this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(InfoAccountActivity.this, "ID người dùng không hợp lệ.", Toast.LENGTH_SHORT).show();
        }
    }


}
