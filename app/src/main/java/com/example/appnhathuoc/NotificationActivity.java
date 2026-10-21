package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.adapter.NotificationAdapter;
import com.example.appnhathuoc.api.ApiClient;
import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.Article;
import com.example.appnhathuoc.model.NotificationItem;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ChipGroup chipGroupNotif;
    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<NotificationItem> allNotifs;
    private List<NotificationItem> displayNotifs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        BottomNavigationHelper.attach(this, R.id.navigation_home);

        btnBack = findViewById(R.id.btn_back_notif);
        chipGroupNotif = findViewById(R.id.chip_group_notif);
        rvNotifications = findViewById(R.id.rv_notifications);

        btnBack.setOnClickListener(v -> finish());

        allNotifs = DataRepository.getNotifications();
        displayNotifs = new ArrayList<>(allNotifs);

        adapter = new NotificationAdapter(displayNotifs, this::openNotificationDetail);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        // Fetch live notifications & promo news from Firebase Cloud
        ApiClient.fetchNotifications(new ApiClient.ApiCallback<List<NotificationItem>>() {
            @Override
            public void onSuccess(List<NotificationItem> result) {
                if (result != null) {
                    allNotifs = result;
                    applySelectedFilter();
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });

        chipGroupNotif.setOnCheckedStateChangeListener((group, checkedIds) -> {
            applySelectedFilter();
        });
    }

    private void applySelectedFilter() {
        int id = chipGroupNotif.getCheckedChipId();
        displayNotifs.clear();
        for (NotificationItem item : allNotifs) {
            if (id == R.id.chip_notif_promo && !"PROMOTION".equalsIgnoreCase(item.getType())) continue;
            if (id == R.id.chip_notif_order && !"ORDER".equalsIgnoreCase(item.getType())) continue;
            if (id == R.id.chip_notif_system && !"SYSTEM".equalsIgnoreCase(item.getType())) continue;
            displayNotifs.add(item);
        }
        adapter.updateList(displayNotifs);
    }

    private void openNotificationDetail(NotificationItem notification) {
        Article article = new Article(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getMessage(),
                "Nhà thuốc",
                notification.getTime(),
                R.drawable.ic_product_placeholder);
        article.setImageUrl(notification.getImageUrl());
        android.content.Intent intent = new android.content.Intent(this, ArticleDetailActivity.class);
        intent.putExtra("ARTICLE", article);
        startActivity(intent);
    }
}