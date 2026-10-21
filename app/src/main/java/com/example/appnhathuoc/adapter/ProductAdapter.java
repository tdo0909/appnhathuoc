package com.example.appnhathuoc.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private final OnProductClickListener listener;
    private static final DecimalFormat priceFormatter = new DecimalFormat("#,###đ");

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(priceFormatter.format(product.getPrice()));

        if (product.getOldPrice() > product.getPrice()) {
            holder.tvOldPrice.setVisibility(View.VISIBLE);
            holder.tvOldPrice.setText(priceFormatter.format(product.getOldPrice()));
            holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvOldPrice.setVisibility(View.GONE);
        }

        if (product.getDiscountPercent() > 0) {
            holder.tvDiscount.setVisibility(View.VISIBLE);
            holder.tvDiscount.setText("-" + product.getDiscountPercent() + "%");
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        if (holder.tvSpec != null) {
            if (product.getSpecification() != null && !product.getSpecification().isEmpty()) {
                holder.tvSpec.setText(product.getSpecification());
                holder.tvSpec.setVisibility(View.VISIBLE);
            } else {
                holder.tvSpec.setVisibility(View.GONE);
            }
        }

        if (holder.tvOrigin != null) {
            if (product.getOrigin() != null && !product.getOrigin().isEmpty()) {
                holder.tvOrigin.setText(product.getOrigin());
                holder.tvOrigin.setVisibility(View.VISIBLE);
            } else {
                holder.tvOrigin.setVisibility(View.GONE);
            }
        }

        holder.tvRating.setText(String.valueOf(product.getRating()));
        holder.tvReviews.setText("(" + product.getReviewCount() + ")");

        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            com.example.appnhathuoc.utils.ImageUtils.loadImage(holder.imgProduct, product.getImageUrl(), product.getImageResId());
        } else {
            holder.imgProduct.setImageResource(product.getImageResId());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        View btnAddToCart;
        TextView tvName, tvPrice, tvOldPrice, tvDiscount, tvRating, tvReviews, tvSpec, tvOrigin;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvOldPrice = itemView.findViewById(R.id.tv_product_old_price);
            tvDiscount = itemView.findViewById(R.id.tv_discount_badge);
            tvRating = itemView.findViewById(R.id.tv_product_rating);
            tvReviews = itemView.findViewById(R.id.tv_product_reviews);
            tvSpec = itemView.findViewById(R.id.tv_product_spec);
            tvOrigin = itemView.findViewById(R.id.tv_origin_badge);
        }
    }
}