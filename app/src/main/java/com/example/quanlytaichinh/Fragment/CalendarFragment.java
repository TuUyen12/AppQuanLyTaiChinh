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
    private String financialJson;
    int userId;
    private List<DTBase.Financial> userFinancialList = new ArrayList<>();
    private Context context;
    private CalendarAdapter adapter;


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


        // Khởi tạo SharedPreferences để lấy dữ liệu financial
        SharedPreferences financialSharedPreferences = getActivity().getSharedPreferences("MyFinancials", MODE_PRIVATE);
        financialJson = financialSharedPreferences.getString("financialList", "[]"); // Mặc định là mảng rỗng nếu không có dữ liệu ([])

        View view = inflater.inflate(R.layout.calendar_layout, container, false);

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            authUser = (DTBase.User) bundle.getSerializable("User"); // Ép kiểu về User
            if(authUser != null){
                userId = authUser.getUserID();
            }else{
                // Gán giá trị mặc định nếu authUser là null
                userId = -1; // Hoặc giá trị hợp lệ tùy theo yêu cầu của bạn
            }
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
        CalendarAdapter adapter = new CalendarAdapter(getContext(), calendarItems);
        // Thiết lập adapter cho ListView
        if (calendarItems != null && !calendarItems.isEmpty()) {
            lvShowInsert.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "No financial data available for the selected date", Toast.LENGTH_SHORT).show();
        }

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
                    bundle.putSerializable("User", authUser);

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
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Lấy đối tượng Financial tại vị trí "position"
                DTBase.Financial financialItem = (DTBase.Financial) adapterView.getItemAtPosition(position);

                // Lấy financialID từ đối tượng Financial
                int financialID = financialItem.getFinancialID();
                int userID = financialItem.getUserID();
                // Gọi hàm xử lý Edit/Delete với financialID
                ShowDelete(userID, financialID, tvShowDay.getText().toString());

                return true; // Trả về true để sự kiện được xử lý và không tiếp tục với các hành động khác
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
                        // Trường hợp isPersonal = true, kiểm tra categoryID < 201
                        if (financial1.getCategoryID() < 201
                                && selectedCalendar.get(Calendar.YEAR) == financialCalendar.get(Calendar.YEAR)
                                && selectedCalendar.get(Calendar.MONTH) == financialCalendar.get(Calendar.MONTH)
                                && selectedCalendar.get(Calendar.DAY_OF_MONTH) == financialCalendar.get(Calendar.DAY_OF_MONTH)) {
                            calendarItems.add(financial1);
                        }
                    } else {
                        // Trường hợp isPersonal = false, kiểm tra categoryID >= 201
                        if (financial1.getCategoryID() >= 201
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

    // Hàm xử lý sự kiện chọn Delete
    private void ShowDelete(int userID, int financialID, String selectedDate) {
        if (userID <= 0 || financialID <= 0) {
            Toast.makeText(getActivity(), "Invalid data. Cannot delete." + userID + " " + financialID, Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "Do you want to delete?";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Tạo đối tượng DTBase để xóa dữ liệu
                    DTBase database = new DTBase();
                    database.deleteFinancial(userID, financialID);
                    // Sau khi xóa thành công, tải lại dữ liệu từ Firebase và cập nhật ListView
                    updateListView(selectedDate);
                    // Cập nhật SharedPreferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyFinancials", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Thông báo sau khi xóa thành công
                    Toast.makeText(getActivity(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Đóng dialog khi người dùng chọn "No"
                    dialog.dismiss();
                });

        builder.create().show();
    }

    private void updateListView(String selectedDate) { // Truyền ngày được chọn vào đây
        DTBase db = new DTBase();
        // Lấy dữ liệu tài chính từ Firebase
        db.fetchFinancialData(userId, new DTBase.FinancialCallback() {
            @Override
            public void onFinancialDataFetched(List<DTBase.Financial> financialList) {
                if (financialList != null) {
                    // Lọc dữ liệu theo ngày
                    List<DTBase.Financial> filteredList = new ArrayList<>();
                    for (DTBase.Financial financial : financialList) {
                        if (financial.getFinancialDate().equals(selectedDate)) { // So sánh ngày
                            filteredList.add(financial);
                        }
                    }

                    userFinancialList.clear();
                    userFinancialList.addAll(filteredList);

                    // Khi dữ liệu đã lọc xong, lưu vào SharedPreferences
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyFinancials", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(userFinancialList);
                    editor.putString("financialList", json);
                    editor.apply();

                    // Cập nhật ListView
                    if (adapter == null) {
                        adapter = new CalendarAdapter(getContext(), filteredList);
                        lvShowInsert.setAdapter(adapter);
                    } else {
                        adapter.setData(filteredList); // Cập nhật adapter
                    }
                } else {
                    Toast.makeText(getActivity(), "Error loading financial data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
