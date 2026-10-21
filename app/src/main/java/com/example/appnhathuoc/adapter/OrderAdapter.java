package com.example.appnhathuoc.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.model.CartItem;
import com.example.appnhathuoc.model.Order;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private final OnOrderClickListener listener;
    private static final DecimalFormat priceFormatter = new DecimalFormat("#,###đ");

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    public void updateList(List<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("Mã đơn: " + order.getOrderId());
        holder.tvDate.setText("Ngày: " + order.getDate());
        holder.tvStatus.setText(order.getStatus());

        String st = order.getStatus();
        if ("Đã giao".equalsIgnoreCase(st)) {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.success_green));
        } else if ("Đã hủy".equalsIgnoreCase(st)) {
            holder.tvStatus.setTextColor(Color.parseColor("#D94B4B"));
        } else if ("Đang giao".equalsIgnoreCase(st)) {
            holder.tvStatus.setTextColor(Color.parseColor("#E97852"));
        } else {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.primary));
        }

        // Progress Tracking Steps Highlight
        if ("Đã hủy".equalsIgnoreCase(st)) {
            if (holder.layoutSteps != null) holder.layoutSteps.setVisibility(View.GONE);
        } else {
            if (holder.layoutSteps != null) holder.layoutSteps.setVisibility(View.VISIBLE);

            int activeLevel = 1;
            if ("Đang chuẩn bị".equalsIgnoreCase(st)) activeLevel = 2;
            else if ("Đang giao".equalsIgnoreCase(st)) activeLevel = 3;
            else if ("Đã giao".equalsIgnoreCase(st)) activeLevel = 4;

            int activeColor = holder.itemView.getContext().getColor(
                    activeLevel == 4 ? R.color.success_green : R.color.primary);
            int inactiveColor = Color.parseColor("#9E9E9E");
            int inactiveBg = Color.parseColor("#E0E0E0");

            // Step 1
            if (holder.step1Dot != null) {
                holder.step1Dot.setText("✓");
                holder.step1Dot.setTextColor(Color.WHITE);
                holder.step1Dot.setBackgroundResource(R.drawable.bg_cart_badge);
            }

            // Line 1 & Step 2
            if (holder.stepLine1 != null) holder.stepLine1.setBackgroundColor(activeLevel >= 2 ? activeColor : inactiveBg);
            if (holder.step2Dot != null) {
                holder.step2Dot.setText(activeLevel >= 2 ? "✓" : "2");
                holder.step2Dot.setTextColor(activeLevel >= 2 ? Color.WHITE : inactiveColor);
                holder.step2Dot.setBackgroundResource(activeLevel >= 2 ? R.drawable.bg_cart_badge : R.drawable.bg_category_icon);
            }

            // Line 2 & Step 3
            if (holder.stepLine2 != null) holder.stepLine2.setBackgroundColor(activeLevel >= 3 ? activeColor : inactiveBg);
            if (holder.step3Dot != null) {
                holder.step3Dot.setText(activeLevel >= 3 ? "✓" : "3");
                holder.step3Dot.setTextColor(activeLevel >= 3 ? Color.WHITE : inactiveColor);
                holder.step3Dot.setBackgroundResource(activeLevel >= 3 ? R.drawable.bg_cart_badge : R.drawable.bg_category_icon);
            }

            // Line 3 & Step 4
            if (holder.stepLine3 != null) holder.stepLine3.setBackgroundColor(activeLevel >= 4 ? activeColor : inactiveBg);
            if (holder.step4Dot != null) {
                holder.step4Dot.setText(activeLevel >= 4 ? "✓" : "4");
                holder.step4Dot.setTextColor(activeLevel >= 4 ? Color.WHITE : inactiveColor);
                holder.step4Dot.setBackgroundResource(activeLevel >= 4 ? R.drawable.bg_cart_badge : R.drawable.bg_category_icon);
            }
        }

        List<CartItem> items = order.getItems();
        if (items != null && !items.isEmpty()) {
            CartItem first = items.get(0);
            if (first.getProduct() != null) {
                holder.tvFirstName.setText(first.getProduct().getName());
                if (first.getProduct().getImageUrl() != null && !first.getProduct().getImageUrl().trim().isEmpty()) {
                    com.example.appnhathuoc.utils.ImageUtils.loadImage(holder.imgPreview, first.getProduct().getImageUrl(), first.getProduct().getImageResId());
                } else {
                    holder.imgPreview.setImageResource(first.getProduct().getImageResId());
                }
                holder.tvFirstQty.setText("Số lượng: x" + first.getQuantity());
            }
        }

        holder.tvSummary.setText(order.getItemCount() + " sản phẩm • " + priceFormatter.format(order.getTotalAmount()));

        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDate, tvStatus, tvFirstName, tvFirstQty, tvSummary, btnDetail;
        TextView step1Dot, step2Dot, step3Dot, step4Dot;
        View stepLine1, stepLine2, stepLine3, layoutSteps;
        ImageView imgPreview;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvDate = itemView.findViewById(R.id.tv_order_date);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvFirstName = itemView.findViewById(R.id.tv_order_first_product_name);
            tvFirstQty = itemView.findViewById(R.id.tv_order_first_product_qty);
            tvSummary = itemView.findViewById(R.id.tv_order_summary);
            btnDetail = itemView.findViewById(R.id.btn_order_detail_view);
            imgPreview = itemView.findViewById(R.id.img_order_preview);

            layoutSteps = itemView.findViewById(R.id.layout_order_tracking_steps);
            step1Dot = itemView.findViewById(R.id.step1_dot);
            step2Dot = itemView.findViewById(R.id.step2_dot);
            step3Dot = itemView.findViewById(R.id.step3_dot);
            step4Dot = itemView.findViewById(R.id.step4_dot);
            stepLine1 = itemView.findViewById(R.id.step_line_1);
            stepLine2 = itemView.findViewById(R.id.step_line_2);
            stepLine3 = itemView.findViewById(R.id.step_line_3);
        }
    }
}