package com.example.quanlytaichinh;

import java.io.Serializable;

public class UserData implements Serializable {
    private String id; // ID duy nhất cho người dùng
    private String username;
    private String email;
    private String birthday;
    private String address;
    private String gender;

    // Constructor mặc định
    public UserData() {
    }

    // Constructor cho việc đăng ký, chỉ bao gồm thông tin cần thiết
    public UserData(String username, String email) {
        this.id = generateCustomId();
        this.username = username;
        this.email = email;
        this.birthday = ""; // Để trống lúc đăng ký
        this.address = ""; // Để trống lúc đăng ký
        this.gender = ""; // Để trống lúc đăng ký
    }

    // Hàm get - Lấy dữ liệu
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    // Hàm set - Cập nhật dữ liệu
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Hàm tạo ID bắt đầu với "2252"
    public static String generateCustomId() {
        long uniqueId = System.currentTimeMillis() % 1000000; // Đảm bảo ID duy nhất
        return "2252" + uniqueId;
    }
}
