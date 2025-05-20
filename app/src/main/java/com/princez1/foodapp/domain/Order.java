package com.princez1.foodapp.domain;

import java.util.ArrayList;
import java.util.HashMap; // Để lưu trữ danh sách sản phẩm dưới dạng Map

public class Order {
    private String orderId;
    private String userId; // ID của người dùng đặt hàng
    private String customerName;
    private String address;
    private String phone;
    private String email;
    private String note;
    private ArrayList<Foods> items; // Danh sách các món ăn trong đơn hàng
    private double totalAmount;
    private long orderDateTimestamp; // Ngày đặt hàng dưới dạng timestamp
    private String status; // Trạng thái đơn hàng: Pending, Processing, Shipped, Delivered, Cancelled

    public Order() {
        // Constructor rỗng cần thiết cho Firebase
    }

    public Order(String orderId, String userId, String customerName, String address, String phone, String email, String note, ArrayList<Foods> items, double totalAmount, long orderDateTimestamp, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.note = note;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDateTimestamp = orderDateTimestamp;
        this.status = status;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<Foods> getItems() {
        return items;
    }

    public void setItems(ArrayList<Foods> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getOrderDateTimestamp() {
        return orderDateTimestamp;
    }

    public void setOrderDateTimestamp(long orderDateTimestamp) {
        this.orderDateTimestamp = orderDateTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}