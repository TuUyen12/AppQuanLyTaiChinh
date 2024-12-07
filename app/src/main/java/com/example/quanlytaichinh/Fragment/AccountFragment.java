package com.example.quanlytaichinh.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import com.example.quanlytaichinh.Activity.ChangePasswordActivity;
import com.example.quanlytaichinh.Activity.InfoAccountActivity;
import com.example.quanlytaichinh.Activity.SignInActivity;
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private Button btn_edit;
    private Button btn_change_password;
    private Button btn_sign_out;
    private FirebaseAuth mAuth;// Thêm biến FirebaseAuth
    private DTBase.User authUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.account_layout, container, false);
        btn_edit = view.findViewById(R.id.btn_edit);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        btn_sign_out = view.findViewById(R.id.btn_sign_out);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Nhận Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            authUser = (DTBase.User) bundle.getSerializable("User");
            if (authUser != null) {

            }
        }

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InfoAccountActivity.class);
                if (authUser != null) {
                    intent.putExtra("User", authUser); // Truyền đối tượng UserData
                }
                startActivity(intent);
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);

                // Truyền authUser qua Bundle
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", authUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowChooseSignOut();
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
    private void ShowChooseSignOut(){
        String message = "Do you want to sign out?";
        // Hiển thị thông báo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sign out");
        builder.setMessage(message);
        // Xử lý sự kiện khi người dùng chọn Yes

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Xử lý đăng xuất
            signOut();
            Toast.makeText(getActivity(), "Signed out sucessfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
          // Xử lý sự kiện khi người dùng chọn No
        builder.setNegativeButton("No", (dialog, which) -> {
            // Không làm gì khi người dùng chọn No
        });
        // Hiển thị dialog
        builder.create().show();
    }
}
