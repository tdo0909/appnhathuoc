package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appnhathuoc.utils.UserManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TextView btnForgotPassword, btnRegister;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BottomNavigationHelper.attach(this, R.id.navigation_profile);

        userManager = UserManager.getInstance(this);

        etPhone = findViewById(R.id.et_login_phone);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login_submit);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        btnRegister = findViewById(R.id.btn_register_link);

        btnLogin.setOnClickListener(v -> processLogin());

        btnForgotPassword.setOnClickListener(v -> Toast.makeText(this, "Liên hệ tổng đài 1800 6928 để hỗ trợ đặt lại mật khẩu", Toast.LENGTH_LONG).show());

        btnRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void processLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        com.example.appnhathuoc.api.ApiClient.login(phone, password, new com.example.appnhathuoc.api.ApiClient.ApiCallback<org.json.JSONObject>() {
            @Override
            public void onSuccess(org.json.JSONObject result) {
                org.json.JSONObject userObj = result.optJSONObject("user");
                String name = userObj != null ? userObj.optString("name", "Khách hàng") : "Khách hàng";
                userManager.login(name, phone);
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}