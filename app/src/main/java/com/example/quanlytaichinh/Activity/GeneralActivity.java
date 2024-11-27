package com.example.quanlytaichinh.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.quanlytaichinh.Fragment.CalendarFragment;
import com.example.quanlytaichinh.Fragment.ChartFragment;
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.Fragment.HomeFragment;
import com.example.quanlytaichinh.Fragment.InsertFragment;
import com.example.quanlytaichinh.R;
import com.example.quanlytaichinh.Fragment.SettingFragment;
import com.google.android.material.tabs.TabLayout;

public class GeneralActivity extends AppCompatActivity {
    private boolean isHome = true; // Biến để theo dõi trạng thái của HomeFragment
    private DTBase.User authUser; // Đối tượng User để lưu thông tin người dùng

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

        // Nhận từ Intent
        authUser = (DTBase.User) getIntent().getSerializableExtra("User");

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
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("User", authUser);
                        selectedFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, selectedFragment)
                                .commit();
                        break;
                    case 3:
                        selectedFragment = new ChartFragment();
                        break;
                    case 4:
                        // Tạo đối tượng SettingFragment và truyền dữ liệu
                        // Truyền vào Fragment
                        SettingFragment settingFragment = new SettingFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("User", authUser);
                        settingFragment.setArguments(bundle1);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, settingFragment)
                                .commit();
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
