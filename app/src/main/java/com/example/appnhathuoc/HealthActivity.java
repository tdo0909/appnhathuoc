package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.adapter.ArticleAdapter;
import com.example.appnhathuoc.api.ApiClient;
import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.Article;

import java.util.List;

public class HealthActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvArticles;
    private View btnSubmitFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        BottomNavigationHelper.attach(this, R.id.navigation_home);

        btnBack = findViewById(R.id.btn_back_health);
        rvArticles = findViewById(R.id.rv_health_articles);
        btnSubmitFeedback = findViewById(R.id.btn_submit_feedback);

        btnBack.setOnClickListener(v -> finish());

        if (btnSubmitFeedback != null) {
            btnSubmitFeedback.setOnClickListener(v -> showSubmitFeedbackDialog());
        }

        DataRepository.loadArticlesFromApi(new ApiClient.ApiCallback<List<Article>>() {
            @Override
            public void onSuccess(List<Article> articles) {
                if (articles == null) return;
                ArticleAdapter adapter = new ArticleAdapter(articles, article -> {
                    Intent intent = new Intent(HealthActivity.this, ArticleDetailActivity.class);
                    intent.putExtra("ARTICLE", article);
                    startActivity(intent);
                });
                rvArticles.setLayoutManager(new LinearLayoutManager(HealthActivity.this));
                rvArticles.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void showSubmitFeedbackDialog() {
        android.widget.EditText etInput = new android.widget.EditText(this);
        etInput.setHint("Nhập nội dung góp ý hoặc đề xuất mẹo hay y khoa...");
        etInput.setPadding(32, 24, 32, 24);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("💬 Gửi Góp Ý & Đề Xuất Mẹo")
                .setMessage("Nhà Thuốc luôn lắng nghe mọi đóng góp ý kiến từ quý khách:")
                .setView(etInput)
                .setPositiveButton("Gửi Góp Ý", (dialog, which) -> {
                    String text = etInput.getText().toString().trim();
                    if (!text.isEmpty()) {
                        Toast.makeText(HealthActivity.this, "Cảm ơn bạn đã gửi góp ý! Dược sĩ Nhà Thuốc sẽ ghi nhận.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }
}