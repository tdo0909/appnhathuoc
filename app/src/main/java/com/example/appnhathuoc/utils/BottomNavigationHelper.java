package com.example.appnhathuoc.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.appnhathuoc.CategoryActivity;
import com.example.appnhathuoc.MainActivity;
import com.example.appnhathuoc.OrdersActivity;
import com.example.appnhathuoc.ProfileActivity;
import com.example.appnhathuoc.R;
import com.example.appnhathuoc.StoreActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class BottomNavigationHelper {

    private BottomNavigationHelper() {}

    public static void setup(Activity activity, BottomNavigationView navigationView, int selectedItemId) {
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == selectedItemId) {
                return true;
            }

            Class<?> destination;
            if (itemId == R.id.navigation_home) {
                destination = MainActivity.class;
            } else if (itemId == R.id.navigation_category) {
                destination = CategoryActivity.class;
            } else if (itemId == R.id.navigation_store) {
                destination = StoreActivity.class;
            } else if (itemId == R.id.navigation_orders) {
                destination = OrdersActivity.class;
            } else if (itemId == R.id.navigation_profile) {
                destination = ProfileActivity.class;
            } else {
                return false;
            }

            Intent intent = new Intent(activity, destination);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
            return true;
        });
        navigationView.setSelectedItemId(selectedItemId);
    }

    public static void attach(Activity activity, int selectedItemId) {
        FrameLayout contentFrame = activity.findViewById(android.R.id.content);
        View content = contentFrame.getChildAt(0);
        if (content == null || content.findViewById(R.id.bottom_navigation) != null) {
            return;
        }

        contentFrame.removeView(content);
        LinearLayout screen = new LinearLayout(activity);
        screen.setOrientation(LinearLayout.VERTICAL);
        contentFrame.addView(screen, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        screen.addView(content, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f));
        BottomNavigationView navigationView = (BottomNavigationView) LayoutInflater.from(activity)
                .inflate(R.layout.view_bottom_navigation, screen, false);
        screen.addView(navigationView);
        setup(activity, navigationView, selectedItemId);
    }
}
