package com.example.appnhathuoc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.model.Article;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private final List<Article> articleList;
    private final OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    public ArticleAdapter(List<Article> articleList, OnArticleClickListener listener) {
        this.articleList = articleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvSummary.setText(article.getSummary());
        holder.tvAuthor.setText(article.getAuthor());
        holder.tvDate.setText(article.getDate());

        if (article.getImageUrl() != null && !article.getImageUrl().trim().isEmpty()) {
            com.example.appnhathuoc.utils.ImageUtils.loadImage(holder.imgArticle, article.getImageUrl(), article.getImageResId());
        } else {
            holder.imgArticle.setImageResource(article.getImageResId());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onArticleClick(article);
        });
    }

    @Override
    public int getItemCount() {
        return articleList != null ? articleList.size() : 0;
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView imgArticle;
        TextView tvTitle, tvSummary, tvAuthor, tvDate;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArticle = itemView.findViewById(R.id.img_article);
            tvTitle = itemView.findViewById(R.id.tv_article_title);
            tvSummary = itemView.findViewById(R.id.tv_article_summary);
            tvAuthor = itemView.findViewById(R.id.tv_article_author);
            tvDate = itemView.findViewById(R.id.tv_article_date);
        }
    }
}