package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appnhathuoc.model.Article;

public class ArticleDetailActivity extends AppCompatActivity {

    private ImageView btnBack, imgArticle;
    private TextView tvTitle, tvAuthor, tvDate, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        BottomNavigationHelper.attach(this, R.id.navigation_home);

        Article article = (Article) getIntent().getSerializableExtra("ARTICLE");
        if (article == null) {
            finish();
            return;
        }

        btnBack = findViewById(R.id.btn_back_article_detail);
        imgArticle = findViewById(R.id.img_article_detail);
        tvTitle = findViewById(R.id.tv_article_detail_title);
        tvAuthor = findViewById(R.id.tv_article_detail_author);
        tvDate = findViewById(R.id.tv_article_detail_date);
        tvContent = findViewById(R.id.tv_article_detail_content);

        btnBack.setOnClickListener(v -> finish());

        if (article.getImageUrl() != null && !article.getImageUrl().trim().isEmpty()) {
            com.example.appnhathuoc.utils.ImageUtils.loadImage(imgArticle, article.getImageUrl(), article.getImageResId());
        } else {
            imgArticle.setImageResource(article.getImageResId());
        }

        tvTitle.setText(article.getTitle());
        tvAuthor.setText("Tác giả: " + article.getAuthor());
        tvDate.setText(article.getDate());
        tvContent.setText(article.getContent());
    }
}