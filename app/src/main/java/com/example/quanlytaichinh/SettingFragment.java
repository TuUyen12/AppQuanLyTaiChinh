package com.example.quanlytaichinh;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến AccountFragment
                AccountFragment accountFragment = new AccountFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, accountFragment); // Thay thế fragment tại container
                fragmentTransaction.addToBackStack(null); // Thêm vào back stack
                fragmentTransaction.commit();
            }
        });

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String email = bundle.getString("email");

            // Nếu cần hiển thị email hoặc thông tin khác trong accountLayout, bạn có thể thêm TextView tại đây.
            // Ví dụ: giả sử bạn có một TextView trong accountLayout để hiển thị email.
            TextView tv_account = accountLayout.findViewById(R.id.tv_account);
            tv_account.setText(email);
        }

        ListView lvSetting = view.findViewById(R.id.lv_setting);

        // Tạo danh sách các item setting
        List<SettingItem> settingItems = new ArrayList<>();
        //Thêm các item ở đây
        settingItems.add(new SettingItem("Account", R.mipmap.ic_account1_foreground));
        settingItems.add(new SettingItem("Privacy", R.mipmap.ic_account1_foreground));

        // Tạo adapter và thiết lập cho ListView
        SettingAdapter adapter = new SettingAdapter(getContext(), settingItems);
        lvSetting.setAdapter(adapter);

        return view;
    }
}
