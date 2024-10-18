package com.example.quanlytaichinh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class InsertFragment extends Fragment {

    GridView gridView;
    TextView tvShowDay;
    ArrayList<InsertItem> insertItems;
    InsertAdapter insertAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insert_layout, container, false);

        // Tham chiếu đến GridView và TextView
        gridView = view.findViewById(R.id.grid_view);  // Thêm dòng này
        tvShowDay = view.findViewById(R.id.tv_show_day);

        // Lấy Bundle từ CalendarFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            int year = bundle.getInt("selectedYear");
            int month = bundle.getInt("selectedMonth");
            int day = bundle.getInt("selectedDay");

            // Hiển thị ngày đã chọn lên TextView
            tvShowDay.setText("Ngày " + day + " Tháng " + month + " Năm " + year);
        }

        // Tạo danh sách các item hiển thị trong GridView
        insertItems = new ArrayList<>();
        insertItems.add(new InsertItem(R.drawable.ic_shopping, "Shopping"));
        insertItems.add(new InsertItem(R.drawable.ic_food, "Food"));
        insertItems.add(new InsertItem(R.drawable.ic_edu, "Education"));
        insertItems.add(new InsertItem(R.drawable.ic_clothing, "Clothing"));
        // Thêm các mục khác nếu cần...

        // Khởi tạo adapter và gán vào GridView
        insertAdapter = new InsertAdapter(getActivity(), insertItems);
        gridView.setAdapter(insertAdapter);  // Đảm bảo gridView đã được ánh xạ

        return view;
    }
}
