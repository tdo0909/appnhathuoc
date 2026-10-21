package com.example.appnhathuoc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.adapter.OrderAdapter;
import com.example.appnhathuoc.model.CartItem;
import com.example.appnhathuoc.model.Order;
import com.example.appnhathuoc.utils.OrderManager;
import com.example.appnhathuoc.utils.BottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private ImageView btnBack;
    private android.widget.TextView btnRefresh;
    private RecyclerView rvOrders;
    private BottomNavigationView bottomNavigationView;
    private OrderAdapter adapter;
    private OrderManager orderManager;
    private DecimalFormat priceFormatter = new DecimalFormat("#,###đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        orderManager = OrderManager.getInstance(this);
        if (!com.example.appnhathuoc.utils.UserManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_orders);
        btnRefresh = findViewById(R.id.btn_refresh_orders);
        rvOrders = findViewById(R.id.rv_orders);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupRecyclerView() {
        String phone = com.example.appnhathuoc.utils.UserManager.getInstance(this).getUserPhone();
        com.example.appnhathuoc.api.ApiClient.fetchOrders(phone, new com.example.appnhathuoc.api.ApiClient.ApiCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> orders) {
                if (orders != null && !orders.isEmpty()) {
                    adapter = new OrderAdapter(orders, order -> showOrderDetailDialog(order));
                    rvOrders.setLayoutManager(new LinearLayoutManager(OrdersActivity.this));
                    rvOrders.setAdapter(adapter);
                } else {
                    showLocalOrders(phone);
                }
            }

            @Override
            public void onError(String errorMessage) {
                showLocalOrders(phone);
            }
        });
    }

    private void showLocalOrders(String phone) {
        List<Order> orders = new java.util.ArrayList<>();
        for (Order order : orderManager.getSavedOrders()) {
            if (phone != null && phone.equals(order.getPhone())) {
                orders.add(order);
            }
        }
        adapter = new OrderAdapter(orders, order -> showOrderDetailDialog(order));
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
        if (orders.isEmpty()) {
            android.widget.Toast.makeText(this, "Bạn chưa có đơn hàng nào.", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> setupRecyclerView());
    }

    private void showOrderDetailDialog(Order order) {
        StringBuilder itemsSb = new StringBuilder();
        if (order.getItems() != null) {
            for (CartItem item : order.getItems()) {
                if (item.getProduct() != null) {
                    itemsSb.append("• ").append(item.getProduct().getName())
                           .append(" (x").append(item.getQuantity()).append(")")
                           .append(" - ").append(priceFormatter.format(item.getTotalPrice()))
                           .append("\n");
                }
            }
        }

        String details = "Mã đơn hàng: " + order.getOrderId() + "\n" +
                "Ngày đặt: " + order.getDate() + "\n" +
                "Trạng thái: " + order.getStatus() + "\n\n" +
                "NGƯỜI NHẬN:\n" +
                "Họ tên: " + order.getFullName() + "\n" +
                "SĐT: " + order.getPhone() + "\n" +
                "Địa chỉ: " + order.getAddress() + "\n\n" +
                "SẢN PHẨM:\n" + itemsSb.toString() + "\n" +
                "Thanh toán: " + order.getPaymentMethod() + "\n" +
                "TỔNG CỘNG: " + priceFormatter.format(order.getTotalAmount());

        new AlertDialog.Builder(this)
                .setTitle("Chi tiết đơn hàng")
                .setMessage(details)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setup(this, bottomNavigationView, R.id.navigation_orders);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView();
        bottomNavigationView.setSelectedItemId(R.id.navigation_orders);
    }
}