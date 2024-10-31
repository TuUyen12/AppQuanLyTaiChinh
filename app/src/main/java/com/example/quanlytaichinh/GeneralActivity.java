package com.example.quanlytaichinh;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class GeneralActivity extends AppCompatActivity {
    private boolean isHome = true; // Biến để theo dõi trạng thái của HomeFragment

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

        // Thiết lập mặc định cho nút Home
        ibHome.setBackgroundColor(ContextCompat.getColor(this, R.color.white)); // Nền trắng
        ibHome.setImageResource(R.drawable.home1_with_size); // Hình ảnh hiển thị ban đầu


        // Hiển thị HomeFragment mặc định khi đăng nhập
        showFragment(new HomeFragment());

        // Nhận dữ liệu từ Intent
        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        UserData userData = (UserData) getIntent().getSerializableExtra("userData");

        // Thiết lập sự kiện cho nút Home
        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị Fragment cho Home
                showFragment(new HomeFragment());

                // Cập nhật trạng thái của các nút khác
                resetButtonStyles(ibHome, ibCalendar, ibInsert, ibChart, ibSetting, ibHome);
            }
        });

        // Gán sự kiện cho các nút khác
        ibCalendar.setOnClickListener(view -> {
            showFragment(new CalendarFragment());
            resetButtonStyles(ibHome, ibCalendar, ibInsert, ibChart, ibSetting, view);
        });

        ibInsert.setOnClickListener(view -> {
            showFragment(new InsertFragment());
            resetButtonStyles(ibHome, ibCalendar, ibInsert, ibChart, ibSetting, view);
        });

        ibChart.setOnClickListener(view -> {
            showFragment(new ChartFragment());
            resetButtonStyles(ibHome, ibCalendar, ibInsert, ibChart, ibSetting, view);
        });

        ibSetting.setOnClickListener(view -> {
            // Tạo đối tượng SettingFragment và truyền dữ liệu
            SettingFragment settingFragment = new SettingFragment();
            // Truyền dữ liệu vào Bundle
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("email", email);
            bundle.putSerializable("userData", userData);
            // Đặt Bundle vào Fragment
            settingFragment.setArguments(bundle);
            showFragment(settingFragment);
            resetButtonStyles(ibHome, ibCalendar, ibInsert, ibChart, ibSetting, view);
        });
    }

    // Phương thức hiển thị Fragment
    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null) // Thêm vào back stack để người dùng có thể quay lại
                .commit();
    }

    // Phương thức đặt lại trạng thái cho các nút khác
    private void resetButtonStyles(ImageButton ibHome, ImageButton ibCalendar, ImageButton ibInsert,
                                   ImageButton ibChart, ImageButton ibSetting, View selectedButton) {
        // Đặt lại cho các nút khác
        ibCalendar.setBackgroundColor(ContextCompat.getColor(this, R.color.my_light_primary));
        ibCalendar.setImageResource(R.drawable.calendar_with_size); // Thay hình ảnh mặc định
        ibInsert.setBackgroundColor(ContextCompat.getColor(this, R.color.my_light_primary));
        ibInsert.setImageResource(R.drawable.insert_with_size); // Thay hình ảnh mặc định
        ibChart.setBackgroundColor(ContextCompat.getColor(this, R.color.my_light_primary));
        ibChart.setImageResource(R.drawable.chart_with_size); // Thay hình ảnh mặc định
        ibSetting.setBackgroundColor(ContextCompat.getColor(this, R.color.my_light_primary));
        ibSetting.setImageResource(R.drawable.setting_with_size); // Thay hình ảnh mặc định

        // Đặt lại cho nút Home chỉ khi nó không phải là nút đã chọn
        if (selectedButton != ibHome) {
            ibHome.setBackgroundColor(ContextCompat.getColor(this, R.color.my_light_primary)); // Màu nền mặc định
            ibHome.setImageResource(R.drawable.home_with_size); // Hình ảnh mặc định
        }

        // Đặt nền và hình ảnh cho nút đã chọn
        selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white)); // Nền trắng cho nút đã chọn
        if (selectedButton == ibCalendar) {
            ibCalendar.setImageResource(R.drawable.calendar1_with_size); // Hình ảnh cho Calendar
        } else if (selectedButton == ibInsert) {
            ibInsert.setImageResource(R.drawable.insert1_with_size); // Hình ảnh cho Insert
        } else if (selectedButton == ibChart) {
            ibChart.setImageResource(R.drawable.chart1_with_size); // Hình ảnh cho Chart
        } else if (selectedButton == ibSetting) {
            ibSetting.setImageResource(R.drawable.setting1_with_size); // Hình ảnh cho Setting
        } else if (selectedButton == ibHome) {
            ibHome.setImageResource(R.drawable.home1_with_size); // Hình ảnh cho Home
        }
    }
}
