package com.example.appnhathuoc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.model.BannerItem;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<BannerItem> bannerList;
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(BannerItem banner);
    }

    public BannerAdapter(List<BannerItem> bannerList, OnBannerClickListener listener) {
        this.bannerList = bannerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerItem banner = bannerList.get(position);
        holder.tvTitle.setText(banner.getTitle());
        holder.tvSubtitle.setText(banner.getSubtitle());
        holder.tvAction.setText(banner.getActionText());

        if (banner.getImageUrl() != null && !banner.getImageUrl().trim().isEmpty()) {
            if (holder.imgPhoto != null) {
                holder.imgPhoto.setVisibility(View.VISIBLE);
                com.example.appnhathuoc.utils.ImageUtils.loadImage(holder.imgPhoto, banner.getImageUrl(), banner.getBackgroundResId());
            }
        } else {
            if (holder.imgPhoto != null) {
                holder.imgPhoto.setVisibility(View.GONE);
            }
            holder.layoutBg.setBackgroundResource(banner.getBackgroundResId());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(banner);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList != null ? bannerList.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvAction;
        RelativeLayout layoutBg;
        ImageView imgPhoto;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_banner_title);
            tvSubtitle = itemView.findViewById(R.id.tv_banner_subtitle);
            tvAction = itemView.findViewById(R.id.tv_banner_action);
            layoutBg = itemView.findViewById(R.id.layout_banner_bg);
            imgPhoto = itemView.findViewById(R.id.img_banner_photo);
        }
    }
}