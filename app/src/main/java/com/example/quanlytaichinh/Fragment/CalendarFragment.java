package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
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

import com.example.quanlytaichinh.CalendarAdapter;
import com.example.quanlytaichinh.CalendarItem;
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    MaterialCalendarView calendarView;
    ImageButton ibInsert;
    TextView tvShowDay;
    ListView lvShowInsert;
    DTBase.User authUser;

    // Biến lưu trữ ngày đã chọn
    private int selectedYear = -1;
    private int selectedMonth = -1;
    private int selectedDay = -1;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Đặt ngôn ngữ ứng dụng
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getActivity().getResources().updateConfiguration(config, null);

        View view = inflater.inflate(R.layout.calendar_layout, container, false);

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            authUser = (DTBase.User) bundle.getSerializable("User"); // Ép kiểu về User
        }

        // Ánh xạ các thành phần giao diện
        calendarView = view.findViewById(R.id.calendarView);
        ibInsert = view.findViewById(R.id.ib_insert);
        tvShowDay = view.findViewById(R.id.tv_show_day);
        lvShowInsert = view.findViewById(R.id.lv_show_insert);

        // Khởi tạo SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Lấy giá trị của `isPersonal`
        boolean isPersonal = sharedPreferences.getBoolean("isPersonal", false);
        List<DTBase.Financial> calendarItems = new ArrayList<>();

        // Lấy dữ liệu tài chính từ SharedPreferences
        SharedPreferences financialSharedPreferences = getActivity().getSharedPreferences("MyFinancials", MODE_PRIVATE);
        String financialJson = financialSharedPreferences.getString("financialList", "[]"); // Mặc định là mảng rỗng nếu không có dữ liệu ([]);
        if (financialJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<DTBase.Financial>>() {}.getType();
            calendarItems = gson.fromJson(financialJson, type);
            for (DTBase.Financial financial : calendarItems) {
                System.out.println(financial.getFinancialID());
            }
        }

        // Thiết lập adapter cho ListView
        CalendarAdapter adapter = new CalendarAdapter(getContext(), calendarItems);
        lvShowInsert.setAdapter(adapter);

        // Sự kiện khi nhấn giữ vào một mục trong ListView
        lvShowInsert.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowChooseEdit_Delete();
                return false;
            }
        });

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

        return view;
    }

    // Hàm cập nhật ngày được hiển thị trên TextView
    private void updateDisplayedDate(int day, int month, int year) {
        String formattedDate = String.format(Locale.ENGLISH, "Date: %02d/%02d/%04d", day, month, year);
        tvShowDay.setText(formattedDate);
    }

    // Hàm xử lý sự kiện chọn Edit hoặc Delete
    private void ShowChooseEdit_Delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit or Delete");

        // Xử lý sự kiện khi người dùng chọn Edit
        builder.setPositiveButton("Edit", (dialog, which) -> {
            // Sự kiện sửa
        });

        // Xử lý sự kiện khi người dùng chọn Delete
        builder.setNegativeButton("Delete", (dialog, which) -> {
            // Sự kiện xóa
        });

        // Hiển thị dialog
        builder.create().show();
    }

}
