package com.example.quanlytaichinh.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;

public class FeedbackActivity extends AppCompatActivity {
    private DTBase.User authUser;
    private EditText feedbackEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);

        // Ánh xạ view
        feedbackEditText = findViewById(R.id.feedbackEditText);
        Button submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        // Nhận thông tin người dùng từ Intent
        authUser = (DTBase.User) getIntent().getSerializableExtra("User");

        submitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = feedbackEditText.getText().toString().trim();
                if (feedback.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Please enter your feedback!", Toast.LENGTH_SHORT).show();
                } else {
                    sendFeedbackEmail(feedback);
                }
            }
        });
    }

    private void sendFeedbackEmail(String feedback) {
        // Địa chỉ email nhận phản hồi
        String recipientEmail = "financialmanagementapp2024@gmail.com";

        // Chủ đề email
        String subject = "ID: " + (authUser != null ? authUser.getUserID() : "Unknown") + " - Feedback";

        // Intent để mở ứng dụng email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822"); // Chỉ mở các ứng dụng email
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail}); // Địa chỉ email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); // Chủ đề
        emailIntent.putExtra(Intent.EXTRA_TEXT, feedback); // Nội dung

        // Kiểm tra nếu có ứng dụng email khả dụng
        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email app found on your device", Toast.LENGTH_SHORT).show();
        }
    }

}
