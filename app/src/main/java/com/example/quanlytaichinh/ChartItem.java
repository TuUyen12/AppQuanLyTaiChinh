package com.example.quanlytaichinh;

public class ChartItem {
    private String title; // Tên danh mục
    private String categoryType; // Loại danh mục
    private String financialName;
    private double money; // Số tiền
    private int imageResId; // ID của hình ảnh

    // Constructor
    public ChartItem(String title, String categoryType, String financialName, double money, int imageResId) {
        this.title = title;
        this.categoryType = categoryType;
        this.financialName = financialName;
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
    // Getter cho financialName
    public String getFinancialName() {
        return financialName;
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