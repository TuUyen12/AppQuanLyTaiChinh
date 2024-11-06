package com.example.quanlytaichinh;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    MaterialCalendarView calendarView;
    ImageButton ibInsert;
    TextView tvShowDay;
    ListView lvShowInsert;

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
        lvShowInsert = view.findViewById(R.id.lv_show_insert);

        // Khởi tạo SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Lấy giá trị của `isPersonnal`
        boolean isPersonnal = sharedPreferences.getBoolean("isPersonnal", false);
        List<CalendarItem> calendarItems = new ArrayList<>();
        if (isPersonnal) {
            calendarItems.add(new CalendarItem("Ăn sáng", "expense", 50000, R.drawable.ic_food));
            calendarItems.add(new CalendarItem("Lương tháng 10", "income", 6000000, R.drawable.ic_salary));
            calendarItems.add(new CalendarItem("Mỹ phẩm", "expense", 500000, R.drawable.ic_cosmetic));
            calendarItems.add(new CalendarItem("Ăn trưa", "expense", 40000, R.drawable.ic_food));
            calendarItems.add(new CalendarItem("Tài liệu", "expense", 20000, R.drawable.ic_edu));
        } else {
            calendarItems.add(new CalendarItem("Quảng cáo", "expense", 3000000, R.drawable.ic_marketing));
            calendarItems.add(new CalendarItem("Bảo trì", "expense", 4000000, R.drawable.ic_maintenance));
            calendarItems.add(new CalendarItem("Dự án", "expense", 9000000, R.drawable.ic_project));
        }

        // Lấy ngày hiện tại khi mở app lên
        Calendar currentDate = Calendar.getInstance();
        selectedYear = currentDate.get(Calendar.YEAR);
        selectedMonth = currentDate.get(Calendar.MONTH);
        selectedDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // Hiển thị ngày mặc định lên TextView
        updateDisplayedDate(selectedDay, selectedMonth + 1, selectedYear);

        // Thiết lập sự kiện khi chọn ngày trong MaterialCalendarView
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedYear = date.getYear();
                selectedMonth = date.getMonth();
                selectedDay = date.getDay();

                // Hiển thị ngày đã chọn lên TextView
                updateDisplayedDate(selectedDay, selectedMonth + 1, selectedYear);
            }
        });


        // Thiết lập sự kiện khi nhấn vào ImageButton
        ibInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedYear != -1 && selectedMonth != -1 && selectedDay != -1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("selectedYear", selectedYear);
                    bundle.putInt("selectedMonth", selectedMonth + 1);
                    bundle.putInt("selectedDay", selectedDay);

                    InsertFragment insertFragment = new InsertFragment();
                    insertFragment.setArguments(bundle);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, insertFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getActivity(), "Please select a date before adding!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập adapter cho ListView
        CalendarAdapter adapter = new CalendarAdapter(getContext(), calendarItems);
        lvShowInsert.setAdapter(adapter);

        lvShowInsert.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                ShowChooseEdit_Delete();
                return false;
            }
        });


        return view;
    }

    private void updateDisplayedDate(int day, int month, int year) {
        String formattedDate = String.format(Locale.ENGLISH, "Date: %02d/%02d/%04d", day, month, year);
        tvShowDay.setText(formattedDate);
    }
    private void ShowChooseEdit_Delete(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit or Delete");
        // Xử lý sự kiện khi người dùng chọn Edit

        builder.setPositiveButton("Edit", (dialog, which) -> {
            //Sự kiện sửa

        });
        // Xử lý sự kiện khi người dùng chọn Delete
        builder.setNegativeButton("Delete", (dialog, which) -> {

        });
        // Hiển thị dialog
        builder.create().show();
    }
}
