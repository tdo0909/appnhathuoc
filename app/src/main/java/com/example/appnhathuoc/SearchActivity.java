package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.adapter.ProductAdapter;
import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.Product;
import com.example.appnhathuoc.utils.CartManager;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etSearchQuery;
    private TextView btnClear, tvResultsCount;
    private LinearLayout layoutSuggestions, layoutEmpty;
    private RecyclerView rvResults;
    private ProductAdapter adapter;
    private List<Product> resultList = new ArrayList<>();
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        BottomNavigationHelper.attach(this, R.id.navigation_category);

        cartManager = CartManager.getInstance(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        setupChips();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_search);
        etSearchQuery = findViewById(R.id.et_search_query);
        btnClear = findViewById(R.id.btn_clear_search);
        tvResultsCount = findViewById(R.id.tv_search_results_count);
        layoutSuggestions = findViewById(R.id.layout_suggestions);
        layoutEmpty = findViewById(R.id.layout_empty_search);
        rvResults = findViewById(R.id.rv_search_results);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(resultList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(SearchActivity.this, ProductDetailActivity.class);
                intent.putExtra("PRODUCT", product);
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                cartManager.addProduct(product, 1);
                Toast.makeText(SearchActivity.this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        rvResults.setLayoutManager(new GridLayoutManager(this, 2));
        rvResults.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnClear.setOnClickListener(v -> {
            etSearchQuery.setText("");
            performSearch("");
        });

        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupChips() {
        int[] chipIds = {
            R.id.chip_vitamin, R.id.chip_canxi, R.id.chip_omega3,
            R.id.chip_sunscreen, R.id.chip_thermometer, R.id.chip_mask
        };

        for (int id : chipIds) {
            Chip chip = findViewById(id);
            if (chip != null) {
                chip.setOnClickListener(v -> {
                    String text = chip.getText().toString();
                    etSearchQuery.setText(text);
                    etSearchQuery.setSelection(text.length());
                    performSearch(text);
                });
            }
        }
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            layoutSuggestions.setVisibility(View.VISIBLE);
            tvResultsCount.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            rvResults.setVisibility(View.GONE);
            resultList.clear();
            adapter.updateList(resultList);
            return;
        }

        layoutSuggestions.setVisibility(View.GONE);

        DataRepository.loadProductsFromApi(null, false, false, query, null, new com.example.appnhathuoc.api.ApiClient.ApiCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> matches) {
                resultList.clear();
                if (matches != null) {
                    resultList.addAll(matches);
                }
                adapter.updateList(resultList);

                if (resultList.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvResults.setVisibility(View.GONE);
                    tvResultsCount.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvResults.setVisibility(View.VISIBLE);
                    tvResultsCount.setVisibility(View.VISIBLE);
                    tvResultsCount.setText("Tìm thấy " + resultList.size() + " sản phẩm cho \"" + query.trim() + "\"");
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }
}