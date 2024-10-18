package com.example.quanlytaichinh;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.text.SimpleDateFormat;


public class CalendarFragment extends Fragment {


    CalendarView calendarView;
    ImageButton ibInsert;
    TextView tvShowDay;

    // Biến lưu trữ ngày đã chọn
    private int selectedYear = -1;
    private int selectedMonth = -1;
    private int selectedDay = -1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.calendar_layout, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        ibInsert = view.findViewById(R.id.ib_insert);
        tvShowDay = view.findViewById(R.id.tv_show_day);

        // Lấy ngày hiện tại khi mở app lên
        Calendar currentDate = Calendar.getInstance();
        selectedYear = currentDate.get(Calendar.YEAR);
        selectedMonth = currentDate.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0 nên cần cộng 1
        selectedDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // Hiển thị ngày mặc định lên TextView
        tvShowDay.setText("Ngày  " + selectedDay + " Tháng " + selectedMonth + " Năm " + selectedYear);

        // Thiết lập sự kiện khi nhấn vào ngày trong CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Lưu lại ngày đã chọn
                selectedYear = year;
                selectedMonth = month + 1;  // Tháng bắt đầu từ 0, nên cần cộng 1
                selectedDay = dayOfMonth;

                // Hiển thị ngày đã chọn lên TextView
                tvShowDay.setText("Ngày " + selectedDay + " Tháng " + selectedMonth + " Năm " + selectedYear);
            }
        });


        // Thiết lập sự kiện khi nhấn vào ImageButton
        ibInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem người dùng đã chọn ngày hay chưa
                if (selectedYear != -1 && selectedMonth != -1 && selectedDay != -1) {
                    // Tạo Bundle để truyền dữ liệu ngày đã chọn sang InsertFragment
                    Bundle bundle = new Bundle();
                    bundle.putInt("selectedYear", selectedYear);
                    bundle.putInt("selectedMonth", selectedMonth);
                    bundle.putInt("selectedDay", selectedDay);


                    // Chuyển đến AccountFragment
                    InsertFragment insertFragment = new InsertFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, insertFragment); // Thay thế fragment tại container
                    fragmentTransaction.addToBackStack(null); // Thêm vào back stack
                    fragmentTransaction.commit();
                } else {
                    // Nếu chưa chọn ngày, hiển thị thông báo
                    Toast.makeText(getActivity(), "Vui lòng chọn một ngày trước khi thêm!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
