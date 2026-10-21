package com.example.appnhathuoc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.api.ApiClient;
import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.Category;
import com.example.appnhathuoc.utils.BottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvCategories;
    private BottomNavigationView bottomNavigationView;
    private CategoryGridAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        btnBack = findViewById(R.id.btn_back_category);
        rvCategories = findViewById(R.id.rv_all_categories);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        btnBack.setOnClickListener(v -> finish());

        adapter = new CategoryGridAdapter(categoryList, category -> {
            Intent intent = new Intent(CategoryActivity.this, ProductListActivity.class);
            intent.putExtra("CATEGORY_ID", category.getId());
            intent.putExtra("CATEGORY_NAME", category.getName());
            startActivity(intent);
        });

        rvCategories.setLayoutManager(new GridLayoutManager(this, 2));
        rvCategories.setAdapter(adapter);

        loadCategories();
        BottomNavigationHelper.setup(this, bottomNavigationView, R.id.navigation_category);
    }

    private void loadCategories() {
        DataRepository.loadCategoriesFromApi(new ApiClient.ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (categories != null) {
                    categoryList = categories;
                    adapter.updateList(categoryList);
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private static class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.ViewHolder> {
        private List<Category> list;
        private final OnCategoryClickListener listener;

        interface OnCategoryClickListener {
            void onClick(Category category);
        }

        CategoryGridAdapter(List<Category> list, OnCategoryClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        public void updateList(List<Category> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_grid, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Category cat = list.get(position);
            holder.tvName.setText(cat.getName());

            if (cat.getIconUrl() != null && !cat.getIconUrl().trim().isEmpty()) {
                com.example.appnhathuoc.utils.ImageUtils.loadImage(holder.imgIcon, cat.getIconUrl(), cat.getIconResId());
            } else {
                holder.imgIcon.setImageResource(cat.getIconResId());
            }

            int count = DataRepository.getProductsByCategory(cat.getId()).size();
            holder.tvCount.setText(count > 0 ? count + " sản phẩm" : "Xem sản phẩm");

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(cat);
            });
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgIcon;
            TextView tvName, tvCount;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgIcon = itemView.findViewById(R.id.img_cat_grid_icon);
                tvName = itemView.findViewById(R.id.tv_cat_grid_name);
                tvCount = itemView.findViewById(R.id.tv_cat_grid_count);
            }
        }
    }
}
