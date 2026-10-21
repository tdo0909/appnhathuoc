package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appnhathuoc.api.ApiClient;
import com.example.appnhathuoc.utils.UserManager;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etName, etPhone, etEmail, etPassword;
    private Button btnSubmit;
    private TextView btnLoginLink;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        BottomNavigationHelper.attach(this, R.id.navigation_profile);

        userManager = UserManager.getInstance(this);

        btnBack = findViewById(R.id.btn_back_register);
        etName = findViewById(R.id.et_reg_name);
        etPhone = findViewById(R.id.et_reg_phone);
        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        btnSubmit = findViewById(R.id.btn_reg_submit);
        btnLoginLink = findViewById(R.id.btn_login_link);

        btnBack.setOnClickListener(v -> finish());

        btnLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        btnSubmit.setOnClickListener(v -> processRegister());
    }

    private void processRegister() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập họ và tên");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu cần tối thiểu 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        ApiClient.register(name, phone, email, password, new ApiClient.ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                userManager.login(name, phone, email);
                Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công! Đã lưu lên Firebase Cloud", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}