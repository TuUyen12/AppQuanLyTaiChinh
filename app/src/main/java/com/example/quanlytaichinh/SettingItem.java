package com.example.quanlytaichinh;

public class SettingItem {
    private String name;
    private int imageResId;

    public SettingItem(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}