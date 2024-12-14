package com.example.quanlytaichinh.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytaichinh.CustomSpinnerAdapter;
import com.example.quanlytaichinh.IconCategoryAdapter;
import com.example.quanlytaichinh.IconCategoryItem;
import com.example.quanlytaichinh.R;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {
    Spinner spinner;
    GridView gridview;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        initVariable();

        // Spinner chọn expense hay income
        String[] typeOptions = {"Expense", "Income"};
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, typeOptions);
        spinner.setSelection(0);
        spinner.setAdapter(adapter);

        // Tạo danh sách các item setting
        List<IconCategoryItem> iconCategoryItems = new ArrayList<>();
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_investment));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_food));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_salary));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_edu));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_housing_expenses));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_bonus));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_cloths));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_contact_fee));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_maintenance));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_marketing));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_project_payment));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_medical));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_sales));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_transport));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_travel));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_cosmetic));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_personnel));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_office_supplies));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_utilities));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_project));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon1));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon2));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon3));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon4));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon5));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon6));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon7));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon8));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon9));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon10));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon11));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon12));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon13));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon14));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon15));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon16));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon17));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon18));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon19));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon20));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon21));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon22));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon23));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon24));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon25));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon26));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon27));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon28));
        iconCategoryItems.add(new IconCategoryItem(R.drawable.ic_icon29));


        // Tạo adapter và thiết lập cho ListView
        IconCategoryAdapter IconAdapter = new IconCategoryAdapter(this, iconCategoryItems);
        gridview.setAdapter(IconAdapter);
    }
    private void initVariable(){
        spinner = findViewById(R.id.spinner);
        gridview = findViewById(R.id.grid_view);
    }
}