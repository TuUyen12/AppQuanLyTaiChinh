package com.example.quanlytaichinh;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    private UserData user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setting_layout, container, false);

        RelativeLayout accountLayout = view.findViewById(R.id.account_layout);

        // Thiết lập sự kiện click cho RelativeLayout
        accountLayout.setOnClickListener(v -> {
            // Chuyển đến AccountFragment
            AccountFragment accountFragment = new AccountFragment();

            // Truyền dữ liệu vào Bundle
            Bundle bundle = new Bundle();
            if (user != null) {
                bundle.putSerializable("userData", user); // Chuyển đối tượng UserData
            }
            accountFragment.setArguments(bundle); // Gán Bundle vào Fragment

            // Thay thế Fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, accountFragment) // Đảm bảo bạn đang thay thế bằng Fragment
                    .addToBackStack(null)
                    .commit();
        });


        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (UserData) bundle.getSerializable("userData"); // Nhận đối tượng UserData
            if (user != null) {
                TextView tv_account = accountLayout.findViewById(R.id.tv_account);
                tv_account.setText(user.getEmail()); // Hiển thị email từ UserData
            }
        }

        ListView lvSetting = view.findViewById(R.id.lv_setting);

        // Tạo danh sách các item setting
        List<SettingItem> settingItems = new ArrayList<>();
        settingItems.add(new SettingItem("Time Setting", R.mipmap.ic_time_setting_foreground));
        settingItems.add(new SettingItem("Guiding and Information", R.mipmap.ic_guide_and_info_foreground));
        settingItems.add(new SettingItem("Feedback", R.mipmap.ic_feedback_foreground));

        // Tạo adapter và thiết lập cho ListView
        SettingAdapter adapter = new SettingAdapter(getContext(), settingItems);
        lvSetting.setAdapter(adapter);

        lvSetting.setOnItemClickListener((parent, view1, position, id) -> {
            // Xử lý sự kiện khi item được chọn
            Intent intent;
            switch (position) {
                case 0: // Time Setting
                    intent = new Intent(getActivity(), TimeSettingActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(getActivity(), GuidingInformationActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(getActivity(), FeedbackActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        });

        return view;
    }
}

