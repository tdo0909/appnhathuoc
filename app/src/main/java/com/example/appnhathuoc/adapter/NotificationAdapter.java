package com.example.appnhathuoc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private List<NotificationItem> notifList;
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotifClick(NotificationItem notif);
    }

    public NotificationAdapter(List<NotificationItem> notifList, OnNotificationClickListener listener) {
        this.notifList = notifList;
        this.listener = listener;
    }

    public void updateList(List<NotificationItem> newList) {
        this.notifList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        NotificationItem item = notifList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());
        holder.tvTime.setText(item.getTime());

        if (item.isRead()) {
            holder.unreadDot.setVisibility(View.GONE);
        } else {
            holder.unreadDot.setVisibility(View.VISIBLE);
        }

        holder.imgTypeIcon.setTag(null);
        if ("ORDER".equalsIgnoreCase(item.getType())) {
            holder.imgTypeIcon.setImageResource(R.drawable.ic_nav_orders);
            holder.imgTypeIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.imgTypeIcon.setBackgroundResource(R.drawable.bg_category_icon);
        } else if (item.getImageUrl() != null && !item.getImageUrl().trim().isEmpty()) {
            holder.imgTypeIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imgTypeIcon.setBackgroundResource(0);
            com.example.appnhathuoc.utils.ImageUtils.loadImage(holder.imgTypeIcon, item.getImageUrl(), getFallbackImage(item));
        } else if ("PROMOTION".equalsIgnoreCase(item.getType())) {
            holder.imgTypeIcon.setImageResource(getFallbackImage(item));
            holder.imgTypeIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.imgTypeIcon.setImageResource(getFallbackImage(item));
            holder.imgTypeIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        holder.itemView.setOnClickListener(v -> {
            item.setRead(true);
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(currentPosition);
            }
            if (listener != null) listener.onNotifClick(item);
        });
    }

    private int getFallbackImage(NotificationItem item) {
        return "PROMOTION".equalsIgnoreCase(item.getType())
                ? R.drawable.bg_banner_3 : R.drawable.bg_banner_4;
    }

    @Override
    public int getItemCount() {
        return notifList != null ? notifList.size() : 0;
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        ImageView imgTypeIcon;
        TextView tvTitle, tvMessage, tvTime;
        View unreadDot;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTypeIcon = itemView.findViewById(R.id.img_notif_type_icon);
            tvTitle = itemView.findViewById(R.id.tv_notif_title);
            tvMessage = itemView.findViewById(R.id.tv_notif_message);
            tvTime = itemView.findViewById(R.id.tv_notif_time);
            unreadDot = itemView.findViewById(R.id.view_notif_unread_dot);
        }
    }
}