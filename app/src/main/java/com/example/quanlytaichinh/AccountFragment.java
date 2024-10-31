package com.example.quanlytaichinh;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private Button btn_edit;
    private Button btn_change_password;
    private Button btn_sign_out;
    private FirebaseAuth mAuth;// Thêm biến FirebaseAuth
    private UserData user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.account_layout, container, false);
        btn_edit = view.findViewById(R.id.btn_edit);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        btn_sign_out = view.findViewById(R.id.btn_sign_out);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (UserData) bundle.getSerializable("userData"); // Nhận đối tượng UserData
            if (user != null) {
                // Hiển thị thông tin người dùng nếu cần
                TextView tv_email = view.findViewById(R.id.tv_id);
                tv_email.setText(user.getId());
            }
        }

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InfoAccountActivity.class);
                if (user != null) {
                    intent.putExtra("userData", user); // Truyền đối tượng UserData
                }
                startActivity(intent);
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        return view;
    }


    private void signOut() {
        mAuth.signOut(); // Đăng xuất người dùng
        // Chuyển hướng về màn hình đăng nhập
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish(); // Kết thúc Activity hiện tại
    }
}
