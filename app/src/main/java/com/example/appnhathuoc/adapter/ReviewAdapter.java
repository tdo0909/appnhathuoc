package com.example.appnhathuoc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.model.ReviewItem;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewItem> reviewList;

    public ReviewAdapter(List<ReviewItem> reviewList) {
        this.reviewList = reviewList;
    }

    public void updateList(List<ReviewItem> newList) {
        this.reviewList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewItem item = reviewList.get(position);
        holder.tvUserName.setText(item.getUserName());
        holder.tvDate.setText(item.getDate());
        holder.tvComment.setText(item.getComment());

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < item.getRating(); i++) {
            stars.append("★");
        }
        holder.tvRatingStars.setText(item.getRating() + ".0 " + stars.toString());
    }

    @Override
    public int getItemCount() {
        return reviewList != null ? reviewList.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDate, tvRatingStars, tvComment;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_review_user_name);
            tvDate = itemView.findViewById(R.id.tv_review_date);
            tvRatingStars = itemView.findViewById(R.id.tv_review_rating_stars);
            tvComment = itemView.findViewById(R.id.tv_review_comment);
        }
    }
}