package com.example.quanlytaichinh;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class GeneralActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        // Khởi tạo các ImageButton
        ImageButton ibHome = findViewById(R.id.ib_home);
        ImageButton ibCalendar = findViewById(R.id.ib_calendar);
        ImageButton ibInsert = findViewById(R.id.ib_insert);
        ImageButton ibChart = findViewById(R.id.ib_chart);
        ImageButton ibSetting = findViewById(R.id.ib_setting);

        // Hiển thị HomeFragment mặc định khi đăng nhập
        showFragment(new HomeFragment());

        // Thiết lập sự kiện cho từng ImageButton
        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị Fragment cho Home
                showFragment(new HomeFragment());
            }
        });

        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị Fragment cho Calendar
                showFragment(new CalendarFragment());
            }
        });

        ibInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị Fragment cho Insert
                showFragment(new InsertFragment());
            }
        });

        ibChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị Fragment cho Chart
                showFragment(new ChartFragment());
            }
        });

        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị Fragment cho Settings
                showFragment(new SettingFragment());
            }
        });
    }

    // Phương thức hiển thị Fragment
    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null) // Thêm vào back stack để người dùng có thể quay lại
                .commit();
    }
}
