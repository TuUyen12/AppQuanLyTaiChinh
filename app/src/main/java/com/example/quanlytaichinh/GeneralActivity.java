package com.example.quanlytaichinh;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;

public class GeneralActivity extends AppCompatActivity {
    private boolean isHome = true; // Biến để theo dõi trạng thái của HomeFragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // Thêm các tab với icon cho mỗi mục
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home_with_size).setContentDescription("Home"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.calendar_with_size).setContentDescription("Calendar"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.insert_with_size).setContentDescription("Insert"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chart_with_size).setContentDescription("Chart"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting_with_size).setContentDescription("Setting"));

        // Hiển thị HomeFragment mặc định khi đăng nhập
        showFragment(new HomeFragment());

        // Nhận dữ liệu từ Intent
        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        UserData userData = (UserData) getIntent().getSerializableExtra("userData");

        // Lắng nghe sự kiện khi một tab được chọn
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;

                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new HomeFragment();
                        break;
                    case 1:
                        selectedFragment = new CalendarFragment();
                        break;
                    case 2:
                        selectedFragment = new InsertFragment();
                        break;
                    case 3:
                        selectedFragment = new ChartFragment();
                        break;
                    case 4:
                        // Tạo đối tượng SettingFragment và truyền dữ liệu
                        SettingFragment settingFragment = new SettingFragment();
                        // Truyền dữ liệu vào Bundle
                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        bundle.putString("email", email);
                        bundle.putSerializable("userData", userData);
                        // Đặt Bundle vào Fragment
                        settingFragment.setArguments(bundle);
                        selectedFragment = settingFragment;
                        break;
                }

                if (selectedFragment != null) {
                    showFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Không cần làm gì khi tab không được chọn
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Không cần làm gì khi tab được chọn lại
            }
        });
    }

    // Phương thức để hiển thị Fragment
    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}
