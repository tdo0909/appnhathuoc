package com.example.appnhathuoc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.CartItem;
import com.example.appnhathuoc.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final String PREF_NAME = "medcare_cart_pref";
    private static final String KEY_CART_DATA = "cart_data";

    private static CartManager instance;
    private final SharedPreferences prefs;

    private CartManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        List<CartItem> list = new ArrayList<>();
        String raw = prefs.getString(KEY_CART_DATA, "");
        if (raw == null || raw.isEmpty()) {
            return list;
        }

        // Format: productId:quantity;productId:quantity
        String[] items = raw.split(";");
        for (String item : items) {
            if (!item.trim().isEmpty()) {
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    String pId = parts[0];
                    int qty = Integer.parseInt(parts[1]);
                    Product p = DataRepository.getProductById(pId);
                    if (p != null) {
                        list.add(new CartItem(p, qty));
                    }
                }
            }
        }
        return list;
    }

    public void saveCartItems(List<CartItem> items) {
        StringBuilder sb = new StringBuilder();
        for (CartItem item : items) {
            if (item.getProduct() != null && item.getQuantity() > 0) {
                sb.append(item.getProduct().getId())
                        .append(":")
                        .append(item.getQuantity())
                        .append(";");
            }
        }
        prefs.edit().putString(KEY_CART_DATA, sb.toString()).apply();
    }

    public void addProduct(Product product, int quantity) {
        List<CartItem> items = getCartItems();
        boolean found = false;
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            items.add(new CartItem(product, quantity));
        }
        saveCartItems(items);
    }

    public void updateQuantity(String productId, int newQuantity) {
        List<CartItem> items = getCartItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getId().equals(productId)) {
                if (newQuantity <= 0) {
                    items.remove(i);
                } else {
                    items.get(i).setQuantity(newQuantity);
                }
                break;
            }
        }
        saveCartItems(items);
    }

    public void removeProduct(String productId) {
        updateQuantity(productId, 0);
    }

    public void clearCart() {
        prefs.edit().remove(KEY_CART_DATA).apply();
    }

    public int getCartCount() {
        int count = 0;
        for (CartItem item : getCartItems()) {
            count += item.getQuantity();
        }
        return count;
    }

    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : getCartItems()) {
            total += item.getTotalPrice();
        }
        return total;
    }
}