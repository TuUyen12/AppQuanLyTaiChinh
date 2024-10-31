package com.example.quanlytaichinh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class TimeSettingActivity extends AppCompatActivity {

    private static final String KEY_SELECTED_TIME_FORMAT = "SelectedTimeFormat";
    private static final String PREFS_NAME = "TimeSettings"; // Tên file SharedPreferences

    private TextView tvSelectedTimeFormat;
    private String selectedFormat; // Lưu định dạng được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_setting_layout);

        tvSelectedTimeFormat = findViewById(R.id.tv_selected_time_format);
        ListView lvTimeFormat = findViewById(R.id.lv_time_format);
        Button btnSave = findViewById(R.id.btn_save); // Giả định bạn có một nút Save

        // Tạo danh sách các định dạng thời gian
        List<String> timeFormats = new ArrayList<>();
        timeFormats.add("dd/mm/yyyy");
        timeFormats.add("mm/dd/yyyy");
        timeFormats.add("yyyy/mm/dd");
        timeFormats.add("dd-mm-yyyy");
        timeFormats.add("mm-dd-yyyy");
        timeFormats.add("yyyy-mm-dd");

        // Tạo adapter và thiết lập cho ListView
        TimeFormatAdapter adapter = new TimeFormatAdapter(this, timeFormats);
        lvTimeFormat.setAdapter(adapter);

        // Đọc định dạng đã chọn từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedFormat = sharedPreferences.getString(KEY_SELECTED_TIME_FORMAT, null);

        if (selectedFormat != null) {
            tvSelectedTimeFormat.setText(selectedFormat); // Hiển thị định dạng đã lưu
            Toast.makeText(this, "Selected format: " + selectedFormat, Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện chọn định dạng
        lvTimeFormat.setOnItemClickListener((parent, view, position, id) -> {
            selectedFormat = timeFormats.get(position); // Cập nhật định dạng đã chọn
            Toast.makeText(TimeSettingActivity.this, "Selected format: " + selectedFormat, Toast.LENGTH_SHORT).show();
        });

        // Xử lý sự kiện nút Save
        btnSave.setOnClickListener(v -> {
            if (selectedFormat != null) {
                // Lưu định dạng đã chọn vào SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_SELECTED_TIME_FORMAT, selectedFormat);
                editor.apply();

                // Cập nhật TextView hiển thị định dạng đã chọn
                tvSelectedTimeFormat.setText(selectedFormat);
                Toast.makeText(TimeSettingActivity.this, "Saved format: " + selectedFormat, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TimeSettingActivity.this, "No format selected!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
