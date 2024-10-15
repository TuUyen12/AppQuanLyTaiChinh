package com.example.quanlytaichinh;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.account_layout, container, false);
        Button btn_edit = view.findViewById(R.id.btn_edit);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sử dụng getActivity() để lấy ngữ cảnh và mở Activity mới
                Intent intent = new Intent(getActivity(), InfoAccountActivity.class);
                startActivity(intent); // Sửa lại tên phương thức thành startActivity
            }
        });
        return view;
    }
}