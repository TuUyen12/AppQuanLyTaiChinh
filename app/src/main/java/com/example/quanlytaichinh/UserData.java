package com.example.quanlytaichinh;


public class UserData {
    public String username;
    public String password;
    public String email;


    public UserData(String username, String password, String email){
        this.email = email;
        this.password = password;
        this.username = username;
    }


    // Hàm get _ Lấy dữ liệu
    public String getUsername() {
        return username;
    }
    public String getEmail(){
        return email;
    }


    public String getPassword() {
        return password;
    }


    // Hàm set _ Chỉnh dữ liệu
    public void setPassword(String password) {
        this.password = password;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public void setUsername(String username) {
        this.username = username;
    }
}
