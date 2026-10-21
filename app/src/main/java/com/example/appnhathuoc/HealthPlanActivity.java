package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class HealthPlanActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etAge, etGender, etHeight, etWeight, etGoal;
    private Button btnGenerate;
    private MaterialCardView cardResult;
    private TextView tvBmiResult, tvAiPlanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_plan);
        BottomNavigationHelper.attach(this, R.id.navigation_home);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_health_plan);
        etAge = findViewById(R.id.et_hp_age);
        etGender = findViewById(R.id.et_hp_gender);
        etHeight = findViewById(R.id.et_hp_height);
        etWeight = findViewById(R.id.et_hp_weight);
        etGoal = findViewById(R.id.et_hp_goal);
        btnGenerate = findViewById(R.id.btn_generate_health_plan);
        cardResult = findViewById(R.id.card_health_plan_result);
        tvBmiResult = findViewById(R.id.tv_hp_bmi_result);
        tvAiPlanResult = findViewById(R.id.tv_hp_ai_plan_result);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnGenerate.setOnClickListener(v -> generatePlan());
    }

    private void generatePlan() {
        String ageStr = etAge.getText().toString().trim();
        String genderStr = etGender.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String goalStr = etGoal.getText().toString().trim();

        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr)) {
            Toast.makeText(this, "Vui lòng nhập chiều cao và cân nặng", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = TextUtils.isEmpty(ageStr) ? 25 : Integer.parseInt(ageStr);
            double heightCm = Double.parseDouble(heightStr);
            double heightM = heightCm / 100.0;
            double weightKg = Double.parseDouble(weightStr);
            if (age < 1 || age > 120 || heightCm < 50 || heightCm > 260 || weightKg <= 0 || weightKg > 500) {
                Toast.makeText(this, "Vui lòng kiểm tra tuổi, chiều cao và cân nặng", Toast.LENGTH_SHORT).show();
                return;
            }
            double bmi = weightKg / (heightM * heightM);

            boolean isMale = !genderStr.equalsIgnoreCase("nữ") && !genderStr.equalsIgnoreCase("nu");

            // BMR Calculation (Mifflin-St Jeor)
            double bmr = isMale ? (10 * weightKg + 6.25 * (heightM * 100) - 5 * age + 5)
                                : (10 * weightKg + 6.25 * (heightM * 100) - 5 * age - 161);
            double tdee = bmr * 1.375; // Moderate Activity Level

            String bmiCategory;
            if (bmi < 18.5) {
                bmiCategory = "Thể trạng gầy (Cần tăng cân & tăng cơ)";
            } else if (bmi < 23.0) {
                bmiCategory = "Thể trạng chuẩn cân đối (Duy trì thể lực tốt)";
            } else if (bmi < 25.0) {
                bmiCategory = "Thừa cân nhẹ (Cần kiểm soát calo)";
            } else {
                bmiCategory = "Cần kiểm soát mỡ thừa & tim mạch";
            }

            tvBmiResult.setText(String.format(Locale.getDefault(), "📊 ĐÁNH GIÁ CHỈ SỐ SỨC KHỎE:\n• BMI: %.1f • %s\n• Nhu cầu Calo TDEE: %d kcal/ngày", bmi, bmiCategory, (int)tdee));

            tvAiPlanResult.setText("⏳ Dược sĩ đang phân tích chỉ số thể trạng và thiết lập lộ trình dinh dưỡng & rèn luyện cá nhân hóa cho bạn...");
            cardResult.setVisibility(View.VISIBLE);
            btnGenerate.setEnabled(false);

            com.example.appnhathuoc.api.GeminiApiClient.generateHealthPlanAdvice(
                    age, genderStr, bmi, bmiCategory, (int) tdee, weightKg, goalStr,
                    new com.example.appnhathuoc.api.GeminiApiClient.GeminiCallback() {
                @Override
                public void onSuccess(String aiResponse) {
                    tvAiPlanResult.setText(aiResponse);
                    btnGenerate.setEnabled(true);
                    Toast.makeText(HealthPlanActivity.this, "Đã hoàn thành lộ trình tư vấn sức khỏe dành riêng cho bạn!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    tvAiPlanResult.setText("Không thể tạo lộ trình bằng AI lúc này.\n\n" + error +
                            "\n\nVui lòng kiểm tra kết nối/cấu hình Gemini rồi thử lại.");
                    btnGenerate.setEnabled(true);
                    Toast.makeText(HealthPlanActivity.this, "AI chưa thể tạo lộ trình. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Tuổi, chiều cao và cân nặng phải là số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}