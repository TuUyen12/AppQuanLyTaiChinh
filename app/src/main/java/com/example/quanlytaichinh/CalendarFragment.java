package com.example.quanlytaichinh;

import android.content.res.Configuration;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        // Đặt ngôn ngữ ứng dụng
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getActivity().getResources().updateConfiguration(config, null);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.calendar_layout, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        ibInsert = view.findViewById(R.id.ib_insert);
        tvShowDay = view.findViewById(R.id.tv_show_day);

        // Lấy ngày hiện tại khi mở app lên
        Calendar currentDate = Calendar.getInstance();
        selectedYear = currentDate.get(Calendar.YEAR);
        selectedMonth = currentDate.get(Calendar.MONTH); // Tháng bắt đầu từ 0
        selectedDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // Hiển thị ngày mặc định lên TextView
        updateDisplayedDate(selectedDay, selectedMonth + 1, selectedYear); // Cộng 1 để hiển thị đúng tháng

        // Thiết lập sự kiện khi nhấn vào ngày trong CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Lưu lại ngày đã chọn
                selectedYear = year;
                selectedMonth = month;  // Tháng bắt đầu từ 0
                selectedDay = dayOfMonth;

                // Hiển thị ngày đã chọn lên TextView
                updateDisplayedDate(selectedDay, selectedMonth + 1, selectedYear); // Cộng 1 để hiển thị đúng tháng
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
                    bundle.putInt("selectedMonth", selectedMonth + 1); // Cộng 1 để truyền đúng tháng
                    bundle.putInt("selectedDay", selectedDay);

                    // Tạo InsertFragment và truyền Bundle
                    InsertFragment insertFragment = new InsertFragment();
                    insertFragment.setArguments(bundle); // Truyền Bundle vào InsertFragment

                    // Chuyển đến InsertFragment
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, insertFragment); // Thay thế fragment tại container
                    fragmentTransaction.addToBackStack(null); // Thêm vào back stack
                    fragmentTransaction.commit();
                } else {
                    // Nếu chưa chọn ngày, hiển thị thông báo
                    Toast.makeText(getActivity(), "Please select a date before adding!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void updateDisplayedDate(int day, int month, int year) {
        // Định dạng ngày thành dd/mm/yyyy
        String formattedDate = String.format(Locale.ENGLISH, "Date: %02d/%02d/%04d", day, month, year);
        tvShowDay.setText(formattedDate);
    }
}
