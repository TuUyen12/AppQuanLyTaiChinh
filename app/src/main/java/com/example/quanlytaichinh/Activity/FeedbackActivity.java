package com.example.quanlytaichinh.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;

public class FeedbackActivity extends AppCompatActivity {
    private DTBase.User authUser;
    private EditText etFeedback;
    private  DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Ánh xạ view
        etFeedback = findViewById(R.id.feedbackEditText);
        Button submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        // Nhận thông tin người dùng từ Intent
        authUser = (DTBase.User) getIntent().getSerializableExtra("User");

        submitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = etFeedback.getText().toString().trim();
                if (feedback.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Please enter your feedback!", Toast.LENGTH_SHORT).show();
                } else {
                    // Ẩn bàn phím
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    etFeedback.setText("");
                    addFeedback(feedback, authUser.getUserID());
                }
            }
        });
    }
    public void addFeedback(String feedback, int userId) {
        // Tạo ID ngẫu nhiên gồm 6 ký tự
        String id = generateRandomId(6);
        Calendar Date = Calendar.getInstance();
        int day = Date.get(Calendar.DAY_OF_MONTH);
        int month = Date.get(Calendar.MONTH) + 1;
        int year = Date.get(Calendar.YEAR);
        String date = day + "/" + month + "/" + year;

        // Kiểm tra xem ID đã tồn tại chưa
        mDatabase.child("FEEDBACKS").child(String.valueOf(userId)).child(id)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Nếu ID đã tồn tại, tạo lại ID mới
                        addFeedback(feedback, userId); // Gọi lại phương thức để tạo ID mới
                    } else {
                        // Nếu ID chưa tồn tại, thêm phản hồi
                        mDatabase.child("FEEDBACK").child(String.valueOf(userId)).child(id)
                                .setValue(new Feedback(feedback, date))
                                .addOnCompleteListener(addTask -> {
                                    if (addTask.isSuccessful()) {
                                        Toast.makeText(FeedbackActivity.this, "Feedback added successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(FeedbackActivity.this, "Error: " + addTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    // Hàm tạo ID ngẫu nhiên với độ dài tùy chỉnh
    private String generateRandomId(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            id.append(characters.charAt(random.nextInt(characters.length())));
        }
        return id.toString();
    }
    // Định nghĩa lớp Feedback
    public static class Feedback {
        private String feedback;
        private String date;

        public Feedback(String feedback, String date) {
            this.feedback = feedback;
            this.date = date;
        }

        public String getFeedback() {
            return feedback;
        }

        public String getDate() {
            return date;
        }
    }

}
