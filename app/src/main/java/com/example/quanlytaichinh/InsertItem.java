package com.example.quanlytaichinh;

public class InsertItem {
    private int imageResId;
    private String title;

    public InsertItem(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }
}
