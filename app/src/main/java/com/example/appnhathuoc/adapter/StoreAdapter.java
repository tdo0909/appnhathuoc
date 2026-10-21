package com.example.appnhathuoc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.model.Store;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private List<Store> storeList;
    private final OnStoreActionListener listener;

    public interface OnStoreActionListener {
        void onCallClick(Store store);
        void onDirectionsClick(Store store);
    }

    public StoreAdapter(List<Store> storeList, OnStoreActionListener listener) {
        this.storeList = storeList;
        this.listener = listener;
    }

    public void updateList(List<Store> newList) {
        this.storeList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = storeList.get(position);
        holder.tvName.setText(store.getName());
        holder.tvAddress.setText(store.getAddress());
        holder.tvPhone.setText(store.getPhone());
        holder.tvHours.setText(store.getOpeningHours());

        holder.btnCall.setOnClickListener(v -> {
            if (listener != null) listener.onCallClick(store);
        });

        holder.btnDirections.setOnClickListener(v -> {
            if (listener != null) listener.onDirectionsClick(store);
        });
    }

    @Override
    public int getItemCount() {
        return storeList != null ? storeList.size() : 0;
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvPhone, tvHours;
        Button btnCall, btnDirections;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_store_name);
            tvAddress = itemView.findViewById(R.id.tv_store_address);
            tvPhone = itemView.findViewById(R.id.tv_store_phone);
            tvHours = itemView.findViewById(R.id.tv_store_hours);
            btnCall = itemView.findViewById(R.id.btn_call_store);
            btnDirections = itemView.findViewById(R.id.btn_directions_store);
        }
    }
}