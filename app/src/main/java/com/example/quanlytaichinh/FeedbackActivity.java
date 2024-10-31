package com.example.quanlytaichinh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);

        feedbackEditText = findViewById(R.id.feedbackEditText);
        Button submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        submitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = feedbackEditText.getText().toString().trim();
                if (feedback.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Vui lòng nhập phản hồi của bạn", Toast.LENGTH_SHORT).show();
                } else {
                    sendFeedbackEmail(feedback);
                }
            }
        });
    }

    private void sendFeedbackEmail(String feedback) {
        String recipientEmail = "financialmanagementapp2024@gmail.com";  // Địa chỉ email nhận phản hồi
        String subject = "User Feedback";  // Chủ đề email

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + recipientEmail));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, feedback);

        // Kiểm tra nếu có ứng dụng email trên thiết bị
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
        } else {
            Toast.makeText(this, "Không tìm thấy ứng dụng email nào", Toast.LENGTH_SHORT).show();
        }
    }
}