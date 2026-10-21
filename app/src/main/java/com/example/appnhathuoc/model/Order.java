package com.example.appnhathuoc.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String date;
    private List<CartItem> items;
    private double totalAmount;
    private String status; // "Chờ xác nhận", "Đang chuẩn bị", "Đang giao", "Đã giao", "Đã hủy"
    private String fullName;
    private String phone;
    private String address;
    private String paymentMethod;

    public Order() {}

    public Order(String orderId, String date, List<CartItem> items, double totalAmount,
                 String status, String fullName, String phone, String address, String paymentMethod) {
        this.orderId = orderId;
        this.date = date;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getItemCount() {
        int count = 0;
        if (items != null) {
            for (CartItem item : items) {
                count += item.getQuantity();
            }
        }
        return count;
    }
}