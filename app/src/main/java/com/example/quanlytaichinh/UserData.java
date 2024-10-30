package com.example.quanlytaichinh;


public class UserData {
    public String username;
    public String email;


    public UserData(String username, String email){
        this.email = email;
        this.username = username;
    }


    // Hàm get _ Lấy dữ liệu
    public String getUsername() {
        return username;
    }
    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
