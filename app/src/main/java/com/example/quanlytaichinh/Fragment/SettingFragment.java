package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.quanlytaichinh.Activity.FeedbackActivity;
import com.example.quanlytaichinh.Activity.GeneralActivity;
import com.example.quanlytaichinh.Activity.GuidingInformationActivity;
import com.example.quanlytaichinh.Activity.SignInActivity;
import com.example.quanlytaichinh.Activity.TimeSettingActivity;
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
import com.example.quanlytaichinh.SettingAdapter;
import com.example.quanlytaichinh.SettingItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private DTBase.User authUser;

    ArrayList<DTBase.Category> Category; // Danh sách hiện tại
    ArrayList<DTBase.Category> expense;
    ArrayList<DTBase.Category> income;
    int userId;
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
                userId = authUser.getUserID();

            }
        }


        ListView lvSetting = view.findViewById(R.id.lv_setting);

        // Tạo danh sách các item setting
        List<SettingItem> settingItems = new ArrayList<>();
        settingItems.add(new SettingItem("Time Setting", R.drawable.time_with_size));
        settingItems.add(new SettingItem("Guiding and Information", R.drawable.guide_with_size));
        settingItems.add(new SettingItem("Feedback", R.drawable.feedback_with_size));
        settingItems.add(new SettingItem("Account Type", R.drawable.exchange_with_size));

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
                case 3:
                    // Khởi tạo SharedPreferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    // Lấy giá trị của `isPersonal`, với giá trị mặc định là `false` nếu biến chưa được lưu
                    boolean isPersonal = sharedPreferences.getBoolean("isPersonal", false);
                    // Hiển thị dialog chọn loại tài khoản
                    showAccountTypeDialog(isPersonal);
                    break;
                default:
                    break;
            }
        });

        return view;
    }
    private void showAccountTypeDialog(boolean isPersonal) {
        // Tạo danh sách các tùy chọn loại tài khoản
        String[] accountTypes = {"Personal", "Business"};

        // Xác định mục mặc định được chọn dựa vào biến isPersonal
        int checkedItem = isPersonal ? 0 : 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Account Type");
        builder.setSingleChoiceItems(accountTypes, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                boolean isPersonal;
                String selectedAccountType;
                // Lưu giá trị boolean vào SharedPreferences
                if (which == 0) {
                    editor.putBoolean("isPersonal", true); // Personal
                    isPersonal = true;
                    selectedAccountType = "Personal";
                } else {
                    editor.putBoolean("isPersonal", false); // Business
                    isPersonal = false;
                    selectedAccountType = "Business";
                }
                editor.apply();
                Toast.makeText(getContext(), "Account switched to " + selectedAccountType, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // Khởi tạo danh sách cho expense và income
                expense = new ArrayList<>();
                income = new ArrayList<>();
                Category = new ArrayList<>();
                // Gọi phương thức initCategory để lấy dữ liệu từ Firebase
                initCategory(userId, isPersonal);
            }
        });
        builder.create().show();
    }

    private void initCategory(int userId, boolean isPersonal) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("CATEGORIES").child(String.valueOf(userId));
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Duyệt qua các danh mục và phân loại chúng vào 2 mảng expense và income
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    DTBase.Category category = categorySnapshot.getValue(DTBase.Category.class);
                    if (category != null) {
                        Category.add(category);
                        if(isPersonal){
                            // Kiểm tra loại category là "expense" hay "income"
                            if ("expense".equals(category.getCategoryType())) {
                                if(category.getCategoryID() < 20) expense.add(category);
                            } else if ("income".equals(category.getCategoryType())) {
                                if(category.getCategoryID() < 20) income.add(category);
                            }
                        }else{
                            if ("expense".equals(category.getCategoryType())) {
                                if(category.getCategoryID() >= 20) expense.add(category);
                            }
                            else if ("income".equals(category.getCategoryType())) {
                                if(category.getCategoryID() >= 20) income.add(category);
                            }
                        }
                    }
                }

                // Chuyển mảng expense và income thành chuỗi JSON
                Gson gson = new Gson();
                String expenseJson = gson.toJson(expense);
                String incomeJson = gson.toJson(income);
                String categoryJson = gson.toJson(Category);


                // Lưu các chuỗi JSON vào SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyCategory", MODE_PRIVATE);
                SharedPreferences.Editor categoryEditor = sharedPreferences.edit();
                categoryEditor.putString("expense", expenseJson);  // Lưu mảng expense
                categoryEditor.putString("income", incomeJson);    // Lưu mảng income
                categoryEditor.putString("category", categoryJson);    // Lưu mảng category
                categoryEditor.apply();  // Lưu thay đổi



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi khi truy cập Firebase
                System.out.println("Error fetching categories: " + error.getMessage());
            }
        });
    }

}

