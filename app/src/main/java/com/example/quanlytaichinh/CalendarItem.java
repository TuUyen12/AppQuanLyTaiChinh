package com.example.quanlytaichinh;

public class CalendarItem {
    private String title; // Tên danh mục
    private String categoryType; // Loại danh mục
    private double money; // Số tiền
    private int imageResId; // ID của hình ảnh

    // Constructor
    public CalendarItem(String title, String categoryType, double money, int imageResId) {
        this.title = title;
        this.categoryType = categoryType;
        this.money = money;
        this.imageResId = imageResId;
    }

    // Getter cho title
    public String getTitle() {
        return title;
    }

    // Getter cho categoryType
    public String getCategoryType() {
        return categoryType;
    }

    // Getter cho money
    public double getMoney() {
        return money;
    }

    // Getter cho imageResId
    public int getImageResId() {
        return imageResId;
    }
}
