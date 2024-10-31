package com.example.quanlytaichinh;

public class FinancialRecordData {
    private String id; // ID của bản ghi
    private String type; // "income" hoặc "expense"
    private double amount; // Số tiền
    private String category; // Danh mục (ví dụ: "Food", "Transport")
    private String date; // Ngày thêm vào
    private String accountType; // "personal" hoặc "business"

    // Constructor
    public FinancialRecordData() {}

    // Getter and setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

}
