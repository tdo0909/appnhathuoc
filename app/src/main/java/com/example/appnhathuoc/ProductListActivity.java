package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.adapter.ProductAdapter;
import com.example.appnhathuoc.api.ApiClient;
import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.Product;
import com.example.appnhathuoc.utils.CartManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle, tvProductCount;
    private ChipGroup chipGroupSort;
    private RecyclerView rvProductList;

    private ProductAdapter adapter;
    private List<Product> baseProducts = new ArrayList<>();
    private List<Product> displayProducts = new ArrayList<>();

    private CartManager cartManager;
    private String categoryId, categoryName, titleExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        BottomNavigationHelper.attach(this, R.id.navigation_category);

        cartManager = CartManager.getInstance(this);

        categoryId = getIntent().getStringExtra("CATEGORY_ID");
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        titleExtra = getIntent().getStringExtra("TITLE");

        initViews();
        loadData();
        setupRecyclerView();
        setupListeners();
        setupSortListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_product_list);
        tvTitle = findViewById(R.id.tv_product_list_title);
        tvProductCount = findViewById(R.id.tv_product_count);
        chipGroupSort = findViewById(R.id.chip_group_sort);
        rvProductList = findViewById(R.id.rv_product_list);
    }

    private void loadData() {
        if (categoryId != null && !categoryId.isEmpty()) {
            tvTitle.setText(categoryName != null ? categoryName : "Danh mục");
            DataRepository.loadProductsFromApi(categoryId, false, false, null, null, new ApiClient.ApiCallback<List<Product>>() {
                @Override
                public void onSuccess(List<Product> products) {
                    baseProducts = products;
                    displayProducts = new ArrayList<>(baseProducts);
                    if (adapter != null) adapter.updateList(displayProducts);
                    updateCountText();
                }

                @Override
                public void onError(String errorMessage) {}
            });
        } else if (titleExtra != null && !titleExtra.isEmpty()) {
            tvTitle.setText(titleExtra);
            boolean isBest = titleExtra.contains("bán chạy");
            boolean isPromo = titleExtra.contains("Ưu đãi") || titleExtra.contains("Khuyến mãi");

            DataRepository.loadProductsFromApi(null, isBest, isPromo, null, null, new ApiClient.ApiCallback<List<Product>>() {
                @Override
                public void onSuccess(List<Product> products) {
                    baseProducts = products;
                    displayProducts = new ArrayList<>(baseProducts);
                    if (adapter != null) adapter.updateList(displayProducts);
                    updateCountText();
                }

                @Override
                public void onError(String errorMessage) {}
            });
        } else {
            tvTitle.setText("Tất cả sản phẩm");
            DataRepository.loadProductsFromApi(null, false, false, null, null, new ApiClient.ApiCallback<List<Product>>() {
                @Override
                public void onSuccess(List<Product> products) {
                    baseProducts = products;
                    displayProducts = new ArrayList<>(baseProducts);
                    if (adapter != null) adapter.updateList(displayProducts);
                    updateCountText();
                }

                @Override
                public void onError(String errorMessage) {}
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(displayProducts, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("PRODUCT", product);
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                cartManager.addProduct(product, 1);
                Toast.makeText(ProductListActivity.this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        rvProductList.setLayoutManager(new GridLayoutManager(this, 2));
        rvProductList.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSortListeners() {
        chipGroupSort.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            displayProducts = new ArrayList<>(baseProducts);

            if (checkedId == R.id.chip_sort_price_low_high) {
                Collections.sort(displayProducts, Comparator.comparingDouble(Product::getPrice));
            } else if (checkedId == R.id.chip_sort_price_high_low) {
                Collections.sort(displayProducts, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
            } else if (checkedId == R.id.chip_sort_bestseller) {
                List<Product> filtered = new ArrayList<>();
                for (Product p : displayProducts) {
                    if (p.isBestSeller()) filtered.add(p);
                }
                displayProducts = filtered;
            } else if (checkedId == R.id.chip_sort_rating) {
                Collections.sort(displayProducts, (p1, p2) -> Float.compare(p2.getRating(), p1.getRating()));
            }

            adapter.updateList(displayProducts);
            updateCountText();
        });
    }

    private void updateCountText() {
        tvProductCount.setText("Hiển thị " + displayProducts.size() + " sản phẩm");
    }
}