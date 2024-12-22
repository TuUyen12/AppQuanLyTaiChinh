package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.example.quanlytaichinh.Activity.AddCategoryActivity;
import com.example.quanlytaichinh.Activity.EditCategoryActivity;
import com.example.quanlytaichinh.Activity.FeedbackActivity;
import com.example.quanlytaichinh.Activity.GuidingInformationActivity;
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

        View view = inflater.inflate(R.layout.setting_layout, container, false);

        authUser = getUserFromSharedPreferences();
        if (authUser != null) {
            TextView tv_account = view.findViewById(R.id.tv_account);
            tv_account.setText(authUser.getUserMail()); // Hiển thị email
            userId = authUser.getUserID();

        }
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


        ListView lvSetting = view.findViewById(R.id.lv_setting);

        // Tạo danh sách các item setting
        List<SettingItem> settingItems = new ArrayList<>();
        settingItems.add(new SettingItem("Guiding and Information", R.drawable.guide_with_size));
        settingItems.add(new SettingItem("Feedback", R.drawable.feedback_with_size));
        settingItems.add(new SettingItem("Account Type", R.drawable.exchange_with_size));
        settingItems.add(new SettingItem("Add Category", R.drawable.add1));
        settingItems.add(new SettingItem("Edit Category", R.drawable.edit));

        // Tạo adapter và thiết lập cho ListView
        SettingAdapter adapter = new SettingAdapter(getContext(), settingItems);
        lvSetting.setAdapter(adapter);

        lvSetting.setOnItemClickListener((parent, view1, position, id) -> {
            // Xử lý sự kiện khi item được chọn
            Intent intent;
            switch (position) {
                case 0: // Time Setting
                    intent = new Intent(getActivity(), GuidingInformationActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(getActivity(), FeedbackActivity.class);
                    // Truyền authUser qua Bundle
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("User", authUser);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    break;
                case 2:
                    // Khởi tạo SharedPreferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    // Lấy giá trị của `isPersonal`, với giá trị mặc định là `false` nếu biến chưa được lưu
                    boolean isPersonal = sharedPreferences.getBoolean("isPersonal", false);
                    // Hiển thị dialog chọn loại tài khoản
                    showAccountTypeDialog(isPersonal);
                    break;
                case 3:
                    intent = new Intent(getActivity(), AddCategoryActivity.class);
                    // Truyền authUser qua Bundle
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("User", authUser);
                    intent.putExtras(bundle2);
                    startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(getActivity(), EditCategoryActivity.class);
                    // Truyền authUser qua Bundle
                    Bundle bundle3 = new Bundle();
                    bundle3.putSerializable("User", authUser);
                    intent.putExtras(bundle3);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        });

        return view;
    }
    private DTBase.User getUserFromSharedPreferences() {
        // Lấy SharedPreferences
        SharedPreferences userSharedPreferences = getActivity().getSharedPreferences("MyUser", MODE_PRIVATE);

        // Lấy chuỗi JSON từ SharedPreferences
        String userJson = userSharedPreferences.getString("userJson", null);

        // Nếu JSON không null, chuyển đổi thành đối tượng User
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, DTBase.User.class);
        }

        return null; // Nếu không tìm thấy JSON, trả về null
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
                                if(category.getCategoryID() < 201) expense.add(category);
                            } else if ("income".equals(category.getCategoryType())) {
                                if(category.getCategoryID() < 201) income.add(category);
                            }
                        }else{
                            if ("expense".equals(category.getCategoryType())) {
                                if(category.getCategoryID() >= 201) expense.add(category);
                            }
                            else if ("income".equals(category.getCategoryType())) {
                                if(category.getCategoryID() >= 201) income.add(category);
                            }
                        }
                    }
                }

                // Chuyển mảng expense và income thành chuỗi JSON
                Gson gson = new Gson();
                String expenseJson = gson.toJson(expense);
                String incomeJson = gson.toJson(income);
                String categoryJson = gson.toJson(Category);

                if (getActivity() != null) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyCategory", MODE_PRIVATE);
                    SharedPreferences.Editor categoryEditor = sharedPreferences.edit();
                    categoryEditor.putString("expense", expenseJson);
                    categoryEditor.putString("income", incomeJson);
                    categoryEditor.putString("category", categoryJson);
                    categoryEditor.apply();
                } else {
                    // Handle case when getActivity() returns null
                    Log.e("SharedPreferences", "getActivity() returned null");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi khi truy cập Firebase
                System.out.println("Error fetching categories: " + error.getMessage());
            }
        });
    }

}

