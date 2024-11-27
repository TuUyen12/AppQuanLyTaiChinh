package com.example.quanlytaichinh.DataBase;

public class InsertItem {
    private int id; // ID danh mục
    private int imageResId; // Hình ảnh đại diện
    private String title; // Tên danh mục
    private String categoryType; // Loại danh mục (ví dụ: "income", "expense")

    public InsertItem(int id, int imageResId, String title, String categoryType) {
        this.id = id;
        this.imageResId = imageResId;
        this.title = title;
        this.categoryType = categoryType;
    }

    public int getId() {
        return id;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategoryType() {
        return categoryType;
    }
}
