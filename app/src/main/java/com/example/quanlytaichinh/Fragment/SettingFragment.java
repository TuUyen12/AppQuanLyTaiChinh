package com.example.quanlytaichinh.Fragment;

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

import com.example.quanlytaichinh.Activity.FeedbackActivity;
import com.example.quanlytaichinh.Activity.GuidingInformationActivity;
import com.example.quanlytaichinh.Activity.TimeSettingActivity;
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
import com.example.quanlytaichinh.SettingAdapter;
import com.example.quanlytaichinh.SettingItem;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private DTBase.User authUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setting_layout, container, false);

        RelativeLayout accountLayout = view.findViewById(R.id.account_layout);

        // Thiết lập sự kiện click cho RelativeLayout
        accountLayout.setOnClickListener(v -> {
            AccountFragment accountFragment = new AccountFragment();

            // Truyền authUser qua Bundle
            Bundle bundle = new Bundle();
            if (authUser != null) {
                bundle.putSerializable("User", authUser);
            }
            accountFragment.setArguments(bundle);

            // Thay thế Fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, accountFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            authUser = (DTBase.User) bundle.getSerializable("User"); // Ép kiểu về User

            if (authUser != null) {
                TextView tv_account = view.findViewById(R.id.tv_account);
                tv_account.setText(authUser.getUserMail()); // Hiển thị email
            }
        }


        ListView lvSetting = view.findViewById(R.id.lv_setting);

        // Tạo danh sách các item setting
        List<SettingItem> settingItems = new ArrayList<>();
        settingItems.add(new SettingItem("Time Setting", R.drawable.time_with_size));
        settingItems.add(new SettingItem("Guiding and Information", R.drawable.guide_with_size));
        settingItems.add(new SettingItem("Feedback", R.drawable.feedback_with_size));

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

