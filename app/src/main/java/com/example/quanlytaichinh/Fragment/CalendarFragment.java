package com.example.quanlytaichinh.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.DatePicker;
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
import com.example.quanlytaichinh.DataBase.DTBase;
import com.example.quanlytaichinh.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;
    ImageButton ibInsert;
    TextView tvShowDay;
    ListView lvShowInsert;
    DTBase.User authUser;

    // Biến lưu trữ ngày đã chọn
    private int selectedYear = -1;
    private int selectedMonth = -1;
    private int selectedDay = -1;
    private boolean isPersonal;
    private String categoryJson;
    private String financialJson;

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

        // Khởi tạo SharedPreferences để lấy dữ liệu isPersonal
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isPersonal = sharedPreferences.getBoolean("isPersonal", false);

        // Khởi tạo SharedPreferences để lấy dữ liệu category
        SharedPreferences categorySharedPreferences = getActivity().getSharedPreferences("MyCategory", MODE_PRIVATE);
        categoryJson = categorySharedPreferences.getString("category", "[]"); // Mặc định là mảng rỗng nếu không có dữ liệu ([])

        // Khởi tạo SharedPreferences để lấy dữ liệu financial
        SharedPreferences financialSharedPreferences = getActivity().getSharedPreferences("MyFinancials", MODE_PRIVATE);
        financialJson = financialSharedPreferences.getString("financialList", "[]"); // Mặc định là mảng rỗng nếu không có dữ liệu ([])

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

        // Lấy ngày hiện tại khi mở app lên
        Calendar currentDate = Calendar.getInstance();
        selectedYear = currentDate.get(Calendar.YEAR);
        selectedMonth = currentDate.get(Calendar.MONTH);
        selectedDay = currentDate.get(Calendar.DAY_OF_MONTH);

        List<DTBase.Financial> calendarItems = getFinancialByDate(currentDate.getTime());
        // Thiết lập adapter cho ListView
        CalendarAdapter adapter = new CalendarAdapter(getContext(), calendarItems);
        lvShowInsert.setAdapter(adapter);

        // Hiển thị ngày mặc định lên TextView
        updateDisplayedDate(selectedDay, selectedMonth + 1, selectedYear);

        // Thiết lập sự kiện khi chọn ngày trong CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;

                // Hiển thị ngày đã chọn lên TextView
                updateDisplayedDate(selectedDay, selectedMonth + 1, selectedYear);

                // Tạo ngày từ các tham số đã chọn
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(selectedYear, selectedMonth, selectedDay);

                // Lấy các mục tài chính theo ngày đã chọn
                List<DTBase.Financial> calendarItems = getFinancialByDate(selectedDate.getTime());

                // Thiết lập adapter cho ListView
                CalendarAdapter adapter = new CalendarAdapter(getContext(), calendarItems);
                lvShowInsert.setAdapter(adapter);
            }
        });
        // Thiết lập sự kiện khi nhấn vào TextView để chọn ngày
        tvShowDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị DatePickerDialog để người dùng chọn ngày
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Cập nhật lại các giá trị ngày, tháng, năm
                                selectedYear = year;
                                selectedMonth = monthOfYear;
                                selectedDay = dayOfMonth;

                                // Cập nhật lại ngày trên TextView
                                updateDisplayedDate(selectedDay, selectedMonth + 1, selectedYear);

                                // Tạo ngày từ các tham số đã chọn
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(selectedYear, selectedMonth, selectedDay);

                                // Lấy các mục tài chính theo ngày đã chọn
                                List<DTBase.Financial> calendarItems = getFinancialByDate(selectedDate.getTime());

                                // Thiết lập adapter cho ListView
                                CalendarAdapter adapter = new CalendarAdapter(getContext(), calendarItems);
                                lvShowInsert.setAdapter(adapter);

                                // Cập nhật ngày đã chọn lên CalendarView
                                calendarView.setDate(selectedDate.getTimeInMillis(), true, true);
                            }
                        },
                        selectedYear, // Năm hiện tại
                        selectedMonth, // Tháng hiện tại
                        selectedDay // Ngày hiện tại
                );
                // Hiển thị dialog
                datePickerDialog.show();
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
        // Sự kiện khi nhấn giữ vào một mục trong ListView
        lvShowInsert.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowChooseEdit_Delete();
                return false;
            }
        });

        return view;
    }
    private ArrayList<DTBase.Financial> getFinancialByDate(Date date) {
        ArrayList<DTBase.Financial> calendarItems = new ArrayList<>();
        Gson gson = new Gson();

        if (financialJson != null) {
            // Chuyển đổi financialJson thành danh sách các đối tượng Financial
            Type listType = new TypeToken<List<DTBase.Financial>>() {}.getType();
            List<DTBase.Financial> financialList = gson.fromJson(financialJson, listType);

            // Chuyển đổi ngày được truyền vào thành Calendar
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTime(date);

            // Duyệt qua danh sách và lọc theo ngày
            for (DTBase.Financial financial1 : financialList) {
                String financialDateString = financial1.getFinancialDate(); // Chuỗi ngày tháng, ví dụ: "dd/MM/yyyy"
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date financialDate = null;
                try {
                    financialDate = dateFormat.parse(financialDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (financialDate != null) {
                    // Chuyển đổi financialDate thành Calendar
                    Calendar financialCalendar = Calendar.getInstance();
                    financialCalendar.setTime(financialDate);

                    if (isPersonal) {
                        // Trường hợp isPersonal = true, kiểm tra categoryID < 20
                        if (financial1.getCategoryID() < 20
                                && selectedCalendar.get(Calendar.YEAR) == financialCalendar.get(Calendar.YEAR)
                                && selectedCalendar.get(Calendar.MONTH) == financialCalendar.get(Calendar.MONTH)
                                && selectedCalendar.get(Calendar.DAY_OF_MONTH) == financialCalendar.get(Calendar.DAY_OF_MONTH)) {
                            calendarItems.add(financial1);
                        }
                    } else {
                        // Trường hợp isPersonal = false, kiểm tra categoryID >= 20
                        if (financial1.getCategoryID() >= 20
                                && selectedCalendar.get(Calendar.YEAR) == financialCalendar.get(Calendar.YEAR)
                                && selectedCalendar.get(Calendar.MONTH) == financialCalendar.get(Calendar.MONTH)
                                && selectedCalendar.get(Calendar.DAY_OF_MONTH) == financialCalendar.get(Calendar.DAY_OF_MONTH)) {
                            calendarItems.add(financial1);
                        }
                    }
                }
            }
        }

        return calendarItems;
    }



    // Hàm cập nhật ngày được hiển thị trên TextView
    private void updateDisplayedDate(int day, int month, int year) {
        String displayedDate = day + "/" + month + "/" + year;
        tvShowDay.setText(displayedDate);
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
