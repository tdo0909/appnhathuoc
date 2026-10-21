package com.example.appnhathuoc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.adapter.StoreAdapter;
import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.Store;
import com.example.appnhathuoc.utils.BottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etSearch;
    private ChipGroup chipGroupCity;
    private RecyclerView rvStores;
    private BottomNavigationView bottomNavigationView;

    private StoreAdapter adapter;
    private List<Store> allStores;
    private List<Store> displayStores;
    private String selectedCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        initViews();
        loadData();
        setupListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_store);
        etSearch = findViewById(R.id.et_search_store);
        chipGroupCity = findViewById(R.id.chip_group_store_city);
        rvStores = findViewById(R.id.rv_stores);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void loadData() {
        allStores = DataRepository.getStores();
        displayStores = new ArrayList<>(allStores);

        adapter = new StoreAdapter(displayStores, new StoreAdapter.OnStoreActionListener() {
            @Override
            public void onCallClick(Store store) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + store.getPhone()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(StoreActivity.this, "Số điện thoại: " + store.getPhone(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDirectionsClick(Store store) {
                new AlertDialog.Builder(StoreActivity.this)
                        .setTitle("Chỉ đường - " + store.getName())
                        .setMessage("Địa chỉ: " + store.getAddress() + "\nGiờ mở cửa: " + store.getOpeningHours())
                        .setPositiveButton("Mở bản đồ", (dialog, which) -> {
                            try {
                                Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(store.getAddress()));
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                                startActivity(mapIntent);
                            } catch (Exception e) {
                                Toast.makeText(StoreActivity.this, "Không tìm thấy ứng dụng Bản đồ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Đóng", null)
                        .show();
            }
        });

        rvStores.setLayoutManager(new LinearLayoutManager(this));
        rvStores.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStores();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupCity.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedCity = "";
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_city_hcm) {
                    selectedCity = "TP. Hồ Chí Minh";
                } else if (checkedId == R.id.chip_city_hanoi) {
                    selectedCity = "Hà Nội";
                } else if (checkedId == R.id.chip_city_danang) {
                    selectedCity = "Đà Nẵng";
                } else {
                    selectedCity = "";
                }
            }
            filterStores();
        });
    }

    private void filterStores() {
        String query = etSearch.getText().toString().trim().toLowerCase();
        displayStores.clear();

        for (Store store : allStores) {
            boolean matchesCity = selectedCity.isEmpty() || store.getProvince().equalsIgnoreCase(selectedCity);
            boolean matchesQuery = query.isEmpty() ||
                    store.getName().toLowerCase().contains(query) ||
                    store.getAddress().toLowerCase().contains(query) ||
                    store.getProvince().toLowerCase().contains(query);

            if (!query.isEmpty()) {
                if (matchesQuery) {
                    displayStores.add(store);
                }
            } else {
                if (matchesCity) {
                    displayStores.add(store);
                }
            }
        }

        adapter.updateList(displayStores);
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setup(this, bottomNavigationView, R.id.navigation_store);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_store);
    }
}