package com.example.appnhathuoc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appnhathuoc.utils.BottomNavigationHelper;
import com.example.appnhathuoc.utils.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvPhone;
    private LinearLayout btnPersonalInfo, btnMyOrders, btnStores, btnNotifs, btnLogout;
    private BottomNavigationView bottomNavigationView;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userManager = UserManager.getInstance(this);

        initViews();
        bindUserData();
        setupMenuListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_profile_name);
        tvPhone = findViewById(R.id.tv_profile_phone);
        btnPersonalInfo = findViewById(R.id.btn_menu_personal_info);
        btnMyOrders = findViewById(R.id.btn_menu_my_orders);
        btnStores = findViewById(R.id.btn_menu_stores);
        btnNotifs = findViewById(R.id.btn_menu_notifications);
        btnLogout = findViewById(R.id.btn_menu_logout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void bindUserData() {
        if (userManager.isLoggedIn()) {
            tvName.setText(userManager.getUserName());
            tvPhone.setText(userManager.getUserPhone().isEmpty() ? "Đã đăng nhập" : userManager.getUserPhone());
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvName.setText("Chưa đăng nhập tài khoản");
            tvPhone.setText("👉 Nhấn vào đây để đăng nhập / đăng ký");
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void setupMenuListeners() {
        tvName.setOnClickListener(v -> {
            if (!userManager.isLoggedIn()) {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        tvPhone.setOnClickListener(v -> {
            if (!userManager.isLoggedIn()) {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        btnPersonalInfo.setOnClickListener(v -> {
            if (!userManager.isLoggedIn()) {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                return;
            }
            Toast.makeText(ProfileActivity.this, "Họ và tên: " + userManager.getUserName() + "\nSĐT: " + userManager.getUserPhone(), Toast.LENGTH_LONG).show();
        });

        btnMyOrders.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, OrdersActivity.class)));

        btnStores.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, StoreActivity.class)));

        btnNotifs.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, NotificationActivity.class)));

        btnLogout.setOnClickListener(v -> new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    userManager.logout();
                    Toast.makeText(ProfileActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show());
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setup(this, bottomNavigationView, R.id.navigation_profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindUserData();
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }
}